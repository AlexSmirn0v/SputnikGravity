import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    ComponentCreator creator = new ComponentCreator();
    HyperbolaAnimationPanel graph;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    private class SubmitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JFormattedTextField[] fields = creator.getFields();
                double[] params = new double[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    Object value = fields[i].getValue();
                    params[i] = ((Number) value).doubleValue();
                }
                graph.setVisible(true);
                graph.setValues(params[3]*1e22, params[4] * 1e3, params[1] * 1e3, params[2], params[0] * 1e3);

                JOptionPane.showMessageDialog(Main.this,
                        "Успешно просчитана анимация",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Main.this,
                        "Некорректный ввод",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Component[] createComponents() {
        JPanel panel = creator.createInputPanel(new SubmitListener());

        JSlider sliderTime = new JSlider(1, 100, 12);
        JLabel sliderTimeLabel = new JLabel("Время протекания анимации: " + sliderTime.getValue());
        sliderTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sliderTime.setOrientation(SwingConstants.HORIZONTAL);
        sliderTime.setPaintLabels(true);
        sliderTime.addChangeListener(event -> {
            sliderTimeLabel.setText("Время протекания анимации: " + sliderTime.getValue());
            graph.setTimeScale(sliderTime.getValue());
        });

        graph = new HyperbolaAnimationPanel();

        return new Component[] { panel, sliderTimeLabel, sliderTime, graph };
    }

    public Main() {
        super("Расчет траектории спутника");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        for (Component comp : createComponents())
            add(comp);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
