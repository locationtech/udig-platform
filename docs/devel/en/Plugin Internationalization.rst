Plugin Internationalization
===========================

The file plugin.properties is used to internationalize the plugin.xml file. Since some of these
descriptions will be showing up in client code.

As an example the view name is supposed to be "a translatable name that will be used in the UI for
this view".

How to acomplish this?

Change the name to a %LocalRegistry (in this example)
 |image0|

Cerate a plugin.properties file with an entry for LocalRegistry.

::

    LocalRegistry=Local Registry
    Provider=Refractions Research, Inc.

You can use this trick to manage many aspects of the plugin.xml file including schema descriptions.

**Links**

* :doc:`http://www.eclipse.org/articles/Article-Internationalization/how2I18n.html`

-  `RCP Tutorial Part
   2 <http://dev.eclipse.org/viewcvs/index.cgi/%7echeckout%7e/org.eclipse.ui.tutorials.rcp.part2/html/tutorial2.html>`_

To internationalize your source code, see `Plugin Internationalization with
ResourceBundles <4%20Plugin%20Internationalization%20with%20ResourceBundles.html>`_.

Problems?
~~~~~~~~~

With Eclipse 3.4 if your strings are internationalized and the plugin.properties file exists;
however they are not showing up as internationalized ensure that the MANIFEST.MF has the following
line.

::

     Bundle-Localization: plugin

.. |image0| image:: /images/plugin_internationalization/InternationalizationPlugInProperties.jpg
