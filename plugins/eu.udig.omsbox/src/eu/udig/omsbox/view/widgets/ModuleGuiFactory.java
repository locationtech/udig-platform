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
package eu.udig.omsbox.view.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.data.simple.SimpleFeatureCollection;

import eu.udig.omsbox.core.FieldData;
import eu.udig.omsbox.utils.OmsBoxConstants;

/**
 * A factory for guis.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class ModuleGuiFactory {

    public static int COLUMNS = 12;

    /**
     * Create the gui for the input field.
     * 
     * @param inputData the input data.
     * @param row the row of this item, used for the constraints.
     * @return the gui for the form element.
     */
    public List<ModuleGuiElement> createInputGui( FieldData inputData, int[] row ) {

        List<ModuleGuiElement> guiElements = new ArrayList<ModuleGuiElement>();
        if (inputData != null)
            if (isAtLeastOneAssignable(inputData.fieldType, String.class)) {
                if (inputData.guiHints != null && inputData.guiHints.startsWith(OmsBoxConstants.MULTILINE_UI_HINT)) {
                    handleTextArea(inputData, row, guiElements);
                } else {
                    handleTextField(inputData, row, guiElements);
                }
            } else if (isAtLeastOneAssignable(inputData.fieldType, Double.class, double.class)) {
                handleTextField(inputData, row, guiElements);
            } else if (isAtLeastOneAssignable(inputData.fieldType, Float.class, float.class)) {
                handleTextField(inputData, row, guiElements);
            } else if (isAtLeastOneAssignable(inputData.fieldType, Integer.class, int.class)) {
                handleTextField(inputData, row, guiElements);
            } else if (isAtLeastOneAssignable(inputData.fieldType, Short.class, short.class)) {
                handleTextField(inputData, row, guiElements);
            } else if (isAtLeastOneAssignable(inputData.fieldType, Boolean.class, boolean.class)) {
                handleBooleanField(inputData, row, guiElements);
            } else if (isAtLeastOneAssignable(inputData.fieldType, GridCoverage2D.class)) {
                handleGridcoverageInputField(inputData, row, guiElements);
            } else if (isAtLeastOneAssignable(inputData.fieldType, GridGeometry2D.class)) {
                handleGridgeometryInputField(inputData, row, guiElements);
            } else if (isAtLeastOneAssignable(inputData.fieldType, SimpleFeatureCollection.class)) {
                handleFeatureInputField(inputData, row, guiElements);
            } else if (isAtLeastOneAssignable(inputData.fieldType, HashMap.class)) {
                handleHashMapInputField(inputData, row, guiElements);
            } else if (isAtLeastOneAssignable(inputData.fieldType, List.class)) {
                if (inputData.guiHints != null && inputData.guiHints.equals(OmsBoxConstants.FILESPATHLIST_UI_HINT)) {
                    handleFilesPathListInputField(inputData, row, guiElements);
                } else {
                    handleListInputField(inputData, row, guiElements);
                }
            } else {
                if (!inputData.fieldType.endsWith("ProgressMonitor")) {
                    System.out.println("Skipping input field: " + inputData.fieldType);
                }
            }

        return guiElements;
        // throw new IllegalArgumentException();
    }
    /**
     * Create the gui for the output field.
     * 
     * @param outputData the output data.
     * @param row the row of this item, used for the constraints.
     * @return the gui for the form element.
     */
    public List<ModuleGuiElement> createOutputGui( FieldData outputData, int[] row ) {

        List<ModuleGuiElement> guiElements = new ArrayList<ModuleGuiElement>();
        // if (isAtLeastOneAssignable(outputData.fieldType, String.class)) {
        // handleTextField(outputData, row, guiElements);
        // } else if (isAtLeastOneAssignable(outputData.fieldType, Double.class, double.class)) {
        // handleTextField(outputData, row, guiElements);
        // } else if (isAtLeastOneAssignable(outputData.fieldType, Integer.class, int.class)) {
        // handleTextField(outputData, row, guiElements);
        // } else if (isAtLeastOneAssignable(outputData.fieldType, Boolean.class, boolean.class)) {
        // handleBooleanField(outputData, row, guiElements);
        // } else
        if (isAtLeastOneAssignable(outputData.fieldType, GridCoverage2D.class)) {
            handleGridcoverageOutputField(outputData, row, guiElements);
        } else if (isAtLeastOneAssignable(outputData.fieldType, SimpleFeatureCollection.class)) {
            handleFeatureOutputField(outputData, row, guiElements);
        } else if (isAtLeastOneAssignable(outputData.fieldType, HashMap.class)) {
            handleHashMapOutputField(outputData, row, guiElements);
        } else if (isAtLeastOneAssignable(outputData.fieldType, List.class)) {
            handleListOutputField(outputData, row, guiElements);
        } else {
            if (outputData != null)
                System.out.println("Skipping output field: " + outputData.fieldType);
        }
        return guiElements;
        // throw new IllegalArgumentException();
    }

    private String extractSingleGuiHint( String pattern, String guiHints ) {
        String[] split = guiHints.split(",");
        for( String hint : split ) {
            hint = hint.trim();
            if (hint.contains(pattern)) {
                return hint;
            }
        }
        return null;
    }

    /**
     * Checks if one class is assignable from at least one of the others.
     * 
     * @param main the chanonical name of class to check.
     * @param classes the other classes.
     * @return true if at least one of the other classes match.
     */
    private boolean isAtLeastOneAssignable( String main, Class< ? >... classes ) {
        for( Class< ? > clazz : classes ) {
            if (clazz.getCanonicalName().equals(main)) {
                return true;
            }
        }
        return false;
    }

    private void handleTextField( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        int labelCol = COLUMNS / 5;
        sb.append(labelCol);
        sb.append(" ");
        sb.append(1);
        // sb.append(", growx");
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(labelCol + 1);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS - labelCol);
        sb.append(" ");
        sb.append(1);
        sb.append(", growx");
        String textConstraint = sb.toString();

        GuiTextField text = new GuiTextField(data, textConstraint);
        guiElements.add(text);
    }

    private void handleTextArea( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS);
        sb.append(" ");
        sb.append(1);
        sb.append(", growx");
        // sb.append(", growx");
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        String hint = extractSingleGuiHint(OmsBoxConstants.MULTILINE_UI_HINT, data.guiHints);
        String rowsStr = hint.replaceFirst(OmsBoxConstants.MULTILINE_UI_HINT, "");
        row[0] = row[0] + 1;
        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS);
        sb.append(" ");
        sb.append(rowsStr);
        row[0] = row[0] + Integer.parseInt(rowsStr);
        sb.append(", growx");
        String textConstraint = sb.toString();

        GuiTextField text = new GuiTextField(data, textConstraint);
        guiElements.add(text);
    }

    private void handleBooleanField( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        int labelCol = COLUMNS / 3;
        sb.append(labelCol);
        sb.append(" ");
        sb.append(1);
        // sb.append(", growx");
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(labelCol + 1);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS - labelCol);
        sb.append(" ");
        sb.append(1);
        sb.append(", growx");
        String textConstraint = sb.toString();

        GuiBooleanField booleanField = new GuiBooleanField(data, textConstraint);
        guiElements.add(booleanField);
    }

    private void handleGridcoverageInputField( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        int labelCol = COLUMNS / 3;
        sb.append(labelCol);
        sb.append(" ");
        sb.append(1);
        // sb.append(", growx");
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(labelCol + 1);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS - labelCol);
        sb.append(" ");
        sb.append(1);
        sb.append(", growx");
        String textConstraint = sb.toString();

        GuiCoverageInputField coverage = new GuiCoverageInputField(data, textConstraint);
        guiElements.add(coverage);
    }
    private void handleGridcoverageOutputField( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        int labelCol = COLUMNS / 3;
        sb.append(labelCol);
        sb.append(" ");
        sb.append(1);
        // sb.append(", growx");
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(labelCol + 1);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS - labelCol);
        sb.append(" ");
        sb.append(1);
        sb.append(", growx");
        String textConstraint = sb.toString();

        GuiCoverageOutputField coverage = new GuiCoverageOutputField(data, textConstraint);
        guiElements.add(coverage);
    }

    private void handleFeatureInputField( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        int labelCol = COLUMNS / 3;
        sb.append(labelCol);
        sb.append(" ");
        sb.append(1);
        // sb.append(", growx");
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(labelCol + 1);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS - labelCol);
        sb.append(" ");
        sb.append(1);
        sb.append(", growx");
        String textConstraint = sb.toString();

        GuiFeatureInputField feature = new GuiFeatureInputField(data, textConstraint);
        guiElements.add(feature);
    }
    private void handleFeatureOutputField( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        int labelCol = COLUMNS / 3;
        sb.append(labelCol);
        sb.append(" ");
        sb.append(1);
        // sb.append(", growx");
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(labelCol + 1);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS - labelCol);
        sb.append(" ");
        sb.append(1);
        sb.append(", growx");
        String textConstraint = sb.toString();

        GuiFeatureOutputField coverage = new GuiFeatureOutputField(data, textConstraint);
        guiElements.add(coverage);
    }

    private void handleHashMapInputField( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        int labelCol = COLUMNS / 3;
        sb.append(labelCol);
        sb.append(" ");
        sb.append(1);
        // sb.append(", growx");
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(labelCol + 1);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS - labelCol);
        sb.append(" ");
        sb.append(1);
        sb.append(", growx");
        String textConstraint = sb.toString();

        GuiHashMapInputField hashmap = new GuiHashMapInputField(data, textConstraint);
        guiElements.add(hashmap);
    }

    private void handleHashMapOutputField( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        int labelCol = COLUMNS / 3;
        sb.append(labelCol);
        sb.append(" ");
        sb.append(1);
        // sb.append(", growx");
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(labelCol + 1);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS - labelCol);
        sb.append(" ");
        sb.append(1);
        sb.append(", growx");
        String textConstraint = sb.toString();

        GuiHashMapOutputField hashmap = new GuiHashMapOutputField(data, textConstraint);
        guiElements.add(hashmap);
    }

    private void handleListInputField( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        int labelCol = COLUMNS / 3;
        sb.append(labelCol);
        sb.append(" ");
        sb.append(1);
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(labelCol + 1);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS - labelCol);
        sb.append(" ");
        sb.append(1);
        sb.append(", growx");
        String textConstraint = sb.toString();

        GuiListInputField hashmap = new GuiListInputField(data, textConstraint);
        guiElements.add(hashmap);
    }

    private void handleFilesPathListInputField( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS);
        sb.append(" ");
        sb.append(1);
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        row[0] = row[0] + 1;
        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS);
        sb.append(" ");
        sb.append(OmsBoxConstants.LISTHEIGHT);
        row[0] = row[0] + OmsBoxConstants.LISTHEIGHT;
        sb.append(", growx, growy");
        String textConstraint = sb.toString();

        GuiFilespathListInputField coverageList = new GuiFilespathListInputField(data, textConstraint);
        guiElements.add(coverageList);
    }

    private void handleListOutputField( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        int labelCol = COLUMNS / 3;
        sb.append(labelCol);
        sb.append(" ");
        sb.append(1);
        // sb.append(", growx");
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(labelCol + 1);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS - labelCol);
        sb.append(" ");
        sb.append(1);
        sb.append(", growx");
        String textConstraint = sb.toString();

        GuiListOutputField hashmap = new GuiListOutputField(data, textConstraint);
        guiElements.add(hashmap);
    }

    private void handleGridgeometryInputField( FieldData data, int[] row, List<ModuleGuiElement> guiElements ) {
        StringBuilder sb = new StringBuilder();
        sb.append("cell ");
        sb.append(0);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        int labelCol = COLUMNS / 3;
        sb.append(labelCol);
        sb.append(" ");
        sb.append(1);
        // sb.append(", growx");
        String labelConstraint = sb.toString();

        GuiLabel label = new GuiLabel(data, labelConstraint);
        guiElements.add(label);

        sb = new StringBuilder();
        sb.append("cell ");
        sb.append(labelCol + 1);
        sb.append(" ");
        sb.append(row[0]);
        sb.append(" ");
        sb.append(COLUMNS - labelCol);
        sb.append(" ");
        sb.append(1);
        sb.append(", growx");
        String textConstraint = sb.toString();

        GuiGridgeometryInputField gridgeometry = new GuiGridgeometryInputField(data, textConstraint);
        guiElements.add(gridgeometry);
    }
}
