package jakobo.util;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class GridCoverage2DSupport {

    private static final GridCoverageFactory GRID_COVERAGE_FACTORY = new GridCoverageFactory();

    public static GridCoverage2D fromBufferedImage(final BufferedImage image, final ReferencedEnvelope bounds) {
        return GRID_COVERAGE_FACTORY.create("RenderedImage", image, bounds);
    }

    public static GridCoverage2D fromWritableRaster(final WritableRaster image, final ReferencedEnvelope bounds) {
        return GRID_COVERAGE_FACTORY.create("RenderedRaster", image, bounds);
    }
}
