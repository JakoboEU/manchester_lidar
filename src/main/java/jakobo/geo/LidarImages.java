package jakobo.geo;

import jakobo.util.BufferedImageFactory;
import jakobo.util.GeoTiffOut;
import jakobo.util.GridCoverage2DSupport;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.MapContent;
import org.geotools.gce.imagemosaic.ImageMosaicReader;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jakobo.util.CrsFactory.crs;
import static jakobo.util.RasterStyle.createDefaultRasterStyle;

public final class LidarImages {

    public static final int MAX_EXPECTED_SATELLITE_TILE_SIZE_METERS = 1000;
    private final List<LidarImage> images;

    private LidarImages(List<LidarImage> images) {
        this.images = images;
    }

    public GridCoverage2D createLidarImage(final ReferencedEnvelope referencedEnvelope) throws IOException {
        if ((referencedEnvelope.getMaxX() - referencedEnvelope.getMinX()) >= MAX_EXPECTED_SATELLITE_TILE_SIZE_METERS ||
                (referencedEnvelope.getMaxY() - referencedEnvelope.getMinY()) >= MAX_EXPECTED_SATELLITE_TILE_SIZE_METERS) {
            throw new IllegalArgumentException("Can only collect layers for envelopes less than 4000x4000m");
        }

        final GridReaderLayer[] layers = this.images.stream()
                // find by x
                .filter(i -> i.getExtent().isYInside(referencedEnvelope.getMinY()) || i.getExtent().isYInside(referencedEnvelope.getMaxY()))
                .filter(i -> i.getExtent().isXInside(referencedEnvelope.getMinX()) || i.getExtent().isXInside(referencedEnvelope.getMaxX()))
                .map(i -> createGridReaderLayer(i.getImageFile()))
                .toArray(size -> new GridReaderLayer[size]);

        if (layers.length == 1) {
            return layers[0].getReader().read(null);
        } else {
            final ImageMosaicReader mosaicReader = new ImageMosaicReader(layers);
            final GridReaderLayer mosaicReaderLayer = new GridReaderLayer(mosaicReader, createDefaultRasterStyle());
            return mosaicReaderLayer.getReader().read(null);
        }
    }

    private GridReaderLayer createGridReaderLayer(final File imageFile) {
        final AbstractGridFormat satelliteFormat = GridFormatFinder.findFormat(imageFile);

        final AbstractGridCoverage2DReader reader = satelliteFormat.getReader(imageFile);
        return new GridReaderLayer(
                reader,
                createDefaultRasterStyle()
        );
    }

    public static LidarImages getLidarImages(final File lidarImageBase, final String filePostfix) throws IOException {
        final Map<String,Extent> contents = getTileExtents();
        System.out.println("Read in contents of " + contents.size() + " tiles.");
        return new LidarImages(toLidarImage(lidarImageBase, filePostfix, contents));
    }

    /*
     * Builds index of x -> y -> tile
     * Where x is left (e.g. min x of tile) and y is bottom (e.g. min y of tile)
     */
    private static List<LidarImage> toLidarImage(final File baseDir, final String filePostfix, final Map<String,Extent> contents) {
        final Map<Extent,LidarImage> index = new HashMap<>();

        for (Map.Entry<String,Extent> entry : contents.entrySet()) {
            if (index.containsKey(entry.getValue())) {
                throw new IllegalStateException("Image already added for extent " + entry.getKey() + ": " + index.get(entry.getValue()) + " and " + entry.getKey());
            }

            index.put(entry.getValue(), new LidarImage(entry.getKey(), entry.getValue(), findTileImageFile(baseDir, filePostfix, entry.getKey())));
        }

        return Collections.unmodifiableList(new ArrayList<>(index.values()));
    }

    private static File findTileImageFile(File tileDir, String filePostfix, String tileName) {
        if (tileName.startsWith("sd")) {
            final File f = new File(new File(tileDir, "sd"), tileName + filePostfix);

            if (f.exists()) {
                return f;
            }
        }

        if (tileName.startsWith("sj")) {
            final File f = new File(new File(tileDir, "sj"), tileName + filePostfix);

            if (f.exists()) {
                return f;
            }
        }

        throw new IllegalArgumentException("Failed to find image file for tile " + tileName + " in " + tileDir);
    }

    private static Map<String, Extent> getTileExtents() {
        final BufferedReader in = new BufferedReader(new InputStreamReader(LidarImages.class.getClassLoader().getResourceAsStream("contents_order.txt")));

        final Map<String,Extent> result = new HashMap<>();

        final Pattern TILE_NAME_LINE = Pattern.compile("Tile Name: {8}([a-z]{2}[0-9]{4})");
        // Extent: left, bottom -  right, top
        // where left < right && bottom < top
        final Pattern EXTENT_LINE = Pattern.compile("Extent: {11}([0-9]{6}\\.0), ([0-9]{6}\\.0) - ([0-9]{6}\\.0), ([0-9]{6}\\.0)");

        try (in) {
            String line;
            while((line = in.readLine()) != null) {
                final Matcher tileMatcher = TILE_NAME_LINE.matcher(line);

                if (tileMatcher.matches()) {
                    final String expectedExtentLine = in.readLine();
                    final Matcher extentMatcher = EXTENT_LINE.matcher(expectedExtentLine);

                    if (!extentMatcher.matches()) {
                        throw new IllegalStateException("Expected 'Tile Name:' line to be followed by 'Extent:' line. But '" + expectedExtentLine + "' does not match.");
                    }

                    result.put(tileMatcher.group(1), new Extent(
                        Double.parseDouble(extentMatcher.group(4)), // top
                        Double.parseDouble(extentMatcher.group(2)), // bottom
                        Double.parseDouble(extentMatcher.group(1)), // left
                        Double.parseDouble(extentMatcher.group(3))  // right
                    ));
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Error reading contents file", ioe);
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Require location of satellite tiles to run.");
        }

        final ReferencedEnvelope bounds = new ReferencedEnvelope(381350.0, 381370.0, 403530.0, 403550.0,  crs());

        final LidarImages factory = getLidarImages(new File(args[0]), "_DTM_25CM.asc");
        GeoTiffOut.saveRaster(
                factory.createLidarImage(bounds),
                "output/test.tif"
        );
    }
}
