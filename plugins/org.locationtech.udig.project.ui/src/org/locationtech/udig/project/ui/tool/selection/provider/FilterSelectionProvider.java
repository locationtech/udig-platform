/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.tool.selection.provider;

import org.eclipse.jface.viewers.StructuredSelection;
import org.locationtech.udig.core.filter.AdaptingFilter;
import org.locationtech.udig.core.filter.AdaptingFilterFactory;
import org.locationtech.udig.project.EditManagerEvent;
import org.locationtech.udig.project.IEditManagerListener;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILayerListener;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.LayerEvent;
import org.locationtech.udig.project.LayerEvent.EventType;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.tool.IMapEditorSelectionProvider;
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
