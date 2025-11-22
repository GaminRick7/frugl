package charts;

import java.awt.Image;

public interface ChartRenderer<T extends AbstractProcessedChartData> {

    Image render(T data) throws Exception;
}
