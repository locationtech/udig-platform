09 uDig menus using Operations and Tools
========================================

We have also set a couple of uDig specific ways of contributing to menus and actions bars. While
these are uDig specific solutions they will processed into the right RCP technique as needed.

* :doc:`Adding a Menu using Operation`

* :doc:`Adding a Menu using Tool`


Adding a Menu using Operation
-----------------------------

The operation extention point provided as part of the GIS Platform (net.refractions.udig.catalog.ui)
allows you to indicate where operations will be added to the menu bar.

This information should only be filled in as an override; Operation category will be used to
position contributions in the correct location (on the menu bar or context menu as needed).

Related:

* :doc:`5 Operations`


Adding a Menu using Tool
------------------------

The tool extension point provided as part of the GIS Application (net.refractions.udig.project.ui)
allows you to indicate where tools
 will be added to the menu bar.

This information should only be filled in as an override; Tool category will be used to position
contributions in the correct location (on the menu bar or context menu as needed).

Related:

* :doc:`06 Tools`


