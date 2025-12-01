package charts;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

/**
 * Renders time chart images from time chart data or returns an IOException if rendering fails.
 * Uses QuickChart.io API.
 */
public class TimeChartRenderer implements ChartRenderer<ProcessedTimeChartData> {
    private static final String DELIMITER = ",";

    @Override
    public Image render(ProcessedTimeChartData data) throws IOException {
        final String chartConfig;
        // Handle empty data case
        if (data.getDataPoints().isEmpty()) {
            chartConfig = "{\"type\": \"bar\","
                    + "\"data\": {"
                    + "\"labels\": [\"No Data\"],"
                    + "\"datasets\": [{"
                    + "\"label\": \"No Data\","
                    + "\"data\": [0],"
                    + "\"backgroundColor\": \"#E0E0E0\""
                    + "}]},"
                    + "\"options\": {\"scales\": {\"yAxes\": [{ \"ticks\": { \"beginAtZero\": true } }]}}}";
        }
        else {
            // Build income data string using the Record accessor
            final String incomeValues = data.getDataPoints().stream()
                    .map(ProcessedTimeChartData.DataPoint::income)
                    .map(value -> String.format("%.2f", value))
                    .collect(Collectors.joining(DELIMITER));

            // Build the expense data strings using Record accessor
            final String expenseValues = data.getDataPoints().stream()
                    .map(ProcessedTimeChartData.DataPoint::expense)
                    .map(value -> String.format("%.2f", value))
                    .collect(Collectors.joining(DELIMITER));

            // Build the labels string using the Record accessor
            final String labels = data.getDataPoints().stream()
                    .map(ProcessedTimeChartData.DataPoint::label)
                    .map(label -> "\"" + label + "\"")
                    .collect(Collectors.joining(DELIMITER));

            chartConfig = "{\"type\": \"bar\","
                    + "\"data\": {"
                    + "\"labels\": [" + labels + "], \"datasets\": ["
                    + "{"
                    + "\"label\": \"Income\", \"data\": [" + incomeValues + "], \"backgroundColor\": \"#4BC0C0\""
                    + "},"
                    + "{"
                    + "\"label\": \"Expenses\", \"data\": [" + expenseValues + "], \"backgroundColor\": \"#FF6384\""
                    + "}]},"
                    + "\"options\": {"
                    + "\"layout\": {"
                    + "\"padding\": 30"
                    + "},"
                    + "\"legend\": { \"display\": true },"
                    + "\"scales\": {"
                    + "\"xAxes\": [{"
                    + "\"stacked\": true,"
                    + "\"barPercentage\": 0.7,"
                    + "\"ticks\": { \"autoSkip\": false, \"minRotation\": 45 },"
                    + "\"gridLines\": { \"display\": false }"
                    + "}],"
                    + "\"yAxes\": [{"
                    + "\"stacked\": true,"
                    + "\"ticks\": {"
                    + "\"beginAtZero\": true,"
                    + "\"suggestedMax\": 10,"
                    + "\"suggestedMin\": -10"
                    + "}}]}}}";
        }

        final String encodedConfig = URLEncoder.encode(chartConfig, StandardCharsets.UTF_8);
        final String urlString = "https://quickchart.io/chart?c=" + encodedConfig;
        return ImageIO.read(new URL(urlString));
    }
}
