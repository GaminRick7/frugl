package charts;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;
import java.util.Map;
import static java.util.stream.Collectors.joining;

public class PieChartRenderer implements ChartRenderer<ProcessedPieChartData> {

    @Override
    public Image render(ProcessedPieChartData data) throws Exception {
        Map<String, Double> categories = data.getCategoryTotals();

        String values = "100";
        String labels = "No%20Data";

        if (categories != null && !categories.isEmpty()) {
             values = categories.values().stream()
                    .map(v -> String.format("%.2f", v))
                    .collect(joining(","));
             labels = categories.keySet().stream()
                    .collect(joining("|"));
        }

        String url = "https://quickchart.io/chart?" +
                "cht=p&chs=500x300" +
                "&chd=t:" + values +
                "&chl=" + labels;

        return ImageIO.read(new URL(url));
    }
}
