Icons
~~~~~

References:

* `http://wiki.eclipse.org/User\_Interface\_Guidelines#Folder\_Structure <http://wiki.eclipse.org/User_Interface_Guidelines#Folder_Structure>`_

Organization of the icons/ directory
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

DIRECTORY

distabled

enabled

Other

Banner

Size

Placement

local toolbar

dlcl16

elcl16

 

 

16x16

left & top clear

toolbar

dtool16

etool16

 

 

16x16

left & top clear

view

dview16

eview16

 

 

16x16

left & bottom clear

model object

 

 

obj16

 

16x16

centered, bottom clear

overlay

 

 

ovr16

 

7x8

one pixel white outline

wizard banner

 

 

 

wizban

55x45

bottom left on blue gradient

 

 

 

 

 

 

 

PALETTE

color

grayscale

color

color

 

 

\*perspective & fastview icons require the right, and bottom edges to be clear.

Icon Types
^^^^^^^^^^

TYPE

type

description

local toolbar

lcl

found on the far right of the title area of a view

toolbar

tool

used in cascading menus, and the global toolbar

view

view

found in the top, left corner of a new view

model object

obj

used in the tree, list, properties views, and editor tabs

overlay

ovr

placed on top of model object to indicate a change

wizard banner

wizban

used in wizard dialog windows

Filename Conventions
^^^^^^^^^^^^^^^^^^^^

FILENAME SUFFIX

lcl

tool

view

obj

ovr

wizban

invoke a wizard, or graphics in a wizard

 

\_wiz

 

 

 

\_wiz

invoke executable file

 

\_exec

 

 

 

 

in an editor view

 

\_edit

 

 

 

 

in a navigator view

\_nav

\_nav

\_nav

 

 

 

do not fit into a category

\_misc

 

\_misc

 

 

 

represent tasks that user can do

\_tsk

 

\_tsk

\_tsk

\_tsk

 

toggles the working mode of the view

\_mode

 

 

 

 

 

found in a menu

\_menu

 

 

 

 

 

found in a property sheet

\_ps

 

\_ps

 

 

 

used in the tree, list, or property view

 

 

 

\_obj

 

 

model object icons on object palettes

 

 

 

\_pal

 

 

commands that engage the system

\_co

\_co

 

 

 

 

The original 2.1 guidelines provided two additional directories clcl & ctool which allowed a full
color palette, the enabled and disabled icons were restricted to 8 and 2 colors respectively. This
restriction has since been relaxed.

lcl16
^^^^^

-  /icons/elcl16/zoom\_layer\_co.gif Zoom to layer

obj16
^^^^^

-  icons/obj16/feature\_obj.gif feature
-  icons/obj16/grid\_obj.gif grid or raster

Service:

-  icons/obj16/database\_obj.gif! database (feature service)
-  icons/obj16/datastore\_obj.gif! datastore (generic feature service)
-  icons/obj16/gce\_obj.gif! grid coverage exchange (generic raster service)
-  icons/obj16/memory\_obj.gif! memory (generated or temporary content)
-  icons/obj16/repository\_obj.gif! repository (catalog service)

Server:

-  icons/obj16/server\_obj.gif! generic server
-  icons/obj16/wfs\_obj.gif! web feature server (open web services feature server)
-  icons/obj16/wms\_obj.gif! web map server (open web services rendering service)
-  icons/obj16/wrs\_obj.gif! web registry service (open web services catalog service)

File:

-  icons/obj16/feature\_file\_obj.gif file (feature file)
-  icons/obj16/grid\_file\_obj.gif file (grid file)

Misc:

-  icons/obj16/world\_obj.gif! world used in logo

ovr16
^^^^^

Status

-  icons/ovr16/error\_ovr.gif! error

tool16
^^^^^^

Project

-  icons/etool16/addlayer\_wiz.gif Add layer
-  icons/etool16/newfile\_wiz.gif New file
-  icons/etool16/newfolder\_wiz.gif New folder
-  icons/etool16/newlayer\_wiz.gif New layer
-  icons/etool16/newmap\_wiz.gif New map
-  icons/etool16/newprj\_wiz.gif New projet
-  icons/etool16/new\_wiz.gif New wizard
-  icons/etool16/newtemplate\_wiz.gif New template
-  icons/etool16/save\_edit.gif Save
-  icons/etool16/saveas\_edit.gif Save As
-  icons/etool16/saveall\_edit.gif Save All

Catalog:

-  icons/etool16/datastore\_wiz.gif DataStore wizard
-  icons/etool16/file\_wiz.gif File Wizard
-  icons/etool16/wms\_wiz.gif WMS wizard
-  icons/etool16/wfs\_wiz.gif WFS Wizard
-  icons/etool16/postgis\_wiz.gif PostGIS wizard
-  icons/etool16/oracle\_wiz.gif Oracle Wizard
-  icons/etool16/acrsde\_wiz.gif ArcSDE Wizard

Edit Tools

-  icons/etool16/add\_vertext\_edit.gif Add Vertex Edit
-  icons/etool16/remove\_vertext\_edit.gif Remove Vertex Edit
-  icons/etool16/edit\_line\_mode.gif Edit Line mode
-  icons/etool16/edit\_point\_mode.gif Edit Point mode
-  icons/etool16/edit\_polygon\_mode.gif Edit Polygon mode
-  icons/etool16/edit\_mode.gif Edit mode

Zoom Tools

-  icons/etool16/zoom\_extent\_co.gif Zoom extend
-  icons/etool16/zoom\_in\_co.gif Zoom in
-  icons/etool16/zoom\_out\_co.gif Zoom out
-  icons/etool16/zoom\_mode.gif Zoom mode

Pan Tools

-  icons/etool16/pan\_mode.gif Pan mode

Selection Tools

-  icons/etool16/selection\_mode.gif Pan mode

Information Tools

-  icons/etool16/info\_mode.gif Information mode

Printing

-  icons/etool16/newpage\_wiz.gif New Page
-  icons/etool16/newtemplate\_wiz.gif New template
-  icons/etool16/print\_tsk.gif Print

Misc Tools

-  icons/etool16/cancel\_all\_co.gif Cancel all
-  icons/etool16/clear\_co.gif Clear
-  icons/etool16/delete.gif Delete
-  icons/etool16/refresh\_co.gif Refresh
-  icons/etool16/incom\_synch.gif Incoming synchronization
-  icons/etool16/outgo\_synch.gif Outgoing synchronization

view16
^^^^^^

-  icons/eview16/catalog\_view.gif Information View
-  icons/eview16/view\_obj.gif Catalog View
-  icons/eview16/layer\_view.gif Layer View
-  icons/eview16/style\_view.gif Style View
-  icons/eview16/select\_view.gif Select View

wizban
^^^^^^

-  icons/wizban/arcsde\_wiz.gif ArcSDE Wizard
-  icons/wizban/oracle\_wiz.gif Oracle Wizard
-  icons/wizban/postgis\_wiz.gif PostGIS Wizard
-  icons/wizban/shapefile\_wiz.gif ShapeFile Wizard
-  icons/wizban/wms\_wiz.gif WMS Wizard
-  icons/wizban/wfs\_wiz.gif WFS Wizard
-  icons/wizban/add\_wiz.gif Add Wizard
-  icons/wizban/catalog\_wiz.gif Catalog Wizard
-  icons/wizban/datastore\_wiz.gif Datastore Wizard
-  icons/wizban/export\_wiz.gif Export Wizard
-  icons/wizban/feature\_file\_wiz.gif Feature File Wizard
-  icons/wizban/file\_wiz.gif File Wizard
-  icons/wizban/gce\_wiz.gif Grid Coverage Exchange Wizard
-  icons/wizban/grid\_file\_wiz.gif Grid File Wizard
-  icons/wizban/import\_wiz.gif Import Wizard
-  icons/wizban/repository\_wiz.gif Repository Wizard
-  icons/wizban/server\_wiz.gif Server Wizard
-  icons/wizban/wrs\_wiz.gif WRS Wizard
-  icons/wizban/chooselayer\_wiz.gif Choose Layer
-  icons/wizban/newfolder\_wiz.gif New Folder
-  icons/wizban/file\_wiz.gif File Wizard
-  icons/wizban/new\_wiz.gif New Wizard
-  icons/wizban/newfile\_wiz.gif New File Wizard
-  icons/wizban/newlayer\_wiz.gif New Layer Wizard
-  icons/wizban/newmap\_wiz.gif New Map Wizard
-  icons/wizban/newpage\_wiz.gif New Page Wizard
-  icons/wizban/newprj\_wiz.gif New Project Wizard
-  icons/wizban/newtemplate\_wiz.gif New Template Wizard

