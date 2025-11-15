package charts;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

public class PieChartRenderer implements ChartRenderer {

    private final Map<String, Integer> categories;

    public PieChartRenderer(Map<String, Integer> categories) {
        this.categories = categories;
        //TODO implementation depends on data input
    }

    @Override
    public BufferedImage render() throws Exception {
        String values = categories.values().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String labels = String.join("|", categories.keySet());

        String url =
                "https://chart.googleapis.com/chart?" +
                        "cht=p&chs=500x300" +
                        "&chd=t:" + values +
                        "&chl=" + labels;

        return ImageIO.read(new URL(url));
    }
}
