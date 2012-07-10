How do I make a new type of layer
=================================

Q: How do I make a new type of layer?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

First one must understand what a layer is before they can understand how to make one:

A layer in uDig is (very generally) a set of IGeoResources which are handles for the same "real"
resource. An example is a WFS and a WMS may be backed onto the same data. As a result both
IGeoResources would be members of the layer's set of IGeoResources.

So, to define a new **type** of layer one must create a new IGeoResource (and IService since
IServices contain the IGeoResources). For example a Web Terrain layer would require a Web Terrain
IService and IGeoResource.

For more information on the Catalog API see the `Catalog Overview <2%20Catalog.html>`_ in the
`Programmer's Guide <2%20Programmer's%20Guide.html>`_.

However once a new layer type is created it does not automatically get rendered unless one of its
IGeoResources resolves to a DataStore, GridCoverage or a WebMapServer. If one of the IGeoResources
does resolve to one of those objects then you are done and the layer will render. If not then a new
Renderer must be developed. For more information on the Renderer API please look at the
 `Renderers <09%20Renderers.html>`_ page.
