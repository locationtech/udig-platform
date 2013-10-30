/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.printing.ui.internal.editor.figures;

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
