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
