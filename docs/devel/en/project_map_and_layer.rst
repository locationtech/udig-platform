Project Map and Layer
=====================

The uDig GIS Application manages your content using Projects, Maps and Layers.

**Before you Start a word on Thread Safety**

Because this is a dynamic application we ask you not to hack away at these data structures as the
system is running (to do so often freezes the screen). Instead we ask you to assemble a
:doc:`command <commands>` that will be issued in the user interface thread.

uDig is highly threaded, as benefits a client application, ensuring that "work" happens in the
correct thread is a large benefit provided to you by the GIS Application.

Project
^^^^^^^

Project represents a folder, on disk where your content is saved.

The API available to you for a Project is three fold: that which is available all the time (such as
the File), that which will need to block (accessing the File), and that which can only be used via a
:doc:`command <commands>` (modifying the contents).

This is not the same as an Eclipse "Project"; we are strictly a "Rich Client Platform" application
and do not support the Eclipse IDE concepts of IResource to manage files.

Map
^^^

Map represents a visualization of spatial information, makes use of a ViewPort indicating the area
of interest

.. figure:: /images/project_map_and_layer/MapDiagram.jpg
   :align: center
   :alt: 

The API available to you for a Map is threefold: that which is available all the time (such as the
File); that which will need to block (accessing the File); and that which can only be used via a
:doc:`command <commands>` (modifying the contents).

Highlights:

-  Layers
-  Viewport
-  Selection, often used with editing tools
-  IssuesList, common API for "workflow user interfaces"

MapListener and Notifications
'''''''''''''''''''''''''''''

The map and layer data structures supports the use of listeners for common events.

.. code-block:: java

    IMapListener mapListener = new IMapListener(){
       public void changed( MapEvent event ){
            if( event.getType() == MapEvent.NAV_COMMAND ){
               // viewport position or crs changed
            }
            // see MapEvent for the complete list
       } 
    };
    addMapListener( mapListener );

You also have access to all events ever using low level notifications:

.. code-block:: java

    Adapter superListener = new AdapterImpl(){
            public void notifyChanged( final Notification msg ) {
                if (msg.getNotifier() instanceof ContextModel) {
                    ContextModel contextModel = (ContextModel) msg.getNotifier();
                    Map map = contextModel.getMap();

                    if (msg.getFeatureID(ContextModel.class) == ProjectPackage.CONTEXT_MODEL__LAYERS) {
                        switch( msg.getEventType() ) {
                        case Notification.ADD: 
                        case Notification.ADD_MANY: {
                               // ...
                               break;
                            }
                        case Notification.SET: {
                              Layer layer = (Layer) msg.getNewValue();
                              // ..
                              break;
                            }
                        }
                    }
                }
            }
        };
        map.addDeepAdapter(superListener);

Layer
^^^^^

A map is composed of Layers, indicating which spatial information is to be drawn (and in what
order).

.. figure:: /images/project_map_and_layer/layer.png
   :align: center
   :alt: 

The API available to you for a Layer is three fold: that which is available all the time (such as
the File), that which will need to block (accessing a resource), and that which can only be used via
a command (modifying the contents).

Highlights:

-  Connection Information, used to look up matching resources in the catalog
-  StyleBlackboard, used to control the adaptive rendering system
-  Transaction, for feature information Transaction support is applied for the entire Map!

Layer Interaction
'''''''''''''''''

You can check **ILayer** interaction flags:

.. code-block:: java

    if( layer.getInteraction( Interaction.BACKGROUND )){
       // layer is intended as a background layer
    }

As usual if you want to change an interaction flag you will need to use a command (in order to
access the **Layer** read-write interface):

.. code-block:: java

    // We can clear the background setting using a custom command
    IMap map = layer.getMap();
    final Layer modifyLayer = (Layer) layer;
    map.sendCommandASync( new AbstractCommand(){

       public void run(IProgressMonitor monitor) throws Exception {
          modifyLayer.setInteraction( Interaction.BACKGROUND, false );
       }

       public String getName() {
          return "Clear Interaction";
       }
    });

LayerListener and Notifications
'''''''''''''''''''''''''''''''

Once again a listener is provided for common notifications:

.. code-block:: java

    ILayerListener layerListener = ...
    layer.addListener( layerListener );

Or you can use notifications to access everything: You also have access to all 
events ever using low level notifications:

.. code-block:: java

    Adapter superListener = new AdapterImpl(){
        public void notifyChanged( final Notification msg ) {
            if (msg.getNotifier() instanceof Layer) {
                Layer layer = (Layer) msg.getNotifier();
                if (msg.getFeatureID(Layer.class) == ProjectPackage.LAYER__VISIBLE){
                    // ...
                }
                // check for change to style blackboard, crs, etc...
            }
        }
    };
    map.addDeepAdapter(superListener);

