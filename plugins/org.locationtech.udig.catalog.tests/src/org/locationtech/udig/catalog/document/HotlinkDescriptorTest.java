/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.document;

import static org.junit.Assert.assertEquals;
import org.locationtech.udig.catalog.document.IDocument.ContentType;
import org.locationtech.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;

import org.junit.Test;

/**
 * Test class for {@link HotlinkDescriptor}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class HotlinkDescriptorTest {

    private static final String LABEL = "label";
    private static final String DESCRIPTION = "description";
    private static final String NAME = "attr";
    private static final ContentType TYPE = ContentType.WEB;
    private static final String CONFIG = "config";
    
    private static final String DELIMITER = HotlinkDescriptor.DELIMITER;
    private static final String FORMAT = "%s" + DELIMITER + "%s" + DELIMITER + "%s" + DELIMITER + "%s" + DELIMITER + "%s";
    
    /**
     * Tests if the string definition is parsed correctly.
     */
    @Test
    public void testFromString() {

        String descStr = String.format(FORMAT, NAME, TYPE.toString(), CONFIG, LABEL, DESCRIPTION);
        HotlinkDescriptor hotlinkDesc = new HotlinkDescriptor(descStr);
        assertEquals("Name is not expected.", NAME, hotlinkDesc.getAttributeName());
        assertEquals("Type is not expected.", TYPE, hotlinkDesc.getType());
        assertEquals("Config is not expected.", CONFIG, hotlinkDesc.getConfig());
        assertEquals("Label is not expected.", LABEL, hotlinkDesc.getLabel());
        assertEquals("Description is not expected.", DESCRIPTION, hotlinkDesc.getDescription());
        
        descStr = String.format(FORMAT, NAME, TYPE.toString(), CONFIG, "", DESCRIPTION);
        hotlinkDesc = new HotlinkDescriptor(descStr);
        assertEquals("Name is not expected.", NAME, hotlinkDesc.getAttributeName());
        assertEquals("Type is not expected.", TYPE, hotlinkDesc.getType());
        assertEquals("Config is not expected.", CONFIG, hotlinkDesc.getConfig());
        assertEquals("Label is not expected.",  null, hotlinkDesc.getLabel());
        assertEquals("Description is not expected.", DESCRIPTION, hotlinkDesc.getDescription());
        
        descStr = String.format(FORMAT, NAME, TYPE.toString(), "", "", DESCRIPTION);
        hotlinkDesc = new HotlinkDescriptor(descStr);
        assertEquals("Name is not expected.", NAME, hotlinkDesc.getAttributeName());
        assertEquals("Type is not expected.", TYPE, hotlinkDesc.getType());
        assertEquals("Config is not expected.", null, hotlinkDesc.getConfig());
        assertEquals("Label is not expected.",  null, hotlinkDesc.getLabel());
        assertEquals("Description is not expected.", DESCRIPTION, hotlinkDesc.getDescription());
        
    }
    
    /**
     * Tests if the descriptor is encoded into a string correctly.
     */
    @Test
    public void testToString() {
        
        String descStr = String.format(FORMAT, NAME, TYPE.toString(), CONFIG, LABEL, DESCRIPTION);
        HotlinkDescriptor hotlinkDesc = new HotlinkDescriptor(LABEL, DESCRIPTION, NAME, TYPE, CONFIG);
        assertEquals("Description string is not expected.", descStr, hotlinkDesc.toString());
        
        descStr = String.format(FORMAT, NAME, TYPE.toString(), CONFIG, "", DESCRIPTION);
        hotlinkDesc = new HotlinkDescriptor(null, DESCRIPTION, NAME, TYPE, CONFIG);
        assertEquals("Description string is not expected.", descStr, hotlinkDesc.toString());
        
        hotlinkDesc = new HotlinkDescriptor("", DESCRIPTION, NAME, TYPE, CONFIG);
        assertEquals("Description string is not expected.", descStr, hotlinkDesc.toString());
        
        descStr = String.format(FORMAT, NAME, TYPE.toString(), "", "", DESCRIPTION);
        hotlinkDesc = new HotlinkDescriptor(null, DESCRIPTION, NAME, TYPE, null);
        assertEquals("Description string is not expected.", descStr, hotlinkDesc.toString());
        
        hotlinkDesc = new HotlinkDescriptor("", DESCRIPTION, NAME, TYPE, "");
        assertEquals("Description string is not expected.", descStr, hotlinkDesc.toString());
        
    }
    
}
