Welcome New Eclipse RCP Developers
----------------------------------

If uDig is your first Eclipse RCP project you are in for a bit of a learning wall.

Working with the uDig project will bring together three aspects:

-  The Java Programming Language
-  Eclipse Rich Client Platform Development
-  The Geospatial Domain

Please understand that it will take some time to become familiar with each of these areas; be kind
to yourself and budget time accordingly. If your team is weak in one or more areas you should
strongly considering purchasing books and budging time for learning (or a training course).

Training Course
~~~~~~~~~~~~~~~

The best advice is (of course) contact a Project Steering Committee member for our week long
training course:

* `http://udig.refractions.net/users/ <http://udig.refractions.net/users/>`_

If you are working for an academic institution please have your professor contact us; we can make
our training materials available to you.

uDig Tutorials
~~~~~~~~~~~~~~

Other than that please jump on in; the udig-devel email list is a good place to ask questions.
Please go through the "getting started" tutorials in the developers guide and ask lots of questions
on the email list and IRC.

Important: As you go through the tutorials please understand it is not enough **just** to get them
to run - you will need to do all the "Things to Try" questions at the end in order to understand how
to turn on tracing, or distribute your custom application to your customers.

uDig Documentation
~~~~~~~~~~~~~~~~~~

The Developers Guide is structured in the same manner as the Eclipse Documentation:

* :doc:`developers_guide`


   * :doc:`udig_guidelines`

   * :doc:`eclipse_house_rules` (from Contributing to Eclipse
      Principles, Patterns and Plug-ins)
   * :doc:`user_interface_guidelines`


The Developers Guide is not complete; or always up to date. The document is contains our notes as we
built the application and were learning the Eclipse RCP environment.

The developers guide does not cover as much scope as much information as our commercial training
materials. In many places you will need to learn by using the debugger to step through functionality
you are interested in.

The extension point documentation and javadoc information can also be of assistance.

Eclipse Rich Client Platform
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

RCP Documentation in the Help Menu
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

A really good source of information (that is always current) is included right in Eclipse:

-  Start up eclipse and select **Help > Help Contents** from the menu bar. Choose **Platform Plug-in
   Developers Guide**
-  Also available `online <http://www.eclipse.org/documentation/>`_:

   -  Eclipse 3.4 Ganymede:
      `http://help.eclipse.org/ganymede/index.jsp <http://help.eclipse.org/ganymede/index.jsp>`_
   -  Eclipse 3.3 Europa:
      `http://help.eclipse.org/help33/index.jsp <http://help.eclipse.org/help33/index.jsp>`_

-  Remember wizards and cheat sheets included in your copy of Eclipse are always up to date!

RCP Tutorials
^^^^^^^^^^^^^

Tutorials:

* `Rich Client Tutorial Part 1 <http://www.eclipse.org/articles/Article-RCP-1/tutorial1.html>`_
* `Rich Client Tutorial Part 2 <http://www.eclipse.org/articles/Article-RCP-2/tutorial2.html>`_
* `Rich Client Tutorial Part 3 <http://www.eclipse.org/articles/Article-RCP-3/tutorial3.html>`_
* `Creating an Eclipse View <http://www.eclipse.org/articles/viewArticle/ViewArticle2.html>`_

Reference Material and Reading List
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

After going through the Eclipse RCP Tutorials you may want some background information on all the
moving parts.

Some useful links that really helped us out:

-  `Notes on the Eclipse Plug-in
   Architecture <http://www.eclipse.org/articles/Article-Plug-in-architecture/plugin_architecture.html>`_
   contains the original lolly-pop diagrams used to show eclipse extension points

Books:

-  Eclipse Rich Client Platform : Design, Coding, and Packaging Java (TM) Applications
-  Contributing to Eclipse Principles, Patterns and Plug-ins
-  Building Commercial Quaility Plug-ins

For more information about these books please check out the :doc:`Reading List <reading_list>`
from the developers guide.

How it fits together
~~~~~~~~~~~~~~~~~~~~

The following notes come from email discussion on the udig-devel list; we will add to this if you
have any additional questions. The concepts here were introduced in the above tutorials.

How plugins work
^^^^^^^^^^^^^^^^

-  Eclipse stuff is grouped into plugs for reuse; the plugin definition
    has a lot of safety/sanity checks included; with the idea that they
    don't want to run or include a plugin that won't work. So they have
    each plugin list what it needs to run; and then the plugin system
    checks all this stuff out; and only if it is good does the plugin get
    loaded - most of this information is in the MANIFEST.MF file (usually
    used to describe a jar in normal java apps).

The name of this plugin system is "OSGi" - Eclipse 2 used to have its
 own but they threw it out and adopted OSGi (and that is the whole
 reason for the Eclipse 3.x series). OSGi started out for like cell
 phones and stuff so it is very good and loading and unloading stuff an
 preventing memory leaks etc... since rebooting a phone is annoying if
 you are expecting a phone call. Indeed OSGi is being used to manage
 server stuff now as well.

-  Once the plugin gets loaded the "Platform" reads the "plugin.xml"
    file and "wires" the code into the resulting application. As a
    developer you can ask the Platform a question (such as what map tools
    are defined) and then do something with the answer - such as as make a
    toolbar for users to select the current map tool. The eclipse map
    editor does this as a toolbar; if you are embeding your own map may
    wish to go through the list and do something else (like a drop down
    combo box?).

It is important that the wiring of the application is not magic;
 programmers are responsible for asking the Platform questions and
 doing something with the result.

As an example the menus are done by the "org.eclipse.ui" plugin going
 though all the menus defined by all the plugin.xml files and producing
 something at the end of the day. In a similar fashion "org.eclipse.ui"
 goes through and finds all the "views" that a user could add to the
 screen.

How features work
^^^^^^^^^^^^^^^^^

Above we saw how plugins can be run resulting in an eclipse
 application. For very small projects you may want to do just that ...
 get a pile of plugins together and hit "run".
 When projects get a bit larger it is useful to gather a group of
 plugins together to make this a bit easier to manage.

The idea is that a feature gathers up plugins that together make one
 concept or capability available to the user.
 As a human visible concept features are the subject of update sites,
 or can be reviewed in the help menu if the user wants to know what is
 installed.

How products work
^^^^^^^^^^^^^^^^^

Products are just that; something packaged up and ready to go! You can
 actually export them as a stand alone application. You have a couple
 of options when defining a product; you can do so using plugins. Or
 for larger projects that you expect to last a while or get updated you
 should define it using features.

Consider a product as an Eclipse rcp app that is ready to go; you can
 define it as a set of plugins (good for small projects) or using
 features.

Plugins vs Features fight
^^^^^^^^^^^^^^^^^^^^^^^^^

So this is where we get into the thick of it.

-  plugins are going to do their best to run; but will refuse to run if
    not everything they need is available
-  features can be used to gather up plugins into groups for distribution

And who is responsible for making sure that the features actually
 gather plugins into groups that can run?
 You!

What about update site
^^^^^^^^^^^^^^^^^^^^^^

You can use an update site to publish features for download; since
 features are a group of plugins this is primary the way to distribute
 additional functionality to applications that are in the field. You
 can also of course use it to distribute updates or patches to existing
 features.

What did we miss
^^^^^^^^^^^^^^^^

The following concepts are not used that often:

-  Fragments are like half a plugin; they are used to patch an existing
    plugin. At a technical level this is done by mixing the plugin.xml
    from both the original plugin and the fragment together.

We used to have a fragment for each language supported by uDig.

-  Removing stuff; you can also get a bit fancy and ask the platform to
    ignore parts of the plugin.xml document (this can be used to strip
    menus or views out of another plugin if you consider them off topic
    for the task at hand). Why doesn't skip the classes? Well because you
    may have subclassed them for your own work .. so the normal Java code
    reuse still needs to be respected.

-  Classloader hell - the OSGi plugin system is mean - it loads plugins
    into different class loaders and only lets you work with code that you
    have explicitly told it you depend on!

-  Execution Environment - OSGi has started talking about the target
    environment and giving it a name (so you can tell the difference
    between Java 5 and Java 6). This is mostly used so a plugin can say
    it requires Java 6 (so OSGi won't accidentally load it when running on
    an older mac where only Java 5 is available).

How to fix it?
~~~~~~~~~~~~~~

If you go to run and the application won't start; open up your run
 configuration and "validate plugins" - it will list any plugins that
 could not run.
 You will need to see why they could not run (ie what they are missing)
 and make sure to add anything missing to your application.

If you are running as plugins there is a button to add anything
 missing in one easy step.

If you are running as features you will need to sort through what is
 missing and decide what feature to include it in. You may also be able
 to reuse one of the features already defined as part of the eclipse
 platform.
