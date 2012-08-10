Common Plugin Files
###################

The following represents all the "well-known" files you will encounter during plugin development.

Links are povided to the pages where the file is discussed.


.. list-table::
   :widths: 40 60
   :header-rows: 1

   * - plugin xyz
     - Links to definition/explaination
   * - **src/**
     - :doc:`source_files <creating_a_plugin>` 
   * - **src/net/refractions/udig/xyz**
     - :doc:`API Package <api_rules_of_engagement>`
   * - src/net/refractions/udig/xyz/\ :doc:`XYZPlugin.java <plugin_activator>`
     - plugin resource and lifecycle management
   * - src/net/refractions/udig/xyz/\ :doc:`XYZPlugin.properties <plugin_internationalization_with_resourcebundles>`
     - option 1 for internationlization
   * - src/net/refractions/udig/xyz/\ :doc:`ISharedImages.java <imageregistry_and_images>`
     - Constants to retrive shared images
   * - **src/net/refractions/udig/xyz/internal**
     - :doc:`Non API Package <api_rules_of_engagement>`
   * - src/net/refractions/udig/xyz/internal/\ :doc:`IHelpContextIds.java <plugin_help>`
     - constants used for context sensitve help
   * - src/net/refractions/udig/xyz/internal/\ :doc:`IImageConstants.java <imageregistry_and_images>`
     - Constants used to retrive images
   * - src/net/refractions/udig/xyz/internal/\ :doc:`Images.java <imageregistry_and_images>`
     - Manages images for plugin class
   * - src/net/refractions/udig/xyz/internal/\ :doc:`Messages.java <plugin_internationalization_with_resourcebundles>`
     - option 2 for internationlization
   * - src/net/refractions/udig/xyz/internal/\ :doc:`Messages.properties <plugin_internationalization_with_resourcebundles>`
     - text for internationlization
   * - src/net/refractions/udig/xyz/internal/\ :doc:`Policy.java <plugin_internationalization_with_resourcebundles>`
     - option 3 for internationlization
   * - **src/net/refractions/udig/xyz/examples**
     - :doc:`Non API Package <api_rules_of_engagement>`
   * - **src/net/refractions/udig/xyz/tests**
     - :doc:`Non API Package <api_rules_of_engagement>`
   * - **bin/** - don't add to svn/git
     - :doc:`generated_class_files <creating_a_plugin>`
   * - **doc/** - don't add to svn/git
     - :doc:`generated_schema_doc_files <creating_and_using_extension_points>`
   * - doc/xyz\_schema.xml
     - :doc:`generated_schema_doc_files <creating_and_using_extension_points>`
   * - **html/**
     - :doc:`online help <plugin_help>`
   * - html/aHtmlDocument
     - :doc:`an online help page <plugin_help>`
   * - **/icons/dlcl16/**
     - :doc:`disabled local toolbar <icons>`
   * - **/icons/dtool16/**
     - :doc:`disabled toolbar <icons>`
   * - **/icons/dview16/**
     - :doc:`disabled view <icons>`
   * - **/icons/elcl16/**
     - :doc:`enabled local toolbar <icons>`
   * - **/icons/etool16/**
     - :doc:`enabled toolbar <icons>`
   * - **/icons/eview16/**
     - :doc:`enabled view <icons>`
   * - **/icons/obj16/**
     - :doc:`model object <icons>`
   * - **/icons/ovr16/**
     - :doc:`overlay <icons>`
   * - **/icons/wizban/**
     - :doc:`wizard banner <icons>`
   * - plugin.xml
     - :doc:`plugin manifest file <plugin_setup>`
   * - plugin.properties
     - :doc:`plugin manifest internationalization <plugin_setup>`
   * - build.xml
     - :doc:`generated ant export script <plugin_setup>`
   * - build.properties
     - :doc:`describes src, and export contents <plugin_setup>`
   * - .classpath
     - `generated plug-in classpath information <http://udig.refractions.net/confluence//display/ADMIN/08+Libs+Jars>`_
   * - .project
     - `plug-in project file <http://udig.refractions.net/confluence//display/ADMIN/08+Libs+Jars>`_
   * - toc.xml
     - :doc:`table of contents for the top level book <plugin_help>`
   * - tocSubject.xml
     - :doc:`table of contents for subject <plugin_help>`
   * - contexts.xml
     - :doc:`Maps context ids to context-sensitve help <plugin_help>`

