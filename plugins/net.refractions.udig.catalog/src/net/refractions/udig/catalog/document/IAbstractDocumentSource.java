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


/**
 * Base document source interface. This has methods for checking permission with regards to document
 * access and modification.
 * 
 * @author Naz Chan
 */
public interface IAbstractDocumentSource {

    /**
     * Checks if the source allows attaching new documents.
     * 
     * @return true if allows attaching, otherwise false
     */
    public boolean canAttach();
    
    /**
     * Checks if the source allows linking new documents.
     * 
     * @return true if allows linking, otherwise false
     */
    public boolean canLink();
    
    /**
     * Checks if the source allows updating documents.
     * 
     * @return true if allows updating, otherwise false
     */
    public boolean canUpdate();
    
    /**
     * Checks if the source allows removing documents for {@link IDocumentSource} and
     * {@link IAttachmentSource} or clearing for {@link IHotlinkSource}.
     * 
     * @return true if allows removing or clearing, otherwise false
     */
    public boolean canRemove();
    
}
