Working With Cheat Sheets
#########################

Cheat sheets is a emerging technology within Eclipse that is meant to guide a users through a series
of complex tasks to achieve some overall goal. Some tasks can be performed automatically, such as
launching the required tools for the user. Other tasks need to be completed manually by the user.

:doc:`add_a_cheat_sheet`


:doc:`contributing_a_cheat_sheet`


* :doc:`add_action_to_an_item`

* :doc:`add_a_command_to_an_item`


:doc:`contributing_a_category`


:doc:`authoring_guidelines`


* :doc:`when_to_create_cheat_sheets`

-  `When to create composite cheat
   sheets <#WorkingwithCheatSheets-Whentocreatecompositecheatsheets>`_
* :doc:`when_not_to_use_cheat_sheets`

* :doc:`when_to_create_a_new_category`


:doc:`cheatsheets_in_udig`


Reference:

* `http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.platform.doc.isv/guide/ua\_cheatsheet\_guidelines.htm <http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.platform.doc.isv/guide/ua_cheatsheet_guidelines.htm>`_

Add a Cheat Sheet
=================

The help plugin (net.refractions.udig.help) is responsible for holding the cheat sheet contributions
and content. Cheat sheets are defined using the org.eclipse.ui.cheatsheets.cheatSheetContent
extension point. The cheat sheet content itself is defined in a separate XML file stored in the
cheatsheet folder.

Contributing a cheat sheet
--------------------------

Contributing a cheat sheet is pretty straightforward.

#. In the help plugin sheetsheet folder create a new XML file with the name of your new cheat sheet.
   Example newProjectWizard.xml
#. Past this template into the file.

   ::

       <?xml version="1.0" encoding="UTF-8"?>
       <cheatsheet title="Cheat Sheet Title">
         <intro>
           <description>
               Add your cheat sheet description here
           </description>
         </intro>  
         <item title="Step One">
            <description>
               Step one description
           </description>
         </item>
         <item title="Step two descriptions">
            <description>
               Add your cheat sheet description here
           </description>
         </item>
       </cheatsheet>

#. Save the file.
#. Add a cheat sheet to the org.eclipse.ui.cheatsheets.cheatSheetContent extension point and use the
   file you just created as your contentFile.

   ::

       <extension
              point="org.eclipse.ui.cheatsheets.cheatSheetContent">
               <cheatsheet
               category="net.refractions.udig.help.cheatsheet.category.myCategory"
               composite="false"
               contentFile="cheatsheets/myCheatSheetContent.xml"
               id="net.refractions.udig.help.cheatsheet.myCheatSheetContent"
               name="My Cheat Sheet">
           </cheatsheet>
       </extension>

#. Edit your cheat sheet template adding new items and actions as required. For more information on
   Cheat Sheet XML format see the `eclipse
   documentation <http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fextension-points%2FcheatSheetContentFileSpec.html>`_

Add Action to an Item
~~~~~~~~~~~~~~~~~~~~~

To add action to your item you must first make sure that the help plugin depends on the plugin that
defines the action. You can then add an <action> to your item.

::

    <item 
      title="New Layer Wizard" 
      dialog="true" 
      href="/net.refractions.udig.help/EN/Add Data wizard.html">
        <description>
            Choose <b>File > New > New Layer</b> from the menu bar to open up the Add Data wizard
        </description>
        <action 
            class="net.refractions.udig.project.ui.internal.actions.AddLayersAction" 
            pluginId="net.refractions.udig.project.ui.editor"
            confirm="false"/>
    </item>

Add a Command to an Item
~~~~~~~~~~~~~~~~~~~~~~~~

To add command to your item you must first make sure that the help plugin depends on the plugin that
defines the command. You can then add an <command> to your item.

::

    <item
        title="Style View">
        <description>
        uDig also offers a Style View that can be utilised to preform simple layer styling.
        To see the Style View in action change to the Style Perspective by selection
        <b>Window > Open Perspective > Style Perspective</b>
         </description>
         <command
              serialization = "org.eclipse.ui.perspectives.showPerspective(org.eclipse.ui.perspectives.showPerspective.perspectiveId=net.refractions.udig.ui.stylePerspective)" 
            required="false" 
            translate=""/>
    </item>

The above example has:

-  command: "org.eclipse.ui.perspectives.showPerspective( ... )"
-  parameter:
   org.eclipse.ui.perspectives.showPerspective.perspectiveId=net.refractions.udig.ui.stylePerspective

Contributing a category
-----------------------

Add a cheat sheet category to the org.eclipse.ui.cheatsheets.cheatSheetContent extension point

::

    <extension
           point="org.eclipse.ui.cheatsheets.cheatSheetContent">
        <category
              id="net.refractions.udig.help.cheatsheet.category.categoryName"
              name="Category Name">
        </category>
    </extension>

Authoring Guidelines
====================

-  Where posible the cheat sheet should provide a "perform action" option.
-  Where posible the cheat sheet should re-use existing actions and commands as opposed to cheating
   there own.
-  Cheat sheets should provide links to the help content where ever appropriate.
-  Cheat sheets should should never consist of more than ten steps/items.
-  Instructions that devine a menu option, button or tool bar action should be in bold.

When to create cheat sheets
---------------------------

Cheat sheets are well suited to tasks which consist of steps which lead towards a tangible goal. The
goal must be well defined so that the user can see success when all the steps in cheat sheet have
been completed. Tutorials are often good candidates for cheat sheets, in a tutorial the goal is to
learn how to perform a specific task. Cheat sheets will usually contain up to 10 steps and can be
completed in a half an hour or less. For larger tasks consider using a composite cheat sheet.

When to create composite cheat sheets
-------------------------------------

Composite cheat sheets are used to for providing guidance through a task which is too large to
describe in a single cheat sheet or which has multiple goals. A composite cheat sheet can be used
when you are guiding the user over a sequence of tasks that exist in individual cheat sheets but
make up a greater task. Example "Quickstart".

When not to use cheat sheets
----------------------------

Cheat sheets work best when problem can be solved by a sequence of simple steps. Cheat sheets are
not a substitute for the help system which allows for creation of HTML pages with rich graphics and
random access of information using search and hyperlinks. Cheat sheets are not intended for tasks
which require a large amount of text to be input by the user.

When to create a new category
-----------------------------

Try to fit your cheat sheet into existing categories, if your cheat sheet doesn't fit into any
existing categories then create your own.

Cheatsheets in uDig
===================

We briefly considered adding cheatsheets to the "help" plugin; however that would force the help
plugin to depend on everything (so not a good idea).

-  net.refractions.project.ui.editor - use of GIS Application (Map / Layer / Style )
-  net.refractions.catalog.ui - use of GIS Platform (Catalog / Data )
-  net.refractions.printing.ui - use of printing facilities
-  net.refractions.udig - location for cheatsheets that need to use everything

The usual guidelines for handling of resources apply with respect to Internationalisation:

-  net.refractions.udig.project.ui.editor/nl/en/style\_cheatsheet.xml
-  net.refractions.udig.project.ui.editor/nl/de/style\_cheatsheet.xml (translation to German)

