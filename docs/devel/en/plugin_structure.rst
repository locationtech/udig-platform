Plugin Structure
================

A Plugin is defined by the following strucutre .. for a complete list of the usual suspects start
with the :doc:`common_plugin_files`.

+-------------------+-----+-----------------------------------------+
| **src/**          | git | source files                            |
+-------------------+-----+-----------------------------------------+
| **bin/**          |     | generated class files                   |
+-------------------+-----+-----------------------------------------+
| **doc/**          |     | generated schema documentation          |
+-------------------+-----+-----------------------------------------+
| **html/**         | git | online help                             |
+-------------------+-----+-----------------------------------------+
| **/icons/**       | git | icons aranged into categories           |
+-------------------+-----+-----------------------------------------+
| plugin.xml        | git | plugin manifest file                    |
+-------------------+-----+-----------------------------------------+
| plugin.properties | git | plugin manifest internationalization    |
+-------------------+-----+-----------------------------------------+
| build.xml         | git | generated ant export script             |
+-------------------+-----+-----------------------------------------+
| build.properties  | git | describes src, and export contents      |
+-------------------+-----+-----------------------------------------+
|                   |     |                                         |
+-------------------+-----+-----------------------------------------+
| .classpath        |     | generated plug-in classpath information |
+-------------------+-----+-----------------------------------------+
| .project          |     | plug-in project file                    |
+-------------------+-----+-----------------------------------------+

Your first build

Plugins have strict naming conventions for their packages - please consult the :doc:`api_rules_of_engagement` 
for more information.

Related concepts
----------------

`Repository Structure <http://udig.refractions.net/confluence//display/UDIG/Repository+Structure>`_
