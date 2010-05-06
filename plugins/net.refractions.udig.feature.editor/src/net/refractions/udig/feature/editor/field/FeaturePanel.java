package net.refractions.udig.feature.editor.field;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.project.ui.IFeaturePanel;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Abstract FetaurePanel with some helper methods for the care and feeding of AttributeFields.
 * <p>
 * We may be able to "retire" our AttributeFields and make use of PropertyEditors directly. If so
 * the add methods defined here should still work.
 * </p>
 * This implementation maintains a list of AttributeField and provides an implementation of refresh
 * that will.
 * 
 * @author Jody
 * @since 1.2.0
 */
public abstract class FeaturePanel extends IFeaturePanel implements IPropertyChangeListener {
    
    protected List<AttributeField> fields = new ArrayList<AttributeField>();
    protected Composite parent;
    private AttributeField invalidField;
    private boolean isValid;

    /**
     * Subclasses should call adjustGridLayout after they have populated parent with their fields.
     */
    public void createPartControl( Composite parent ) {
        this.parent = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        this.parent.setLayout(layout);
        this.parent.setFont(parent.getFont());

        createFieldEditors();

        adjustGridLayout(this.parent);
    }

    protected abstract void createFieldEditors();

    public Composite getParent() {
        return parent;
    }
    
    protected void checkState() {
        boolean valid = true;
        invalidField = null;
        // The state can only be set to true if all
        // field editors contain a valid value. So we must check them all
        for( AttributeField field : fields ) {
            valid = valid && field.isValid();
            if (!valid) {
                invalidField = field;
                break;
            }
        }
        setValid(valid);
    }

    public void setValid( boolean b ) {
        boolean oldValue = isValid;
        isValid = b;
        if (oldValue != isValid) {
            // update feature site
            if (getSite() != null) {
                // getSite().updateStatus();
            }
            // update page state
            // updateApplyButton();
        }
    }

    /**
     * Calculates the number of columns needed to host all field editors.
     * 
     * @return the number of columns
     */
    private int calcNumberOfColumns() {
        int result = 0;
        if (fields != null) {
            for( AttributeField field : fields ) {
                result = Math.max(result, field.getNumberOfControls());
            }
        }
        return result;
    }

    /**
     * Adjust the layout of the field editors so that they are properly aligned.
     */
    protected void adjustGridLayout( Composite parent ) {
        int numColumns = calcNumberOfColumns();
        if (parent.getLayout() instanceof GridLayout) {
            ((GridLayout) parent.getLayout()).numColumns = numColumns;
        } else {
            GridLayout layout = new GridLayout();
            layout.numColumns = numColumns;
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            parent.setLayout(layout);
        }

        if (fields != null) {
            for( AttributeField field : fields ) {
                field.adjustForNumColumns(numColumns);
            }
        }
    }

    /**
     * Remember the provided field; so it can be used with refresh/dispose/etc.
     * <p>
     * Subclasses may wish to override (and call super!) in order to process fields added to the
     * panel in one spot. As example this could be used to check security and set some of the fields
     * to read-only.
     * 
     * @param <F>
     * @param field
     * @return
     */
    protected <F extends AttributeField> F addField( F field ) {
        if (field == null) {
            throw new NullPointerException("AttributeField exepcted");
        }
        fields.add(field);
        return field;
    }

    @Override
    public void aboutToBeShown() {
        for( AttributeField field : fields ) {
            field.setPropertyChangeListener(this);
            field.setFeature(getSite().getEditFeature());
            field.doLoad();
        }
    }
    
    @Override
    public void refresh() {
        for( AttributeField field : fields ) {
            field.setFeature(getSite().getEditFeature());
            field.doLoad();
        }
    }

    @Override
    public void aboutToBeHidden() {
        for( AttributeField field : fields ) {
            field.setPropertyChangeListener(null);
            // field.setFeature(null);
        }
    }

    @Override
    public void dispose() {
        for( AttributeField field : fields ) {
            field.dispose();
            field.setPropertyChangeListener(null);
            field.setFeature(null);
        }
        super.dispose();
    }

    public void propertyChange( PropertyChangeEvent event ) {
        AttributeField field = (AttributeField) event.getSource();
        if (event.getProperty().equals(AttributeField.IS_VALID)) {
            boolean isValid = ((Boolean) event.getNewValue()).booleanValue();
            // If the new value is true then we must check all field editors.
            // If it is false, then the page is invalid in any case.
            if (isValid) {
                Label label = field.getLabelControl();
                Color foreground = label.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
                label.setForeground(foreground);
                label.setToolTipText(null);
                
                checkState();
            } else {
                Label label = field.getLabelControl();
                Color blue = label.getDisplay().getSystemColor(SWT.COLOR_BLUE);
                label.setForeground(blue);
                label.setToolTipText(field.getAttributeName() + " invalid!");
            
                invalidField = field;
                setValid(isValid);
            }
        }
        if (event.getProperty().equals(AttributeField.VALUE)) {
            if (field.isValid()) {
                field.store();
            }        
        }
    }
    
}
