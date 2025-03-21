package jakobo.geo;

import java.io.File;

public class LidarImage {
    private final String name;
    private final Extent extent;
    private final File imageFile;

    public LidarImage(String name, Extent extent, File imageFile) {
        this.name = name;
        this.extent = extent;
        this.imageFile = imageFile;
    }

    public Extent getExtent() {
        return extent;
    }

    public File getImageFile() {
        return imageFile;
    }
}
