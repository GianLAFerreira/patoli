package padroes.projeto.patoli.controller.viewmodel;

import padroes.projeto.patoli.controller.viewmodel.enums.CellTypeEnum;
import padroes.projeto.patoli.controller.viewmodel.enums.PlayerColorEnum;

public class CellVM {
    public final int row;
    public final int col;
    public final CellTypeEnum type;
    public final boolean occupied;
    public final PlayerColorEnum occupantColor; // null se vazio
    public final Integer occupantId;          // null se vazio

    public CellVM(int row, int col, CellTypeEnum type,
                  boolean occupied, PlayerColorEnum occupantColor, Integer occupantId) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.occupied = occupied;
        this.occupantColor = occupantColor;
        this.occupantId = occupantId;
    }
}