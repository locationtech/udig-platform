Repository Structure
####################

uDig : Repository Structure

This page last changed on Oct 26, 2006 by chorner.

The uDig repository is host to several kinds of development:

Project

Development

Plug-In

The main building block of eclipse development, defines the uDig Framework and Extension Points

JUnit Test Plug-In

Used to test associated Plug-In

Plug-In Fragment

Provides a patch at the Plug-In level, for bug fixes or additional language support

Feature

Bundle of Plug-Ins and Plug-In Fragments packaged for installation

We have done our best to follow the "usual" pattern for the naming and directory structure for each
of these kinds of projects.

Project Naming Conventions
~~~~~~~~~~~~~~~~~~~~~~~~~~

All plug-ins core to the uDig framework will be named starting with **net.refractions.udig**.

Project

Example

Naming Convention

Plug-In

net.refractions.udig.render

named in agreement with internal package structure

JUnit Test Plug-In

net.refractions.udig.render-test

Append "-test"

Plug-In Fragment

net.refractions.udig.german

Provide ".*language*" file at the root udig

Plug-In Fragment

net.refractions.render-1

Do anything except add a dot

Features

net.refractions.udig.render-feature

Append "-feature" to associated root Plug-In

Repository Structure
~~~~~~~~~~~~~~~~~~~~

svn.geotools.org/udig/trunk

uDig Project

doc/

shared documentation for the uDig application

deploy/

contains Rich Client Platform Update site project and related deploy materials

plugins/

location of Plug-Ins

features/

location of Features

fragments/

location of Fragments

Core Features
~~~~~~~~~~~~~

svn.geotools.org/udig/trunk/features

net.refractions.udig.gisapplication

net.refractions.udig.gisplatform

net.refractions.udig.printing

net.refractions.udig.release

net.refractions.udig.tests

Core Plug-Ins
~~~~~~~~~~~~~

Currently the uDig application is comprised of the following Plug-Ins.

svn.geotools.org/udig/trunk/plugins

Core Plug-Ins

net.refractions.udig.catalog

Catalog model definition

net.refractions.udig.catalog.arcsde

arcsde

net.refractions.udig.catalog.cgdi

cgdi

net.refractions.udig.catalog.geotiff

geotiff

net.refractions.udig.catalog.gml

gml

net.refractions.udig.catalog.google

google

net.refractions.udig.catalog.memory

memory

net.refractions.udig.catalog.oracle

oracle

net.refractions.udig.catalog.postgis

postgis

net.refractions.udig.catalog.rasterings

rasterings

net.refractions.udig.catalog.shp

shp

net.refractions.udig.catalog.ui

ui

net.refractions.udig.catalog.wfs

wfs

net.refractions.udig.catalog.wms

wms

net.refractions.udig.catalog.worldimage

worldimage

net.refractions.udig.core

uDig framework and model definition

net.refractions.udig.feature.editor

feature editor

net.refractions.udig.help

uDig online help

net.refractions.udig.libs

groups jars from other projects

net.refractions.udig.mapgraphic

mapgraphic

net.refractions.udig.printing.edit

printer support

net.refractions.udig.printing.model

printing model definition

net.refractions.udig.printing.ui

printing views and user interface

net.refractions.udig.project

project model definition

net.refractions.udig.project.edit

project edit

net.refractions.udig.project.ui

project views and user interface

net.refractions.udig.render.feature.basic

low-level rendering

net.refractions.udig.render.feature.shapefile

low-level rendering

net.refractions.udig.render.gridcoverage.basic

low-level rendering

net.refractions.udig.render.wms.basic

low-level rendering

net.refractions.udig.render.wms.tiling

low-level rendering

net.refractions.udig.style

style Model

net.refractions.udig.style.sld

SLD Style

net.refractions.udig.tool.default

Default Tools

net.refractions.udig.tool.info

Information Tools

net.refractions.udig.tool.select

Selection Tools

net.refractions.udig.ui

uDig application perspective and views

svn.geotools.org/udig/trunk/fragments

net.refractions.udig.catalog.arcsde.nl1

net.refractions.udig.catalog.cgdi.nl1

net.refractions.udig.catalog.geotiff.nl1

net.refractions.udig.catalog.gml.nl1

net.refractions.udig.catalog.google.nl1

net.refractions.udig.catalog.oracle.nl1

net.refractions.udig.catalog.postgis.nl1

net.refractions.udig.catalog.shp.nl1

net.refractions.udig.catalog.ui.nl1

net.refractions.udig.catalog.wfs.nl1

net.refractions.udig.catalog.wms.nl1

net.refractions.udig.catalog.worldimage.nl1

net.refractions.udig.feature.editor.nl1

net.refractions.udig.help.nl1

net.refractions.udig.libs.nl1

net.refractions.udig.mapgraphic.nl1

net.refractions.udig.printing.edit.nl1

net.refractions.udig.printing.model.nl1

net.refractions.udig.printing.ui.nl1

net.refractions.udig.project.edit.nl1

net.refractions.udig.project.nl1

net.refractions.udig.project.ui.nl1

net.refractions.udig.render.feature.basic.nl1

net.refractions.udig.render.gridcoverage.basic.nl1

net.refractions.udig.render.wms.basic.nl1

net.refractions.udig.render.wms.tiling.nl1

net.refractions.udig.style.nl1

net.refractions.udig.style.sld.nl1

net.refractions.udig.tool.default.nl1

net.refractions.udig.tool.info.nl1

net.refractions.udig.tool.select.nl1

net.refractions.udig.ui.nl1

net.refractions.udig.catalog.nl1

net.refractions.udig.core.nl1

Extension Plug-Ins
~~~~~~~~~~~~~~~~~~

In addition we plan to host the development of additional uDig features.

svn.geotools.org/udig/community/validation/trunk

sample plugin

plugins/org.geotools.validation.core

validation plug-in to uDig framework

plugins/org.geotools.validation.ui

validation views and user interface

features/org.geotools.validation-feature

feature bundling extention

Links:

-  `Automating Product Builds with PDE
   Build <http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.releng.basebuilder/readme.html?rev=HEAD&content-type=text/html>`__
-  `Keeping Up to Date <http://www.eclipse.org/articles/Article-Update/keeping-up-to-date.html>`__

+-------------------------------------------------------------------------------------------------------------------------------------------------+
| |image1|                                                                                                                                        |
| **Next**                                                                                                                                        |
| Cannot resolve external resource into attachment. `PlugIn Structure <http://udig.refractions.net/confluence//display/DEV/PlugIn+Structure>`__   |
+-------------------------------------------------------------------------------------------------------------------------------------------------+

+------------+----------------------------------------------------------+
| |image3|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/information.gif
.. |image1| image:: images/icons/emoticons/information.gif
.. |image2| image:: images/border/spacer.gif
.. |image3| image:: images/border/spacer.gif
