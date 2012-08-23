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
package net.refractions.udig.catalog.shp.tests;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;

import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;
import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import net.refractions.udig.catalog.shp.ShpDocPropertyParser;

/**
 * Test class for {@link ShpDocPropertyParser}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class ShpDocPropertyParserTest extends AbstractShpDocTest {
    
    public void testHasProperties() throws MalformedURLException {
        
        ShpDocPropertyParser parser = new ShpDocPropertyParser(url);
        assertTrue("Property file does not exist.", parser.hasProperties());
        
        final URL url = new File(new File(DIRECTORY), "dummy.shp").toURI().toURL();
        parser = new ShpDocPropertyParser(url);
        assertFalse("Property file exist.", parser.hasProperties());
        
    }
    
    public void testSetGetShapeDocInfos() {
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url);

        List<DocumentInfo> inInfos = new ArrayList<DocumentInfo>();
        parser.setShapeDocmentInfos(inInfos);

        List<DocumentInfo> outInfos = parser.getShapeDocumentInfos();
        assertNull("Info list is not null.", outInfos);

        inInfos.add(fileDocInfo1);
        inInfos.add(webDocInfo1);
        parser.setShapeDocmentInfos(inInfos);

        outInfos = parser.getShapeDocumentInfos();
        assertNotNull("Info list is null.", outInfos);
        assertEquals("Info count is not expected.", 2, outInfos.size());

        inInfos.remove(fileDocInfo1);
        parser.setShapeDocmentInfos(inInfos);

        outInfos = parser.getShapeDocumentInfos();
        assertNotNull("Info list is null.", outInfos);
        assertEquals("Info count is not expected.", 1, outInfos.size());

        inInfos.add(fileDocInfo1);
        inInfos.add(fileDocInfo2);
        inInfos.add(webDocInfo2);
        parser.setShapeDocmentInfos(inInfos);

        outInfos = parser.getShapeDocumentInfos();
        assertNotNull("Info list is null.", outInfos);
        assertEquals("Info count is not expected.", 4, outInfos.size());
        
        inInfos.clear();
        parser.setShapeDocmentInfos(inInfos);

        outInfos = parser.getShapeDocumentInfos();
        assertNull("Info list is not null.", outInfos);
        
    }

    @SuppressWarnings("unchecked")
    public void testSetGetFeatureDocInfos() {
        
        SimpleFeatureTypeBuilder fb = new SimpleFeatureTypeBuilder();
        fb.setName("feature");
        SimpleFeature feature1 = SimpleFeatureBuilder.build(fb.buildFeatureType(),
                Collections.EMPTY_LIST, "feature.1");
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url);
        
        List<DocumentInfo> inInfos = new ArrayList<DocumentInfo>();
        parser.setFeatureDocumentInfos(feature1, inInfos);

        List<DocumentInfo> outInfos = parser.getFeatureDocumentInfos(feature1);
        assertNull("Info list is not null.", outInfos);

        inInfos.add(fileDocInfo1);
        inInfos.add(webDocInfo1);
        parser.setFeatureDocumentInfos(feature1, inInfos);

        outInfos = parser.getFeatureDocumentInfos(feature1);
        assertNotNull("Info list is null.", outInfos);
        assertEquals("Info count is not expected.", 2, outInfos.size());

        inInfos.remove(fileDocInfo1);
        parser.setFeatureDocumentInfos(feature1, inInfos);

        outInfos = parser.getFeatureDocumentInfos(feature1);
        assertNotNull("Info list is null.", outInfos);
        assertEquals("Info count is not expected.", 1, outInfos.size());
        
    }
    
    public void testSetGetFeatureHotlinkDescriptors() {
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url);
        
        List<HotlinkDescriptor> inInfos = new ArrayList<HotlinkDescriptor>();
        parser.setHotlinkDescriptors(inInfos);

        List<HotlinkDescriptor> outInfos = parser.getHotlinkDescriptors();
        assertNull("Info list is not null.", outInfos);

        inInfos.add(descriptor1);
        inInfos.add(descriptor2);
        parser.setHotlinkDescriptors(inInfos);

        outInfos = parser.getHotlinkDescriptors();
        assertNotNull("Info list is null.", outInfos);
        assertEquals("Info count is not expected.", 2, outInfos.size());
        
        inInfos.add(descriptor3);
        parser.setHotlinkDescriptors(inInfos);

        outInfos = parser.getHotlinkDescriptors();
        assertNotNull("Info list is null.", outInfos);
        assertEquals("Info count is not expected.", 3, outInfos.size());
        
    }
    
}
