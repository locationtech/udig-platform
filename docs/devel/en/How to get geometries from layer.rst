How to get geometries from layer
================================

Q: How to get geometries from layer
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To get geometries from a layer you need to ask for a FeatureSource object from the layer:

::

    FeatureSource source=layer.getResource( FeatureSource.class, progressMonitor);

(You need the org.geotools.feature.FeatureSource class). Once you have a feature source you can get
all the features from the source by:

::

    FeatureCollection collection=source.getFeatures();

It is a feature collect and has all the normal collection methods.

**WARNING:** don't forget to close your iterator after using one:

::

    collections.close(iterator);

