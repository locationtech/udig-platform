Code Examples
~~~~~~~~~~~~~

The uDig SDK (and development team) makes many useful examples. This page covers several ways of
getting the examples into your workspace so you can review and run them.

Please note these instructions can be followed to access both code examples (ie not always runnable)
and tutorials from the training course. In addition you can use this technique to load up parts of
the uDig application into your workspace for study and fixing.

We will use the **org.locationtech.udig.tutorials.examples** plug-in in the instructions provided
below; you can use the same technique to import any plug-in for study.

h14 Import Examples Directly from your SDK

You can do this quickly now:

#. Switch to the **Plug-in Development Perspective**

   -  Click on the Change Perspective button and choose **Other**
   -  From the **Open Perspective** dialog choose **Plug-in Development**

#. Open the **Plug-ins** view
#. Select **org.locationtech.udig.tutorials.examples** from the list
#. Right click and choose **Import As > Source Project**
#. The plug-in will be copied into your workspace so you can review it

Please note you can delete this plug-in from your workspace at any time; the origional in still in
the SDK in case you want to restore it after breaking one of the examples.

Reading the Code Examples
^^^^^^^^^^^^^^^^^^^^^^^^^

You can view the "live" code examples directly from git:

* `https://github.com/uDig/udig-platform/tree/master/plugins/org.locationtech.udig.tutorials.examples/src/net/refractions/udig/tutorials/examples <https://github.com/uDig/udig-platform/tree/master/plugins/org.locationtech.udig.tutorials.examples/src/net/refractions/udig/tutorials/examples>`_

Checking out Examples from GIT
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

You can also check out the source code for the project:

#. `01 Git Install <http://udig.refractions.net/confluence//display/ADMIN/01+Git+Install>`_):
#. `02 Checkout Source
   Code <http://udig.refractions.net/confluence//display/ADMIN/02+Checkout+Source+Code>`_
#. Select **File > Import**
#. Choose **General > Existing Projects into Workspace**
#. From the **Import Projects** page browse to: **tutorials**
#. Choose **org.locationtech.udig.code.examples** from the list
    |image0|
#. Press **Finish**

.. |image0| image:: /images/code_examples/ImportExamples.PNG
