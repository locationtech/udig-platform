/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.style.advanced.raster;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * This object holds everything needed to create a {@link CoverageRuleComposite}. 
 * 
 * <p>This is needed since
 * {@link CoverageRuleComposite} have to be disposed when cleared and recreated when needed again.
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class CoverageRule {
    private double[] fromToValues = null;
    private Color fromColor = null;
    private Color toColor = null;
    private boolean isActive = true;
    private double opacity = 1.0;

    public CoverageRule() {
        this.fromColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
        this.toColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
        this.fromToValues = new double[]{Double.NaN, Double.NaN};
        this.isActive = true;
    }

    public CoverageRule( double[] fromToValues, Color fromColor, Color toColor, double opacity,
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

    public Color getFromColor() {
        return fromColor;
    }

    public Color getToColor() {
        return toColor;
    }

    public boolean isActive() {
        return isActive;
    }
    
    public double getOpacity() {
        return opacity;
    }

    public void setFromColor( Color fromColor ) {
        this.fromColor = fromColor;
    }

    public void setFromToValues( double[] fromToValues ) {
        this.fromToValues = fromToValues;
    }

    public void setActive( boolean isActive ) {
        this.isActive = isActive;
    }

    public void setToColor( Color toColor ) {
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
        rule.append(fromColor.getRed() + ":" + fromColor.getGreen() + ":" + fromColor.getBlue() //$NON-NLS-1$ //$NON-NLS-2$
                + " "); //$NON-NLS-1$
        rule.append(fromToValues[1] + ":"); //$NON-NLS-1$
        rule.append(toColor.getRed() + ":" + toColor.getGreen() + ":" + toColor.getBlue() + " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return rule.toString();
    }

}
