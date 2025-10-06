package padroes.projeto.patoli.view.frame;

import padroes.projeto.patoli.controller.GameController;
import padroes.projeto.patoli.controller.viewmodel.enums.PlayerColorVMEnum;
import padroes.projeto.patoli.view.panel.ActionBarPanel;
import padroes.projeto.patoli.view.panel.BoardPanel;
import padroes.projeto.patoli.view.panel.PlayerInfoPanel;
import padroes.projeto.patoli.view.panel.PotPanel;
import padroes.projeto.patoli.view.frame.GameView;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements GameView {
    private final GameController controller;

    private final BoardPanel boardPanel;
    private final JPanel leftPanel;
    private final PlayerInfoPanel blackInfoPanel;
    private final PlayerInfoPanel whiteInfoPanel;

    // Coluna da direita passa a ter o pote + a barra de ações
    private final JPanel rightColumn;
    private final PotPanel potPanel;
    private final ActionBarPanel actionBar;

    public MainFrame(GameController controller) {
        super("Patolli - MVC");
        this.controller = controller;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 820);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tabuleiro (centro)
        boardPanel = new BoardPanel(controller);
        add(boardPanel, BorderLayout.CENTER);

        // Painel esquerdo com infos dos jogadores
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setBackground(new Color(20, 20, 20));
        GridBagConstraints gl = new GridBagConstraints();
        gl.gridx = 0; gl.weightx = 1; gl.fill = GridBagConstraints.HORIZONTAL; gl.insets = new Insets(6, 6, 6, 6);

        blackInfoPanel = new PlayerInfoPanel(controller, PlayerColorVMEnum.BLACK);
        gl.gridy = 0;
        leftPanel.add(blackInfoPanel, gl);

        whiteInfoPanel = new PlayerInfoPanel(controller, PlayerColorVMEnum.WHITE);
        gl.gridy = 1;
        leftPanel.add(whiteInfoPanel, gl);

        add(leftPanel, BorderLayout.WEST);

        // Coluna da direita (NORTH: pote, CENTER: barra de ação)
        rightColumn = new JPanel(new BorderLayout());
        rightColumn.setOpaque(false);
        rightColumn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        potPanel = new PotPanel(controller);
        actionBar = new ActionBarPanel(controller);

        rightColumn.add(potPanel, BorderLayout.NORTH);
        rightColumn.add(actionBar, BorderLayout.CENTER);

        add(rightColumn, BorderLayout.EAST);
    }

    @Override
    public void refresh() {
        potPanel.refresh();
        actionBar.refresh();
        boardPanel.refresh();
        blackInfoPanel.refresh();
        whiteInfoPanel.refresh();
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @Override
    public void onEvent(String reason) {
        // Apenas solicitar refresh (a UI lê estado pelo controller)
        refresh();
    }

    public void goFullScreen() {
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
}