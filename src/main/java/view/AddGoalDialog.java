package view;

import java.awt.Component;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import entity.Category;
import interface_adapter.set_goal.SetGoalController;

public class AddGoalDialog {
    private static final int VERTICAL_SPACING = 10;

    /**
     * Displays a modal dialog allowing the user to set a new goal.
     * Parses the input values and calls the controller to set the goal.
     *
     * @param parent the parent component to attach the dialog to
     * @param controller the SetGoalController to handle the submitted goal
     */

    public static void show(Component parent, SetGoalController controller) {
        final JTextField monthField = new JTextField(10);
        monthField.setText(YearMonth.now().toString());
        final JTextField amountField = new JTextField(10);
        final JTextField categoriesField = new JTextField(10);

        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Month (YYYY-MM):"));
        panel.add(monthField);
        panel.add(Box.createVerticalStrut(VERTICAL_SPACING));
        panel.add(new JLabel("Goal Amount ($):"));
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(VERTICAL_SPACING));
        panel.add(new JLabel("Categories (comma separated):"));
        panel.add(categoriesField);

        final int result = JOptionPane.showConfirmDialog(parent, panel,
                "Plant a New Goal Tree", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                final YearMonth month = YearMonth.parse(monthField.getText());
                final float amount = Float.parseFloat(amountField.getText());
                final String[] catNames = categoriesField.getText().split(",");
                final List<Category> categories = new ArrayList<>();
                for (String name : catNames) {
                    if (!name.trim().isEmpty()) {
                        categories.add(new Category(name.trim()));
                    }
                }
                controller.setGoal(month, amount, categories);
            }
            catch (DateTimeParseException error) {
                JOptionPane.showMessageDialog(parent, "Invalid Date. Use YYYY-MM.");
            }
            catch (NumberFormatException error) {
                JOptionPane.showMessageDialog(parent, "Invalid Goal Amount. Enter a valid number.");
            }
        }
    }
}
