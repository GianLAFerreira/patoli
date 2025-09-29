package padroes.projeto.patoli.model.game.rules;

import padroes.projeto.patoli.model.board.Board;

/**
 * Regras puras de pontuação/efeitos ao pousar.
 * Determina penalidades e bônus sem mutar estado do jogo.
 */
public class ScoringRules {

    /**
     * Retorna o delta de moedas para o jogador atual ao pousar na célula 'index'.
     * -1 em casas de punição (triângulos).
     *  0 caso contrário.
     */
    public int penaltyForLanding(Board board, int index) {
        return board.isTriangle(index) ? -1 : 0;
    }

    /**
     * Indica se a casa confere turno extra (ENDPOINT).
     */
    public boolean isExtraTurn(Board board, int index) {
        return board.isEndpoint(index);
    }
}