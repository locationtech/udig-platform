/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.geoselection;

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
