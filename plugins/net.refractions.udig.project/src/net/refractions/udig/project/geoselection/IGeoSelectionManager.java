package net.refractions.udig.project.geoselection;

import java.util.Iterator;

/**
 * DOCUMENT ME
 * 
 * @author Vitalus
 */
public interface IGeoSelectionManager {

    /**
     * DOCUMENT ME
     * 
     * @param listener
     */
    public void addListener( IGeoSelectionChangedListener listener );

    /**
     * DOCUMENT ME
     * 
     * @param listener
     */
    public void removeListener( IGeoSelectionChangedListener listener );

    /**
     * DOCUMENT ME
     * 
     * @param context
     * @param selection
     */
    public void setSelection( String context, IGeoSelection selection );

    /**
     * DOCUMENT ME
     * 
     * @param context
     * @return
     */
    public IGeoSelection getSelection( String context );
    
    
    public Iterator<IGeoSelectionEntry> getSelections();
    
    
    /**
     * 
     * This method returns a IGeoSelectionEntry with latest IGeoSelection has been
     * set to this  selection manager.
     * 
     * @return
     */
    public IGeoSelectionEntry getLatestSelection();
}
