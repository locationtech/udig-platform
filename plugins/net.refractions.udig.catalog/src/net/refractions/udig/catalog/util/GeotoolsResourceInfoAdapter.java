/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.catalog.util;

import java.net.URI;
import java.util.Set;

import javax.swing.Icon;

import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.ui.graphics.AWTSWTImageUtils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.data.ResourceInfo;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Adapts a {@link ResourceInfo} to a {@link IGeoResourceInfo}
 * 
 * @author jesse
 * @since 1.1.0
 */
public class GeotoolsResourceInfoAdapter extends IGeoResourceInfo implements ResourceInfo {

    private final ResourceInfo toAdapt;

    /**
     * Create IGeoResourceInfo that adapts the ResourceInfo
     * 
     * @param toAdapt the info to adapt
     */
    public GeotoolsResourceInfoAdapter(ResourceInfo toAdapt){
        this.toAdapt = toAdapt;
    }
    
    @Override
    public ReferencedEnvelope getBounds() {
        return toAdapt.getBounds();
    }
    
    @Override
    public CoordinateReferenceSystem getCRS() {
        return toAdapt.getCRS();
    }
    
    @Override
    public String getDescription() {
        return toAdapt.getDescription();
    }
    @Override
    public Icon getIcon() {
        //return toAdapt.getIcon();
    	return null;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return AWTSWTImageUtils.awtIcon2ImageDescriptor(getIcon());
    }
    
    @Override
    public Set<String> getKeywords() {
        return toAdapt.getKeywords();
    }
    
    @Override
    public String getName() {
        return toAdapt.getName();
    }
    
    @Override
    public URI getSchema() {
        return toAdapt.getSchema();
    }
    
    @Override
    public String getTitle() {
        return toAdapt.getTitle();
    }
    
    public ResourceInfo getToAdapt() {
        return toAdapt;
    }
}
