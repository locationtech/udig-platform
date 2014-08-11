Upgrade From 1.0 To 1.1
#######################

uDig : Upgrade from 1.0 to 1.1

This page last changed on Jul 14, 2012 by jgarnett.

The number of changes between uDig 1.0 and uDig 1.1 has been kept to a minimum. The following are
known considerations at this time.

GIS Application

GIS Platform

GeoTools

`IRenderer <#Upgradefrom1.0to1.1-IRenderer>`__

`IResolve <#Upgradefrom1.0to1.1-IResolve>`__

`Upgrade to GeoTools 2.4 <http://docs.codehaus.org/display/GEOTOOLS/Upgrade+to+2.4>`__

 

`IService <#Upgradefrom1.0to1.1-IService>`__

`Upgrade GeoTools <Upgrade%20GeoTools.html>`__

 

`IGeoResource <#Upgradefrom1.0to1.1-IGeoResource>`__

 

 

`ServiceExtension.exsd <#Upgradefrom1.0to1.1-ServiceExtension.exsd>`__

 

GIS Application
===============

GIS Application: IRenderer
--------------------------

Now proivides information on just the bounds to be drawn.

-  you will need to change method signature
-  get/setRenderBounds added

Please note that simply implementing IRenderer is not sufficient; you will need to extend
**RenderImpl**.

BEFORE (uDig 1.0)
~~~~~~~~~~~~~~~~~

.. code:: code-java

    class MyRenderer extends RenderImpl {
        render( Envelope bounds, IProgressMonitor monitor ){ 
            ...
        }
    }

AFTER (uDig 1.1)
~~~~~~~~~~~~~~~~

.. code:: code-java

    class MyRenderer extends RenderImpl {
        render( IProgressMonitor monitor ){
            Envelope bounds = getRenderBounds();
            ...
        }
    }

Note the superclass RenderImpl provides the implementation of get/setRenderBounds().

GIS Platform
============

GIS Application: IRenderer
--------------------------

We are handling members differently.

-  resolve( List.class, monitor ) no longer needs to evaulate to members
-  members( monitor ) uses Collections.EMPTY\_LIST to indicate a leaf

BEFORE (uDig 1.0)
~~~~~~~~~~~~~~~~~

.. code:: code-java

    class MyResolve implements IRender {
       public List members( Monitor ){
           return null; // I am a leaf
       }
    }

AFTER (uDig 1.1)
~~~~~~~~~~~~~~~~

.. code:: code-java

    class MyResolve implements IRender {
       public List<IResolve> members( Monitor ){
           return Collections.emptyList(); // I am a leaf
       }
    }

GIS Platform: IService
----------------------

IService now makes use of a ResolveManager in order to allow the addition of adapters by other
plug-ins. We have also moved the api contract from javadocs directly into the superclass
implementation.

Consequences:

-  implement getInfo( monitor )
-  canResolve implementation needs to call super, remove references to List, IService or ServiceInfo
-  resolve implementation needs to call super, remove any resolve code that works on List, IService
   or ServiceInfo

QA Warnings:

-  please ensure canResolve( null ) returns false

BEFORE (uDig 1.0)
~~~~~~~~~~~~~~~~~

.. code:: code-java

    class MyService extends IService {
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ){
       public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ){
            if( monitor == null ) monitor = new NullProgressMonitor();
            
            if( adaptee.isAssignableFrom( DataStore.class ) ){
                 return getDataStore( monitor );
            }
            if( adaptee.isAssignableFrom( IService.class ) ){
                 return this;
            }
            if( adaptee.isAssignableFrom( IServiceInfo.class ) ){
                 return new MyServiceInfo( monitor );
            }
            if( adaptee.isAssignableFrom( List.class ) ){
                 return members( monitor );
            }
            return null;
       }  

    }

AFTER (uDig 1.1)
~~~~~~~~~~~~~~~~

.. code:: code-java

    class MyService extends IService {
       MyServiceInfo info = null;
       public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ){
            if( monitor == null ) monitor = new NullProgressMonitor();
            
            if( adaptee.isAssignableFrom( DataStore.class ) ){
                 return getDataStore( monitor );
            }
            return super.resolve( adaptee, monitor );
       }  
       public synchornized ServiceInfo getInfo( Monitor monitor ){
            if( info != null ){
                info = new MyServiceInfo( monitor );
            }
            return info;
       }
    }

GIS Platform: IGeoResource
--------------------------

IGeoResourcenow makes use of a ResolveManager and javadoc contract coded directly into the
superclass. We have also split the functionality of the parent method into seperate method to allow
for nested IGeoResources.

Consequences:

-  implement parent( monitor )
-  implement getInfo( monitor )
-  canResolve implementation needs to call super, remove references to List, IGeoResource or
   IGeoResourceInfo
-  resolve implementation needs to call super, remove references to List, IGeoResource or
   IGeoResourceInfo

QA Warnings:

-  please ensure canResolve( null ) returns false

BEFORE (uDig 1.0)
~~~~~~~~~~~~~~~~~

.. code:: code-java

    class MyGeoResource extends IGeoResource {
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ){
       public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ){
            if( monitor == null ) monitor = new NullProgressMonitor();
            
            if( adaptee.isAssignableFrom( FeatureSource.class ) ){
                 return getFeatureSource( monitor );
            }
            if( adaptee.isAssignableFrom( IGeoResource.class ) ){
                 return this;
            }
            if( adaptee.isAssignableFrom( IGeoResourceInfo.class ) ){
                 return new MyGeoResourceInfo( monitor );
            }
            if( adaptee.isAssignableFrom( List.class ) ){
                 return members( monitor );
            }
            return null;
       }
    }

AFTER (uDig 1.1)
~~~~~~~~~~~~~~~~

.. code:: code-java

    class MyGeoResource extends IGeoResource {
       MyGeoResourceInfoinfo = null;
       public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ){
            if( monitor == null ) monitor = new NullProgressMonitor();
            
            if( adaptee.isAssignableFrom( FeatureSource.class ) ){
                 return getFeatureSource( monitor );
            }
            return super.resolve( adaptee, monitor );
       }  
       public synchornized IGeoResourceInfo getInfo( Monitor monitor ){
            if( info != null ){
                info = new MyGeoResourceInfo( monitor );
            }
            return info;
       }
    }

ServiceExtension.exsd
---------------------

The serviceExtension extension point has had a new required attribute added: id. Now each service
extension must have id defined for the attribute. A name attribute has also been added.

BEFORE
~~~~~~

.. code:: code-xml

    <extension
             point="net.refractions.udig.catalog.ServiceExtension">
          <service
              class="net.refractions.udig.catalog.memory.MemoryServiceExtensionImpl"/>
       </extension>

AFTER
~~~~~

.. code:: code-xml

    <extension
             point="net.refractions.udig.catalog.ServiceExtension">
          <service
                class="net.refractions.udig.catalog.memory.MemoryServiceExtensionImpl" 
                id="memory"
                name="In-Memory Datastore"/>
       </extension>

+------------+----------------------------------------------------------+
| |image1|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/border/spacer.gif
.. |image1| image:: images/border/spacer.gif
