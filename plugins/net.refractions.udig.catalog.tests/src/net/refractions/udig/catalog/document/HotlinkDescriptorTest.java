/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.document;

import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import junit.framework.TestCase;

/**
 * Test class for {@link HotlinkDescriptor}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class HotlinkDescriptorTest extends TestCase {

    private static final String LABEL = "label";
    private static final String NAME = "attr";
    private static final Type TYPE = Type.WEB;
    private static final String CONFIG = "config";
    
    private static final String DELIMITER = HotlinkDescriptor.DELIMITER;
    private static final String FORMAT = "%s" + DELIMITER + "%s" + DELIMITER + "%s" + DELIMITER + "%s";
    
    /**
     * Tests if the string definition is parsed correctly.
     */
    public void testFromString() {

        String descStr = String.format(FORMAT, NAME, TYPE.toString(), CONFIG, LABEL);
        HotlinkDescriptor hotlinkDesc = new HotlinkDescriptor(descStr);
        assertEquals("Label is not expected.", NAME, hotlinkDesc.getAttributeName());
        assertEquals("Label is not expected.", TYPE, hotlinkDesc.getType());
        assertEquals("Label is not expected.", CONFIG, hotlinkDesc.getConfig());
        assertEquals("Label is not expected.", LABEL, hotlinkDesc.getLabel());
        
        descStr = String.format(FORMAT, NAME, TYPE.toString(), CONFIG, "");
        hotlinkDesc = new HotlinkDescriptor(descStr);
        assertEquals("Label is not expected.", NAME, hotlinkDesc.getAttributeName());
        assertEquals("Label is not expected.", TYPE, hotlinkDesc.getType());
        assertEquals("Label is not expected.", CONFIG, hotlinkDesc.getConfig());
        assertEquals("Label is not expected.",  null, hotlinkDesc.getLabel());
        
        descStr = String.format(FORMAT, NAME, TYPE.toString(), "", "");
        hotlinkDesc = new HotlinkDescriptor(descStr);
        assertEquals("Label is not expected.", NAME, hotlinkDesc.getAttributeName());
        assertEquals("Label is not expected.", TYPE, hotlinkDesc.getType());
        assertEquals("Label is not expected.", null, hotlinkDesc.getConfig());
        assertEquals("Label is not expected.",  null, hotlinkDesc.getLabel());
        
    }
    
    /**
     * Tests if the descriptor is encoded into a string correctly.
     */
    public void testToString() {
        
        String descStr = String.format(FORMAT, NAME, TYPE.toString(), CONFIG, LABEL);
        HotlinkDescriptor hotlinkDesc = new HotlinkDescriptor(LABEL, NAME, TYPE, CONFIG);
        assertEquals("Description string is not expected.", descStr, hotlinkDesc.toString());
        
        descStr = String.format(FORMAT, NAME, TYPE.toString(), CONFIG, "");
        hotlinkDesc = new HotlinkDescriptor(null, NAME, TYPE, CONFIG);
        assertEquals("Description string is not expected.", descStr, hotlinkDesc.toString());
        
        hotlinkDesc = new HotlinkDescriptor("", NAME, TYPE, CONFIG);
        assertEquals("Description string is not expected.", descStr, hotlinkDesc.toString());
        
        descStr = String.format(FORMAT, NAME, TYPE.toString(), "", "");
        hotlinkDesc = new HotlinkDescriptor(null, NAME, TYPE, null);
        assertEquals("Description string is not expected.", descStr, hotlinkDesc.toString());
        
        hotlinkDesc = new HotlinkDescriptor("", NAME, TYPE, "");
        assertEquals("Description string is not expected.", descStr, hotlinkDesc.toString());
        
    }
    
}
