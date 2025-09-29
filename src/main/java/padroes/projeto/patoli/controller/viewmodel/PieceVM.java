package padroes.projeto.patoli.controller.viewmodel;

public class PieceVM {
    public final int id;
    public final PlayerColorVM owner;
    public final boolean onBoard;
    public final boolean finished;
    public final Integer row; // null se off-board/finished
    public final Integer col; // null se off-board/finished

    public PieceVM(int id, PlayerColorVM owner, boolean onBoard, boolean finished, Integer row, Integer col) {
        this.id = id;
        this.owner = owner;
        this.onBoard = onBoard;
        this.finished = finished;
        this.row = row;
        this.col = col;
    }
}