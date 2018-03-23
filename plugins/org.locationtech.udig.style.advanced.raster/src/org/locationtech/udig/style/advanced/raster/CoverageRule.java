/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.raster;

import org.eclipse.swt.graphics.RGB;

/**
 * This object holds everything needed to create a {@link CoverageRuleComposite}. 
 * 
 * <p>This is needed since
 * {@link CoverageRuleComposite} have to be disposed when cleared and recreated when needed again.
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class CoverageRule {
    
    private static final RGB BLACK_DEFAULT_RGB = new RGB(0, 0, 0);
    private double[] fromToValues = null;
    private RGB fromColor = null;
    private RGB toColor = null;
    private boolean isActive = true;
    private double opacity = 1.0;

    public CoverageRule() {
        this(new double[]{Double.NaN, Double.NaN}, BLACK_DEFAULT_RGB, BLACK_DEFAULT_RGB, 1.0, true);
    }

    public CoverageRule( double[] fromToValues, RGB fromColor, RGB toColor, double opacity,
            boolean isActive ) {
        this.fromColor = fromColor;
        this.toColor = toColor;
        this.fromToValues = fromToValues;
        this.opacity = opacity;
        this.isActive = isActive;
    }

    public double[] getFromToValues() {
        return fromToValues;
    }

    public RGB getFromColor() {
        return fromColor;
    }

    public RGB getToColor() {
        return toColor;
    }

    public boolean isActive() {
        return isActive;
    }
    
    public double getOpacity() {
        return opacity;
    }

    public void setFromColor( RGB fromColor ) {
        this.fromColor = fromColor;
    }

    public void setFromToValues( double[] fromToValues ) {
        this.fromToValues = fromToValues;
    }

    public void setActive( boolean isActive ) {
        this.isActive = isActive;
    }

    public void setToColor( RGB toColor ) {
        this.toColor = toColor;
    }
    
    public void setOpacity( double opacity ) {
        this.opacity = opacity;
    }

    /**
     * @return the GRASS definition of a color rule
     */
    public String ruleToString() {
        StringBuffer rule = new StringBuffer();
        rule.append(fromToValues[0] + ":"); //$NON-NLS-1$
        if (fromColor != null) {
            rule.append(fromColor.red + ":" + fromColor.green + ":" + fromColor.blue //$NON-NLS-1$ //$NON-NLS-2$
                + " "); //$NON-NLS-1$
        } 
        rule.append(fromToValues[1] + ":"); //$NON-NLS-1$
        rule.append(toColor.red + ":" + toColor.green + ":" + toColor.blue + " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return rule.toString();
    }

}
