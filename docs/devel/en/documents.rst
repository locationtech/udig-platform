Documents
~~~~~~~~~

Documents provide support information for resources and features. These come in the form of attachments, links and hotlinks.

The *Document View* provides a user interface to access and manage these documents. It is driven by an API that allows developers to customise the document data models and the document sources. This provides the flexibility of implementing your own document classes and publishing them to the view.

Documents
---------

These are the data models that we can implement to define a document.

- **IDocument** - The basic document data model. This provides access to the document's content and other metadata like label and description.

::

   net.refractions.udig.document.model.FileLinkedDocument

- **IAttachment** - An add-on to IDocument. This provides the attachment specific *Save As* method.

::

   net.refractions.udig.document.model.FileAttachmentDocument

- **IHotlink** - An add-on to IDocument. This provides access to hotlink metadata like attribute name and hotlink descriptors.

::

   net.refractions.udig.document.model.FileHotlinkDocument

Documents Sources
-----------------

These are the document sources that we can implement to provide access to documents and allow getting, adding, updating and deleting of documents.

These make use of the data models above to communicate to and from the *Document View*. 

- **IDocumentSource** - This provides access to resource-level attachments and links.

::

   net.refractions.udig.document.source.ShpDocumentSource
   
- **IAttachmentSource** - This provides access to feature-level attachments and links.

::

   net.refractions.udig.document.source.ShpAttachmentSource

- **IHotlinkSource** - This provides access to feature hotlinks.

::

   net.refractions.udig.document.source.ShpHotlinkSource

Integrating with Document View
------------------------------

The *Document View* basically listens to the workbench selection and checks if it can get a document source from *IGeoResource* selections.
   
**How the view gets a document source:**

::

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
   
**How to hook up a document source:**

1. Implement the document sources, *see sample implementations above*

2. Extend the *net.refractions.udig.catalog.resolvers* extension point

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
   
3. Implement the *IResolveAdapterFactory* referenced by the extension and integrate the document sources

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

   