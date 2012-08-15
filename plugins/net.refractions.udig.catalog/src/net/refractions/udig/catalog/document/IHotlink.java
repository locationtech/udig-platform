package net.refractions.udig.catalog.document;

import java.util.List;

import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;

/**
 * IDocument stored as a "hotlink" in the indicated {@link #getAttributeName()}.
 * 
 * @see IHotlinkSource
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public interface IHotlink extends IDocument {

    /**
     * Gets the attribute name of related to the document.
     * 
     * This is only used by documents from feature hotlinks.
     * 
     * @return attribute name
     */
    public String getAttributeName();
    
    /**
     * Gets the list of {@link HotlinkDescriptor} related to document.
     * 
     * @return list of descriptors
     */
    public List<HotlinkDescriptor> getDescriptors();
    
}
