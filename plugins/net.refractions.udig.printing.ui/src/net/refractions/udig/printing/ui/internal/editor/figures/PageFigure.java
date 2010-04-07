/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.printing.ui.internal.editor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * The visual component for a page
 * @author Richard Gould
 * @author jesse
 * @since 0.3
 */
public class PageFigure extends Layer {

    private static final int INSETS = 0;

    public PageFigure( Dimension size ) {
        int twoInsets = INSETS * 2;
        setBounds(new Rectangle(INSETS, INSETS, size.width + twoInsets, size.height + twoInsets));
        setBorder(new LineBorder(ColorConstants.gray, INSETS));
    }
}
