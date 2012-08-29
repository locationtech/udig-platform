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

import junit.framework.TestCase;
import net.refractions.udig.catalog.document.IDocument.ContentType;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;

/**
 * Test class for {@link DocumentInfo}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class DocumentInfoTest extends TestCase {

    private static final String LABEL = "label";
    private static final String DESCRIPTION = "description";
    private static final String INFO = "info";
    private static final ContentType TYPE = ContentType.FILE;
    private static final boolean IS_TEMPLATE = true;
    
    private static final String DELIMITER = DocumentInfo.DELIMITER;
    private static final String FORMAT = "%s" + DELIMITER + "%s" + DELIMITER + "%s" + DELIMITER + "%s" + DELIMITER + "%s";
    
    /**
     * Tests if the string definition is parsed correctly.
     */
    public void testFromString() {

        String infoString = String.format(FORMAT, INFO, TYPE.toString(), LABEL, DESCRIPTION, Boolean.toString(IS_TEMPLATE));
        DocumentInfo info = new DocumentInfo(infoString);
        assertEquals("Info is not expected.", INFO, info.getInfo());
        assertEquals("Type is not expected.", TYPE, info.getType());
        assertEquals("Label is not expected.", LABEL, info.getLabel());
        assertEquals("Description is not expected.", DESCRIPTION, info.getDescription());
        assertEquals("Is template flag is not expected.", IS_TEMPLATE, info.isTemplate());
        
        infoString = String.format(FORMAT, INFO, TYPE.toString(), "", DESCRIPTION, Boolean.toString(IS_TEMPLATE));
        info = new DocumentInfo(infoString);
        assertEquals("Info is not expected.", INFO, info.getInfo());
        assertEquals("Type is not expected.", TYPE, info.getType());
        assertEquals("Label is not expected.", null, info.getLabel());
        assertEquals("Description is not expected.", DESCRIPTION, info.getDescription());
        assertEquals("Is template flag is not expected.", IS_TEMPLATE, info.isTemplate());
        
        infoString = String.format(FORMAT, INFO, TYPE.toString(), "", "", Boolean.toString(IS_TEMPLATE));
        info = new DocumentInfo(infoString);
        assertEquals("Info is not expected.", INFO, info.getInfo());
        assertEquals("Type is not expected.", TYPE, info.getType());
        assertEquals("Label is not expected.", null, info.getLabel());
        assertEquals("Description is not expected.", null, info.getDescription());
        assertEquals("Is template flag is not expected.", IS_TEMPLATE, info.isTemplate());
        
    }
    
    /**
     * Tests if the descriptor is encoded into a string correctly.
     */
    public void testToString() {
        
        String infoString = String.format(FORMAT, INFO, TYPE.toString(), LABEL, DESCRIPTION, Boolean.toString(IS_TEMPLATE));
        DocumentInfo hotlinkDesc = new DocumentInfo(LABEL, DESCRIPTION, INFO, TYPE, IS_TEMPLATE);
        assertEquals("Description string is not expected.", infoString, hotlinkDesc.toString());
        
        infoString = String.format(FORMAT, INFO, TYPE.toString(), "", DESCRIPTION, Boolean.toString(IS_TEMPLATE));
        hotlinkDesc = new DocumentInfo(null, DESCRIPTION, INFO, TYPE, IS_TEMPLATE);
        assertEquals("Description string is not expected.", infoString, hotlinkDesc.toString());
        
        hotlinkDesc = new DocumentInfo("", DESCRIPTION, INFO, TYPE, IS_TEMPLATE);
        assertEquals("Description string is not expected.", infoString, hotlinkDesc.toString());
        
        infoString = String.format(FORMAT, INFO, TYPE.toString(), "", "", Boolean.toString(IS_TEMPLATE));
        hotlinkDesc = new DocumentInfo(null, null, INFO, TYPE, IS_TEMPLATE);
        assertEquals("Description string is not expected.", infoString, hotlinkDesc.toString());
        
        hotlinkDesc = new DocumentInfo("", "", INFO, TYPE, IS_TEMPLATE);
        assertEquals("Description string is not expected.", infoString, hotlinkDesc.toString());
        
    }
    
}
