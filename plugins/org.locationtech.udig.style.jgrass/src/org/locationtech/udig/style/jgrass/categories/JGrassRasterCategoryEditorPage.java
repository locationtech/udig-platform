/**
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.jgrass.categories;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.gce.grassraster.JGrassConstants;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapGeoResource;
import org.locationtech.udig.catalog.jgrass.utils.JGrassCatalogUtilities;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.style.jgrass.JGrassrasterStyleActivator;
import org.locationtech.udig.style.sld.editor.StyleEditorPage;

public class JGrassRasterCategoryEditorPage extends StyleEditorPage {

    public static String JGRASSRASTERSTYLEID = "org.locationtech.udig.style.jgrass.cats"; //$NON-NLS-1$

    private CategoryEditor categoryRulesEditor = null;

    private boolean editorSupported = false;

    private String type = "unknown"; //$NON-NLS-1$

    public JGrassRasterCategoryEditorPage() {
        super();
        setSize(new Point(500, 450));
    }

    @Override
    public void createPageContent(Composite parent) {

        Layer layer = getSelectedLayer();
        IGeoResource resource = layer.getGeoResource();

        if (resource.canResolve(JGrassMapGeoResource.class)) {
            try {
                JGrassMapGeoResource grassMapGeoResource = resource
                        .resolve(JGrassMapGeoResource.class, null);
                if (grassMapGeoResource.getType().equals(JGrassConstants.GRASSBINARYRASTERMAP)) {
                    editorSupported = true;
                } else {
                    editorSupported = false;
                }
                type = grassMapGeoResource.getType();

            } catch (IOException e) {
                JGrassrasterStyleActivator.log("JGrassrasterStyleActivator problem", e); //$NON-NLS-1$
                e.printStackTrace();
            }
        } else {
            editorSupported = false;
        }

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginWidth = 0;
        parent.setLayout(gridLayout);

        if (editorSupported) {
            categoryRulesEditor = new CategoryEditor(parent, SWT.NONE);

            String[] mapsetPathAndMapName = JGrassCatalogUtilities
                    .getMapsetpathAndMapnameFromJGrassMapGeoResource(resource);

            categoryRulesEditor.setLayer(layer);
            categoryRulesEditor.makeSomeCategories(mapsetPathAndMapName[0] + File.separator
                    + JGrassConstants.CATS + File.separator + mapsetPathAndMapName[1]);

        } else {
            Label problemLabel = new Label(parent, SWT.NONE);
            problemLabel.setText("No support for map styling of map type: \"" + type + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }

    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public void gotFocus() {

    }

    @Override
    public boolean performCancel() {
        return false;
    }

    @Override
    public boolean okToLeave() {
        return true;
    }

    @Override
    public boolean performApply() {
        if (editorSupported) {
            categoryRulesEditor.makePersistent();
        }
        return true;
    }

    @Override
    public boolean performOk() {
        if (editorSupported) {
            categoryRulesEditor.makePersistent();
        }
        return false;
    }

    @Override
    public void refresh() {

    }

    @Override
    public void dispose() {
        if (editorSupported) {
            categoryRulesEditor = null;
        }
        super.dispose();
    }

    @Override
    public void styleChanged(Object source) {

    }

}
