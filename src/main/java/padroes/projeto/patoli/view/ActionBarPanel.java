package padroes.projeto.patoli.view;

import padroes.projeto.patoli.controller.GameController;
import padroes.projeto.patoli.model.Game;
import padroes.projeto.patoli.model.Player;

import javax.swing.*;
import java.awt.*;

public class ActionBarPanel extends JPanel {
    private final GameController controller;
    private final Game game;

    private final JButton rollButton;
    private final JButton enterButton;
    private final JButton passButton;
    private final JLabel turnLabel;
    private final JLabel rollLabel;

    public ActionBarPanel(GameController controller, Game game) {
        this.controller = controller;
        this.game = game;

        setOpaque(false);
        setPreferredSize(new Dimension(300, 0)); // largura fixa agradável na direita
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 8, 8, 8);

        // Cartão de fundo (container interno) para visual melhor
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override public boolean isOpaque() { return false; }
        };

        // Título/Turno
        turnLabel = new JLabel();
        turnLabel.setForeground(new Color(235, 235, 235));
        turnLabel.setFont(getFont().deriveFont(Font.BOLD, 16f));
        GridBagConstraints c0 = new GridBagConstraints();
        c0.gridx = 0; c0.gridy = 0; c0.weightx = 1; c0.insets = new Insets(6, 10, 2, 10); c0.anchor = GridBagConstraints.CENTER;
        card.add(turnLabel, c0);

        // Última rolagem
        rollLabel = new JLabel();
        rollLabel.setForeground(new Color(210, 210, 210));
        rollLabel.setFont(getFont().deriveFont(Font.PLAIN, 14f));
        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0; c1.gridy = 1; c1.weightx = 1; c1.insets = new Insets(0, 10, 10, 10); c1.anchor = GridBagConstraints.CENTER;
        card.add(rollLabel, c1);

        // Botões empilhados (verticais)
        rollButton = createPrimaryButton("Rolar Moedas");
        rollButton.addActionListener(e -> controller.onRoll());

        enterButton = createSecondaryButton("Entrar Peça (1)");
        enterButton.addActionListener(e -> controller.onEnterNewPiece());

        passButton = createGhostButton("Passar");
        passButton.addActionListener(e -> controller.onPassIfRequired());

        GridBagConstraints cb = new GridBagConstraints();
        cb.gridx = 0; cb.weightx = 1; cb.fill = GridBagConstraints.HORIZONTAL;
        cb.insets = new Insets(6, 12, 6, 12);

        cb.gridy = 2; card.add(rollButton, cb);
        cb.gridy = 3; card.add(enterButton, cb);
        cb.gridy = 4; card.add(passButton, cb);

        // Espaçador
        GridBagConstraints csp = new GridBagConstraints();
        csp.gridx = 0; csp.gridy = 5; csp.weightx = 1; csp.weighty = 1; csp.fill = GridBagConstraints.BOTH;
        card.add(Box.createVerticalGlue(), csp);

        add(card, gc);
    }

    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(0x2D6AE3));
        b.setFont(getFont().deriveFont(Font.BOLD, 14f));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x1F4DB0)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        return b;
    }

    private JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setForeground(new Color(240, 240, 240));
        b.setBackground(new Color(60, 60, 60));
        b.setFont(getFont().deriveFont(Font.BOLD, 14f));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 90)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        return b;
    }

    private JButton createGhostButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setForeground(new Color(220, 220, 220));
        b.setFont(getFont().deriveFont(Font.BOLD, 14f));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100, 120)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        return b;
    }

    public void refresh() {
        Player current = game.getCurrent();
        Player opp = game.getOpponent();
        String turnText = "Vez: " + current.getName() + " (" + current.getColor() + ")";
        turnLabel.setText(turnText);

        String rollText = "Rolagem: " + (game.getLastRoll() >= 0 ? game.getLastRoll() : "-");
        rollLabel.setText(rollText);

        boolean canRoll = game.getLastRoll() == -1 && !game.isGameOver();
        rollButton.setEnabled(canRoll);
        enterButton.setEnabled(game.canEnterNewPiece() && !game.isGameOver());
        passButton.setEnabled(!game.isGameOver());

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Fundo estilizado vertical (cartão à direita)
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = 22;

        GradientPaint gp = new GradientPaint(0, 0, new Color(36, 39, 46, 235), w, 0, new Color(28, 30, 36, 235));
        g2.setPaint(gp);
        g2.fillRoundRect(6, 6, w - 12, h - 12, arc, arc);

        // Borda
        g2.setColor(new Color(0, 0, 0, 100));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(6, 6, w - 12, h - 12, arc, arc);

        // Brilho interno
        g2.setColor(new Color(255, 255, 255, 28));
        g2.drawRoundRect(8, 8, w - 16, h - 16, arc - 2, arc - 2);

        g2.dispose();
        super.paintComponent(g);
    }
}