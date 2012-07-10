GeoTools 2.5 for uDig Developers
================================

The formal API changes are documented on the GeoTools website:

* :doc:`Upgrade to GeoTools 2.5`

* :doc:`Upgrade to GeoTools 2.4`

* :doc:`Upgrade to GeoTools 2.3`


This page contains a list of the modifications required to the uDig codebase when transitioning from
GeoTools 2.2 to GeoTools 2.5.

Use of AttributeType.getRestriction()
-------------------------------------

::

    //return attributeType.getRestriction().accepts(feature);
    return attributeType.getRestriction().evaluate(feature);

Use of org.geotools.referencing.CRS
-----------------------------------

::

    //MathTransform mt = CRS.transform(coordCRS, destinationCRS, true);
    MathTransform mt = CRS.findMathTransform(coordCRS, destinationCRS, true);

Extension of org.geotools.util.ProgressListener
-----------------------------------------------

Implementations will need to add the following method:

::

    return new ProgressListener(){
       ...
       public void setTask( InternationalString task ) {
           setDescription( task.toString() );
       }
    }

Any custom extensions of org.geotools.filter.AbstractFilter
-----------------------------------------------------------

::

    class SelectionFilteretends AbstractFilter implements FidFilter {   
        // #1 constructor needs filter factory
        SelectionFilter(Collection<String> fids){    
           super( (FilterFactory) CommonFactoryFinder.getFilterFactory(null));
           ....
        }
        // #2 contains is now final, and a calls evaulate    
        //
        // public boolean contains( Feature feature ) {
        public boolean evaulate( Feature feature ) {
            return selectionFids.contains(feature.getID());
        }

        // #3 remove boilerplate visitor code
        //
        //public void accept( FilterVisitor arg0 ) {
        //   arg0.visit(this);
        //}

       // #4 implement the helper methods using provided factory
       public Filter and( org.opengis.filter.Filter filter ) {
           return factory.and( this, filter );
       }
       public Filter or( org.opengis.filter.Filter filter ) {
           return (Filter) factory.or( this, filter );
       }
       // #5 meet any new GeoAPI contracts
       public Set<Identifier> getIdentifiers() {
           Set<Identifier> ids = new HashSet<Identifier>();
           for( String fid : selectionFids ){
              ids.add( new FeatureIdImpl( fid ));
           }
           return ids;
       }
    }

Any use of org.geotools.referencing FactoryFinder
-------------------------------------------------

::

    // load(FactoryFinder.getCoordinateOperationAuthorityFactories());
    load(FactoryFinder.getCoordinateOperationAuthorityFactories( null ));

    // load(FactoryFinder.getCoordinateOperationAuthorityFactories());
    load(FactoryFinder.getCoordinateOperationAuthorityFactories( null ));

    // load(FactoryFinder.getCRSFactories());
    load(FactoryFinder.getCRSFactories(null));

    // load(FactoryFinder.getCSFactories());
    load(FactoryFinder.getCSFactories(null));

    // load(FactoryFinder.getDatumAuthorityFactories());
    load(FactoryFinder.getDatumAuthorityFactories(null));

    // load(FactoryFinder.getDatumFactories());
    load(FactoryFinder.getDatumFactories(null));

    // load(FactoryFinder.getMathTransformFactories());
    load(FactoryFinder.getMathTransformFactories(null));

FilterHandler
-------------

Change to org.opengis.filter.Filter

::

    public static class SimpleFilterHandler extends DefaultHandler implements FilterHandler {
        private org.opengis.filter.Filter filter;
        public void filter( org.opengis.filter.Filter filter ) {
            this.filter = filter;
        }
        public org.opengis.filter.Filter getFilter() {
            return filter;
        }
    }

CustomClassifierFunction missing
--------------------------------

No replacement available? Apparently this was a bad idea:

::

    \\customBreak = new CustomClassifierFunction();

StreamingRenderer.DEFAULT\_LISTENER
-----------------------------------

::

    //renderer.removeRenderListener(StreamingRenderer.DEFAULT_LISTENER);

GeodeticCalculator
------------------

::

    //calc.setAnchorPosition(new DirectPosition2D(min.x, min.y));
    calc.setStartingGeographicPoint(new DirectPosition2D(min.x, min.y));

GridCoverageExchange
--------------------

::

    //import org.geotools.coverage.grid.GridCoverageExchange;
    import org.opengis.coverage.grid.GridCoverageExchange;

JTS (&JTS.ReferenceEnvelope)
----------------------------

::

    //import org.geotools.geometry.JTS;
    import org.geotools.geometry.jts.JTS;
    import org.geotools.geometry.jts.ReferencedEnvelope;

    ...

    //Envelope envelope = JTS.empty();
    Envelope envelope = new Envelope();

    return new ReferencedEnvelope( envelope, crs );

CRSUtilities.getEnvelope moved to CRS.getEnvelope
-------------------------------------------------

No idea what to replace this with!

::

    //envelope = CRSUtilities.getEnvelope(crs);
    envelope = CRS.getEnvelope(crs);

FeatureCollection.reader()
--------------------------

BEFORE

::

    for( FeatureReader iter = source.getFeatures().reader(); iter.hasNext();){
       Feature element = iter.next();
       ...
    }

AFTER

::

    FeatureIterator iter = source.getFeatures().features();
    try {
        while( iter.hasNext() ){
            Feature element = iter.next();
            ...
        }
    }finally {
        iter.close();
    }

CRS.decode now throws FactoryException!
---------------------------------------

::

    try {
        crs = CRS.decode("EPSG:4326"); //$NON-NLS-1$
    } catch (NoSuchAuthorityCodeException e) {
        throw (IOException) new IOException(
            Messages.WMSGeoResourceImpl_bounds_unavailable
        ).initCause( e ); 
    } catch (FactoryException e) { // required for geotools 2.4
                    throw (RuntimeException) new RuntimeException(
                            Messages.WMSGeoResourceImpl_bounds_unavailable
                    ).initCause( e );
                }

WorldImageFormat.ENVELOPE
-------------------------

This parameter no longer appears to be supported?

::

    ParameterDescriptor env = WorldImageFormat.ENVELOPE;

ValidationProcess.runFeatureTests
---------------------------------

Simplified to work with FeatureCollection directly:

::

    //runFeatureTests( dataStoreID, type, collection.reader(), results );
    runFeatureTests( dataStoreID, collection, results );

GridCoverageRenderer Moved
--------------------------

::

    //import org.geotools.renderer.lite.GridCoverageRenderer;
    import org.geotools.renderer.lite.gridcoverage2d.GridCoverageRenderer;

GridCoverageRenderer has Different API
--------------------------------------

AFTER:

::

    CoordinateReferenceSystem destinationCRS = getContext().getCRS();
                                  
    Envelope envelope = getRenderBounds();
    if( envelope == null || envelope.isNull()){
        envelope = getContext().getViewportModel().getBounds();
    }
    Point upperLeft = getContext().worldToPixel( new Coordinate( envelope.getMinX(), envelope.getMinY()) );
    Point bottomRight = getContext().worldToPixel( new Coordinate( envelope.getMaxX(), envelope.getMaxY()) );
    Rectangle screenSize = new Rectangle( upperLeft );
    screenSize.add( bottomRight );
                    
    GridCoverageRenderer paint = new GridCoverageRenderer( destinationCRS, envelope, screenSize );
                 
    RasterSymbolizer symbolizer = CommonFactoryFinder.getStyleFactory(null).createRasterSymbolizer();
                    
    paint.paint( graphics, coverage, symbolizer );

FeatureCollection.collection() removed
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

To quickly preserver old functionality make a copy:

::

    // return features.collection();
    return DataUtilities.collection( features );

Update FactoryFinder
^^^^^^^^^^^^^^^^^^^^

Although covered by the geotools page it happens often enough that this example is useful:

::

    //LogicFilter logicFilter;
    Filter logicFilter;

    //logicFilter = createFilterFactory.createLogicFilter(oldFilter, filter, FilterType.LOGIC_OR);
    logicFilter = createFilterFactory.and(oldFilter, filter);

