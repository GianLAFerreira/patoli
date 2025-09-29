package padroes.projeto.patoli.model.game;

public interface GameObserver {
    // reason: identificador textual do evento (ex.: "ROLL", "MOVE", "ENTER", "TURN", "GAME_OVER", ...)
    void onGameChanged(Game game, String reason);
}