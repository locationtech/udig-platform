License Change
##############

uDig : License Change

This page last changed on Apr 02, 2013 by jgarnett.

+----------------+----------------+----------------+----------------+----------------+----------------+----------------+
| `Motivation <# |
| LicenseChange- |
| Motivation>`__ |
| `Inspiration < |
| #LicenseChange |
| -Inspiration>` |
| __             |
| `Proposal <#Li |
| censeChange-Pr |
| oposal>`__     |
|                |
| -  `Phase 1    |
|    Preparation |
|  <#LicenseChan |
| ge-Phase1Prepa |
| ration>`__     |
| -  `Phase 2    |
|    Distributio |
| n <#LicenseCha |
| nge-Phase2Dist |
| ribution>`__   |
|                |
| `Status <#Lice |
| nseChange-Stat |
| us>`__         |
| `Tasks <#Licen |
| seChange-Tasks |
| >`__           |
|                |
| -  `License    |
|    Change      |
|    Examples <L |
| icense%20Chang |
| e%20Examples.h |
| tml>`__        |
| -  `License    |
|    Change      |
|    Notes <Lice |
| nse%20Change%2 |
| 0Notes.html>`_ |
| _              |
                
+----------------+----------------+----------------+----------------+----------------+----------------+----------------+

Motivation
==========

As part of getting ready for `Eclipse Foundation
LocationTech <Eclipse%20Foundation%20LocationTech.html>`__ we need to select one of the approved
licenses: BSD, MIT, EPL or Apache. Our current LGPL license is not approved for distribution.

Inspiration
===========

Our motivation in selecting an LGPL license for the uDig code base is simple - it was the most
widely adopted "business friendly" license that our customers were comfortable. Selecting any other
license (the "new" Eclipse CPL was considered) involved customers getting out their legal council to
review the terms and conditions.

Time has moved on, and a wider range of "`business
friendly <http://www.codinghorror.com/blog/2007/04/pick-a-license-any-license.html>`__\ " licenses
is acceptable to our customers and community.

**Orion**

As an example the `Orion <http://www.eclipse.org/orion/>`__ project is distributed using the
following
`about.html <https://github.com/eclipse/orion.client/blob/master/bundles/org.eclipse.orion.client.core/about.html>`__.

**About This Content**

April 8, 2011

**License**

The Eclipse Foundation makes available all content in this plug-in ("Content"). Unless otherwise
indicated below, the Content is provided to you under the terms and conditions of the `Eclipse
Public License Version 1.0 <http://www.eclipse.org/legal/epl-v10.html>`__ ("EPL"), and the `Eclipse
Distribution License Version 1.0 <http://www.eclipse.org/org/documents/edl-v10.html>`__ ("EDL"). For
purposes of the EPL and EDL, "Program" will mean the Content.

If you did not receive this Content directly from the Eclipse Foundation, the Content is being
redistributed by another party ("Redistributor") and different terms and conditions may apply to
your use of any object code in the Content. Check the Redistributor's license that was provided with
the Content. If no such license exists, contact the Redistributor. Unless otherwise indicated below,
the terms and conditions of the EPL still apply to any source code in the Content and such source
code may be obtained at http://www.eclipse.org

And following
`header <https://github.com/eclipse/orion.client/blob/master/bundles/org.eclipse.orion.client.editor/web/orion/textview/annotations.js>`__

.. code:: code-java

    /*******************************************************************************
     * @license
     * Copyright (c) 2010, 2011 IBM Corporation and others.
     * All rights reserved. This program and the accompanying materials are made 
     * available under the terms of the Eclipse Public License v1.0 
     * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
     * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html). 
     * 
     * Contributors: 
     *      Felipe Heidrich (IBM Corporation) - initial API and implementation
     *      Silenio Quarti (IBM Corporation) - initial API and implementation
     ******************************************************************************/

Proposal
========

This proposal is split into two phases based on `A Guide to the Legal Documentation for
Eclipse-Based Content <http://www.eclipse.org/legal/guidetolegaldoc.php>`__,
http://www.eclipse.org/legal/epl/about.php and `The
about.html <http://wiki.eclipse.org/The_about.html>`__).

What goes into choosing a license for uDig?

-  Review the shortlist, and select based on the following criteria:

   -  "business friendly", all of the LocationTech licenses qualify
   -  RCP outreach, wish to engage wider Eclipse RCP community
   -  Smoothly contribute to GeoTools

Our final goal will look like this:

::

    udig/notice.html
    udig/epl-v10.html <-- assuming EPL for example
    udig/plugins/net.refractions.udig/about.properties <-- displayed in about screen
    udig/plugins/net.refractions.udig/splash.bmp
    udig/plugins/net.refractions.udig/nl/de/splash.bmp
    udig/plugins/net.refractions.udig/notice.html
    udig/plugins/net.refractions.udig/epl-v10.txt
    udig/plugins/net.refractions.udig.catalog/about.html
    udig/plugins/net.refractions.udig.catalog/epl-v10.html
    udig/plugins/net.refractions.udig.libs/about.html <-- see example below
    udig/plugins/net.refractions.udig.libs/about_files/lgpl-2.1.txt
    udig/plugins/net.refractions.udig.libs/about_files/asl-v20.txt.html
    udig/plugins/net.refractions.udig.libs/about_files/bsd.txt
    udig/plugins/net.refractions.udig.libs/about_files/happy_fun_ball.txt <-- not making that up
    udig/features/net.refractions.udig_application-feature/feature.xml
    udig/features/net.refractions.udig_application-feature/license.html
    udig/features/net.refractions.udig_application-feature/udig/epl-v10.html
    udig/features/net.refractions.udig_application-feature/about.properties

Phase 1 Preparation
-------------------

Phase one before forking to LocationTech:

#. Notices in source code

   -  Update headers (see example below)
   -  Only changing license at this time

Phase 2 Distribution
--------------------

Phase two before deployment:

#. Update product and deploy scripts

   -  Every build distribution has the standard SUA in the root as "notice.html"
   -  Every build distribution has a copy of the primary license(s) in the root

#. Plugin checklist

   -  Every plugin has an "about.html", usually the standard one
   -  Every plugin with a non-standard "about.html" contains the additional referenced license files
   -  Every JAR'ed plugin stores linked files in the "about\_files" (what does that mean?)

#. Feature checklist

   -  Every feature has the standard SUA in HTML in "license.html"
   -  Every feature has the Project's primary license(s)
   -  Every feature has the standard SUA in plain text in "feature.xml" or "feature.properties"
   -  Every feature plug-in has copyright notices, etc. in the "blurb" property of
      "about.properties"

Status
======

Project Steering committee support:

-  Jesse Eichar: +1 Motion
-  Frank Gasdorf: +1 Seconded
-  Jody Garnett: +1
-  Mauricio Pazos: +1
-  Andrea Antonello: +1

Committer Support:

-  David Zwiers: "preference has been BSD ... EPL has a few more conditions and isn't as simple to
   follow
-  Richard Gould: "MIT as default choice"
-  Kenneth Gulbrandsoy: "don't care as long as the new license is not more restrictive"
-  Survey: 9 EPL / 3 BSD / 4 MIT

Community support:

-  Chris Holmes (with a reminder to reference BSD)
-  Offline: Several individual that are happy with any OSI approved license (and are enthusiastic to
   see uDig joining LocationTech)
-  Offline: Several development teams reached the PSC favouring EPL
-  Offline: Company standard of EPL
-  Offline: EPL preferred (as results in no change of technology evaluation)
-  Offline: Anything other than EPL preferred due to conflicts moving code to GPL

A vote of -1 requires an alternate suggestion; community members are invited to indicate
support/suggestions.

Tasks
=====

A list of the tasks needed to accomplish this change; if you prefer you can use a single Jira issue
with subtasks. It is important to include any deadlines so the community knows when you are working
to a schedule.

 

no progress

|image0|

in progress

|image1|

blocked

|image2|

help needed

|image3|

done

Tasks:

#. RFC and community discussion

   -  |image4| JG:\ http://udig-news.blogspot.com.au/2012/10/udig-license-change.html
   -  |image5| Dual EPL and BSD recommended on email list
   -  |image6| `survey <http://micropoll.com/t/KE6vRZQIRw>`__
   -  |image7| PSC: Select license (Dual EPL and BSD license selected)
   -  |image8| http://udig-news.blogspot.com.au/2012/10/udig-change-to-epl-and-bsd-license.html

#. For files with missing headers

   -  |image9| JG: Search for: (?-m)(^\\s\*)package
   -  |image10| JG: Search for: (?-m)(^\\s+\|\\s+$)package
   -  |image11| JG: Use "git log" to establish initial contribution date, example:
      ``git log META_INF/MANIFEST.MF``

#. Update website

   -  |image12| FG: http://udig.refractions.net

      -  |image13| http://udig.refractions.net/main.html
      -  |image14| http://udig.refractions.net/gallery/about.html
      -  |image15| http://udig.refractions.net/gallery/arbonaut/content.html

   -  FG: http://udig.github.com
   -  |image16| FG : Update Twitter Account Description (https://twitter.com/udigGIS)

#. Update docs

   -  |image17| FG: User Guide
   -  |image18| FG: Developers Guide
   -  |image19| Admin Manual - may wait for conversion from wiki to sphinx

#. Update distribution and product details

   -  |image20| Publish license on server for reference:

      -  http://udig.refractions.net/files/epl-v10.html <-- not strictly necessary but nice to have
         on using our fonts
      -  http://udig.refractions.net/files/bsd3-v10.html Refractions BSD 3 Clause License Version
         1.0
      -  http://udig.refractions.net/files/hsd3-v10.html HydoloGIS BSD 3 Clause License Version 1.0
      -  http://udig.refractions.net/files/asd3-v10.html Axios BSD 3 Clause License Version 1.0

   -  |image21| Update splash screens (english, italian and german done and the rest removed)
   -  |image22| Update about.properties (for the about dialog)
   -  |image23| Root notice.html for product distribution (check deploy scripts). Placeholder
      provided but it is not a legal software usage agreement.
   -  |image24| Copy of primary license(s)in root distribution (check deploy scripts)

#. Update features

   -  |image25| Copied notice.html to license.html, and license files
   -  |image26| feature.xml **license** section has our usual header information (not full
      notice.html text)
   -  |image27| branding plugins have blurb filled in

#. Update headers: Search and replace

   -  |image28| Initial Search and LGPL to shortlist (3120 files!)
   -  |image29| Search and Replace on standard header
   -  |image30| Update remaining headers, search replace and manual inspection
   -  |image31| Update EMF public static copyright Strings

#. Update plugins: about.html and related files

   -  |image32| Initial about.html
   -  |image33| epl-v10.html and bsd-v10.html <-- may not be needed?
   -  |image34| Hunt down hsd-v10.html (HydoloGIS) plugins
   -  |image35| Hunt down asd-v10.html (Axios) plugins
   -  |image36| Fill in more extensive about.html for net.refractions.udig.libs
   -  We can do the remaining third party plugins during dependency audit

Timeframe:

-  Activity undertaken in conjunction with incubation proposal
-  LocationTech meeting on Oct 10th

Status:

-  https://jira.codehaus.org/browse/UDIG-1952

+-------------+----------------------------------------------------------+
| |image38|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+-------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/star_yellow.gif
.. |image1| image:: images/icons/emoticons/error.gif
.. |image2| image:: images/icons/emoticons/warning.gif
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
.. |image19| image:: images/icons/emoticons/warning.gif
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
.. |image34| image:: images/icons/emoticons/check.gif
.. |image35| image:: images/icons/emoticons/check.gif
.. |image36| image:: images/icons/emoticons/check.gif
.. |image37| image:: images/border/spacer.gif
.. |image38| image:: images/border/spacer.gif
