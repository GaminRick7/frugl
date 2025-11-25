package interface_adapter.dashboard;

import java.awt.*;
import java.util.List;

public class DashboardState {
    private java.util.List<Image> chartImages;

    public java.util.List<Image> getChartImages() {
        return chartImages;
    }
    public void setChartImages(List<Image> chartImages) {
        this.chartImages = chartImages;
    }
}
