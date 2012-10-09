/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
