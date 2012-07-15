Render Creation
~~~~~~~~~~~~~~~

Each time rendering is initiated a renderer must be chosen for each layer. This page is a deep dive
into the internal implementation that is responsible for doing this task in particular it will look
how RenderManagerDynamic does this. We are looking at RenderManagerDynamic because it is the more
complicated mechanism since it is the RenderManager used when rendering to a *live* uDig map display
(compared to printing a map.) Because the RenderManager is rendering to a ViewportPane it needs to
render in multiple threads and incrementally update the viewport as data is made available.

Before continuing one must understand the relationship between RenderContexts and Renderers. A
Renderer can render a particular type of data to a Graphics2D object or a RenderContext's
BufferedImage. A RenderContext contains the image the Renderer must render to as well as a
references to the Layer(s) that the Renderer is responsible for rendering. Each Renderer has exactly
one RenderContext, however a MultiLayerRendererImpl is a renderer that takes a
CompositeRenderContext. This is a subclass of RenderContext that contains multiple RenderContexts.
In this case the Renderer is responsible for rendering all Layers referenced by the contained
RenderContexts. This has several important consequences.

-  First the renderer will only render to the BufferedImage of the CompositeRendererContext and
   therefore less memory is required and less time is taken to compose all the individual
   RenderContexts into a single image
-  Second a single thread will render all the layers so it is possible that this is slower (but may
   be the same speed depending on the implementation).
-  Third if any of the layers require an update all the layers must be redrawn

To perform a rendering several steps are required. First, create a RenderContext for each Layer.
Then find the appropriate renderer for each RenderContext (and by extension each Layer). During this
process the renderers are queried to determine if they will handle multiple layers or not.

The choosing of a Renderer has several intersecting concerns. First if the renderer is capable of
rendering the Layer and, second, how suited that renderer is for rendering the layer given the
current context of the map, editing, styling, bandwidth, etc... As such the eclipse renderer plugin
do not register Renderers, instead RenderMetricFactories are registered. The factories are used to
create a *Metrics* object which will (when passed a layer) calculate a value of *suitability* score.
The higher the value the more indicators it satisfies.

For example, a WMS renderer might be be able to render a layer and its associated metrics object
would return a 2 (1 for being able to render and another 1 because it can perform the rendering
efficiently in terms of performance and memory footprint). A WFS renderer might also be able to
render the layer but only return a 1 because it cannot claim to be able to render the layer
efficiently. However, if there is an open edit transaction open on the layer the WMS renderer would
return a 0 because although it is capable of rendering the layer, under the current context it
cannot correctly render an edited layer. While the WFS renderer could still correctly render the
layer.

These are the considerations we must keep in mind while analyzing the internals of the renderer
creations process.

Process of Creating Renderers and Keeping them in Sync
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The starting point is the RenderManagerDynamic's refresh(Envelope) method. At the time of this
writing one of the first steps it makes is to get the RendererCreator and reset it. The reset method
clears the previous configuration and recreates it based on the current set of layers. The steps in
this process are roughly as follows:

#. For each layer process all the renderer extension points and find all the renderers capable of
   rendering the layer. This information is placed in a cache within the RendererCreator and is not
   calculated more than once. This is called the layerToMetricsFactoryMap.
#. Next for each layer all the RenderMetricsFactories are obtained from the layerToMetricsFactoryMap
   and the factories are used to create RenderMetrics objects that will be used to calculate the
   *suitability* of the renderer. The Metrics are then sorted based on their *suitability* and the
   one with the highest score will be chosen for that layer and the metrics will be requested to
   create a RenderContext object
#. If the RenderContext is a CompositeRenderContext then the Metrics object will be queried if it is
   capable of rendering the next layer (and the next and next until it cannot render one of the
   layers.) Each layer that it can render will be added to the RenderContext for rendering by its
   Renderer. Only layers that are adjacent in the z-order of the map can be added to the same
   RenderContext because a MultiLayerRenderer renders to a single BufferedImage and thus layers
   cannot be inserted in to the middle.
#. After each layer has a RenderContext and Metrics object assigned they are put into a map which is
   commonly termed the renderer configuration.

Note: The synchronization in the RendererCreator is very important because it is possible for a
rendering to be triggered in two different threads and both threads could try to configure all the
layers at the same time. The fact that the configuration could be calculated multiple times is
acceptable but the synchronization and the copying of the different objects is required to ensure
that each configuration calculation does not interfere with another and result in a potentially
wrong result.

After the RendererCreator has been reset and a configuration based on the latest layers and
surrounding context has been constructed. The configuration is compared to the last configuration to
see if the configuration has changed at all. For example if a RenderContext is in the previous
configuration but not in the new configuration that RenderContext (and renderer) will be removed.

In the RenderManagerDynamic there is a root CompositeRenderExecutor which has a
CompositeRenderContext. The first time the map is renderered, the RenderExecutor is created. All
RenderContexts in the configuration (obtained from the RendererCreator) are added to the executor's
CompositeRenderContext and the RenderExecutor uses the RendererCreator to instantiate each Renderer.
More importantly a listener is added to the CompositeRenderContext so that when a RenderContext is
added or removed the old renderer (in the case of a remove) is removed as well as the context and a
new renderer is created using the RendererCreator (in the case of an add). In this way the Renderers
are kept synchronized each time a render occurs.

This occurs each time a render is triggered because the framework is so flexible that nearly any
change could require the a new renderer. However in practice only rarely is a new configuration
required so perhaps in the future the possible changes might be restricted so that only certain
changes will result in a configuration synchronization

Summary
^^^^^^^

To summarize: Each render a configuration is constructed. That configuration is compared to the
previous configuration and in the case of differences renderers are removed or added to the root
CompositeRenderExecutor.
