/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.util;

import java.net.URI;
import java.util.Set;

import javax.swing.Icon;

import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;

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
