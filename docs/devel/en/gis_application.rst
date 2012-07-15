GIS Application
~~~~~~~~~~~~~~~

The uDig GIS Application represents three things: the example download application, a tier of the
uDig architecture where visualization occurs, and a utility class to access services of the uDig
visualization services.

Example GIS Application
^^^^^^^^^^^^^^^^^^^^^^^

The GIS Application, when executed is the barebones uDig application. It represents a simple and
capable GIS application. This is, however, only the beginning.

You can extend uDig in two ways:

-  customize uDig for your needs through the addition of plug-ins
-  defining a new application based on uDig

The uDig Application is an extension point; defined by the net.refractions.udig.ui plug-in. It
publishes the UDIGApplicaiton class; this extension point is then referred to by the
net.refractions.udig plugin as it defines the udig.product.

GIS Application Tier
^^^^^^^^^^^^^^^^^^^^

Both of these activities are made in reference to the GIS Application tier. While the GIS Platform
provided programmatic access to spatial information there was nothing visual about it. The GIS
Application refines these ideas into the concept of Projects, Maps, Layers on the data modeling
side. On the user-interface side the GIS Application tier defines the MapEditors, the Layers view
and many more ways of interacting with the central data model.

The GIS Application data model was constructed using the Eclipse Modeling Framework, you do not need
to know anything about EMF to use the GIS Application data model. We have provided the *usual*
concepts of listeners and events

ApplicationGIS Utility Class
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The GISApplication class has been defined to assist programmers in making use of the GIS Application
tier visualization services.

ApplicationGIS.getActiveProject
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Retrieve the current project; can be used to create new maps etc...

::

    IProject project = ApplicationGIS.getActiveProject();
    project.sendASync( new CreateMapCommand( name, resources, project );

ApplicationGIS.getActiveMap
^^^^^^^^^^^^^^^^^^^^^^^^^^^

The getActiveMap() method is often used by eclipse menus or views to figure out which Map to send a
command to; or switch modal tools etc.

::

    IMap map = ApplicationGIS.getActiveMap();
    map.sendCommandAsync( new SetViewportCenterCommand( location ) );

ApplicationGIS.getEditManager()
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The edit manager for a map track the selected layer and editing concerns such as the transaction and
current feature being edited.

::

    IEditManager editManager = ApplicationGIS.getEditManager();
    ILayer layer = editManager.getSelectedLayer();
    IStyleBlackboard styleBlackboard = layer.getStyleBlackboard();
    styleBlackboard().put(ProjectBlackboardConstants.LAYER__DATA_QUERY, filter);

