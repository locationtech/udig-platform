Reading List
------------

This list is not ordered, how important I feel these documents are really changed as I climbed up
the learning curve. Suffice to say that each link here saved me weeks rather then days.

Getting Started
~~~~~~~~~~~~~~~

When starting out it is easy to get stuck in the details of how to accomplish things. These links
not only help with that, but also provide some lasting understanding.

The Big Picture / Platform and Plug-ins
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

**Notes on the Eclipse Plug-in Architecture** (Article)

`http://www.eclipse.org/articles/Article-Plug-in-architecture/plugin\_architecture.html <http://www.eclipse.org/articles/Article-Plug-in-architecture/plugin_architecture.html>`_

A watershed document with a clear explanation about how an Eclipse application is assembled out of
extension points and extensions.

-  learning what an extension point is good for
-  understanding lolly-pop diagrams
-  Comparision with Publish/Subscribe

**Contributing to Eclipse Principles, Patterns and Plug-ins** (Book)
 `http://www.awprofessional.com/title/0321205758 <http://www.awprofessional.com/title/0321205758>`_

This book answers the crucial **why** questions I had about Eclipse development.

-  Buy for the UDIG:Eclipse House Rules
-  Buy if **IAdaptable** is bothering you
-  Some examples are showing their age

The last couple chapters that use patterns to show you how things work, and what trade-offs were
considered, is worth the price of admission alone.

**Eclipse Plug-ins** (Book)
 Previous edition: Building Commercial Quality Plug-ins
 `http://www.qualityeclipse.com/ <http://www.qualityeclipse.com/>`_

Despite its title, and perhaps intent, this book covers a great middle ground between the the
**Contributing to** and the **FAQ** books. Covers a range of topics, communicating the *tricks* that
we would not have found otherwise.

-  SWT Layout tutorial with the same form done with each layout manager
-  Was how I finally learned how to use Viewers, LabelDirectors and ItemProviders
-  Only book intro to building offline with PDE Build / Ant
-  Content occasionally needs adapting for RCP use

The main benefit is showing the use of these technologies in a more realistic context. Recommend
this book as an alternative to the FAQ book if you can make it through the first month without being
annoyed at your ability to update a status bar.

Rich Client Platform
^^^^^^^^^^^^^^^^^^^^

**Developing Eclipse Rich Client Applications** (Tutorial)

`http://www.eclipsecon.org/2005/presentations/EclipseCon2005\_Tutorial8.pdf <http://www.eclipsecon.org/2005/presentations/EclipseCon2005_Tutorial8.pdf>`_

Makes use of the Eclipse wizards to make a quick intro. A good tutorial to check out while we wait
for the other links to get updates.

-  Use of Wizards to limit writing of code
-  Intro to PDE Units
-  Predates the switch to Manifests (so you need to do a few extra steps to get your unit test to
   work)

**Eclipse Rich Client Platform : Design, Coding, and Packaging Java Applications** (Review)

`http://www.eclipsezone.com/articles/rcp-review/ <http://www.eclipsezone.com/articles/rcp-review/>`_
 I have a copy of it, I read it from cover to cover and I agree with this review Alain Demers, Faune
Quebec

Tips
^^^^

-  Website:
   `http://dev.eclipse.org/viewcvs/index.cgi/%7Echeckout%7E/platform-ui-home/rcp/faq.html <http://dev.eclipse.org/viewcvs/index.cgi/%7Echeckout%7E/platform-ui-home/rcp/faq.html>`_
-  Article:
   `http://www.eclipse.org/articles/Article-RCP-1/tutorial1.html <http://www.eclipse.org/articles/Article-RCP-1/tutorial1.html>`_
   |image0| Updated
-  Article:
   `http://www.eclipse.org/articles/Article-RCP-2/tutorial2.html <http://www.eclipse.org/articles/Article-RCP-2/tutorial2.html>`_
-  Article:
   `http://www.eclipse.org/articles/Article-RCP-3/tutorial3.html <http://www.eclipse.org/articles/Article-RCP-3/tutorial3.html>`_
-  Article:
   `http://dev.eclipse.org/viewcvs/index.cgi/~checkout~/pde-build-home/articles/export%20rcp%20apps/article.html <http://dev.eclipse.org/viewcvs/index.cgi/~checkout~/pde-build-home/articles/export%20rcp%20apps/article.html>`_
-  Reference:
   `http://wiki.eclipse.org/index.php/Rich\_Client\_Platform <http://wiki.eclipse.org/index.php/Rich_Client_Platform>`_

Reference
~~~~~~~~~

For day to day day UDIG development you will find the following useful.

Development
^^^^^^^^^^^

**Eclipse Documentation**
 `http://help.eclipse.org/ <http://help.eclipse.org/>`_
 Find yourself using outdated tutorials from the web? Check the latest online help to see how things
are done today.

-  From within Eclipse: **Help > Help Contents**
-  This is the only information that is actually up to date.
-  Use the website to provide links for others
-  Often can only be understood as a reference (after you have figured it out once)

SWT & JFace
^^^^^^^^^^^

**SWT: The Standard Widget Toolkit, Volume 1** (Book)
 `http://www.awprofessional.com/title/0321256638 <http://www.awprofessional.com/title/0321256638>`_

A good reference (but so is the source code), I found the **Building Commercial Quaility Plug-ins**
examples of using the SWT/JFace in anger more compelling.

-  Least used of all the books we bought

Editors and Views
^^^^^^^^^^^^^^^^^

* `Creating an Eclipse View <http://www.eclipse.org/articles/viewArticle/ViewArticle2.html>`_
-  `Using
   Perspectives <http://www.eclipse.org/articles/using-perspectives/PerspectiveArticle.html>`_
-  Use **Import > External Plug-ins and Fragments** to import the source code for a an eclipse view
   you are familar with and see how it works.

Help system
^^^^^^^^^^^

* `http://www.eclipse.org/articles/Article-Online%20Help%20for%202\_0/help1.htm <http://www.eclipse.org/articles/Article-Online%20Help%20for%202_0/help1.htm>`_

Arcane & Advanced
~~~~~~~~~~~~~~~~~

If you wander into the guts of uDig development the following will be of service.

Eclipse Modeling Framework (EMF)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

**Eclipse Modeling Framework** (Book)

`http://www.awprofessional.com/titles/0131425420 <http://www.awprofessional.com/titles/0131425420>`_

Eclipse Modeling Framework seems to be the first successful Model Driven Design framework. It uses a
Ecore for modeling, a reduced subset of the UML2 Meta Object Facility (MOF).

-  Buy if you need to patch the core of the uDig application - Project, Map, Page etc..
-  Look into it for your own needs, it seems to be stupidly useful
-  IAdaptable is used instead of traditional listeners/events

It literally appears the be the shape of the future as the OMG is targeting the same subset w/ their
EMOF work.

* `http://download.eclipse.org/tools/emf/scripts/docs.php?doc=references/overview/EMF.html <http://download.eclipse.org/tools/emf/scripts/docs.php?doc=references/overview/EMF.html>`_
* :doc:`guide_to_emf`


.. |image0| image:: images/icons/emoticons/star_yellow.gif
