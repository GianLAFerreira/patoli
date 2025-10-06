package padroes.projeto.patoli.model.game.rules;

import padroes.projeto.patoli.model.board.Board;
import padroes.projeto.patoli.model.board.Piece;
import padroes.projeto.patoli.model.board.Player;

/**
 * Regras puras de movimento.
 * Responsável por calcular o destino e a finalização (exatidão + START livre),
 * e bloquear "ultrapassagem" quando a peça está a poucos passos da casa de saída.
 */
public class MovementRules {

    /**
     * Calcula o destino de uma peça avançando 'steps' na trilha.
     *
     * Regras:
     * - Permite cruzar a START em movimentos que não estejam finalizando (sem efeito).
     * - Se a distância até a START (no sentido do percurso) for 'dist' > 0:
     *   - steps <  dist  => move normalmente 'steps' casas.
     *   - steps == dist  => finaliza (Piece.FINISHED), somente se a START estiver livre.
     *   - steps >  dist  => movimento inválido (Integer.MIN_VALUE), NÃO dá a volta novamente.
     */
    public int computeDestination(Board board, Player current, int from, int steps) {
        if (steps <= 0) return from;

        int startIdx = board.getStartIndex(current.getColor());
        int n = board.size();

        // Distância linear até a START no sentido do percurso
        int distToStart = (startIdx - from);
        if (distToStart < 0) distToStart += n;

        if (distToStart > 0) {
            // Está "a caminho" da START
            if (steps > distToStart) {
                // Ultrapassaria a START — bloquear o movimento desta peça
                return Integer.MIN_VALUE;
            }
            if (steps == distToStart) {
                // Finaliza somente se a START estiver livre
                return board.isFree(startIdx) ? Piece.FINISHED : Integer.MIN_VALUE;
            }
            // steps < distToStart: movimento normal
            return board.advanceIndex(from, steps);
        }

        // distToStart == 0: a peça está exatamente na START (não finalizada).
        // Não há finalização com steps > 0; segue movimento normal.
        return board.advanceIndex(from, steps);
    }
}