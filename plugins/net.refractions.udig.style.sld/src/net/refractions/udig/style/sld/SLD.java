package net.refractions.udig.style.sld;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ILayer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.TextSymbolizer;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public enum SLD {
    POINT(PointSymbolizer.class) {

        public ImageDescriptor createImageDescriptor() {
            return StyleGlyph.point(SWT.COLOR_GREEN, 7);
        }

        public ImageDescriptor createDisabledImageDescriptor() {
            return StyleGlyph.point(SWT.COLOR_GRAY, 7);
        }
        // TODO: inline this?
        public PointSymbolizer createDefault() {
            return SLDContent.createPointSymbolizer(null);
        }
        public boolean supports( ILayer layer ) {
            FeatureType featureType = layer.getSchema();
            if (featureType == null) return false;

            return isPoint( featureType.getDefaultGeometry() );
        }
    },
    LINE(LineSymbolizer.class) {
        public ImageDescriptor createImageDescriptor() {
            return StyleGlyph.line(SWT.COLOR_BLACK, 2);
        }

        public ImageDescriptor createDisabledImageDescriptor() {
            return StyleGlyph.line(SWT.COLOR_GRAY, 2);
        }
        public LineSymbolizer createDefault() {
            return SLDContent.createLineSymbolizer(null);
        }

        public boolean supports( ILayer layer ) {
            return isLine( layer.getSchema() );
        }
    },
    POLYGON(PolygonSymbolizer.class) {
        public ImageDescriptor createImageDescriptor() {
            return StyleGlyph.polygon(SWT.COLOR_BLACK, SWT.COLOR_RED, 1);
        }

        public ImageDescriptor createDisabledImageDescriptor() {
            return StyleGlyph.polygon(SWT.COLOR_BLACK, SWT.COLOR_GRAY, 1);
        }

        public PolygonSymbolizer createDefault() {
            return SLDContent.createPolygonSymbolizer(null);
        }

        public boolean supports( ILayer layer ) {
        	return isPolygon( layer.getSchema() );
        }
    },
    TEXT(TextSymbolizer.class) {
        public ImageDescriptor createImageDescriptor() {
            return StyleGlyph.text(SWT.COLOR_BLACK, 2);
        }

        public ImageDescriptor createDisabledImageDescriptor() {
            return StyleGlyph.text(SWT.COLOR_GRAY, 2);
        }

        public TextSymbolizer createDefault() {
            return SLDContent.createTextSymbolizer();
        }

        public boolean supports( ILayer layer ) {
            FeatureType featureType = layer.getSchema();
            if (featureType == null) return false;

            //TODO: Can a text symbolizer be applied to a raster?
            return true;
        }
    },
    RASTER(RasterSymbolizer.class) {
        public ImageDescriptor createImageDescriptor() {
            return StyleGlyph.raster(SWT.COLOR_BLACK, SWT.COLOR_RED, 1);
        }

        public ImageDescriptor createDisabledImageDescriptor() {
            return StyleGlyph.raster(SWT.COLOR_BLACK, SWT.COLOR_GRAY, 1);
        }

        public Object createDefault() {
            return SLDContent.createRasterSymbolizer();
        }

        public boolean supports( ILayer layer ) {
            return layer.hasResource(org.geotools.data.ows.Layer.class);
        }
    };

    //private static SLD[] all = new SLD[]{POINT, LINE, POLYGON, RASTER, TEXT};

    Class type;

    SLD( Class object ) {
        this.type = object;
    }

    public ImageDescriptor createImageDescriptor() {
        return null;
    }

    public ImageDescriptor createDisabledImageDescriptor() {
        return null;
    }

    public Object createDefault() {
        return null;
    }

    /**
     * Determines if the style component supports the specified feature type.
     *
     * @param layer Feature type, must not be null and must have a valid geometry type attribute.
     * @return true if the feature type is supported, otherwise false.
     */
    public boolean supports( ILayer layer ) {
        return false;
    }

    public static <T> T createDefault( Class<T> theClass ) {
        SLD sld = get(theClass);
        if (sld != null) {
            return theClass.cast( sld.createDefault() );
        }
        return null;
    }

    public static ImageDescriptor createImageDescriptor( Class theClass ) {
        SLD symbolizer = get(theClass);
        if (symbolizer != null)
            return symbolizer.createImageDescriptor();
        return null;
    }

    public static ImageDescriptor createDisabledImageDescriptor( Class theClass ) {
        SLD object = get(theClass);
        if (object != null)
            return object.createDisabledImageDescriptor();
        return null;
    }

    /**
     * Returns the types of SLD style components that support the specified feature type. This
     * method returns an empty list if no style types support the specified feature type.
     *
     * @param layer the layer.
     * @return A list (possibly empty) of types that support the feature type.
     */
    public static List<Class> getSupportedTypes( ILayer layer ) {
        ArrayList<Class> l = new ArrayList<Class>();
        for( SLD sld : SLD.values() ){
        	if( sld.supports( layer )) l.add( sld.type );
        }

        return l;
    }

    @SuppressWarnings("unchecked")
	private static SLD get( Class symbolizer ) {
    	for( SLD sld : SLD.values() ){
    		if( symbolizer.isAssignableFrom( sld.type ) ) {
				return sld;
			}
    	}
    	/*
        if (PointSymbolizer.class.isAssignableFrom( symbolizer ))
            return POINT;
        if (LineSymbolizer.class.isAssignableFrom( symbolizer ))
            return LINE;
        if (PolygonSymbolizer.class.isAssignableFrom( symbolizer ))
            return POLYGON;
        if (TextSymbolizer.class.isAssignableFrom( symbolizer ))
            return TEXT;
        if (RasterSymbolizer.class.isAssignableFrom(symbolizer))
            return RASTER;
            */
        return null;
    }

	public static final boolean isPolygon( FeatureType featureType ){
		if( featureType == null ) return false;
		return isPolygon( featureType.getDefaultGeometry() );
	}
    /* This needed to be a function as it was being writen poorly everywhere */
	public static final boolean isPolygon( GeometryAttributeType geometryType ){
		if( geometryType == null ) return false;
		Class type = geometryType.getType();
		return Polygon.class.isAssignableFrom( type ) ||
		       MultiPolygon.class.isAssignableFrom( type );
	}
	public static final boolean isLine( FeatureType featureType ){
		if( featureType == null ) return false;
		return isLine( featureType.getDefaultGeometry() );
	}
    /* This needed to be a function as it was being writen poorly everywhere */
	public static final boolean isLine( GeometryAttributeType geometryType ){
		if( geometryType == null ) return false;
		Class type = geometryType.getType();
		return LineString.class.isAssignableFrom( type ) ||
		       MultiLineString.class.isAssignableFrom( type );
	}
	public static final boolean isPoint( FeatureType featureType ){
		if( featureType == null ) return false;
		return isPoint( featureType.getDefaultGeometry() );
	}
    /* This needed to be a function as it was being writen poorly everywhere */
	public static final boolean isPoint( GeometryAttributeType geometryType ){
		if( geometryType == null ) return false;
		Class type = geometryType.getType();
		return Point.class.isAssignableFrom( type ) ||
		       MultiPoint.class.isAssignableFrom( type );
	}
}
