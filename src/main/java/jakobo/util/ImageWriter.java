package jakobo.util;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class ImageWriter {
    private ImageWriter() {

    }
    public static void writeMap(MapContent map, final File outputFile, final int imageWidth, final ReferencedEnvelope mapBounds) {
        final GTRenderer renderer = new StreamingRenderer();
        renderer.setMapContent(map);

        double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
        final Rectangle imageBounds = new Rectangle(
                0, 0, imageWidth, (int) Math.round(imageWidth * heightToWidth));

        final BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

        Graphics2D gr = image.createGraphics();
        gr.setPaint(Color.WHITE);
        gr.fill(imageBounds);

        try {
            renderer.paint(gr, imageBounds, mapBounds);
            ImageIO.write(image, "jpeg", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
