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
package net.refractions.udig.project.ui.tileset;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.preferences.PreferenceConstants;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

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
