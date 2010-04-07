package net.refractions.udig.ui;


import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * 
 * Defines the behaviour when a drag and drop event occurs.  The extension declaration combined with the accepts method
 * determines whether the Action will be ran for a given drag/drop event.
 * 
 * <p>If the enablesFor is a anything other than null or 1 then an array of objects (all the same type)
 * will be the data returned by getData, otherwise the data be a single object (not an array).
 * 
 * @author jdeolive
 * @since 1.0.0
 */
public abstract class IDropAction {
	
	public static final String XPID = "net.refractions.udig.ui.dropAction"; //$NON-NLS-1$
	
	/** the extension info **/
	IConfigurationElement element;
	
    private Object destination;

    private Object data;

    private DropTargetEvent event;

    private ViewerDropLocation location; 
	
    /**
     * Returns the Configuration element that definates the action in the extension declaration.
     *
     * @return the Configuration element that definates the action in the extension declaration.
     */
	public IConfigurationElement getElement() {
		return element;
	}
	
    /**
     * Returns the name of the action.
     *
     * @return Returns the name of the action.
     */
	public String getName() {
		if (element == null)
			return ""; //$NON-NLS-1$
		return element.getAttribute("name"); //$NON-NLS-1$
	}
	
    /**
     * Called if the action seems to be a good candidate for handling the drop event.  
     * 
     * @see #getData();
     * @see #getData()
     * @see #getEvent()
     *
     * @return true if the action this it should handle the drop event.
     */
    public abstract boolean accept();
	
    /**
     * Performs the drop action.
     *
     * @param monitor a progress monitor for showing the progress of the current event.
     */
	public abstract void perform(IProgressMonitor monitor);

    /**
     * Returns the dropped data.  If the enablesFor is a anything other than null or 1 then an array of objects (all the same type)
     * will be the data, otherwise the data will not be in an array.
     *
     * @return the dropped data.
     */
    public Object getData() {
        return data;
    }

    /**
     * Returns the object that the data was dropped on.
     *
     * @return the object that the data was dropped on.
     */
    public Object getDestination() {
        return destination;
    }

    /**
     * Called by framework to initialize the action.
     *
     * @param element2 the extension configuration element
     * @param event2 the drop event.
     * @param location wrt the destination.    
     * @param destination2 the object that the data was dropped on.
     * @param data2 the data that was dropped.
     */
    public void init( IConfigurationElement element2, DropTargetEvent event2, ViewerDropLocation location2,
            Object destination2, Object data2 ) {
        this.element=element2;
        this.event=event2;
        this.location=location2;
        this.destination=destination2;
        this.data=data2;
    }

    /**
     * The drop event.
     * @return Returns the drop event.
     */
    public DropTargetEvent getEvent() {
        return event;
    }

    /**
     * This does not always make sense for the drop event.  But in the case that the component is a jface viewer then this method will indicate the
     * location where the drop is taking place (before, on or after the destination object).
     *
     * @see ViewerDropAdapter#getCurrentLocation()
     * 
     * @return If the component is not a viewer this will always return {@link ViewerDropAdapter#LOCATION_ON} otherwise it will be one of the LOCATION_* 
     * values. 
     */
    public ViewerDropLocation getViewerLocation() {
        return location;
    }

}