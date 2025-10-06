package padroes.projeto.patoli.controller;

import padroes.projeto.patoli.controller.viewmodel.enums.CellTypeVMEnum;
import padroes.projeto.patoli.controller.viewmodel.enums.PlayerColorVMEnum;
import padroes.projeto.patoli.model.board.Board;
import padroes.projeto.patoli.model.board.Cell;
import padroes.projeto.patoli.model.board.enums.CellTypeEnum;
import padroes.projeto.patoli.model.game.Game;
import padroes.projeto.patoli.model.game.GameObserver;
import padroes.projeto.patoli.model.board.Piece;
import padroes.projeto.patoli.model.board.Player;
import padroes.projeto.patoli.model.board.enums.PlayerColorEnum;
import padroes.projeto.patoli.view.frame.GameView;
import padroes.projeto.patoli.controller.viewmodel.CellVM;
import padroes.projeto.patoli.controller.viewmodel.PieceVM;

import java.util.ArrayList;
import java.util.List;


public class GameController implements GameObserver {
    private final Game game;
    private GameView view;

    public GameController(Game game) {
        this.game = game;
    }

    public void setView(GameView view) {
        this.view = view;
        game.addObserver(this);
        if (this.view != null) {
            this.view.onEvent("INIT");
            this.view.onEvent("TURN");
            this.view.refresh();
        }
    }

    @Override
    public void onGameChanged(Game game, String reason) {
        if (view != null) {
            view.onEvent(reason);
            view.refresh();
        }
    }

    public void onRoll() {
        if (game.getLastRoll() != -1) {
            if (view != null) view.showMessage("Você já rolou neste turno. Mova uma peça ou passe se não houver jogada.");
            return;
        }
        int val = game.roll();
        if (val == 0) {
            if (view != null) view.showMessage("Você tirou 0. Sem movimento.");
            onPassIfRequired();
        }
    }

    public void onEnterNewPiece() {
        if (game.enterNewPiece()) {
            endMovePhase();
        } else {
            if (view != null) view.showMessage("Não é possível inserir nova peça agora.");
        }
    }

    public void onCellClicked(int row, int col) {
        if (game.isGameOver()) return;
        Board b = game.getBoard();
        for (Cell cell : b.getTrack()) {
            if (cell.getRow() == row && cell.getCol() == col) {
                Piece occ = cell.getOccupant();
                if (occ != null && occ.getOwner() == game.getCurrent()) {
                    onPieceClicked(occ);
                }
                return;
            }
        }
    }

    private void onPieceClicked(Piece piece) {
        if (game.getLastRoll() <= 0) {
            if (view != null) view.showMessage("Role as moedas antes de mover.");
            return;
        }
        if (game.movePiece(piece)) {
            endMovePhase();
        } else {
            if (view != null) view.showMessage("Movimento inválido para esta peça.");
        }
    }

    public void onPassIfRequired() {
        if (game.mustPass()) {
            game.nextTurnIfNeeded();
            checkGameOverOrContinue();
        } else {
            if (view != null) view.showMessage("Você ainda tem jogadas válidas. Não pode passar.");
        }
    }

    private void endMovePhase() {
        game.nextTurnIfNeeded();
        checkGameOverOrContinue();
    }

    private void checkGameOverOrContinue() {
        if (game.isGameOver()) {
            if (view != null) view.showMessage(game.gameOverMessage());
        }
    }

    // Provedores de estado para a View
    public int getRows() { return game.getBoard().getRows(); }
    public int getCols() { return game.getBoard().getCols(); }
    public int getPot() { return game.getPot(); }

    public int getLastRoll() { return game.getLastRoll(); }
    public boolean canEnterNewPiece() { return game.canEnterNewPiece(); }
    public boolean isGameOver() { return game.isGameOver(); }

    public String getPlayerName(PlayerColorVMEnum color) {
        return (color == PlayerColorVMEnum.BLACK ? game.getBlack() : game.getWhite()).getName();
    }

    public int getPlayerCoins(PlayerColorVMEnum color) {
        return (color == PlayerColorVMEnum.BLACK ? game.getBlack() : game.getWhite()).getCoins();
    }

    public PlayerColorVMEnum getCurrentPlayerColor() {
        return map(game.getCurrent().getColor());
    }

    public PlayerColorVMEnum getOpponentPlayerColor() {
        return map(game.getOpponent().getColor());
    }

    public long getFinishedCount(PlayerColorVMEnum color) {
        return (color == PlayerColorVMEnum.BLACK ? game.getBlack() : game.getWhite()).countFinished();
    }

    public List<CellVM> getCells() {
        Board b = game.getBoard();
        List<CellVM> out = new ArrayList<>(b.getTrack().size());
        for (Cell c : b.getTrack()) {
            Piece occ = c.getOccupant();
            boolean occupied = (occ != null);
            PlayerColorVMEnum occColor = occupied ? map(occ.getOwner().getColor()) : null;
            Integer occId = occupied ? occ.getId() : null;
            out.add(new CellVM(c.getRow(), c.getCol(), map(c.getType()), occupied, occColor, occId));
        }
        return out;
    }

    public List<PieceVM> getPieces(PlayerColorVMEnum color) {
        Player p = (color == PlayerColorVMEnum.BLACK) ? game.getBlack() : game.getWhite();
        Board b = game.getBoard();
        List<PieceVM> list = new ArrayList<>();
        for (Piece pc : p.getPieces()) {
            boolean onBoard = pc.isOnBoard();
            boolean finished = pc.isFinished();
            Integer row = null, col = null;
            if (onBoard) {
                Cell cell = b.getTrack().get(pc.getPosition());
                row = cell.getRow(); col = cell.getCol();
            }
            list.add(new PieceVM(pc.getId(), color, onBoard, finished, row, col));
        }
        return list;
    }

    public List<PieceVM> getReservedPieces(PlayerColorVMEnum color) {
        List<PieceVM> all = getPieces(color);
        List<PieceVM> out = new ArrayList<>();
        for (PieceVM vm : all) if (!vm.onBoard && !vm.finished) out.add(vm);
        return out;
    }

    public List<PieceVM> getFinishedPieces(PlayerColorVMEnum color) {
        List<PieceVM> all = getPieces(color);
        List<PieceVM> out = new ArrayList<>();
        for (PieceVM vm : all) if (vm.finished) out.add(vm);
        return out;
    }

    private PlayerColorVMEnum map(PlayerColorEnum c) {
        return c == PlayerColorEnum.BLACK ? PlayerColorVMEnum.BLACK : PlayerColorVMEnum.WHITE;
    }

    private CellTypeVMEnum map(CellTypeEnum t) {
        return switch (t) {
            case NORMAL -> CellTypeVMEnum.NORMAL;
            case TRIANGLE_PENALTY -> CellTypeVMEnum.TRIANGLE_PENALTY;
            case ENDPOINT -> CellTypeVMEnum.ENDPOINT;
            case START -> CellTypeVMEnum.START;
        };
    }

}
