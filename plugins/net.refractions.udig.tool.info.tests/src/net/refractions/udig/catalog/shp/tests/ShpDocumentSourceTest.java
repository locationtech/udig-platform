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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;
import net.refractions.udig.catalog.shp.ShpDocPropertyParser;
import net.refractions.udig.catalog.shp.ShpDocumentSource;

import org.geotools.data.shapefile.ShapefileDataStoreFactory;

/**
 * Test class for {@link ShpDocumentSource}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class ShpDocumentSourceTest extends AbstractShpDocTest {

    private ShpDocumentSource source;
    
    @Override
    protected void setUpInternal() {
        super.setUpInternal();
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url);
        
        List<DocumentInfo> inInfos = new ArrayList<DocumentInfo>();
        inInfos.add(fileDocInfo1);
        inInfos.add(webDocInfo1);
        parser.setShapeDocmentInfos(inInfos);
        parser.writeProperties();
        
        final Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, false);
        params.put(ShapefileDataStoreFactory.DBFCHARSET.key, "P_DEFAULT_CHARSET");
        
        final ShpServiceImpl service = new ShpServiceImpl(url, params);
        final ShpGeoResourceImpl geoResource = new ShpGeoResourceImpl(service, "");
        source = new ShpDocumentSource(geoResource);
        
    }
    
    public void testGet() {
        assertEquals("Count is not expected.", 2, source.getDocuments().size());
    }
    
    public void testAddRemove() {
        
        List<IDocument> docs = source.getDocuments();
        assertEquals("Count is not expected.", 2, docs.size());
        
        source.add(fileDocInfo2);
        assertEquals("Count is not expected.", 3, docs.size());

        source.remove(getDoc(docs, fileDocInfo2));
        assertEquals("Count is not expected.", 2, docs.size());

        List<DocumentInfo> inInfos = new ArrayList<DocumentInfo>();
        inInfos.add(fileDocInfo2);
        inInfos.add(webDocInfo2);

        source.add(inInfos);
        assertEquals("Count is not expected.", 4, docs.size());

        List<IDocument> inDocs = new ArrayList<IDocument>();
        inDocs.add(getDoc(docs, fileDocInfo1));
        inDocs.add(getDoc(docs, webDocInfo1));
        
        source.remove(inDocs);
        assertEquals("Count is not expected.", 2, docs.size());
        
    }
    
    public void testUpdateFile() {
        
        List<IDocument> docs = source.getDocuments();
        assertEquals("Count is not expected.", 2, docs.size());
        
        IDocument doc = getDoc(docs, fileDocInfo1);
        assertNotNull("Doc does not exists.", doc);
        
        source.update(doc, fileDocInfo2);
        doc = getDoc(docs, fileDocInfo1);
        assertNull("Doc exists.", doc);
        doc = getDoc(docs, fileDocInfo2);
        assertNotNull("Doc does not exists.", doc);
        
    }
    
}
