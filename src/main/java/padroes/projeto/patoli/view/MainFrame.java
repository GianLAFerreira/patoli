package padroes.projeto.patoli.view;

import padroes.projeto.patoli.controller.GameController;
import padroes.projeto.patoli.model.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final GameController controller;
    private final Game game;

    private final BoardPanel boardPanel;

    // Painel esquerdo com infos dos jogadores
    private final JPanel leftPanel;
    private final PlayerInfoPanel blackInfoPanel;
    private final PlayerInfoPanel whiteInfoPanel;

    // Barra de ações (controles)
    private final ActionBarPanel actionBar;

    public MainFrame(GameController controller, Game game) {
        super("Patolli - MVC");
        this.controller = controller;
        this.game = game;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 820);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Painel do tabuleiro (centro)
        boardPanel = new BoardPanel(controller, game);
        add(boardPanel, BorderLayout.CENTER);

        // Painel esquerdo: infos dos jogadores (moedas e peças)
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setBackground(new Color(20, 20, 20));
        GridBagConstraints gl = new GridBagConstraints();
        gl.gridx = 0; gl.weightx = 1; gl.fill = GridBagConstraints.HORIZONTAL; gl.insets = new Insets(6, 6, 6, 6);

        blackInfoPanel = new PlayerInfoPanel(game, PlayerColor.BLACK);
        gl.gridy = 0;
        leftPanel.add(blackInfoPanel, gl);

        whiteInfoPanel = new PlayerInfoPanel(game, PlayerColor.WHITE);
        gl.gridy = 1;
        leftPanel.add(whiteInfoPanel, gl);

        add(leftPanel, BorderLayout.WEST);

        // Barra de ações posicionada à direita do tabuleiro
        actionBar = new ActionBarPanel(controller, game);
        add(actionBar, BorderLayout.EAST);

        refresh();
    }

    public void refresh() {
        actionBar.refresh();
        boardPanel.repaint();
        blackInfoPanel.refresh();
        whiteInfoPanel.refresh();
    }

    public void goFullScreen() {
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
}