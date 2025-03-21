package jakobo.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class ImageWriter {
    private ImageWriter() {

    }
    public static void writeImage(BufferedImage image, final File outputFile) {
        try {
            ImageIO.write(image, "jpeg", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
