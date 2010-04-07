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
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author ptozer TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class PaletteRadioPanel {
    List<Color> colours;
    List<Image> images;
    Point fExtent;
    Composite composite;

    public PaletteRadioPanel( Composite parent ) {
        // for ulterior motives
        colours = new ArrayList<Color>();
        images = new ArrayList<Image>();
        composite = new Composite(parent, SWT.NONE);
        fExtent = computeImageSize(composite);
    }

    public Control getRadioButtons( AWTColor[] coloursToUse ) {

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = coloursToUse.length * 2;

        composite.setLayout(gridLayout);
        // Display display = parent.getDisplay();

        for( int i = 0; i < coloursToUse.length; i++ ) {

            AWTColor awtColour = coloursToUse[i];
            Color swtColour = new Color(composite.getDisplay(), new RGB(awtColour.getColour()
                    .getRed(), awtColour.getColour().getGreen(), awtColour.getColour().getBlue()));
            colours.add(swtColour);

            // Button b = new Button(composite, SWT.TOGGLE);
            Button b = new Button(composite, SWT.FLAT);
            Image image = new Image(composite.getDisplay(), fExtent.x, fExtent.y);
            images.add(image);

            GC gc = new GC(image);
            gc.setBackground(swtColour);
            gc.fillRectangle(0, 0, fExtent.x, fExtent.y);
            gc.dispose();
            // Button b = new Button(image);
            // b.setStyle(STYLE_TOGGLE);
            b.setImage(image);

            GridData data = new GridData();
            b.setLayoutData(data);

            Button radio = new Button(composite, SWT.RADIO);
            data = new GridData();
            radio.setLayoutData(data);
        }
        // we end up with a line of flat boxes (buttons that don't work) with a radio button beside
        // it

        return composite;
    }

    public void dispose() {
        // clean up the colours and images

        for( int i = 0; i < colours.size(); i++ ) {
            colours.remove(i).dispose();
            images.remove(i).dispose();
        }

    }

    protected Point computeImageSize( Control window ) {
        GC gc = new GC(window);
        Font f = JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT);
        gc.setFont(f);
        int height = gc.getFontMetrics().getHeight();
        gc.dispose();
        Point p = new Point(height * 3 - 6, height);
        return p;
    }
}
