10 Service and GeoResource Interceptors
=======================================

The catalog also provides "interceptors" that can be used to hook into the life cycle of IService
and IGeoResource object.

* :doc:`ServiceInterceptor`

* :doc:`GeoResourceInterceptor`


Related:

* :doc:`10 Interceptors`


ServiceInterceptor
==================

ServiceInterceptor can be used to hook into the service lifecycle events:

-  **serviceCreated**: Called when an IService instance is created. Please be aware that the service
   may not actually be used; it is very common to create an IService just to get a hold of a good ID
   for a service - and then throw it away.
-  **serviceAdded**: called when a service is actually being added to the catalog; giving you a
   chance to configure; inject security credentials; or otherwise set a service up. The service may
   not yet "be connected" but it is at least now being tracked and the catalog will take
   responsibility for cleaning it up now.
-  **serviceRemoved**: the time has come; the catalog is cleaning up this service.

Note that the catalog does maintains persisted properties; you can interact with these properties
uding serviceAdded / serviceRemoved to provide such things as a better default title.

:doc:`ShpPropertiesInterceptor.java`

code example of grabbing a good title from a shapefile sidecar "properties" file:

::

    public class ShpPropertiesInterceptor implements ServiceInterceptor {
        public void run(IService service){
            if( service instanceof ShpServiceImpl){
                ID id = service.getID();
                File directory = id.toFile().getParentFile();
                File infoFile = new File( directory, id.toBaseFile()+".properties" );
                if( infoFile.exists() ){
                   try { 
                       FileReader infoReader = new FileReader( infoFile );
                     Properties info = new Properties();
                     info.load( infoReader );
                     String title = (String) info.get("title");
                     if( title != null ){
                       service.getPersistentProperties().put("title", title);
                     }
                   } catch (IOException eek ){
                   }
                }
            }
         }
    }

GeoResourceInterceptor
======================

In a similar manner you can have a go at setting up a IGeoResource:

-  **resourceAdded**: called to perform any configuration or code injection (such as listeners) on a
   service prior to use
-  **resoruceRemoved**: called to clean up any listeners or configuration

This is usually used to "seed" the GeoResourceInfo with a good bounds in cases where the actual
underlying data is wrong.
