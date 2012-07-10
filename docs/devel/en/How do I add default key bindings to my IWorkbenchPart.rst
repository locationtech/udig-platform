How do I add default key bindings to my IWorkbenchPart
======================================================

How do I add default key bindings to my IWorkbenchPart?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**A:** Simply add this line to the createPartControl() method of your workbench part.

::

    ApplicationGIS.getToolManager().registerActionsWithPart(this);

