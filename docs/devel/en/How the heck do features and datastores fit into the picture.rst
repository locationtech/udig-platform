How the heck do features and datastores fit into the picture
============================================================

At the uDig level there are IServices and IGeoResources. These are generic handles to something
"real."  For example, an IService can be a handle to a WMS or a Datastore. An IGeoResource can be a
handle to a "FeatureSource" or "GridCoverage".

Next are uDig layers. A layer references an IGeoResource... Actually, since two IGeoResources can
refer to the same data (for example a WMS Layer and a WFS FeatureCollection backed onto the same
data) a layer can reference one or more IGeoResources **BUT** only 1 data. A layer has functionality
allowing inspection of the georesources and also map specific information that is unrelated to the
IGeoResource, for example a map name and a style.

Renderers draw layers. There are many different types of renderers. Some renderers can render Vector
data, others can render GridCoverages or make WMS requests. The BasicFeatureRenderer, for example,
only works for Layers that has an IGeoResource that is a handle for a FeatureSource.

That is all at the uDig level. uDig uses the Geotools library extensively so most of the current
IServices and IGeoResources are handles for Geotools objects. Geotools has DataStores and
FeatureSources for Vector data. A DataStore is a peer of IService. An example of a Datastore is a
PostGIS database. FeatureSources can be obtained from DataStores and are a peer of IGeoResource.
FeatureSources can be used to obtain features of a particular feature type from a DataStore.
FeatureSources are read-only. If the Datastore is read-write you can do an instance check on the
FeatureSource to see if it is a FeatureStore. FeatureStores provide methods for
adding/removing/modifying features.

The next obvious question is: If I am in uDig how do I get a FeatureSource? Here are some common
scenarios that occur in uDig.

Scenario 1. You have a layer and you want a feature source.

::

    layer.getResource( FeatureSource.class, monitor );

This is a blocking call you can do a non-blocking check to see if the layer has a FeatureSource by:

::

    layer.hasResource( FeatureSource.class );

Scenario 2. You have a IGeoResource and you want a FeatureSource.

::

    if( resource.canResolve( FeatureSource.class ) )
        return resource.resolve( FeatureSource.class );

**NOTE:** If it is possible, always obtain a FeatureSource from a layer. This is because uDig's
EditManager manages transactions for the user (and developer). FeatureSources by default use auto
commit transactions where the FeatureSource obtained from a layer uses a transaction.
