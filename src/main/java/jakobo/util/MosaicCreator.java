package jakobo.util;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.geometry.Envelope2D;
import org.geotools.map.GridReaderLayer;
import org.opengis.geometry.Envelope;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.ArrayList;

public class MosaicCreator {

    public static GridCoverage2D createMosaic(String title, GridReaderLayer[] layers) throws IOException {
        System.out.println("Creating mosaic for " + title);
        // Step 1: Get the bounds and CRS of all layers (assuming layers are aligned)
        final Mosaic mosaic = new Mosaic(layers);

        // Step 2: Calculate the size of the output raster (mosaic)
        final double width = mosaic.getWidth();
        final double height = mosaic.getHeight();

        // Step 3: Create a WritableRaster for the output mosaic
        WritableRaster resultRaster = Raster.createWritableRaster(
                new BandedSampleModel(DataBuffer.TYPE_DOUBLE, (int) width, (int) height, 1), new Point(0, 0));

        // Step 4: Add each layer to the mosaic
        for (GridCoverage2D coverage : mosaic.getLayers()) {
            Raster layerRaster = coverage.getRenderedImage().getData();

            // Get the pixel data and determine the offset in the mosaic
            int offsetX = (int) (coverage.getEnvelope2D().getMinX() - mosaic.getMinX());
            int offsetY = (int) (coverage.getEnvelope2D().getMinY() - mosaic.getMinY());

            // Copy the data from the layer raster into the mosaic raster
            for (int y = 0; y < layerRaster.getHeight(); y++) {
                for (int x = 0; x < layerRaster.getWidth(); x++) {
                    double value = layerRaster.getSampleDouble(x, y, 0); // Get layer value
                    resultRaster.setSample(x + offsetX, y + offsetY, 0, value); // Set in mosaic
                }
            }
        }

        // Step 5: Create GridCoverage2D from the mosaic raster
        GridCoverageFactory factory = new GridCoverageFactory();
        return factory.create(title, resultRaster, mosaic.getEnvelope());
    }

    private static class Mosaic {
        private final Envelope2D envelope;

        private final java.util.List<GridCoverage2D> layers = new ArrayList<>();

        Mosaic(GridReaderLayer[] inputLayers) throws IOException {
            Envelope2D combinedEnvelope = null;

            for (GridReaderLayer layer : inputLayers) {
                final GridCoverage2D coverage = layer.getReader().read(null);
                if (combinedEnvelope == null) {
                    combinedEnvelope = coverage.getEnvelope2D();
                } else {
                    combinedEnvelope.add(coverage.getEnvelope2D()); // Merge envelopes
                }
                layers.add(coverage);
            }
            this.envelope =  combinedEnvelope;
        }

        public double getWidth() {
            return envelope.getWidth();
        }

        public double getHeight() {
            return envelope.getHeight();
        }

        public java.util.List<GridCoverage2D> getLayers() {
            return layers;
        }

        public double getMinX() {
            return envelope.getMinX();
        }

        public double getMinY() {
            return envelope.getMinY();
        }

        public Envelope getEnvelope() {
            return envelope;
        }
    }
}
