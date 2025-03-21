package jakobo.util;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.geotiff.GeoTiffWriter;

import java.io.File;
import java.io.IOException;

import static jakobo.util.CrsFactory.crs;

public class GeoTiffOut {
    public static void saveRaster(GridCoverage2D coverage, String outputPath) throws IOException {
        System.out.println("Writing tiff with CRS: " + coverage.getCoordinateReferenceSystem());
        final File outputFile = new File(outputPath);
        final GeoTiffWriter writer = new GeoTiffWriter(outputFile);
        writer.write((GridCoverage2D) Operations.DEFAULT.resample(coverage, crs()), null);
        writer.dispose();
    }
}
