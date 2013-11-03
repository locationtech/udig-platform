/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.core;

import org.locationtech.udig.omsbox.processingregion.ProcessingRegion;
import org.locationtech.udig.omsbox.utils.OmsBoxConstants;

/**
 * The data that represent a field.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class FieldData {
    /**
     * Type of the field, either @in or @out.
     */
    public boolean isIn;
    /**
     * Name of the field, to be called via reflection.
     */
    public String fieldName;
    /**
     * Type of the field.
     */
    public String fieldType;

    /**
     * Description of the field.
     */
    public String fieldDescription;

    /**
     * Guihints if there are some.
     */
    public String guiHints;

    // if not connected to other modules
    /**
     * The fields value, at first the default, in the end the set one.
     */
    public String fieldValue;

    // if connected to other module
    /**
     * Field name of a connecting module, if available.
     */
    public String otherFieldName;
    /**
     * Other module, if available.
     */
    public ModuleDescription otherModule;

    /**
     * Checks if the FieldData are referred to values of the {@link ProcessingRegion}. 
     * 
     * @return <code>true</code>, if the field is related to the processing region.
     */
    public boolean isProcessingRegionRelated() {
        if (guiHints != null) {
            if (guiHints.contains(OmsBoxConstants.PROCESS_NORTH_UI_HINT) || //
                    guiHints.contains(OmsBoxConstants.PROCESS_SOUTH_UI_HINT) || //
                    guiHints.contains(OmsBoxConstants.PROCESS_WEST_UI_HINT) || //
                    guiHints.contains(OmsBoxConstants.PROCESS_EAST_UI_HINT) || //
                    guiHints.contains(OmsBoxConstants.PROCESS_COLS_UI_HINT) || //
                    guiHints.contains(OmsBoxConstants.PROCESS_ROWS_UI_HINT) || //
                    guiHints.contains(OmsBoxConstants.PROCESS_XRES_UI_HINT) || //
                    guiHints.contains(OmsBoxConstants.PROCESS_YRES_UI_HINT)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this field is a simple type.
     * 
     * <p>
     * Simple types are:
     * <ul>
     * <li>double</li>
     * <li>float</li>
     * <li>int</li>
     * <li>...</li>
     * <li>Double</li>
     * <li>Float</li>
     * <li>...</li>
     * <li>boolean</li>
     * <li>Boolean</li>
     * <li>String</li>
     * </ul>
     * </p>
     * 
     * @return true if the type is simple.
     */
    public boolean isSimpleType() {
        if (//
        fieldType.equals(Double.class.getCanonicalName()) || //
                fieldType.equals(Float.class.getCanonicalName()) || //
                fieldType.equals(Integer.class.getCanonicalName()) || //
                fieldType.equals(double.class.getCanonicalName()) || //
                fieldType.equals(float.class.getCanonicalName()) || //
                fieldType.equals(int.class.getCanonicalName()) || //
                fieldType.equals(Boolean.class.getCanonicalName()) || //
                fieldType.equals(boolean.class.getCanonicalName()) || //
                fieldType.equals(String.class.getCanonicalName()) //
        ) {
            return true;
        }
        return false;
    }

    /**
     * Checks if this field is of simple array/matrix type.
     * 
     * @return true if the type is simple array or matrix.
     */
    public boolean isSimpleArrayType() {
        if (//
        fieldType.equals(Double[].class.getCanonicalName()) || //
                fieldType.equals(Float[].class.getCanonicalName()) || //
                fieldType.equals(Integer[].class.getCanonicalName()) || //
                fieldType.equals(double[].class.getCanonicalName()) || //
                fieldType.equals(float[].class.getCanonicalName()) || //
                fieldType.equals(int[].class.getCanonicalName()) || //
                fieldType.equals(Boolean[].class.getCanonicalName()) || //
                fieldType.equals(boolean[].class.getCanonicalName()) || //
                fieldType.equals(String[].class.getCanonicalName()) || //
                fieldType.equals(Double[][].class.getCanonicalName()) || //
                fieldType.equals(Float[][].class.getCanonicalName()) || //
                fieldType.equals(Integer[][].class.getCanonicalName()) || //
                fieldType.equals(double[][].class.getCanonicalName()) || //
                fieldType.equals(float[][].class.getCanonicalName()) || //
                fieldType.equals(int[][].class.getCanonicalName()) || //
                fieldType.equals(Boolean[][].class.getCanonicalName()) || //
                fieldType.equals(boolean[][].class.getCanonicalName()) || //
                fieldType.equals(String[][].class.getCanonicalName()) //
        ) {
            return true;
        }
        return false;
    }

    public FieldData makeCopy() {
        FieldData clone = new FieldData();
        clone.isIn = isIn;
        clone.fieldName = fieldName;
        clone.fieldType = fieldType;
        clone.fieldDescription = fieldDescription;
        clone.guiHints = guiHints;
        clone.fieldValue = fieldValue;
        clone.otherFieldName = otherFieldName;
        clone.otherModule = otherModule;
        return clone;
    }

    @SuppressWarnings("nls")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("*************************");
        sb.append("\nisIn: ");
        sb.append(isIn);
        sb.append("\nField name: ");
        sb.append(fieldName);
        sb.append("\nField type: ");
        sb.append(fieldType);
        sb.append("\nField descr: ");
        sb.append(fieldDescription);
        sb.append("\nField value: ");
        sb.append(fieldValue);
        sb.append("\nGui Hints: ");
        sb.append(guiHints);
        sb.append("\n*************************\n");
        return sb.toString();
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldDescription == null) ? 0 : fieldDescription.hashCode());
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        result = prime * result + ((fieldType == null) ? 0 : fieldType.hashCode());
        return result;
    }

    public boolean equals( Object obj ) {
        if (obj instanceof FieldData) {
            FieldData oFd = (FieldData) obj;
            return fieldName.equals(oFd.fieldName) && fieldDescription.equals(oFd.fieldDescription)
                    && fieldType.equals(oFd.fieldType);
        }
        return false;
    }

}
