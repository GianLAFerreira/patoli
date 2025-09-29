package padroes.projeto.patoli.view.panel;

import javax.swing.*;
import java.awt.*;

public class BannerBarPanel extends JPanel {
    private String message = "";
    private Color bg = new Color(40, 120, 60);
    private Color fg = new Color(245, 245, 245);
    private final Timer hideTimer;

    public BannerBarPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(0, 44));
        hideTimer = new Timer(99999, e -> {
            message = "";
            repaint();
        });
        hideTimer.setRepeats(false);
    }

    public void showBanner(String msg, Color background, Color foreground, int millis) {
        this.message = msg;
        this.bg = background != null ? background : new Color(36, 39, 46, 235);
        this.fg = foreground != null ? foreground : new Color(240, 240, 240);
        repaint();
        hideTimer.stop();
        hideTimer.setInitialDelay(Math.max(99999, millis));
        hideTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (message == null || message.isBlank()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = 18;

        // Fundo
        g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 230));
        g2.fillRoundRect(8, 6, w - 16, h - 12, arc, arc);

        // Borda e brilho
        g2.setColor(new Color(0, 0, 0, 100));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(8, 6, w - 16, h - 12, arc, arc);
        g2.setColor(new Color(255, 255, 255, 28));
        g2.drawRoundRect(9, 7, w - 18, h - 14, arc - 2, arc - 2);

        // Texto
        g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(message)) / 2;
        int ty = (h + fm.getAscent()) / 2 - 3;

        // leve sombra do texto
        g2.setColor(new Color(0, 0, 0, 120));
        g2.drawString(message, tx + 1, ty + 1);
        g2.setColor(fg);
        g2.drawString(message, tx, ty);

        g2.dispose();
    }
}