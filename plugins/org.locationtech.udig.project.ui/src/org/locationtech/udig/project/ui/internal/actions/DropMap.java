/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.actions;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Control;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.LayersView;
import org.locationtech.udig.project.ui.internal.MapEditorPart;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.ui.IDropAction;

/**
 * Opens a map when dropped on an IEditorPart
 * 
 * @author jones
 * @since 1.1.0
 */
public class DropMap extends IDropAction {

    @Override
    public boolean accept( ) {

        if (getDestination() instanceof ILayer) {
        	if(getEvent()==null){
        		return false;
        	}
            Control control = ((DropTarget)getEvent().widget).getControl();
            if ( control != LayersView.getViewer().getControl() && !(control instanceof ViewportPane))
                return false;
        }
        
        if (getData() instanceof IProjectElement) {
            return true;
        }
       
        // we want to open the project element if one of it children is dragged
        if (getData() instanceof EObject && !(getDestination() instanceof ILayer)) {
            EObject eobj = (EObject) getData();
            while (eobj!=null && !(eobj instanceof IProjectElement) ){
                eobj=eobj.eContainer();
            }
            
            if (eobj == null)
                return false;
            
            // if layer dropped in layers view when map is open then we want use another action
            if( getData() instanceof ILayer && getDestination() instanceof LayersView && ApplicationGIS.getActiveMap()!=ApplicationGIS.NO_MAP ){
                return false;
            }
            // layer droppe in map if special too
            if( getData() instanceof ILayer && getDestination() instanceof MapEditorPart){
                return false;
            }
            
            return true;
        }
        URL url = null;
        if (getData() instanceof String) {
            String string = (String) getData();
            try {
                string = URLDecoder.decode(string, "UTF-8");
            } catch (UnsupportedEncodingException e2) {
                // so ignore...
            }
            try {
                url = new URL(string);
            } catch (MalformedURLException e) {
                // guess it is not a URL
            }
            if( url == null ){
                File file=new File(string);
                if( !file.exists() ){
                    return false;
                }
            }
        }

        if (getData() instanceof URL) {
            url = (URL) getData();
        }

        if (url != null) {
            String fileString = url.getFile();
            try {
                File file = new File(fileString);
                if (file.exists() && fileString.endsWith(".umap")) //$NON-NLS-1$
                    return true;

                return false;
            } catch (Exception e) {
                // ok not a file either
                return false;
            }
        }
        return false;
    }

    @Override
    public void perform( IProgressMonitor monitor ) {
        if (getData() instanceof EObject && !(getDestination() instanceof ILayer)) {
            EObject eobj = (EObject) getData();
            while (eobj!=null && !(eobj instanceof IProjectElement) ){
                eobj=eobj.eContainer();
            }
            ApplicationGIS.openProjectElement((IProjectElement) eobj,false);
        }
        URL url = null;
        if (getData() instanceof IProjectElement) {
            ApplicationGIS.openProjectElement((IProjectElement) getData(),false);
        } else if (getData() instanceof String) {
            String string = (String) getData();
            try {
                string = URLDecoder.decode(string, "UTF-8");
            } catch (UnsupportedEncodingException e2) {
                // so ignore...
            }
            try {
                url = new URL(string);
            } catch (MalformedURLException e) {
                // guess it is not a URL
                File file=new File(string);
                if( file.exists() )
                    try {
                        url=file.toURL();
                    } catch (MalformedURLException e1) {
                        // oh well
                    }
            }

        } else if (getData() instanceof URL) {
            url = (URL) getData();
        }

        if (url != null) {
            String fileString;
            try {
                fileString = url.getFile();
                File file = new File(fileString);
                if (!file.exists() || !fileString.endsWith(".umap")) { //$NON-NLS-1$
                    ProjectUIPlugin.log("Some how accept() accepted: " + getData(), new Exception()); //$NON-NLS-1$
                    return;
                }
            } catch (Exception e) {
                // ok not a file either
                ProjectUIPlugin.log("Some how accept() accepted: " + getData(), new Exception()); //$NON-NLS-1$
                return;
            }

            IProjectElement elem;
            try {
                elem = ApplicationGIS.loadProjectElement(url, ApplicationGIS.getActiveProject());
                if ( elem instanceof IProjectElement )
                    ApplicationGIS.openProjectElement((IProjectElement) elem,false);
            } catch (IllegalArgumentException e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            } catch (IOException e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

}
