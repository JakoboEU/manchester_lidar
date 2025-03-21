package jakobo;

import jakobo.geo.LidarImages;
import jakobo.geo.SurveySites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
