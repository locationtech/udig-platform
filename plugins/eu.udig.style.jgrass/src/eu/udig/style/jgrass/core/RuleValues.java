package eu.udig.style.jgrass.core;

import java.awt.Color;

import org.eclipse.swt.widgets.Display;

public class RuleValues {
    public double fromValue;
    public double toValue;
    public Color fromColor;
    public Color toColor;

    public static org.eclipse.swt.graphics.Color asSWT( Color awtColor ) {
        org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(Display
                .getDefault(), awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
        return color;
    }
}