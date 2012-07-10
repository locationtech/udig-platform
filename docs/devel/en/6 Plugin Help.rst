6 Plugin Help
=============

The Eclipse RCP Platform provides the following:

-  Online Help
-  Context-Sensitive Help
-  Dynamically Generated Help

The literature suggests the creation of separate documentation plug-ins. That is a plug-in that only
consists of XML configuration files and HTML content for the help system.

In practice many plug-ins are package with there help files.

File

Use

toc.xml

table of contents for the top level "book"

toc\*.xml

table of contents associated with a heading

contexts\*.xml

maps context Ids to context-sensitive help

The above filenames are only a suggestion -
 The help system actually makes use of extensions points allowing any file to be used.

plugin.xml is used to extend three kinds of extension points:

-  toc - table of contents
-  contexts
-  contentProducer - allows for dynamic generation of help

Online Help Files
~~~~~~~~~~~~~~~~~

The first configuration file used by Online help is one that is familiar to you - plugin.xml.

plugin.xml extension points:

::

    <extension
             point="org.eclipse.help.toc">
          <toc file="toc.xml" primary="true"/>
          <toc file="tocconcepts.xml"/>
       </extension>

The next xml file is the above mentioned toc.xml file:

::

    <toc label="XYZ User's Guide" topic="html/overview.html">
       <topic label="Concepts">
          <anchor id="concepts"/>
       </topic>
    </toc>

And the tocconcepts.xml file follows a similar format:

::

    <toc label="Concepts" link_to="toc.xml#concepts"> 
        <topic label="Main Topic"  href="html/concepts/maintopic.html"> 
            <topic label="Sub Topic" href="html/concepts/subtopic.html" /> 
        </topic>
        <topic label="Main Topic 2">
            <topic label="Sub Topic 2" href="html/concepts/subtopic2.html" /> 
        </topic> 
    </toc>

The configuration files refer to the following directory structure:

::

    toc.xml
    tocconcepts.xml
    html/overview.html
    html/concepts/maintopic.html
    html/concepts/subtopic.html
    html/concepts/subtopic2.html

Help Category
~~~~~~~~~~~~~

Online help is traditional divided into four categories:

-  Getting Started - for tutorials
-  Concepts - to explain background information as needed
-  Tasks - really quick instructions on how to do stuff
-  Reference - screen snaps showing what everything is

Context Sensitive Help
~~~~~~~~~~~~~~~~~~~~~~

Context sensitive help (or InfoPops) is available when the user presses the F1 key.

Context sensitive help can be associated with:

-  Control
-  IAction - when used to generate a MenuItem (not toolbar)
-  Menu
-  MenuItem

plugin.xml file extension point:

::

    <extension point="org.eclipse.help.contexts"> 
           <contexts file="contexts.xml" plugin="an.example.plugin"/>
       </extension>

Sample context.xml file:

::

    <contexts>
      <context id="add_action_context">
        <description>This command adds.</description>
        <topic label="Add" href="tasks/tasks-1.htm"/>
        <topic label="Concepts" href="html/concepts/maintopic.html"/> 
      </context>
    </contexts>

To connect everything up on the UI side of things we need to know the help contexts ids.

::

    public interface IHelpContextIds {
        public static final String PREFIX = XYZPlugin.ID + "."; //$NON-NLS-1$
            // Dialogs
            public static final String CONNECTION = PREFIX + "connection_context"; //$NON-NLS-1$

            // View
            public static final String SEARCH_VIEW = PREFIX + "search_view_context";
            // Viewers
            public static final String SEARCH_VIEWER = PREFIX + "search_viewer_context"; //$NON-NLS-1$
     // Actions
     public static final String SEARCH_ACTION = PREFIX + "search_action_context"; //$NON-NLS-1$        
    }

And finally the connection needs to be made to the control:

::

    WorkbenchHelp.setHelp( newAction , HelpContextIds.NEW_REGISTRY_LOCATION_ACTION);

TODO: Dynamic Help
~~~~~~~~~~~~~~~~~~

Figure out the contentProducer extension point in the hopes of generating context-sensitive help for
DataStore parameter screens.

Sample Table of Contents Wizard
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Eclipse provides a code generation template associated with the toc extension point.

#. Start the Extension Point Selection Wizard
    |image0|

   #. Open the plugin.xml file, and switch to the extensions point tab
   #. Press the "Add" button

#. Start the "Help Content" template
    |image1|

   #. Select "org.exlipse.help.toc"
   #. Select "Help Content" from the available templates
   #. Next

#. Chose the categories required for you plug-in

   #. Note the Primary check box will result in a separate top level "book" in the help system

This is enough to get us going.

**Links**

-  `Rich Client Platform Tutorial
   3 <http://dev.eclipse.org/viewcvs/index.cgi/%7echeckout%7e/org.eclipse.ui.tutorials.rcp.part3/html/tutorial3.html>`_
-  `Eclipse Online Help Tutorial
   101 <http://devresource.hp.com/drc/technical_white_papers/ecliphelp/index.jsp#contexts_xml>`_

.. |image0| image:: /images/6_plugin_help/HelpTocExtentionPoint.jpg
.. |image1| image:: /images/6_plugin_help/HelpSampleTableOfContents.jpg
