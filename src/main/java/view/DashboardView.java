package view;

import interface_adapter.dashboard.DashboardController;
import interface_adapter.dashboard.DashboardViewModel;
import use_case.load_dashboard.TimeRange;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardView extends JPanel{
    private final DashboardController controller;
    private final DashboardViewModel viewModel;

    private JComboBox<TimeRange> timeRangeDropdown;
    private JPanel chartPanel;

    public DashboardView(DashboardController controller, DashboardViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;

        setupUI();
        loadInitialData();
    }

    private void setupUI() {
        this.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Time Range:"));
        timeRangeDropdown = new JComboBox<>(TimeRange.values());
        controlPanel.add(timeRangeDropdown);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> onRefreshClicked());
        controlPanel.add(refreshButton);

        this.add(controlPanel, BorderLayout.NORTH);

        chartPanel = new JPanel();
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        this.add(chartPanel, BorderLayout.CENTER);
    }

    private void loadInitialData() {
        onRefreshClicked();
    }

    private void onRefreshClicked() {
        TimeRange selectedTimeRange = (TimeRange) timeRangeDropdown.getSelectedItem();
        controller.loadDashboard(selectedTimeRange);
        updateChartDisplay();
    }

    private void updateChartDisplay() {
        chartPanel.removeAll();

        List<Image> chartImages = viewModel.getChartImages();
        if (chartImages == null || chartImages.isEmpty()){
            chartPanel.add(new JLabel("No chart data available"));
        } else {
            chartPanel.setLayout(new GridLayout(1, chartImages.size(), 10, 10));
            for (Image image : chartImages) {
                chartPanel.add(new JLabel(new ImageIcon(image)));
            }
        }
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}
