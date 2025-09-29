package padroes.projeto.patoli.model.game.rules;

import padroes.projeto.patoli.model.board.Board;
import padroes.projeto.patoli.model.board.Piece;
import padroes.projeto.patoli.model.board.Player;

/**
 * Regras puras de movimento.
 * Responsável por calcular o destino e a finalização (exatidão + START livre).
 */
public class MovementRules {

    /**
     * Calcula o destino de uma peça avançando 'steps' na trilha.
     * - Permite cruzar a START.
     * - Finaliza somente se cair exatamente na START E ela estiver livre.
     * - Se tentar finalizar com START ocupada, retorna Integer.MIN_VALUE (movimento inválido).
     */
    public int computeDestination(Board board, Player current, int from, int steps) {
        int dest = board.advanceIndex(from, steps);

        if (steps > 0) {
            int startIdx = board.getStartIndex(current.getColor());
            if (dest == startIdx) {
                if (board.isFree(startIdx)) {
                    return Piece.FINISHED;
                } else {
                    return Integer.MIN_VALUE; // START ocupada: não pode finalizar
                }
            }
        }
        return dest;
    }
}