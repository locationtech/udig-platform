package net.refractions.udig.project.listener;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;

/**
 * A listener which is notified during {@link EditFeature} life cycle events.
 * 
 * <p>
 * Implement to listen to EditFeature changes
 * 
 * @author Levi Putna
 * @since 1.3.0
 */
public interface EditFeatureListener {

    /**
     * Called before an attribute value changes, returning false will cause the edit event to be
     * canceled. It is the responsibility of the implementing method to inform the EditManager as to
     * why the edit was canceled. {@link EditManager.addErrorMessage()}
     * 
     * <p>
     * This is also the best place to update the value of any other attributes that's value is
     * calculated from this attribute. This change will be applied in the same transaction and will
     * not trigger a {@link #beforeEdit(SimpleFeature, AttributeDescriptor)} or
     * {@link #afterEdit(SimpleFeature, AttributeDescriptor)}
     * <p>
     * Example:
     * 
     * <pre>
     * public Boolean beforeEdit(Feature feature, AttributeDescriptor attributeDescriptor);
     *     feature.setAttribute("name", attributeDescriptor.name + " " + feature.getAttribute("type"));
     *     return true;
     * }
     * 
     * </pre>
     * 
     * @param feature
     * @param attributeDescriptor
     * @return true to allow the edit, false to cancel the edit.
     */
    public Boolean beforeEdit(SimpleFeature feature, AttributeDescriptor attributeDescriptor);

    /**
     * Called after an attribute value changes, this is a great place to add any code that updater
     * the UI.
     * 
     * @param feature
     * @param attributeDescriptor
     * @return
     */
    public void afterEdit(SimpleFeature feature, AttributeDescriptor attributeDescriptor);

    /**
     * Called before a feature is added to the current layer, returning false will cause the create
     * event to be canceled. It is the responsibility of the implementing method to inform the
     * EditManager as to why the feature was canceled. {@link EditFeature.addErrorMessage()}
     * 
     * <p>
     * Example:
     * 
     * <pre>
     * public Boolean beforeCreate(SimpleFeature feature) {
     * 
     *     if (feature.getType()getTypeName() == "Samle" && (feature.getAttribute(&quot;name&quot;).length() &lt; 5 || feature.getAttribute(&quot;name&quot;).length() &gt; 20)) {
     *         editManager
     *                 .setErrorMessage(&quot;Attribute 'name' must be greater than 5 characters and less than 20 characters in length.&quot;);
     *         return false;
     *     }
     *     return true;
     * }
     * </pre>
     * 
     * @param feature
     * @return true to allow the edit, false to cancel the edit.
     */
    public Boolean beforeCreate(SimpleFeature feature);

    /**
     * Called after a feature was added, this is a great place to update any UI.
     * 
     * @param feature
     */
    public void afterCreate(SimpleFeature feature);

    /**
     * Called before a feature is added the the current layer, returning false will cause the delete
     * event to be canceled. It is the responsibility of the implementing method to inform the
     * EditManage as the why the feature was canceled.
     * 
     * <p>
     * Example:
     * 
     * <pre>
     * public Boolean beforeDelete(SimpleFeature feature) {
     *      if (feature.getType()getTypeName() == "ReadOnly") {
     *         editManager
     *                 .setErrorMessage("Feature type '" + feature.getType()getTypeName() + "' is not editable.");
     *         return false;
     *     }
     *     return true;
     * }
     * </pre>
     * 
     * @param feature
     * @return true to allow the delete, false to cancel the delete.
     */
    public Boolean beforeDelete(SimpleFeature feature);

    /**
     * Called after a feature was deleted, this is a great place to update any UI.
     * 
     * @param feature
     */
    public void afterDelete(SimpleFeature feature);

    /**
     * 
     * Called before each attribute is desplayed, returning false will cause the value to not be
     * desplayed.
     * 
     * @param feature
     * @return
     */
    public Boolean beforeDesplay(SimpleFeature feature);
}
