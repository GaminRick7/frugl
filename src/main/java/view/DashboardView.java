package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import interface_adapter.ViewManagerModel;
import interface_adapter.dashboard.DashboardController;
import interface_adapter.dashboard.DashboardState;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.import_statement.ImportStatementViewModel;
import interface_adapter.set_goal.SetGoalViewModel;
import interface_adapter.view_transaction.ViewTransactionViewModel;
import use_case.load_dashboard.TimeRange;

/**
 * View for the Dashboard Use Case.
 */
public class DashboardView extends JPanel implements PropertyChangeListener {
    private static final int DEFAULT_DAY_RANGE = -30;
    private static final int DEFAULT_GAP = 10;

    private transient DashboardController controller;
    private final transient DashboardViewModel viewModel;
    private final transient ViewManagerModel viewManagerModel;

    // UI Components
    private JComboBox<TimeRange> timeRangeDropdown;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JLabel pieChartLabel;
    private JLabel timeChartLabel;

    public DashboardView(DashboardViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;

        // For state updates
        this.viewModel.addPropertyChangeListener(this);
        setupUserInterface();
    }

    /**
     * Sets the controller for this view.
     *
     * @param dashboardController The DashboardController.
     */
    public void setDashboardController(DashboardController dashboardController) {
        this.controller = dashboardController;
    }

    /**
     * Triggers initial data load upon application start.
     */
    public void loadInitialData() {
        if (controller != null) {
            onRefreshClicked();
        }
    }

    private void setupUserInterface() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(DEFAULT_GAP, DEFAULT_GAP, DEFAULT_GAP, DEFAULT_GAP));

        // 1. Create central split panel for charts
        final JPanel splitPanel = createSplitPanel();
        this.add(splitPanel, BorderLayout.CENTER);

        // 2. Create bottom button panel
        final JPanel bottomPanel = createBottomPanel();
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Helper: Creates the main middle panel with two columns.
     * @return JPanel The center split panel.
     */
    private JPanel createSplitPanel() {
        final JPanel splitPanel = new JPanel(new GridLayout(1, 2, DEFAULT_GAP, DEFAULT_GAP));

        // Left: Time Chart
        splitPanel.add(createLeftPanel());

        // Right: Pie Chart
        splitPanel.add(createRightPanel());

        return splitPanel;
    }

    /**
     * Helper: Creates the Left Panel with time range controls and time chart.
     * @return JPanel The left panel with the time chart.
     */
    private JPanel createLeftPanel() {
        final JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Time Chart"));

        // 1. Left: Time Chart Controls
        final JPanel timeControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeControlsPanel.add(new JLabel("Time Range:"));

        timeRangeDropdown = new JComboBox<>(TimeRange.values());
        timeControlsPanel.add(timeRangeDropdown);

        leftPanel.add(timeControlsPanel, BorderLayout.NORTH);

        // 2. Left: Time Chart Display
        timeChartLabel = new JLabel("Loading Time Chart...", SwingConstants.CENTER);
        leftPanel.add(timeChartLabel, BorderLayout.CENTER);

        return leftPanel;
    }

    /**
     * Helper: Creates the Right Panel with date spinners and pie chart.
     * @return JPanel The right panel with the pie chart.
     */
    private JPanel createRightPanel() {
        final JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Pie Chart"));

        // 1. Right: Pie Chart Controls
        final JPanel pieControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        setupDateSpinner(pieControlsPanel);

        rightPanel.add(pieControlsPanel, BorderLayout.NORTH);

        // 2. Right: Pie Chart Display
        pieChartLabel = new JLabel("Loading Pie Chart...", SwingConstants.CENTER);
        rightPanel.add(pieChartLabel, BorderLayout.CENTER);

        return rightPanel;
    }

    /**
     * Helper: Creates Date Spinners.
     * @param pieControlsPanel The pie controls panel.
     */
    private void setupDateSpinner(JPanel pieControlsPanel) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, DEFAULT_DAY_RANGE);
        final Date defaultStartDate = calendar.getTime();
        final Date defaultEndDate = new Date();

        startDateSpinner = new JSpinner(new SpinnerDateModel(defaultStartDate, null, null, Calendar.DAY_OF_MONTH));
        endDateSpinner = new JSpinner(new SpinnerDateModel(defaultEndDate, null, null, Calendar.DAY_OF_MONTH));

        final JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        final JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startEditor);
        endDateSpinner.setEditor(endEditor);

        pieControlsPanel.add(new JLabel("Start Date:"));
        pieControlsPanel.add(startDateSpinner);
        pieControlsPanel.add(new JLabel("End Date:"));
        pieControlsPanel.add(endDateSpinner);
    }

    /**
     * Helper: Creates bottom panel with refresh and navigation buttons.
     * @return JPanel The bottom panel with the buttons
     */
    private JPanel createBottomPanel() {
        final JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // 1. Import Button
        final JButton importStatementButton = new JButton("Import Statement");
        importStatementButton.addActionListener(event -> onImportStatementClicked());
        bottomPanel.add(importStatementButton);

        // 2. Refresh Button
        final JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.addActionListener(event -> {
            if (controller != null) {
                onRefreshClicked();
            }
        });
        bottomPanel.add(refreshButton);

        // 3. Goal Button
        final JButton goalViewButton = new JButton("View Goals");
        goalViewButton.addActionListener(event -> onGoalViewClicked());
        bottomPanel.add(goalViewButton);

        // 4. View Transactions Button
        final JButton viewTransactionsButton = new JButton("View Transactions");
        viewTransactionsButton.addActionListener(event -> onViewTransactionsClicked());
        bottomPanel.add(viewTransactionsButton);

        return bottomPanel;
    }

    private void onRefreshClicked() {
        final TimeRange selectedTimeRange = (TimeRange) timeRangeDropdown.getSelectedItem();

        final Date legacyStartDate = (Date) startDateSpinner.getValue();
        final Date legacyEndDate = (Date) endDateSpinner.getValue();

        final LocalDate startDate = convertToLocalDate(legacyStartDate);
        final LocalDate endDate = convertToLocalDate(legacyEndDate);

        final LocalDate currentDate = LocalDate.now();

        controller.loadDashboard(currentDate, selectedTimeRange, startDate, endDate);
    }

    // Helper to convert Date to LocalDate
    private LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // Helper to update chart displays
    private void updateChartDisplay() {
        final DashboardState state = viewModel.getState();
        final List<Image> chartImages = state.getChartImages();

        if (chartImages == null || chartImages.isEmpty()) {
            pieChartLabel.setText("No Data");
            pieChartLabel.setIcon(null);
            timeChartLabel.setText("No Data");
            timeChartLabel.setIcon(null);
        }
        else {
            timeChartLabel.setText("");
            timeChartLabel.setIcon(new ImageIcon(chartImages.get(1)));
            pieChartLabel.setText("");
            pieChartLabel.setIcon(new ImageIcon(chartImages.get(0)));
        }
    }

    /**
     * Property change method.
     * @param event A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent event) {
        final DashboardState state = (DashboardState) event.getNewValue();

        SwingUtilities.invokeLater(() -> {
            if (state.getChartImages() != null) {
                updateChartDisplay();
            }

            this.revalidate();
            this.repaint();
        });
    }

    private void onImportStatementClicked() {
        viewManagerModel.setState(ImportStatementViewModel.VIEW_NAME);
        viewManagerModel.firePropertyChange();
    }

    private void onGoalViewClicked() {
        viewManagerModel.setState(SetGoalViewModel.VIEW_NAME);
        viewManagerModel.firePropertyChange();

    }

    private void onViewTransactionsClicked() {
        viewManagerModel.setState(ViewTransactionViewModel.VIEW_NAME);
        viewManagerModel.firePropertyChange();
    }
}
