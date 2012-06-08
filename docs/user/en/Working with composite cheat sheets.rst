Working with composite cheat sheets
###################################

For procedures involve more then one task, a composite cheat sheet is used to provide step-by-step
guidance for each task in turn. Composite cheat sheets are displayed in the normal cheat sheet view
and show two panels which will be side by side or one above the other depending on the relative
height and width of the view.

The panel which appears on the left or on top (depending on the view orientation) is called the task
explorer and shows all of the tasks which need to be completed and their state. The lower or right
panel is called the task detail panel and shows the task which is selected in the explorer.

-  `Launching a composite cheat
   sheet <#Workingwithcompositecheatsheets-Launchingacompositecheatsheet>`_
* :doc:`Task explorer`

* :doc:`Task detail pane`

* :doc:`Task groups`

* :doc:`Cheat sheet tasks`

* :doc:`Task dependencies`

* :doc:`Skipping`

* :doc:`Restarting`

* :doc:`Closing`


**Related reference**


Cheat sheet

Launching a composite cheat sheet
=================================

Composite cheat sheets are launched in the same way as any other cheat sheet by selecting Help >
Cheat Sheets from the menu bar. If this command is not in the menu, it can be added from Window >
Customize Perspective > Commands, and check Cheat Sheets.

Task explorer
=============

The task explorer is the tree view which shows the tasks and groups of tasks in a composite cheat
sheet. Each task will also have an overlay image at the lower right corner it it is in progress, has
been completed or has been skipped. Selecting a task in the task explorer will cause that task to be
displayed in the task detail pane. Right clicking on a task in the task explorer brings up a context
menu which depending on the state of the task will allow the task to be started, skipped or reset.

Task detail pane
================

The task detail pane will show different contents depending on the kind of task and whether it has
been started. A cheat sheet task which has not been started will show a description and a button to
start the task (or if it cannot be started the reason why not). When in progress a cheat sheet task
will show a cheatsheet, and when it has been completed it will show a link to the next task.

Task groups
===========

There are three kinds of task groups, "set", "sequence" and "choice". A sequence represents a set of
subtasks that must be performed in order, a set represents a set of subtasks that can be performed
in any order, a choice represents a set of tasks only one of which should be performed. A set or
sequence is complete when all of its subtasks have been completed. A choice is complete when one of
its subtasks has been completed.

Cheat sheet tasks
=================

A cheat sheet task represents a single cheat sheet. When a cheat sheet task is first visited the
task detail pane shows a description of the task and gives the option to start working on the task
and possibly to skip the task. The picture below also shows the images for task sets, sequences and
choices. Note that some images are shown in gray, that is because these tasks cannot yet be started
because prerequisite tasks have not been completed.

Task dependencies
=================

A composite cheat sheet may have dependencies which require one task to be completed before another
can be started. An example of where this might happen would be if one task created a project and a
second task uses that project. In this case the first task must be completed or skipped before the
second task can start.

Skipping
========

The author of a composite cheat sheet can choose to make a task or task group optional. If a task is
optional it can be skipped, either by right clicking on that task in the task explorer and selecting
skip from the context menu, or by clicking on the "Skip this task" hyperlink in the task detail
pane. Skipping a task will allow tasks which depend on this task to be started.
 Order of task completion

The task detail area will provide hyperlinks which will walk through the tasks in order. It is also
possible to perform tasks out of order by selecting the task in the task explorer and starting work
on that task. This is useful if you want to go directly to a task of interest without working
through all earlier tasks.

Restarting
==========

Right clicking on the root node in the task explorer shows a menu item "Restart all tasks". If
selected this will reset the state of every task in the composite cheat sheet. Use this only if you
want to reset all tasks. The context menu for any other task will have a menu option to reset an
individual task. Any tasks which depend on this task will also be reset, and for task groups all
subtasks will also be reset.

Closing
=======

A composite cheat sheet is closed if the cheat sheet view is closed, if Eclipse is closed or if
another cheat sheet is opened. The state of each task is saved so that when the composite cheat
sheet is reopened its state will be restored.
