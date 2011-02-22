package net.refractions.udig.issues.internal.datastore;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.refractions.udig.issues.IIssue;
import net.refractions.udig.issues.IListStrategy;

import org.eclipse.ui.XMLMemento;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.feature.AttributeType;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.SchemaException;
import org.geotools.filter.AttributeExpression;
import org.geotools.filter.CompareFilter;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.FilterType;
import org.geotools.filter.IllegalFilterException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public abstract class AbstractDatastoreStrategy implements IListStrategy{

    protected boolean tested;
    private FeatureTypeAttributeMapper mapper;
    /**
     * The URL of the datastore
     */
    protected String url;
    /**
     * the FeatureTypeName of the FeatureType to be used
     */
    protected String layer;
    protected FeatureStore featureStore;

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
        FeatureStore featureStore2 = getFeatureStore();
        if( featureStore2==null )
            return false;


        return getAttributeMapper().isValid();
    }

    /**
     * Creates the FeatureType if it doesn't exist. Returns an error if the connection can't be
     * made.
     *
     * @return an error if the connection can't be made or null if the process worked
     */
    protected String createConnection() {
            try {
                mapper = new FeatureTypeAttributeMapper(layer);
            } catch (SchemaException e) {
                throw new RuntimeException("Error creating the FeatureType schema, this is a bug", e);
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
                return "Error creating FeatureType verify that you have write access";
            }

            return null;
    }

    protected abstract DataStore getDataStore() throws IOException;

    protected synchronized FeatureStore getFeatureStore() throws IOException {
        DataStore ds=getDataStore();
        if ( ds==null )
            return null;
        if( featureStore!=null )
            return featureStore;

        List<String> typeNames = Arrays.asList(ds.getTypeNames());
        if( !typeNames.contains(layer) )
            return null;
        FeatureSource fs = ds.getFeatureSource(layer);
        if( fs instanceof FeatureStore )
            featureStore=(FeatureStore) fs;

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

    protected FeatureCollection getFeatures() throws IOException {
        return getFeatureStore().getFeatures();
    }

    public void modifyIssue( final IIssue issue ) throws IOException {
          FeatureType schema = getFeatureStore().getSchema();
          AttributeType[] attributeType=new AttributeType[9];
          Object[] newValues=new Object[9];

          attributeType[0]=schema.getAttributeType(getAttributeMapper().getBounds());
          attributeType[1]=schema.getAttributeType(getAttributeMapper().getDescription());
          attributeType[2]=schema.getAttributeType(getAttributeMapper().getExtensionId());
          attributeType[3]=schema.getAttributeType(getAttributeMapper().getGroupId());
          attributeType[4]=schema.getAttributeType(getAttributeMapper().getId());
          attributeType[5]=schema.getAttributeType(getAttributeMapper().getMemento());
          attributeType[6]=schema.getAttributeType(getAttributeMapper().getPriority());
          attributeType[7]=schema.getAttributeType(getAttributeMapper().getResolution());
          attributeType[8]=schema.getAttributeType(getAttributeMapper().getViewMemento());

          XMLMemento memento=XMLMemento.createWriteRoot("memento"); //$NON-NLS-1$
          XMLMemento viewMemento=XMLMemento.createWriteRoot("viewMemento"); //$NON-NLS-1$

          issue.save(memento);
          issue.getViewMemento(viewMemento);

          GeometryFactory geometryFactory = new GeometryFactory();
          Geometry bounds=null;
          if( issue.getBounds()!=null )
          bounds = geometryFactory.toGeometry(issue.getBounds());
          if( bounds instanceof Polygon && MultiPolygon.class.isAssignableFrom(attributeType[0].getType()) ){
              bounds=geometryFactory.createMultiPolygon(new Polygon[]{ (Polygon)bounds});
          }
          if( bounds instanceof MultiPolygon && Polygon.class.isAssignableFrom(attributeType[0].getType()) ){
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

          FilterFactory factory=FilterFactoryFinder.createFilterFactory();
          try {
              CompareFilter filter = factory.createCompareFilter(FilterType.COMPARE_EQUALS);
              filter.addLeftValue( factory.createAttributeExpression(getAttributeMapper().getId()) );
              filter.addRightValue( factory.createLiteralExpression(issue.getId()) );
              // TODO this is a hack to accomadate the geotools bug.  should be
//              filter.addRightValue(new LiteralExpression(){
//
//                  public Object getLiteral() {
//                      return issue.getId();
//                  }
//
//                  public short getType() {
//                      return ExpressionType.LITERAL_STRING;
//                  }
//
//                  public Object getValue( Feature feature ) {
//                      return issue.getId();
//                  }
//
//                  public void setLiteral( Object literal ) throws IllegalFilterException {
//                  }
//
//                  public void accept( FilterVisitor visitor ) {
//                      visitor.visit((LiteralExpression)this);
//                  }
//
//              });
              getFeatureStore().modifyFeatures(attributeType, newValues, filter);
          } catch (IllegalFilterException e) {
              throw (IOException) new IOException( ).initCause( e );
          }
        }

    public void removeIssues( Collection< ? extends IIssue> changed ) throws IOException {
        FilterFactory factory=FilterFactoryFinder.createFilterFactory();
        AttributeExpression attrExpr = factory.createAttributeExpression(getAttributeMapper().getId());
        Filter filter=null;
        for( IIssue issue : changed ) {
            String id=issue.getId();
            CompareFilter tmp;
            try {
                tmp = factory.createCompareFilter(FilterType.COMPARE_EQUALS);
                tmp.addLeftValue(attrExpr);
                tmp.addRightValue(factory.createLiteralExpression(id));
            } catch (IllegalFilterException e) {
                throw (IOException) new IOException( ).initCause( e );
            }

            if( filter==null )
                filter=tmp;
            else
                filter=filter.or(tmp);

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
