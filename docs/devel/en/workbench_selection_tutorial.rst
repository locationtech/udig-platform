Workbench Selection Tutorial
============================

This tutorial goes over creating a new view which will watch the workbench selection and use
IAdatable to show how the uDig application functions at runtime.

.. _WorkbenchSelection.pdf: http://udig.refractions.net/files/tutorials/WorkbenchSelection.pdf

.. image:: /images/workbench_selection_tutorial/WorkbenchSelectionWorkbook.png
   :target: WorkbenchSelection.pdf_

References:

* `http://www.eclipsezone.com/articles/what-is-iadaptable/ <http://www.eclipsezone.com/articles/what-is-iadaptable/>`_

This workbook is part of our public training materials:

* WorkbenchSelection.pdf_

Resources:

* `workbench_icons.zip`

Source code:

-  Available in your uDig SDK (import the :doc:`source code <code_examples>` from the plugins view)
-  plugin:
   `org.locationtech.udig.tutorials.workbench <https://github.com/uDig/udig-platform/tree/master/plugins/org.locationtech.udig.tutorials.workbench>`_ (github)

Introduction
------------

In this tutorial you will:

-  Create a new View
-  Locate an Eclipse RCP Service
-  Listen for the workbench selection
-  Use "IAdaptable" to allow a single selection entry to represent multiple values

This workbook answers the bigger question of "Where to Start" when making your own application.

You will often start by contributing to the user interface - in this case we are defining a new
view. This definition consists of both an XML fragment being added to the plugin.xml defining the
title, icon etc... and a new class implementing the user interface. In other cases you may be
defining a new menu option or a new tool.

The second step will be paying attention to the what the user is up to - in this case we locate the
SelectionService for the workbench window. In other cases you may be checking the current Map or the
currently selected Layer.

Finally we will be acting when the user does something - in this case we are waiting for the user to
select something and reporting back on what we find.

.. figure:: /images/workbench_selection_tutorial/WorkbenchSelection.jpg
   :align: center
   :alt: 

What to Try Next
----------------

Here are some additional challenges for you to try:

MapEditor ModalTool Selection
`````````````````````````````

You should have noticed that each View provides a unique selection. Did you also notice that the Map
Editor will change what workbench selection it provides based on the current modal tool.

Explore the available tools and note what content each tool thinks it is working on.

Perspective Extension for Show View Menu
````````````````````````````````````````

Currently, if you want to see the workbench view you have to select Window>Show View>Other to open a
the Show view dialog. You can then use the Show view dialog to navigate to Other >Select View.

Can you use a "perspectiveExtension" make your view show up under the main view menu?

Advertise a Selection to the Workbench (Advanced)
`````````````````````````````````````````````````

Advanced: We have focused on listening to the workbench selection. Can you use getViewSite() to
advertise an object to the workbench selection service? As a side effect, the Selection View you've
just created will listen to itself

Advanced: In this example we have checked "instance of" and "IAdaptable". Can you extend this
example to check IResolve?

Use of IResolve (Advanced)
``````````````````````````

IResolve is uDig specific and represents external content. You should be very careful to read the
javadocs and not call any methods from the event thread that may block while waiting for a WFS
service on the other side of the work. If you make a mistake here it will look like the uDig
application has "hung".

The uDig API very carefully throws IOExceptions when ever there is a chance of waiting for an
external service. If you find yourself doing a try/catch block while in an event thread you have
probably made a mistake!

Use of IAdaptable
`````````````````

Advanced: If you've done the IAdaptable workbook, you will note that your SelectionView tells you an
IService is selected and gives you its URL. Similarly with an IGeoResource. However, it doesn't seem
to be able to adapt them to URLs...go ahead and fix that.

Hint: you'll need to use the AdapterUtil class.

Tips, Tricks and Suggestions
----------------------------

Workbench Services
``````````````````

The use of Workbench "Services" is similar to a global scratch pad - this an example of the
"blackboard" design pattern.

The idea is that workbench services act as a place for plugins to communicate with each other.
Specifically for SelectionService it allows the plugins to report on what the user is doing.

The upside of this is that the workbench as a whole appears to be an integrated application; when in
fact each of the plugins have never been formally introduced.

**Concept**: Workbench selection is used to communicate between plugins.

Extensible Interface
````````````````````

The next idea presented here is that of an "extensible interface". We are used to as Java developers
the idea of a class implementing an interface.

We can check what interfaces an object implements at runtime:

.. code-block:: java

    if( obj instanceof URL){
        URL url = (URL) obj;
        System.out.println("URL:"+url);
    }

The extensible interface idea allows programers to "extend" the number of interfaces an object can
be converted to at runtime.

In eclipse this is handled by the **IAdaptable** interface which is great for information that is
held in memory:

.. code-block:: java

    URL url = (URL) adaptable.getAdapter( URL.class );
    if( url != null ){
        System.out.println("URL:"+url);
    }

If you like you can extend **PlatformObject** as a quick way to implement IAdaptable.

uDig uses this same general approach to handle external resources (that may throw an IOException):

.. code-block:: java

    if( geoResource.canResolve( URL.class ) ){
         try {
             URL url = geoResource.resolve( URL.class, new NullProgressMonitor() );
             System.out.println("URL:"+url);
         }
         catch( IOExeption eek){
             System.err.println("Could not determine URL for "+geoResource.getID() );
         }
    }

As an example you can select a Shapefile in the udig catalog and resolve it to a
**org.geotools.data.DataStore**. This may throw an IOException if the user does not have read
permission for the file.

**Concept**: A single selection can "Adapt to" multiple Java Interfaces as needed.

uDig 1.1 version of this workbook
`````````````````````````````````

For uDig 1.1 developers the previous version of this document is available

* `http://udig.refractions.net/files/tutorials/workbench.pdf <http://udig.refractions.net/files/tutorials/workbench.pdf>`_

