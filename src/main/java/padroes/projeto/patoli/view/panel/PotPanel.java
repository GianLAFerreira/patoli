package padroes.projeto.patoli.view.panel;

import padroes.projeto.patoli.controller.GameController;

import javax.swing.*;
import java.awt.*;

/**
 * Painel visual do "Pote" de moedas.
 * Visual responsivo com cartão, título, contagem grande e "grade" de moedas.
 */
public class PotPanel extends JPanel {
    private final GameController controller;

    public PotPanel(GameController controller) {
        this.controller = controller;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        // Altura preferida generosa para não sofrer corte em layouts estreitos
        setPreferredSize(new Dimension(300, 160));
        setMinimumSize(new Dimension(220, 140));
    }

    public void refresh() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int pot = controller.getPot();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dimensões
        int w = getWidth();
        int h = getHeight();
        int arc = 18;

        // Cartão de fundo
        GradientPaint card = new GradientPaint(0, 0, new Color(36, 39, 46, 240), 0, h, new Color(28, 30, 36, 240));
        Paint old = g2.getPaint();
        g2.setPaint(card);
        g2.fillRoundRect(4, 4, w - 8, h - 8, arc, arc);
        g2.setPaint(old);

        // Borda + brilho
        g2.setColor(new Color(0, 0, 0, 100));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(4, 4, w - 8, h - 8, arc, arc);
        g2.setColor(new Color(255, 255, 255, 28));
        g2.drawRoundRect(6, 6, w - 12, h - 12, arc - 2, arc - 2);

        // Título
        int padding = 14;
        int contentX = 4 + padding;
        int contentY = 4 + padding;

        g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
        g2.setColor(new Color(235, 235, 235));
        g2.drawString("Pote de Moedas", contentX, contentY + 2);

        // Linha divisória
        int dividerY = contentY + 10;
        g2.setColor(new Color(70, 70, 70));
        g2.drawLine(contentX, dividerY, w - padding - 4, dividerY);

        // Área de conteúdo abaixo do título
        int areaTop = dividerY + 10;
        int areaBottom = h - padding - 4;
        int areaHeight = Math.max(40, areaBottom - areaTop);

        // Coluna esquerda: "jarro" estilizado
        int jarAreaW = Math.max(90, (int)(w * 0.28));
        int jarX = contentX;
        int jarY = areaTop + 6;
        int jarW = jarAreaW - 18;
        int jarH = areaHeight - 12;

        // Jarro (corpo)
        g2.setColor(new Color(110, 85, 55));
        g2.fillRoundRect(jarX + 10, jarY + 14, jarW, jarH - 14, 16, 16);
        g2.setColor(new Color(80, 60, 40));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(jarX + 10, jarY + 14, jarW, jarH - 14, 16, 16);

        // Gargalo
        g2.setColor(new Color(120, 95, 65));
        g2.fillRoundRect(jarX + 24, jarY, Math.max(30, jarW - 28), 18, 12, 12);
        g2.setColor(new Color(80, 60, 40));
        g2.drawRoundRect(jarX + 24, jarY, Math.max(30, jarW - 28), 18, 12, 12);

        // Coluna direita: contagem grande + grade de moedas
        int rightX = contentX + jarAreaW + 8;
        int rightW = w - rightX - padding - 4;
        int lineY = jarY + 8;

        // Contagem grande
        String count = String.valueOf(pot);
        g2.setFont(getFont().deriveFont(Font.BOLD, 28f));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(new Color(255, 215, 0));
        g2.drawString(count + " moedas", rightX, lineY + fm.getAscent());

        // Subtexto
        g2.setFont(getFont().deriveFont(Font.PLAIN, 12f));
        g2.setColor(new Color(210, 210, 210));
        g2.drawString("Penalidades alimentam o pote.", rightX, lineY + fm.getAscent() + 18);
        g2.drawString("Vencedor leva tudo no final!", rightX, lineY + fm.getAscent() + 34);

        // Grade de moedas (visual)
        int gridTop = lineY + fm.getAscent() + 48;
        int gridHeight = areaBottom - gridTop;
        int coinSize = Math.max(12, Math.min(18, gridHeight / 3)); // 2-3 linhas
        int gap = Math.max(4, coinSize / 3);
        int perRow = Math.max(3, (rightW) / (coinSize + gap));
        int maxCoins = Math.max(perRow * 2, 10); // mostrar de 2 a 3 linhas ou no mínimo 10

        int drawn = Math.min(pot, maxCoins);
        int cx = rightX;
        int cy = gridTop;

        for (int i = 0; i < drawn; i++) {
            drawCoin(g2, cx, cy, coinSize);
            cx += coinSize + gap;
            if ((i + 1) % perRow == 0) {
                cx = rightX;
                cy += coinSize + gap;
                if (cy + coinSize > areaBottom) break; // não vazar a área
            }
        }

        // Sufixo "+N" se tiver mais moedas no pote do que cabem na grade
        if (pot > drawn) {
            String more = "+" + (pot - drawn);
            g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
            g2.setColor(new Color(235, 235, 235));
            g2.drawString(more, cx, Math.min(cy + coinSize, areaBottom));
        }

        g2.dispose();
    }

    private void drawCoin(Graphics2D g2, int x, int y, int size) {
        // sombra
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillOval(x + 2, y + 2, size, size);
        // moeda
        RadialGradientPaint paint = new RadialGradientPaint(
                new Point(x + size / 3, y + size / 3), size,
                new float[]{0f, 0.7f, 1f},
                new Color[]{new Color(255, 245, 200), new Color(255, 215, 0), new Color(200, 160, 0)}
        );
        Paint old = g2.getPaint();
        g2.setPaint(paint);
        g2.fillOval(x, y, size, size);
        g2.setPaint(old);
        // aro
        g2.setColor(new Color(120, 90, 0));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x, y, size, size);
        // brilho sutil
        g2.setColor(new Color(255, 255, 255, 90));
        int d = Math.max(4, size / 3);
        g2.fillOval(x + size / 6, y + size / 6, d, d);
    }
}