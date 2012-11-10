/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.style.sld.editor;

import java.awt.Color;

import org.geotools.brewer.color.BrewerPalette;
import org.geotools.brewer.color.ColorBrewer;
import org.geotools.brewer.color.PaletteSuitability;
import org.geotools.brewer.color.PaletteType;
import org.geotools.brewer.color.SampleScheme;

/**
 * A dynamic palette that adapts to whatever number of classes through interpolation.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CustomDynamicPalette extends BrewerPalette {
    public enum TABLE {
        RAINBOW, GREY
    }
    
    private int[][] current = null;

    private int[][] rainbow = new int[][]{{255, 255, 0}, /* yellow */
    {0, 255, 0}, /* green */
    {0, 255, 255}, /* cyan */
    {0, 0, 255}, /* blue */
    {255, 0, 255}, /* magenta */
    {255, 0, 0} /* red */
    };
    private int[][] grey = new int[][]{{0, 0, 0}, {255, 255, 255}};

    private CustomSampleScheme sampler;
    private PaletteType type;

    public CustomDynamicPalette( TABLE tableType ) {
        switch( tableType ) {
        case RAINBOW:
            setName("Dynamic Rainbow");
            setDescription("A rainbow colors palette dynamically adapting to the number of classes");
            current = rainbow;
            break;
        case GREY:
            setName("Dynamic Greyscale");
            setDescription("A greyscale palette dynamically adapting to the number of classes");
            current = grey;
            break;

        default:
            break;
        }
    }

    public CustomDynamicPalette( String name, String descripiton, Color[] colors ) {
        setName(name);
        setDescription(descripiton);

        current = new int[colors.length][3];
        for( int i = 0; i < colors.length; i++ ) {
            current[i][0] = colors[i].getRed();
            current[i][1] = colors[i].getGreen();
            current[i][2] = colors[i].getBlue();
        }
    }

    public PaletteType getType() {
        return ColorBrewer.ALL;
    }

    public void setType( PaletteType type ) {
        this.type = type;
    }

    public Color getColor( int index, int length ) {
        return getColors(length)[index];
    }

    public int getMaxColors() {
        return Integer.MAX_VALUE;
    }

    public int getMinColors() {
        return 6;
    }

    public Color[] getColors( int length ) {
        if (length < 2) {
            length = 2; // if they ask for 1 colour, give them 2 instead of crashing
        }
        if (length == Integer.MAX_VALUE) {
            length = 20;
        }

        return interpolateColors(length);
    }

    private Color[] interpolateColors( int classes ) {
        Color[] colors = new Color[classes];

        // normalize to color nums

        float factor = (current.length - 1) / (float) classes;

        for( int i = 0; i < classes; i++ ) {
            float y = factor * i;

            // interpolate
            int floor = (int) Math.floor(y);
            int ceil = (int) Math.ceil(y);

            int r;
            int g;
            int b;
            if ((ceil - floor) == 0) {
                r = current[floor][0];
                g = current[floor][1];
                b = current[floor][2];
            } else {
                float r1 = current[floor][0];
                float g1 = current[floor][1];
                float b1 = current[floor][2];
                float r2 = current[ceil][0];
                float g2 = current[ceil][1];
                float b2 = current[ceil][2];

                r = (int) (r2 - (ceil - y) * (r2 - r1) / (ceil - floor));
                g = (int) (g2 - (ceil - y) * (g2 - g1) / (ceil - floor));
                b = (int) (b2 - (ceil - y) * (b2 - b1) / (ceil - floor));
            }

            colors[i] = new Color(r, g, b);
        }

        // refresh the colorscheme
        int length = colors.length;
        sampler = new CustomSampleScheme(length);

        for( int j = 2; j < length; j++ ) {
            int[] list = new int[j];
            for( int k = 0; k < list.length; k++ ) {
                list[k] = k;
            }
            sampler.setSampleScheme(j, list);
        }

        return colors;
    }

    public PaletteSuitability getPaletteSuitability() {
        return CustomUnknownPaletteSuitability.getInstance();
    }

    public void setPaletteSuitability( PaletteSuitability suitability ) {
    }

    public SampleScheme getColorScheme() {
        return sampler;
    }

    public void setColorScheme( CustomSampleScheme scheme ) {
        this.sampler = scheme;
    }
}
