package padroes.projeto.patoli.view.panel;

import padroes.projeto.patoli.controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

/**
 * Painel visual do "Pote" de moedas.
 */
public class PotPanel extends JPanel {
    private final GameController controller;

    public PotPanel(GameController controller) {
        this.controller = controller;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setPreferredSize(new Dimension(300, 180));
        setMinimumSize(new Dimension(240, 160));
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

        int w = getWidth();
        int h = getHeight();
        int arc = 18;

        // Cartão de fundo
        GradientPaint card = new GradientPaint(0, 0, new Color(36, 39, 46, 240), 0, h, new Color(28, 30, 36, 240));
        Paint old = g2.getPaint();
        g2.setPaint(card);
        g2.fillRoundRect(4, 4, w - 8, h - 8, arc, arc);
        g2.setPaint(old);
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

        // Linha
        int dividerY = contentY + 10;
        g2.setColor(new Color(70, 70, 70));
        g2.drawLine(contentX, dividerY, w - padding - 4, dividerY);

        // Área principal
        int areaTop = dividerY + 10;
        int areaBottom = h - padding - 6;
        int areaHeight = Math.max(40, areaBottom - areaTop);
        int areaLeft = contentX;
        int areaRight = w - padding - 6;
        int areaWidth = Math.max(100, areaRight - areaLeft);

        // Layout: à esquerda jarro, à direita contador
        int jarAreaW = Math.max(110, (int)(areaWidth * 0.45));
        int infoAreaW = areaWidth - jarAreaW - 10;

        // Dimensão jarro
        int jarX = areaLeft;
        int jarY = areaTop + 2;
        int jarW = jarAreaW;
        int jarH = areaHeight - 4;

        // Desenhar jarro e obter área de clipping interna
        Shape jarClip = drawJarAndGetInnerClip(g2, jarX, jarY, jarW, jarH);

        // Desenhar moedas DENTRO do jarro (clipping)
        Shape oldClip = g2.getClip();
        g2.setClip(jarClip);
        drawCoinsInsideJar(g2, jarX, jarY, jarW, jarH, pot);
        g2.setClip(oldClip);

        // Contagem/descrição
        int infoX = jarX + jarW + 10;
        int lineY = jarY + 6;
        g2.setFont(getFont().deriveFont(Font.BOLD, 28f));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(new Color(255, 215, 0));
        g2.drawString(pot + " moedas", infoX, lineY + fm.getAscent());

        g2.setFont(getFont().deriveFont(Font.PLAIN, 12f));
        g2.setColor(new Color(210, 210, 210));
        g2.drawString("Penalidades vão para o pote.", infoX, lineY + fm.getAscent() + 18);
        g2.drawString("Vencedor leva tudo no final!", infoX, lineY + fm.getAscent() + 34);

        g2.dispose();
    }

    /**
     * Desenha o jarro (gargalo + corpo) e retorna uma área interna para clipping das moedas.
     */
    private Shape drawJarAndGetInnerClip(Graphics2D g2, int x, int y, int w, int h) {
        // Gargalo
        int neckW = Math.max(40, (int)(w * 0.5));
        int neckH = Math.min(20, Math.max(14, h / 8));
        int neckX = x + (w - neckW) / 2;
        int neckY = y;

        g2.setColor(new Color(120, 95, 65));
        g2.fillRoundRect(neckX, neckY, neckW, neckH, 12, 12);
        g2.setColor(new Color(80, 60, 40));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(neckX, neckY, neckW, neckH, 12, 12);

        // Corpo do jarro
        int bodyX = x + (int)(w * 0.08);
        int bodyY = neckY + neckH - 2;
        int bodyW = w - (int)(w * 0.16);
        int bodyH = h - neckH - 2;

        // Corpo externo
        g2.setColor(new Color(110, 85, 55));
        g2.fillRoundRect(bodyX, bodyY, bodyW, bodyH, 18, 18);
        g2.setColor(new Color(80, 60, 40));
        g2.drawRoundRect(bodyX, bodyY, bodyW, bodyH, 18, 18);

        // Brilho no jarro
        GradientPaint gloss = new GradientPaint(bodyX, bodyY, new Color(255, 255, 255, 26),
                bodyX, bodyY + bodyH, new Color(255, 255, 255, 6));
        Paint old = g2.getPaint();
        g2.setPaint(gloss);
        g2.fillRoundRect(bodyX + 2, bodyY + 2, bodyW - 4, bodyH - 4, 16, 16);
        g2.setPaint(old);

        // Área interna para clipping (um pouco menor que o corpo)
        int innerPad = Math.max(6, w / 20);
        RoundRectangle2D inner = new RoundRectangle2D.Float(
                bodyX + innerPad, bodyY + innerPad,
                bodyW - innerPad * 2, bodyH - innerPad * 2,
                14, 14
        );

        return inner;
    }

    private void drawCoinsInsideJar(Graphics2D g2, int jarX, int jarY, int jarW, int jarH, int pot) {
        // Área interna estimada (menos padding)
        int innerPad = Math.max(6, jarW / 20);
        int ix = jarX + (int)(jarW * 0.08) + innerPad;
        int iy = jarY + (int)(jarH * 0.22) + innerPad;
        int iw = jarW - (int)(jarW * 0.16) - innerPad * 2;
        int ih = jarH - (int)(jarH * 0.22) - innerPad * 2;

        if (iw <= 0 || ih <= 0) return;

        // Dimensionamento de moedas baseado na altura
        int coinSize = Math.max(10, Math.min(18, ih / 5));
        int gap = Math.max(3, coinSize / 4);

        // Layout em colunas compactas com jitter
        int perRow = Math.max(3, iw / (coinSize + gap));
        int rows = Math.max(2, ih / (coinSize + gap));

        int maxCoins = perRow * rows;
        int coinsToDraw = Math.min(pot, maxCoins);

        Random rnd = new Random(pot * 31L + iw * 7L + ih); // determinístico p/ frame

        int drawn = 0;
        int y = iy + ih - coinSize; // preenche de baixo para cima
        for (int r = 0; r < rows && drawn < coinsToDraw; r++) {
            int x = ix;
            for (int c = 0; c < perRow && drawn < coinsToDraw; c++) {
                int jitterX = rnd.nextInt(Math.max(1, gap)) - gap / 2;
                int jitterY = rnd.nextInt(Math.max(1, gap)) - gap / 2;
                drawCoin(g2, x + jitterX, y + jitterY, coinSize);
                x += coinSize + gap;
                drawn++;
            }
            y -= coinSize + gap;
        }

        if (pot > drawn) {
            String more = "+" + (pot - drawn);
            g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
            g2.setColor(new Color(235, 235, 235));
            g2.drawString(more, ix + iw - 28, iy + 16);
        }
    }

    private void drawCoin(Graphics2D g2, int x, int y, int size) {
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillOval(x + 2, y + 2, size, size);

        RadialGradientPaint paint = new RadialGradientPaint(
                new Point(x + size / 3, y + size / 3), size,
                new float[]{0f, 0.7f, 1f},
                new Color[]{new Color(255, 245, 200), new Color(255, 215, 0), new Color(200, 160, 0)}
        );
        Paint old = g2.getPaint();
        g2.setPaint(paint);
        g2.fillOval(x, y, size, size);
        g2.setPaint(old);

        g2.setColor(new Color(120, 90, 0));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x, y, size, size);

        g2.setColor(new Color(255, 255, 255, 90));
        int d = Math.max(4, size / 3);
        g2.fillOval(x + size / 6, y + size / 6, d, d);
    }
}