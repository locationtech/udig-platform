/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.ui.UDIGEditorInput;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

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
