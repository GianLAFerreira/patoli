package padroes.projeto.patoli.model.board;

import padroes.projeto.patoli.model.board.enums.CellTypeEnum;
import padroes.projeto.patoli.model.board.enums.PlayerColorEnum;
import padroes.projeto.patoli.model.board.layout.BoardLayout;
import padroes.projeto.patoli.model.board.layout.Cross2x2Layout16;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Board {
    // Tabuleiro com layout configurável
    private final List<Cell> track = new ArrayList<>();
    private final Map<PlayerColorEnum, Integer> startIndex = new EnumMap<>(PlayerColorEnum.class);
    private final Set<Integer> endpoints = new HashSet<>();
    private final Set<Integer> trianglePenalty = new HashSet<>();

    private final int rows;
    private final int cols;

    public Board() {
        this(new Cross2x2Layout16());
    }

    public Board(BoardLayout layout) {
        this.rows = layout.getRows();
        this.cols = layout.getCols();

        List<int[]> coords = layout.getTrackCoords();

        // Criar caminho (track)
        for (int i = 0; i < coords.size(); i++) {
            int r = coords.get(i)[0];
            int c = coords.get(i)[1];
            track.add(new Cell(i, r, c, CellTypeEnum.NORMAL));
        }

        // ENDPOINTs via layout
        for (int[] ep : layout.getEndpointCoords()) {
            int idx = indexOfCoord(coords, ep[0], ep[1]);
            if (idx >= 0) {
                endpoints.add(idx);
                setCellType(idx, CellTypeEnum.ENDPOINT);
            }
        }

        // Triângulos: 2 casas antes e 2 depois de cada ponta
        int n = track.size();
        for (int ep : endpoints) {
            int b1 = mod(ep - 1, n);
            int b2 = mod(ep - 2, n);
            int a1 = mod(ep + 1, n);
            int a2 = mod(ep + 2, n);
            trianglePenalty.addAll(Arrays.asList(b1, b2, a1, a2));
        }
        for (int idx : trianglePenalty) {
            if (!endpoints.contains(idx)) {
                setCellType(idx, CellTypeEnum.TRIANGLE_PENALTY);
            }
        }

        // STARTs por cor via layout
        for (PlayerColorEnum color : PlayerColorEnum.values()) {
            int[] st = layout.getStartForColor(color);
            int idx = indexOfCoord(coords, st[0], st[1]);
            if (idx >= 0) {
                setCellType(idx, CellTypeEnum.START);
                startIndex.put(color, idx);
            }
        }
        // fallback
        for (PlayerColorEnum color : PlayerColorEnum.values()) {
            startIndex.putIfAbsent(color, 0);
        }
    }

    private void setCellType(int index, CellTypeEnum type) {
        Cell old = track.get(index);
        Cell cell = new Cell(old.getIndex(), old.getRow(), old.getCol(), type);
        cell.setOccupant(old.getOccupant());
        track.set(index, cell);
    }

    private int mod(int a, int n) {
        int m = a % n;
        return m < 0 ? m + n : m;
    }

    public List<Cell> getTrack() { return track; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public int getStartIndex(PlayerColorEnum color) {
        return startIndex.get(color);
    }

    public boolean isEndpoint(int idx) { return endpoints.contains(idx); }
    public boolean isTriangle(int idx) { return trianglePenalty.contains(idx); }

    public boolean canPlaceAtStart(Player player) {
        int idx = getStartIndex(player.getColor());
        return track.get(idx).isFree();
    }

    public boolean isFree(int index) {
        return track.get(index).isFree();
    }

    public void occupy(int index, Piece piece) {
        track.get(index).setOccupant(piece);
    }

    public void free(int index) {
        track.get(index).setOccupant(null);
    }

    public int size() { return track.size(); }

    public int advanceIndex(int from, int steps) {
        return mod(from + steps, size());
    }

    private int indexOfCoord(List<int[]> coords, int r, int c) {
        for (int i = 0; i < coords.size(); i++) {
            int[] p = coords.get(i);
            if (p[0] == r && p[1] == c) return i;
        }
        return -1;
    }
}