Wps Testing
###########

Community Plugins : WPS Testing

This page last changed on Jul 20, 2010 by admin.

This page contains notes from testing different WPS services. Where possible we try to make use of a
public WPS instance.

`Test Plan <#WPSTesting-TestPlan>`__

`Tasks Generated <#WPSTesting-TasksGenerated>`__

`Open Source WPS Services <#WPSTesting-OpenSourceWPSServices>`__

-  `52North <#WPSTesting-52North>`__
-  `ZooWPS <#WPSTesting-ZooWPS>`__
-  `deegree 3 <#WPSTesting-deegree3>`__
-  `GeoServer <#WPSTesting-GeoServer>`__
-  `PyWPS <#WPSTesting-PyWPS>`__

`Other WPS Instances <#WPSTesting-OtherWPSInstances>`__

-  `Esri Catalog <#WPSTesting-EsriCatalog>`__
-  `Erdas Catalog <#WPSTesting-ErdasCatalog>`__
-  `lat/lon WPS 1.0.0 <#WPSTesting-lat%2FlonWPS1.0.0>`__
-  `UAB-CREAF WPS <#WPSTesting-UABCREAFWPS>`__
-  `Intergraph WPS 1.0.0 <#WPSTesting-IntergraphWPS1.0.0>`__

Related:

-  http://how2map.blogspot.com/2010/06/web-process-service-round-up.html
-  http://how2map.blogspot.com/2010/06/start-your-wps-services.html

Test Plan
=========

Where possible sample data will be used from:

-  http://www.naturalearthdata.com/
-  http://cegrp.cga.harvard.edu/haiti/ (click on the data link)

   -  Major Rivers
   -  Lakes

Does it work:

-  Parse capabilities document - can the entry be added to the catalog?
-  Parse DescribeProcess - does the catalog show the correct titles?

Basic Tests:

-  Geometry: test buffer since every service seems to implement it; should be based on literal
   geometry probably with GML or WKT
-  Features: test feature collection; something simple like statistics
-  Raster: test raster operation; something simple like threshold if we can find it

Advanced Tests:

-  Status: check status of a long running process
-  External Result: result posted to ftp for wfs service

For any issues parsing capabilities document or describeprocess results we will:

#. talk to the project email list
#. produce a test case in geotools with the document in question

Built-in

 

caps

describe

geom

feature

raster

status

external

GeoTools

 

|image0|

|image1|

|image2|

 

 

 

 

WPS (listed FDF Services)

 

caps

describe

geom

feature

raster

status

external

`52North <#WPSTesting-52North>`__

 

|image3|

|image4|

 

 

 

 

 

`ZooWPS <#WPSTesting-ZooWPS>`__

 

|image5|

|image6|

 

 

 

 

 

`deegree 3 <#WPSTesting-deegree3>`__

 

|image7|

|image8|

 

 

 

 

 

`GeoServer <#WPSTesting-GeoServer>`__

 

|image9|

|image10|

 

 

 

 

 

`PyWPS <#WPSTesting-PyWPS>`__

 

 

 

 

 

 

 

 

`UAB-CREAF WPS <#WPSTesting-UABCREAFWPS>`__

MiraMon

|image11|

|image12|

 

 

 

 

 

`Intergraph WPS 1.0.0 <#WPSTesting-IntergraphWPS1.0.0>`__

 

|image13|

|image14|

 

 

 

 

 

`lat/lon WPS 1.0.0 <#WPSTesting-lat%2FlonWPS1.0.0>`__

52North

|image15|

|image16|

 

 

 

 

 

other WPS (listed in Catalog)

WPS

caps

describe

geom

feature

raster

status

external

Centre for Geospatial Science (Nottingham Uni)

52North

|image17|

|image18|

 

 

 

 

 

Jaume I University (Spain)

52North

|image19|

|image20|

 

 

 

 

 

AAFC GDAS (for OGC WPSie)

N/A

|image21|

|image22|

 

 

 

 

 

CIESIN

N/A

|image23|

|image24|

 

 

 

 

 

IDEBarcelona - CAE1M

N/A

|image25|

|image26|

 

 

 

 

 

Tasks Generated
===============

| The following tasks have been generated during testing; will create/link Jira issues.
|  testing)

-  UI Team (Deavi/Jody)

   -  |image27| Create LocalProcessService allowing access to processes defined by GeoTools
      (facilitate UI
   -  |image28| Supply SERVICE=WPS&VERSION=1.0&REQUEST=GetCapabilities in the URL; the wizard should
      be able to add that for us
   -  |image29| Execute a simple local process with literals (ie Geometry Buffer)
   -  Execute a simple local process with features (ie Feature Buffer)
   -  Execute a simple local process with raster (ie raster to vector)
   -  Wish: Remember previous WPS instances (or supply recommendations) in a drop down combo

-  WPS Client Team (Jody/Ben)

   -  |image30| https://jira.codehaus.org/browse/GEOT-3159 WPS UOM Parsing for Capabilities
   -  |image31| https://jira.codehaus.org/browse/GEOT-3160 DescribeProcess failing on 'unity'
   -  Test cases for FeatureCollection Execute
   -  Test cases for Raster Execute

-  Wrap Up

   -  |image32| Video Script
   -  Record Draft Video (Friday)
   -  Record Final Video (Monday)

Open Source WPS Services
========================

Where possible made use of public services provided by the projects.

For services only available in war form:

#. Used Tomcat6 from macports
   (`how-to-install-and-run-tomcat-on-macos <http://mikevalentiner.wordpress.com/2009/01/30/how-to-install-and-run-tomcat-on-macos/>`__)
#. Deployed the war using the Tomcat Manager user interface (ie nothing fancy)

52North
-------

| Service:
http://giv-wps.uni-muenster.de:8080/wps/WebProcessingService?Request=GetCapabilities&Service=WPS
|  Local: http://localhost:8080/wps/WebProcessingService?Request=GetCapabilities&Service=WPS

Does it work:

-  |image33| Capabilities: provided test server works and shows up in the uDig catalog
-  |image34| DescribeProcess: works, uDig catalog updated with correct titles

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

Notes:

-  Installed 52n-wps-wrbapp-2.0-rc4 war; installation was smooth; however capabilities document not
   generated. Going to proceed with provided test instance for now.
-  Connected to WPS uDig community module

ZooWPS
------

Service:
http://shilpa.media.osaka-cu.ac.jp/zoo/?Service=WPS&Request=GetCapabilities&Version=1.0.0&Language=en-CA

Does it work?

-  |image35| Capabilities: provided test server works and shows up in the uDig catalog
-  |image36| DescribeProcess: works, uDig catalog updated with correct titles

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

deegree 3
---------

| Website: http://wiki.deegree.org/deegreeWiki/deegree3/WebProcessingService
|  Service: n/a
|  Download:
http://artefacts.deegree.org/repo/org/deegree/deegree-wps-demo/3.0-pre5/deegree-wps-demo-3.0-pre5.war
|  Localhost:
http://localhost:8080/deegree-wps-demo-3.0-pre5/services?Request=getCapabilities&Version=1.0.0&Service=WPS

Does it work?

-  Capabilities: |image37| Connected
-  DescribeProcess: |image38| Was unable to list the processes

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

Notes:

-  deegree 3 is required as we are testing WPS 1.0 specification
-  Installation

   -  Trying with tomcat6 from macports
   -  deploy went fine
   -  has adorable ASCII art landing page

      ::

             _
           _| | ___  ___  ___  _ _  ___  ___  3
          / . |/ ._>/ ._>/ . || '_>/ ._>/ ._>
          \___|\___.\___.\_. ||_|  \___.\___.
                         <___'
          ...has been successfully installed on this server.

          Version information

          - core, 3.0-pre5 (build@20100622-1705 by hudson)
          - services, 3.0-pre5 (build@20100622-1709 by hudson)

          Active services

          - WPS [details]

          Request statistics

          - Dispatched: 1
          - Active: 0
          - Average: 27 ms
          - Maximum: 27 ms

          [Send requests]

   -  web user interface provided to try out requests

GeoServer
---------

| Website: http://geoserver.org/display/GEOSDOC/4.+WPS+-+Web+Processing+Service
|  Service: n/q
|  Local: (pending)
|  Download: n/q
|  Build:

::

    %> mvn install -Pwps
    %> cd web/app
    %> mvn jetty:run

Does it work?

-  |image39| Capabilities (needed to fix unit handling code before this would work)
-  |image40| DescribeProcess

   -  An incorrect URL was generated for DescribeProcess based on the capabilities document; this
      was promptly fixed when reported.

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

Notes:

-  documentation patch: http://jira.codehaus.org/browse/GEOS-4024

PyWPS
-----

| Website:
|  Service:

Does it work?

-  Capabilities: untested
-  DescribeProcess: untested

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

Notes:

-  community very helpful in pointing me at sample services

Other WPS Instances
===================

Esri Catalog
------------

(Most recently registered WPS services from ESRI Geoportal Extension Sandbox)

+\ http://gptogc.esri.com/GPT931/rest/find/document?searchText=WPS&max=50&f=html+

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Service:
http://v-soknos.uni-muenster.de:8080/wps-ags/WebProcessingService?Request=GetCapabilities&Service=WPS&Version=1.0.0

Does it work?

-  |image41| Capabilities:  Connected
-  |image42| DescribeProcess: works, using HTTP IE

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

 Notes:

-  Service based on the 52north implementation of WPS 1.0.0 

| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
|  Service: http://schemas.opengis.net/wps/1.0.0/examples/20_wpsGetCapabilities_response.xml

Does it work?

-  |image43| Capabilities:  Connected (URL proxy: http://wms1.agr.gc.ca/GeoPS/GeoPS?)
-  |image44| DescribeProcess: fail (The requested URL /GeoPS/GeoPS was not found on this server)

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

 Notes:

-  AAFC GDAS-based WPS server developed for the OGC WPSie.

| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
|  Service: http://beta.sedac.ciesin.columbia.edu/wps/WebProcessingService?

Does it work?

-  |image45| Capabilities:  Connected
-  |image46| DescribeProcess: works, uDig catalog updated with correct titles

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

 Notes:

-  CIESIN Population Statistics WPS
-  Estimates population counts (persons, 2005) within provided polygon features. Uses Gridded
   Population of the World, version 3, population estimates, land areas (square km), and mean
   administrative unit area (square km) to generate a table containing the ...

| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
|  Service: http://sitmun.diba.cat/wps/CAE1M/WebProcessingService?

Does it work?

-  |image47| Capabilities:  Connected
-  |image48| DescribeProcess: works, uDig catalog updated with correct titles

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

 Notes:

-  CAE1M WPS
-  Web Processing Service de la Cartografia de Carrers. [xml:lang="es-ES"] Web Processing Service de
   la Cartografía de Callejero.

Erdas Catalog
-------------

http://projects-eu.erdas.com/ows7-catalog/catalog/content/services/wps

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Service:
http://geoportal.dlsi.uji.es:80/sextantewps100/WebProcessingService?service=WPS&request=GetCapabilities

Does it work?

-  |image49| Capabilities:  Connected
-  |image50| DescribeProcess: works, uDig catalog updated with correct titles

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

 Notes:

| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
|  Service:
http://cgs.nottingham.ac.uk:8080/wps/WebProcessingService?service=WPS&request=GetCapabilities

Does it work?

-  |image51| Capabilities:  Connected
-  |image52| DescribeProcess: works, using HTTP IE

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

 Notes:

lat/lon WPS 1.0.0
-----------------

Service: \ http://ows7.lat-lon.de/d3WPS_JTS/services?service=WPS&request=GetCapabilities

Does it work?

-  |image53| Capabilities:  Connected
-  |image54| DescribeProcess: works, uDig catalog updated with correct titles

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

 Notes:  using degree 3

UAB-CREAF WPS
-------------

| Service1: http://www.creaf.uab.es/cgi-bin/wps/MiraMon.cgi?service=WPS&request=GetCapabilities
|  Service2: http://www.creaf.uab.es/temp/ows7/wps

Does it work?

-  |image55| Capabilities:  Connected
-  |image56| DescribeProcess: works, uDig catalog updated with correct titles

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

Notes:

-  CombineFeatures is very cool and offers the out of band workflow where both GML and XSD are
   published to an FTP serve

Intergraph WPS 1.0.0
--------------------

Service: \ http://ogc.intergraph.com:8000/VectorProcService/WPS?service=WPS&request=GetCapabilities

Does it work?

-  |image57| Capabilities:  Connected
-  |image58| DescribeProcess: works, using HTTP IE

Basic Tests:

-  Geometry: untested
-  Features: untested
-  Raster: untested

Advanced Tests:

-  Status: untested
-  Result: untested

|  Notes:

+-------------+----------------------------------------------------------+
| |image60|   | Document generated by Confluence on Aug 11, 2014 12:24   |
+-------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/check.gif
.. |image1| image:: images/icons/emoticons/check.gif
.. |image2| image:: images/icons/emoticons/check.gif
.. |image3| image:: images/icons/emoticons/check.gif
.. |image4| image:: images/icons/emoticons/check.gif
.. |image5| image:: images/icons/emoticons/check.gif
.. |image6| image:: images/icons/emoticons/check.gif
.. |image7| image:: images/icons/emoticons/check.gif
.. |image8| image:: images/icons/emoticons/check.gif
.. |image9| image:: images/icons/emoticons/check.gif
.. |image10| image:: images/icons/emoticons/check.gif
.. |image11| image:: images/icons/emoticons/check.gif
.. |image12| image:: images/icons/emoticons/check.gif
.. |image13| image:: images/icons/emoticons/check.gif
.. |image14| image:: images/icons/emoticons/check.gif
.. |image15| image:: images/icons/emoticons/check.gif
.. |image16| image:: images/icons/emoticons/check.gif
.. |image17| image:: images/icons/emoticons/check.gif
.. |image18| image:: images/icons/emoticons/check.gif
.. |image19| image:: images/icons/emoticons/check.gif
.. |image20| image:: images/icons/emoticons/check.gif
.. |image21| image:: images/icons/emoticons/check.gif
.. |image22| image:: images/icons/emoticons/error.gif
.. |image23| image:: images/icons/emoticons/check.gif
.. |image24| image:: images/icons/emoticons/check.gif
.. |image25| image:: images/icons/emoticons/check.gif
.. |image26| image:: images/icons/emoticons/check.gif
.. |image27| image:: images/icons/emoticons/check.gif
.. |image28| image:: images/icons/emoticons/check.gif
.. |image29| image:: images/icons/emoticons/check.gif
.. |image30| image:: images/icons/emoticons/check.gif
.. |image31| image:: images/icons/emoticons/check.gif
.. |image32| image:: images/icons/emoticons/warning.gif
.. |image33| image:: images/icons/emoticons/check.gif
.. |image34| image:: images/icons/emoticons/check.gif
.. |image35| image:: images/icons/emoticons/check.gif
.. |image36| image:: images/icons/emoticons/check.gif
.. |image37| image:: images/icons/emoticons/check.gif
.. |image38| image:: images/icons/emoticons/error.gif
.. |image39| image:: images/icons/emoticons/check.gif
.. |image40| image:: images/icons/emoticons/check.gif
.. |image41| image:: images/icons/emoticons/check.gif
.. |image42| image:: images/icons/emoticons/check.gif
.. |image43| image:: images/icons/emoticons/check.gif
.. |image44| image:: images/icons/emoticons/error.gif
.. |image45| image:: images/icons/emoticons/check.gif
.. |image46| image:: images/icons/emoticons/check.gif
.. |image47| image:: images/icons/emoticons/check.gif
.. |image48| image:: images/icons/emoticons/check.gif
.. |image49| image:: images/icons/emoticons/check.gif
.. |image50| image:: images/icons/emoticons/check.gif
.. |image51| image:: images/icons/emoticons/check.gif
.. |image52| image:: images/icons/emoticons/check.gif
.. |image53| image:: images/icons/emoticons/check.gif
.. |image54| image:: images/icons/emoticons/check.gif
.. |image55| image:: images/icons/emoticons/check.gif
.. |image56| image:: images/icons/emoticons/check.gif
.. |image57| image:: images/icons/emoticons/check.gif
.. |image58| image:: images/icons/emoticons/check.gif
.. |image59| image:: images/border/spacer.gif
.. |image60| image:: images/border/spacer.gif
