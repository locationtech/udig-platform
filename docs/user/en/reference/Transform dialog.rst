Transform dialog
################

The transform dialog is used in a number of places where information needs to be prepared,
manipulated or processed during import, copying or export.

.. figure:: /images/transform_dialog/TransformDialog.png
   :align: center
   :alt: 

Transform
=========

The **Transform** table lists the attributes that will be generated when creating your new features.

The table provides the following information:

-  **Attribute:** name of the attribute being generated
-  **Type:** type of attribute being generated. This value is calculated on the fly as you enter a
   expression.
-  **Expression:** expression used to calculate output values

Add
---

Adds a new blank definition to the transform list; you will need to fill in the name of the
attribute and expression used when defining values.

Up
--

Moves the selected attribute up, this changes the order of the attributes in the generated output.

This is commonly used to reorder columns in a shapefile or database table.

Down
----

Moves the selected attribute down, this changes the order of the attributes in the generated output.

This is commonly used to reorder columns in a shapefile or database table.

Remove
------

Used to remove an attribute from the list.

This is commonly used when removing columns from a shapefile or database table.

Definition
==========

-  **Name:** Name to use when defining the selected attribute. Please keep in mind the limitations
   of your output format when choosing a name. Many formats such as shape file are unable to handle
   names that contain spaces, punctuation or start with a number.

-  **Expression**: The :doc:`/concepts/Constraint Query Language` used to
   generate the values on output. Use the drop down arrow on the right to cycle the :doc:`Expression viewer` 
   through several helpful options.

How would you like to handle the result
=======================================

-  **Add to Map:** can be used when processing a layer, in addition to adding to the catalog the
   result is immediately added to the Map as a preview.

-  **Add to Catalog:** the results are saved into the **Scratch** working area

OK
==

The operation will be started.

Cancel
======

Cancel the operation, no result will be generated.

**Related tasks**

:doc:`Using Feature operations`


**Related reference**

:doc:`Transform operation`

:doc:`Operations dialog`

:doc:`Expression viewer`
