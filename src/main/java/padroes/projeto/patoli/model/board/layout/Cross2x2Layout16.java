package padroes.projeto.patoli.model.board.layout;

import padroes.projeto.patoli.model.board.enums.PlayerColorEnum;

import java.util.*;

public class Cross2x2Layout16 implements BoardLayout {
    private final int rows = 16;
    private final int cols = 16;

    @Override
    public int getRows() { return rows; }

    @Override
    public int getCols() { return cols; }

    @Override
    public List<int[]> getTrackCoords() {
        // Caminho atual 16x16 (cruz 2x2 central) na ordem validada
        List<int[]> path = new ArrayList<>();

        // 1) (8,7) -> (8,0): linha 8 para a esquerda até a extremidade
        addH(path, 8, 7, 0);
        // 2) (8,0) -> (7,0): sobe uma casa
        addV(path, 7, 7, 0);
        // 3) (7,0) -> (7,7): volta ao centro pela linha 7 (direita)
        addH(path, 7, 0, 7);
        // 4) (7,7) -> (0,7): sobe até a extremidade superior (coluna 7)
        addV(path, 6, 0, 7);
        // 5) (0,7) -> (0,8): vai para a direita uma casa (topo)
        addH(path, 0, 7, 8);
        // 6) (0,8) -> (8,7): desce ao centro (coluna 8)
        addV(path, 1, 7, 8);
        // 7) (7,8) -> (8,15): vai para a direita até a extremidade (linha 8)
        addH(path, 7, 8, 15);
        // 8) (8,15) -> (8,15): desce uma casa (quina direita)
        addV(path, 8, 8, 15);
        // 9) (8,15) -> (9,8): volta ao centro pela linha 9 (esquerda)
        addH(path, 8, 15, 8);
        // 10) (9,8) -> (15,8): desce até a extremidade inferior (coluna 8)
        addV(path, 9, 15, 8);
        // 11) (15,8) -> (15,7): vai para a esquerda uma casa (base)
        addH(path, 15, 8, 7);
        // 12) (15,7) -> (8,7): sobe ao centro (coluna 7), fechando o ciclo
        addV(path, 14, 8, 7);

        return dedup(path);
    }

    @Override
    public List<int[]> getEndpointCoords() {
        return List.of(
                new int[]{0,7}, new int[]{0,8},      // Topo
                new int[]{15,7}, new int[]{15,8},    // Base
                new int[]{7,0}, new int[]{8,0},      // Esquerda
                new int[]{7,15}, new int[]{8,15}     // Direita
        );
    }

    @Override
    public int[] getStartForColor(PlayerColorEnum color) {
        if (color == PlayerColorEnum.BLACK) return new int[]{7, 8};
        return new int[]{8, 7}; // WHITE
    }

    // Helpers
    private void addH(List<int[]> path, int row, int fromCol, int toCol) {
        if (fromCol <= toCol) {
            for (int c = fromCol; c <= toCol; c++) path.add(new int[]{row, c});
        } else {
            for (int c = fromCol; c >= toCol; c--) path.add(new int[]{row, c});
        }
    }
    private void addV(List<int[]> path, int fromRow, int toRow, int col) {
        if (fromRow <= toRow) {
            for (int r = fromRow; r <= toRow; r++) path.add(new int[]{r, col});
        } else {
            for (int r = fromRow; r >= toRow; r--) path.add(new int[]{r, col});
        }
    }
    private List<int[]> dedup(List<int[]> path) {
        List<int[]> cleaned = new ArrayList<>();
        Set<Long> seen = new LinkedHashSet<>();
        for (int[] p : path) {
            long key = ((((long) p[0]) & 0xffffffffL) << 32) | (((long) p[1]) & 0xffffffffL);
            if (seen.add(key)) cleaned.add(p);
        }
        return cleaned;
    }
}