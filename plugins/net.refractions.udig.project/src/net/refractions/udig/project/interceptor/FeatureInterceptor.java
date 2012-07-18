package net.refractions.udig.project.interceptor;

import net.refractions.udig.project.EditFeature;

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
     * Called before a NEW feature is created and added to the current layer, returning false will
     * cause the create event to be canceled. It is the responsibility of the implementing method to
     * inform the EditManager as to why the feature was canceled. {@link
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
    public final String CREATE = "featureCreated";

    /**
     * Called before a feature is added the the current layer, returning false will cause the delete
     * event to be canceled. It is the responsibility of the implementing method to inform the
     * EditManage as the why the feature was canceled.
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
    public final String DELETE = "delete";

    /**
     * Called before any outstanding changes in the EditFeature are written back the the feature
     * model. Returning false will cause the event to be blocked from calling.
     */
    public final String APPLY = "apply";

    /**
     * Called before the the EditFeature reverts back to the feature model forgetting all
     * outstanding changes.
     */
    public final String CANCEL = "cancel";

    /**
     * Called before a feature gains focus in the edit manager.
     * 
     * <p>
     * This is a great place add listeners that will update the UI, this is also a great place
     * change attribute value that will be displayed in the UI.
     * </p>
     * 
     */
    public final String ACTIVATE = "active";

    /**
     * Called before a feature looses focus in the EditManage. This is a great place to remove any
     * listeners.
     */
    public final String INACTIVATE = "inactive";

    /**
     * Interceptor used to interact with Feature.
     * <p>
     * The provided EditFeature is used to communicate the current editing state, including a
     * reference to the current EditManager (for session information) and Layer if avaiable.
     * <p>
     * 
     * <p>
     * The EditFeature is used to communicate the results of processing, you can record any errors
     * or warnings on an attribute by attribute basis. Flag asttribute state (such as editable based
     * on a security check), fill in default values.
     * <p>
     * Feature Life-cycle:
     * <ul>
     * <li>createFeature: used to fill in default values when a feature is first created and before
     * it is shown to the user or available for interaction. If an Exception is thrown
     * the user will be prevented from creating this feature (perhaps due to a failed security check).</li>
     * <li>deleteFeature: used to perform any integrity checks and otherwise clean up references or
     * internal cached information associated with this feature. If an Exception is thrown the user
     * will be prevented from removing the feature (perhaps due to a failed security check).
     * </li>
     * </ul>
     * Feature persistence:
     * <ul>
     * <li>applyFeature: Called prior to a feature being stored (after modification). Can be used as
     * a last ditch check, throw IllegalStateException to Cancel write. Can also be used to clear any
     * associated caches, so that they are refreshed from the backing datastore.</li>
     * <li>cancelFeature: Called prior to a feature edit being canceled (after modification). Can
     * also be used to clear any associated caches, so that they are refreshed from the backing
     * datastore.</li>
     * </ul>
     * Feature editing:
     * <ul>
     * <li>activateFeature: Called prior to displaying the feature for review and editing. Often used to register
     * listeners, or fill in an initial set of warnings.</li>
     * <li>deactivateFeature: Called </li>
     * </ul>
     * When throwing an exception it responsibility of the Interceptor to inform the EditFeature as to
     * why the action was canceled. {@link EditFeature.addErrorMessage()}
     * </p>
     * 
     * @param feature EditFeature
     */
    public void run(EditFeature feature);;
}
