/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.ui.UDIGEditorInput;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Provides access for editing maps in uDig
 * 
 * @author Richard Gould
 * @since 0.3
 */
public class MapEditorInput extends UDIGEditorInput {

	public MapEditorInput(){
			
	}

	public MapEditorInput(IMap map){
		setProjectElement(map);
	}

	
    /** MUST BE LAT LONG */
    public Envelope getExtent() {
        IViewportModel model = getProjectElement().getViewportModel();
        Envelope bounds = model.getBounds();
        CoordinateReferenceSystem crs = model.getCRS();
        ReferencedEnvelope extent = new ReferencedEnvelope(bounds, crs);
        try {
            return extent.transform(DefaultGeographicCRS.WGS84, true);
        } catch (Exception e) {
            return new Envelope(-180, 180, -90, 90);
        }
    }

    public IMap getProjectElement() {
        return (IMap) super.getProjectElement();
    }
    /**
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists() {
        return true;
    }

    /**
     * TODO summary sentence for getImageDescriptor ...
     * 
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     * @return ImageDescriptor
     */
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /**
     * TODO summary sentence for getPersistable ...
     * 
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     * @return null
     */
    public IPersistableElement getPersistable() {
        return null;
    }

    /**
     * TODO summary sentence for getToolTipText ...
     * 
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     * @return getName
     */
    public String getToolTipText() {
        return getName();
    }

    /**
     * TODO summary sentence for getAdapter ...
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     * @param adapter
     * @return null
     */
    public Object getAdapter( Class adapter ) {
        return null;
    }

    /**
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName() {
        return Messages.MapEditorInput_name; 
    }

}
