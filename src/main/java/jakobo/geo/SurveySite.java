package jakobo.geo;

import org.opengis.feature.simple.SimpleFeature;

public class SurveySite {
    private final String title;
    private final SimpleFeature polygon;

    public SurveySite(String title, SimpleFeature polygon) {
        this.title = title;
        this.polygon = polygon;
    }
}
