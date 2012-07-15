Plugin Setup
~~~~~~~~~~~~

Now that you have created a plug-in it is time to play the set up game.
 It may be helpfull to review the `Common Plugin Files <Common%20Plugin%20Files.html>`_.

We are going to be setting up the following:

-  plugin.xml
-  build.properties
-  plugin.properties
-  build.xml (optional)
-  .classpath
-  .project

Setting up plugin.xml
^^^^^^^^^^^^^^^^^^^^^

The plugin.xml file is known as the manifest and provides all the hooks used to tie together the
Eclipse framework.

Some of these entries were filled out for you during `Creating a
Plugin <Creating%20a%20Plugin.html>`_.

-  id: id for the plugin, each plug-in gets its own package tree to prevent source code conflict
-  name: externalized to plugin.properties, display name for Plug-in
-  version: release version based
-  provider-name: externalized to plugin.properties, usual "Refractions Research, Inc."
-  runtime: identify jars for this plug-in
-  requires: captures dependency information, this is a soft link that is resolved via "dynamic
   classpaths" as part of the eclipse plug-in development environment

   -  note import is based on **plugin**

-  extension: associates id with well known `What is an Extension
   Point <What%20is%20an%20Extension%20Point.html>`_ with classes in this plug-in

   -  try and make **id** match the class
   -  top level **id** entries have the plugin "id" prepended (like "org.geotools.udig.ui""."
      "UDigApplication"
   -  nested **id** entries need to be explicit (like "org.geotools.udig.ui.UDigPerspective")

::

    <?xml version="1.0" encoding="UTF-8"?>
    <?eclipse version="3.0"?>
    <plugin
        id="org.geotools.udig.ui"
        name="%pluginName"
        version="0.1"
        provider-name="%providerName">
        <runtime>
            <library name="udig-ui.jar">
                <export name="*"/>
            </library>
        </runtime>
        <requires>
            <import plugin="org.eclipse.core.runtime"/>
            <import plugin="org.eclipse.ui"/>
        </requires>

        <extension
            id="UDigApplication"
            point="org.eclipse.core.runtime.applications">
            <application>
                <run
                    class="org.geotools.udig.ui.UDigApplication">
                </run>
            </application>
        </extension>
        <extension
            point="org.eclipse.ui.perspectives">
            <perspective
                name="%perspectiveName"
                class="org.geotools.udig.ui.CatalogPerspective"
                id="org.geotools.udig.ui.CatalogPerspective">
            </perspective>
        </extension>
    </plugin>

Setting up plugin.properties
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The plugin.properties file is used to externalize strings from the plugin.xml manifest. This is good
practice and will allow us to
 provide French support later.

::

    pluginName = UDig Application Plug-in
    providerName = Refractions Research, Inc.
    perspectiveName = Local Catalog

For more information:

* :doc:`plugin_internationalization`


Setting up build.properties
^^^^^^^^^^^^^^^^^^^^^^^^^^^

The build.properties file is used when bundling up the plug-in for use:

::

    bin.includes = plugin.xml,\
                   *.jar,\
                   udig-core.jar,\
                   plugin.properties
    source.udig-core.jar = src/

Setting up build.xml
^^^^^^^^^^^^^^^^^^^^

Use of an ANT build.xml file for plug-in bundling is optional (although it does save you from using
the wizard everytime).

If you want to create an ANT build file to build your plug-in:

#. Right click on your plugin.xml file
#. Select PDE-Tools-> Ant build file

When we set up nightly builds this step will be mandatory.

**Dependency Hack**

To set up proper dependencies, do the following:

--------------

Set up proper classpath:

#. Right click on project, select properties
#. Select Java Build Path
#. Select Projects tab

   -  Check org.eclipse.core.runtime
   -  Ok.

#. Add the following to your plugin.xml:

   ::

       <requires>
          <import plugin="org.eclipse.core.runtime.compatibility"/>
          <import plugin="org.eclipse.ui"/>
       </requires>

**Reference**

-  `Rich Client Paltform Tutorial - Part
   1 <http://dev.eclipse.org/viewcvs/index.cgi/%7echeckout%7e/org.eclipse.ui.tutorials.rcp.part1/html/tutorial1.html>`_

   -  Handy for setting up the initial app, but also contains more information regarding the UI.
   -  A lot of this page is ripped from there


