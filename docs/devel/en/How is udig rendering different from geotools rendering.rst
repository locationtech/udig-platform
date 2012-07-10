How is udig rendering different from geotools rendering
=======================================================

Q: How is udig rendering different from geotools rendering

Our problem is a little bit different then the one that the geotools lite and j2d renderers works
with.

Often in a OGC Open Web Services workflow there is more then one way to do it. Both a Web Feature
Server and a Web Map Server may provide the same information, or indeed be able to provide a picture
for a layer in a map. To make matters interesting a Web Map Server may be able to draw more then one
layer at the same time. Our api is trying to walk the line between capturing this complexity, and
hiding it. Not much fun, but there you go.

However when we have figured out which workflow is going to be used for a layer we end up with the
concept of a Renderer.

Each Layer, or a set of Layers, get a Renderer. Each Renderer produced a raster. Renderers are
aranged into a stack, and enough event notification is used to so that everyone can play in their
own thread.

We do plan to work on optimization, indeed many of the optimizations championed by the j2d renderer.
We needed to work on the workflow issues and lite renderer was easier to hook up.
