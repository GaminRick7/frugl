package charts;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

public class TimeChartRenderer implements ChartRenderer {

    private final List<Integer> income;

    private final List<Integer> expenses;

    private final List<String> months;

    public TimeChartRenderer(List<Integer> income,
                             List<Integer> expenses,
                             List<String> months) {
        this.income = income;
        this.expenses = expenses;
        this.months = months;

        // TODO implementation depends on data input
    }

    @Override
    public BufferedImage render() throws Exception {
        String incomeValues = income.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String expenseValues = expenses.stream()
                .map(e -> "-" + e) // negative to plot below axis
                .collect(Collectors.joining(","));

        String monthLabels = String.join("|", months);

        String url =
                "https://chart.googleapis.com/chart?" +
                        "cht=lc&chs=700x300" +
                        "&chxt=x,y" +
                        "&chd=t:" + incomeValues + "|" + expenseValues +
                        "&chco=0000FF,FF0000" +
                        "&chxl=0:|" + monthLabels +
                        "&chdl=Income|Expenses";

        return ImageIO.read(new URL(url));
    }
}
