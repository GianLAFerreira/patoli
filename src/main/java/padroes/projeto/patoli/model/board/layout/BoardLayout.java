package padroes.projeto.patoli.model.board.layout;

import padroes.projeto.patoli.model.board.enums.PlayerColor;

import java.util.List;

public interface BoardLayout {
    int getRows();
    int getCols();
    // Lista de coordenadas (row, col) que formam o caminho, em ordem
    List<int[]> getTrackCoords();
    // Coordenadas (row, col) das extremidades (ENDPOINT)
    List<int[]> getEndpointCoords();
    // Coordenada START por cor
    int[] getStartForColor(PlayerColor color);
}