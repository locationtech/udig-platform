/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.ui.export;

import net.refractions.udig.catalog.CatalogPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.type.GeometricAttributeType;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
/**
 * Adapts an existing feature of one Feature type to another of a "compatible" feature type.
 * <p>
 * A compatible feature type are feature types that have the same attributes types but maybe in a different order
 * and the second (the type being adapted to) may have fewer attribute types.
 * </p>
 *
 * @author Jesse
 */
class FeatureWrapper implements Feature{
    protected final Feature wrapped;
    protected final FeatureType featureType;
    protected final Geometry[] geometry;
    protected final String[] geomAttNames;
    protected Geometry defaultGeometry;

	/**
	 * New instance
	 * @param wrapped Feature that needs to be adapted to new FeatureType
	 * @param featureType the new feature type.  Must be "compatible" with featureType of wrapped.
	 * 			See javadocs for class for more information
	 * @param geometries the geometries to use instead of the feature's geometries this allows the geometries to be transformed
	 * @param geomAttNames the attribute names of the geometries so we know how to put them in the attribute array.
	 *
	 * @throws IllegalAttributeException Thrown if the default geometry does not exist
	 */
    public FeatureWrapper( final Feature wrapped, final FeatureType featureType, Geometry[] geometries, String[] geomAttNames ) throws IllegalArgumentException {
        super();
        this.wrapped = wrapped;
        this.featureType=featureType;
        this.geometry=geometries;
        this.geomAttNames=geomAttNames;
        String defaultGeomName = featureType.getDefaultGeometry().getName();
        this.defaultGeometry=findTransformedGeometry(defaultGeomName);
    }

    private Geometry findTransformedGeometry(String defaultGeomName) throws IllegalArgumentException  {
    	for (int i = 0; i < geomAttNames.length; i++) {
			String attName = geomAttNames[i];
			if( attName.equals(defaultGeomName) )
				return geometry[i];
		}
		throw new IllegalArgumentException ("Attribute does not exist:"+defaultGeomName); //$NON-NLS-1$
	}

	public final Object getAttribute( int index ) {
        return getAttribute(featureType.getAttributeType(index).getName());
    }

    public Object getAttribute( String xPath ) {
    	AttributeType type=featureType.getAttributeType(xPath);

    	if( type instanceof GeometryAttributeType ){
			return findTransformedGeometry(xPath);
    	}

        return wrapped.getAttribute(xPath);
    }

    public Object[] getAttributes( Object[] attributes ) {
    	if( attributes==null ||  attributes.length==0){
    		attributes = new Object[featureType.getAttributeCount()];
    	}
    	if( attributes.length<featureType.getAttributeCount() )
    		throw new IllegalArgumentException("There needs to be at least "+featureType.getAttributeCount()+" elements in the provided array"); //$NON-NLS-1$ //$NON-NLS-2$

    	Object[] atts = new Object[wrapped.getNumberOfAttributes()];
        atts=wrapped.getAttributes(atts);
        for (int i = 0; i < atts.length; i++) {
			Object object = atts[i];
			AttributeType attributeType = wrapped.getFeatureType().getAttributeType(i);
			int index = featureType.find(attributeType.getName());
			if( index==-1 )
				continue;
			if( attributeType instanceof GeometricAttributeType ){
				attributes[index]=findTransformedGeometry(attributeType.getName());
			}else{
				attributes[index]=object;
			}
		}
        return attributes;
    }

    public Envelope getBounds() {
        return defaultGeometry.getEnvelopeInternal();
    }

    public Geometry getDefaultGeometry() {
        return defaultGeometry;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public String getID() {
        return wrapped.getID();
    }

    public int getNumberOfAttributes() {
        return featureType.getAttributeCount();
    }

    @SuppressWarnings("deprecation")
    public FeatureCollection getParent() {
        return wrapped.getParent();
    }

    public void setAttribute( int position, Object val ) throws IllegalAttributeException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException();
    }

    public void setAttribute( String xPath, Object attribute ) throws IllegalAttributeException {
        throw new UnsupportedOperationException();
    }

    public void setDefaultGeometry( Geometry geometry ) throws IllegalAttributeException {
        throw new UnsupportedOperationException();
    }

    public void setParent( FeatureCollection collection ) {
        throw new UnsupportedOperationException();
    }
}
