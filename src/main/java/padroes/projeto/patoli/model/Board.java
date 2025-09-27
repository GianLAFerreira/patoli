package padroes.projeto.patoli.model;

import java.util.*;

public class Board {
    // Tabuleiro em cruz 16x16 (proporção 1:1), braços largura 2, trilha nas duas linhas/colunas centrais
    private final List<Cell> track = new ArrayList<>();
    private final Map<PlayerColor, Integer> startIndex = new EnumMap<>(PlayerColor.class);
    private final Set<Integer> endpoints = new HashSet<>();
    private final Set<Integer> trianglePenalty = new HashSet<>();

    private final int rows = 16;
    private final int cols = 16;

    public Board() {
        // Trilho cruz 2x2 central com uma ordem que dá:
        // - De (7,8) o próximo é (7,9) (direita) -> Preto
        // - De (8,7) o próximo é (8,6) (esquerda) -> Branco
        List<int[]> coords = buildCross2x2ClockwiseOrder();

        for (int i = 0; i < coords.size(); i++) {
            int r = coords.get(i)[0];
            int c = coords.get(i)[1];
            CellType type = CellType.NORMAL;
            track.add(new Cell(i, r, c, type));
        }

        // ENDPOINTs exatamente nos pontos fornecidos
        int[][] endpointCoords = new int[][]{
                // Topo
                {0,7},{0,8},
                // Base
                {15,7},{15,8},
                // Esquerda
                {7,0},{8,0},
                // Direita
                {7,15},{8,15}
        };
        for (int[] ep : endpointCoords) {
            int idx = indexOfCoord(coords, ep[0], ep[1]);
            if (idx >= 0) {
                endpoints.add(idx);
                setCellType(idx, CellType.ENDPOINT);
            }
        }

        // Triângulos: 2 casas antes e 2 depois de cada ponta (mantém regra atual)
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
                setCellType(idx, CellType.TRIANGLE_PENALTY);
            }
        }

        // STARTs distintos:
        // Preto em (7,8) -> próximo índice é (7,9) (direita)
        // Branco em (8,7) -> próximo índice é (8,6) (esquerda)
        int blackStart = indexOfCoord(coords, 7, 8);
        int whiteStart = indexOfCoord(coords, 8, 7);
        if (blackStart >= 0) setCellType(blackStart, CellType.START);
        if (whiteStart >= 0) setCellType(whiteStart, CellType.START);

        startIndex.put(PlayerColor.BLACK, blackStart >= 0 ? blackStart : 0);
        startIndex.put(PlayerColor.WHITE, whiteStart >= 0 ? whiteStart : Math.min(1, track.size() - 1));
    }

    // ... existing code ...
    // Ordem do trilho para cruz 2x2 em 16x16 seguindo exatamente a sequência descrita para o BRANCO:
    // (8,7) -> esquerda até a extremidade (8,0) -> sobe 1 (7,0) ->
    // centro pela linha 7 até (7,7) -> sobe até o topo pela coluna 7 até (0,7) ->
    // direita 1 (0,8) -> desce ao centro pela coluna 8 até (8,8) ->
    // direita até a extremidade (8,15) -> desce 1 (9,15) ->
    // centro pela linha 9 até (9,8) -> desce até a base pela coluna 8 até (15,8) ->
    // esquerda 1 (15,7) -> sobe ao centro pela coluna 7 até (8,7) [fecha o ciclo].
    private List<int[]> buildCross2x2ClockwiseOrder() {
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

        // Deduplicar mantendo ordem
        List<int[]> dedup = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        for (int[] p : path) {
            long key = ((((long) p[0]) & 0xffffffffL) << 32) | (((long) p[1]) & 0xffffffffL);
            if (seen.add(key)) dedup.add(p);
        }

        // Remover duplicatas consecutivas
        List<int[]> cleaned = new ArrayList<>();
        int lastR = Integer.MIN_VALUE, lastC = Integer.MIN_VALUE;
        for (int[] p : dedup) {
            if (p[0] == lastR && p[1] == lastC) continue;
            cleaned.add(p);
            lastR = p[0]; lastC = p[1];
        }
        return cleaned;
    }




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

    private void setCellType(int index, CellType type) {
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

    public int getStartIndex(PlayerColor color) {
        return startIndex.get(color);
    }

    public boolean isEndpoint(int idx) { return endpoints.contains(idx); }
    public boolean isTriangle(int idx) { return trianglePenalty.contains(idx); }

    public boolean canPlaceAtStart(Player player) {
        int idx = getStartIndex(player.getColor());
        Cell cell = track.get(idx);
        return cell.isFree();
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