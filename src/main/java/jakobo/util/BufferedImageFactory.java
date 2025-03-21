package jakobo.util;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BufferedImageFactory {

    public static BufferedImage createBufferedImage(final MapContent map, final int imageWidth, final ReferencedEnvelope mapBounds) {
        final GTRenderer renderer = new StreamingRenderer();
        renderer.setMapContent(map);

        double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
        final Rectangle imageBounds = new Rectangle(
                0, 0, imageWidth, (int) Math.round(imageWidth * heightToWidth));

        final BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

        Graphics2D gr = image.createGraphics();
        gr.setPaint(Color.WHITE);
        gr.fill(imageBounds);

        renderer.paint(gr, imageBounds, mapBounds);

        map.dispose();

        return image;
    }
}
