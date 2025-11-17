package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DashboardView extends JPanel implements ActionListener, PropertyChangeListener {

    private final DashboardViewModel dashboardViewModel;
    private final DashboardController controller;

    private final JButton viewTransactionsButton;
    private final JButton goalsButton;
    private final JButton importStatementButton;

    public DashboardView(DashboardViewModel dashboardViewModel, DashboardController controller) {
        this.dashboardViewModel = dashboardViewModel;
        this.controller = controller;

        setLayout(new BorderLayout());

        // Placeholder charts panel
        JPanel chartsPanel = new JPanel();
        chartsPanel.setPreferredSize(new Dimension(600, 300));
        chartsPanel.setBackground(Color.LIGHT_GRAY);
        chartsPanel.add(new JLabel("Charts will go here"));
        add(chartsPanel, BorderLayout.CENTER);

        // Bottom button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        viewTransactionsButton = new JButton("View All Transactions");
        goalsButton = new JButton("Goals");
        importStatementButton = new JButton("Import Statement");

        buttonPanel.add(viewTransactionsButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(goalsButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(importStatementButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Register each button with its own ActionListener using anonymous inner class
        viewTransactionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(viewTransactionsButton)) {
                    controller.onViewTransactions();
                }
            }
        });

        goalsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(goalsButton)) {
                    controller.onGoals();
                }
            }
        });

        importStatementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(importStatementButton)) {
                    controller.onImportStatement();
                }
            }
        });

        // Register as PropertyChangeListener
        dashboardViewModel.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
