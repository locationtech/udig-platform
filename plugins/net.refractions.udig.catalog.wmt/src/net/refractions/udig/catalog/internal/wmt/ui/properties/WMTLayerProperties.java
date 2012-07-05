/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.wmt.ui.properties;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import net.refractions.udig.catalog.internal.wmt.WMTService;
import net.refractions.udig.project.internal.StyleBlackboard;

public class WMTLayerProperties {
    private StyleBlackboard blackboard = null;
    
    private Boolean selectionAutomatic = null;
    private Integer zoomLevel = null;
    
    public WMTLayerProperties(StyleBlackboard blackboard) {
        this.blackboard = blackboard;
    }
    
    public Boolean getSelectionAutomatic() {
        return selectionAutomatic;
    }
    
    public Integer getZoomLevel() {
        return zoomLevel;
    }
    
    public void save(boolean selectionAutomatic, int zoomLevel) {
        IMemento memento = (IMemento) blackboard.get(WMTLayerStyleContent.EXTENSION_ID);
        if(memento == null ){
            memento = XMLMemento.createWriteRoot("WMTLayer"); //$NON-NLS-1$
        }
        
        memento.putBoolean(WMTService.KEY_PROPERTY_ZOOM_LEVEL_SELECTION_AUTOMATIC, selectionAutomatic);
        memento.putInteger(WMTService.KEY_PROPERTY_ZOOM_LEVEL_VALUE, zoomLevel);

        blackboard.put(WMTLayerStyleContent.EXTENSION_ID, memento);
    }
    
    public boolean load() {
        IMemento memento = (IMemento) blackboard.get(WMTLayerStyleContent.EXTENSION_ID);
        if (memento != null) {
            Integer zoomLevelRaw = memento.getInteger(WMTService.KEY_PROPERTY_ZOOM_LEVEL_VALUE);
            Boolean selectionAutomaticRaw = memento
                    .getBoolean(WMTService.KEY_PROPERTY_ZOOM_LEVEL_SELECTION_AUTOMATIC);

            if ((zoomLevelRaw != null) && (selectionAutomaticRaw != null)) {
                zoomLevel = zoomLevelRaw;
                selectionAutomatic = selectionAutomaticRaw;
                
                return true;
            } 
        } 
        
        return false;
    }
}
