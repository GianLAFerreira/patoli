package padroes.projeto.patoli.view.panel;

import padroes.projeto.patoli.controller.GameController;
import padroes.projeto.patoli.controller.viewmodel.PieceVM;
import padroes.projeto.patoli.controller.viewmodel.enums.PlayerColorEnum;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PlayerInfoPanel extends JPanel {
    private final GameController controller;
    private final PlayerColorEnum color;

    public PlayerInfoPanel(GameController controller, PlayerColorEnum color) {
        this.controller = controller;
        this.color = color;
        setPreferredSize(new Dimension(240, 260));

        setBackground(new Color(28, 28, 28));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    public void refresh() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        boolean isCurrent = (controller.getCurrentPlayerColor() == color);
        String title = controller.getPlayerName(color) + " (" + color + ")";

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
        g2.setColor(isCurrent ? new Color(255, 215, 0) : new Color(210, 210, 210));
        g2.drawString(title, 8, 20);

        int y = 36;
        g2.setColor(new Color(70, 70, 70));
        g2.drawLine(8, y, getWidth() - 8, y);
        y += 12;

        g2.setFont(getFont().deriveFont(Font.PLAIN, 13f));
        g2.setColor(new Color(210, 210, 210));
        g2.drawString("Moedas: " + controller.getPlayerCoins(color), 8, y);
        y += 10;

        y = drawCoins(g2, 8, y + 6, controller.getPlayerCoins(color));

        y += 8;
        g2.setColor(new Color(70, 70, 70));
        g2.drawLine(8, y, getWidth() - 8, y);
        y += 14;

        List<PieceVM> reserved = controller.getReservedPieces(color);
        g2.setColor(new Color(210, 210, 210));
        g2.drawString("Reservadas: " + reserved.size(), 8, y);
        y += 8;
        y = drawMiniPieces(g2, 8, y + 6, reserved, color);

        y += 8;
        g2.setColor(new Color(70, 70, 70));
        g2.drawLine(8, y, getWidth() - 8, y);
        y += 14;

        List<PieceVM> finished = controller.getFinishedPieces(color);
        g2.setColor(new Color(210, 210, 210));
        g2.drawString("Finalizadas: " + finished.size(), 8, y);
        y += 8;
        drawMiniPieces(g2, 8, y + 6, finished, color);

        g2.dispose();
    }

    private int drawCoins(Graphics2D g2, int x, int startY, int coins) {
        int innerPadding = 16;
        int available = Math.max(60, getWidth() - innerPadding);
        int perRow = 10;
        int gap = 4;
        int size = (available - (perRow - 1) * gap) / perRow;
        size = Math.max(14, Math.min(22, size));

        int cx = x;
        int cy = startY;
        int shown = Math.min(coins, 40);
        for (int i = 0; i < shown; i++) {
            drawCoinIcon(g2, cx, cy, size);
            cx += size + gap;
            if ((i + 1) % perRow == 0) {
                cx = x;
                cy += size + gap;
            }
        }
        if (coins > shown) {
            g2.setColor(new Color(210, 210, 210));
            g2.drawString("+" + (coins - shown), x, cy + size + gap + 12);
            cy += 12;
        }
        return cy + size + gap;
    }

    private void drawCoinIcon(Graphics2D g2, int x, int y, int size) {
        int r = size;
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillOval(x + 2, y + 2, r, r);

        RadialGradientPaint paint = new RadialGradientPaint(
                new Point(x + r / 3, y + r / 3), r,
                new float[]{0f, 0.7f, 1f},
                new Color[]{new Color(255, 245, 200), new Color(255, 215, 0), new Color(200, 160, 0)}
        );
        Paint old = g2.getPaint();
        g2.setPaint(paint);
        g2.fillOval(x, y, r, r);
        g2.setPaint(old);

        g2.setColor(new Color(120, 90, 0));
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawOval(x, y, r, r);

        g2.setColor(new Color(255, 255, 255, 110));
        int d = Math.max(4, r / 3);
        g2.fillOval(x + r / 6, y + r / 6, d, d);
    }

    private int drawMiniPieces(Graphics2D g2, int x, int startY, List<PieceVM> pieces, PlayerColorEnum color) {
        int cx = x, cy = startY;
        int size = 22, gap = 6;

        for (PieceVM pc : pieces) {
            drawMiniPieceToken(g2, cx, cy, size, color, pc.id);
            cx += size + gap;
            if ((cx + size) > getWidth()) {
                cx = x;
                cy += size + gap;
            }
        }
        return cy + size + gap;
    }

    private void drawMiniPieceToken(Graphics2D g2, int x, int y, int size, PlayerColorEnum color, int id) {
        int r = size / 2;
        int cx = x + r, cy = y + r;

        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(cx - r + 2, cy - r + 2, 2 * r, 2 * r);

        boolean isBlack = (color == PlayerColorEnum.BLACK);
        RadialGradientPaint bodyPaint = new RadialGradientPaint(
                new Point(cx - r / 3, cy - r / 3), r,
                new float[]{0f, 0.65f, 1f},
                isBlack
                        ? new Color[]{new Color(85, 85, 85), new Color(35, 35, 35), new Color(10, 10, 10)}
                        : new Color[]{new Color(255, 255, 255), new Color(230, 230, 230), new Color(200, 200, 200)}
        );
        Paint old = g2.getPaint();
        g2.setPaint(bodyPaint);
        g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
        g2.setPaint(old);

        g2.setColor(isBlack ? new Color(230, 230, 230) : new Color(25, 25, 25));
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);

        String label = String.valueOf(id + 1);
        g2.setFont(getFont().deriveFont(Font.BOLD, 11f));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(isBlack ? Color.BLACK : Color.WHITE);
        g2.drawString(label, cx - fm.stringWidth(label) / 2 + 1, cy + fm.getAscent() / 2 - 2 + 1);
        g2.setColor(isBlack ? Color.WHITE : Color.BLACK);
        g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + fm.getAscent() / 2 - 2);
    }
}