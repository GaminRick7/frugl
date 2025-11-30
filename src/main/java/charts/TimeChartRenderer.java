package charts;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;
import static java.util.stream.Collectors.joining;

public class TimeChartRenderer implements ChartRenderer<ProcessedTimeChartData> {

    @Override
    public Image render(ProcessedTimeChartData data) throws Exception {
        String incomeValues = data.getDataPoints().stream()
                .map(ProcessedTimeChartData.DataPoint::getIncome)
                .map(v -> String.format("%.2f", v))
                .collect(joining(","));

        String expenseValues = data.getDataPoints().stream()
                .map(ProcessedTimeChartData.DataPoint::getExpense)
                .map(v -> String.format("%.2f", v))
                .collect(joining(","));

        String labels = data.getDataPoints().stream()
                .map(ProcessedTimeChartData.DataPoint::getLabel)
                .collect(joining("|"));

        if (labels.isEmpty()) {
            labels = "No%20Data";
            incomeValues = "0.0";
            expenseValues = "0.0";
        }

        String url =
                "https://quickchart.io/chart?" +
                        "cht=bvg&chs=700x300" +
                        "&chxt=x,y" +
                        "&chbh=a" +
                        "&chd=t:" + incomeValues + "|" + expenseValues +
                        "&chco=0000FF,FF0000" + //green income, red expenses
                        "&chxl=0:|" + labels +
                        "&chdl=Income|Expenses";

        return ImageIO.read(new URL(url));
    }
}
