package padroes.projeto.patoli.controller.viewmodel;

public class CellVM {
    public final int row;
    public final int col;
    public final CellTypeVM type;
    public final boolean occupied;
    public final PlayerColorVM occupantColor; // null se vazio
    public final Integer occupantId;          // null se vazio

    public CellVM(int row, int col, CellTypeVM type,
                  boolean occupied, PlayerColorVM occupantColor, Integer occupantId) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.occupied = occupied;
        this.occupantColor = occupantColor;
        this.occupantId = occupantId;
    }
}