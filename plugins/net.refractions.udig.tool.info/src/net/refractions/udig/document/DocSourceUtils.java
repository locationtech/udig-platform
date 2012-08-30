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
package net.refractions.udig.document;

import net.refractions.udig.catalog.document.IAbstractDocumentSource;
import net.refractions.udig.catalog.document.IAttachmentSource;
import net.refractions.udig.catalog.document.IDocumentSource;
import net.refractions.udig.catalog.document.IHotlinkSource;

/**
 * Utility methods to access document sources.
 * 
 * @author Naz Chan
 */
public final class DocSourceUtils {

    /**
     * Checks if the document source allows attaching new documents.
     * 
     * @param source
     * @return true if allows, otherwise false
     */
    public static boolean canAttach(IAbstractDocumentSource source) {
        if (source instanceof IDocumentSource) {
            final IDocumentSource layerDocSource = (IDocumentSource) source;
            return layerDocSource.canAttach();
        } else if (source instanceof IAttachmentSource) {
            final IAttachmentSource featureDocSource = (IAttachmentSource) source;
            return featureDocSource.canAttach();
        }
        return false;
    }
    
    /**
     * Checks if the document source allows linking new documents.
     * 
     * @param source
     * @return true if allows, otherwise false
     */
    public static boolean canLink(IAbstractDocumentSource source) {
        return canLinkFile(source) || canLinkWeb(source);
    }
    
    /**
     * Checks if the document source allows linking new file documents.
     * 
     * @param source
     * @return true if allows, otherwise false
     */
    public static boolean canLinkFile(IAbstractDocumentSource source) {
        if (source instanceof IDocumentSource) {
            final IDocumentSource layerDocSource = (IDocumentSource) source;
            return layerDocSource.canLinkFile();
        } else if (source instanceof IAttachmentSource) {
            final IAttachmentSource featureDocSource = (IAttachmentSource) source;
            return featureDocSource.canLinkFile();
        }
        return false;
    }
    
    /**
     * Checks if the document source allows linking new web documents.
     * 
     * @param source
     * @return true if allows, otherwise false
     */
    public static boolean canLinkWeb(IAbstractDocumentSource source) {
        if (source instanceof IDocumentSource) {
            final IDocumentSource layerDocSource = (IDocumentSource) source;
            return layerDocSource.canLinkWeb();
        } else if (source instanceof IAttachmentSource) {
            final IAttachmentSource featureDocSource = (IAttachmentSource) source;
            return featureDocSource.canLinkWeb();
        }
        return false;
    }
    
    /**
     * Checks if the document source allows updating documents or setting hotlinks.
     * 
     * @param source
     * @param isHotlink
     * @return true if allows, otherwise false
     */
    public static boolean canUpdate(IAbstractDocumentSource source, boolean isHotlink) {
        if (isHotlink) {
            final IHotlinkSource hotlinkSource = (IHotlinkSource) source;
            return hotlinkSource.canSetHotlink();
        } else {
            if (source instanceof IDocumentSource) {
                final IDocumentSource layerDocSource = (IDocumentSource) source;
                return layerDocSource.canUpdate();
            } else if (source instanceof IAttachmentSource) {
                final IAttachmentSource featureDocSource = (IAttachmentSource) source;
                return featureDocSource.canUpdate();
            }    
        }
        return false;
    }
    
    /**
     * Checks if the document source allows removing documents or clearing hotlinks.
     * 
     * @param source
     * @param isHotlink
     * @return true if allows, otherwise false
     */
    public static boolean canRemove(IAbstractDocumentSource source, boolean isHotlink) {
        if (isHotlink) {
            final IHotlinkSource hotlinkSource = (IHotlinkSource) source;
            return hotlinkSource.canClearHotlink();
        } else {
            if (source instanceof IDocumentSource) {
                final IDocumentSource layerDocSource = (IDocumentSource) source;
                return layerDocSource.canRemove();
            } else if (source instanceof IAttachmentSource) {
                final IAttachmentSource featureDocSource = (IAttachmentSource) source;
                return featureDocSource.canRemove();
            }    
        }
        return false;
    }
    
}
