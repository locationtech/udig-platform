Plugin Internationalization
===========================

The file plugin.properties is used to internationalize the plugin.xml file. Since some of these
descriptions will be showing up in client code.

As an example the view name is supposed to be "a translatable name that will be used in the UI for
this view".

How to acomplish this?

Change the name to a %LocalRegistry (in this example)

.. image:: /images/plugin_internationalization/InternationalizationPlugInProperties.jpg

Cerate a plugin.properties file with an entry for LocalRegistry.

.. todo:: 
   Change provider here ..

   
::

    LocalRegistry=Local Registry
    Provider=Refractions Research, Inc.

You can use this trick to manage many aspects of the plugin.xml file including schema descriptions.

**Links**

* `http://www.eclipse.org/articles/Article-Internationalization/how2I18n.html <http://www.eclipse.org/articles/Article-Internationalization/how2I18n.html>`_
* `RCP Tutorial Part 2 <http://dev.eclipse.org/viewcvs/index.cgi/%7echeckout%7e/org.eclipse.ui.tutorials.rcp.part2/html/tutorial2.html>`_

To internationalize your source code, see :doc:`Plugin Internationalization with ResourceBundles <plugin_internationalization_with_resourcebundles>`.

Problems?
---------

With Eclipse 3.4 if your strings are internationalized and the plugin.properties file exists;
however they are not showing up as internationalized ensure that the MANIFEST.MF has the following
line.

::

     Bundle-Localization: plugin

