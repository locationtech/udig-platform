/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.geotools.styling.Style;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.style.sld.SLDContent;
import org.locationtech.udig.ui.graphics.SLDs;

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
        /*
         * Check all user selections-> if the check box is selected, then we replace all colour info
         * with the current default colour scheme for the Map. If not, ignore it and the song
         * remains the same.
         */
        map.setColourScheme(panel.getCurrentColourScheme());
        map.setColorPalette(panel.getCurrentColourScheme().getColourPalette());
        Iterator<PaletteCombo> iterator = allLayerColourControls.iterator();
        boolean needsrefresh = false;
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
                needsrefresh = true;
            }
        }
        
        //set map background color
        Color newColor = panel.getMapBackgroundColor();
        boolean changed = updateMapBackgroundColor(newColor);
        if (changed || needsrefresh){
            map.getRenderManager().refresh(null);
        }
        return super.performOk();
    }

    //updates the map background color if the new color
    //differs from the old color
    private boolean updateMapBackgroundColor(Color newColor){
        Color oldColor = (Color)map.getBlackboard().get(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR);
        if (!oldColor.equals(newColor)){
            map.getBlackboard().put(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR, newColor);
            return true;
        }
        return false;
    }
    
    protected void performDefaults() {
        IPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
        RGB background = PreferenceConverter.getColor(store, PreferenceConstants.P_BACKGROUND); 
        Color defaultColor = new Color(background.red, background.green, background.blue);
        updateMapBackgroundColor(defaultColor);
        panel.updateMapBackgroundColor(defaultColor);
        
        
        String defaultPalette = ProjectPlugin.getPlugin().getPreferenceStore().getString(PreferenceConstants.P_DEFAULT_PALETTE);
        panel.updatePalette(defaultPalette);
        
        super.performDefaults();
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
