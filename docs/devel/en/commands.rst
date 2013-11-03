Commands
~~~~~~~~

Commands are the only acceptable way of modifying the data model. The reason for forcing developers
to write and use commands is so that actions can be undone and redone, otherwise developers **will**
directly attack the model and none of the changes will be able to be undone by the application user.

Command authors have a parallel but separate API than most extension authors. Command authors are
permitted to use the *internal* API, that is classes with internal in their package name. Notice
that Command authors are not permitted to reference any classes implementation classes (classes with
impl in their package name).

Command Helper classes
^^^^^^^^^^^^^^^^^^^^^^

There are a number of factories built into uDig that create commonly used commands.

#. BasicCommandFactory: Creates the common commands such as Delete Layer Commands and Add Layer
   Commands.
#. SelectionCommandFactory: Creates subset of ICommands that sets the current selection in layers.
   All the commands are normal Commands.
#. NavigationCommandFactory: Creates common navigation commands such as PanCommand.
#. EditCommandFactory: Creates edit commands such as ModifyFeatureCommand and AddFeatureCommand.
#. DrawCommandFactory: Creates draw commands such as TranslationCommand and DrawShapeCommand.

