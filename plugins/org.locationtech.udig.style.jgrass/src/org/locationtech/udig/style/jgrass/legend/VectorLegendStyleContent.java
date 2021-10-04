/**
 * JGrass - Free Open Source Java GIS http://www.jgrass.org
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.jgrass.legend;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.StyleContent;

public class VectorLegendStyleContent extends StyleContent {

    public static final String ID = "eu.hydrologis.jgrass.rasterlegend.vectorlegendStyle"; //$NON-NLS-1$

    private Object stylevalue;

    public VectorLegendStyleContent() {
        super(ID);
    }

    @Override
    public Class getStyleClass() {
        return VectorLegendStyle.class;
    }

    @Override
    public void save(IMemento momento, Object value) {
        this.stylevalue = value;
    }

    @Override
    public Object load(IMemento momento) {
        return stylevalue;
    }

    @Override
    public Object load(URL url, IProgressMonitor monitor) throws IOException {
        return null;
    }

    @Override
    public Object createDefaultStyle(IGeoResource resource, Color colour, IProgressMonitor monitor)
            throws IOException {
        if (!resource.canResolve(VectorLegendGraphic.class))
            return null;
        return createDefault();
    }

    public static VectorLegendStyle createDefault() {
        VectorLegendStyle style = new VectorLegendStyle();

        style.legendWidth = 80;
        style.legendHeight = 200;
        style.boxWidth = 15;
        style.xPos = 15;
        style.yPos = 15;
        style.isRoundedRectangle = false;
        style.bAlpha = 230;
        style.fAlpha = 255;
        style.titleString = ""; //$NON-NLS-1$

        style.fontColor = new Color(0, 0, 0);
        style.foregroundColor = new Color(255, 255, 255, style.fAlpha);
        style.backgroundColor = new Color(255, 255, 255, style.bAlpha);

        return style;
    }
}
