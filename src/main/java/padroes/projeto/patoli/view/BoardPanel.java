package padroes.projeto.patoli.view;

import padroes.projeto.patoli.controller.GameController;
import padroes.projeto.patoli.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class BoardPanel extends JPanel {
    private final GameController controller;
    private final Game game;

    private int cellSize = 56;
    private int margin = 20;

    public BoardPanel(GameController controller, Game game) {
        this.controller = controller;
        this.game = game;

        setBackground(new Color(30, 30, 30));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick(e.getX(), e.getY());
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        Board b = game.getBoard();
        return new Dimension(b.getCols() * cellSize + margin * 2, b.getRows() * cellSize + margin * 2);
    }

    private void onClick(int x, int y) {
        if (game.isGameOver()) return;

        Board b = game.getBoard();
        int col = (x - margin) / cellSize;
        int row = (y - margin) / cellSize;
        if (col < 0 || row < 0 || col >= b.getCols() || row >= b.getRows()) return;

        // Detectar peça do jogador atual na célula da trilha clicada
        for (Cell cell : b.getTrack()) {
            if (cell.getRow() == row && cell.getCol() == col) {
                Piece occ = cell.getOccupant();
                if (occ != null && occ.getOwner() == game.getCurrent()) {
                    controller.onPieceClicked(occ);
                    return;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Board b = game.getBoard();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fundo com gradiente e vinheta suave
        drawBackground(g2);

        // Grade sutil
        drawGrid(g2, b.getRows(), b.getCols());

        // Sombra geral do tabuleiro (leve)
        drawBoardShadow(g2, b.getRows(), b.getCols());

        // Moldura do tabuleiro (moldura "de madeira")
        drawBoardFrame(g2, b.getRows(), b.getCols());

        // Desenhar trilha com estilo
        for (Cell cell : b.getTrack()) {
            drawCell(g2, cell);
        }

        // Desenhar peças
        for (Player p : new Player[]{game.getBlack(), game.getWhite()}) {
            for (Piece pc : p.getPieces()) {
                if (pc.isOnBoard()) {
                    drawPiece(g2, pc);
                }
            }
        }

        g2.dispose();
    }



    private void drawGrid(Graphics2D g2, int rows, int cols) {
        // Grade suave para dar referência visual
        Color line = new Color(255, 255, 255, 18);
        g2.setColor(line);

        int w = cols * cellSize;
        int h = rows * cellSize;

        for (int r = 0; r <= rows; r++) {
            int y = margin + r * cellSize;
            g2.drawLine(margin, y, margin + w, y);
        }
        for (int c = 0; c <= cols; c++) {
            int x = margin + c * cellSize;
            g2.drawLine(x, margin, x, margin + h);
        }
    }

    private void drawBackground(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();

        // Gradiente diagonal
        GradientPaint gp = new GradientPaint(0, 0, new Color(25, 28, 34), w, h, new Color(18, 20, 24));
        Paint old = g2.getPaint();
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
        g2.setPaint(old);

        // Vinheta
        RadialGradientPaint vignette = new RadialGradientPaint(
                new Point(w / 2, h / 2),
                Math.max(w, h),
                new float[]{0.0f, 1.0f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 80)}
        );
        g2.setPaint(vignette);
        g2.fillRect(0, 0, w, h);
        g2.setPaint(null);
    }

    private void drawBoardShadow(Graphics2D g2, int rows, int cols) {
        int boardW = cols * cellSize;
        int boardH = rows * cellSize;
        int x = margin;
        int y = margin;

        // Sombra projetada
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(x + 8, y + 8, boardW, boardH, 26, 26);

        // Base discreta do tabuleiro
        g2.setColor(new Color(45, 48, 55, 90));
        g2.fillRoundRect(x, y, boardW, boardH, 20, 20);
        g2.setColor(new Color(70, 74, 82));
        g2.drawRoundRect(x, y, boardW, boardH, 20, 20);
    }

    // Moldura externa do tabuleiro
    private void drawBoardFrame(Graphics2D g2, int rows, int cols) {
        int boardW = cols * cellSize;
        int boardH = rows * cellSize;
        int x = margin;
        int y = margin;

        int arcOuter = Math.max(18, cellSize / 2);
        int arcInner = Math.max(12, arcOuter - 8);
        int thickness = Math.max(14, cellSize / 3); // espessura da moldura

        Shape outer = new RoundRectangle2D.Float(x - thickness, y - thickness, boardW + 2 * thickness, boardH + 2 * thickness, arcOuter + 8, arcOuter + 8);
        Shape inner = new RoundRectangle2D.Float(x, y, boardW, boardH, arcInner, arcInner);

        // Fundo da moldura com gradiente "madeira"
        GradientPaint wood = new GradientPaint(
                (int) ((x + boardW / 2.0) - 100), y - thickness,
                new Color(120, 78, 34),
                (int) ((x + boardW / 2.0) + 100), y + boardH + thickness,
                new Color(92, 62, 28)
        );
        Paint old = g2.getPaint();
        g2.setPaint(wood);
        g2.fill(outer);
        g2.setPaint(old);

        // "Recortar" o interior do tabuleiro para não cobrir as casas
        g2.setColor(new Color(45, 48, 55, 255));
        g2.fill(inner);

        // Veios da madeira (linhas leves diagonais)
        g2.setClip(outer);
        g2.setColor(new Color(180, 130, 80, 25));
        for (int d = -boardH - thickness; d < boardW + boardH + thickness; d += 10) {
            g2.drawLine(x - thickness + d, y - thickness, x - thickness + d - 60, y - thickness + 60);
        }
        g2.setClip(null);

        // Borda externa escura
        g2.setColor(new Color(45, 30, 15, 180));
        g2.setStroke(new BasicStroke(3f));
        g2.draw(outer);

        // Bisel interno claro
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(2f));
        g2.draw(inner);

        // Sombra interna junto ao tabuleiro
        g2.setColor(new Color(0, 0, 0, 70));
        g2.draw(new RoundRectangle2D.Float(x + 1, y + 1, boardW - 2, boardH - 2, arcInner - 4, arcInner - 4));
    }



    private void drawCell(Graphics2D g2, Cell cell) {
        int x = margin + cell.getCol() * cellSize;
        int y = margin + cell.getRow() * cellSize;

        // Base e paleta por tipo
        Color base = new Color(72, 78, 88); // normal
        if (cell.getType() == CellType.START) base = new Color(40, 130, 70);
        if (cell.getType() == CellType.ENDPOINT) base = new Color(170, 55, 55);
        if (cell.getType() == CellType.TRIANGLE_PENALTY) base = new Color(180, 145, 50);

        int arc = Math.max(10, cellSize / 4);
        Shape rect = new RoundRectangle2D.Float(x + 3, y + 3, cellSize - 6, cellSize - 6, arc, arc);

        // Sombra da célula
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fill(new RoundRectangle2D.Float(x + 5, y + 5, cellSize - 6, cellSize - 6, arc, arc));

        // Preenchimento com leve gradiente
        GradientPaint gp = new GradientPaint(x, y, base.brighter(), x + cellSize, y + cellSize, base.darker());
        Paint old = g2.getPaint();
        g2.setPaint(gp);
        g2.fill(rect);
        g2.setPaint(old);

        // Borda externa
        g2.setColor(new Color(25, 25, 25, 160));
        g2.setStroke(new BasicStroke(2f));
        g2.draw(rect);

        // Borda interna
        g2.setColor(new Color(255, 255, 255, 35));
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Float(x + 5, y + 5, cellSize - 10, cellSize - 10, arc - 4, arc - 4));

        // Padrão sutil no caminho (linhas diagonais claras)
        g2.setClip(rect);
        g2.setColor(new Color(255, 255, 255, 18));
        for (int d = -cellSize; d < cellSize; d += 6) {
            g2.drawLine(x + d, y, x + d + cellSize, y + cellSize);
        }
        g2.setClip(null);

        // Triângulo nas casas de punição (já tínhamos, mantendo)
        if (cell.getType() == CellType.TRIANGLE_PENALTY) {
            int padding = Math.max(6, cellSize / 6);
            int cx = x + cellSize / 2;
            int topY = y + padding + 4;
            int leftX = x + padding + 4;
            int rightX = x + cellSize - padding - 4;
            int bottomY = y + cellSize - padding - 4;

            int[] px = new int[]{cx, rightX, leftX};
            int[] py = new int[]{topY, bottomY, bottomY};

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 240, 130));
            g2.fillPolygon(px, py, 3);

            g2.setColor(new Color(120, 95, 30));
            g2.setStroke(new BasicStroke(2f));
            g2.drawPolygon(px, py, 3);
        }

        // Visual nas extremidades (bônus) — estrela, mantido
        if (cell.getType() == CellType.ENDPOINT) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cx = x + cellSize / 2;
            int cy = y + cellSize / 2;
            int outer = Math.max(6, (cellSize - 14) / 2);
            int inner = Math.max(3, outer / 2);

            Polygon star = new Polygon();
            for (int i = 0; i < 10; i++) {
                double angle = Math.toRadians(-90 + i * 36);
                int r = (i % 2 == 0) ? outer : inner;
                int sx = cx + (int) Math.round(r * Math.cos(angle));
                int sy = cy + (int) Math.round(r * Math.sin(angle));
                star.addPoint(sx, sy);
            }
            // Halo
            g2.setColor(new Color(255, 215, 0, 80));
            g2.fillOval(cx - outer - 3, cy - outer - 3, (outer + 3) * 2, (outer + 3) * 2);

            g2.setColor(new Color(255, 215, 0));
            g2.fillPolygon(star);

            g2.setColor(new Color(120, 80, 0));
            g2.setStroke(new BasicStroke(2f));
            g2.drawPolygon(star);
        }
    }


    private void drawPiece(Graphics2D g2, Piece pc) {
        Cell cell = game.getBoard().getTrack().get(pc.getPosition());
        int cx = margin + cell.getCol() * cellSize + cellSize / 2;
        int cy = margin + cell.getRow() * cellSize + cellSize / 2;

        int r = cellSize / 2 - 6;
        boolean isBlack = pc.getOwner().getColor() == PlayerColor.BLACK;
        boolean isCurrentPlayer = pc.getOwner() == game.getCurrent();

        // Anti-alias
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1) Sombra de contato (drop shadow)
        int shadowOffset = Math.max(2, cellSize / 12);
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillOval(cx - r + shadowOffset, cy - r + shadowOffset, 2 * r, 2 * r);

        // 2) Corpo com gradiente radial (efeito moeda/peça)
        float radius = r;
        RadialGradientPaint bodyPaint;
        if (isBlack) {
            bodyPaint = new RadialGradientPaint(
                    new Point(cx - r / 3, cy - r / 3), radius,
                    new float[]{0f, 0.65f, 1f},
                    new Color[]{
                            new Color(85, 85, 85),
                            new Color(35, 35, 35),
                            new Color(10, 10, 10)
                    }
            );
        } else {
            bodyPaint = new RadialGradientPaint(
                    new Point(cx - r / 3, cy - r / 3), radius,
                    new float[]{0f, 0.65f, 1f},
                    new Color[]{
                            new Color(255, 255, 255),
                            new Color(230, 230, 230),
                            new Color(200, 200, 200)
                    }
            );
        }
        Paint oldPaint = g2.getPaint();
        g2.setPaint(bodyPaint);
        g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);

        // 3) Aro externo
        g2.setPaint(oldPaint);
        g2.setStroke(new BasicStroke(2.5f));
        g2.setColor(isBlack ? new Color(240, 240, 240) : new Color(20, 20, 20));
        g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);

        // 4) Aro interno sutil
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(isBlack ? new Color(80, 80, 80) : new Color(180, 180, 180));
        int inner = r - 5;
        g2.drawOval(cx - inner, cy - inner, 2 * inner, 2 * inner);

        // 5) Brilho (gloss) no topo-esquerda
        int glossW = (int) (1.2 * r);
        int glossH = (int) (0.9 * r);
        g2.setColor(new Color(255, 255, 255, 90));
        g2.fillOval(cx - glossW / 2 - r / 4, cy - glossH / 2 - r / 3, glossW, glossH);

        // 6) Realce do jogador atual (halo suave)
        if (isCurrentPlayer) {
            int halo = r + 4;
            g2.setColor(new Color(255, 215, 0, 90)); // dourado translúcido
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(cx - halo, cy - halo, 2 * halo, 2 * halo);
        }

        // 7) ID da peça (número)
        String label = String.valueOf(pc.getId() + 1);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, Math.max(12f, cellSize / 3.2f)));
        FontMetrics fm = g2.getFontMetrics();

        // contorno para legibilidade
        g2.setColor(isBlack ? Color.BLACK : Color.WHITE);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                g2.drawString(label, cx - fm.stringWidth(label) / 2 + dx, cy + fm.getAscent() / 2 - 3 + dy);
            }
        }
        // texto principal
        g2.setColor(isBlack ? Color.WHITE : Color.BLACK);
        g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + fm.getAscent() / 2 - 3);
    }

}