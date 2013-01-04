/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.jconsole.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import eu.udig.jconsole.JConsolePlugin;

/**
 * A singleton cache for images.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public enum Keywords {
    CONSTANTS("keywords/constants.txt"), //
    GEOSCRIPT("keywords/geoscript.txt"), //
    KEYWORDS("keywords/keywords.txt"), //
    OMS("keywords/oms.txt"), //
    TYPES("keywords/types.txt");

    private static HashMap<String, List<String>> keywordsMap = new HashMap<String, List<String>>();
    private final String path;

    private Keywords( String path ) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    /**
     * Get the keywords for a particular type.
     * 
     * @param keyword the type of keywords to get.
     * @return the {@link List} of keywords.
     */
    public static List<String> getValues( Keywords keyword ) {
        List<String> stringsList = keywordsMap.get(keyword.toString());
        if (stringsList == null) {
            stringsList = getStrings(keyword);
            keywordsMap.put(keyword.toString(), stringsList);
        }
        return stringsList;
    }

    private static List<String> getStrings( Keywords keyword ) {
        List<String> stringsList = new ArrayList<String>();
        String keyPath = keyword.getPath();
        URL keyUrl = Platform.getBundle(JConsolePlugin.PLUGIN_ID).getResource(keyPath);
        try {
            String realKeyPath = FileLocator.toFileURL(keyUrl).getPath();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(realKeyPath));
                String line = null;
                while( (line = br.readLine()) != null ) {
                    line = line.trim();
                    if (line.length() == 0) {
                        continue;
                    }
                    if (line.startsWith("#")) {
                        continue;
                    }
                    stringsList.add(line);
                }
            } finally {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringsList;
    }

}
