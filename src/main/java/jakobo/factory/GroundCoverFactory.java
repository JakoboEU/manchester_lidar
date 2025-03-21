package jakobo.factory;

import jakobo.util.GridCoverage2DSupport;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.jts.ReferencedEnvelope;

import java.awt.image.*;

public class GroundCoverFactory {

    public static GridCoverage2D createGroundCover(String title, GridCoverage2D terrain, GridCoverage2D surface, ReferencedEnvelope bounds) {
        // Create output raster
//        final WritableRaster terrainWritableRaster = terrain.getRaster();
//        final WritableRaster surfaceWritableRaster = surface.getRaster();
//
//        // Perform pixel-wise subtraction
//        double[] rasterValues = new double[terrain.getWidth() * terrain.getHeight()];
//
//        for (int y = 0; y < terrainWritableRaster.getHeight(); y++) {
//            for (int x = 0; x < terrainWritableRaster.getWidth(); x++) {
//                final double terrainHeight = terrainWritableRaster.getSampleDouble(x, y, 0);
//                final double surfaceHeight = surfaceWritableRaster.getSampleDouble(x, y, 0);
//
//                final double groundCoverHeight = surfaceHeight - terrainHeight;
//                rasterValues[y * terrain.getWidth() + x] = groundCoverHeight;
//
//                if (groundCoverHeight < 0) {
//                    System.out.println(title);
//                }
//            }
//        }
//
//        final DataBufferDouble dataBuffer = new DataBufferDouble(rasterValues, rasterValues.length);
//        SampleModel sampleModel = new BandedSampleModel(DataBuffer.TYPE_DOUBLE, terrain.getWidth(), terrain.getHeight(), 1);
//        return GridCoverage2DSupport.fromWritableRaster(Raster.createWritableRaster(sampleModel, dataBuffer, null), bounds);
        return terrain;
    }
}
