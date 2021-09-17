/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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

	public void activate(EditToolHandler handler) {
		this.handler = handler;
		handler.getContext().getEditManager().addListener(this);
	}

	public void deactivate(EditToolHandler handler) {
		handler.getContext().getEditManager().removeListener(this);

	}

	public void handleActivateError(EditToolHandler handler, Throwable error) {
		EditPlugin.log("Activate Error", error); //$NON-NLS-1$
	}

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
