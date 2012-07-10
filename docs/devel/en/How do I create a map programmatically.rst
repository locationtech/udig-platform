How do I create a map programmatically
======================================

**Q: How do I create a map programmatically?**

**A:**
 uDig 1.0.x branch:

::

    Project owner = // somehow get a project
    String name="MapName";

    // create the map
    IMap map = ProjectFactory.eINSTANCE.createMap(owner, name, new ArrayList());

    LayerFactory layerFactory = map.getLayerFactory();
    List<Layer> toAdd=new ArrayList<Layer>(resources.size());
    for( IGeoResource resource : resources ) {
        Layer layer = layerFactory.createLayer(resource);
        toAdd.add(layer);
    }

    map.getLayersInternal().addAll(toAdd);

uDig 1.1.x branch:

::

    CreateMapCommand command=new CreateMapCommand("MapName", listofGeoResources, project);
    project.sendSync(command);
    IMap map=command.getCreatedMap();

