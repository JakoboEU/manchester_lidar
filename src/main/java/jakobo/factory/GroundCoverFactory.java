package jakobo.factory;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.processing.Operations;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;

import java.awt.image.*;

public class GroundCoverFactory {

    public static GridCoverage2D createGroundCover(String title, GridCoverage2D terrain, GridCoverage2D surface, ReferencedEnvelope bounds) {
        // Create output raster
        GridCoverage2D croppedTerrain = (GridCoverage2D) new Operations(null).crop(terrain, bounds);
        GridCoverage2D croppedSurface = (GridCoverage2D) new Operations(null).crop(surface, bounds);

        return calculateGroundCover(title, croppedTerrain, croppedSurface);
    }

    private static GridCoverage2D calculateGroundCover(String title, GridCoverage2D terrain, GridCoverage2D surface) {
        RenderedImage terrainImage = terrain.getRenderedImage();
        RenderedImage surfaceImage = surface.getRenderedImage();

        Raster terrainRaster = terrainImage.getData();
        Raster surfaceRaster = surfaceImage.getData();
        WritableRaster resultRaster = terrainRaster.createCompatibleWritableRaster(); // Same size & format

        final int width = terrainImage.getWidth();
        final int height = terrainImage.getHeight();

        // Loop through each pixel and calculate (surface - terrain)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double terrainValue = terrainRaster.getSampleDouble(x, y, 0); // Get terrain value
                double surfaceValue = surfaceRaster.getSampleDouble(x, y, 0); // Get surface value
                double resultValue = surfaceValue - terrainValue; // Perform subtraction

                resultRaster.setSample(x, y, 0, resultValue); // Store result
            }
        }

        // Create new GridCoverage2D with the result raster
        final GridCoverageFactory factory = new GridCoverageFactory();
        final Envelope2D envelope = terrain.getEnvelope2D(); // Keep spatial bounds

        return factory.create(title, resultRaster, envelope);
    }
}
