4 Edit Commands
===============

Edit Commands
=============

Edit commands are commands that affect data within a transaction.

Example would be deleting or adding a feature or editing a feature. This is also a special case
because the frame work ensures that a transaction has been started before edit commands are
executed.

Transactions can be controlled using the Commit/Rollback functionality of uDig. By modifying data
withing a Transaction you allow your users to "preview" any modifications made before application.

By supporting the concept of a Transaction at the application level, modification made via tools,
opperations and wizards can all coexsist together. To follow up these ideas the application also
supports a shared "Issues List", so Tools, Opperations an wizards can request user intervention.

Code Example
~~~~~~~~~~~~

**DeleteFeatureCommand.java**

::

    /**
     * Deletes a feature from the provided layer.
     *  
     * @author jeichar
     * @since 0.6.0
     * @see AbstractEditCommand
     * @see UndoableCommand
     */
    public class DeleteFeatureCommand extends AbstractEditCommand implements
            UndoableCommand {

        Feature feature;

        private Layer sourceLayer;

        protected boolean done;

        /**
         * Construct <code>DeleteFeatureCommand</code>.
         */
        public DeleteFeatureCommand(Feature feature, Layer layer) {
            this.feature = feature;
            this.sourceLayer = layer;
        }

        /**
         * @see net.refractions.udig.project.command.Command#run()
         */
        public void run() throws Exception {
            sourceLayer.getResource(FeatureStore.class, null).removeFeatures(
                    FilterFactory.createFilterFactory().createFidFilter(
                            feature.getID()));
            map.getEditManagerInternal().setEditFeature(null, null);
        }

        /**
         * @see net.refractions.udig.project.command.Command#copy()
         */
        public Command copy() {
            return new DeleteFeatureCommand(feature, sourceLayer);
        }

        /**
         * @see net.refractions.udig.project.command.Command#getName()
         */
        public String getName() {
            return Policy.bind("DeleteFeatureCommand.deleteFeature"); //$NON-NLS-1$
     }

        /**
         * @see net.refractions.udig.project.command.UndoableCommand#rollback()
         */
        public void rollback() throws Exception {
            map.getEditManagerInternal().setEditFeature(feature, sourceLayer);
            map.getEditManagerInternal().addFeature(feature, sourceLayer);
        }

    }

