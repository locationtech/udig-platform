What should I put in as the target for my operation
===================================================

**Q:** What should I put in as the target for my operation?

**A:**

Legal Targets
~~~~~~~~~~~~~

One of the most common questions are "What are legal targets?" and "How do I know when those targets
will be active?"

Targets are tied very closely to the Eclipse workspace's selection framework and is designed to
operate in a similar manner as the org.eclipse.popupMeus extension point. In Eclipse each **view**
has a selection and the *current* selection is the selection of the current view. A selection can be
empty, a single object or many objects. It may be heterogeneous or homogeneous. Currently operations
will always be inactive if the selection is made up of many heterogeneous objects. However, as with
the popupMenus extension point, operations can be defined so they are active for one or many and
filters can be defined as well to further restrict what selections the operations will be enabled
for.

For the basic case, where only the target is defined, the object in the current selection is
examined to see if it matches the declared target in the operation. However more than just and
instanceof check is made. uDig, as with eclipse, uses the Extensible Interface/Adaptable pattern
quite heavily. So if the object(s) in the selection can adapt to the target object then that is also
considered a match.

.. figure:: images/icons/emoticons/information.gif
   :align: center
   :alt: 

**Extensible Interface Pattern**

In a nutshell, the Extensible Interface pattern allows an object to *adapt* to objects of other
types. An example of this is WMS IServices can *adapt* to WebMapServer objects.

I'm now going to provide a little tour of the standard views in uDig and what selections you can
expect in the different views.

Views and the selections that can be expected
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Project Explorer: IMap, IProject, IProjectElement, IPage
-  Layers View: ILayer
   |image0|
   Layers also adapt to their IGeoResources. However adapting only goes 1 level. So while layers
   adapt to IGeoResources they do not adapt to FeatureSources
-  Catalog View: IServices, IGeoResources
   |image1|
   IServices can adapt to many different objects such as WebMapServers and DataStores
   |image2|
   IGeoResources can adapt to objects such as FeatureSources, FeatureStores, GridCoverages and
   WMSLayers
-  Info View: Features
-  Selection View (Soon to be renamed to Table View): Features
-  Feature Editor: Feature
-  MapEditor: This is a trickier workbench part. The selection is determined by the active tool. By
   default, the current selection is the map but some tools (editing tools for example) will cause
   the current selection to be features.

   -  Pan, Measurement and Zoom: Map
   -  Selection tools: AdaptiveFilter which is a org.geotools.filter.Filter and can adapt to the
      ILayer that it applies to.
   -  Info tool: ??? I don't know yet probably features
   -  Edit tools: The features on the EditBlackboard

Object passed to the operation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When the op method is called by the framework, the objected passed to the method is an instance of
the declared class, not necessarily the class that is in the selection. For example if the class
FeatureStore is the target class and the object that is clicked on is a IGeoResource that can
resolve to a FeatureStore then a FeatureStore instance is passed to the operation, not the
IGeoResource.

A second point to note is that if the the class declares 1 as the enablesFor parameter then the
single object will be passed to the operation. **However**, if the enablesFor parameter is anything
else, (+,\*,3,etc..) an array of the target type will be passed in as the target parameter. See Code
Snippets for an example.

Code Snippets
~~~~~~~~~~~~~

**Example where target is a single IMap**
 The follow example declares an operation that is enabled when the selection is a *single* IMap
object. This operation would be enabled in the MapEditor and the Project Explorer.

::

    <operation               categoryId="net.refractions.udig.project.ui.informationOperations"
    class="net.refractions.udig.project.ui.operations.example.FeaturesInView"
                   enablesFor="1"
                   id="net.refractions.udig.project.ui.featuresInView"
                   name="%featuresView"
                   targetClass="net.refractions.udig.project.IMap"/>

**FeaturesInView.java**

::

    public void op( final Display display, Object target, IProgressMonitor monitor )
                throws Exception {
            IMap map = (IMap) target; 

    // ... some work
    }

**Example where target is exactly 2 ILayers**
 The following example would be enabled when exactly 2 ILayers are selected. No more and no less.
The standard views where this operation could be enabled in are the Layers View and Project
Explorer.

::

    <operation
                class="net.refractions.udig.tool.edit.DifferenceOp"
                enablesFor="2"
                id="net.refractions.udig.tool.edit.difference"
                name="%difference.name"
                targetClass="net.refractions.udig.project.ILayer"/>

**DifferenceOp.java**

::

    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {
            final ILayer[] layers=(ILayer[]) target;
    // ... some work
    }

**Example using enablement filter**
 The following example show the declaration of an operation that is enabled only when the selection
consists of a single IService that can adapt (resolve in the case of IService) to a DataStore
object. The difference between this example and simply having the target as a DataStore is that the
object that is passed in is a IService and not a DataStore. Obviously the functionality of IService
is required and not the functionality of a DataStore.

::

    <operation
         categoryId="net.refractions.udig.ui.edit"
              class="net.refractions.udig.catalog.internal.ui.ops.NewFeatureTypeOp"
               enablesFor="1"
               id="net.refractions.udig.catalog.ui.newFeatureType"
               menuPath="file/new"
               name="%newFeatureType"
               targetClass="net.refractions.udig.catalog.IService">
            <enablement>
               <filter adaptsTo="org.geotools.data.DataStore"/>
            </enablement>

Notice in the following snippet that the service is not checked to see if it can resolve to a
DataStore, it is known because the operation is not enabled if the service cannot resolve to a
DataStore.

**NewFeatureTypeOp.java**

::

    public void op( final Display display, final Object target, final IProgressMonitor monitor )
                throws Exception {
            IService service = (IService) target;
            DataStore ds = service.resolve(DataStore.class, monitor);
    // ... some work
    }

.. |image0| image:: images/icons/emoticons/forbidden.gif
.. |image1| image:: images/icons/emoticons/check.gif
.. |image2| image:: images/icons/emoticons/check.gif
