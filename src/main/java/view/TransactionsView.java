package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import interface_adapter.ViewManagerModel;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.view_transaction.ViewTransactionController;
import interface_adapter.view_transaction.ViewTransactionState;
import interface_adapter.view_transaction.ViewTransactionViewModel;

/**
 * View for the View Transactions Use Case.
 */
public class TransactionsView extends JPanel implements ActionListener, PropertyChangeListener {

    private static final int ROW_HEIGHT = 40;

    // Initialize all the components in CA
    private static final String VIEW_TRANSACTION_VIEW_NAME = "view transaction";
    private final transient ViewTransactionViewModel viewTransactionViewModel;
    private final transient ViewManagerModel viewManagerModel;
    private transient ViewTransactionController viewTransactionController;

    // Master Frame made up of all JPanel

    // Components for my view
    private final JPanel transactionTilesBlock = new JPanel();
    private JComboBox<String> dropdownMonth;
    private JComboBox<String> dropdownYear;

    // Dropdown data
    private final Map<String, String> dropdownMonthLabels = new LinkedHashMap<>();
    private final String[] dropdownYearList = {"2025", "2024", "2023"};

    public TransactionsView(ViewTransactionViewModel viewTransactionViewModel, ViewManagerModel viewManagerModel) {
        this.viewTransactionViewModel = viewTransactionViewModel;
        this.viewManagerModel = viewManagerModel;
        this.viewTransactionViewModel.addPropertyChangeListener(this);
        this.setLayout(new BorderLayout());

        populateDropDown();
        buildContainer();
    }

    /**
     * Populate the dropdown select option.
     */
    public void populateDropDown() {
        // Using LinkedHashMap to preserve insertion order (Jan -> Dec)
        dropdownMonthLabels.put("January", "01");
        dropdownMonthLabels.put("February", "02");
        dropdownMonthLabels.put("March", "03");
        dropdownMonthLabels.put("April", "04");
        dropdownMonthLabels.put("May", "05");
        dropdownMonthLabels.put("June", "06");
        dropdownMonthLabels.put("July", "07");
        dropdownMonthLabels.put("August", "08");
        dropdownMonthLabels.put("September", "09");
        dropdownMonthLabels.put("October", "10");
        dropdownMonthLabels.put("November", "11");
        dropdownMonthLabels.put("December", "12");
    }

    /**
     * We will build the basic default container where total transactions is stored.
     */
    private void buildContainer() {
        // Create dropdown
        final JPanel selectDatePanel = new JPanel();

        // Fill dropdowns
        final String[] months = dropdownMonthLabels.keySet().toArray(new String[0]);
        dropdownMonth = new JComboBox<>(months);
        dropdownYear = new JComboBox<>(dropdownYearList);

        final JLabel monthTitle = new JLabel("Month:");
        final JLabel yearTitle = new JLabel("Year:");
        // Creating okay buttons
        final JButton dateButton = UserInterfaceFactory.createButton("Okay", evt -> clickedMonth());
        UserInterfaceFactory.stylePrimaryButton(dateButton);

        selectDatePanel.add(yearTitle);
        selectDatePanel.add(dropdownYear);
        selectDatePanel.add(monthTitle);
        selectDatePanel.add(dropdownMonth);
        selectDatePanel.add(dateButton);

        final JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(UserInterfaceFactory.createHeader("Transactions"), BorderLayout.NORTH);
        topPanel.add(selectDatePanel, BorderLayout.SOUTH);

        this.add(topPanel, BorderLayout.NORTH);

        // Loading the data
        transactionTilesBlock.setLayout(new BoxLayout(transactionTilesBlock, BoxLayout.Y_AXIS));
        final JScrollPane scrollPane = new JScrollPane(transactionTilesBlock);

        this.add(scrollPane, BorderLayout.CENTER);

        final JPanel buttonPanel = new JPanel();
        final JButton backButton = UserInterfaceFactory.createButton("Back", evt -> {
            viewManagerModel.setState(DashboardViewModel.VIEW_NAME);
            viewManagerModel.firePropertyChange();
        });
        UserInterfaceFactory.styleSecondaryButton(backButton);
        buttonPanel.add(backButton);

        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void rebuildTiles(List<Map<String, Object>> monthlyTransactions) {
        // Monthly transactions
        transactionTilesBlock.removeAll();

        if (monthlyTransactions == null || monthlyTransactions.isEmpty()) {
            transactionTilesBlock.add(new JLabel("No transactions found for this month."));
        }
        else {
            // Header
            final JPanel header = new JPanel(new GridLayout(1, 5));
            header.add(new JLabel("Date"));
            header.add(new JLabel("Source"));
            header.add(new JLabel("Category"));
            header.add(new JLabel("Amount"));
            header.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));
            transactionTilesBlock.add(header);

            for (int i = 0; i < monthlyTransactions.size(); i++) {
                final Map<String, Object> transaction = monthlyTransactions.get(i);
                final JPanel row = new JPanel(new GridLayout(1, 5));
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

                row.add(new JLabel(String.valueOf(transaction.get("date"))));
                row.add(new JLabel(String.valueOf(transaction.get("source"))));
                row.add(new JLabel(String.valueOf(transaction.get("category"))));
                row.add(new JLabel((String) transaction.get("amount")));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));
                transactionTilesBlock.add(row);
            }
        }

        transactionTilesBlock.revalidate();
        transactionTilesBlock.repaint();
    }

    private void clickedMonth() {
        final String selectedMonth = (String) dropdownMonth.getSelectedItem();
        final String selectedYear = (String) dropdownYear.getSelectedItem();
        final String monthNumber = dropdownMonthLabels.get(selectedMonth);
        final String yearMonthString = selectedYear + "-" + monthNumber;

        viewTransactionController.execute(yearMonthString);
    }

    /**
     * React to a button click that results in evt.
     * @param evt The ActionEvent to react to.
     */
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            final ViewTransactionState state = (ViewTransactionState) evt.getNewValue();
            rebuildTiles(state.getMonthlyTransactions());
        }
    }

    public String getViewName() {
        return VIEW_TRANSACTION_VIEW_NAME;
    }

    public void setViewTransactionController(ViewTransactionController viewTransactionController) {
        this.viewTransactionController = viewTransactionController;
    }
}
