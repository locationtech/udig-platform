/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.style.sld.editor;

import javax.xml.transform.TransformerException;

import org.geotools.styling.SLDTransformer;
import org.geotools.styling.StyledLayerDescriptor;

/**
 * StyleEditor facade.
 */
public class StyleEditor {
    /**
     * Extension point processed for sld editor
     */
    public static final String ID = "net.refractions.udig.style.sld.editor"; //$NON-NLS-1$
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
