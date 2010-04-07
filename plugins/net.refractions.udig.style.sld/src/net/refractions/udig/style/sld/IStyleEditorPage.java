package net.refractions.udig.style.sld;


/**
 * 
 * An interface for a page in the SLD Editor. This interface
 * is used primarily by the page's container, is pretty much 
 * identical to IPreferencePage. 
 * <p>
 *
 * </p>
 * @author chorner
 * @since 1.0.0
 */
public interface IStyleEditorPage extends IEditorPage {

    public IStyleEditorPageContainer getContainer();
    
}
