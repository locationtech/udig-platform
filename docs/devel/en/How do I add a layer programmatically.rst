How do I add a layer programmatically
=====================================

uDig 1.1
--------

::

    ApplicationGIS.addLayersToMap(map, layers, startposition, project);

uDig 1.0
--------

::

    IMap map;
    map.sendCommandASync(new AddLayerCommand(layer) );

