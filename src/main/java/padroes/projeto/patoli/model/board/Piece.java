package padroes.projeto.patoli.model.board;

public class Piece {
    public static final int OFF_BOARD = -1;
    public static final int FINISHED = -2;

    private final int id;
    private final Player owner;
    private int position = OFF_BOARD;

    // Direção de movimento para o modo "ida e volta": +1 (indo para baixo) ou -1 (voltando para cima)
    // Por padrão, começa indo "para baixo" (+1). Ajustado conforme primeira jogada.
    private int direction = +1;

    public Piece(int id, Player owner) {
        this.id = id;
        this.owner = owner;
    }

    public int getId() { return id; }
    public Player getOwner() { return owner; }
    public int getPosition() { return position; }

    public boolean isOnBoard() { return position >= 0; }
    public boolean isFinished() { return position == FINISHED; }

    public void placeAtStart(int startIndex) {
        this.position = startIndex;
        this.direction = +1; // começa seguindo a ordem crescente dos índices (ida)
    }

    public void moveTo(int index) {
        this.position = index;
    }

    public void finish() {
        this.position = FINISHED;
    }

    public void resetOffBoard() {
        this.position = OFF_BOARD;
        this.direction = +1;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction >= 0 ? +1 : -1;
    }
}