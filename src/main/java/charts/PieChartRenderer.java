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

        String values = categories.values().stream()
                .map(v -> String.format("%.2f", v))
                .collect(joining(","));

        String labels = categories.keySet().stream()
                .collect(joining("|"));

        String url = "https://chart.googleapis.com/chart?" +
                "cht=p&chs=500x300" +
                "&chd=t:" + values +
                "&chl=" + labels;

        return ImageIO.read(new URL(url));
    }
}
