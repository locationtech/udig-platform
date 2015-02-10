Cross Project Wps Planning
##########################

Community Plugins : Cross Project WPS Planning

This page last changed on Jun 24, 2010 by jgarnett.

This page is hiding in the udig wiki for two reasons: I need to think about an end to end solution
and the GeoServer wiki is in a state of flux as they migrate system.

+-----------------------------------+-----------------------------------+-----------------------------------+
| Contents of Page:                 |
|                                   |
| -  `Project Guidelines and        |
|    Scope <#CrossProjectWPSPlannin |
| g-ProjectGuidelinesandScope>`__   |
| -  `Web Processing Service 1.0    |
|    Specification <#CrossProjectWP |
| SPlanning-WebProcessingService1.0 |
| Specification>`__                 |
| -  `Planning <#CrossProjectWPSPla |
| nning-Planning>`__                |
| -  `Use-Case <#CrossProjectWPSPla |
| nning-UseCase>`__                 |
|                                   |
| Further details:                  |
+-----------------------------------+-----------------------------------+-----------------------------------+

This project was completed in 2008 (in the sense that the funding Refractions had obtained is
finished!). Work on both the GeoTools process API and the GeoServer WPS community module has proceed
on personal time thanks to Jody, Andrea, and Micheal.

Subsequent activity:

-  Process API refined to allow a factory to support many operations; enabling Sextante integration
   with GeoTools / GeoServer.
-  JGrass is evaluating the Process API but has not announced anything yet
-  GeoServer WPS module has been fixed up by Justin to support feature collections and rasters (this
   makes it complete with respect to the original project goals)

Related:

-  http://docs.codehaus.org/display/GEOTDOC/Process+Plugin
-  http://code.google.com/p/jgrasstools/wiki/GeotoolsProcessApi
-  http://geoserver.org/display/GEOSDOC/4.+WPS+-+Web+Processing+Service

Project Guidelines and Scope
============================

Project Guidelines:

-  Respect Process Contributors: Make the **Process** interface used by Java developers to implement
   new a new process as easy as possible.
-  End-to-End: We need to always keep the client and server abilities in sync - if we cannot
   demonstrate a capability in uDig / GeoTools / GeoServer it is not worth doing yet.
-  Time to implement: we are on the clock and must complete the project on time
-  Community collaboration
-  Don't build a process engine

Links:

-  `WPS 1.0 Specification <http://portal.opengeospatial.org/files/?artifact_id=24151&version=2>`__
-  http://geoserver.org
-  http://geotools.org
-  `GeoServer IRC Logs March
   4th <http://docs.codehaus.org/display/GEOS/2008/03/04/IRC+Logs%2C+March+4>`__
-  https://52north.org/twiki/bin/view/Processing/52nWebProcessingService

Web Processing Service 1.0 Specification
========================================

Impressions
-----------

-  there is a lot to like; the specification is small enough to accomplish
-  this is about as small a service as can be found and represents a low-risk test of the OGC
   WSDL/SOAP direction
-  the lack of a CITE test increases risk; the fact that we are considering an end-to-end solution
   mitigates this risk
-  trouble is taken to set up WPS process as "Atomic", but comparisons to WSDL/SOAP take care to
   show that they are not atomic
-  this is the usual OGC game; the data and process time is too large to take an out of the box
   solution like WSDL/SOAP
-  the WPS specification is centered around the idea of "web accessible" resources; this is an
   important difference from the WPS / WSDL stack. The OGC knows up front that the data needs to be
   handled "out of band" from the request/response system - a lot of care is taken to make sure that
   a WFS request can be provided as a prameter; and that results can be published to a FTP site.
-  Even with the above enthusiasm I am concerned that deep chains will produce timeouts once any
   real work begins
-  Diagram C.8 is missing (page 65); will ask the standards@osgeo.org list for a contact

Considerations
--------------

-  Handling WPS "end to end" is a strict requirement; but the work can still be broken down into
   milestones by content and protocol.
-  GeoServer has great support parsing/encoding various formats, on the GeoTools it is preferable to
   keeping the API at the Object level.
-  GeoServer "configuration" has slowed down a lot of work; WPS should not be effected as
   implementations generally are too simple to require configuration. Previously community modules
   were provided a "memento" for configuration; if that is still the case it will suffice for our
   modest needs.
-  The GML chaining example is constrained; a getFeatures request (or even GML) is not always enough
   - it would of been better to be given the WFS end point and the query. I can sympathize with this
   need ...

Community and Collaborations
----------------------------

-  A popular request if for a REST front end; while SOAP/WSDL is outlined in the WPS specification
   it is a well understood problem to turn a SOAP API into a REST API. This work is out of scope,
   but should be allowed for in our design.
-  I have been asked by a community member to leave room open to "hot-swap" WPS implementations;
   this should not effect the core definition of the problem and should be considered in our design.
-  Puzzle and uDig desktop projects have been looking hard at a `Process
   proposal <http://docs.codehaus.org/display/GEOTOOLS/Process+proposal>`__; ironically we arrived
   at an almost identical solution to that presented by the WPS specification. Please keep in mind
   that this API is close but it was developed in isolation and has the goal of making a Process
   easy to write for the Java developer. In the past around four "operations" proposals have failed
   so we need to remember to keep it simple.
-  52North has a GPL implementation that should be reviewed; an initial review shows that custom
   parser work is needed for additional xml schema; unclear how they handle the GML case thus far.

Planning
========

|image0| Milestone One - HTTP Get WPS Proof of Concept
------------------------------------------------------

Protocol \\ Content

Raster

Feature

Literal

Reference

HTTP GET using KVP

x

x

|image1|

 

HTTP POST

 

 

 

 

WSDL/SOAP

 

 

 

 

Deliver a prototype of WPS support in GeoServer / GeoTools library / and uDig application. The
prototype is focused on setting up the project and is limited to the HTTP Get protocol as a proof of
concept.

Milestone One Deliverables:

-  Application Spike: End-to-end with HTTP Get and simple KVP Literals
-  Test Conformance Goal: Relative to Annex A: 20%

At the end of this milestone developers can start making simple Process implementations using
"literals" and make use of them using GeoServer and uDig (and in their own programs). Deployment of
a new process should be as simple dropping a jar into GeoServer and restarting.

The majority of the work for this milestone would go into the design and community proposal stage,
since this step is based on a public review it would be very hard to commit to a strict deadline,
although the GeoServer change process does have some allowance for projects with a schedule.

Community and Communication
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Design and Community Proposal:

-  GeoServer Design resulting in a WPS `GeoServer Improvement
   Proposal <http://geoserver.org/display/GEOS/GeoServer+Improvement+Proposals>`__ for community
   review
-  Email to GeoTools asking to start work on unsupported/process
-  Email to uDig-devel as needed
-  Formal GeoServer Change Proposal

Release procedure, Testing and Documentation:

-  Measure conformance relative to Annex A
-  Deliver of a GeoServer milestone release
-  Deliver of a GeoTools milestone release
-  Deliver of a uDig milestone release
-  Instructions for deploying GeoServer with a community plug-in and use of WPS from udig

uDig Catalog WPS Plug-in
~~~~~~~~~~~~~~~~~~~~~~~~

Prototype client implementation (as part of core uDig)

-  Definition of a WPS as a Service that can be added to the catalog.
-  Addition of a WPS to the catalog, results in additional "operations" being made available to the
   uDig user

GeoTools Process Support
~~~~~~~~~~~~~~~~~~~~~~~~

GeoTools **wps** module allowing access to WPS service:

-  Creation of java bean data model, looks to be best done with EMF as per existing examples.
   Generation of data model as Java Beans (ProcessBrief etc...) hopefully from XSD. In the past this
   never works the first time due to errors in the OGC schema.
-  WPS client facade (minimal abstraction) providing direct access in the style of existing WMS
   client code.
-  Generic KVP transformer, generate a KVP String from a java.util.Map of parameters. Note existing
   WMS client knew exactly what the inputs/outputs were so a general transformer was not needed.

GeoTools **process** module allowing the definition of "Process\* as per existing proposal page

-  define first cut of interfaces
-  implement **sin** process making use of simple literals

GeoServer WPS Community Module
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Proof-of-Concept server implementation (as a GeoServer a community module)

-  Set up first cut of WebProcessingService:

   -  WPS object for configuration information (ie a simple java bean)
   -  WebProcessingService to dispatch actual requests

-  Key Value Pair parser
-  GetCapabilies using HTTP GET
-  DescribeProcess using HTTP GET
-  Execute using HTTP GET

Milestone Two - HTTP Post WPS Prototype
---------------------------------------

Protocol \\ Content

Raster

Feature

Literal

Reference

HTTP GET using KVP

x

x

|image2|

x

HTTP POST

x

x

x

x

WSDL/SOAP

 

 

 

 

Deliver a "core" WPS with very few optional capabilities added. As such this is the first release
suitable for the public. This milestone focuses on adding HTTP Post support.

Milestone Two Deliverables:

-  Application Prototype: End-to-end with HTTP Post and spatial content
-  Test Conformance Goal: Relative to Annex A: 50%

The majority of the work for this milestone would go into protocol handling; especially the use of
WFS via reference (which is seen as central to allowing the creation of a WPS capable of doing real
work).

Community and Communication
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Release procedure, Testing and Documentation

-  Measure conformance relative to Annex A
-  Deliver of a GeoServer milestone release
-  Deliver of a GeoTools milestone release
-  Deliver of a uDig milestone release

uDig Catalog WPS Plug-in
~~~~~~~~~~~~~~~~~~~~~~~~

Prototype client implementation (as part of core uDig)

-  Creation of a simple wizard allowing the definition (or at least confirmation of default) of WPS
   parameters
-  Developers Guide documentation showing how to wire up a Service that only provides functionality

GeoTools Process Support
~~~~~~~~~~~~~~~~~~~~~~~~

Creation of a **xml-wps** module:

-  Provide XML-XSD Bindings for WPS constructs

For the **wps** module:

-  Implement **Process** wrapper allowing an external WPS to be used as a normal GeoTools Process.
   This is the ground work for WPS chaining.
-  User Guide documentation for use of a WPS for both Raster and Feature content, should be limited
   to setting up a FeatureCollection and GridCoverage as parameters.

For the **process** module:

-  User Guide documentation covering the implementation of a Process

GeoServer WPS Community Module
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Hook up HTTP Post support for GetCapabilies, Describe Process and Execute (ie use of xml bindings
   for populating request objects)
-  Parsing WFS and WMS References as input
-  Developer Tutorial showing how to implement and deploy your own process
-  Uploading of a result to an FTP site; and generation of a Reference
-  Developer Tutorial(s)

--------------

+--------------------------------------------------------------------------------------------+
| |image4|                                                                                   |
| This is about all we have thought through so if you have ideas or priorities let us know   |
+--------------------------------------------------------------------------------------------+

--------------

Milestone Three - WSDL / SOAP Beta and Chaining Beta
----------------------------------------------------

Protocol \\ Content

Raster

Feature

Literal

Reference

HTTP GET using KVP

x

x

|image5|

x

HTTP POST

x

x

x

x

WSDL/SOAP

x

x

x

x

The majority of this milestone is devoted to the set up of a WSDL / SOAP architecture for GeoServer.

#. Design and Community Proposal

   -  Formal GeoServer Change Proposal

#. Server Implementation of:

   -  WSDL document describing "complete" WPS service of four operations
   -  WSDL docuemnt describing each operation
   -  SOAP Parsing of GetCapabilities / DescribeProcess / Execute operations
   -  SOAP Response generation; need to generate the SOAP Response envelope around existing result
      types
   -  Detection of "in service" chaining; optimize the case where GeoServer WPS output is fed to
      GeoServer WPS input

#. Client library

   -  User guide example showing how to connect to a WPS using JAXB (note this is a one off example
      generated against a **known** WSDL service defined as a Milestone Two example). Example will
      need to show how to call the GeoTools library to handle several of the spatial content types.

#. uDig

   -  Tutorial showing the integration of the above SOAP example a custom uDig operation, these two
      examples together show how to connect to any SOAP/WSDL end point from uDig.
   -  View allowing users to chain together several WPS instances as a single "Process Chain" or
      "Macro", the resulting process will be available as a new uDig operation and published into
      the catalog.

#. Test Conformance

   -  Relative to Annex A: 90%

The WSDL / SOAP stack would be used initially only by the WPS service within GeoServer; indeed this
represents the best case (a simple service that can be used and understood by all for debugging
purposes). Enabling WMS SOAP is work for another day.

Milestone 4 - Completed
-----------------------

#. GeoServer Release

   -  Conformance tests are not provided for the WPS service, we will need to meet GeoServers test
      coverage requirment
   -  User Guide documentation on the configuration and deployment of WPS
   -  Since GeoServer keeps its own release time table we may need to settle for moving the code in
      to core.
   -  Write up GeoServer WPS training materials

#. GeoTools Release

   -  GeoTools Supported Module, requires documentation and 40% test coverage
   -  User Guide page showing the chaining of WPS services
   -  Write up GeoTools Process training materials

#. uDig Release

   -  Updating the user guide, adding this story to Walkthrough 2
   -  Add WPS as an example to the Developers Guide
   -  Write up WPS Tutorial

#. Test Conformance

   -  Relative to Annex A: 100%

Use-Case
========

The following examples are taken by request; and also by reviewing the WPS 1.0 documentation. These
examples are mostly interesting to wrap your head around the idea of service chaining presented by
WPS.

Chaining
^^^^^^^^

uDig Client

 

WPS A

 

WPS B

 

 

 

WFS D

result

<--

buffer

<--

clip

<--

<-------

<---

getFeatures

 

 

 

 

 

 

WPS C

 

 

 

 

 

 

 

<--

boundsOf

<--

getFeatures

FTP Output
^^^^^^^^^^

uDig Client

 

WPS

 

FTP

execute Request

--->

 

 

 

 

<---

executeResponse 0%

 

 

 

 

process pending

 

 

 

 

process started

 

 

 

 

outputs

-->

put

 

 

process complete

 

 

 

 

 

 

 

show latest

--->

 

 

 

 

<---

executeResponse 100%

 

 

 

 

 

 

 

get

--------------

--------------------

-->

 

 

<---

--------------------

-->

outputs

This table format is not working; going to go break out viso.

+------------+----------------------------------------------------------+
| |image7|   | Document generated by Confluence on Aug 11, 2014 12:24   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/check.gif
.. |image1| image:: images/icons/emoticons/check.gif
.. |image2| image:: images/icons/emoticons/check.gif
.. |image3| image:: images/icons/emoticons/information.gif
.. |image4| image:: images/icons/emoticons/information.gif
.. |image5| image:: images/icons/emoticons/check.gif
.. |image6| image:: images/border/spacer.gif
.. |image7| image:: images/border/spacer.gif
