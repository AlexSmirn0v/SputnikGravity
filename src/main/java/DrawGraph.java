import java.awt.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DrawGraph extends JPanel {
   private static final int PREF_W = 800;
   private static final int PREF_H = 650;
   private static final int BORDER_GAP = 30;
   private static final Color GRAPH_COLOR = new Color(22, 159, 72);
   private static final Color GRAPH_POINT_COLOR = new Color(219, 4, 4);
   private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
   private static final int GRAPH_POINT_WIDTH = 5;
   private static final int MAX_SCALE = 200;
   private static int SCALE;

   private Function<Double, double[]> func;

   public DrawGraph(Function<Double, double[]> func, int scale) {
      this.func = func;
      SCALE = scale;
   }

   public void setScale(int scale) {
      SCALE = scale;
      repaint();
   }

   public void setFunc(Function<Double, double[]> func) {
      this.func = func;
      repaint();
   }

   @Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      double xScale = ((double) getWidth() - 2 * BORDER_GAP) / 2 * SCALE / MAX_SCALE;
      double yScale = ((double) getHeight() - 2 * BORDER_GAP) / 2 * SCALE / MAX_SCALE;

      List<Point> graphPoints = new ArrayList<>();
      for (double i = -Math.floor((getHeight() - 2 * BORDER_GAP) / yScale / 2) - 1; i < Math.floor((getHeight() - 2 * BORDER_GAP) / yScale
            / 2 + 1); i += 0.25) {
         int x1 = (int) (-i * xScale + getWidth() / 2);
         double[] xPoints = this.func.apply(i);
         if (xPoints.length == 0) continue;
         for (double p : xPoints) {
            int y1 = (int) (p * yScale + getHeight() / 2);
            graphPoints.add(new Point(x1, y1));
         }
      }

      // Отрисовка осей
      g2.drawLine(getWidth() / 2, getHeight() - BORDER_GAP, getWidth() / 2, BORDER_GAP);
      g2.drawLine(BORDER_GAP, getHeight() / 2, getWidth() - BORDER_GAP, getHeight() / 2);

      // Отрисовка отметок на OY
      for (int i = -(int) ((getHeight() - 2 * BORDER_GAP) / yScale / 2); i < ((getHeight() - 2 * BORDER_GAP) / yScale
            / 2); i++) {
         int x0 = (getWidth() - GRAPH_POINT_WIDTH) / 2;
         int x1 = (getWidth() + GRAPH_POINT_WIDTH) / 2;
         int y0 = getHeight() / 2 - (int) (i * yScale);
         int y1 = y0;
         g2.drawLine(x0, y0, x1, y1);
      }

      // Отрисовка отметок на OX
      for (int i = -(int) ((getWidth() - 2 * BORDER_GAP) / yScale / 2); i < ((getWidth() - 2 * BORDER_GAP) / yScale
            / 2); i++) {
         int x0 = getWidth() / 2 + (int) (i * xScale);
         int x1 = x0;
         int y0 = (getHeight() - GRAPH_POINT_WIDTH) / 2;
         int y1 = (getHeight() + GRAPH_POINT_WIDTH) / 2;
         g2.drawLine(x0, y0, x1, y1);
      }

      Stroke oldStroke = g2.getStroke();
      g2.setColor(GRAPH_COLOR);
      g2.setStroke(GRAPH_STROKE);
      for (int i = 0; i < graphPoints.size() - 1; i++) {
         int x1 = graphPoints.get(i).x;
         int y1 = graphPoints.get(i).y;
         int x2 = graphPoints.get(i + 1).x;
         int y2 = graphPoints.get(i + 1).y;
         g2.drawLine(x1, y1, x2, y2);
      }

      g2.setStroke(oldStroke);
      g2.setColor(GRAPH_POINT_COLOR);
      for (int i = 0; i < graphPoints.size(); i++) {
         int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
         int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;
         ;
         int ovalW = GRAPH_POINT_WIDTH;
         int ovalH = GRAPH_POINT_WIDTH;
         g2.fillOval(x, y, ovalW, ovalH);
      }
   }

   @Override
   public Dimension getPreferredSize() {
      return new Dimension(PREF_W, PREF_H);
   }
}