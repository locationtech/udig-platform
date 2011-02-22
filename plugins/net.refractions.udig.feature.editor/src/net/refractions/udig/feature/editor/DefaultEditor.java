/**
 *
 */
package net.refractions.udig.feature.editor;


import net.refractions.udig.feature.editor.internal.Messages;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IUDIGView;
import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.geotools.feature.Feature;

/**
 * @author Jesse
 *
 */
public class DefaultEditor extends ViewPart implements IUDIGView{

	/**
	 *
	 */
	public DefaultEditor() {
		super();
	}

	private IToolContext context;
	private PropertySheetPage featureDisplay;
	private Feature current;
	/**
	 * @see net.refractions.udig.project.ui.IUDIGView#setContext()
	 */
	public void setContext(IToolContext context) {
		this.context = context;
	}

	/**
	 * @see net.refractions.udig.project.ui.IUDIGView#getContext()
	 */
	public IToolContext getContext() {
		return context;
	}

	public void editFeatureChanged(Feature feature) {
        current = feature;
        StructuredSelection selection;
        Object value = defaultSource;
        if( current!=null )
        	value=current;
        else
        	value=defaultSource;
    	selection=new StructuredSelection(value);
        featureDisplay.selectionChanged(null, selection);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
        featureDisplay = new PropertySheetPage();
        featureDisplay.createControl(parent);
        final IMap map=ApplicationGIS.getActiveMap();
        if( map!=ApplicationGIS.NO_MAP){
            try{
                editFeatureChanged(map.getEditManager().getEditFeature());
            }catch (Throwable e) {
                UiPlugin.log("Default Feature Editor threw an exception", e); //$NON-NLS-1$
            }

        }
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		featureDisplay.setFocus();
		if( current==null )
			featureDisplay.selectionChanged(null, new StructuredSelection(defaultSource));
	}

	IAdaptable defaultSource=new IAdaptable (){

		public Object getAdapter(Class adapter) {
			if( IPropertySource.class.isAssignableFrom(adapter) )
				return new IPropertySource() {

					public void setPropertyValue(Object id, Object value) {
						// TODO Auto-generated method stub

					}

					public void resetPropertyValue(Object id) {
						// TODO Auto-generated method stub

					}

					public boolean isPropertySet(Object id) {
						// TODO Auto-generated method stub
						return false;
					}

					public Object getPropertyValue(Object id) {
						return ""; //$NON-NLS-1$
					}

					public IPropertyDescriptor[] getPropertyDescriptors() {
						return new PropertyDescriptor[]{new PropertyDescriptor("ID",Messages.DefaultEditor_1)}; //$NON-NLS-1$
					}

					public Object getEditableValue() {
						return null;
					}

				};
			return null;
		}

	};
}
