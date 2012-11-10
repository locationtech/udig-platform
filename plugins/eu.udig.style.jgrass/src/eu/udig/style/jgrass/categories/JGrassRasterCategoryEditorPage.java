/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.style.jgrass.categories;

import java.io.File;
import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.sld.editor.StyleEditorPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.gce.grassraster.JGrassConstants;

import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;
import eu.udig.style.jgrass.JGrassrasterStyleActivator;

public class JGrassRasterCategoryEditorPage extends StyleEditorPage {

    public static String JGRASSRASTERSTYLEID = "eu.udig.style.jgrass.cats";
    private CategoryEditor categoryRulesEditor = null;
    private boolean editorSupported = false;
    private String type = "unknown";

    public JGrassRasterCategoryEditorPage() {
        super();
        setSize(new Point(500, 450));
    }

    public void createPageContent( Composite parent ) {

        Layer layer = getSelectedLayer();
        IGeoResource resource = layer.getGeoResource();

        if (resource.canResolve(JGrassMapGeoResource.class)) {
            try {
                JGrassMapGeoResource grassMapGeoResource = resource.resolve(
                        JGrassMapGeoResource.class, null);
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
            problemLabel.setText("No support for map styling of map type: \"" + type + "\"");
        }

    }

    public String getErrorMessage() {
        return null;
    }

    public String getLabel() {
        return null;
    }

    public void gotFocus() {
    }

    public boolean performCancel() {
        return false;
    }

    public boolean okToLeave() {
        return true;
    }

    public boolean performApply() {
        if (editorSupported) {
            categoryRulesEditor.makePersistent();
        }
        return true;
    }

    public boolean performOk() {
        if (editorSupported) {
            categoryRulesEditor.makePersistent();
        }
        return false;
    }

    public void refresh() {
    }

    public void dispose() {
        if (editorSupported) {
            categoryRulesEditor = null;
        }
        super.dispose();
    }

    public void styleChanged( Object source ) {
    }

}
