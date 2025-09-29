package padroes.projeto.patoli.model.board;

import padroes.projeto.patoli.model.board.enums.CellTypeEnum;

public class Cell {
    private final int index;
    private final int row;
    private final int col;
    private final CellTypeEnum type;
    private Piece occupant;

    public Cell(int index, int row, int col, CellTypeEnum type) {
        this.index = index;
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public int getIndex() { return index; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public CellTypeEnum getType() { return type; }

    public Piece getOccupant() { return occupant; }
    public void setOccupant(Piece occupant) { this.occupant = occupant; }

    public boolean isFree() { return occupant == null; }
}
