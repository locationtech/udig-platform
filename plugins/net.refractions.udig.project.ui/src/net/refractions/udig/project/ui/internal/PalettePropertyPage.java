/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.style.sld.SLDContent;
import net.refractions.udig.ui.graphics.SLDs;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.geotools.styling.Style;

/**
 * <p>
 * <b>Purpose:</b>
 * </p>
 * <p>
 * Property page for <em><b>Map</b></em> objects allows user to change default colours.
 * </p>
 *
 * @author ptozer
 */
public class PalettePropertyPage extends PropertyPage implements IWorkbenchPropertyPage {
    PaletteDefaultChooserPanel panel = new PaletteDefaultChooserPanel();

    private Map map;

    public PalettePropertyPage() {
        setTitle(Messages.PalettePropertyPage_Title);
    }

    @Override
    public void setElement( IAdaptable element ) {
        Map map;
        if( element instanceof Map){
            map=(Map)element;
        }else{
            map=(Map) element.getAdapter(Map.class);
        }
        this.map = map;
    }

//    public PalettePropertyPage( Map map ) {
//        setTitle(Messages.PalettePropertyPage_Title);
//        this.map = map;
//    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     * @param parent
     */
    protected Control createContents( Composite parent ) {

        Control c = panel.createPaletteDefaultChooserPanel(parent, map);
        return c;
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {

        // get colours from the combos and set them into the layer
        // Control [] allChildren = panel.composite.getChildren();
        // find controls that are the layer colours
        // get colour scheme
        // get the layer colour letter
        // set colour on symbolizer for the layer

        ArrayList<PaletteCombo> allLayerColourControls = panel.getAllLayerControls();
        // so here we want to get all controls, check if the checkbox is selected, and update colour
        // for the given layer if it is checked
        // colour controls have a ref to their layer, so we can use this to make changes to the
        // default for the layer

        Iterator<PaletteCombo> iterator = allLayerColourControls.iterator();

        /*
         * Check all user selections-> if the check box is selected, then we replace all colour info
         * with the current default colour scheme for the Map. If not, ignore it and the song
         * remains the same.
         */
        while( iterator.hasNext() ) {
            PaletteCombo combo = iterator.next();
            Layer l = combo.layerReference;
            Button checkbox = combo.getCheckbox();

            if (checkbox.getSelection()) {
                //ColourScheme cs = map.getColourScheme();
                Combo colourLetterCombo = combo.getColourLetterCombo();
                String[] letters = colourLetterCombo.getItems();
                int index = colourLetterCombo.getSelectionIndex();
                Color colour = null;
                if (index >= 0) {
                    colour = map.getColorPalette().getColors(letters.length)[index];
                    l.setDefaultColor(colour);
                }
                // set layer default colours here
                Style style = (Style) l.getStyleBlackboard().get(SLDContent.ID);

                if (style != null) {
                    SLDs.setLineColour(style, colour);
                    SLDs.setPointColour(style, colour);
                    SLDs.setPolyColour(style, colour);
                }
                l.setStyleBlackboard(l.getStyleBlackboard());
                // show the change on the Map
                l.refresh(l.getBounds(null, l.getCRS()));

            }
        }
        return super.performOk();
    }

    /*
     * TODO; where to call dispose() for the panel control?
     */

    /**
     * @see org.eclipse.jface.preference.PreferencePage#doComputeSize()
     */
    protected Point doComputeSize() {
        return new Point(500, 500);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init( IWorkbench workbench ) {
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
     */
    public IPreferenceStore getPreferenceStore() {
        return ProjectUIPlugin.getDefault().getPreferenceStore();
    }
}
