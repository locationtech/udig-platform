/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.aoi;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.udig.aoi.AOIListener;
import org.locationtech.udig.aoi.IAOIStrategy;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.render.IViewportModelListener;
import org.locationtech.udig.project.render.ViewportModelEvent;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Returns an empty ReferencedEnvelope so that zoom to extent goes to all
 * @author pfeiffp
 *
 */
public class ScreenAOIStrategy extends IAOIStrategy {

	private static String name = "Screen";
	
    protected IViewportModelListener watcher = new IViewportModelListener(){
        @Override
        public void changed( ViewportModelEvent viewportEvent ) {
            AOIListener.Event aoiEvent = new AOIListener.Event( ScreenAOIStrategy.this);            
            if( viewportEvent.getType() == ViewportModelEvent.EventType.BOUNDS ){                
                aoiEvent.bounds = (ReferencedEnvelope) viewportEvent.getNewValue();
            }
            else {
                aoiEvent.bounds = viewportEvent.getSource().getBounds();
            }
            notifyListeners( aoiEvent);
        }
    };
    
    
    
	public ScreenAOIStrategy() {
	    listenToViewport();
	}
	
    private void listenToViewport() {
        IViewportModel viewportModel = ApplicationGIS.getActiveMap().getViewportModel();
	    viewportModel.addViewportModelListener(watcher);
    }
	
	@Override
	public ReferencedEnvelope getExtent() {
		IMap currentMap = ApplicationGIS.getActiveMap();
		if (!currentMap.equals(ApplicationGIS.NO_MAP)) {
			return currentMap.getViewportModel().getBounds();
		}
		return null;
	}

	@Override
	public Geometry getGeometry() {
		ReferencedEnvelope extent = this.getExtent();
		if (extent != null) {
			return new GeometryFactory().toGeometry(extent);
		}
		return null;
	}
	
	@Override
	public CoordinateReferenceSystem getCrs() {
		IMap currentMap = ApplicationGIS.getActiveMap();
		if (!currentMap.equals(ApplicationGIS.NO_MAP)) {
			return currentMap.getViewportModel().getCRS();
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

}
