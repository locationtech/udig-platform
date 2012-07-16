package net.refractions.udig.project.interceptor;

import org.opengis.feature.Feature;

/**
 * Used to intercept and interrupt events during the feature lifecycle.
 * 
 * @author Jody
 * @author Levi Putna
 * @since 1.2.0
 */
public interface FeatureInterceptor {
    /**
     * Extension Point ID of feature interceptors
     */
    final String EXTENSION_ID = "net.refractions.udig.project.featureInterceptor"; //$NON-NLS-1$
    
    /**
     * the xml tag that will hold the configuration of this extension point.
     */
    final String XML_TAG = "featureCreated";

    public enum Lifecycle {
        /**
         * Called before a NEW feature is created and added to the current layer, returning false
         * will cause the create event to be canceled. It is the responsibility of the implementing
         * method to inform the EditManager as to why the feature was canceled. {@link
         * EditFeature.addErrorMessage()}
         * 
         * <p>
         * Example:
         * 
         * Lifecycle LIFECYCLE_ID = Lifecycle.CREATE;
         * 
         * <pre>
         * public Boolean run(SimpleFeature feature) {
         * 
         *     if (feature.getType()getTypeName() == "Sample" && (feature.getAttribute(&quot;name&quot;).length() &lt; 5 || feature.getAttribute(&quot;name&quot;).length() &gt; 20)) {
         *         editManager
         *                 .setErrorMessage(&quot;Attribute 'name' must be greater than 5 characters and less than 20 characters in length.&quot;);
         *         return false;
         *     }
         *     return true;
         * }
         * </pre>
         */
        CREATE,

        /**
         * Called before a feature is added the the current layer, returning false will cause the
         * delete event to be canceled. It is the responsibility of the implementing method to
         * inform the EditManage as the why the feature was canceled.
         * 
         * <p>
         * Example:
         * 
         * Lifecycle LIFECYCLE_ID = Lifecycle.DELETE;
         * 
         * <pre>
         * public Boolean run(SimpleFeature feature) {
         *      if (feature.getType()getTypeName() == "ReadOnly") {
         *         editManager
         *                 .setErrorMessage("Feature type '" + feature.getType()getTypeName() + "' is not editable.");
         *         return false;
         *     }
         *     return true;
         * }
         * </pre>
         */
        DELETE,

        /**
         * Called before any outstanding changes in the EditFeature are written back the the feature
         * model. Returning false will cause the event to be blocked from calling.
         */
        APPLY,

        /**
         * Called before the the EditFeature reverts back to the feature model forgetting all
         * outstanding changes.
         */
        CANCEL,

        /**
         * Called before a feature gains focus in the edit manager.
         * 
         * <p>
         * This is a great place add listeners that will update the UI, this is also a great place
         * change attribute value that will be displayed in the UI.
         * </p>
         * 
         */
        ACTIVATE,

        /**
         * Called before a feature looses focus in the EditManage. This is a great place to remove
         * any listeners.
         */
        INACTIVATE
    }

    /**
     * Action that this intercepter will be intercepting, if not specified defaults to
     * {@link Lifecycle#CREATE}
     */
    Lifecycle LIFECYCLE_ID = Lifecycle.CREATE; //$NON-NLS-1$

    /**
     * Performs an action on a feature.
     * <p>
     * You can look up the current EditLayer from the EditManager if you want to check out what is
     * going on. Chances are the provided feature is adaptable and can adapt to the current layer
     * anyways.
     * <p>
     * 
     * <p>
     * Returning False will block the action from happening, It is the responsibility of the
     * implementing class to inform the EditFeature as to why the action was canceled. {@link
     * EditFeature.addErrorMessage()}
     * </p>
     * 
     * @param feature
     * @return return false to block the action from happening. True to proceed with the action.
     */
    public Boolean run(Feature feature);
}
