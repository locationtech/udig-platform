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

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.tests.ui.CatalogTestsUIPlugin;
import org.locationtech.udig.project.ui.internal.LayersView;
import org.locationtech.udig.ui.ViewerDropLocation;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

public class WizardDropActionTest extends AbstractProjectUITestCase{

    /*
     * Test method for 'org.locationtech.udig.project.ui.internal.actions.WizardDropAction.accept(Object, Object)'
     */
    @Test
    public void testHttpAccept() {
        MapDropAction action=new MapDropAction();
        Object layersView=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(LayersView.ID);
        action.init(null, null, ViewerDropLocation.NONE, layersView, "http://localhost:8080/geoserver/wfs"); //$NON-NLS-1$
        boolean acceptable = action.accept( );
        assertTrue(acceptable);
    }

    /*
     * Test method for 'org.locationtech.udig.project.ui.internal.actions.WizardDropAction.accept(Object, Object)'
     */
    @Test
    public void testFileStringAccept() throws Exception {
        MapDropAction action=new MapDropAction();
        Object layersView=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(LayersView.ID);
        action.init(null, null, ViewerDropLocation.NONE, layersView, getData().getFile()); 
        boolean acceptable = action.accept( );
        assertTrue(acceptable);
    }
    
    public URL getData() throws Exception {
        URL url = CatalogTestsUIPlugin.getDefault().getBundle()
            .getEntry("data/streams.shp");   //$NON-NLS-1$
        return FileLocator.toFileURL(url);
    }
    
    @Test
    public void testURLAccept() throws Exception{
        MapDropAction action=new MapDropAction();
        Object layersView=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(LayersView.ID);
        action.init(null, null, ViewerDropLocation.NONE, layersView, getData());
        boolean acceptable = action.accept( );
        assertTrue(acceptable);
    }

    
}
