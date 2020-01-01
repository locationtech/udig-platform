/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor;

import javax.xml.transform.TransformerException;

import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.xml.styling.SLDTransformer;

/**
 * StyleEditor facade.
 */
public class StyleEditor {
    /**
     * Extension point processed for sld editor
     */
    public static final String ID = "org.locationtech.udig.style.sld.editor"; //$NON-NLS-1$
    /**
     * Amount to indent nested SLD documents
     */
    public static final int INDENT = 4;
    
    public static String styleToXML(StyledLayerDescriptor sld) {
        SLDTransformer aTransformer = new SLDTransformer();
        aTransformer.setIndentation(StyleEditor.INDENT);
        try {
            return aTransformer.transform(sld);
        } catch (TransformerException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
}
