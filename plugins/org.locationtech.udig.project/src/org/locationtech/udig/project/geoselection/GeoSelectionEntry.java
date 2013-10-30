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

public class GeoSelectionEntry implements IGeoSelectionEntry {
    
    private String context;
    
    private IGeoSelection selection;
    
    
    public GeoSelectionEntry(String context){
        this.context = context;
    }
    
    public GeoSelectionEntry(String context, IGeoSelection selection){
        this.context = context;
        this.selection = selection;
    }
    

    public void setSelection( IGeoSelection selection ) {
        this.selection = selection;
    }

    public String getContext() {
        return context;
    }

    public IGeoSelection getSelection() {
        return selection;
    }

}
