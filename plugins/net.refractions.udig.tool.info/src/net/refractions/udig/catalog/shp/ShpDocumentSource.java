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
package net.refractions.udig.catalog.shp;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocumentSource;
import net.refractions.udig.catalog.internal.document.AbstractBasicDocument;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;

/**
 * This is the shapefile document source implementation. This implements getters and setters to the
 * documents attached to the shapefile.
 * 
 * @author Naz Chan
 */
public class ShpDocumentSource extends AbstractShpDocumentSource implements IDocumentSource {

    private List<IDocument> docs;
    
    /**
     * Creates a new ShpDocumentSource
     * 
     * @param url of the existing .shp file
     * @throws Exception
     */
    public ShpDocumentSource(ShpGeoResourceImpl resource) {
        super(resource);
    }

    @Override
    public List<IDocument> getDocuments() {
        docs = new ArrayList<IDocument>();
        final List<DocumentInfo> infos = propParser.getShapeDocumentInfos();
        if (infos != null && infos.size() > 0) {
            docs.addAll(docFactory.create(infos));
        }
        return docs;
    }

    private List<IDocument> getDocsInternal() {
        if (docs == null) {
            return getDocuments();
        }
        return docs;
    }
        
    @Override
    public boolean canAdd() {
        return true;
    }

    @Override
    public IDocument add(DocumentInfo info) {
        final IDocument newDoc = docFactory.create(info);
        getDocsInternal().add(newDoc);
        save();
        return newDoc;
    }

    @Override
    public List<IDocument> add(List<DocumentInfo> infos) {
        final List<IDocument> newDocs = docFactory.create(infos);
        getDocsInternal().addAll(newDocs);
        save();
        return newDocs;
    }

    @Override
    public boolean canRemove() {
        return true;
    }

    @Override
    public boolean remove(IDocument oldDoc) {
        getDocsInternal().remove(oldDoc);
        save();
        return true;
    }

    @Override
    public boolean remove(List<IDocument> oldDocs) {
        getDocsInternal().removeAll(oldDocs);
        save();
        return true;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public IDocument update(IDocument doc, DocumentInfo info) {
        if (doc instanceof AbstractBasicDocument) {
            final AbstractBasicDocument updDoc = (AbstractBasicDocument) doc;
            updDoc.setInfo(info);
            save();
            return updDoc; 
        }
        return doc;
    }
 
    private void save() {
        final List<DocumentInfo> infos = new ArrayList<IDocumentSource.DocumentInfo>();
        for (IDocument doc : getDocsInternal()) {
            final AbstractBasicDocument shpDoc = (AbstractBasicDocument) doc;
            infos.add(shpDoc.getInfo());
        }
        propParser.setShapeDocmentInfos(infos);
        propParser.writeProperties();
    }
    
}
