/**
 *
 */
package org.locationtech.udig.feature.editor;

import org.locationtech.udig.feature.editor.internal.Messages;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.IUDIGView;
import org.locationtech.udig.project.ui.tool.IToolContext;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.opengis.feature.simple.SimpleFeature;

/**
 * This is the default FeatureEditor; making use of a PropertySheet page to show the attributes of a
 * feature and allow them to be edited.
 *
 * @author Jesse
 */
public class DefaultEditor extends ViewPart implements IUDIGView {

    public DefaultEditor() {
        super();
    }

    private IToolContext context;
    private PropertySheetPage featureDisplay;
    private SimpleFeature current;
    /**
     * @see org.locationtech.udig.project.ui.IUDIGView#setContext()
     */
    public void setContext( IToolContext context ) {
        this.context = context;
    }

    /**
     * @see org.locationtech.udig.project.ui.IUDIGView#getContext()
     */
    public IToolContext getContext() {
        return context;
    }

    public void editFeatureChanged( SimpleFeature feature ) {
        current = feature;
        StructuredSelection selection;
        Object value = defaultSource;
        if (current != null)
            value = current;
        else
            value = defaultSource;
        selection = new StructuredSelection(value);
        featureDisplay.selectionChanged(null, selection);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent ) {
        featureDisplay = new PropertySheetPage();
        featureDisplay.createControl(parent);
        final IMap map = ApplicationGIS.getActiveMap();
        if (map != ApplicationGIS.NO_MAP) {
            try {
                editFeatureChanged(map.getEditManager().getEditFeature());
            } catch (Throwable e) {
                UiPlugin.log("Default SimpleFeature Editor threw an exception", e); //$NON-NLS-1$
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus() {
        featureDisplay.setFocus();
        if (current == null)
            featureDisplay.selectionChanged(null, new StructuredSelection(defaultSource));
    }

    IAdaptable defaultSource = new IAdaptable(){

        public Object getAdapter( Class adapter ) {
            if (IPropertySource.class.isAssignableFrom(adapter))
                return new IPropertySource(){

                    public void setPropertyValue( Object id, Object value ) {

                    }

                    public void resetPropertyValue( Object id ) {

                    }

                    public boolean isPropertySet( Object id ) {
                        return false;
                    }

                    public Object getPropertyValue( Object id ) {
                        return ""; //$NON-NLS-1$
                    }

                    public IPropertyDescriptor[] getPropertyDescriptors() {
                        return new PropertyDescriptor[]{new PropertyDescriptor(
                                "ID", Messages.DefaultEditor_1)}; //$NON-NLS-1$
                    }

                    public Object getEditableValue() {
                        return null;
                    }

                };
            return null;
        }

    };
}
