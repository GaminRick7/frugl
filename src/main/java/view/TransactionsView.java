package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import interface_adapter.view_transaction.TransactionRowPanel;
import interface_adapter.view_transaction.ViewTransactionController;
import interface_adapter.view_transaction.ViewTransactionState;
import interface_adapter.view_transaction.ViewTransactionViewModel;

/**
 * The View for the Transactions Use Case.
 */
public class TransactionsView extends JPanel implements ActionListener, PropertyChangeListener {
    private static final String DEFAULT_MONTH_NUM = "01";
    private static final String[] MONTHS = {"January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"};
    private static final String[] MONTH_NUMBERS = {"01", "02", "03", "04", "05", "06",
        "07", "08", "09", "10", "11", "12"};

    private final String viewName = "view transaction";
    private final ViewTransactionViewModel viewTransactionViewModel;
    private ViewTransactionController viewTransactionController;

    private final JPanel transactionTilesBlock = new JPanel();
    private JComboBox<String> dropdownMonth;
    private JComboBox<String> dropdownYear;

    private final String[] dropdownYearList = {"2025", "2024", "2023"};

    public TransactionsView(ViewTransactionViewModel viewTransactionViewModel) {
        this.viewTransactionViewModel = viewTransactionViewModel;
        this.viewTransactionViewModel.addPropertyChangeListener(this);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        buildContainer();
    }

    /**
     * We will build the basic default container where total transactions is stored.
     */
    private void buildContainer() {
        final JPanel selectDatePanel = new JPanel();
        dropdownMonth = new JComboBox<>(MONTHS);
        dropdownYear = new JComboBox<>(dropdownYearList);

        final JLabel monthTitle = new JLabel("Month:");
        final JLabel yearTitle = new JLabel("Year:");
        final JButton dateButton = new JButton("Okay");
        dateButton.addActionListener(event -> clickedMonth());

        selectDatePanel.add(yearTitle);
        selectDatePanel.add(dropdownYear);
        selectDatePanel.add(monthTitle);
        selectDatePanel.add(dropdownMonth);
        selectDatePanel.add(dateButton);

        this.add(selectDatePanel);

        // Loading the data
        transactionTilesBlock.setLayout(new BoxLayout(transactionTilesBlock, BoxLayout.Y_AXIS));
        final JScrollPane scrollPane = new JScrollPane(transactionTilesBlock);

        this.add(scrollPane);
    }

    private void rebuildTiles(final List<Map<String, Object>> monthlyTransactions) {
        transactionTilesBlock.removeAll();

        if (monthlyTransactions == null || monthlyTransactions.isEmpty()) {
            transactionTilesBlock.add(new JLabel("No transactions found for this month."));
        }
        else {
            final TransactionRowPanel header = new TransactionRowPanel(
                    "Date", "Source", "Category", "Amount"
            );
            transactionTilesBlock.add(header);

            for (int i = 0; i < monthlyTransactions.size(); i++) {
                final Map<String, Object> transaction = monthlyTransactions.get(i);
                final TransactionRowPanel row = new TransactionRowPanel(
                        String.valueOf(transaction.get("date")),
                        String.valueOf(transaction.get("source")),
                        String.valueOf(transaction.get("category")),
                        (String) transaction.get("amount")
                );
                transactionTilesBlock.add(row);
            }
        }

        transactionTilesBlock.revalidate();
        transactionTilesBlock.repaint();
    }

    private void clickedMonth() {
        final String selectedMonth = (String) dropdownMonth.getSelectedItem();
        final String selectedYear = (String) dropdownYear.getSelectedItem();
        final String monthNumber = getMonthNumber(selectedMonth);
        final String yearMonthString = selectedYear + "-" + monthNumber;

        viewTransactionController.execute(yearMonthString);
    }

    /**
     * React to a button click that results in evt.
     * @param evt the ActionEvent to react to
     */
    @Override
    public void actionPerformed(final ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            final ViewTransactionState state = (ViewTransactionState) evt.getNewValue();
            if (state.getMonthlyTransactions() != null) {
                rebuildTiles(state.getMonthlyTransactions());
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewTransactionController(final ViewTransactionController viewTransactionController) {
        this.viewTransactionController = viewTransactionController;
    }

    private String getMonthNumber(String month) {
        String result = DEFAULT_MONTH_NUM;
        for (int i = 0; i < MONTHS.length; i++) {
            if (MONTHS[i].equals(month)) {
                result = MONTH_NUMBERS[i];
                break;
            }
        }
        return result;
    }
}
