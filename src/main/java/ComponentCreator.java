import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

public class ComponentCreator {
    private JFormattedTextField[] fields = new JFormattedTextField[5];

    public JPanel createInputPanel(ActionListener onSubmit) {
        JPanel out = new JPanel();
        out.setLayout(new FlowLayout());
        out.add(createLeftPanel(onSubmit));
        out.add(createRightPanel());
        return out;
    }

    private JPanel createLeftPanel(ActionListener onSubmit) {
        JPanel out = new JPanel();
        out.setLayout(new BoxLayout(out, BoxLayout.Y_AXIS));

        final String[] textLabels = {
                "Прицельная дальность <i>b</i>, км",
                "Скорость космического аппарата в гелиоцентрической системе <i>v<sub>1</sub></i>, км/с",
                "Угол между векторами скоростей планеты и космического аппарата <i>α</i>, <sup>o</sup>",
        };

        addInputFields(out, textLabels, 0);
        JButton button = new JButton("Рассчитать траекторию");
        button.setSize(220, 50);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(onSubmit);
        out.add(Box.createVerticalGlue());
        out.add(button);
        return out;
    }

    private JPanel createRightPanel() {
        JPanel out = new JPanel();
        out.setLayout(new BoxLayout(out, BoxLayout.Y_AXIS));

        final String[] textLabels = {
                "Масса планеты <i>M</i>, ×10<sup>22</sup> кг",
                "Скорость планеты в гелиоцентрической системе <i>v<sub>p</sub></i>, км/с",
        };

        addInputFields(out, textLabels, 3);
        JButton button = new JButton("Случайные значения");
        button.setSize(220, 50);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(randomizer);
        out.add(Box.createVerticalGlue());
        out.add(button);
        return out;
    }

    private ActionListener randomizer = e -> {
        Random random = new Random();
        for (JFormattedTextField field : Arrays.copyOfRange(fields, 3, 5)) {
            double value = 200 * random.nextDouble();
            field.setValue(value);
        }
    };

    private void addInputFields(JPanel panel, String[] textLabels, int startIndex) {
        for (int i = 0; i < textLabels.length; i++) {
            JFormattedTextField field = createDoubleField();
            int index = startIndex + i;
            fields[index] = field;
            JLabel label = new JLabel("<html>" + textLabels[i] + "</html>", SwingConstants.LEFT);
            label.setLabelFor(field);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            field.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(label);
            panel.add(field);
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

    public JFormattedTextField[] getFields() {
        return fields;
    }
}
