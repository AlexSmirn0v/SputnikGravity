import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class HyperbolaAnimationPanel extends JPanel {
    private static final double G = 6.67430e-11;

    private static final int PREF_W = 900;
    private static final int PREF_H = 700;

    private static final Color BACKGROUND = new Color(8, 8, 14);
    private static final Color AXIS_COLOR = new Color(140, 140, 140);
    private static final Color TRAJECTORY_COLOR = new Color(40, 200, 120);
    private static final Color TEXT_COLOR = Color.WHITE;

    private final Timer timer;
    private long startTime = System.currentTimeMillis();

    private double periodMs = 12000.0;
    private double border = 40;

    private double planetMass; // кг
    private double planetSpeed; // м/с
    private double spacecraftSpeed; // м/с
    private double angleDeg; // градусы
    private double b; // м

    private double mu;
    private double vInf;
    private double aLen;
    private double e;
    private double p;
    private double betaRad;
    private double kTime;
    private double tMax;
    private double deltaV;
    private double hMax = 3.2;

    private BufferedImage satelliteImage;
    private Color planetColor = new Color(70, 130, 255);
    private Random randomizer = new Random();

    public HyperbolaAnimationPanel() {
        setBackground(BACKGROUND);
        loadSatelliteImage();

        timer = new Timer(16, e -> repaint());
        timer.start();
    }

    public void setValues(double planetMass,
            double planetSpeed,
            double spacecraftSpeed,
            double angleDeg,
            double b) {
        this.planetMass = planetMass;
        this.planetSpeed = planetSpeed;
        this.spacecraftSpeed = spacecraftSpeed;
        this.angleDeg = angleDeg;
        this.b = b;
        this.planetColor = new Color((int) (255.0 * randomizer.nextDouble()), (int) (255.0 * randomizer.nextDouble()),
                (int) (255.0 * randomizer.nextDouble()));
        recalc();
        repaint();
    }

    public void setTimeScale(int periodS) {
        this.periodMs = periodS * 1000.0;
    }

    private void recalc() {
        mu = G * planetMass;

        double lambda = Math.toRadians(angleDeg);
        vInf = Math.sqrt(
                planetSpeed * planetSpeed
                        + spacecraftSpeed * spacecraftSpeed
                        - 2.0 * planetSpeed * spacecraftSpeed * Math.cos(lambda));

        if (vInf <= 0) {
            vInf = 1e-9;
        }

        // Модуль большой полуоси
        aLen = mu / (vInf * vInf);

        // Экcцентриситет
        e = Math.sqrt(1.0 + Math.pow(b * vInf * vInf / mu, 2));

        // Фокальный параметр
        p = aLen * (e * e - 1.0);

        // Угол отклонения
        betaRad = 2.0 * Math.atan(mu / (b * vInf * vInf));

        // Модуль приращения скорости
        deltaV = 2 * vInf * Math.sin(betaRad / 2);

        // Время от гиперболической аномалии
        kTime = Math.sqrt(aLen * aLen * aLen / mu);
        tMax = kTime * (e * Math.sinh(hMax) - hMax);
    }

    private Point2D.Double orbitPoint(double H) {
        double x = aLen * (e - Math.cosh(H));
        double y = aLen * Math.sqrt(e * e - 1.0) * Math.sinh(H);
        return new Point2D.Double(x, y);
    }

    private Point2D.Double orbitVelocity(double H) {
        double denom = e * Math.cosh(H) - 1.0;

        double vx = -Math.sqrt(mu / aLen) * Math.sinh(H) / denom;
        double vy = Math.sqrt(mu / aLen) * Math.sqrt(e * e - 1.0) * Math.cosh(H) / denom;

        return new Point2D.Double(vx, vy);
    }

    private double solveHFromTime(double t) {
        double lo = -hMax;
        double hi = hMax;

        for (int i = 0; i < 70; i++) {
            double mid = 0.5 * (lo + hi);
            double fMid = kTime * (e * Math.sinh(mid) - mid) - t;

            if (fMid > 0) {
                hi = mid;
            } else {
                lo = mid;
            }
        }

        return 0.5 * (lo + hi);
    }

    private double currentPhase() {
        long now = System.currentTimeMillis();
        double elapsed = (now - startTime) % (long) periodMs;
        return elapsed / periodMs;
    }

    private void loadSatelliteImage() {
        try {
            satelliteImage = ImageIO.read(new File("src/main/resources/satellite.png"));
        } catch (IOException ex) {
            satelliteImage = null;
        }
    }

    private int worldToScreenX(double x, double scale) {
        return (int) Math.round(getWidth() / 2.0 + x * scale);
    }

    private int worldToScreenY(double y, double scale) {
        return (int) Math.round(getHeight() / 2.0 - y * scale);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mu <= 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Point2D.Double edge = orbitPoint(hMax);
        double maxX = Math.max(1.0, Math.abs(edge.x));
        double maxY = Math.max(1.0, Math.abs(edge.y));

        double scaleX = (w - 2.0 * border) / (2.2 * maxX);
        double scaleY = (h - 2.0 * border) / (2.2 * maxY);
        double scale = Math.min(scaleX, scaleY);

        // Оси
        g2.setColor(AXIS_COLOR);
        g2.drawLine(0, h / 2, w, h / 2);
        g2.drawLine(w / 2, 0, w / 2, h);

        // Деления на осях
        for (int i = -10; i <= 10; i++) {
            int x = worldToScreenX(i * maxX / 10.0, scale);
            g2.drawLine(x, h / 2 - 5, x, h / 2 + 5);
        }
        for (int i = -10; i <= 10; i++) {
            int y = worldToScreenY(i * maxY / 10.0, scale);
            g2.drawLine(w / 2 - 5, y, w / 2 + 5, y);
        }

        // Траектория
        Path2D path = new Path2D.Double();
        boolean first = true;
        for (int i = 0; i <= 500; i++) {
            double H = -hMax + 2.0 * hMax * i / 500.0;
            Point2D.Double pt = orbitPoint(H);
            int sx = worldToScreenX(pt.x, scale);
            int sy = worldToScreenY(pt.y, scale);

            if (first) {
                path.moveTo(sx, sy);
                first = false;
            } else {
                path.lineTo(sx, sy);
            }
        }

        g2.setColor(TRAJECTORY_COLOR);
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(path);

        // Планета в точке пересечения осей
        int planetRadius = 14;
        int planetX = w / 2 - planetRadius;
        int planetY = h / 2 - planetRadius;
        g2.setColor(planetColor);
        g2.fillOval(planetX, planetY, planetRadius * 2, planetRadius * 2);

        g2.setColor(Color.WHITE);
        g2.drawOval(planetX, planetY, planetRadius * 2, planetRadius * 2);

        // Текущая позиция спутника
        double phase = currentPhase();
        double t = -tMax + 2.0 * tMax * phase;
        double H = solveHFromTime(t);
        Point2D.Double sat = orbitPoint(H);
        @SuppressWarnings("unused")
        Point2D.Double vel = orbitVelocity(H);

        int satX = worldToScreenX(sat.x, scale);
        int satY = worldToScreenY(sat.y, scale);

        int satW = 34;
        int satH = 34;

        if (satelliteImage != null) {
            g2.drawImage(satelliteImage, satX - satW / 2, satY - satH / 2, satW, satH, null);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillOval(satX - satW / 2, satY - satH / 2, satW, satH);
            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(satX - satW / 2, satY - satH / 2, satW, satH);
        }

        // Подпись
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        String s1 = String.format("V∞ = %.3f km/s", vInf / 1000.0);
        String s2 = String.format("∆V = %.3f km/s", deltaV / 1000.0);
        String s3 = String.format("e = %.3f", e);
        String s4 = String.format("β = %.2f°", Math.toDegrees(betaRad));
        String s5 = String.format("p = %.3e m", p);

        g2.drawString(s1, 20, 24);
        g2.drawString(s2, 20, 44);
        g2.drawString(s3, 20, 64);
        g2.drawString(s4, 20, 84);
        g2.drawString(s5, 20, 104);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Гравитационный манёвр");
            HyperbolaAnimationPanel panel = new HyperbolaAnimationPanel();

            // Пример: Юпитер, как в вашей статье
            // Масса, скорости и b — в СИ
            panel.setValues(
                    1.898e27, // масса Юпитера, кг
                    13.07e3, // скорость планеты, м/с
                    15.2e3, // скорость КА, м/с
                    180.0, // угол между векторами, градусы
                    8.230e7 // прицельная дальность, м
            );

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}