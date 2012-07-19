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
     * Called after a feature is created, and before it is added to the current layer.
     * <p>
     * Feature are initially created using the create geometry tools, with additional default values
     * determined from the GeoTools FeatureType. This interceptor allows you to fill in appropriate
     * default values (possibly based on the current selection or opening a dialog to ask the user
     * to fill in a form).
     * <p>
     * If your interceptor throws an exception it will be reported to the user as a error ( along
     * with any other errors you record against specific attributes). If you register any errors
     * against the EditFeature adding of the feature will be blocked.
     * <p>
     * You can also register warnings against individual attributes and they will be presented to
     * the user.
     * </p>
     * Consider the following example:
     * 
     * <pre>
     * public void run(EditFeature feature) {
     *     if(!"Sample".equals(feature.getType().getTypeName() ) {
     *        return; // wrong featureType
     *     }
     *     String name = (String) feature.getAttribute("name");
     *     if( name == null ){
     *         feature.getState("name").addError("{0} is required");
     *     }
     *     if( name.legnth < 5 || name.length > 20 ){
     *         feature.getState("name").addWarning("{0} limited to between 5 and 20 characters: {1}");
     *     }
     * }
     * </pre>
     * 
     * Note any outstanding errors prevent a feature being added, outstanding war
     */
    public final String PRE_CREATE = "featurePreCreate";

    /**
     * Called after a feature is added to the current layer.
     * <p>
     * This interceptor may be used to open an associated view, update any interaction logs or
     * otherwise respond to content being added.
     * <p>
     * Any warnings raised against the EditFeature will be displayed to the user,
     * <p>
     * If there are any errors raised against the EditFeature we have a very limited ability to
     * respond. While the errors will be displayed; the feature has already been added at this
     * point.
     * <p>
     */
    public final String CREATED = "featureCreated";

    /**
     * Called prior to a feature being removed.
     * <p>
     * Features are removed using the delete feature tool, this interceptor allows you to perform
     * any associated cleanup (or sanity check) prior to the feature being removed.
     * <p>
     * If your interceptor throws an exception it will be added to the EditFeature as an error. If
     * there are any outstanding errors deleting the feature will be blocked.
     * <p>
     * In a similar fashion any warnings raised will be displayed to the user. This can be used to
     * remind them of associated work.
     * <p>
     */
    public final String PRE_DELETE = "featurePreDelete";

    public final String DELETED = "featureDeleted";

    /**
     * Called before any outstanding changes in the EditFeature are written back the the feature
     * model. Returning false will cause the event to be blocked from calling.
     */
    public final String APPLY = "featureApply";

    /**
     * Called before the the EditFeature reverts back to the feature model forgetting all
     * outstanding changes.
     */
    public final String CANCEL = "featureCancel";

    /**
     * Called before a feature gains focus in the edit manager.
     * 
     * <p>
     * This is a great place add listeners that will update the UI, this is also a great place
     * change attribute value that will be displayed in the UI.
     * </p>
     * 
     */
    public final String ACTIVATE = "featureActive";

    /**
     * Called before a feature looses focus in the EditManage. This is a great place to remove any
     * listeners.
     */
    public final String DEACTIVATE = "featureInactive";

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
     * <li>featurePreCreate: used to fill in default values when a feature is first created and
     * before it is shown to the user or available for interaction. If an Exception is thrown the
     * user will be prevented from creating this feature (perhaps due to a failed security check).</li>
     * <li>featureCreatef</li>
     * <li>featurePreDelete: used to perform any integrity checks associated with removing this
     * feature. If an Exception is thrown (or any errors otherwised raised against the feature) the
     * user will be prevented from removing. Example: A failed security check.</li>
     * <li>featureDeleted: clean up references or internal cached information associated with this
     * feature</li>
     * </ul>
     * Feature persistence:
     * <ul>
     * <li>featureApply: Called prior to a feature being stored (after modification). Can be used as
     * a last ditch check, throw IllegalStateException to Cancel write. Can also be used to clear
     * any associated caches, so that they are refreshed from the backing datastore.</li>
     * <li>featureCancel: Called prior to a feature edit being canceled (after modification). Can
     * also be used to clear any associated caches, so that they are refreshed from the backing
     * datastore.</li>
     * </ul>
     * Feature editing:
     * <ul>
     * <li>featureActivate: Called prior to displaying the feature for review and editing. Often
     * used to register listeners, or fill in an initial set of warnings.</li>
     * <li>featureDeacitvate:</li>
     * </ul>
     * 
     * @param feature EditFeature
     */
    public void run(EditFeature feature);;
}
