package padroes.projeto.patoli;

import padroes.projeto.patoli.controller.GameController;
import padroes.projeto.patoli.model.game.Game;
import padroes.projeto.patoli.view.frame.MainFrame;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Game game = new Game("Jogador Preto", "Jogador Branco");
            GameController controller = new GameController(game);
            MainFrame frame = new MainFrame(controller);
            controller.setView(frame);
            frame.goFullScreen();
            frame.setVisible(true);
        });
    }
}