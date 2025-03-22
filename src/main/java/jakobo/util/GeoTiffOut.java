package jakobo.util;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.Envelope;

import java.io.File;
import java.io.IOException;

public class GeoTiffOut {
    public static void saveRaster(GridCoverage2D coverage, String outputPath) throws IOException {
        System.out.println("Writing tiff with CRS: " + coverage.getCoordinateReferenceSystem());
        final File outputFile = new File(outputPath);
        final GeoTiffWriter writer = new GeoTiffWriter(outputFile);
        writer.write(coverage, null);
        writer.dispose();
    }

    public static void saveRaster(GridCoverage2D coverage, ReferencedEnvelope bounds, String outputPath) throws IOException {
        System.out.println("Input CRS " + coverage.getCoordinateReferenceSystem());
        final Envelope envelope = new GeneralEnvelope(bounds);
        GridCoverage2D croppedCoverage = (GridCoverage2D) new Operations(null).crop(coverage, envelope);
        saveRaster(croppedCoverage, outputPath);
    }
}
