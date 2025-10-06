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

        // Marcação de casas de punição por COORDENADAS (cobre ida e volta)
        trianglePenalty.clear();
        Set<Long> penaltyCoords = new HashSet<>();
        for (int epIndex : endpoints) {
            Cell epCell = track.get(epIndex);
            int r = epCell.getRow();
            int c = epCell.getCol();

            // Topo
            if (r == 0) {
                addPenaltyCoord(penaltyCoords, 1, c);
                addPenaltyCoord(penaltyCoords, 2, c);
            }
            // Base
            if (r == rows - 1) {
                addPenaltyCoord(penaltyCoords, rows - 2, c);
                addPenaltyCoord(penaltyCoords, rows - 3, c);
            }
            // Esquerda
            if (c == 0) {
                addPenaltyCoord(penaltyCoords, r, 1);
                addPenaltyCoord(penaltyCoords, r, 2);
            }
            // Direita
            if (c == cols - 1) {
                addPenaltyCoord(penaltyCoords, r, cols - 2);
                addPenaltyCoord(penaltyCoords, r, cols - 3);
            }
        }

        // Converter coordenadas de punição em índices do track e marcar
        for (long key : penaltyCoords) {
            int pr = (int)(key >> 32);
            int pc = (int)(key & 0xffffffffL);
            int idx = indexOfCoord(coords, pr, pc);
            if (idx >= 0 && !endpoints.contains(idx)) {
                trianglePenalty.add(idx);
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

    private void addPenaltyCoord(Set<Long> set, int r, int c) {
        if (r < 0 || c < 0 || r >= rows || c >= cols) return;
        long key = (((long) r) << 32) | (c & 0xffffffffL);
        set.add(key);
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