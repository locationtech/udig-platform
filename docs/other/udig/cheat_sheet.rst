Cheat Sheet
###########

uDig : Cheat Sheet

This page last changed on Nov 23, 2011 by jgarnett.

Motivation
==========

Cheat sheets is a new emerging technology within Eclipse that is meant to guide a users through a
series of complex tasks to achieve some overall goal. Some tasks can be performed automatically,
such as launching the required tools for the user. Other tasks need to be completed manually by the
user.

Inspiration
===========

Eclipse has long made use of Cheat Sheets to guide you through some of its application development
processes. uDig could benefit from moving the uDig tasks and some of the tutorials form the help
wiki to cheat sheets.

Each cheat sheet is designed to help a user complete some task, and it lists the sequence of steps
required to help them achieve that goal. As they progress from one step to the next, the cheat sheet
will automatically launch the required tools for them. If there is a manual step in the process, the
step will tell them to perform the task and click a button in the cheat sheet to move on to the next
step. Also, relevant help information to guide them through a task is retrieved in a single click so
that lengthy documentation searches will no longer be required.

Proposal
========

The uDig help plugin (net.refractions.udig.help) is responsible for holding the online documentation
including a series of html pages and XML files used to drive the Help dialog. Cheat sheet should
contribute to the help plugin adding a cheatsheet (net.refractions.udig.help.cheatsheet) package and
contributing to the org.eclipse.ui.cheatsheets.cheatSheetContent extension point.

Cheat Sheet Extension Example
-----------------------------

.. code:: code-xml

    <extension point="org.eclipse.ui.cheatsheets.cheatSheetContent">
        <category id="net.refractions.udig.help.cheatsheet.categoty.project" name="Projects"></category>
        <cheatsheet
            category="net.refractions.udig.help.cheatsheet.categoty.project"
            composite="false"
            contentFile="content/newProject.xml"
            id="createnewprojectnet.refractions.udig.help.cheatsheet.newproject"
            name="Create New Project">
        </cheatsheet>
    </extension>

Cheat Sheet Help Menu
---------------------

A cheat Sheet UI is defined by eclipse and we need to add an action and menu contribution to the
help menu

Cheat Sheet Help Menu Extension
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code:: code-xml

    <extension point="org.eclipse.ui.actionSets">
        <actionSet id="org.eclipse.ui.cheatsheets.actionSet" label="%CHEAT_SHEETS" visible="true">
            <action class="org.eclipse.ui.cheatsheets.CheatSheetExtensionFactory:helpMenuAction"
                id="org.eclipse.ui.cheatsheets.actions.CheatSheetHelpMenuAction"
                label="Cheat Sheet..."
                menubarPath="help/helpStart"
                style="push"/>
        </actionSet>
    </extension>

API Change
----------

| uDig defines its own extension points OpAction and ToolAction, we will need to wrap these in a
Action or CheatSheetAction if we want to make use of them via the cheat sheet "Preform Action"
button. This will meen we need to add two new API's to uDig OpCheatSheetAction and
ToolCheatSheetAction. I will ne prototyping these within the help plugin however It might be worth
moving them into another plugin at a later date. Cheat sheets can also make use of commands, not
sure what the benefit is over Actions.

|image0|

| 

Wireframes and ScreenShots
--------------------------

Cheat Sheet in Action
~~~~~~~~~~~~~~~~~~~~~

| 

|image1|

| 

Cheat Sheet Browser
~~~~~~~~~~~~~~~~~~~

| 

|image2|

| 

Status
======

Project Steering committee support:

-  Andrea Antonello: +1
-  Jesse Eichar: +1
-  Jody Garnett: +1
-  Mauricio Pazos: +1

Committer Support:

-   

A vote of -1 requires an alternate suggestion; community members are invited to indicate
support/suggestions.

Documentation
=============

Will update as project progresses.

Tasks
=====

 

no progress

|image3|

in progress

|image4|

blocked

|image5|

help needed

|image6|

done

Tasks:

#. |image7| Initial design for review and feedback
#. |image8| Add cheat sheet menu contribution
#. |image9| Initial cheat sheet development including example cheat sheet without Op and Tool
   Actions
#. |image10| Build Action API wrapper for Op and Tool Actions and add actions to cheat sheet example
#. Add new test cases
#. |image11| Updated developers guide documentation
#. |image12| GitHub pull request
#. |image13| Review from PSC member and request merged

Reference
=========

http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.platform.doc.user/reference/ref-cheatsheets.htm

| 

Attachments:

| |image14| `Cheat Sheet Dialog.png <download/attachments/13533219/Cheat%20Sheet%20Dialog.png>`__
(image/png)
|  |image15| `Screen Shot 2011-07-27 at 3.30.15
PM.png <download/attachments/13533219/Screen%20Shot%202011-07-27%20at%203.30.15%20PM.png>`__
(image/png)
|  |image16| `Cheat Sheet Class.jpg <download/attachments/13533219/Cheat%20Sheet%20Class.jpg>`__
(image/jpeg)
|  |image17| `Cheat Sheet Class
V2.jpg <download/attachments/13533219/Cheat%20Sheet%20Class%20V2.jpg>`__ (image/jpeg)

+-------------+----------------------------------------------------------+
| |image19|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+-------------+----------------------------------------------------------+

.. |image0| image:: download/attachments/13533219/Cheat%20Sheet%20Class%20V2.jpg
.. |image1| image:: download/attachments/13533219/Screen%20Shot%202011-07-27%20at%203.30.15%20PM.png
.. |image2| image:: download/attachments/13533219/Cheat%20Sheet%20Dialog.png
.. |image3| image:: images/icons/emoticons/star_yellow.gif
.. |image4| image:: images/icons/emoticons/error.gif
.. |image5| image:: images/icons/emoticons/warning.gif
.. |image6| image:: images/icons/emoticons/check.gif
.. |image7| image:: images/icons/emoticons/star_yellow.gif
.. |image8| image:: images/icons/emoticons/star_yellow.gif
.. |image9| image:: images/icons/emoticons/star_yellow.gif
.. |image10| image:: images/icons/emoticons/star_yellow.gif
.. |image11| image:: images/icons/emoticons/star_yellow.gif
.. |image12| image:: images/icons/emoticons/star_yellow.gif
.. |image13| image:: images/icons/emoticons/star_yellow.gif
.. |image14| image:: images/icons/bullet_blue.gif
.. |image15| image:: images/icons/bullet_blue.gif
.. |image16| image:: images/icons/bullet_blue.gif
.. |image17| image:: images/icons/bullet_blue.gif
.. |image18| image:: images/border/spacer.gif
.. |image19| image:: images/border/spacer.gif
