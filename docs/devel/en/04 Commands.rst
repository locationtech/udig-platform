04 Commands
===========

Commands are the only acceptable way of modifying the data model. The reason for forcing developers
to write and use commands is so that actions can be undone and redone, otherwise developers **will**
directly attack the model and none of the changes will be able to be undone by the application user.

-  `1 Simple Commands <1%20Simple%20Commands.html>`_ — how to **do stuff**
-  `2 NavCommand Example <2%20NavCommand%20Example.html>`_ — used to go places
-  `3 Draw Command <3%20Draw%20Command.html>`_ — affect the MapDisplay and are how tools provide
   dynamic feedback
-  `4 Edit Commands <4%20Edit%20Commands.html>`_ — affect data within a transaction
-  `5 Composite Commands <5%20Composite%20Commands.html>`_ — combine commands

Command authors have a parallel but separate API than most extension authors. Command authors are
permitted to use the *internal* API, that is classes with internal in their package name. Notice
that Command authors are not permitted to reference any classes implementation classes (classes with
impl in their package name).

Command Helper classes
~~~~~~~~~~~~~~~~~~~~~~

Command Factories
'''''''''''''''''

There are a number of factories built into uDig that create commonly used commands.

#. BasicCommandFactory: Creates the common commands such as Delete Layer Commands and Add Layer
   Commands.
#. SelectionCommandFactory: Creates subset of ICommands that sets the current selection in layers.
   All the commands are normal Commands.
#. NavigationCommandFactory: Creates common navigation commands such as PanCommand.
#. EditCommandFactory: Creates edit commands such as ModifyFeatureCommand and AddFeatureCommand.
#. DrawCommandFactory: Creates draw commands such as TranslationCommand and DrawShapeCommand.

