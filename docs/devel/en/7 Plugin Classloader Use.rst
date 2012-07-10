7 Plugin Classloader Use
========================

Eclipse's Classloader maintains seperate classpaths for each plugin.

A plugin's classpath is derived from its parents classpath and the plugin dependencies. It must be
realized that the classpath a plugin has access to is not the same classpath as the environment
provides. For example a java application has a different class path than a plugin.

You can configure your classpath stuff in your Manifest:

+------------+--------------------------+---------------------------------------------------------------------------------------+
|            | parentClassloader=app    | The application class loader                                                          |
+------------+--------------------------+---------------------------------------------------------------------------------------+
|            | parentClassloader=boot   | Your plugin can **just** see JRE classes (ie the boot classpath)                      |
+------------+--------------------------+---------------------------------------------------------------------------------------+
| |image1|   | parentClassloader=ext    | Your plugin can see **JRE** and **JRE extention** classes - such as JAI and ImageIO   |
+------------+--------------------------+---------------------------------------------------------------------------------------+

As noted above - the difficulty with default classloader configuration is that it does not recognize
java extensions such as JAI because the jai jar files are in the jre/lib/ext directory.

For more information please consult the Eclipse Help menu.

.. |image0| image:: images/icons/emoticons/check.gif
.. |image1| image:: images/icons/emoticons/check.gif
