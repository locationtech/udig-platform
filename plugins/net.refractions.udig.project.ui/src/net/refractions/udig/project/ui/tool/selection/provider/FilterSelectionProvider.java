/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.project.ui.tool.selection.provider;

import net.refractions.udig.core.filter.AdaptingFilter;
import net.refractions.udig.core.filter.AdaptingFilterFactory;
import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ILayerListener;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.LayerEvent;
import net.refractions.udig.project.LayerEvent.EventType;
import net.refractions.udig.project.ui.internal.MapPart;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;

import org.eclipse.jface.viewers.StructuredSelection;
import org.opengis.filter.Filter;

/**
 * A selection provider that provides as the current selection the currently selected layer's selection filter.
 * (map.getEditManager().getSelectedLayer().getFilter()).  The filter will be an Adaptable filter that adapts
 * to the layer that the filter is from.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class FilterSelectionProvider extends AbstractMapEditorSelectionProvider implements IMapEditorSelectionProvider {
    private ILayerListener layerListener=new ILayerListener(){

        public void refresh( LayerEvent event ) {
            if( event.getSource()!=map.getEditManager().getSelectedLayer()){
                event.getSource().removeListener(this);
            }
            if( event.getType()==EventType.FILTER ){
                AdaptingFilter filter = AdaptingFilterFactory.createAdaptingFilter((Filter) event.getNewValue(), event.getSource() );
                
                selection=new StructuredSelection( filter );
                notifyListeners();
            }
        }
    
    };
    
    private IMap map;
    
    private IEditManagerListener editManagerListener =  new IEditManagerListener(){

        public void changed( EditManagerEvent event ) {
        	
        	if(event.getSource().getMap() != map){
        		event.getSource().removeListener(this);
        		return;
        	}
            if( event.getType()== EditManagerEvent.SELECTED_LAYER ){
                ILayer oldLayer = (ILayer) event.getOldValue();
                if( oldLayer!=null )
                    oldLayer.removeListener(layerListener);
                ILayer selectedLayer = (ILayer) event.getNewValue();
                if( selectedLayer!=null ){
                    selectedLayer.addListener(layerListener);
                    AdaptingFilter filter = AdaptingFilterFactory.createAdaptingFilter(
                            selectedLayer.getFilter(), selectedLayer );                    
                    selection=new StructuredSelection( filter );
                }
                notifyListeners();
            }
        } 
    };
    


	public void setActiveMap( IMap map, MapPart editor ) {
        this.map=map;
		if( map==null || map.getMapLayers().size()==0){
			selection=new StructuredSelection();
			notifyListeners();
        }else{
			ILayer selectedLayer = map.getEditManager().getSelectedLayer();
            if( selectedLayer!=null ){
                selectedLayer.addListener(layerListener);
            
    			AdaptingFilter filter = AdaptingFilterFactory.createAdaptingFilter(
    			        selectedLayer.getFilter(), selectedLayer );
    			selection=new StructuredSelection(filter);
    			notifyListeners();
            }
	    }
	
        if(map != null && map.getEditManager() != null && 
            !map.getEditManager().containsListener(editManagerListener)) {
	        map.getEditManager().addListener(editManagerListener);
        }
    }

}
