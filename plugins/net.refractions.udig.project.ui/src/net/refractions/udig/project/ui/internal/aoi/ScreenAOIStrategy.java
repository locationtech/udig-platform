/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal.aoi;

import net.refractions.udig.aoi.AOIListener;
import net.refractions.udig.aoi.IAOIStrategy;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.render.IViewportModelListener;
import net.refractions.udig.project.render.ViewportModelEvent;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

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
