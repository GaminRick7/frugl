package view;
import interface_adapter.dashboard.DashboardState;
import interface_adapter.view_transaction.ViewTransactionController;
import interface_adapter.view_transaction.ViewTransactionState;
import interface_adapter.view_transaction.ViewTransactionViewModel;
import use_case.load_dashboard.TimeRange;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.*;

public class TransactionsView extends JPanel implements ActionListener, PropertyChangeListener {

    //initialize all the compoents in CA
    private final String viewName = "view transaction";
    private final ViewTransactionViewModel viewTransactionViewModel;

    private ViewTransactionController viewTransactionController;

    //master Frame made up of all JPanel
    private final JFrame parentFrame;

    //componets for my view
    private final JPanel transactionTilesBlock = new JPanel();
    private JComboBox<String> dropdownMonth;
    private JComboBox<String> dropdownYear;


    //dropDown data
    private final Map<String, String> dropdownMonthLabels = new LinkedHashMap<>(); //searched up online for Reference
    private final String[] dropdownYearList = {"2025", "2024", "2023"};


    public TransactionsView (ViewTransactionViewModel viewTransactionViewModel, JFrame parentFrame) {
        this.viewTransactionViewModel= viewTransactionViewModel;
        this.parentFrame = parentFrame;
        this.viewTransactionViewModel.addPropertyChangeListener(this);
        this.setLayout(new BorderLayout());

        populateDropDown();

        buildContainer();

    }


    public void populateDropDown(){
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
     * We will build the basic default container where total transactions is stored
     */
    private void buildContainer() {

        //Create dropdown
        JPanel selctDatePanel = new JPanel();

        //fill dropdowns
        String[] months = dropdownMonthLabels.keySet().toArray(new String[0]);
        dropdownMonth = new JComboBox<>(months);
        dropdownYear = new JComboBox<>(dropdownYearList);

        JLabel monthTitle = new JLabel("Month:");
        JLabel yearTitle = new JLabel("Year:");
        //Creatiing okay buttons

        JButton dateButton = new JButton("Okay");
        dateButton.addActionListener(e -> ClickedMonth());

        selctDatePanel.add(yearTitle);
        selctDatePanel.add(dropdownYear);
        selctDatePanel.add(monthTitle);
        selctDatePanel.add(dropdownMonth);
        selctDatePanel.add(dateButton);

        this.add(selctDatePanel, BorderLayout.NORTH);


        //loading the data
        transactionTilesBlock.setLayout(new BoxLayout(transactionTilesBlock, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(transactionTilesBlock);

        this.add(scrollPane, BorderLayout.CENTER);

    }


    private void rebuildTiles(List<HashMap<String, Object>> monthlyTransactions) { //monthly transactions

        transactionTilesBlock.removeAll();



        if (monthlyTransactions == null || monthlyTransactions.isEmpty()) {
            transactionTilesBlock.add(new JLabel("No transactions found for this month."));
        } else {
            // Header
            JPanel header = new JPanel(new GridLayout(1, 5));
            header.add(new JLabel("Date"));
            header.add(new JLabel("Source"));
            header.add(new JLabel("Category"));
            header.add(new JLabel("Amount"));
            header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            transactionTilesBlock.add(header);

            for (int i = 0; i < monthlyTransactions.size(); i++) {
                HashMap<String, Object> t = monthlyTransactions.get(i);
                JPanel row = new JPanel(new GridLayout(1, 5));
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

                row.add(new JLabel(String.valueOf(t.get("date"))));
                row.add(new JLabel(String.valueOf(t.get("source"))));
                row.add(new JLabel(String.valueOf(t.get("category"))));
                row.add(new JLabel((String) t.get("amount")));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                transactionTilesBlock.add(row);
            }
        }


        transactionTilesBlock.revalidate();
        transactionTilesBlock.repaint();



}


    private void ClickedMonth() {
        String selectedMonth = (String) dropdownMonth.getSelectedItem();
        String selectedYear = (String) dropdownYear.getSelectedItem();
        String monthNumber = dropdownMonthLabels.get(selectedMonth);
        String yearMonthString = selectedYear + "-" + monthNumber;

        viewTransactionController.execute(yearMonthString);

    }

    /**
     * React to a button click that results in evt.
     * @param evt the ActionEvent to react to
     * Class Code**
     */
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            ViewTransactionState state = (ViewTransactionState) evt.getNewValue();
            if (state.getMonthlyTransactions() != null) {
                rebuildTiles(state.getMonthlyTransactions());
            }
        }}

    public String getViewName() {
        return viewName;
    }

    public void setViewTransactionController(ViewTransactionController viewTransactionController) {
        this.viewTransactionController = viewTransactionController;
    }


}

