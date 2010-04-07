package net.refractions.udig.project.geoselection;

import org.eclipse.core.runtime.ListenerList;

/**
 * General abstract implementation of <code>IGeoSelectionManager</code> interface.
 * 
 * @author vitalus
 * @version 0.1
 * @since UDIG 1.1
 */
public abstract class AbstractGeoSelectionManager implements IGeoSelectionManager {

    protected ListenerList listeners = new ListenerList();

    /**
     * Empty constructor
     */
    protected AbstractGeoSelectionManager() {

    }

    public void addListener( IGeoSelectionChangedListener listener ) {
        listeners.add(listener);
    }

    public void removeListener( IGeoSelectionChangedListener listener ) {
        listeners.remove(listener);
    }

}
