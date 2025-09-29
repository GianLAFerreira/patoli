package padroes.projeto.patoli.controller.viewmodel;

import padroes.projeto.patoli.controller.viewmodel.enums.CellTypeVMEnum;
import padroes.projeto.patoli.controller.viewmodel.enums.PlayerColorVMEnum;

public class CellVM {
    public final int row;
    public final int col;
    public final CellTypeVMEnum type;
    public final boolean occupied;
    public final PlayerColorVMEnum occupantColor; // null se vazio
    public final Integer occupantId;          // null se vazio

    public CellVM(int row, int col, CellTypeVMEnum type,
                  boolean occupied, PlayerColorVMEnum occupantColor, Integer occupantId) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.occupied = occupied;
        this.occupantColor = occupantColor;
        this.occupantId = occupantId;
    }
}