Keyboard Shortcut Example
^^^^^^^^^^^^^^^^^^^^^^^^^

**Definitions:**

-  Command - an abstract representation of some semantic behaviour, but not it's actual
   implementation
-  Key Binding - a mapping between a certain group of conditions, some user input and a triggered
   command
-  Scheme - set of one or more bindings
-  Context - Definition of a context where key bindings are active

There are many steps that must be take in order to define a completely new command and assign it to
an Action.

#. Define a org.eclipse.ui.command extension for the new command

   #. Define a category (part of the org.eclipse.ui.command extension) of the command. (You can also
      use an existing category if one that applies exists).
   #. Define the command (part of the org.eclipse.ui.command extension)

#. Define an org.eclipse.ui.contexts extension (or reuse and existing one). Common contexts are:

-  org.eclipse.ui.textEditorScope
-  org.eclipse.ui.contexts.dialogAndWindow
-  org.eclipse.ui.contexts.window
-  org.eclipse.ui.contexts.dialog
-  net.refractions.udig.project.ui.tool

#. Define a org.eclipse.ui.bindings extension

   #. Define a scheme (or use udig's default scheme:
      net.refractions.udig.defaultUDIGKeyConfiguration ). Schemes are part of the
      org.eclipse.ui.bindings extension.

-  Only 1 scheme is active at a time. So if you create a new scheme you have to create new bindings
   for every command that you want to use in your application.
-  To activate a new scheme you must create a plugin\_customization.ini file in your application
   plugin or choose a new scheme in the Window > Preferences > Keys preference page.

   #. Define a key binding for your command (part of the org.eclipse.ui.bindings extension). In the
      key binding definition you have to declare which command the binding applies to, the context
      that the binding will be active in and the scheme that the binding is part of.

#. You are now done the extension definition, now you must programmatically bind the command to an
   action.

   #. When you create your action set the definitionID to the command's id.

-  ::

       action.setActionDefinitionId("yourCommandsID");

   #. Next you must register your command with the current site's keybinding service.

-  ::

       part.getSite().getKeyBindingService().registerAction(action);

   #. During the creation of a view the context that are active must be defined as well. By default
      org.eclipse.ui.contexts.window context is active so if you keybinding is part of that context
      then you can skip this step.

-  ::

       site.getKeyBindingService().setScopes(new String[] {"yourContextID"});

**Tips**
 The ToolManager's contributeGlobalActions() sets all the "normal" uDig actions such as copy, paste,
delete, undo, redo, etc... The ToolManager can be obtained with the method
ApplicationGIS.getToolManager().

**Example:**

::

    <!-- This is part of org.eclipse.ui plugin.xml -->
          <extension
                point="org.eclipse.ui.contexts">
             <context
                   description="%context.tool.description"
                   id="net.refractions.udig.project.ui.tool"
                   name="%Tools"
                   parentId="org.eclipse.ui.contexts.window"/>
          </extension>
    <!-- This is part of net.refractions.udig.project.ui plugin.xml -->
          <extension
             point="org.eclipse.ui.commands">
            <command
                categoryId="net.refractions.udig.tools.toolCategory"
                name="%zoom.category.name"
                id="net.refractions.udig.tools.ZoomCommand"/>
          </extension>
       <extension
             point="org.eclipse.ui.bindings">
          <key
                commandId="net.refractions.udig.tools.ZoomCommand"
                contextId="net.refractions.udig.project.ui.tool"
                sequence="Z" schemeId="net.refractions.udig.defaultUDIGKeyConfiguration"/>
          </extension>

::

    // This is made up to illustrate the concept
    class NewView extends Viewpart {
      public void createPartControl( Composite parent ){
        // You don't need this step because it is included by default
        String[] newScopes = new String[];
        newScopes[0] = "net.refractions.udig.project.ui.tool";           
        getSite().getKeyBindingService().setScopes(newScopes);

        // Create action, assign and register action and its command
        IAction action=createAction();
        Action.setActionDefinitionId("net.refractions.udig.tools.ZoomCommand");
        IKeyBindingService service = part.getSite().getKeyBindingService();
        service.registerAction(action);
      }
    }

**Resources**

-  See the org.eclipse.ui plugin.xml for many existing commmands, categories, keybindings, contexts
   and schemes.
* `http://www.magma.ca/~pollockd/despumate/bindingsHowTo.html <http://www.magma.ca/~pollockd/despumate/bindingsHowTo.html>`_

