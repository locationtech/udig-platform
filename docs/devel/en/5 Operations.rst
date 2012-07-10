5 Operations
============

The operation extension point is one of the most useful (and used) facilities of the uDig SDK. It
allows you to contribute functionality to the user interface based on what is selected, regardless
of which view it is displayed in.

* :doc:`IOp`

* :doc:`Implementation of an Operation`

* :doc:`Contributing Operations to a Menu`


Related:

-  Export Shapefile tutorial

IOp
===

The IOp interface is used to define user interface needs. This represents a suitable compromise that
can be maintained both by uDig and Toolkit providers.

::

    <extension
             point="net.refractions.udig.ui.operation">
          <operation
                class="net.refractions.udig.tool.edit.DifferenceOp"
                enablesFor="2"
                id="net.refractions.udig.tool.edit.difference"
                name="%difference.name"
                targetClass="net.refractions.udig.project.ILayer"/>
          <operation
                class="net.refractions.udig.tool.edit.SplitLineOp"
                enablesFor="+"
                id="net.refractions.udig.tool.edit.operation1"
                menuPath="edit/add.ext"
                name="%operation.splitLine.name"
                targetClass="net.refractions.udig.tools.edit.support.Point">
             <enablement>
                <and>
                    <property propertyId="net.refractions.udig.tools.edit.CurrentShape.ShapeType" expectedValue="LINE"/>
                    <property propertyId="net.refractions.udig.project.ui.OpenMap" expectedValue="java.lang.Object"/>
                </and>
             </enablement>
          </operation>
       </extension>

The above example (from net.refractions.udig.tool.edit) contributes two "operations" to the system:

-  DifferenceOp which is available whenever the user is working with a ILayer (or even something
   that can adapt to an ILayer)
-  SplitLineOp which is available whenever the user has selected a point using the edit tool and the
   enablement conditions are met.

All operations are available in the **Edit > Operations...** menu in accordance with the Eclipse
House Rules.

Implementation of an Operation
==============================

The targetClass mentioned in the extension point is an actual API contract; the system will make
sure the user has supplied you with an actual object of the requested Class before you are called.
So performing a cast to the expected targetClass is expected.

Here is an example from the Export Shapefile tutorial:

::

    public class ShpExportOp implements IOp {
      public void op(Display display, Object target, IProgressMonitor monitor)
            throws Exception {
        FeatureSource source = (FeatureSource) target;
        
        SimpleFeatureType featureType = source.getSchema();
        GeometryDescriptor geometryType = featureType.getGeometryDescriptor();
        CoordinateReferenceSystem crs = geometryType.getCoordinateReferenceSystem();    
        
        String typeName = featureType.getTypeName();    
        ...
      }
    }

The system will contribute this operation to the user interface whenever the conditions can be met.
So this operation will work on both GeoResources that can adapt to FeatureSource; and on ILayers
used to render a FeatureSource.

Contributing Operations to a Menu
=================================

Operations are slotted into a menu position using the eclipse menu contribution system. The idea
here is to slot an entire category of operations into a menu location:

-  if the category contains one operation it will be shown as is
-  if more than one operation they will be displayed as a submenu
-  any operations not assigned a menu slot will be available in the Edit > Operations dialog

Future improvements:

-  to meet the eclipse other Rule we should limit the size of the submenu and allow an "Other..."
   menu item to bring up a dialog showing all the operations in the category

In the uDig application you can see this in action where shapefile operations are added to the menu
system as shown below:

::

    <extension
              point="org.eclipse.ui.menus">
           <menuContribution
                 locationURI="menu:data?before=reset">
              <dynamic
                    class="net.refractions.udig.ui.operations.OpCategoryContributionItem:net.refractions.udig.catalog.shp.operationCategory"
                    id="net.refractions.udig.catalog.shp.menu">
              </dynamic>
           </menuContribution>
        </extension>

Note how in the above example the cateogry is provided as a parameter to the
OpCategoryContributionItem.
