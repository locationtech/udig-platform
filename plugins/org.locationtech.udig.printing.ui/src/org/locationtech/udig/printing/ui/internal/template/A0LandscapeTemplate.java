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
package net.refractions.udig.printing.ui.internal.template;

import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;

/**
 * Implementation of an A0 size Template in landscape mode. 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class A0LandscapeTemplate extends AbstractPrinterPageTemplate {

    protected Rectangle getPaperSize() {
        Rectangle a0 = PageSize.A0;
        Rectangle a0Landscape = new Rectangle(0f, 0f, a0.getHeight(), a0.getWidth());
        return a0Landscape;
    }

    public String getAbbreviation() {
        return "A0L";
    }

}
