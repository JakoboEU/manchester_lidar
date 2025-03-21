package jakobo.util;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class CrsFactory {

    private static final CoordinateReferenceSystem _CRS_;

    static {
        try {
            _CRS_ = CRS.decode("EPSG:27700");
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }

    public static CoordinateReferenceSystem crs() {
        return _CRS_;
    }
}
