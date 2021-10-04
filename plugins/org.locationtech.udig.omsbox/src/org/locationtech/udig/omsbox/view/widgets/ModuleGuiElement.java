/**
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.view.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.locationtech.udig.omsbox.core.FieldData;

import net.miginfocom.swt.MigLayout;

/**
 * A GUI element.
 *
 * @author Andrea Antonello (www.hydrologis.com)
 */
public abstract class ModuleGuiElement {

    /**
     * Creates the GUI for the element basing on the {@link MigLayout}.
     *
     * @param parent the parent {@link Composite}, assuming that it has a {@link MigLayout}.
     * @return the created control.
     */
    public abstract Control makeGui(Composite parent);

    /**
     * Get the FieldData that were modified by user interaction with the widget.
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
     * <p>
     * This might for example be if a file exists in the case of an open file widget.
     *
     * @return null if everything is OK, a message string if not.
     */
    public abstract String validateContent();

    /**
     * Tries to convert a string to the required class.
     *
     * @param value the string to convert.
     * @param clazz the class to which convert.
     * @return the new object or <code>null</code> if it can't adapt.
     */
    public <T> T adapt(String value, Class<T> clazz) {
        try {
            if (clazz.isAssignableFrom(Double.class)) {
                Double parsedDouble = Double.valueOf(value);
                return clazz.cast(parsedDouble);
            } else if (clazz.isAssignableFrom(Float.class)) {
                Float parsedFloat = Float.valueOf(value);
                return clazz.cast(parsedFloat);
            } else if (clazz.isAssignableFrom(Integer.class)) {
                Integer parsedInteger = null;
                try {
                    parsedInteger = Integer.valueOf(value);
                } catch (Exception e) {
                    // try also true/false
                    if (value.toLowerCase().equals("true") || value.toLowerCase().equals("y")) { //$NON-NLS-1$ //$NON-NLS-2$
                        parsedInteger = 1;
                    } else if (value.toLowerCase().equals("false") //$NON-NLS-1$
                            || value.toLowerCase().equals("n")) { //$NON-NLS-1$
                        parsedInteger = 0;
                    } else {
                        return null;
                    }
                }
                return clazz.cast(parsedInteger);
            } else if (clazz.isAssignableFrom(Long.class)) {
                Long parsedLong = Long.valueOf(value);
                return clazz.cast(parsedLong);
            } else if (clazz.isAssignableFrom(String.class)) {
                return clazz.cast(value);
            }
        } catch (Exception e) {
            // ignore, if it can't resolve, return null
        }
        return null;
    }

    protected String checkBackSlash(String textStr, boolean isFile) {
        if (isFile) {
            textStr = textStr.replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return textStr;
    }

}
