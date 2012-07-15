Simple Commands
~~~~~~~~~~~~~~~

These are the workhorses of uDig application development, this is how to **do stuff**, and
interestingly **undo stuff**.

ICommand and IUndoable
^^^^^^^^^^^^^^^^^^^^^^

There are two basic categories of commands, a ***normal*** ICommand, which can not be undone and
when executed clears the undo stack, and **IUndoable** Commands, which can be undone and should be
used in as many cases as possible.

An example of a situation where is command can not be undone is a commit transaction command. Once a
transaction is committed it makes sense that it cannot be undone.

Code Example
^^^^^^^^^^^^

**AddLayerCommand.java**

::

    /**
     * Creates a command that adds a layer to the map.
     * 
     * @author jones
     * @since 0.6.0
     */
    public class AddLayerCommand extends AbstractCommand implements UndoableCommand {

        private Layer layer;
        private int index=-1;
        /**
         * Construct <code>AddLayerCommand</code>.
         *
         * @param layer the layer that will be added.
         */
        public AddLayerCommand(Layer layer) {
            this.layer=layer;
        }

        /**
         * Construct <code>AddLayerCommand</code>.
         *
         * @param layer the layer that will be added.
         * @param index the zorder that the layer will be added.
         */
        public AddLayerCommand(Layer layer, int index) {
            this.layer=layer;
            this.index=index;
        }
        
        /**
         * Remove the layer that was added during execution.
         * @see net.refractions.udig.project.command.UndoableCommand#rollback()
         */
        public void rollback() throws Exception {
            getMap().getLayersInternal().remove(layer);
        }

        /**
         * Adds a layer to the map.  Defensive programming is recommended but command
         * framework protects against exceptions raised in commands.
         * @see net.refractions.udig.project.command.Command#run()
         */
        public void run() throws Exception {
            if( index<0 || index>getMap().getLayersInternal().size())
                getMap().getLayersInternal().add(layer);
            else
                getMap().getLayersInternal().add(index, layer);
        }

        /**
         * Returns a copy of the command.  A command should only be executed once.  If the same
         * command is to be used multiple times the command should be copied.  Some commands
         * contain internal state.
         * 
         * @see net.refractions.udig.project.command.Command#copy()
         */
        public Command copy() {
            return new AddLayerCommand(layer, index);
        }

        /**
         * Each command has a name that is displayed with the undo/redo buttons.
         * @see net.refractions.udig.project.command.Command#getName()
         */
        public String getName() {
            return Policy.bind("AddLayerCommand.Name")+layer.getName(); //$NON-NLS-1$
        }

    }

