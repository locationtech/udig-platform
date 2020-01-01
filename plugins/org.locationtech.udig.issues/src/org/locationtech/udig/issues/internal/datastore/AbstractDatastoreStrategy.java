/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal.datastore;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.ui.XMLMemento;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.udig.issues.IIssue;
import org.locationtech.udig.issues.IListStrategy;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;

public abstract class AbstractDatastoreStrategy implements IListStrategy{

    protected boolean tested;
    private FeatureTypeAttributeMapper mapper;
    /**
     * The URL of the datastore
     */
    protected String url;
    /**
     * the FeatureTypeName of the SimpleFeatureType to be used
     */
    protected String layer;
    protected FeatureStore<SimpleFeatureType, SimpleFeature> featureStore;

    public AbstractDatastoreStrategy() {
        super();
    }

    /**
     * Tests that the datstore and its feature source exist and are 
     * correctly configured for the list to use.
     *
     * @return true if the list is ready to use.
     * @throws IOException
     */
    protected boolean testConnection() throws IOException {
    	FeatureStore<SimpleFeatureType, SimpleFeature> featureStore2 = getFeatureStore();
        if( featureStore2==null )
            return false;
        
    
        return getAttributeMapper().isValid();
    }
    
    /**
     * Creates the SimpleFeatureType if it doesn't exist. Returns an error if the connection can't be
     * made.
     * 
     * @return an error if the connection can't be made or null if the process worked
     */
    protected String createConnection() {
            try {
                mapper = new FeatureTypeAttributeMapper(layer);
            } catch (SchemaException e) {
                throw new RuntimeException("Error creating the SimpleFeatureType schema, this is a bug", e);
            }
            
            assert mapper.isValid();

            DataStore dataStore;
            try {
                dataStore = getDataStore();
            } catch (IOException e) {
                return "Unable to connect to the datastore, check connection parameters";
            }
            
            try {
                if( Arrays.asList(dataStore.getTypeNames()).contains(layer)){
                    return "Type exists choose a new name";
                }
            } catch (IOException e) {
                return "Error communicating with datastore, check connection and availability of service";
            }
            
            try {
                dataStore.createSchema(mapper.getSchema());
            } catch (IOException e) {
                return "Error creating SimpleFeatureType verify that you have write access";
            }
            
            return null;
    }
    
    protected abstract DataStore getDataStore() throws IOException;

    protected synchronized FeatureStore<SimpleFeatureType, SimpleFeature> getFeatureStore() throws IOException {
        DataStore ds=getDataStore();
        if ( ds==null )
            return null;
        if( featureStore!=null )
            return featureStore;
        
        List<String> typeNames = Arrays.asList(ds.getTypeNames());
        if( !typeNames.contains(layer) )
            return null;
        FeatureSource<SimpleFeatureType, SimpleFeature> fs = ds.getFeatureSource(layer);
        if( fs instanceof FeatureStore )
            featureStore=(FeatureStore<SimpleFeatureType, SimpleFeature>) fs;
        
        return featureStore;
    }

    public FeatureTypeAttributeMapper getAttributeMapper() throws IOException {
        if( mapper==null || mapper.getSchema()!=getFeatureStore().getSchema()){
            mapper=new FeatureTypeAttributeMapper(getFeatureStore().getSchema());
        }
        return mapper;
    }

    public Collection<? extends IIssue> getIssues() throws IOException {
        return new FeatureCollectionToIssueCollectionAdapter(getFeatures(), getAttributeMapper());
    }

    protected FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures() throws IOException {
        return getFeatureStore().getFeatures();
    }

    public void modifyIssue( final IIssue issue ) throws IOException {
          SimpleFeatureType schema = getFeatureStore().getSchema();
          Name[] attributeType=new Name[9];
          Object[] newValues=new Object[9];
          
          AttributeDescriptor desc = schema.getDescriptor(getAttributeMapper().getBounds());
          attributeType[0]=schema.getDescriptor(getAttributeMapper().getBounds()).getName();
          attributeType[1]=schema.getDescriptor(getAttributeMapper().getDescription()).getName();
          attributeType[2]=schema.getDescriptor(getAttributeMapper().getExtensionId()).getName();
          attributeType[3]=schema.getDescriptor(getAttributeMapper().getGroupId()).getName();
          attributeType[4]=schema.getDescriptor(getAttributeMapper().getId()).getName();
          attributeType[5]=schema.getDescriptor(getAttributeMapper().getMemento()).getName();
          attributeType[6]=schema.getDescriptor(getAttributeMapper().getPriority()).getName();
          attributeType[7]=schema.getDescriptor(getAttributeMapper().getResolution()).getName();
          attributeType[8]=schema.getDescriptor(getAttributeMapper().getViewMemento()).getName();
          
          XMLMemento memento=XMLMemento.createWriteRoot("memento"); //$NON-NLS-1$
          XMLMemento viewMemento=XMLMemento.createWriteRoot("viewMemento"); //$NON-NLS-1$
          
          issue.save(memento);
          issue.getViewMemento(viewMemento);
          
          GeometryFactory geometryFactory = new GeometryFactory();
          Geometry bounds=null;
          if( issue.getBounds()!=null )
          bounds = geometryFactory.toGeometry(issue.getBounds());
          if( bounds instanceof Polygon && MultiPolygon.class.isAssignableFrom(desc.getType().getBinding()) ){
              bounds=geometryFactory.createMultiPolygon(new Polygon[]{ (Polygon)bounds});
          }
          if( bounds instanceof MultiPolygon && Polygon.class.isAssignableFrom(desc.getType().getBinding()) ){
              bounds=((MultiPolygon)bounds).getGeometryN(0);
          }
          
          newValues[0]=bounds;
          newValues[1]=issue.getDescription();
          newValues[2]=issue.getExtensionID();
          newValues[3]=issue.getGroupId();
          newValues[4]=issue.getId();
          newValues[5]=memento;
          newValues[6]=issue.getPriority();
          newValues[7]=issue.getResolution();
          newValues[8]=viewMemento;
          
          FilterFactory factory = CommonFactoryFinder.getFilterFactory(GeoTools
				.getDefaultHints());
		Expression expr1 = factory.property(getAttributeMapper().getId());
		Expression expr2 = factory.literal(issue.getId());
		PropertyIsEqualTo filter = factory.equals(expr1, expr2);
		getFeatureStore().modifyFeatures(attributeType, newValues, filter);
	}


    public void removeIssues( Collection< ? extends IIssue> changed ) throws IOException {
        FilterFactory factory=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());

		Expression expr1 = factory.property(getAttributeMapper().getId());
		
		Filter filter = null;
		
        for( IIssue issue : changed ) {
            String id=issue.getId();
            Expression expr2 = factory.literal(id);
            PropertyIsEqualTo tmp = factory.equals(expr1, expr2);
            
            if( filter==null )
                filter=tmp;
            else
                filter=factory.or(filter, tmp);
            
        }
        if( filter!=null ){
            getFeatureStore().removeFeatures(filter);
        }
    }

    public void addIssues( List< ? extends IIssue> changed ) throws IOException {
        getFeatureStore().addFeatures(new IssuesCollectionToFeatureCollection(changed, getAttributeMapper()));
    }

    /**
     * @return Returns the layer.
     */
    public String getFeatureTypeName() {
        return layer;
    }

    /**
     * @param featureTypeName The layer to set.
     */
    public void setFeatureTypeName( String featureTypeName ) {
        this.layer = featureTypeName;
    }

    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url to set.
     */
    public void setUrl( String url ) {
        this.url = url;
    }

}
