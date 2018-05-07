/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.ui;

import org.locationtech.udig.catalog.document.IAbstractDocumentSource;
import org.locationtech.udig.catalog.document.IAttachmentSource;
import org.locationtech.udig.catalog.document.IDocumentSource;
import org.locationtech.udig.catalog.document.IHotlinkSource;

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
        if (isValidSource(source)) {
            if (source instanceof IDocumentSource) {
                final IDocumentSource layerDocSource = (IDocumentSource) source;
                return layerDocSource.canAttach();
            } else if (source instanceof IAttachmentSource) {
                final IAttachmentSource featureDocSource = (IAttachmentSource) source;
                return featureDocSource.canAttach();
            }    
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
        if (isValidSource(source)) {
            if (source instanceof IDocumentSource) {
                final IDocumentSource layerDocSource = (IDocumentSource) source;
                return layerDocSource.canLinkFile();
            } else if (source instanceof IAttachmentSource) {
                final IAttachmentSource featureDocSource = (IAttachmentSource) source;
                return featureDocSource.canLinkFile();
            }    
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
        if (isValidSource(source)) {
            if (source instanceof IDocumentSource) {
                final IDocumentSource layerDocSource = (IDocumentSource) source;
                return layerDocSource.canLinkWeb();
            } else if (source instanceof IAttachmentSource) {
                final IAttachmentSource featureDocSource = (IAttachmentSource) source;
                return featureDocSource.canLinkWeb();
            }    
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
    public static boolean canUpdate(IAbstractDocumentSource source) {
        if (isValidSource(source)) {
            if (source instanceof IDocumentSource) {
                final IDocumentSource layerDocSource = (IDocumentSource) source;
                return layerDocSource.canUpdate();
            } else if (source instanceof IAttachmentSource) {
                final IAttachmentSource featureDocSource = (IAttachmentSource) source;
                return featureDocSource.canUpdate();
            } else if (source instanceof IHotlinkSource) {
                final IHotlinkSource hotlinkSource = (IHotlinkSource) source;
                return hotlinkSource.canSetHotlink();
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
    public static boolean canRemove(IAbstractDocumentSource source) {
        if (isValidSource(source)) {
            if (source instanceof IDocumentSource) {
                final IDocumentSource layerDocSource = (IDocumentSource) source;
                return layerDocSource.canRemove();
            } else if (source instanceof IAttachmentSource) {
                final IAttachmentSource featureDocSource = (IAttachmentSource) source;
                return featureDocSource.canRemove();
            } else if (source instanceof IHotlinkSource) {
                final IHotlinkSource hotlinkSource = (IHotlinkSource) source;
                return hotlinkSource.canClearHotlink();
            }    
        }
        return false;
    }
    
    /**
     * Checks if the source is not null and is enabled.
     * 
     * @param source
     * @return true if valid, otherwise false
     */
    private static boolean isValidSource(IAbstractDocumentSource source) {
        return (source != null && source.isEnabled());
    }
    
    
}
