Documents
~~~~~~~~~

Documents provide support information for resources and features. These come in the form of
attachments, links and hotlinks.

The *Document View* provides a user interface to access and manage these documents. It is driven
by an API that allows developers to customise the document data models and the document sources.
This provides the flexibility of implementing your own document classes and publishing them to
the view.

These facilities are also used to allow an IGeoResource to publish support files
(such as default style or icon sets).

Document
--------

.. figure:: /../../../plugins/net.refractions.udig.catalog/docs/IDocument.PNG
   :align: center
   :alt: 
   
   IDocument Classes

These are the data models that we can implement to define a document.

- **IDocument** - The basic document data model. This provides access to the document's content and other metadata like label and description.

- **IAttachment** - An add-on to IDocument. This provides the attachment specific *Save As* method.

- **IHotlink** - An add-on to IDocument. This provides access to hotlink metadata including the attribute name used
  to record the file or web link. The list of descriptors captures how the hotlink is to be used,
  especially for attribtues that are configured with several actions.
  
Documents Sources
-----------------

These are the document sources that we can implement to provide access to documents and allow
getting, adding, updating and deleting of documents.

.. image:: /../../../plugins/net.refractions.udig.catalog/docs/DocumentSource.PNG
   :scale: 50 %
   :alt: Document Source Classes
   :align: center
   
These make use of the data models above to communicate to and from the *Document View*. 

IDocumentSource
^^^^^^^^^^^^^^^

IDocumentSource provides access to resource-level attachments and links.
  
Example Implementation **ShpDocumentSource** copies attachments into a folder
and records the **DocumentInfo** information in the property file.

To access a list of documents:: 
   
    IDocumentSource documents = geoResource.resolve( IDocumentSource.class, new NullProgressMonitor );
    List<IDocument> list = documents.getDocuments( new NullProgressMonitor() );

To register a support file (such as a default style) with a GeoResource::

    IDocumentSource documents = geoResource.resolve( IDocumentSource.class, new NullProgressMonitor );
    
    DocumentInfo docInfo = new DocumentInfo("default.sld", "Default style", file.getAbsolutePath(), ContentType.FILE, false, Type.ATTACHMENT);
    documents.add(docInfo, new NullProgressMonitor() );

IAttachmentSource
^^^^^^^^^^^^^^^^^

IAttachmentSource provides access to feature-level attachments and link.
  
Example Implementation **ShpAttachmentSource** copies attachments into a folder and records
the **DocumentInfo** metadata in a property file.

To list attachments for a feature::
   
   IAttachmentSource attachments = geoResource.resolve( IAttachmentSource.class, new NullProgressMonitor );
   List<IDocument> list = attachments.getDocuments(feature, monitor);

To add an attachment, such as an image, to a feature::

    IDocumentSource documents = geoResource.resolve( IDocumentSource.class, new NullProgressMonitor );
    
    DocumentInfo docInfo = new DocumentInfo("picture.jpg", "Location picture", file.getAbsolutePath(), ContentType.FILE, false, Type.ATTACHMENT);
    documents.add(feature, docInfo, new NullProgressMonitor() );

IHotlinkSource
^^^^^^^^^^^^^^

Provides access to feature hotlinks. IHotlinkSource is interesting in that it is used to update
the value of a feature, which you then need to commit in the usual manner.
  
Example Implementation: net.refractions.udig.document.source.ShpHotlinkSource

To list hotlinks for a feature::
   
   IHotlinkSource hotlinks = geoResource.resolve( IHotlinkSource.class, new NullProgressMonitor );
   
   List<HotlinkDescriptor> descriptors = hotlinks.getHotlinkDescriptors( feature, new NullProgressMonitor() );
   List<IDocument> list = hotlinks.getDocuments(feature, monitor);

To update a hotlink attribute::
   
   hotlinks.setFile( feature, hotlinkDescriptor.getAttributeName(), file, new IProgressMonitor() );
   Object value = feature.getProperty( hotlinkDescriptor.getAttributeName() );
   Filter filter = ff.id( feature.getIdentifier() );
   featureStore.modifyFeatures( filter, hotlinkDescriptor.getAttributeName(), value );

Integration with Document View
------------------------------

The *Document View* listens to the workbench selection and checks if it can get any of the
document source classes from the current *IGeoResource* selections.
   
How the view gets a document source::

   final IGeoResource geoResource = // Get from selection
   final Class<T> type = // IDocumentSource.class, IAttachmentSource.class or IHotlinkSource.class

   if (geoResource != null) {
      if (geoResource.canResolve(type)) {
         try {
            return geoResource.resolve(type, monitor);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }    
   }
   return null;

To integrate with Document view make sure your selection resolves to an IGeoResource, and
configure the IGeoResource to resolve to one of the document source interfaces.

Configure Generic Hotlink Support
---------------------------------

Generic hotlink support is available for *any* FeatureSource hosted
by the uDig catalog.

The IGeoResource persisted properties are used to store
metadata marking attributes to be used for hotlink storage.

You can turn this on yourself as a developer using the following::
   
   BasicHotlinkDescriptorParser configure = new BasicHotlinkDescriptorParser( geoResource );
   
   configure.setEnabled( true );
   List<HotlinkDescriptor> descriptors = new ArrayList<HotlinkDescriptor>();
   descriptors.add( new HotlinkDescriptor("report", ContentType.FILE ) );
   descriptors.add( new HotlinkDescriptor("citation", ContentType.WEB ) );
   
   configure.setDescriptors( descriptors );

Configure Shapefile Documents
-----------------------------

Here is an example properties file:

.. literalinclude:: /../files/australia.properties
   :language: ini
   
Configure Custom Documents
--------------------------

When working on your own data type or custom application you can add
document support to any existing IGeoResource.

Example: tie a database table to a content management system.

How to hook up a document source:

1. Implement the document sources, *see sample implementations above*

2. If you have your own custom IGeoResource class you can update the IResolve methods **canReoslve** and **resolve**.

3. If you are working with a provided **IGeoResource** class you can use the
   the *net.refractions.udig.catalog.resolvers* extension point

::

   <extension
         id="shp"
         name="Shapefile Document Resolvers"
         point="net.refractions.udig.catalog.resolvers">
      <factory
            class="net.refractions.udig.document.source.ShpDocumentResolveFactory"
            resolveableType="net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl">
         <resolve
               type="net.refractions.udig.catalog.document.IHotlinkSource"></resolve>
         <resolve
               type="net.refractions.udig.catalog.document.IDocumentSource"></resolve>
         <resolve
               type="net.refractions.udig.catalog.document.IAttachmentSource">
         </resolve>
      </factory>
   </extension> 
   
4. Implement the *IResolveAdapterFactory* referenced by the extension and integrate the document sources

::

   public class ShpDocumentResolveFactory implements IResolveAdapterFactory {

    @Override
    public boolean canAdapt(IResolve resolve, Class<?> adapter) {
        if (resolve instanceof ShpGeoResourceImpl) {
            if (adapter.isAssignableFrom(IDocumentSource.class)
                    || adapter.isAssignableFrom(IHotlinkSource.class)
                    || adapter.isAssignableFrom(IAttachmentSource.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> T adapt(IResolve resolve, Class<T> adapter, IProgressMonitor monitor)
            throws IOException {

        if (resolve instanceof ShpGeoResourceImpl) {
            final ShpGeoResourceImpl shpGeoResource = (ShpGeoResourceImpl) resolve;
            if (adapter.isAssignableFrom(IDocumentSource.class)) {
                IDocumentSource documentSource = new ShpDocumentSource(shpGeoResource);
                if (documentSource != null) {
                    return adapter.cast(documentSource);
                }
            }
            if (adapter.isAssignableFrom(IHotlinkSource.class)) {
                IHotlinkSource hotlink = new ShpHotlinkSource(shpGeoResource);
                if (hotlink != null) {
                    return adapter.cast(hotlink);
                }
            }
            if (adapter.isAssignableFrom(IAttachmentSource.class)) {
                IAttachmentSource attachmentSource = new ShpAttachmentSource(shpGeoResource);
                if (attachmentSource != null) {
                    return adapter.cast(attachmentSource);
                }
            }
        }
            
        return null;
    }

   }

   