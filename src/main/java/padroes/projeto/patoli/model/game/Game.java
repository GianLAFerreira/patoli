package padroes.projeto.patoli.model.game;

import padroes.projeto.patoli.model.board.Board;
import padroes.projeto.patoli.model.board.Piece;
import padroes.projeto.patoli.model.board.Player;
import padroes.projeto.patoli.model.board.enums.PlayerColor;
import padroes.projeto.patoli.model.game.rules.MovementRules;
import padroes.projeto.patoli.model.game.rules.ScoringRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game implements GameObservable {
    private final Board board = new Board();
    private final CoinDice dice = new CoinDice();

    private final Player black;
    private final Player white;

    private Player current;
    private int lastRoll = -1;
    private boolean extraTurn = false;

    // Regras extraídas
    private final MovementRules movementRules = new MovementRules();
    private final ScoringRules scoringRules = new ScoringRules();

    // Observers
    private final List<GameObserver> observers = new ArrayList<>();

    @Override
    public void addObserver(GameObserver observer) {
        if (observer != null) observers.add(observer);
    }

    @Override
    public void removeObserver(GameObserver observer) { observers.remove(observer); }

    private void notifyObservers(String reason) {
        for (GameObserver o : List.copyOf(observers)) {
            o.onGameChanged(this, reason);
        }
    }

    public Game(String blackName, String whiteName) {
        this.black = new Player(blackName, PlayerColor.BLACK);
        this.white = new Player(whiteName, PlayerColor.WHITE);
        int r1 = dice.roll();
        int r2 = dice.roll();
        current = r1 >= r2 ? black : white;
        lastRoll = -1;
        initializeStartingPieces();
        notifyObservers("INIT");
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
        notifyObservers("ROLL");
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
        notifyObservers("ENTER_INIT");
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
        notifyObservers("ENTER");
        return true;
    }

    public List<Piece> movablePieces() {
        List<Piece> list = new ArrayList<>();
        if (lastRoll <= 0) return list;
        for (Piece p : current.getPieces()) {
            if (p.isFinished()) continue;
            if (!p.isOnBoard()) continue;
            int dest = computeDestination(p.getPosition(), lastRoll);
            if (dest == Piece.FINISHED || (dest >= 0 && board.isFree(dest))) list.add(p);
        }
        return list;
    }

    // Delegado para MovementRules
    private int computeDestination(int from, int steps) {
        return movementRules.computeDestination(board, current, from, steps);
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
            notifyObservers("FINISH");
            return true;
        }

        if (!board.isFree(dest)) return false;

        board.free(from);
        piece.moveTo(dest);
        board.occupy(dest, piece);
        applyLandingRules(piece, dest);
        notifyObservers("MOVE");
        return true;
    }

    // Delegado para ScoringRules (aplica resultado no estado do jogo)
    private void applyLandingRules(Piece piece, int index) {
        int penalty = scoringRules.penaltyForLanding(board, index);
        if (penalty != 0) {
            current.addCoins(penalty);
            getOpponent().addCoins(-penalty);
            notifyObservers("PENALTY");
        }
        if (scoringRules.isExtraTurn(board, index)) {
            extraTurn = true;
            notifyObservers("BONUS");
        }
    }

    public boolean mustPass() {
        if (lastRoll <= 0) return true;
        return movablePieces().isEmpty() && !canEnterNewPiece();
    }

    public void nextTurnIfNeeded() {
        if (!current.hasCoins()) {
            notifyObservers("NO_COINS");
            return;
        }
        if (!extraTurn) {
            current = getOpponent();
        }
        lastRoll = -1;
        extraTurn = false;
        notifyObservers("TURN");
    }

    public boolean isGameOver() {
        if (!black.hasCoins() || !white.hasCoins()) return true;
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