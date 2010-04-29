package net.refractions.udig.project.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PartInitException;

/**
 * Contribute a panel for editing a specific feature type to the user interface.
 * <p>
 * Panels are expected to be displayed in a view (using a series of tabs) and also in a dialog
 * or wizard page.
 * 
 * @author Jody
 * @since 1.2.0
 */
public abstract class IFeaturePanel {
    /** extension point id **/
    public static final String XPID = "net.refractions.udig.project.ui.featurePanel"; //$NON-NLS-1$
    /**
     * Access to the feature being edited
     */
    private IFeatureSite site;
    /**
     * Returns the label describing the feature panel.
     * <p>
     * Used to represent the feature panel in a list, tab or wizard
     * dialog title.
     * @return A short name for this feature panel.
     */
    public abstract String getName();
    
    public abstract String getTitle();
    
    public abstract String getDescription();

    /**
     * Returns the site for this feature panel. 
     * 
     * @return EditManager until we figure the right thing
     */
    public IFeatureSite getSite(){
        return site;
    }
    /**
     * Initializes the feature panel with a site. 
     * <p>
     * This method is automatically shortly after the part is instantiated.
     * It marks the start of the panel's lifecycle.
     * <p>
     * Clients must not call this method.
     * </p>
     *
     * @param site Allows access to user interface facilities
     * @param memento Used to access any prior history recorded by this feature panel
     * @throws PartInitException 
     */
    public void init(IFeatureSite site, IMemento memento) throws PartInitException{
        init( site, memento );
    }     
    /**
     * Creates the control that is to be used to configure the style.
     * <p>
     * This method uses a template pattern to get the subclass to create
     * the control. This method will not be called until after init and
     * setViewPart. The parent container (composite) passed in is for the 
     * explicit use of the configurator, this method must set a layout for
     * the container. 
     * </p>  
     * <p>
     * You can set the layout of the parent to be whatever you want.
     * </p>
     * @param parent 
     */
    public abstract void createPartControl( Composite parent );    
    
    /**
     * Called when the panel is about to be shown.
     * <p>
     * This is your chance to listen to the user interface fields
     */
    public void aboutToBeShown() {
    }      
    
    /**
     * Called when the panel is about to be hidden.
     * <p>
     * This is your chance to stop listening to the user interface fields
     * </p>
     */
    public void aboutToBeHidden(){        
    }
    
    /**
     * Called to refresh screen contents; usually in response to a selection change.
     */
    public void refresh() {
    }
    /**
     * Cleans up any resources (like icons) held by this StyleConfigurator.
     * <p>
     * You should not assume that create, or even init has been called.
     * You must call super.dispose();
     * </p>
     */
    public void dispose(){
        // subclass should override
    }

    public boolean controlsHaveBeenCreated() {
        return false;
    }
 
}
