5 Reading List
==============

This list is not ordered, how important I feel these documents are really changed as I climbed up
the learning curve. Suffice to say that each link here saved me weeks rather then days.

:doc:`Getting Started`


* :doc:`The Big Picture / Platform and Plug-ins`

* :doc:`Rich Client Platform`


:doc:`Reference`


* :doc:`Development`

* :doc:`SWT & JFace`

* :doc:`Editors and Views`

* :doc:`Help system`


:doc:`Arcane & Advanced`


* :doc:`Eclipse Modeling Framework (EMF)`


Getting Started
===============

When starting out it is easy to get stuck in the details of how to accomplish things. These links
not only help with that, but also provide some lasting understanding.

The Big Picture / Platform and Plug-ins
---------------------------------------

**Notes on the Eclipse Plug-in Architecture**

`Article <http://www.eclipse.org/articles/Article-Plug-in-architecture/plugin_architecture.html>`_ :
A watershed document with a clear explanation about how an Eclipse application is assembled out of
extension points and extensions.
 |image0| learning what an extension point is good for
 |image1| understanding lolly-pop diagrams
 |image2| Comparision with Publish/Subscribe

**Contributing to Eclipse Principles, Patterns and Plug-ins**

`Book <http://www.awprofessional.com/title/0321205758>`_ : This book answers the crucial **why**
questions I had about Eclipse development.
 |image3| Buy for the UDIG:Eclipse House Rules
 |image4| Buy if **IAdaptable** is bothering you
 |image5| Some examples are showing their age
 The last couple chapters that use patterns to show you how things work, and what trade-offs were
considered, is worth the price of admission alone.

**Eclipse Plug-ins**

Formally called: Building Commercial Quality Plug-ins
 `Book <http://www.qualityeclipse.com/>`_ : Despite its title, and perhaps intent, this book covers
a great middle ground between the the **Contributing to** and the **FAQ** books. Covers a range of
topics, communicating the *tricks* that we would not have found otherwise.
 |image6| SWT Layout tutorial with the same form done with each layout manager
 |image7| Was how I finally learned how to use Viewers, LabelDirectors and ItemProviders
 |image8| Only book intro to building offline with PDE Build / Ant
 |image9| Content occasionally needs adapting for RCP use
 The main benefit to us was that it showied the use of these technologies for real. I would really
recommend this book as an alternative to the FAQ book if you can make it through the first month
without being annoyed at your ability to update a status bar.

Rich Client Platform
--------------------

**Developing Eclipse Rich Client Applications**

`Tutorial <http://www.eclipsecon.org/2005/presentations/EclipseCon2005_Tutorial8.pdf>`_ : Makes use
of the Eclipse wizards to make a quick intro. A good tutorial to check out while we wait for the
other links to get updates.
 |image10| Use of Wizards to limit writing of code
 |image11| Intro to PDE Units
 |image12| Predates the switch to Manifests (so you need to do a few extra steps to get your unit
test to work)

**Eclipse Rich Client Platform : Design, Coding, and Packaging Java (TM) Applications**

:doc:`review`

 I have a copy of it, I read it from cover to cover and I agree with this reviewâ€¦
 Alain Demers, Faune QuÃ©bec

-  Website:
   :doc:`FAQ`

-  Article: `Rich Client Platform Tutorial - Part
   1 <http://www.eclipse.org/articles/Article-RCP-1/tutorial1.html>`_ |image13| Updated
-  Article: `Rich Client Platform Tutorial - Part
   2 <http://www.eclipse.org/articles/Article-RCP-2/tutorial2.html>`_
-  Article: `Rich Client Platform Tutorial - Part
   3 <http://www.eclipse.org/articles/Article-RCP-3/tutorial3.html>`_
-  Article: `Exporting an RCP
   Application <http://dev.eclipse.org/viewcvs/index.cgi/~checkout~/pde-build-home/articles/export%20rcp%20apps/article.html>`_
-  Reference: `Eclipse Rich Client
   Platform <http://dev.eclipse.org/viewcvs/index.cgi/%7Echeckout%7E/platform-ui-home/rcp/index.html>`_

Reference
=========

For day to day day UDIG development you will find the following useful.

Development
-----------

**Eclipse 3.1 Documentation**

`Website <http://help.eclipse.org/help31>`_ : I often find myself following outdated tutorials and
using the online help system to see how things are done today.
 |image14| From within Eclipse: **Help > Help Contents**
 |image15| This is the only information that is actually up to date.
 |image16| Use the website to provide links for others
 |image17| Often can only be understood after you have figured it out

**Offical Eclipse 3.0 FAQs**

`Book & Website <http://www.eclipsefaq.org/chris/faq/>`_ : Quite helpful with the initial
frustration of working with the Eclipse Framework. Helps when you just want it to work and don't
care why.
 |image18| Buy: if you use the above link more then twice an hour
 |image19| Can be installed as a plug-in
 |image20| The detailed code tips don't age well
 I am going to buy the next version, not that we know when/if it is to be expected.

SWT & JFace
-----------

**SWT: The Standard Widget Toolkit, Volume 1**

`Book <http://www.awprofessional.com/title/0321256638>`_ : A good reference (but so is the source
code), I found the **Building Commercial Quaility Plug-ins** examples of using the SWT/JFace in
anger more compelling.
 |image21| Least used of all the books we bought

Editors and Views
-----------------

* :doc:`Creating an Eclipse View`

-  `Using
   Perspectives <http://www.eclipse.org/articles/using-perspectives/PerspectiveArticle.html>`_
-  Use **Import > External Plug-ins and Fragments** to import the source code for a an eclipse view
   you are familar with and see how it works.

Help system
-----------

-  `Working with the Help
   system <http://www.eclipse.org/articles/Article-Online%20Help%20for%202_0/help1.htm>`_
-  `Eclipse Online Help Tutorial
   101 <http://devresource.hp.com/drc/technical_white_papers/ecliphelp/index.jsp#contexts_xml>`_

Arcane & Advanced
=================

If you wander into the guts of uDig development the following will be of service.

Eclipse Modeling Framework (EMF)
--------------------------------

**Eclipse Modeling Framework**

`Book <http://www.awprofessional.com/titles/0131425420>`_ : Eclipse Modeling Framework seems to be
the first successful Model Driven Design framework. It uses a Ecore for modeling, a reduced subset
of the UML2 Meta Object Facility (MOF).
 |image22| Buy if you need to patch the core of the uDig application - Project, Map, Page etc..
 |image23| Look into it for your own needs, it seems to be stupidly useful
 |image24| IAdaptable is used instead of traditional listeners/events
 It literally appears the be the shape of the future as the OMG is targeting the same subset w/
their EMOF work.

-  `The Eclipse Modeling Framework (EMF)
   Overview <http://download.eclipse.org/tools/emf/scripts/docs.php?doc=references/overview/EMF.html>`_
* :doc:`Guide To EMF`


.. |image0| image:: images/icons/emoticons/add.gif
.. |image1| image:: images/icons/emoticons/add.gif
.. |image2| image:: images/icons/emoticons/add.gif
.. |image3| image:: images/icons/emoticons/add.gif
.. |image4| image:: images/icons/emoticons/add.gif
.. |image5| image:: images/icons/emoticons/forbidden.gif
.. |image6| image:: images/icons/emoticons/add.gif
.. |image7| image:: images/icons/emoticons/add.gif
.. |image8| image:: images/icons/emoticons/warning.gif
.. |image9| image:: images/icons/emoticons/forbidden.gif
.. |image10| image:: images/icons/emoticons/add.gif
.. |image11| image:: images/icons/emoticons/add.gif
.. |image12| image:: images/icons/emoticons/forbidden.gif
.. |image13| image:: images/icons/emoticons/star_yellow.gif
.. |image14| image:: images/icons/emoticons/add.gif
.. |image15| image:: images/icons/emoticons/add.gif
.. |image16| image:: images/icons/emoticons/add.gif
.. |image17| image:: images/icons/emoticons/forbidden.gif
.. |image18| image:: images/icons/emoticons/add.gif
.. |image19| image:: images/icons/emoticons/warning.gif
.. |image20| image:: images/icons/emoticons/forbidden.gif
.. |image21| image:: images/icons/emoticons/forbidden.gif
.. |image22| image:: images/icons/emoticons/add.gif
.. |image23| image:: images/icons/emoticons/add.gif
.. |image24| image:: images/icons/emoticons/forbidden.gif
