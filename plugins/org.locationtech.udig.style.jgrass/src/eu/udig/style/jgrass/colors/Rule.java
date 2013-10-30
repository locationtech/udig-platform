/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
 package eu.udig.style.jgrass.colors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * This object holds everything needed to create a {@link RuleComposite}. This is needed since
 * {@link RuleComposite} have to be disposed when cleared and recreated when needed again.
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class Rule {
    private float[] fromToValues = null;
    private Color fromColor = null;
    private Color toColor = null;
    private boolean isActive = true;

    public Rule() {
        this.fromColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
        this.toColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
        this.fromToValues = new float[]{Float.NaN, Float.NaN};
        this.isActive = true;
    }

    public Rule( float[] fromToValues, Color fromColor, Color toColor, boolean isActive ) {
        this.fromColor = fromColor;
        this.toColor = toColor;
        this.fromToValues = fromToValues;
        this.isActive = isActive;
    }

    public float[] getFromToValues() {
        return fromToValues;
    }

    public Color getFromColor() {
        return fromColor;
    }

    public Color getToColor() {
        return toColor;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setFromColor( Color fromColor ) {
        this.fromColor = fromColor;
    }

    public void setFromToValues( float[] fromToValues ) {
        this.fromToValues = fromToValues;
    }

    public void setActive( boolean isActive ) {
        this.isActive = isActive;
    }

    public void setToColor( Color toColor ) {
        this.toColor = toColor;
    }

    /**
     * @return the GRASS definition of a color rule
     */
    public String ruleToString() {
        StringBuffer rule = new StringBuffer();
        rule.append(fromToValues[0] + ":");
        rule.append(fromColor.getRed() + ":" + fromColor.getGreen() + ":" + fromColor.getBlue()
                + " ");
        rule.append(fromToValues[1] + ":");
        rule.append(toColor.getRed() + ":" + toColor.getGreen() + ":" + toColor.getBlue() + " ");
        return rule.toString();
    }

}
