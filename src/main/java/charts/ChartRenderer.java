package charts;

import java.awt.Image;
import java.io.IOException;

/**
 * Interface for rendering chart data to images.
 * @param <T> The specific type of chart data to render.
 */
public interface ChartRenderer<T extends AbstractProcessedChartData> {
    /**
     * Renders the given data into an Image.
     * @param data The processed data to visualize.
     * @return An Image object with the chart data.
     * @throws IOException If the chart image cannot be rendered.
     */
    Image render(T data) throws IOException;
}
