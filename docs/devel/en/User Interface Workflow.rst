User Interface Workflow
=======================

Eclips RCP gives us two user interface workflows (for managing the workbench and managing editors in
the workbench). This covers a lot of ground (from navigation and bookmarks to an undo system).

RCP SDK:

-  `Workbench Workflow <#UserInterfaceWorkflow-WorkbenchWorkflow>`_ - open / exit
-  `Map Workflow <#UserInterfaceWorkflow-MapWorkflow>`_ - open / navigate / close

In adition uDig contributes several workflows for content we manage:

uDig SDK:

-  `Service Workflow <#UserInterfaceWorkflow-ServiceWorkflow>`_ - import / export service
-  `Scratch Workflow <#UserInterfaceWorkflow-ScratchWorkflow>`_ - create / save
-  `Edit Workflow <#UserInterfaceWorkflow-EditWorkflow>`_ - edit tools / commit / revert
-  `Task Workflow <#UserInterfaceWorkflow-TaskWorkflow>`_ - validate / tasks / fix

Where possible we have maintained compatibility with the Eclipse User Interface Guidelines.

Eclipse RCP Workflows
=====================

Workbench Workflow
------------------

You can create multiple workbench windows, closing the last window will exit the application.

When you exit the application you will be prompted to "save":

-  any modified maps
-  any temporary results

Map Workflow
------------

As an actual RCP "Editor" our Maps follow a open / save / close / close all work flow.

We make use of navigation tools to control what is visible in the Map. Navigation forward and back
buttons allow the user to sift through previous locations.

Locations can be bookmarked for future reference.

Close
~~~~~

Close will prompt the user for action if there is any:

-  unsaved changes to the Map
-  transactions with external services

Layers
~~~~~~

Maps are defined as a series of layers:

-  New Layer - looks wrong?
-  Add Layer
-  Delete Layer

A Layer is defined as a reference to a GeoResource and a Style. Any change to Map structure
(including Layer style) is tracked as an edit and can be managed with the Undo / Redo history.

Save (and SaveAll)
~~~~~~~~~~~~~~~~~~

Save is used to store the current state of the Map (ie layer information a and viewport).

Since we are a client application the Map also be involved in:

-  transactions with external services
-  creation of temporary (ie scratch) content

uDig SDK
========

Service Workflow
----------------

Services are used to access geospatial content; while managing services is a perfectly reasonable
activity it is not the point of a GIS application.

You can **Import** additional services into the uDig application, and also **Remove** services that
are not in use.

Export
~~~~~~

You can use export to take information known to uDig and export it to an existing Service. In the
case of file formats you can create a new File to hold the content.

Scratch Workflow
----------------

The concept of a "Scratch pad" is used to handle temporary results; there are two ways in which a
temporary result would be created:

-  explicitly using Layer > Create
-  explicitly using a Editing Tool

A scratch layer lasts for the duration of the uDig application; the user is prompted to to save a
scratch layer:

-  when they close a map that displays the scratch layer
-  when they exit the application

When saved (similar to export) a scratch layer will be replaced with the newly created georesoruce.

The Scratchpad service is avaialble to any plugin developer wishing to work with temporary results.

Edit Workflow
-------------

Edit tools and various feature editors allow for interaction with external services. External
services are handled within a transaction using a commit / rollback workflow.

Any number of changes may be made (using any view or edit tool), the changes are not sent to the
server until commit is called.

Task Workflow
-------------

The Task workflow is a traditional user focused workflow; the list of tasks represents work that
needs to be done; each task can carry with it enough context information to help the user address
the task.

Here is an example of its use with the validation operation:

#. Validation Operation generates a series of Issues
#. User preses "Fix" on an Issue
#. The feature in question is brought up on screen

In a workgroup environment the Task list should be stored on a shared database; or intergrated with
an existing workflow system. The Task workflow is available to any uDig plugin developer.
