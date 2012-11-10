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

/**
 * Base interface for document sources that support attachments and document linking.
 * 
 * @author Naz Chan
 */
public interface IAbstractAttachmentSource extends IAbstractDocumentSource {

    /**
     * Checks if the source allows attaching new documents.
     * 
     * @return true if allowed, otherwise false
     */
    public boolean canAttach();

    /**
     * Checks if the source allows linking file documents.
     * 
     * @return true if allowed, otherwise false
     */
    public boolean canLinkFile();

    /**
     * Checks if the source allows linking web documents.
     * 
     * @return true if allowed, otherwise false
     */
    public boolean canLinkWeb();

    /**
     * Checks if the source allows updating documents.
     * 
     * @return true if allowed, otherwise false
     */
    public boolean canUpdate();

    /**
     * Checks if the source allows removing documents.
     * 
     * @return true if allowed, otherwise false
     */
    public boolean canRemove();

}
