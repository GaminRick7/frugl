package interface_adapter.dashboard;

import java.awt.Image;
import java.io.IOException;

import use_case.load_dashboard.ChartData;

/**
 * Interface for rendering chart data to images.
 * @param <T> The specific type of chart data to render.
 */
public interface ChartRenderer<T extends ChartData> {
    /**
     * Renders the given data into an Image.
     * @param data The processed data to visualize.
     * @return An Image object with the chart data.
     * @throws IOException If the chart image cannot be rendered.
     */
    Image render(T data) throws IOException;
}
