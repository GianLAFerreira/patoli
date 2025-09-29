package padroes.projeto.patoli.view.frame;

public interface GameView {
    // Solicita atualização completa da interface
    void refresh();

    // Exibe uma mensagem informativa/alerta ao usuário
    void showMessage(String message);

    // Notificação de evento do jogo (para banners/animações)
    void onEvent(String reason);
}