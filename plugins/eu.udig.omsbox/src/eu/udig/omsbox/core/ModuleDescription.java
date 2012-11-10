/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.omsbox.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import oms3.annotations.Name;

/**
 * Description of a module.
 * 
 * <p>This is used to describe completely a module, so that a gui can be generated from it.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class ModuleDescription {

    public static enum Status {
        mature, experimental;
    }

    private final Class< ? > moduleClass;
    private String className;
    private String scriptName;
    private String name;
    private String category;
    private String description;
    private Status status;

    /**
     * The creation time of the object, to be used when in need to identify the object exactly.
     */
    private final int creationTime = (int) (System.currentTimeMillis() / 1000);

    /**
     * The list of input fields.
     */
    private List<FieldData> inputsList = new ArrayList<FieldData>();

    /**
     * The list of output fields.
     */
    private List<FieldData> outputsList = new ArrayList<FieldData>();

    private oms3.annotations.Status omsStatus;

    /**
     * Constructor.
     * 
     * @param moduleClass the class of the module.
     * @param category a category for the module. Can be nested through the use of pipes (ex. cat|subcat)
     * @param description a description of the module.
     * @param omsStatus the status of the module. Will end to define the maturity of the module.
     */
    public ModuleDescription( Class< ? > moduleClass, String category, String description, oms3.annotations.Status omsStatus ) {
        this.moduleClass = moduleClass;
        this.className = moduleClass.getCanonicalName();
        this.category = category;
        this.description = description;
        this.omsStatus = omsStatus;

        name = className.substring(className.lastIndexOf('.') + 1);

        Name nameAnn = moduleClass.getAnnotation(Name.class);
        if (nameAnn != null) {
            scriptName = nameAnn.value();
        } else {
            scriptName = className;
        }

        int statusValue = oms3.annotations.Status.DRAFT;
        if (omsStatus != null) {
            statusValue = omsStatus.value();
        };
        switch( statusValue ) {
        case oms3.annotations.Status.CERTIFIED:
        case oms3.annotations.Status.VALIDATED:
        case oms3.annotations.Status.TESTED:
            status = Status.mature;
            break;
        case oms3.annotations.Status.EXPERIMENTAL:
        case oms3.annotations.Status.DRAFT:
            status = Status.experimental;
            break;
        default:
            status = Status.experimental;
            break;
        }
    }

    /**
     * Adds an input field to the module.
     * 
     * @param fieldName the name of the field (accessed through reflection).
     * @param type the chanonical name of the class of the field.
     * @param description a description of the field.
     * @param defaultValue a default value or <code>null</code>.
     * @param uiHint
     */
    public void addInput( String fieldName, String type, String description, String defaultValue, String uiHint ) {
        if (fieldName == null) {
            throw new IllegalArgumentException("field name is mandatory");
        }
        if (type == null) {
            throw new IllegalArgumentException("field type is mandatory");
        }
        if (description == null) {
            description = "No description available";
        }

        FieldData fieldData = new FieldData();
        fieldData.isIn = true;
        fieldData.fieldName = fieldName;
        fieldData.fieldType = type;
        fieldData.fieldDescription = description;
        fieldData.fieldValue = defaultValue;
        fieldData.guiHints = uiHint;

        if (!inputsList.contains(fieldData)) {
            inputsList.add(fieldData);
        } else {
            throw new IllegalArgumentException("Duplicated field: " + fieldName);
        }
    }

    /**
     * Adds an output field to the module.
     * 
     * @param fieldName the name of the field (accessed through reflection).
     * @param type the chanonical name of the class of the field.
     * @param description a description of the field.
     * @param defaultValue a default value or <code>null</code>.
     * @param uiHint
     */
    public void addOutput( String fieldName, String type, String description, String defaultValue, String uiHint ) {
        if (fieldName == null) {
            throw new IllegalArgumentException("field name is mandatory");
        }
        if (type == null) {
            throw new IllegalArgumentException("field type is mandatory");
        }
        if (description == null) {
            description = "No description available";
        }

        FieldData fieldData = new FieldData();
        fieldData.isIn = false;
        fieldData.fieldName = fieldName;
        fieldData.fieldType = type;
        fieldData.fieldDescription = description;
        fieldData.fieldValue = defaultValue;
        fieldData.guiHints = uiHint;

        if (!outputsList.contains(fieldData)) {
            outputsList.add(fieldData);
        } else {
            throw new IllegalArgumentException("Duplicated field: " + fieldName);
        }
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public String getScriptName() {
        return scriptName;
    }

    // public Class< ? > getModuleClass() {
    // return moduleClass;
    // }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public List<FieldData> getInputsList() {
        // List<FieldData> cloneList = new ArrayList<FieldData>();
        // for( FieldData fieldData : inputsList ) {
        // cloneList.add(fieldData.makeCopy());
        // }
        // return cloneList;
        return inputsList;
    }

    public List<FieldData> getOutputsList() {
        // List<FieldData> cloneList = new ArrayList<FieldData>();
        // for( FieldData fieldData : outputsList ) {
        // cloneList.add(fieldData.makeCopy());
        // }
        // return cloneList;
        return outputsList;
    }

    public int getCreationTime() {
        return creationTime;
    }

    public ModuleDescription makeCopy() {
        ModuleDescription copy = new ModuleDescription(moduleClass, category, description, omsStatus);

        List<FieldData> inputsList2 = copy.getInputsList();
        for( FieldData inData : inputsList ) {
            inputsList2.add(inData.makeCopy());
        }

        List<FieldData> outputList2 = copy.getOutputsList();
        for( FieldData outData : outputsList ) {
            outputList2.add(outData.makeCopy());
        }

        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("*************************");
        sb.append("\nModule: ");
        sb.append(className);
        sb.append("\nCategory: ");
        sb.append(category);
        sb.append("\nDescription: ");
        sb.append(description);
        sb.append("\nStatus: ");
        sb.append(status);
        sb.append("\nInput fields: ");
        for( int i = 0; i < inputsList.size(); i++ ) {
            FieldData input = inputsList.get(i);
            sb.append("\n   -> Name: ");
            sb.append(input.fieldName);
            sb.append(", Description: ");
            sb.append(input.fieldDescription);
            sb.append(", Class: ");
            sb.append(input.fieldType);
        }
        sb.append("\nOutput fields: ");
        for( int i = 0; i < outputsList.size(); i++ ) {
            FieldData output = outputsList.get(i);
            sb.append("\n   -> Name: ");
            sb.append(output.fieldName);
            sb.append(", Description: ");
            sb.append(output.fieldDescription);
            sb.append(", Class: ");
            sb.append(output.fieldType);
        }
        sb.append("\n*************************\n");
        return sb.toString();
    }

    public static class ModuleDescriptionNameComparator implements Comparator<ModuleDescription> {
        public int compare( ModuleDescription m1, ModuleDescription m2 ) {
            String n1 = m1.getName();
            String n2 = m2.getName();
            return n1.compareTo(n2);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = creationTime;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((inputsList == null) ? 0 : inputsList.hashCode());
        if (inputsList != null)
            for( FieldData inData : inputsList ) {
                result = prime * result + inData.fieldValue.hashCode();
            }
        result = prime * result + ((outputsList == null) ? 0 : outputsList.hashCode());
        if (outputsList != null)
            for( FieldData outData : outputsList ) {
                result = prime * result + outData.fieldValue.hashCode();
            }
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return Math.abs(result);
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ModuleDescription other = (ModuleDescription) obj;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (inputsList == null) {
            if (other.inputsList != null)
                return false;
        } else if (!inputsList.equals(other.inputsList))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (outputsList == null) {
            if (other.outputsList != null)
                return false;
        } else if (!outputsList.equals(other.outputsList))
            return false;
        if (status != other.status)
            return false;
        if (creationTime != other.getCreationTime())
            return false;
        return true;
    }

}
