package interface_adapter.dashboard;

import java.awt.Image;
import java.util.List;

/**
 * State for the Dashboard Use Case.
 */
public class DashboardState {
    private List<Image> chartImages;
    private String error;

    // Copy Constructor for updating
    public DashboardState(DashboardState dashboardState) {
        this.chartImages = dashboardState.chartImages;
        this.error = dashboardState.error;
    }

    // Default Constructor for setup
    public DashboardState() {
    }

    public List<Image> getChartImages() {
        return chartImages;
    }

    public void setChartImages(List<Image> chartImages) {
        this.chartImages = chartImages;
    }

    public void setError(String error) {
        this.error = error;
    }
}
