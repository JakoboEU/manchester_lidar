package jakobo.util;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.*;
import org.opengis.filter.FilterFactory2;

public class RasterStyle {

    public static Style createDefaultRasterStyle() {
        // Create a StyleFactory
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2();

        // Create a RasterSymbolizer
        RasterSymbolizer rasterSymbolizer = styleFactory.getDefaultRasterSymbolizer();

        // Create a rule with the raster symbolizer
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(rasterSymbolizer);

        // Create a feature type style and add the rule
        FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle();
        featureTypeStyle.rules().add(rule);

        // Create the final style and return
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);

        return style;
    }
}
