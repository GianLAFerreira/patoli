package padroes.projeto.patoli.view.frame;

import padroes.projeto.patoli.controller.GameController;
import padroes.projeto.patoli.controller.viewmodel.enums.PlayerColorVMEnum;
import padroes.projeto.patoli.view.panel.ActionBarPanel;
import padroes.projeto.patoli.view.panel.BannerBarPanel;
import padroes.projeto.patoli.view.panel.BoardPanel;
import padroes.projeto.patoli.view.panel.PlayerInfoPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements GameView {
    private final GameController controller;

    private final BoardPanel boardPanel;
    private final JPanel leftPanel;
    private final PlayerInfoPanel blackInfoPanel;
    private final PlayerInfoPanel whiteInfoPanel;
    private final ActionBarPanel actionBar;
    private final BannerBarPanel banner;

    public MainFrame(GameController controller) {
        super("Patolli - MVC");
        this.controller = controller;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 820);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Banner no topo
        banner = new BannerBarPanel();
        add(banner, BorderLayout.NORTH);

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

        // Barra de ações na direita
        actionBar = new ActionBarPanel(controller);
        add(actionBar, BorderLayout.EAST);
    }

    @Override
    public void refresh() {
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
        Color ok = new Color(40, 130, 70);
        Color warn = new Color(180, 120, 40);
        Color info = new Color(36, 39, 46);
        Color bonus = new Color(200, 160, 0);
        Color finish = new Color(30, 145, 90);
        Color danger = new Color(160, 55, 55);

        switch (reason) {
            case "ROLL" -> banner.showBanner("Rolagem: " + controller.getLastRoll(), info, new Color(235, 235, 235), 1200);
            case "ENTER_INIT" -> banner.showBanner("Peças iniciais posicionadas", info, new Color(235, 235, 235), 1200);
            case "ENTER" -> banner.showBanner("Nova peça entrou no tabuleiro", ok, Color.WHITE, 1200);
            case "MOVE" -> banner.showBanner("Peça movida", info, new Color(235, 235, 235), 800);
            case "FINISH" -> banner.showBanner("Peça finalizada! +1 moeda", finish, Color.WHITE, 1800);
            case "PENALTY" -> banner.showBanner("Penalidade: -1 moeda", warn, Color.BLACK, 1600);
            case "BONUS" -> banner.showBanner("Bônus: jogada extra!", bonus, Color.BLACK, 1600);
            case "TURN" -> {
                PlayerColorVMEnum c = controller.getCurrentPlayerColor();
                String name = controller.getPlayerName(c);
                banner.showBanner("Vez: " + name + " (" + c + ")", info, new Color(235, 235, 235), 1200);
            }
            case "NO_COINS" -> banner.showBanner("Jogador sem moedas!", danger, Color.WHITE, 1600);
            case "INIT" -> banner.showBanner("Partida iniciada", info, new Color(235, 235, 235), 1200);
            default -> { /* silencioso */ }
        }
    }

    public void goFullScreen() {
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
}