package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.dashboard.DashboardController;
import interface_adapter.dashboard.DashboardState;
import interface_adapter.dashboard.DashboardViewModel;
import use_case.load_dashboard.TimeRange;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardView extends JPanel implements PropertyChangeListener {
    private DashboardController controller;
    private final DashboardViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    //UI Components
    private JComboBox<TimeRange> timeRangeDropdown;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;

    private JLabel pieChartLabel;
    private JLabel timeChartLabel;

    public DashboardView(DashboardViewModel viewModel,  ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;

        this.viewManagerModel = viewManagerModel;
        this.viewModel.addPropertyChangeListener(this);

        setupUI();
    }

    private void setupUI() {
        this.setLayout(new BorderLayout());

        JPanel splitPanel = new JPanel(new GridLayout(1, 2));
        splitPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- LEFT SIDE: TIME CHART PANEL ----
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Time Chart"));

        // 1. Left: Time Chart Controls
        JPanel timeControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeControlsPanel.add(new JLabel("Time Range:"));
        timeRangeDropdown = new JComboBox<>(TimeRange.values());
        timeControlsPanel.add(timeRangeDropdown);

        leftPanel.add(timeControlsPanel, BorderLayout.NORTH);

        // 2. Left: Time Chart Display
        timeChartLabel = new JLabel("Loading Time Chart...", SwingConstants.CENTER);
        leftPanel.add(timeChartLabel, BorderLayout.CENTER);

        // ---- RIGHT SIDE: PIE CHART PANEL ----
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Pie Chart"));

        // 1. Right: Pie Chart Controls
        JPanel pieControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Calendar calendar =  Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date defaultStartDate = calendar.getTime();
        Date defaultEndDate = new Date();

        startDateSpinner = createDateSpinner(defaultStartDate);
        endDateSpinner = createDateSpinner(defaultEndDate);

        pieControlsPanel.add(new JLabel("Start Date:"));
        pieControlsPanel.add(startDateSpinner);
        pieControlsPanel.add(new JLabel("End Date:"));
        pieControlsPanel.add(endDateSpinner);

        rightPanel.add(pieControlsPanel, BorderLayout.NORTH);

        //2. Right: Pie Chart Display
        pieChartLabel = new JLabel("Loading Pie Chart...", SwingConstants.CENTER);
        rightPanel.add(pieChartLabel, BorderLayout.CENTER);

        splitPanel.add(leftPanel);
        splitPanel.add(rightPanel);
        this.add(splitPanel, BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.addActionListener(e -> {
            try {
                onRefreshClicked();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        bottomPanel.add(refreshButton);
        JButton importStatementButton = new JButton("Import Statement");
        importStatementButton.addActionListener(e -> onImportStatementClicked());
        bottomPanel.add(importStatementButton);

        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JSpinner createDateSpinner(Date defaultDate){
        SpinnerDateModel model = new SpinnerDateModel();
        model.setValue(defaultDate);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        return spinner;
    }

    public void loadInitialData() throws Exception {
        onRefreshClicked();
    }

    private void onImportStatementClicked() {
        viewManagerModel.setState("import statement");
        viewManagerModel.firePropertyChange();
    }

    private void onRefreshClicked() throws Exception {
        TimeRange selectedTimeRange = (TimeRange) timeRangeDropdown.getSelectedItem();

        Date legacyStartDate = (Date) startDateSpinner.getValue();
        Date legacyEndDate = (Date) endDateSpinner.getValue();

        LocalDate startDate = convertToLocalDate(legacyStartDate);
        LocalDate endDate = convertToLocalDate(legacyEndDate);

        LocalDate currentDate = LocalDate.now();

        controller.loadDashboard(currentDate, selectedTimeRange, startDate, endDate);
    }

    // Helper to convert Date to LocalDate
    private LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private void updateChartDisplay() {
        DashboardState state = viewModel.getState();
        List<Image> chartImages = state.getChartImages();

        if (chartImages == null || chartImages.isEmpty()){
            pieChartLabel.setText("No Data");
            pieChartLabel.setIcon(null);
            timeChartLabel.setText("No Data");
            timeChartLabel.setIcon(null);
        } else {
            timeChartLabel.setText("");
            timeChartLabel.setIcon(new ImageIcon(chartImages.get(1)));
            pieChartLabel.setText("");
            pieChartLabel.setIcon(new ImageIcon(chartImages.get(0)));
            }
        }

    public void setDashboardController(DashboardController controller) {
        this.controller = controller;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        DashboardState state = (DashboardState)  evt.getNewValue();

        SwingUtilities.invokeLater(() -> {
            if (state.getChartImages() != null) {
                updateChartDisplay();
            }

            this.revalidate();
            this.repaint();
        });
    }
}
