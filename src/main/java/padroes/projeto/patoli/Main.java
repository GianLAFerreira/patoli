package padroes.projeto.patoli;

import padroes.projeto.patoli.controller.GameController;
import padroes.projeto.patoli.model.Game;
import padroes.projeto.patoli.view.MainFrame;

import java.awt.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Game game = new Game("Jogador Preto", "Jogador Branco");
            GameController controller = new GameController(game);
            MainFrame frame = new MainFrame(controller, game);
            controller.setView(frame);
            frame.goFullScreen();
            frame.setVisible(true);
        });
    }

}