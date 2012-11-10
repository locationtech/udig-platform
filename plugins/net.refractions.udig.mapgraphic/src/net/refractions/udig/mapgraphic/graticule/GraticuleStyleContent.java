/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.mapgraphic.graticule;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

public class GraticuleStyleContent extends StyleContent {

    private static final String LINE_STYLE_ID = "LINE_STYLE_ID"; //$NON-NLS-1$

    private static final String LINE_WIDTH_ID = "LINE_WIDTH_ID"; //$NON-NLS-1$

    private static final String LINE_RED_ID = "LINE_RED_ID"; //$NON-NLS-1$

    private static final String LINE_GREEN_ID = "LINE_GREEN_ID"; //$NON-NLS-1$

    private static final String LINE_BLUE_ID = "LINE_BLUE_ID"; //$NON-NLS-1$

    private static final String LINE_ALPHA_ID = "LINE_ALPHA_ID"; //$NON-NLS-1$

    private static final String FONT_RED_ID = "FONT_RED_ID"; //$NON-NLS-1$

    private static final String FONT_GREEN_ID = "FONT_GREEN_ID"; //$NON-NLS-1$

    private static final String FONT_BLUE_ID = "FONT_BLUE_ID"; //$NON-NLS-1$

    private static final String FONT_ALPHA_ID = "FONT_ALPHA_ID"; //$NON-NLS-1$

    private static final String SHOW_LABELS_ID = "SHOW_LABELS_ID"; //$NON-NLS-1$

    private static final String INIT_CRS_ID = "INIT_CRS_ID"; //$NON-NLS-1$

    private static final String OPACITY_ID = "OPACITY_ID"; //$NON-NLS-1$    

    private static final String CRS_ID = "CRS_ID"; //$NON-NLS-1$    

    public GraticuleStyleContent() {
        super(GraticuleStyle.ID);
    }

    @Override
    public Object createDefaultStyle(IGeoResource resource, Color colour, IProgressMonitor monitor)
            throws IOException {
        if (resource.canResolve(GraticuleGraphic.class))
            return GraticuleStyle.DEFAULT;

        return null;
    }

    @Override
    public Class<?> getStyleClass() {
        return GraticuleStyle.class;
    }

    @Override
    public Object load(IMemento memento) {
        int lineStyle = memento.getInteger(LINE_STYLE_ID);
        int lineWidth = memento.getInteger(LINE_WIDTH_ID);
        Color lineColor = new Color(memento.getInteger(LINE_RED_ID),
                memento.getInteger(LINE_GREEN_ID), memento.getInteger(LINE_BLUE_ID),
                memento.getInteger(LINE_ALPHA_ID));
        Color fontColor = new Color(memento.getInteger(FONT_RED_ID),
                memento.getInteger(FONT_GREEN_ID), memento.getInteger(FONT_BLUE_ID),
                memento.getInteger(FONT_ALPHA_ID));
        Boolean isShowLabels = memento.getBoolean(SHOW_LABELS_ID);
        Boolean isInitCRS = memento.getBoolean(INIT_CRS_ID);
        int opacity = memento.getInteger(OPACITY_ID);
        String crs = memento.getString(CRS_ID);

        return new GraticuleStyle(fontColor, lineColor, opacity, lineStyle, lineWidth,
                isShowLabels, isInitCRS, crs);
    }

    @Override
    public Object load(URL url, IProgressMonitor monitor) throws IOException {
        return null;
    }

    @Override
    public void save(IMemento memento, Object value) {
        if (value instanceof GraticuleStyle) {
            GraticuleStyle style = (GraticuleStyle) value;

            memento.putInteger(LINE_STYLE_ID, style.getLineStyle());
            memento.putInteger(LINE_WIDTH_ID, style.getLineWidth());
            memento.putInteger(LINE_RED_ID, style.getLineColor().getRed());
            memento.putInteger(LINE_GREEN_ID, style.getLineColor().getGreen());
            memento.putInteger(LINE_BLUE_ID, style.getLineColor().getBlue());
            memento.putInteger(LINE_ALPHA_ID, style.getLineColor().getAlpha());
            memento.putInteger(FONT_RED_ID, style.getFontColor().getRed());
            memento.putInteger(FONT_GREEN_ID, style.getFontColor().getGreen());
            memento.putInteger(FONT_BLUE_ID, style.getFontColor().getBlue());
            memento.putInteger(FONT_ALPHA_ID, style.getFontColor().getAlpha());
            memento.putInteger(OPACITY_ID, style.getOpacity());
            memento.putBoolean(SHOW_LABELS_ID, style.isShowLabels());
            memento.putBoolean(INIT_CRS_ID, style.isInitCRS());
            memento.putString(CRS_ID, style.getCRS());
        }
    }
}