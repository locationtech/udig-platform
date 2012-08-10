Interceptors
~~~~~~~~~~~~

Interceptors are a really interesting way to "hook into" the operation of the running uDig
application.

Related:

* :doc:`service_and_georesource_interceptors`


Layer Interceptors
^^^^^^^^^^^^^^^^^^

Layer interceptors are used to process a layer during one of the following lifecycle activities:

-  layerCreated: Called when a layer is created; good for configuring the layer
   blackboard/styleblackboard for your application.
-  layerAdded: Called when a layer is added to the map; can be used to hook up any additional
   listeners or logic; or simply adjust where in the layer list the layer appears; or perform a
   security check and hide sensitive information
-  layerRemoved: Called when the layer is being removed; can clean up any additional listeners or
   logic associated with the layer

Examples of layer interceptor in udig:

::

    LayerInterceptor
    - InitMapBoundsInterceptor
    - InitMapCRS
    - SetLayerNameInterceptor
    - SetStyleInterceptor

Example RasterLayerInterceptor
''''''''''''''''''''''''''''''

Thanks to Jim for the following of taking a new raster layer and makes it drop down the layer list
until it reaches the first raster layer. It is an example taken from a custom application where
users did not want rasters covering up their point, line and polygon layers.

To hook up the interceptor:

::

    <extension
          id="net.refractions.udig.tutorials.examples.layerInterceptor"
          point="net.refractions.udig.project.layerInterceptor">
       <layerAdded
             class="net.refractions.udig.tutorials.RasterToBottomOfZOrder"
             id="net.refractions.udig.tutorials.examples.interceptors.rasterToBottomOfZOrder">
       </layerAdded>
    </extension>

Here is the Java code:

::

    public class RasterToBottomOfZOrder implements LayerInterceptor {
        @Override
        public void run( Layer layer ) {
            GridCoverage resource = null;
            try {
                resource = layer.getResource(GridCoverage.class, new NullProgressMonitor());
            } catch (IOException e) {
                // couldn't get the layer as a GridCoverage, thats fine.
                return;
            }        
            if(resource == null) {
                // it's not a raster, put it just above the highest raster
                IMap map = ApplicationGIS.getActiveMap();
                
                int highestRaster = -1;
                
                List<ILayer> layers = map.getMapLayers();
                for(ILayer l: layers) {
                    try {
                        resource = l.getResource(GridCoverage.class, new NullProgressMonitor());
                    } catch (IOException e) {
                        // couldn't get the layer as a GridCoverage, thats fine.
                        resource = null;
                    }                
                    if(resource != null) {
                        if(l.getZorder() > highestRaster) {
                            highestRaster = l.getZorder();
                        }
                    }
                }            
                if(highestRaster > 0) {
                    layer.setZorder(highestRaster);
                }
            } else {
                // new raster, put it down the bottom
                layer.setZorder(0);
            }
        }

    }

Map Interceptors
^^^^^^^^^^^^^^^^

To give you a taste of the kinds of activities that are performed with MapInterceptors here are some
of the map interceptors used in uDig 1.2.

::

    MapInterceptor
    - DisposeBlackboardOnCloseInterceptor

Resource Interceptors
^^^^^^^^^^^^^^^^^^^^^

Resource interceptors are where the rubber really hits the road; this is used when a layer is
resolving to a particular data access class such as FeatureStore.

This interceptor is powerful; as an example it is used to wrap any and all requests for a
FeatureStore with a "UDIGFeatureStore" for which the setTransaction method can only be called once.

::

    public class WrapFeatureStore
            implements IResourceInterceptor<FeatureStore<SimpleFeatureType, SimpleFeature>> {

        @SuppressWarnings("unchecked")
        public FeatureStore<SimpleFeatureType, SimpleFeature> run( ILayer layer,
                FeatureStore<SimpleFeatureType, SimpleFeature> resource,
                Class< ? super FeatureStore<SimpleFeatureType, SimpleFeature>> requestedType ) {
            if (!(resource instanceof UDIGFeatureStore)) {
                if (requestedType.isAssignableFrom(FeatureStore.class)){
                    return new UDIGFeatureStore(resource, layer);
                }
                else {
                    return resource;
                }
            }
            return resource;
        }
    }

The xml to hook this up is:

::

    <interceptor
      class="net.refractions.udig.project.internal.interceptor.WrapFeatureStore"
      id="net.refractions.udig.project.wrap.featurestore"
      order="PRE"
      target="org.geotools.data.FeatureStore"/>

The other information that can be specified with the resource interceptor extension point is a
caching strategy. Many resources (such as FeatureSource) can be created multiple times. By using a
caching strategy we reserve one instance for use with our layer - very handy when we want to attach
listeners to it and notice when the content changes.

Here is an example of how uDig caches one instance of each resource for each layer:

::

    public class ResourceCacheInterceptor implements IResourceCachingInterceptor {
        private Map<Class, Object> resources = new HashMap<Class, Object>();

        public <T> boolean isCached( ILayer layer, IGeoResource resource, Class<T> requestedType ) {
            return resources.containsKey(requestedType);
        }

        public <T> T get( ILayer layer, Class<T> requestedType ) {
            return (T) resources.get(requestedType);
        }

        public <T> void put( ILayer layer, T resource, Class<T> requestedType ) {
            if (resource != null) {
                registerClasses(resource.getClass(), resource);
            }
        }

        private <T> void registerClasses( Class<T> clazz, Object obj ) {
            if (obj instanceof Style || obj instanceof GridCoverage) {
                return;
            }
            if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
                registerClasses(clazz.getSuperclass(), obj);
            }
            for( int i = 0; i < clazz.getInterfaces().length; i++ ) {
                registerClasses(clazz.getInterfaces()[i], obj);
            }
            resources.put(clazz, obj);
        }
    }

Although the above example uses an internal map; you may also find it useful to use the map
blackboard to cache objects.

Feature Interceptors
^^^^^^^^^^^^^^^^^^^^

Feature interceptors are used to "pre process" features before they are added to a FeatureStore.

Examples:

-  They can be used passively to create an audit log tracking the creation of features; please note
   that the FeatureID is not actually set until the the user presses the commit button.
-  They can be used dynamically to modify the feature with sensible default values (rather then
   those that come out of the box).

This extension point may be extended in the future to cover the removal of features, or the actual
commit of features to a DataStore.
