/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.actions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.styling.Style;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.style.sld.SLDContent;
import org.locationtech.udig.ui.IDropAction;
import org.locationtech.udig.ui.ViewerDropLocation;

public class SLDDropAction extends IDropAction {


    private Style style;

    @Override
    public boolean accept( ) {
        
        if( getViewerLocation()==ViewerDropLocation.NONE )
            return false;
        URL url=null;
        // make sure we can turn the object into an sld
        try {
            if (getData() instanceof URL) {
                url = (URL) getData();
            } else if (getData() instanceof File) {
                url = ((File) getData()).toURL();
            } else if (getData() instanceof String) {
                try {
                    url = new URL((String) getData());
                } catch (MalformedURLException e) {
                    // try attaching a file protocol
                    url = new URL("file:///" + (String) getData()); //$NON-NLS-1$
                }
                if (url != null) {
                    if( !url.getFile().toLowerCase().endsWith(".sld") || url.getQuery()!=null ) //$NON-NLS-1$
                        url=null;
                }

            }
        } catch (MalformedURLException e) {
            String msg = Messages.SLDDropAction_badSldUrl; 
            ProjectUIPlugin.log(msg, e);
        }

        if( url==null )
            return false;
        try {
            style = SLDContent.parse(url);
        } catch (Throwable e) {
            return false;
        }
        
        return style != null && (getDestination() instanceof Layer || getDestination() instanceof Map);
    }

    @Override
    public void perform( IProgressMonitor monitor ) {

        if ( !accept() )
            throw new IllegalStateException("Data is not acceptable for this action!  Programatic Error!!!"); //$NON-NLS-1$
        // grab the actual target
        Object target = getDestination();
        if ( target instanceof Layer) {
            dropOnLayer(monitor, (Layer) target);
        }else{
            if( target instanceof Map ){
                dropOnLayer(monitor, (Layer)((Map)target).getEditManagerInternal().getSelectedLayer());
            }
        }

    }

    private void dropOnLayer( IProgressMonitor monitor, Layer target ) {
        Layer layer = (Layer) target;
        // parse the sld object

        try {
            SLDContent.apply(layer, style, monitor);
            layer.refresh(null);

        } catch (IOException e) {
            String msg = Messages.SLDDropAction_sldParseError; 
            ProjectUIPlugin.log(msg, e);
        }
    }

}
