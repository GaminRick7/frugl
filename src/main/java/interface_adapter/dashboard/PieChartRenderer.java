package interface_adapter.dashboard;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import use_case.load_dashboard.PieChartData;

/**
 * Renders pie chart images from pie chart data or returns an IOException if rendering fails.
 * Uses QuickChart.io API.
 */
public class PieChartRenderer implements ChartRenderer<PieChartData> {

    @Override
    public Image render(PieChartData data) throws IOException {
        final Map<String, Double> categories = data.getCategoryTotals();
        final String chartConfig;

        // Handle empty data case
        if (categories == null || categories.isEmpty()) {
            chartConfig = "{"
                    + "\"type\": \"pie\","
                    + "\"data\": {"
                    + "\"labels\": [\"No Data\"],"
                    + "\"datasets\": [{"
                    + "\"data\": [1],"
                    + "\"backgroundColor\": [\"#E0E0E0\"]"
                    + "}]},"
                    + "\"options\": { \"plugins\": { \"datalabels\": { \"display\": false }}}"
                    + "}";
        }
        else {
            // Collect values and labels from data
            final String labels = categories.keySet().stream()
                    .map(key -> "\"" + key + "\"")
                    .collect(Collectors.joining(", "));

            final String values = categories.values().stream()
                    .map(Math::abs)
                    .map(value -> String.format("%.2f", value))
                    .collect(Collectors.joining(","));

            chartConfig = "{"
                    + "\"type\": \"pie\","
                    + "\"data\": {"
                    + "\"labels\": [" + labels + "],"
                    + "\"datasets\": [{"
                    + "\"data\": [" + values + "],"
                    + "\"backgroundColor\": [\"#FF6384\", \"#36A2EB\", \"#FFCE56\", \"#4BC0C0\", \"#9966FF\"]"
                    + "}]},"
                    + "\"options\": { \"plugins\": { \"datalabels\": { \"display\": true }}}"
                    + "}";
        }

        final String encodedConfig = URLEncoder.encode(chartConfig, StandardCharsets.UTF_8);
        final String urlString = "https://quickchart.io/chart?c=" + encodedConfig;

        // Throws IOException if required
        return ImageIO.read(new URL(urlString));
    }
}
