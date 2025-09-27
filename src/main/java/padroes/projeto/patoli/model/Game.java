package padroes.projeto.patoli.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game {
    private final Board board = new Board();
    private final CoinDice dice = new CoinDice();

    private final Player black;
    private final Player white;

    private Player current;
    private int lastRoll = -1;
    private boolean extraTurn = false;

    public Game(String blackName, String whiteName) {
        this.black = new Player(blackName, PlayerColor.BLACK);
        this.white = new Player(whiteName, PlayerColor.WHITE);
        // Decisão inicial simples: rolagem para decidir
        int r1 = dice.roll();
        int r2 = dice.roll();
        current = r1 >= r2 ? black : white;
        lastRoll = -1;
        // Colocar 1 peça inicial de cada jogador nas casas START
        initializeStartingPieces();
    }

    public Board getBoard() { return board; }
    public Player getBlack() { return black; }
    public Player getWhite() { return white; }
    public Player getCurrent() { return current; }
    public Player getOpponent() { return current == black ? white : black; }
    public int getLastRoll() { return lastRoll; }

    public int roll() {
        int val = dice.roll();
        lastRoll = val;
        extraTurn = false;
        return val;
    }

    private void initializeStartingPieces() {
        placeInitialPiece(black);
        placeInitialPiece(white);
    }

    private void placeInitialPiece(Player player) {
        int start = board.getStartIndex(player.getColor());
        if (!board.isFree(start)) return;
        Optional<Piece> opt = player.getPieces().stream()
                .filter(pc -> !pc.isFinished() && !pc.isOnBoard())
                .findFirst();
        if (opt.isEmpty()) return;
        Piece p = opt.get();
        p.placeAtStart(start);
        board.occupy(start, p);
    }

    public boolean canEnterNewPiece() {
        return lastRoll == 1 && board.canPlaceAtStart(current) && hasOffBoardPiece(current);
    }

    public boolean enterNewPiece() {
        if (!canEnterNewPiece()) return false;
        int start = board.getStartIndex(current.getColor());
        Piece p = getFirstOffBoardPiece(current).orElse(null);
        if (p == null) return false;
        p.placeAtStart(start);
        board.occupy(start, p);
        applyLandingRules(p, start);
        return true;
    }

    public List<Piece> movablePieces() {
        List<Piece> list = new ArrayList<>();
        if (lastRoll <= 0) return list;

        for (Piece p : current.getPieces()) {
            if (p.isFinished()) continue;
            if (!p.isOnBoard()) continue;

            int dest = computeDestination(p.getPosition(), lastRoll);
            if (dest == Piece.FINISHED) {
                list.add(p);
                continue;
            }
            if (board.isFree(dest)) {
                list.add(p);
            }
        }
        return list;
    }

    // Movimento circular normal ao longo da trilha (permite cruzar o START, mas só finaliza se START estiver livre)
    private int computeDestination(int from, int steps) {
        int n = board.size();
        int startIdx = board.getStartIndex(current.getColor());

        int dest = (from + steps) % n;

        // Finaliza apenas se cair exatamente na START e ela estiver desocupada
        if (dest == startIdx && steps > 0) {
            // Se a casa START está ocupada (por qualquer peça), não pode finalizar (regra "sem captura")
            if (board.isFree(startIdx)) {
                return Piece.FINISHED;
            } else {
                return Integer.MIN_VALUE; // movimento inválido (não pode finalizar por START ocupada)
            }
        }

        // Permitir cruzar o START sem bloquear o movimento
        return board.advanceIndex(from, steps);
    }


    public boolean movePiece(Piece piece) {
        if (lastRoll < 0) return false;
        if (piece.isFinished()) return false;
        if (!piece.isOnBoard()) return false;

        int from = piece.getPosition();
        int dest = computeDestination(from, lastRoll);
        if (dest == Integer.MIN_VALUE) return false;

        if (dest == Piece.FINISHED) {
            board.free(from);
            piece.finish();
            getOpponent().addCoins(-1);
            current.addCoins(1);
            return true;
        }

        if (!board.isFree(dest)) return false;

        board.free(from);
        piece.moveTo(dest);
        board.occupy(dest, piece);

        applyLandingRules(piece, dest);
        return true;
    }

    private void applyLandingRules(Piece piece, int index) {
        // Triângulo: paga 1 moeda ao adversário
        if (board.isTriangle(index)) {
            current.addCoins(-1);
            getOpponent().addCoins(1);
        }
        // Ponta: ganha turno extra
        if (board.isEndpoint(index)) {
            extraTurn = true;
        }
    }

    public boolean mustPass() {
        if (lastRoll <= 0) return true; // 0: não move
        return movablePieces().isEmpty() && !canEnterNewPiece();
    }

    public void nextTurnIfNeeded() {
        if (!current.hasCoins()) {
            return;
        }
        if (!extraTurn) {
            current = getOpponent();
        }
        lastRoll = -1;
        extraTurn = false;
    }

    public boolean isGameOver() {
        if (!black.hasCoins()) return true;
        if (!white.hasCoins()) return true;
        if (black.allFinished() || white.allFinished()) return true;
        return false;
    }

    public String gameOverMessage() {
        if (!black.hasCoins() && !white.hasCoins()) return "Ambos sem moedas. Empate raro!";
        if (!black.hasCoins()) return winnerMsg(white, "oponente ficou sem moedas");
        if (!white.hasCoins()) return winnerMsg(black, "oponente ficou sem moedas");
        if (black.allFinished()) return winnerMsg(black, "moveu todas as peças para fora");
        if (white.allFinished()) return winnerMsg(white, "moveu todas as peças para fora");
        return "";
    }

    private String winnerMsg(Player p, String cause) {
        return "Vitória de " + p.getName() + " (" + p.getColor() + ") - " + cause;
    }

    private boolean hasOffBoardPiece(Player player) {
        return getFirstOffBoardPiece(player).isPresent();
    }

    private Optional<Piece> getFirstOffBoardPiece(Player player) {
        return player.getPieces().stream().filter(pc -> !pc.isOnBoard() && !pc.isFinished()).findFirst();
    }
}