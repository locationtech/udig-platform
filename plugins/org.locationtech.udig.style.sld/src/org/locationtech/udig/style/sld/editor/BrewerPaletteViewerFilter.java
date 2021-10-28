/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.brewer.color.PaletteSuitability;

final class BrewerPaletteViewerFilter extends ViewerFilter {

    private final StyleThemePage styleThemePage;

    /**
     * @param styleThemePage
     */
    BrewerPaletteViewerFilter(StyleThemePage styleThemePage) {
        this.styleThemePage = styleThemePage;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof BrewerPalette) {
            BrewerPalette pal = (BrewerPalette) element;
            int numClasses = Integer.valueOf(
                    this.styleThemePage.getCombo(StyleThemePage.COMBO_CLASSES).getText())
                            .intValue();
            int maxColors = pal.getMaxColors();
            if (maxColors != -1 && maxColors < numClasses) {
                return false;
            }
            if (this.styleThemePage.getButton(StyleThemePage.BUTTON_COLORBLIND).getSelection()) {
                if (pal.getPaletteSuitability().getSuitability(numClasses,
                        PaletteSuitability.VIEWER_COLORBLIND) != PaletteSuitability.QUALITY_GOOD)
                    return false;
            }
            if (this.styleThemePage.getButton(StyleThemePage.BUTTON_CRT).getSelection()) {
                if (pal.getPaletteSuitability().getSuitability(numClasses,
                        PaletteSuitability.VIEWER_CRT) != PaletteSuitability.QUALITY_GOOD)
                    return false;
            }
            if (this.styleThemePage.getButton(StyleThemePage.BUTTON_LCD).getSelection()) {
                if (pal.getPaletteSuitability().getSuitability(numClasses,
                        PaletteSuitability.VIEWER_LCD) != PaletteSuitability.QUALITY_GOOD)
                    return false;
            }
            if (this.styleThemePage.getButton(StyleThemePage.BUTTON_PHOTOCOPY).getSelection()) {
                if (pal.getPaletteSuitability().getSuitability(numClasses,
                        PaletteSuitability.VIEWER_PHOTOCOPY) != PaletteSuitability.QUALITY_GOOD)
                    return false;
            }
            if (this.styleThemePage.getButton(StyleThemePage.BUTTON_PRINT).getSelection()) {
                if (pal.getPaletteSuitability().getSuitability(numClasses,
                        PaletteSuitability.VIEWER_PRINT) != PaletteSuitability.QUALITY_GOOD)
                    return false;
            }
            if (this.styleThemePage.getButton(StyleThemePage.BUTTON_PROJECTOR).getSelection()) {
                if (pal.getPaletteSuitability().getSuitability(numClasses,
                        PaletteSuitability.VIEWER_PROJECTOR) != PaletteSuitability.QUALITY_GOOD)
                    return false;
            }
        }
        return true;
    }
}
