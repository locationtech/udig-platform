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
package org.locationtech.udig.printing.ui.internal.template;

import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;

/**
 * Implementation of an A3 size Template in landscape mode. 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class A3LandscapeTemplate extends AbstractPrinterPageTemplate {

    protected Rectangle getPaperSize() {
        Rectangle a3 = PageSize.A3;
        Rectangle a3Landscape = new Rectangle(0f, 0f, a3.getHeight(), a3.getWidth());
        return a3Landscape;
    }

    public String getAbbreviation() {
        return "A3L";
    }

}
