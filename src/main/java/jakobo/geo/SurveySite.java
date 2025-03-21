package jakobo.geo;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

import static jakobo.util.CrsFactory.crs;

public class SurveySite {

    private final String title;
    private final SimpleFeature feature;

    public SurveySite(String title, SimpleFeature polygon) {
        this.title = title;
        this.feature = polygon;
    }

    public ReferencedEnvelope toReferencedEnvelope(final double areaBuffer) {
        final Double left = (Double) feature.getAttribute("left");
        final Double right = (Double) feature.getAttribute("right");
        final Double bottom = (Double) feature.getAttribute("bottom");
        final Double top = (Double) feature.getAttribute("top");

        return new ReferencedEnvelope(left - areaBuffer, right + areaBuffer, bottom - areaBuffer, top + areaBuffer, crs());
    }

    public String getTitle() {
        return title;
    }
}
