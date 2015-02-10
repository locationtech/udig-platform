Milestone 1
###########

uDig : Milestone 1

This page last changed on Nov 18, 2005 by jgarnett.

Milestone 1 has been completed on schedule.

Goals:

-  Set up as an open-source project with (`website <http://udig.refractions.net/>`__,
   `wiki <http://docs.codehaus.org/display/UDIG/Home>`__, `svn <http://svn.geotools.org/udig/>`__,
   `bug tacking <http://jira.codehaus.org/browse/UDIG>`__ and `build
   environment <http://eclipse.org/>`__
-  Introduce team members to the community and technologies they will be working with
-  Select an application framework
-  Focused reseach on rendering and printing technologies
-  Complete initial requirements and design

At the end of Milestone 1 we wish to be prepared for the first implementation phase.

`Milestone 1 Timeline <Milestone%201%20Timeline.html>`__
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

`Timeline <>`__

Date

Event

Complete

Jun 22, 2004

Design and Requirements Gathering

`Milestone 1 Deliverables <Milestone%201%20Deliverables.html>`__
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Status

Deliverables

Contact

|image0|

`Milestone 1 Report <http://udig.refractions.net/docs/Milestone-1.pdf>`__

All

|image1|

`Requirements Document <http://udig.refractions.net/docs/Requirements.pdf>`__

All

|image2|

`WFS DataStore Testing Plan <http://udig.refractions.net/docs/WFS-TestPlan.pdf>`__

David Zwiers

|image3|

`WFS DataStore Design Document <http://udig.refractions.net/docs/WFS-Design.pdf>`__

David Zwiers

|image4|

`Printing Technologies Report <http://udig.refractions.net/docs/Printing-Technologies-Report.pdf>`__

Richard Gould

|image5|

`Rendering Technologies
Report <http://udig.refractions.net/docs/Rendering-Technologies-Report.pdf>`__

Jesse

|image6|

`Web Registry Service Report <http://udig.refractions.net/docs/WRS.pdf>`__

Jody Garnett

|image7|

`uDIG Platform Comparison <http://udig.refractions.net/docs/Platform-Report.pdf>`__

Jody Garnett

 

 

 

|image8|

`Website <http://udig.refractions.net/>`__

Paul Ramsey

|image9|

`Bug Tracker <http://jira.codehaus.org/browse/UDIG>`__

James Macgill

|image10|

`Version Control <http://svn.geotools.org/udig/>`__

Paul Ramsey

|image11|

`Wiki <Home.html>`__

James Macgill

|image12|

Resource

|image13|

Completed Document

|image14|

Draft Document

Summary Of Work Acomplished
===========================

| The goal of this project is to build a GIS Desktop Client that integrates with Open
|  GIS Consortium (OGC) Open Web Services (OWS).

| The Application Framework we will be using is called Eclipse Rich Client
|  Platform. Eclipse Rich Client Platform is an offshoot of a popular Java Integrated
|  Development Environment and offers a unique approach to Java User Interface
|  development.

| All required documents have been completed for this initial milestone.
|  These Documents are:

-  *Phase 1.1*

   -  WFS DataStore Design and Testing Plan

-  *Phase 1.2*

   -  Printing Technologies Report

-  *Phase 1.6*

   -  Rendering Technologies Report

-  *Phase 1.8*

   -  GIS Web Registry Services Report

-  *Phase 1.10*

   -  Requirements Document
   -  Milestone Report

For milestone 1 we have completed:

-  *Phase 1.10*

   -  Project Web Site, svn repository, wiki and email list

| We were very pleased with the assistance provided by our partners and the open
|  source community in setting up the uDig project.

| This work is publicly available via the uDig project website, community wiki and
|  Subversion repository.

Encountered Problems and Solutions
==================================

GeoAPI and GO-1 Initiative
^^^^^^^^^^^^^^^^^^^^^^^^^^

| The GeoAPI library provides the Java GIS community with a common set of
|  interfaces based on OGC standards. Recently the GO-1 initiative has been
|  brought before the OGC as a common object representation of OGC concepts
|  across object oriented languages.

| Several of the changes made to GeoTools to track the GeoAPI and the GO-1
|  initiative have resulted in a reduced feature set during the porting process. The
|  GeoTools 2.1 branch has been scheduled to host the new capabilities required for
|  the uDig project. The creation of this branch has been delayed due to GeoAPI and
|  the GO-1 initiative.

| At a functional level the impact on the GeoTools library is limited to reprojection.
|  The uDig Project does not require reprojection services until Milestone 3.
|  The creation of the GeoServer 2.1 branch has been rescheduled to during the
|  Milestone 2 timeline. We have contacted several members of the GeoTools
|  community with expertise in reprojection to ensure a timely response to GeoAPI
|  changes. In addition, the company Polexis has offered the uDig project a
|  commercial implementation of the GO-1 rendering pipeline to use during
|  development.

Grid Coverage Exchange
^^^^^^^^^^^^^^^^^^^^^^

| The GeoTools support raster information has become outdated. The GO-1
|  initiative has provided the Java GIS community with the concept of a Grid
|  Coverage Exchange (GCE) for access to raster information.

| We have taken part in the specification of the GeoAPI interfaces for Grid
|  Coverage Exchange, and provided an implementation for GeoTools ArcGrid file
|  format.

Availability of Web Registry Service Documentation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

| The available documentation of the OGC Web Registry Service is inadequate.
|  We have received the support of several OGC members with access to current
|  documentation. We have received a commitment to answer our implementation
|  questions as they arise via email.

Notes
=====

We have found a couple of "gaps" in technologies that we wish to see filled:

+----------------------------------------------------+----------------------------------------------------+
| Grid Coverage Exchange                             | GML                                                |
| Raster access provided using out-dated DataSource  | GML XML parsing does not validate,                 |
| API                                                | `GeoServer <GeoServer.html>`__ has forked          |
+----------------------------------------------------+----------------------------------------------------+

The uDig project is focused on bringing together the existing open-source GIS community. To this end
we have start collaborative work with the `Geotools <Geotools.html>`__ and `GeoAPI <GeoAPI.html>`__
projects to address these issues.

+-------------+----------------------------------------------------------+
| |image16|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+-------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/check.gif
.. |image1| image:: images/icons/emoticons/check.gif
.. |image2| image:: images/icons/emoticons/check.gif
.. |image3| image:: images/icons/emoticons/check.gif
.. |image4| image:: images/icons/emoticons/check.gif
.. |image5| image:: images/icons/emoticons/check.gif
.. |image6| image:: images/icons/emoticons/check.gif
.. |image7| image:: images/icons/emoticons/check.gif
.. |image8| image:: images/icons/emoticons/information.gif
.. |image9| image:: images/icons/emoticons/information.gif
.. |image10| image:: images/icons/emoticons/information.gif
.. |image11| image:: images/icons/emoticons/information.gif
.. |image12| image:: images/icons/emoticons/information.gif
.. |image13| image:: images/icons/emoticons/check.gif
.. |image14| image:: images/icons/emoticons/warning.gif
.. |image15| image:: images/border/spacer.gif
.. |image16| image:: images/border/spacer.gif
