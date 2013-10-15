Connect to a Service on Startup
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

One common request when people make there own custom application - is making sure their PostGIS (or
WFS or whatever) is available and added to the Catalog when the user first starts up.

Related:

* :doc:`catalog`

* `net.refractions.udig.community.jody.tile <http://svn.refractions.net/udig/udig/community/jody/trunk/plugins/net.refractions.udig.community.jody.tile/>`_
   (example that adds a custom service on startup)

Add an Eclipse "Startup" extension that adds
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Here is the part of the plugin.xml used in the above example:

::

    <extension
             point="org.eclipse.ui.startup">
          <startup class="net.refractions.udig.community.jody.tile.Preload"/>
       </extension>

**Implement an IStartup**

::

    /**
     * Used to preload some nice datasets (ie worldwind).
     */
    public class Preload implements IStartup {
        public void earlyStartup() {
            Map<String,Serializable> params = new HashMap<String,Serializable>();
            params.put( "url", WorldWindTileProtocol.class.getResource("earthimages.xml") );
            
            List<IService> match = CatalogPlugin.getDefault().getServiceFactory().acquire( params );
            if( !match.isEmpty()){
                IService service = match.get(0);
                
                ICatalog local = CatalogPlugin.getDefault().getLocalCatalog();
                local.add( service );
            }         
        }
    }

Improvements
^^^^^^^^^^^^

This example is pretty simple - to be a bit more correct we should check if the IService is already
in the catalog:

::

    IService found = catalog.getById( IService.class, service.getIdentifier(), progressmonitor );
    if( found != null ){
       return; // already loaded!
    }
    ICatalog local = CatalogPlugin.getDefault().getLocalCatalog();
    local.add( service );

Normally you also check the service to see if it is working - see the `Catalog <Catalog.html>`_ page
for details.

Connection Parameters
^^^^^^^^^^^^^^^^^^^^^

There are two good approaches to acquiring connection parameters for these "required" services.

**Preferences**

Store the connection parameters in preferences; make a preference page so they can change the host /
port / etc...

**Login**

Make a login screen that the user can fill in their credentials.
