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
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.geotools.brewer.color.BrewerPalette;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.ui.ColorEditor;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.palette.ColourScheme;

/**
 * <p>
 * <b>Purpose:</b>
 * </p>
 * <p>
 * Panel for Property page for <em><b>Map</b></em> objects with widgets that allow users to
 * change default colours for the map.
 * </p>
 * 
 * @author ptozer
 * @author chorner
 */
public class PaletteDefaultChooserPanel {
    Composite composite;
    ScrolledComposite scrolledComposite;
    BrewerPalette mapPalette = null;
    ColourScheme mapScheme = null;
    Combo paletteSelectionCombo = null;
    Combo quantityCombo = null;
    ArrayList<PaletteCombo> allLayerControls = new ArrayList<PaletteCombo>();
    Map map = null;
    ColorEditor colorEditor;  //map background color

    int numberOfLayers = 0;

    /**
     * Constructor
     */
    public PaletteDefaultChooserPanel() {
        /*
         * colourLetterCombo lists to choose colours per Each layer- show colour and allow colour
         * choice in drop-down Background colour neutrals plus blue Polygon borders neutrals Polygon
         * Fill palette Line palette plus black and white Point palette plus black and white Text
         * palette plus black and white
         */
    }
    /**
     * <p>
     * Creates a Control with:<br>
     * A colourLetterCombo box to allow the user to change the default colour scheme used to colour
     * all subsequent layers after closing this panel.<br>
     * <br>
     * A list of all current Map Layers and the colour assigned to the layer. The colour is combined
     * with a drop-down of all available colours to choose from. Currently, choosing from this list
     * does nothing expect change the colour beside it. No changes are affected to the layer.
     * </p>
     * 
     * @param parent
     * @param element
     * @return
     */
    public Control createPaletteDefaultChooserPanel( Composite parent, Map thisMap ) {
        /*
         * uses ColorBrewer.com as a guide- all palettes and colour schemes taken from this. The
         * concept of lettered colours also comes from ColorBrewer.
         */

        this.map = thisMap;
        numberOfLayers = this.map.getLayersInternal().size();
        mapPalette = map.getColorPalette();
        mapScheme =  new ColourScheme(map.getColourScheme().getColourPalette(), map.getColourScheme().getSizePalette());
        //workaround for non-saving (need to correct number of colours)
        if (mapScheme.getSizePalette() < numberOfLayers) {
            mapScheme.setSizePalette(numberOfLayers);
        }
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        composite = new Composite(scrolledComposite, SWT.NONE);
        
        scrolledComposite.setContent(composite);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        composite.setLayout(gridLayout);

        // title at top of panel
        Label titleLabel = new Label(composite, SWT.NONE);
        titleLabel.setText(Messages.PaletteDefaultChooserPanel_title); 

        GridData data = new GridData();
        data.horizontalSpan = 4;
        titleLabel.setLayoutData(data);

        //map background color label
        Label backgroundColor = new Label(composite, SWT.NONE);
        backgroundColor.setText(Messages.PaletteDefaultChooserPanel_MapBackgroundColor);
        data = new GridData();
        data.horizontalSpan = 1;
        backgroundColor.setLayoutData(data);
        
        colorEditor = new ColorEditor(composite);
        Color bgColor = (Color)thisMap.getBlackboard().get(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR);
        if (backgroundColor != null)
            colorEditor.setColorValue(new RGB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue()));
       //spacer
        Composite colorc = new Composite(composite, SWT.NONE);
        data = new GridData();
        data.horizontalSpan = 2;
        colorc.setLayoutData(data);
        
        // palette label
        Label paletteLabel = new Label(composite, SWT.NONE);
        paletteLabel.setText(Messages.PaletteDefaultChooserPanel_palette); 

        data = new GridData();
        data.horizontalSpan = 1;
        paletteLabel.setLayoutData(data);

        // palette colourLetterCombo box
        paletteSelectionCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);

        String[] names = PlatformGIS.getColorBrewer().getPaletteNames();

        paletteSelectionCombo.setItems(names);

        // find out index number for selection
        int index = 0;
        for( int i = 0; i < names.length; i++ ) {
            if ((names[i]).equalsIgnoreCase(mapPalette.getName())) {
                index = i;
                break;
            }
        }

        paletteSelectionCombo.addSelectionListener(new SelectionListener(){
            public void widgetSelected( SelectionEvent e ) {
                /*
                 * When a new palette is selected: get the palette String and then we want to
                 * repaint all paletteCombos to have new colours
                 */
                int index = paletteSelectionCombo.getSelectionIndex();
                String name = paletteSelectionCombo.getItem(index);
                BrewerPalette palette = PlatformGIS.getColorBrewer().getPalette(name);
                //ColourScheme scheme = map.getColourScheme();
                //scheme.setColourPalette(palette);
                //map.setColorPalette(palette);
                //map.setColourScheme(scheme);
                mapScheme = new ColourScheme(palette,  Integer.parseInt(quantityCombo.getItem(quantityCombo.getSelectionIndex())) );
                updateLayerDisplay();
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });

        // TODO set selection to default or current colour palette
        paletteSelectionCombo.select(index);// default

        data = new GridData();
        data.horizontalSpan = 3;
        data.widthHint = 50;

        paletteSelectionCombo.setLayoutData(data);

        // palette label
        Label quantityLabel = new Label(composite, SWT.NONE);
        quantityLabel.setText(Messages.PaletteDefaultChooserPanel_colours); 

        data = new GridData();
        data.horizontalSpan = 1;
        quantityLabel.setLayoutData(data);

        quantityCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        int minColours = mapScheme.getMinColours();
        int maxColours = mapPalette.getMaxColors();
        for (int i = minColours; i <= maxColours; i++) {
            quantityCombo.add(Integer.toString(i));
        }
        quantityCombo.select(quantityCombo.indexOf(Integer.toString(mapScheme.getSizePalette())));

        quantityCombo.addSelectionListener(new SelectionListener(){
            public void widgetSelected( SelectionEvent e ) {
                //when the number of colours is modified, regenerate the list of layers w/ colours
                mapScheme.setSizePalette(Integer.parseInt(quantityCombo.getText()));
                updateLayerDisplay();
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });

        data = new GridData();
        data.horizontalSpan = 3;
        data.widthHint = 50;

        quantityCombo.setLayoutData(data);

        /*
         * //TODO what we want eventually here is to allow the user to change to map background,
         * polygon outline colours. Each layer has its own colourscheme These should be defaulted to
         * greysclae (plus cyan for background)
         */

        Label checkBoxLabel = new Label(composite, SWT.NONE);
        checkBoxLabel.setText(Messages.PaletteDefaultChooserPanel_check); 
        data = new GridData();
        data.horizontalSpan = 4;
        checkBoxLabel.setLayoutData(data);

        createLayerDisplay();
        Point pt = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        composite.setSize(pt);

        return scrolledComposite;

    }

    void createLayerDisplay() {
        // clear all existing colour controls
        if (allLayerControls != null && allLayerControls.size() > 0) {
            Iterator iter = allLayerControls.iterator();
            while( iter.hasNext() ) {
                ((PaletteCombo) iter.next()).dispose();
            }
            allLayerControls = new ArrayList<PaletteCombo>();
        }

        // get the layers from the map
        List<ILayer> layers = map.getMapLayers();
        Iterator<ILayer> layerIterator = layers.iterator();
        while( layerIterator.hasNext() ) {
            // for each layer display its current colour
            Layer layer = (Layer) layerIterator.next();
            // System.out.println(layer.getName());

            PaletteCombo layerColourCombo = new PaletteCombo(composite);
            // ad the colour indicator for the layer to the dialog panel
            Control layerCombo = layerColourCombo.getPaletteCombo(layer);

            GridData data = new GridData();
            data.horizontalSpan = 3;
            layerCombo.setLayoutData(data);

            allLayerControls.add(layerColourCombo);
        }
        Point pt = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        composite.setSize(pt);
        composite.layout(true);
        scrolledComposite.layout(true);
    }
    
    void updateLayerDisplay() {
        Iterator<PaletteCombo> layerIterator = allLayerControls.iterator();
        while (layerIterator.hasNext()) {
            PaletteCombo thisLayer = layerIterator.next();
            thisLayer.updateContents(mapScheme);
        }
    }

    /**
     * TODO: rewrite to dispose off all class vars.
     */
    public void dispose() {
        Control[] controls = composite.getChildren();
        for( int i = 0; i < controls.length; i++ ) {

            controls[i].dispose();
        }
        composite.dispose();
        scrolledComposite.dispose();
    }
    
    /**
     * @return Returns the allLayerControls.
     */
    public ArrayList<PaletteCombo> getAllLayerControls() {
        return allLayerControls;
    }
    
    public Color getMapBackgroundColor(){
        return new Color(this.colorEditor.getColorValue().red, this.colorEditor.getColorValue().green, this.colorEditor.getColorValue().blue);
    }
    
    public void updateMapBackgroundColor(Color newColor){
        colorEditor.setColorValue(new RGB(newColor.getRed(), newColor.getGreen(), newColor.getBlue()));
    }
    
    public void updatePalette(String newPalette){
        for (int i = 0; i < paletteSelectionCombo.getItemCount(); i ++){
            if (paletteSelectionCombo.getItem(i).equals(newPalette)){
                paletteSelectionCombo.select(i);
                break;
            }
        }
        
        String name = paletteSelectionCombo.getItem(paletteSelectionCombo.getSelectionIndex());
        BrewerPalette palette = PlatformGIS.getColorBrewer().getPalette(name);
        mapScheme = new ColourScheme(palette,  Integer.parseInt(quantityCombo.getItem(quantityCombo.getSelectionIndex())) );
        updateLayerDisplay();
    }
    
    public ColourScheme getCurrentColourScheme(){
        return this.mapScheme;
    }
}
