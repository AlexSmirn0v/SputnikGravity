import java.util.Random;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Main extends JFrame {
    private JFormattedTextField[] fields = new JFormattedTextField[6];
    private JLabel outLabel = new JLabel("<html>Введите значения коэффициентов<br>Здесь будет формула и тип</html>");
    private DrawGraph graph;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    private class SubmitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double[] params = new double[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    Object value = fields[i].getValue();
                    params[i] = ((Number) value).doubleValue();
                }

                Logic.CurveInstance curve = Logic.findCurve(params);
                outLabel.setText(
                        "<html>Формула: " + curve.getFormulaString() + "<br>Тип: " + curve.getTypeString() + "</html>");
                graph.setFunc(curve.getYFunction());

                JOptionPane.showMessageDialog(Main.this,
                        "Успешно считаны параметры\nТип кривой: " + curve.getTypeString(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Main.this,
                        "Некорректный ввод",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JFormattedTextField createDoubleField() {
        DecimalFormat doubleFormat = (DecimalFormat) NumberFormat.getNumberInstance();
        doubleFormat.setGroupingUsed(false);
        doubleFormat.setMinimumFractionDigits(0);
        doubleFormat.setMaximumFractionDigits(10);
        NumberFormatter doubleFormatter = new NumberFormatter(doubleFormat);
        doubleFormatter.setValueClass(Double.class);

        JFormattedTextField a = new JFormattedTextField(doubleFormatter);

        a.setValue(0.0);
        a.setColumns(5);

        return a;
    }

    private JPanel createInputPanel() {
        final String[] textLabels = {
                "x<sup>2</sup> +",
                "xy + ",
                "y<sup>2</sup> + ",
                "x + ",
                "y + ",
                "= 0"
        };

        JPanel out = new JPanel();
        out.setLayout(new FlowLayout());
        JLabel[] labels = new JLabel[6];

        for (int i = 0; i < 6; i++) {
            fields[i] = createDoubleField();
            out.add(fields[i]);
            labels[i] = new JLabel("<html>" + textLabels[i] + "</html>");
            out.add(labels[i]);
        }
        return out;
    }

    private Component[] createComponents() {
        JPanel panel = createInputPanel();

        JButton button = new JButton("Получить значения");
        button.setSize(220, 50);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(new SubmitListener());

        outLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSlider slider = new JSlider(1, 100, 10);
        JLabel sliderLabel = new JLabel("Масштаб: " + slider.getValue());
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        slider.setOrientation(SwingConstants.HORIZONTAL);
        slider.setPaintLabels(true);
        slider.addChangeListener(event -> {
            sliderLabel.setText("Масштаб: " + slider.getValue());
            graph.setScale(slider.getValue());
        });

        double[] scores = new double[16];
        Random random = new Random();
        int maxScore = 20;
        for (int i = 0; i < scores.length; i++) {
            scores[i] = random.nextInt(maxScore);
        }
        graph = new DrawGraph(x -> new double[] { x }, slider.getValue());

        return new Component[] { panel, button, outLabel, sliderLabel, slider, graph };
    }

    public Main() {
        super("Расчет кривой второго порядка");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        for (Component comp : createComponents())
            add(comp);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
