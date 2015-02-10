Eclipse Foundation Locationtech
###############################

uDig : Eclipse Foundation LocationTech

This page last changed on Apr 09, 2013 by fgdrf.

Motivation
==========

In the past we were unable to respond to the requests that the project "join a foundation" due to
lack of resources, technical limitations, and more important priorities (such as making a 1.2.x).

The request has come from potential contributors being apprehensive contributing to an open source
project owned by a single company. We would appreciate the assistance the a foundation offers with
respect to legal advice, advocacy, promotion and support.

This topic of joining a foundation has been considered previously; resulting in the following
actions:

-  The uDig project has e balanced project steering committee with members from a number of
   organisations
-  uDig reviewed joining the "Eclipse Labs" project (and rejected the idea as they did not support
   git)
-  uDig reviewed the OSGeo Foundation and currently participates in the OSGeo Live DVD.
-  uDig maintains a Refractions Copyright to give the PSC freedom to change the license if needed

Why the Eclipse foundation:

-  the Eclipse Foundation has resolved its technical issues with git mentioned above
-  uDig has regular releases, defined project process, and is on a stable technical footing
-  the Eclipse Foundation is starting to engage with the mapping community
-  the majority of the paid work brought into the uDig project has come in via the Eclipse
   Development community.

Proposal
========

Additional wiki pages covering uDig specific questions, progress and research:

-  `Eclipse Incubation Questions and
   Answers <Eclipse%20Incubation%20Questions%20and%20Answers.html>`__

The Eclipse incubation process is well defined:

-  http://wiki.eclipse.org/Development_Resources/HOWTO/Incubation_Phase
-  http://wiki.eclipse.org/Development_Resources/HOWTO/Starting_A_New_Project (supplied by Andrew
   Ross includes example proposals)

Other reference:

-  http://www.eclipse.org/projects/dev_process/development_process_2011.php
-  http://wiki.eclipse.org/Development_Resources/Process_Guidelines/What_is_Incubation
-  http://eclipsehowl.wordpress.com/2010/01/18/oh-these-names-incubation-incubator-labs/

In general there is alignment with the expectations of the eclipse development process and the
running of the uDig project. We do not for see any major adjustment to procedure being required
during the incubation process.

Some specifics about the Eclipse incubation process:

-  Unfortunately our use of LGPL code (JTS and GeoTools) prevents us directly joining the
   foundation, however we are taking part in the formation of the `LocationTech Industry Working
   Group <http://wiki.eclipse.org/LocationTech>`__.
-  The Eclipse Foundation has members on staff to perform much of the "grunt work" tasks such as
   checking dependencies

Status
======

Project Steering committee support:

-  Andrea Antonello: +1
-  Jesse Eichar: +0
-  Jody Garnett: +1
-  Mauricio Pazos: +1
-  Frank Gasdorf +1

Committer Support:

-  

A vote of -1 requires an alternate suggestion; community members are invited to indicate
support/suggestions.

Documentation
=============

The developers guide and administration guide are expected to be updated in response to this RFC.
Specifically we can retire much of our project documentation and make use of the definitions
(release, contributor, committer, etc..) provided by the foundation.

Other:

-  Projects in incubation must have the incubation graphic on their home page
-  projects must have a standard project page (i.e. "About this project" )

Tasks
=====

 

no progress

|image0|

in progress

|image1|

blocked

|image2|

help needed

|image3|

done

#. Discussion

   -  |image4| Public discussion on udig-devel
   -  |image5| PMC and contributor approval
   -  |image6| Contact Refracitons
   -  |image7| Quick pre-flight check on LGPL license, JAI discussed, remaining license questions
      saved for incubation

#. |image8| Assemble incubation materials with LocationTech email list

   -  Example 1: http://eclipse.org/proposals/technology.aether/
   -  Example 2: http://eclipse.org/proposals/mpc/

#. |image9| Check against `Project Naming
   Policy <http://wiki.eclipse.org/Development_Resources/HOWTO/Project_Naming_Policy>`__

   -  Descriptive name: User-friendly Desktop Internet GIS
   -  Short name: uDig
   -  First use: "The proposed LocationTech project User-friendly Desktop Internet GIS (uDig) is
      fun!"
   -  Second use: "The uDig project is still fun!"
   -  forum: locationtech.udig
   -  email: udig-dev
   -  package: org.locationtech.udig

#. Proposal for incubation as part of LocationTech:
    Live from github:
   `udig-docs/admin/proposal/udig\_proposal.html <https://github.com/uDig/udig-docs/blob/master/admin/proposal/proposal.html>`__

   -  |image10| Contact details
   -  |image11| Background
   -  |image12| Scope - feedback focused on establishing the scope of "mapping"
   -  |image13| Description
   -  |image14| Contribution
   -  |image15| Committers - this section is unclear; think we need to list names from github
   -  |image16| Mentors - `Bug 391310 Mentors needed for uDig
      project <https://bugs.eclipse.org/bugs/show_bug.cgi?id=391310>`__
   -  |image17| Interested Parties - should hunt down a larger list of downstream projects
   -  |image18| Project Scheduling

#. Change License so we are not asking LocationTech to directly distribute LGPL. Use one of BSD,
   MIT, EPL, or Apache.

   -  |image19| Shortlist: BSD, MIT, Apache. Consider based on outreach to Eclipse RCP developers
      and collaboration with GeoTools
   -  |image20| Write up an RFC to change license: `License Change <License%20Change.html>`__

#. Communication with LocationTech

   -  |image21| Review "top level project": http://wiki.eclipse.org/LocationTech/TechnologyTLP
   -  |image22| Review and revise uDig Proposal proposal
   -  |image23| `Bug 391310 Mentors needed for uDig
      project <https://bugs.eclipse.org/bugs/show_bug.cgi?id=391310>`__ mentors volunteered
   -  |image24| Submit above proposal as a proposal.zip to emo@eclipse.org

#. Project Creation (from
   `Starting\_A\_New\_Project <http://wiki.eclipse.org/Development_Resources/HOWTO/Starting_A_New_Project>`__)

   #. |image25| Contact emo@eclipse.org to state intension to propose a new project
   #. |image26| Assemble proposal from
      `template <http://www.eclipse.org/proposals/templates/proposal-template.zip>`__
   #. |image27| EMO will review document and provide feedback
   #. |image28| EMO will post a draft copy of request and open a bug report to track the proposal
   #. |image29| Proposal will be posted on `What's
      New <http://www.eclipse.org/projects/whatsnew.php>`__ page
   #. |image30| Request mentors from Architecture Council (`Bug 391310 Mentors needed for uDig
      project <https://bugs.eclipse.org/bugs/show_bug.cgi?id=391310>`__)
   #. |image31| Proposal is up for a minimum two weeks community review / comment
   #. |image32| Once trademark search has been resolved, schedule creation review
   #. |image33| Invited to provide provisioning information (used for space on servers, committer
      list, etc...)

#. After Creation (from
   `Starting\_A\_New\_Project <http://wiki.eclipse.org/Development_Resources/HOWTO/Starting_A_New_Project>`__)

   -  Formal Announcement:
      http://udig-news.blogspot.com.au/2013/03/udig-officially-joins-locationtech.html
   -  Tasks continue on http://locationtech.org/wiki/index.php/UDig_Infrastructure_Migration#Code

Status:

-  no Jira created yet

+-------------+----------------------------------------------------------+
| |image35|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+-------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/star_yellow.gif
.. |image1| image:: images/icons/emoticons/error.gif
.. |image2| image:: images/icons/emoticons/warning.gif
.. |image3| image:: images/icons/emoticons/check.gif
.. |image4| image:: images/icons/emoticons/check.gif
.. |image5| image:: images/icons/emoticons/check.gif
.. |image6| image:: images/icons/emoticons/check.gif
.. |image7| image:: images/icons/emoticons/check.gif
.. |image8| image:: images/icons/emoticons/star_yellow.gif
.. |image9| image:: images/icons/emoticons/star_yellow.gif
.. |image10| image:: images/icons/emoticons/check.gif
.. |image11| image:: images/icons/emoticons/check.gif
.. |image12| image:: images/icons/emoticons/check.gif
.. |image13| image:: images/icons/emoticons/check.gif
.. |image14| image:: images/icons/emoticons/check.gif
.. |image15| image:: images/icons/emoticons/check.gif
.. |image16| image:: images/icons/emoticons/error.gif
.. |image17| image:: images/icons/emoticons/check.gif
.. |image18| image:: images/icons/emoticons/check.gif
.. |image19| image:: images/icons/emoticons/check.gif
.. |image20| image:: images/icons/emoticons/check.gif
.. |image21| image:: images/icons/emoticons/check.gif
.. |image22| image:: images/icons/emoticons/check.gif
.. |image23| image:: images/icons/emoticons/check.gif
.. |image24| image:: images/icons/emoticons/check.gif
.. |image25| image:: images/icons/emoticons/check.gif
.. |image26| image:: images/icons/emoticons/check.gif
.. |image27| image:: images/icons/emoticons/check.gif
.. |image28| image:: images/icons/emoticons/check.gif
.. |image29| image:: images/icons/emoticons/check.gif
.. |image30| image:: images/icons/emoticons/check.gif
.. |image31| image:: images/icons/emoticons/check.gif
.. |image32| image:: images/icons/emoticons/check.gif
.. |image33| image:: images/icons/emoticons/check.gif
.. |image34| image:: images/border/spacer.gif
.. |image35| image:: images/border/spacer.gif
