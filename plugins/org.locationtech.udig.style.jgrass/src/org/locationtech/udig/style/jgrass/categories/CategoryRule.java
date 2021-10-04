/**
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.jgrass.categories;

/**
 * This object holds everything needed to create a {@link CategoryRuleComposite}. This is needed
 * since {@link CategoryRuleComposite} have to be disposed when cleared and recreated when needed
 * again.
 *
 * @author Andrea Antonello - www.hydrologis.com
 */
public class CategoryRule {
    private String value = null;

    private String label = null;

    private boolean isActive = true;

    public CategoryRule() {
        this.value = ""; //$NON-NLS-1$
        this.label = ""; //$NON-NLS-1$
        this.isActive = true;
    }

    public CategoryRule(String value, String label, boolean isActive) {
        this.value = value;
        this.label = label;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the GRASS definition of a category rule
     */
    public String ruleToString() {
        StringBuffer rule = new StringBuffer();
        rule.append(value).append(":").append(label); //$NON-NLS-1$
        return rule.toString();
    }

}
