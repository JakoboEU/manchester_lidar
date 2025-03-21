package jakobo.geo;

import jakobo.properties.SelectedSurveySites;
import org.geotools.data.FeatureReader;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SurveySites {

    private final Map<String, SurveySite> sites;

    public SurveySites(Map<String, SurveySite> grids) {
        this.sites = Collections.unmodifiableMap(grids);
    }

    public static SurveySites loadFromFile(final String filename) throws IOException {
        final FileDataStore surveyGridShapeFile = FileDataStoreFinder.getDataStore(new File(filename));

        final SelectedSurveySites selectedSurveySites = SelectedSurveySites.getSelectedSurveySites();

        final Map<String, SurveySite> surveySites = new HashMap<>();
        final FeatureReader<SimpleFeatureType, SimpleFeature> featureReader = surveyGridShapeFile.getFeatureReader();

        try(featureReader) {
            while (featureReader.hasNext()) {
                final SimpleFeature next = featureReader.next();
                final String surveyGridTitle = next.getAttribute("lg_id") + " " + next.getAttribute("surv_prio");

                if (selectedSurveySites.contains(surveyGridTitle)) {
                    surveySites.put(surveyGridTitle, new SurveySite(surveyGridTitle, next));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        System.out.println("Added " + surveySites.size() + " survey sites.");
        return new SurveySites(surveySites);
    }
}
