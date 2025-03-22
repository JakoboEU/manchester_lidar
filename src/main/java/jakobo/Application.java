package jakobo;

import jakobo.factory.GroundCoverFactory;
import jakobo.geo.LidarImages;
import jakobo.geo.SurveySites;
import jakobo.util.GeoTiffOut;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.jts.ReferencedEnvelope;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class Application {

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Expected to be called with 2 arguments: [directory containing terrain rasters] [directory containing surface rasters] [survey squares shape file]");
        }

        final File terrainRasterDir = verifyDirectory("terrain", args[0]);
        final File surfaceRasterDir = verifyDirectory("surface", args[1]);

        System.out.println("Reading terrain data from " + terrainRasterDir.getAbsolutePath());
        System.out.println("Reading surface data from " + surfaceRasterDir.getAbsolutePath());

        final SurveySites surveyGrids = SurveySites.loadFromFile(args[2]);
        final LidarImages terrain = LidarImages.getLidarImages(terrainRasterDir, "_DTM_25CM.asc");
        final LidarImages surface = LidarImages.getLidarImages(surfaceRasterDir, "_DSM_25CM.asc");

        surveyGrids.sites().forEach(site -> {
            final ReferencedEnvelope bounds = site.toReferencedEnvelope(0);
            try {
                Optional<GridCoverage2D> terrainImage = terrain.createLidarImage(site.getTitle(), bounds);
                Optional<GridCoverage2D> surfaceImage = surface.createLidarImage(site.getTitle(), bounds);
                if (terrainImage.isPresent() && surfaceImage.isPresent()) {
                    GeoTiffOut.saveRaster(
                            GroundCoverFactory.createGroundCover(
                                    site.getTitle(),
                                    terrainImage.get(),
                                    surfaceImage.get(),
                                    bounds
                            ),
                            "./output/" + site.getTitle() + ".tif"
                    );
                } else {
                    System.err.println("Could not create ground cover for " + site.getTitle() + " as missing lidar imagery.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static File verifyDirectory(final String type, final String directory) throws FileNotFoundException {
        final File rasterDir = new File(directory);

        if (!rasterDir.isDirectory() || !rasterDir.exists()) {
            throw new FileNotFoundException(directory + " does not exist or is not a directory, expected it to contain rasters for " + type);
        }

        validateRasterDirectoryContents(rasterDir, "sd");
        validateRasterDirectoryContents(rasterDir, "sj");

        return rasterDir;
    }

    private static void validateRasterDirectoryContents(final File rasterDir, final String grid) throws FileNotFoundException {
        final File gridRasterDir = new File(rasterDir, grid);
        if (!gridRasterDir.isDirectory() || !gridRasterDir.exists()) {
            throw new FileNotFoundException(rasterDir.getName() + " does not contain an '" + grid + "' directory");
        } else {
            final String[] rasterFiles = gridRasterDir.list((dir, filename) -> filename.endsWith(".asc"));

            if (rasterFiles.length == 0) {
                throw new FileNotFoundException(gridRasterDir.getName() + " contains '" + grid + "' directory, but the .asc raster files are missing.");
            }
        }
    }
}
