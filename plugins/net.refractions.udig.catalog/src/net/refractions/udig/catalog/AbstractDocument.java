/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.catalog;

import java.util.UUID;


/**
 * This is the abstract class for documents.
 * 
 * @author paul.pfeiffer
 * @author Naz Chan
 *
 */
public abstract class AbstractDocument extends AbstractDocumentItem implements IDocument {

    private UUID id;
    
    private IAbstractDocumentSource source;
    private IDocumentFolder folder;
    
    protected String label;
    private String attributeName;
    
    protected static final String UNASSIGNED = "unassigned"; //$NON-NLS-1$
    protected static final String UNASSIGNED_NO_LABEL = "(unassigned)"; //$NON-NLS-1$
    protected static final String LABEL_FORMAT = "%s (%s)"; //$NON-NLS-1$
    
    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getAttributeName() {
        return attributeName;
    }
    
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
    
    @Override
    public UUID getID() {
        return id;
    }
    
    public void setID(UUID id) {
        this.id = id;
    }
    
    @Override
    public IAbstractDocumentSource getSource() {
        return source;
    }

    public void setSource(IAbstractDocumentSource source) {
        this.source = source;
    }

    @Override
    public IDocumentFolder getFolder() {
        return folder;
    }

    public void setFolder(IDocumentFolder folder) {
        this.folder = folder;
    }
 
    @Override
    public boolean equals(Object obj) {
        if (isEmpty()) {
            return false;
        } else {
            if (obj instanceof AbstractDocument) {
                final AbstractDocument doc = (AbstractDocument) obj;
                if (!doc.isEmpty()) {
                    if (getURI().compareTo(doc.getURI()) == 0) {
                        return true;
                    }    
                }
                return false;
            }    
        }
        return super.equals(obj);
    }
    
}
