package net.refractions.udig.project.listener;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;

import net.refractions.udig.project.EditFeature;
/**
 * EditFeature event notification, provides feedback during attribute value editing with additional
 * events covering isDirty, isVisible, isEnabled, isEditable model changes.
 * <p>
 * This is similar to {@link IPropertyChangeListener}.
 * 
 * @author Levi Putna
 * @since 1.3.0
 */
public interface EditFeatureListener {

    public enum StateType {
        DIRTY,
        VISIBLE,
        ENABLED,
        EDITABLE
    };

    /**
     * Notification of attribute value change
     * 
     * @param oldValue The old value of the changed property, or <code>null</code> if not known or
     *        not relevant.
     * @param newValue The new value of the changed property, or <code>null</code> if not known or
     *        not relevant.
     */
    public void attributeValueChange(PropertyChangeEvent event);

    /**
     * 
     */
    public void attributeStateChange(StateType type, EditFeature feature);

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
     *     if (feature.getType()getTypeName() == "Sample" && (feature.getAttribute(&quot;name&quot;).length() &lt; 5 || feature.getAttribute(&quot;name&quot;).length() &gt; 20)) {
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
     * Called before the feature is displayed, returning false will cause the value for every
     * attribute in this feature to return null.
     * 
     * <p>
     * This is a great place change the value that will be displayed in the UI.
     * </p>
     * 
     * <pre>
     * public Boolean beforeDesplay(SimpleFeature feature){
     *     if (feature.getType()getTypeName() == "NoName") {
     *         SimpleFeature.Attribute("name") = null;
     *     }
     *     return true; 
     * }
     * </pre>
     * 
     * @param feature
     * @return
     */
    public Boolean beforeDesplay(SimpleFeature feature);
}
