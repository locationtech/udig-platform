package net.refractions.udig.catalog.document;

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
    
}
