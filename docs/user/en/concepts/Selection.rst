Selection
~~~~~~~~~

Selection is not a simple concept because there are different types of selection.

* `Workbench Selection`_
* `Layer Selection`_
* `Edit Selection`_
* `Boundary Selection`_

Workbench Selection
^^^^^^^^^^^^^^^^^^^

The workbench selection is determined by what is selected in the current view. For example in the
Layers view the selection will always be a layer. In the Project Explorer the selection could be a
project, map, page, etc... The selection in the Map Editor depends on the tool that is selected.

+-------------------+------------------------------------------------------------+
| **Tool**          | **Selection**                                              |
+-------------------+------------------------------------------------------------+
| Zoom              | The map shown in the map editor                            |
+-------------------+------------------------------------------------------------+
| Pan               | The map shown in the map editor                            |
+-------------------+------------------------------------------------------------+
| Selection         | The current layer's selection filter                       |
+-------------------+------------------------------------------------------------+
| Information Tools | The map shown in the map editor                            |
+-------------------+------------------------------------------------------------+
| Edit Tools        | The currently edited feature **or** the currently selected |
|                   | vertices. Depending on whether there are                   |
|                   | selected vertices                                          |
+-------------------+------------------------------------------------------------+
| Boundary          | The current boundary layer's selection filter              |
+-------------------+------------------------------------------------------------+

Layer Selection
^^^^^^^^^^^^^^^

Each layer has a filter that indicates what is selected on that layer.

Edit Selection
^^^^^^^^^^^^^^

The :doc:`/EditBlackboard` contains the features that have been selected for
editing. At any one time a single feature on the edit blackboard is the *edit feature*, the feature
that has the vertex handles and can directly be edited.

**Related concepts**

:doc:`Workbench`

:doc:`Layer`

**Related reference**

:doc:`/EditBlackboard`

:doc:`/Map editor`

**Related tasks**

:doc:`/Working with Features`


Boundary Selection
^^^^^^^^^^^^^^^^^^

Background layers can be marked as Boundary layers, features can be selected from these Boundary
layers and user as a filter on other content.
