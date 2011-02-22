/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.palette.ColourScheme;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.geotools.brewer.color.BrewerPalette;

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

    int index = 0;
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
        numberOfLayers = this.map.getContextModel().getLayers().size();
        mapPalette = map.getColorPalette();
        mapScheme = map.getColourScheme();
        //workaround for non-saving (need to correct number of colours)
        if (mapScheme.getSizePalette() < numberOfLayers) {
            mapScheme.setSizePalette(numberOfLayers);
        }
        if (mapScheme.getSizeScheme() < numberOfLayers) {
            mapScheme.setSizeScheme(numberOfLayers);
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
        index = 0;
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
                index = paletteSelectionCombo.getSelectionIndex();
                String name = paletteSelectionCombo.getItem(index);
                BrewerPalette palette = PlatformGIS.getColorBrewer().getPalette(name);
                ColourScheme scheme = map.getColourScheme();
                scheme.setColourPalette(palette);
                map.setColorPalette(palette);
                map.setColourScheme(scheme);
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
}
