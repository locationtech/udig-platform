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
package org.locationtech.udig.style.sld.editor;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IStartup;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.brewer.color.ColorBrewer;
import org.geotools.brewer.color.PaletteSuitability;
import org.geotools.brewer.color.PaletteType;
import org.geotools.brewer.color.SampleScheme;
import org.locationtech.udig.style.sld.SLDPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A loader for custom colortables. The colorbrewer 15 values hardcoded is simply not enough.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CustomPalettesLoader implements IStartup {
    // public static final PaletteType UDIG_CUSTOM = new PaletteType(true, true, "UDIG_CUSTOM");

    public static final List<BrewerPalette> PALETTESLIST = new ArrayList<BrewerPalette>();

    public void earlyStartup() {
        if (PALETTESLIST.size() > 0) {
            // read only first time
            return;
        }
        URL palettesFolderUrl = Platform.getBundle(SLDPlugin.ID).getResource("palettes"); //$NON-NLS-1$
        String palettesFolderPath = null;
        try {
            palettesFolderPath = FileLocator.toFileURL(palettesFolderUrl).getPath();

            File palettesFolderFile = new File(palettesFolderPath);
            File[] palettesList = palettesFolderFile.listFiles(new FilenameFilter(){
                public boolean accept( File dir, String name ) {
                    return name.endsWith(".xml"); //$NON-NLS-1$
                }
            });

            for( File file : palettesList ) {
                try {
                    load(new FileInputStream(file), ColorBrewer.ALL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load( InputStream stream, PaletteType type ) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(stream);
        String name = fixToString(document.getElementsByTagName("name").item(0).getFirstChild().toString());
        String description = fixToString(document.getElementsByTagName("description").item(0).getFirstChild().toString());

        SampleScheme scheme = new SampleScheme();

        NodeList samples = document.getElementsByTagName("sample");

        for( int i = 0; i < samples.getLength(); i++ ) {
            Node sample = samples.item(i);
            int size = Integer.parseInt(sample.getAttributes().getNamedItem("size").getNodeValue());
            String values = fixToString(sample.getFirstChild().toString());
            int[] list = new int[size];
            StringTokenizer tok = new StringTokenizer(values);

            for( int j = 0; j < size; j++ ) {
                list[j] = Integer.parseInt(tok.nextToken(","));
            }

            scheme.setSampleScheme(size, list);
        }

        NodeList palettes = document.getElementsByTagName("palette");

        for( int i = 0; i < palettes.getLength(); i++ ) {
            BrewerPalette pal = new BrewerPalette();
            PaletteSuitability suitability = new PaletteSuitability();
            NodeList paletteInfo = palettes.item(i).getChildNodes();

            for( int j = 0; j < paletteInfo.getLength(); j++ ) {
                Node item = paletteInfo.item(j);

                if (item.getNodeName().equals("name")) {
                    pal.setName(fixToString(item.getFirstChild().toString()));
                }

                if (item.getNodeName().equals("description")) {
                    pal.setDescription(fixToString(item.getFirstChild().toString()));
                }

                if (item.getNodeName().equals("colors")) {
                    StringTokenizer oTok = new StringTokenizer(fixToString(item.getFirstChild().toString()));
                    List<Color> colors = new ArrayList<Color>();

                    while( oTok.hasMoreTokens() ) {
                        String entry = oTok.nextToken(":");
                        StringTokenizer iTok = new StringTokenizer(entry);
                        int r = Integer.parseInt(iTok.nextToken(",").trim());
                        int g = Integer.parseInt(iTok.nextToken(",").trim());
                        int b = Integer.parseInt(iTok.nextToken(",").trim());
                        colors.add(new Color(r, g, b));
                    }

                    pal.setColors((Color[]) colors.toArray(new Color[colors.size()]));
                }

                if (item.getNodeName().equals("suitability")) {
                    NodeList schemeSuitability = item.getChildNodes();

                    for( int k = 0; k < schemeSuitability.getLength(); k++ ) {
                        Node palScheme = schemeSuitability.item(k);

                        if (palScheme.getNodeName().equals("scheme")) {
                            int paletteSize = Integer.parseInt(palScheme.getAttributes().getNamedItem("size").getNodeValue());

                            String values = fixToString(palScheme.getFirstChild().toString());
                            String[] list = new String[6];
                            StringTokenizer tok = new StringTokenizer(values);

                            // obtain all 6 values, which should each be
                            // G=GOOD, D=DOUBTFUL, B=BAD, or ?=UNKNOWN.
                            for( int m = 0; m < 6; m++ ) {
                                list[m] = tok.nextToken(",");
                            }

                            suitability.setSuitability(paletteSize, list);
                        }
                    }
                }
            }

            pal.setType(type);

            if (scheme.getMaxCount() == -1) {
                Color[] colors = pal.getColors();
                int length = colors.length;
                CustomSampleScheme scheme2 = new CustomSampleScheme(length);
                
                for( int j = 2; j < length; j++ ) {
                    int[] list = new int[j];
                    for( int k = 0; k < list.length; k++ ) {
                        list[k] = k;
                    }
                    scheme2.setSampleScheme(j, list);
                }
                pal.setColorScheme(scheme2);
            }

            pal.setPaletteSuitability(suitability);
            PALETTESLIST.add(pal);
        }
    }

    /**
     * Converts "[#text: 1,2,3]" to "1,2,3".
     *
     * <p>
     * This is a fix for the org.w3c.dom API. Under j1.4
     * Node.toString() returns "1,2,3", under j1.5 Node.toString() returns
     * "[#text: 1,2,3]".
     * </p>
     *
     * @param input A String with the input.
     *
     * @return A String with the modified input.
     */
    private String fixToString( String input ) {
        if (input.startsWith("[") && input.endsWith("]")) {
            input = input.substring(1, input.length() - 1); // remove []
            input = input.replaceAll("#text: ", ""); // remove "#text: "
        }

        return input;
    }

}
