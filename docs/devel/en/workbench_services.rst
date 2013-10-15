Workbench Services
~~~~~~~~~~~~~~~~~~

Boundary Service
^^^^^^^^^^^^^^^^

|image0|
  

Adding a new BoundaryStrategy
'''''''''''''''''''''''''''''

Additional Boundary Strategies are added by the boundary extension point.

Details:

-  Add a boundary extension (net.refractions.udig.ui.boundary)
-  Add a new boundary to the extension
-  Create a strategy class which extends the abstract IBoundaryStrategy class

Note: If a strategy has not defined a boundary it should return a null geometry and extent. Tools
and views listening to the boundary service will by default treat a null geometry or extent as a
world extent (see BoundaryStrategyAll)

Adding a page (optional)
''''''''''''''''''''''''

to be completed

Getting the current Boundary
''''''''''''''''''''''''''''

Access to the current Boundary is available through the workbench BoundaryService as follows:

::

    IBoundaryService service = PlatformGIS.getBoundaryService();
    ReferencedEnvelope extent = service.getExtent();

or

::

    IBoundaryService service = PlatformGIS.getBoundaryService();
    Geometry boundingGeom = service.getGeometry();

Example use of the BoundaryService can be found in BoundaryStrategyScreen and
BoundaryStrategyMapCrs.

.. |image0| image:: /images/workbench_services/BoundaryService.PNG
