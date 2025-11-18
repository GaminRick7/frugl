package charts;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;
import static java.util.stream.Collectors.joining;

public class TimeChartRenderer implements ChartRenderer<ProcessedTimeChartData> {

    @Override
    public Image render(ProcessedTimeChartData data) throws Exception {
        String incomeValues = data.getIncomeValues().stream()
                .map(v -> String.format("%.2f", v))
                .collect(joining(","));

        String expenseValues = data.getExpenseValues().stream()
                .map(v -> String.format("%.2f", v))
                .collect(joining(","));

        String labels = String.join("|", data.getLabels());

        String url =
                "https://chart.googleapis.com/chart?" +
                        "cht=lc&chs=700x300" +
                        "&chxt=x,y" +
                        "&chd=t:" + incomeValues + "|" + expenseValues +
                        "&chco=0000FF,FF0000" + //green income, red expenses
                        "&chxl=0:|" + labels +
                        "&chdl=Income|Expenses";

        return ImageIO.read(new URL(url));
    }
}
