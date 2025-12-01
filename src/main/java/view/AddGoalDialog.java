package view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import entity.Category;
import interface_adapter.set_goal.SetGoalController;

public class AddGoalDialog {
    private static final int VERTICAL_SPACING = 10;
    private static final int CATEGORY_LIST_VISIBLE_ROWS = 5;
    private static final String[] AVAILABLE_CATEGORIES = {
        "Food and Dining",
        "Transportation",
        "Shopping",
        "Rent and Utilities",
        "Entertainment",
    };

    /**
     * Displays a modal dialog allowing the user to set a new goal.
     * Parses the input values and calls the controller to set the goal.
     *
     * @param parent the parent component to attach the dialog to
     * @param controller the SetGoalController to handle the submitted goal
     */

    public static void show(Component parent, SetGoalController controller) {
        final InputFields fields = createInputFields();
        final JPanel panel = buildPanel(fields);
        final int result = JOptionPane.showConfirmDialog(parent, panel,
                "Plant a New Goal Tree", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            processDialogResult(parent, controller, fields);
        }
    }

    private static InputFields createInputFields() {
        final JTextField monthField = new JTextField(10);
        monthField.setText(YearMonth.now().toString());
        final JTextField amountField = new JTextField(10);
        
        // Create multiselect dropdown for categories
        final JList<String> categoriesList = new JList<>(AVAILABLE_CATEGORIES);
        categoriesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        categoriesList.setVisibleRowCount(CATEGORY_LIST_VISIBLE_ROWS);

        return new InputFields(monthField, amountField, categoriesList);
    }

    private static JPanel buildPanel(InputFields fields) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        final JPanel monthLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        monthLabelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        monthLabelPanel.add(new JLabel("Month (YYYY-MM):"));

        panel.add(monthLabelPanel);
        panel.add(fields.getMonthField());
        panel.add(Box.createVerticalStrut(VERTICAL_SPACING));
        panel.add(new JLabel("Goal Amount ($):"));
        panel.add(fields.getAmountField());
        panel.add(Box.createVerticalStrut(VERTICAL_SPACING));
        panel.add(new JLabel("Categories (select one or more):"));
        final JScrollPane categoriesScrollPane = new JScrollPane(fields.getCategoriesList());
        panel.add(categoriesScrollPane);

        return panel;
    }

    private static void processDialogResult(Component parent, SetGoalController controller,
                                           InputFields fields) {
        try {
            final YearMonth month = YearMonth.parse(fields.getMonthField().getText());
            final float amount = Float.parseFloat(fields.getAmountField().getText());
            final List<String> selectedCategoryNames = fields.getCategoriesList().getSelectedValuesList();
            final List<Category> categories = new ArrayList<>();
            for (String name : selectedCategoryNames) {
                categories.add(new Category(name));
            }
            
            if (!categories.isEmpty()) {
                controller.setGoal(month, amount, categories);
            }
            else {
                JOptionPane.showMessageDialog(parent, "Please select at least one category.");
            }
        }
        catch (DateTimeParseException error) {
            JOptionPane.showMessageDialog(parent, "Invalid Date. Use YYYY-MM.");
        }
        catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(parent, "Invalid Goal Amount. Enter a valid number.");
        }
    }

    private static class InputFields {
        private final JTextField monthField;
        private final JTextField amountField;
        private final JList<String> categoriesList;

        InputFields(JTextField monthField, JTextField amountField, JList<String> categoriesList) {
            this.monthField = monthField;
            this.amountField = amountField;
            this.categoriesList = categoriesList;
        }

        JTextField getMonthField() {
            return monthField;
        }

        JTextField getAmountField() {
            return amountField;
        }

        JList<String> getCategoriesList() {
            return categoriesList;
        }
    }
}
