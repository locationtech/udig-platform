/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.omsbox.core;

import eu.udig.omsbox.processingregion.ProcessingRegion;
import eu.udig.omsbox.utils.OmsBoxConstants;

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