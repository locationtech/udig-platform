/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.export;

import java.util.ArrayList;
import java.util.List;

import org.geotools.feature.DecoratingFeature;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.IllegalAttributeException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;

/**
 * Adapts an existing feature of one SimpleFeature type to another of a "compatible" feature type.  
 * <p>
 * A compatible feature type are feature types that have the same attributes types but maybe in a different order 
 * and the second (the type being adapted to) may have fewer attribute types. 
 * </p>
 * 
 * @author Jesse
 */
class FeatureWrapper extends DecoratingFeature implements SimpleFeature{
    protected final SimpleFeatureType featureType;
    protected final Geometry[] geometry;
    protected final String[] geomAttNames;
    protected Geometry defaultGeometry;

	/**
	 * New instance
	 * @param wrapped SimpleFeature that needs to be adapted to new FeatureType
	 * @param featureType the new feature type.  Must be "compatible" with featureType of wrapped.  
	 * 			See javadocs for class for more information
	 * @param geometries the geometries to use instead of the feature's geometries this allows the geometries to be transformed
	 * @param geomAttNames the attribute names of the geometries so we know how to put them in the attribute array.
	 * 
	 * @throws IllegalAttributeException Thrown if the default geometry does not exist
	 */
    public FeatureWrapper( final SimpleFeature wrapped, final SimpleFeatureType featureType, Geometry[] geometries, String[] geomAttNames ) throws IllegalArgumentException {
        super( wrapped );
        this.featureType=featureType;
        this.geometry=geometries;
        this.geomAttNames=geomAttNames;
        String defaultGeomName = featureType.getGeometryDescriptor().getName().getLocalPart();
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
        return getAttribute(featureType.getDescriptor(index).getName().getLocalPart());
    }

    public final Object getAttribute( String xPath ) {
    	AttributeDescriptor type=featureType.getDescriptor(xPath);
    	
    	if( type instanceof GeometryDescriptor ){
			return findTransformedGeometry(xPath);
    	}
        
        return delegate.getAttribute(xPath);
    }

    static int indexOf( SimpleFeatureType schema, String name ){
    	for( int i=0; i<schema.getAttributeCount(); i++){
    		if( schema.getDescriptor(i).getLocalName().equalsIgnoreCase( name )){
    			return i;
    		}
    	}
    	return -1;
    }

    public ReferencedEnvelope getBounds() {
    	ReferencedEnvelope bounds = new ReferencedEnvelope( delegate.getBounds());
    	if( bounds != null ) return bounds;
    	
    	CoordinateReferenceSystem crs = featureType.getCoordinateReferenceSystem();    
    	
        return new ReferencedEnvelope( defaultGeometry.getEnvelopeInternal(), crs );
    }

    public Geometry getDefaultGeometry() {
        return defaultGeometry;
    }
    public Geometry getPrimaryGeometry() {
    	return getDefaultGeometry();
    }

    public void setPrimaryGeometry(Geometry geometry) throws IllegalAttributeException {
        setDefaultGeometry(geometry);
    }

    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    public String getID() {
        return delegate.getID();
    }

    public int getAttributeCount() {
        return featureType.getAttributeCount();
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

    public void setParent( FeatureCollection<SimpleFeatureType, SimpleFeature> collection ) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<Object> getAttributes() {
        List<Object> attributes = new ArrayList<Object>( delegate.getAttributes());
        for (int i = 0; i < attributes.size(); i++) {
            Object object = attributes.get(i);
            AttributeDescriptor attributeType = delegate.getFeatureType().getDescriptor(i);
            
            
            int index = indexOf( featureType, attributeType.getName().getLocalPart() );
            if( index==-1 )
                continue;
            if( attributeType instanceof GeometryDescriptor ){
                attributes.set(index,findTransformedGeometry(attributeType.getName().getLocalPart()));
            }else{
                attributes.set(index,object);
            }
        }
        return attributes;
    }
}
