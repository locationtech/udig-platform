Plugin Structure
################

A Plugin is defined by the following strucutre .. for a complete list of the usual suspects start
with the `Common Plugin Files <Common%20Plugin%20Files.html>`_.

**src/**

svn

source files

**bin/**

 

generated class files

**doc/**

 

generated schema documentation

**html/**

svn

online help

**/icons/**

svn

icons aranged into categories

plugin.xml

svn

plugin manifest file

plugin.properties

svn

plugin manifest internationalization

build.xml

svn

generated ant export script

build.properties

svn

describes src, and export contents

 

 

 

.classpath

 

generated plug-in classpath information

.project

 

plug-in project file

Your first build

Plugins have strict naming conventions for their packages - please consult the `API rules of
engagement <API%20rules%20of%20engagement.html>`_ for more information.

.. figure:: http://udig.refractions.net/image/DEV/ngrelc.gif
   :align: center
   :alt: 

`Repository Structure <http://udig.refractions.net/confluence//display/UDIG/Repository+Structure>`_
