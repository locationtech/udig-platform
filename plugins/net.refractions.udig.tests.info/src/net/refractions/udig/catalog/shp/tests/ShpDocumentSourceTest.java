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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;
import net.refractions.udig.document.source.ShpDocPropertyParser;
import net.refractions.udig.document.source.ShpDocumentSource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;

/**
 * Test class for {@link ShpDocumentSource}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class ShpDocumentSourceTest extends AbstractShpDocTest {

    private ShpDocPropertyParser parser;
    private ShpDocumentSource source;
    private File attachDir;
    
    private IProgressMonitor monitor;
    
    @Override
    protected void setUpInternal() {
        super.setUpInternal();
        
        monitor = new NullProgressMonitor();
        
        parser = new ShpDocPropertyParser(url);
        attachDir = parser.getShapefileAttachDir();
        
        final Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, false);
        params.put(ShapefileDataStoreFactory.DBFCHARSET.key, "P_DEFAULT_CHARSET");
        
        final ShpServiceImpl service = new ShpServiceImpl(url, params);
        final ShpGeoResourceImpl geoResource = new ShpGeoResourceImpl(service, "");
        source = new ShpDocumentSource(geoResource);
        
        cleaupAttachDir();
        
    }
    
    public void testGet() {
        assertEquals("Count is not expected.", 0, source.getDocuments(monitor).size());
    }
    
    public void testAddRemove() {
        
        List<IDocument> docs = source.getDocuments(monitor);
        assertEquals("Count is not expected.", 0, docs.size());
        
        source.add(fileDocInfo1, monitor);
        assertEquals("Count is not expected.", 1, docs.size());

        source.remove(getDoc(docs, fileDocInfo1), monitor);
        assertEquals("Count is not expected.", 0, docs.size());

        List<DocumentInfo> inInfos = new ArrayList<DocumentInfo>();
        inInfos.add(fileDocInfo2);
        inInfos.add(webDocInfo2);

        source.add(inInfos, monitor);
        assertEquals("Count is not expected.", 2, docs.size());

        List<IDocument> inDocs = new ArrayList<IDocument>();
        inDocs.add(getDoc(docs, fileDocInfo2));
        inDocs.add(getDoc(docs, webDocInfo2));
        
        source.remove(inDocs, monitor);
        assertEquals("Count is not expected.", 0, docs.size());
        
        cleaupAttachDir();
        
    }
    
    public void testUpdateFile() {
        
        List<IDocument> docs = source.getDocuments(monitor);
        assertEquals("Count is not expected.", 0, docs.size());
        
        source.add(fileDocInfo1, monitor);
        
        IDocument doc = getDoc(docs, fileDocInfo1);
        assertNotNull("Doc does not exists.", doc);
        
        source.update(doc, fileDocInfo2, monitor);
        doc = getDoc(docs, fileDocInfo1);
        assertNull("Doc exists.", doc);
        doc = getDoc(docs, fileDocInfo2);
        assertNotNull("Doc does not exists.", doc);
        
        cleaupAttachDir();
        
    }
    
    private void cleaupAttachDir() {
        
        parser.setShapeDocmentInfos(new ArrayList<DocumentInfo>());
        parser.writeProperties();
        
        if (attachDir.exists()) {
            for (File file : attachDir.listFiles()) {
                file.delete();
            }
            attachDir.delete();
        }
    }
    
}
