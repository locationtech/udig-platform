/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package eu.udig.omsbox.utils;

import java.lang.reflect.Method;
import java.util.Locale;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Keywords;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Status;

/**
 * Utilities to handle annotations.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class AnnotationUtilities {

    private static String LANG = Locale.getDefault().getCountry().toLowerCase();

    /**
     * Gets the localized description of the {@link Description}.
     * 
     * @param description the {@link Description} annotation.
     * @return the description string or " - ".
     * @throws Exception
     */
    public static String getLocalizedDescription( Description description ) throws Exception {
        // try to get the language
        Class< ? > annotationclass = Description.class;
        return getLocalizedString(description, annotationclass);
    }

    public static String getLocalizedDocumentation( Documentation documentation ) throws Exception {
        // try to get the language
        Class< ? > annotationclass = Documentation.class;
        return getLocalizedString(documentation, annotationclass);
    }

    public static String getLocalizedStatus( Status annotation ) throws Exception {
        // try to get the language
        Class< ? > annotationclass = Status.class;
        return getLocalizedString(annotation, annotationclass);
    }

    public static String getLocalizedName( Name annotation ) throws Exception {
        // try to get the language
        Class< ? > annotationclass = Name.class;
        return getLocalizedString(annotation, annotationclass);
    }

    public static String getLocalizedAuthor( Author annotation ) throws Exception {
        // try to get the language
        Class< ? > annotationclass = Author.class;
        return getLocalizedString(annotation, annotationclass);
    }
    public static String getLocalizedLicense( License annotation ) throws Exception {
        // try to get the language
        Class< ? > annotationclass = License.class;
        return getLocalizedString(annotation, annotationclass);
    }
    public static String getLocalizedKeywords( Keywords annotation ) throws Exception {
        // try to get the language
        Class< ? > annotationclass = Keywords.class;
        return getLocalizedString(annotation, annotationclass);
    }

    private static String getLocalizedString( Object object, Class< ? > annotationclass ) throws Exception {
        Method method = null;
        try {
            method = annotationclass.getMethod(LANG);
        } catch (Exception e) {
            // ignore
        }
        String valueStr = "value";
        if (method == null) {
            try {
                method = annotationclass.getMethod(valueStr);
            } catch (Exception e) {
                // ignore
            }
        }

        if (method != null) {
            try {
                Object result = method.invoke(object);
                if (result instanceof String) {
                    String descriptionStr = (String) result;
                    if (descriptionStr.length() > 0) {
                        return descriptionStr;
                    } else {
                        // the method of the language exists but was not filled
                        // try with value()
                        try {
                            method = annotationclass.getMethod(valueStr);
                            result = method.invoke(object);
                            if (result instanceof String) {
                                descriptionStr = (String) result;
                                if (descriptionStr.length() > 0) {
                                    return descriptionStr;
                                }
                            }
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }
            } catch (Exception e) {
                // ignore and return default
            }
        }

        return " - ";
    }

}
