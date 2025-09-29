package padroes.projeto.patoli.model.board;

import padroes.projeto.patoli.model.board.enums.PlayerColorEnum;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private final PlayerColorEnum color;
    private int coins = 20;
    private final List<Piece> pieces = new ArrayList<>();

    public Player(String name, PlayerColorEnum color) {
        this.name = name;
        this.color = color;
        for (int i = 0; i < 6; i++) {
            pieces.add(new Piece(i, this));
        }
    }

    public String getName() { return name; }
    public PlayerColorEnum getColor() { return color; }
    public int getCoins() { return coins; }
    public void addCoins(int delta) { coins += delta; }
    public boolean hasCoins() { return coins > 0; }

    public List<Piece> getPieces() { return pieces; }

    public long countFinished() {
        return pieces.stream().filter(Piece::isFinished).count();
    }

    public boolean allFinished() {
        return countFinished() == pieces.size();
    }
}
