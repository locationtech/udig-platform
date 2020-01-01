/**
 * 
 */
package org.locationtech.udig.tools.edit;

import java.util.Map;

import org.locationtech.udig.project.EditManagerEvent;
import org.locationtech.udig.project.IEditManagerListener;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;

import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.jts.geom.Geometry;

/**
 * Activator that listens to the EditManager's selected feature. 
 * 
 * @author Jesse
 */
public class EditManagerListenerActivator implements Activator, IEditManagerListener {

	private EditToolHandler handler;

	/* (non-Javadoc)
	 * @see org.locationtech.udig.tools.edit.Activator#activate(org.locationtech.udig.tools.edit.EditToolHandler)
	 */
	public void activate(EditToolHandler handler) {
		this.handler = handler;
		handler.getContext().getEditManager().addListener(this);
	}

	/* (non-Javadoc)
	 * @see org.locationtech.udig.tools.edit.Activator#deactivate(org.locationtech.udig.tools.edit.EditToolHandler)
	 */
	public void deactivate(EditToolHandler handler) {
		handler.getContext().getEditManager().removeListener(this);

	}

	/* (non-Javadoc)
	 * @see org.locationtech.udig.tools.edit.Activator#handleActivateError(org.locationtech.udig.tools.edit.EditToolHandler, java.lang.Throwable)
	 */
	public void handleActivateError(EditToolHandler handler, Throwable error) {
		EditPlugin.log("Activate Error", error); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.locationtech.udig.tools.edit.Activator#handleDeactivateError(org.locationtech.udig.tools.edit.EditToolHandler, java.lang.Throwable)
	 */
	public void handleDeactivateError(EditToolHandler handler, Throwable error) {
		EditPlugin.log("Deactivate Error", error); //$NON-NLS-1$
	}

	public void changed(EditManagerEvent event) {
		int type = event.getType();
		if(type == EditManagerEvent.EDIT_FEATURE){
			
			EditGeom currentGeom = handler.getCurrentGeom();
			SimpleFeature newValue = (SimpleFeature) event.getNewValue();
			if( currentGeom==null && newValue!=null ){
				setCurrentGeom(newValue);
			} else if( currentGeom!=null && newValue==null ) {
				handler.setCurrentShape(null);
			} else {
				String fid = handler.getCurrentGeom().getFeatureIDRef().get();
				if( !fid.equals(newValue.getID()) ){
					setCurrentGeom(newValue);
				}
			}
		}
		
	}

	/**
	 * @param newValue
	 */
	private void setCurrentGeom(SimpleFeature newValue) {
		EditBlackboard bb = handler.getEditBlackboard(handler.getContext().getSelectedLayer());
		Map<Geometry, EditGeom> editGeoms = bb.setGeometries((Geometry) newValue.getDefaultGeometry(), newValue.getID());
		if( editGeoms.isEmpty() ){
			return;
		}
		
		EditGeom firstShape = editGeoms.values().iterator().next();
		handler.setCurrentShape(firstShape.getShell());
	}

}
