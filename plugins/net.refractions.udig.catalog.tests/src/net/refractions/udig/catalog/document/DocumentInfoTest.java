/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.document;

import static org.junit.Assert.assertEquals;
import net.refractions.udig.catalog.document.IDocument.ContentType;
import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;

import org.junit.Test;

/**
 * Test class for {@link DocumentInfo}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class DocumentInfoTest {

    private static final String LABEL = "label";
    private static final String DESCRIPTION = "description";
    private static final String INFO = "info";
    private static final Type TYPE = Type.ATTACHMENT;
    private static final ContentType CONTENT_TYPE = ContentType.FILE;
    private static final boolean IS_TEMPLATE = true;
    
    private static final String DELIMITER = DocumentInfo.DELIMITER;
    private static final String FORMAT = "%s" + DELIMITER + "%s" + DELIMITER + "%s" + DELIMITER
            + "%s" + DELIMITER + "%s" + DELIMITER + "%s";
    
    /**
     * Tests if the string definition is parsed correctly.
     */
    @Test
    public void testFromString() {

        String infoString = String.format(FORMAT, INFO, TYPE.toString(), CONTENT_TYPE.toString(),
                LABEL, DESCRIPTION, Boolean.toString(IS_TEMPLATE));
        DocumentInfo info = new DocumentInfo(infoString);
        assertEquals("Info is not expected.", INFO, info.getInfo());
        assertEquals("Type is not expected.", TYPE, info.getType());
        assertEquals("Content type is not expected.", CONTENT_TYPE, info.getContentType());
        assertEquals("Label is not expected.", LABEL, info.getLabel());
        assertEquals("Description is not expected.", DESCRIPTION, info.getDescription());
        assertEquals("Is template flag is not expected.", IS_TEMPLATE, info.isTemplate());

        infoString = String.format(FORMAT, INFO, TYPE.toString(), CONTENT_TYPE.toString(), "",
                DESCRIPTION, Boolean.toString(IS_TEMPLATE));
        info = new DocumentInfo(infoString);
        assertEquals("Info is not expected.", INFO, info.getInfo());
        assertEquals("Type is not expected.", TYPE, info.getType());
        assertEquals("Content type is not expected.", CONTENT_TYPE, info.getContentType());
        assertEquals("Label is not expected.", null, info.getLabel());
        assertEquals("Description is not expected.", DESCRIPTION, info.getDescription());
        assertEquals("Is template flag is not expected.", IS_TEMPLATE, info.isTemplate());

        infoString = String.format(FORMAT, INFO, TYPE.toString(), CONTENT_TYPE.toString(), "", "",
                Boolean.toString(IS_TEMPLATE));
        info = new DocumentInfo(infoString);
        assertEquals("Info is not expected.", INFO, info.getInfo());
        assertEquals("Type is not expected.", TYPE, info.getType());
        assertEquals("Content type is not expected.", CONTENT_TYPE, info.getContentType());
        assertEquals("Label is not expected.", null, info.getLabel());
        assertEquals("Description is not expected.", null, info.getDescription());
        assertEquals("Is template flag is not expected.", IS_TEMPLATE, info.isTemplate());
        
    }
    
    /**
     * Tests if the descriptor is encoded into a string correctly.
     */
    @Test
    public void testToString() {
        
        String infoString = String.format(FORMAT, INFO, TYPE.toString(), CONTENT_TYPE.toString(),
                LABEL, DESCRIPTION, Boolean.toString(IS_TEMPLATE));
        DocumentInfo hotlinkDesc = new DocumentInfo(LABEL, DESCRIPTION, INFO, CONTENT_TYPE,
                IS_TEMPLATE, TYPE);
        assertEquals("Description string is not expected.", infoString, hotlinkDesc.toString());

        infoString = String.format(FORMAT, INFO, TYPE.toString(), CONTENT_TYPE.toString(), "",
                DESCRIPTION, Boolean.toString(IS_TEMPLATE));
        hotlinkDesc = new DocumentInfo(null, DESCRIPTION, INFO, CONTENT_TYPE, IS_TEMPLATE, TYPE);
        assertEquals("Description string is not expected.", infoString, hotlinkDesc.toString());

        hotlinkDesc = new DocumentInfo("", DESCRIPTION, INFO, CONTENT_TYPE, IS_TEMPLATE, TYPE);
        assertEquals("Description string is not expected.", infoString, hotlinkDesc.toString());

        infoString = String.format(FORMAT, INFO, TYPE.toString(), CONTENT_TYPE.toString(), "", "",
                Boolean.toString(IS_TEMPLATE));
        hotlinkDesc = new DocumentInfo(null, null, INFO, CONTENT_TYPE, IS_TEMPLATE, TYPE);
        assertEquals("Description string is not expected.", infoString, hotlinkDesc.toString());

        hotlinkDesc = new DocumentInfo("", "", INFO, CONTENT_TYPE, IS_TEMPLATE, TYPE);
        assertEquals("Description string is not expected.", infoString, hotlinkDesc.toString());
        
    }
    
}
