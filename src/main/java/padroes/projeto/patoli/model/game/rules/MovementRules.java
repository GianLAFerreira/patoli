package padroes.projeto.patoli.model.game.rules;

import padroes.projeto.patoli.model.board.Board;
import padroes.projeto.patoli.model.board.Piece;
import padroes.projeto.patoli.model.board.Player;

/**
 * Regras puras de movimento.
 * Responsável por calcular o destino e a finalização (exatidão),
 * bloqueando ultrapassagem e permitindo finalizar mesmo com START ocupada.
 */
public class MovementRules {

    /**
     * Calcula o destino de uma peça avançando 'steps' na trilha.
     *
     * Regras:
     * - Se a distância até a START (no sentido do percurso) for 'dist' > 0:
     *   - steps <  dist  => move normalmente 'steps' casas.
     *   - steps == dist  => finaliza (Piece.FINISHED), independentemente da START estar ocupada.
     *   - steps >  dist  => movimento inválido (Integer.MIN_VALUE), NÃO dá a volta novamente.
     * - Permite cruzar a START quando não estiver finalizando (sem efeito).
     */
    public int computeDestination(Board board, Player current, int from, int steps) {
        if (steps <= 0) return from;

        int startIdx = board.getStartIndex(current.getColor());
        int n = board.size();

        // Distância linear até a START no sentido do percurso
        int distToStart = (startIdx - from);
        if (distToStart < 0) distToStart += n;

        if (distToStart > 0) {
            if (steps > distToStart) {
                // Ultrapassaria a START — bloquear o movimento desta peça
                return Integer.MIN_VALUE;
            }
            if (steps == distToStart) {
                // Finaliza mesmo que a START esteja ocupada (sem captura e sem ocupar a START)
                return Piece.FINISHED;
            }
            // steps < distToStart: movimento normal
            return board.advanceIndex(from, steps);
        }

        // distToStart == 0: a peça está exatamente na START (não finalizada).
        // Não há finalização com steps > 0; segue movimento normal.
        return board.advanceIndex(from, steps);
    }
}