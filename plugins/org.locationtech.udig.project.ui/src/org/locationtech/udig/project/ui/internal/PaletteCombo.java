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
import java.util.HashMap;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.ui.palette.ColourScheme;

/**
 * @author ptozer TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates 
 * @author chorner
 */
public class PaletteCombo {
    Point fExtent = null;
    Composite composite = null;
    Combo colourLetterCombo = null;
    Button colourIndicatorButton = null;
    Image image = null;
    Button checkbox = null;
    Layer layerReference = null;
    org.eclipse.swt.graphics.Color swtColour = null;
    
    public PaletteCombo( Composite parent ) {
        composite = parent; //new Composite(parent, SWT.NONE);
        fExtent = computeImageSize(composite);
        image = new Image(parent.getDisplay(), fExtent.x, fExtent.y);
    }

    /**
     * @param colourToUse
     * @param layerNumber
     * @param colourLetters
     * @return
     */
    public Control getPaletteCombo( Layer layer ) {
        layerReference = layer;
        String layerName = layer.getName();
        ColourScheme layerScheme = layer.getColourScheme();
        ColourScheme mapScheme = layer.getMapInternal().getColourScheme();
        
        final ColourScheme currentScheme;
        if (layerScheme != null && !(layerScheme.equals(mapScheme))) { //TODO: check logic
            currentScheme = layerScheme;
        } else {
            currentScheme = mapScheme;
        }
        String[] colourLetters = getColourLetters(currentScheme);
        
        int currentColourIndex = layer.getMapInternal().getMapLayers().indexOf(layer);
        layerReference = layer;

//        GridLayout gridLayout = new GridLayout();
//        gridLayout.numColumns = 4;
//
//        composite.setLayout(gridLayout);

        Label layerLabel = new Label(composite, SWT.NONE);
        layerLabel.setText(layerName + ":"); //$NON-NLS-1$

        GridData data = new GridData();
        data.horizontalSpan = 1;
        layerLabel.setLayoutData(data);

        colourIndicatorButton = new Button(composite, SWT.FLAT | SWT.TRAIL);
        updateButtonColourDisplay(currentScheme, currentColourIndex);

        data = new GridData();
        colourIndicatorButton.setLayoutData(data);

        colourLetterCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        colourLetterCombo.setItems(colourLetters);
        colourLetterCombo.select(currentColourIndex);
        colourLetterCombo.addSelectionListener(new SelectionListener(){
            public void widgetSelected( SelectionEvent e ) {
                int selectIndex = colourLetterCombo.getSelectionIndex();
                updateButtonColourDisplay(currentScheme, selectIndex);
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });

        data = new GridData();
        colourLetterCombo.setLayoutData(data);

        checkbox = new Button(composite, SWT.CHECK);
        checkbox.setSelection(true);

        data = new GridData();
        checkbox.setLayoutData(data);

        // composite.layout(true);
        return composite;
    }

    /**
     * 
     */
    public void dispose() {
        // clean up the colours and images
        if (composite != null) {
            Control[] kids = composite.getChildren();
            for( int i = 0; i < kids.length; i++ ) {
                Control c = kids[i];
                if (c instanceof Button) {
                    Image img = ((Button) c).getImage();
                    if (img != null) {
                        img.dispose();
                        ((Button) c).getImage().dispose();
                    }
                }
                c.dispose();
            }
        }

        fExtent = null;
        composite = null;
        colourLetterCombo = null;
        colourIndicatorButton = null;
        image = null;
        checkbox = null;
        layerReference = null;
    }

    public void updateContents(ColourScheme scheme) {
        int index = colourLetterCombo.getSelectionIndex();
        String[] colourLetters = getColourLetters(scheme);
        colourLetterCombo.setItems(colourLetters);
        if (index == -1) {
            index = colourLetters.length-1;
        }
        colourLetterCombo.select(index);
        updateButtonColourDisplay(scheme, index);
    }
    
    /**
     * @param index
     */
    protected void updateButtonColourDisplay( ColourScheme scheme, int index ) {
        GC gc = new GC(image);
        gc.drawRectangle(0, 2, fExtent.x, fExtent.y);
        
        if (swtColour != null)
            swtColour.dispose();

        Color clr = scheme.getColour(index);
        swtColour = new org.eclipse.swt.graphics.Color(composite.getDisplay(), new RGB(clr.getRed(),
                clr.getGreen(), clr.getBlue()));
        gc.setBackground(swtColour);
        gc.fillRectangle(0, 2, fExtent.x, fExtent.y);
        gc.dispose();
        colourIndicatorButton.setImage(image);
    }

    /**
     * @param window
     * @return
     */
    protected Point computeImageSize( Control window ) {
        GC gc = new GC(window);
        Font f = JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT);
        gc.setFont(f);
        int height = gc.getFontMetrics().getHeight();
        gc.dispose();
        Point p = new Point(height * 3 - 6, height);
        return p;
    }

    /**
     * @return Returns the colourLetterCombo.
     */
    public Combo getColourLetterCombo() {
        return colourLetterCombo;
    }

    /**
     * @return Returns the checkbox.
     */
    public Button getCheckbox() {
        return checkbox;
    }
    
    private String[] getColourLetters(ColourScheme scheme) {
        int size = scheme.getSizePalette();
        String[] allColourLetters = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$ //$NON-NLS-20$ //$NON-NLS-21$ //$NON-NLS-22$ //$NON-NLS-23$ //$NON-NLS-24$ //$NON-NLS-25$ //$NON-NLS-26$
        String[] colourLetters;
        int[] colourIndex = scheme.getColourPalette().getColorScheme().getSampleScheme(size);
        colourLetters = new String[size];
        if (scheme.getSizeScheme() < size) {
            scheme.setSizeScheme(size);
        }
        HashMap<Integer,Integer> colourMap = scheme.getColourMap();
        for( int i = 0; i < size; i++ ) {
            int schemeIndex = colourMap.get(i);
            int actualIndex = colourIndex[schemeIndex];
            colourLetters[i] = allColourLetters[actualIndex];
        }
        return colourLetters;
    }
}
