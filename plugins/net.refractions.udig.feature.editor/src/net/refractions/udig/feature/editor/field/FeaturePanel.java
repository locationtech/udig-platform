package net.refractions.udig.feature.editor.field;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ui.IFeaturePanel;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Label;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Abstract FetaurePanel with some helper methods
 * for the care and feeding of AttributeFields.
 * <p>
 * We may be able to "retire" our AttributeFields and make
 * use of PropertyEditors directly. If so the add methods
 * defined here should still work.
 * </p>
 * This implementation maintains a list of AttributeField and
 * provides an implementation of refresh that will.
 * @author Jody
 * @since 1.2.0
 */
public abstract class FeaturePanel extends IFeaturePanel {
    protected List<AttributeField> fields = new ArrayList<AttributeField>();

    private IPropertyChangeListener listener = new IPropertyChangeListener(){        
        public void propertyChange( PropertyChangeEvent event ) {
            AttributeField field = (AttributeField) event.getSource();
            if( field.isValid()){
                field.store();
                
                Label label = field.getLabelControl();
                Color foreground = label.getDisplay().getSystemColor( SWT.COLOR_INFO_FOREGROUND );
                label.setForeground(foreground);
                SimpleFeatureType featureType = field.getFeature().getFeatureType();
                label.setToolTipText( null );
            }
            else {
                Label label = field.getLabelControl();
                Color blue = label.getDisplay().getSystemColor( SWT.COLOR_BLUE );
                label.setForeground(blue);
                label.setToolTipText( field.getAttributeName() + " invalid!" );                
            }
        }
    };
    
    /**
     * Remember the provided field; so it can be used with refresh/dispose/etc.
     * <p>
     * Subclasses may wish to override (and call super!) in order to process
     * fields added to the panel in one spot. As example this could be used
     * to check security  and set some of the fields to read-only.
     * 
     * @param <F>
     * @param field
     * @return
     */
    protected <F extends AttributeField> F addField( F field ){
        if( field == null ) {
            throw new NullPointerException("AttributeField exepcted");
        }
        fields.add( field );
        return field;
    }
    
    @Override
    public void aboutToBeShown() {
        for( AttributeField field : fields ){
            field.setPropertyChangeListener(listener);
            field.setFeature(getSite().getEditFeature());
        }
    }

    @Override
    public void aboutToBeHidden() {
        for( AttributeField field : fields ){
            field.setPropertyChangeListener(null);
            // field.setFeature(null);
        }
    }
    
    @Override
    public void dispose() {
        for( AttributeField field : fields ){
            field.dispose();
            field.setPropertyChangeListener(null);
            field.setFeature(null);
        }
        super.dispose();        
    }
    
}
