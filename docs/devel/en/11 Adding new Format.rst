11 Adding new Format
====================

Adding a new Format
===================

UDig makes use of a Catalog API that provides handles to all running services. You basically follow
the handle to get access to the real data (be it DataStore, GridCoverage, or something new that you
just made up).

These handles are called IService (which contains IGeoResource handles). Each has an associated
"Info" object that captures the exact same information everyone always wants
(name,title,icon,etc...)

Sometimes for a given "data" more then one handle is available (the data may be available through
PostGIS and through a WMS). There is a voting system where the "best" renderer is chosen to render a
layer. Some renderers can draw more then one layer (like WMS), some renderers like Shapefile
renderer are stupidly fast.

Adding a new format to uDig involves introducing a catalog service and a renderer for your content
type.

Catalog service
~~~~~~~~~~~~~~~

The catalog service is contributed via the **net.refractions.udig.catalog.ServiceExtension**
extension point. This boils down to writing implementations of the following interfaces.

-  IService
-  IGeoResource
-  ServiceExtension

Renderer
~~~~~~~~

The renderer is contributed via the **net.refractions.udig.project.renderer** extension point. This
involves writing implementations of the following interfaces

-  IRenderer
-  IRenderMetrics
-  IRenderMetricsFactory

Plugin.xml
~~~~~~~~~~

::

    <extension
          point="net.refractions.udig.catalog.ui.fileFormat">
          <fileService fileExtension="*.mif"/>
          <fileService fileExtension="*.dxf"/>
       </extension>

:doc:`MapInfo Data Interchange Format MIF`

 :doc:`Autocad DXF Format`

