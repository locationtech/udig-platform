/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.processingregion;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

@SuppressWarnings("nls")
public class ProcessingRegionStyleContent extends StyleContent {

    public static final String ID = "org.locationtech.udig.omsbox.processingregion.ProcessingRegionStyleContent"; //$NON-NLS-1$

    public static final String N = "N";
    public static final String S = "S";
    public static final String E = "E";
    public static final String W = "W";
    public static final String ROWS = "ROWS";
    public static final String COLS = "COLS";
    public static final String CRS = "CRS";
    public static final String BALPHA = "BALPHA";
    public static final String FALPHA = "FALPHA";

    public ProcessingRegionStyleContent() {
        super(ID);
    }

    public Class< ? > getStyleClass() {
        return ProcessingRegionStyle.class;
    }

    @Override
    public void save( IMemento memento, Object value ) {
        ProcessingRegionStyle style = (ProcessingRegionStyle) value;

        memento.putString(N, String.valueOf(style.north));
        memento.putString(S, String.valueOf(style.south));
        memento.putString(E, String.valueOf(style.east));
        memento.putString(W, String.valueOf(style.west));
        memento.putInteger(ROWS, style.rows);
        memento.putInteger(COLS, style.cols);
        memento.putFloat(BALPHA, style.bAlpha);
        memento.putFloat(FALPHA, style.fAlpha);

        // TODO save colors
    }

    @Override
    public Object load( IMemento memento ) {
        ProcessingRegionStyle style = createDefault();

        try {
            style.north = Double.parseDouble(memento.getString(N));
            style.south = Double.parseDouble(memento.getString(S));
            style.east = Double.parseDouble(memento.getString(E));
            style.west = Double.parseDouble(memento.getString(W));
            style.rows = memento.getInteger(ROWS);
            style.cols = memento.getInteger(COLS);
            style.bAlpha = memento.getFloat(BALPHA);
            style.fAlpha = memento.getFloat(FALPHA);
        } catch (Exception e) {
            style = createDefault();
        }

        return style;
    }

    @Override
    public Object load( URL url, IProgressMonitor monitor ) throws IOException {
        return null;
    }

    @Override
    public Object createDefaultStyle( IGeoResource resource, Color colour, IProgressMonitor monitor ) throws IOException {
        if (!resource.canResolve(ProcessingRegionMapGraphic.class))
            return null;
        return createDefault();
    }

    public static ProcessingRegionStyle createDefault() {
        ProcessingRegionStyle style = new ProcessingRegionStyle();

        style.north = 100d;
        style.south = 0d;
        style.east = 100d;
        style.west = 0d;
        style.rows = 10;
        style.cols = 10;
        style.bAlpha = 0.3f;
        style.backgroundColor = new Color(22 / 255f, 163 / 255f, 221 / 255f, style.bAlpha);
        style.fAlpha = 0.5f;
        style.foregroundColor = new Color(24 / 255f, 124 / 255f, 165 / 255f, style.fAlpha);

        return style;
    }
}
