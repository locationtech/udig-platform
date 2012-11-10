/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.omsbox.view.widgets;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.udig.omsbox.core.FieldData;

/**
 * A gui element.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public abstract class ModuleGuiElement {

    /**
     * Creates the gui for the element basing on the {@link MigLayout}.
     * 
     * @param parent the parent {@link Composite}, assuming that it has a {@link MigLayout}.
     * @return the created control.
     */
    public abstract Control makeGui( Composite parent );

    /**
     * Get the fielddata that were modified by user interaction with the widget.
     * 
     * @return the {@link FieldData}.
     */
    public abstract FieldData getFieldData();

    /**
     * Defines if the widget is for data supply (for example a label does not).
     * 
     * @return true if the widget has data to supply.
     */
    public abstract boolean hasData();

    /**
     * Does a consistency check on the content of the widget.
     * 
     * <p>This might for example be if a file exists in the case of an open file widget.
     * 
     * @return null if everything is ok, a message string if not.
     */
    public abstract String validateContent();

    /**
     * Tries to convert a string to the required class.
     * 
     * @param value the string to convert.
     * @param clazz the class to which convert.
     * @return the new object or <code>null</code> if it can't adapt.
     */
    @SuppressWarnings("nls")
    public <T> T adapt( String value, Class<T> clazz ) {
        try {
            if (clazz.isAssignableFrom(Double.class)) {
                Double parsedDouble = new Double(value);
                return clazz.cast(parsedDouble);
            } else if (clazz.isAssignableFrom(Float.class)) {
                Float parsedFloat = new Float(value);
                return clazz.cast(parsedFloat);
            } else if (clazz.isAssignableFrom(Integer.class)) {
                Integer parsedInteger = null;
                try {
                    parsedInteger = new Integer(value);
                } catch (Exception e) {
                    // try also true/false
                    if (value.toLowerCase().equals("true") || value.toLowerCase().equals("y")) {
                        parsedInteger = 1;
                    } else if (value.toLowerCase().equals("false") || value.toLowerCase().equals("n")) {
                        parsedInteger = 0;
                    } else {
                        return null;
                    }
                }
                return clazz.cast(parsedInteger);
            } else if (clazz.isAssignableFrom(Long.class)) {
                Long parsedLong = new Long(value);
                return clazz.cast(parsedLong);
            } else if (clazz.isAssignableFrom(String.class)) {
                return clazz.cast(value);
            }
        } catch (Exception e) {
            // ignore, if it can't resolve, return null
        }
        return null;
    }

    protected String checkBackSlash( String textStr, boolean isFile ) {
        if (isFile) {
            textStr = textStr.replaceAll("\\\\", "/");
        }
        return textStr;
    }

}
