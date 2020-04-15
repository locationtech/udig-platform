/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.tileset;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;

/**
 * A property page for TileSet definition.
 * <p>
 * This page makes use of {@link PreferenceConstants#P_TILESET_ON_OFF} to enable
 * the generation (or overide) of a TileSet for use with a tiled map renderer.
 * 
 * @author jhudson
 * @since 1.3.0
 */
public class TileSetPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

    private TileSetControl tileControlPage;

    @Override
    protected Control createContents( Composite parent ) {
        final Layer layer = (Layer) getElement();
        IGeoResource resource = layer.getGeoResource();
        IGeoResourceInfo info = null;
        try {
            info = resource.getInfo(null);
        } catch (IOException e) {
            ProjectUIPlugin.trace( TileSetPropertyPage.class, "Unable to obtain info:"+e, e );
        }
        if (info != null) {
            this.tileControlPage = new TileSetControl(resource);
        }
        tileControlPage.createControl(parent);
        return tileControlPage.getControl();
    }

    @Override
    protected void performApply() {
        tileControlPage.performOk();
    }

    @Override
    public boolean performCancel() {
        performDefaults();
        return super.performCancel();
    }
    
    @Override
    public boolean performOk() {
        performApply();
        return super.performOk();
    }
    
    @Override
    protected void performDefaults() {
        tileControlPage.loadDefaults();
    }
}
