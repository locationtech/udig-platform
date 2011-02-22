/**
 *
 */
package net.refractions.udig.tools.edit;

import java.util.Map;

import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;

import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Activator that listens to the EditManager's selected feature.
 *
 * @author Jesse
 */
public class EditManagerListenerActivator implements Activator, IEditManagerListener {

	private EditToolHandler handler;

	/* (non-Javadoc)
	 * @see net.refractions.udig.tools.edit.Activator#activate(net.refractions.udig.tools.edit.EditToolHandler)
	 */
	public void activate(EditToolHandler handler) {
		this.handler = handler;
		handler.getContext().getEditManager().addListener(this);
	}

	/* (non-Javadoc)
	 * @see net.refractions.udig.tools.edit.Activator#deactivate(net.refractions.udig.tools.edit.EditToolHandler)
	 */
	public void deactivate(EditToolHandler handler) {
		handler.getContext().getEditManager().removeListener(this);

	}

	/* (non-Javadoc)
	 * @see net.refractions.udig.tools.edit.Activator#handleActivateError(net.refractions.udig.tools.edit.EditToolHandler, java.lang.Throwable)
	 */
	public void handleActivateError(EditToolHandler handler, Throwable error) {
		EditPlugin.log("Activate Error", error); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see net.refractions.udig.tools.edit.Activator#handleDeactivateError(net.refractions.udig.tools.edit.EditToolHandler, java.lang.Throwable)
	 */
	public void handleDeactivateError(EditToolHandler handler, Throwable error) {
		EditPlugin.log("Deactivate Error", error); //$NON-NLS-1$
	}

	public void changed(EditManagerEvent event) {
		int type = event.getType();
		if(type == EditManagerEvent.EDIT_FEATURE){

			EditGeom currentGeom = handler.getCurrentGeom();
			Feature newValue = (Feature) event.getNewValue();
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
	private void setCurrentGeom(Feature newValue) {
		EditBlackboard bb = handler.getEditBlackboard(handler.getContext().getSelectedLayer());
		Map<Geometry, EditGeom> editGeoms = bb.setGeometries(newValue.getDefaultGeometry(), newValue.getID());
		if( editGeoms.isEmpty() ){
			return;
		}

		EditGeom firstShape = editGeoms.values().iterator().next();
		handler.setCurrentShape(firstShape.getShell());
	}

}
