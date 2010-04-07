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

import net.refractions.udig.project.AdaptableFeature;
import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.internal.MapPart;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;

import org.eclipse.jface.viewers.StructuredSelection;

/**
 * A selection provider that provides as the current selection the currently selected feature.
 * (map.getEditManager().getEditFeature()).  The feature will be an Adaptable feature that adapts
 * to the layer that the feature is from.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class EditFeatureSelectionProvider extends AbstractMapEditorSelectionProvider implements IMapEditorSelectionProvider {

    public void setActiveMap( IMap map, MapPart editor ) {
        AdaptableFeature selectedFeature = (AdaptableFeature) map.getEditManager().getEditFeature();
        if( selectedFeature==null )
            selection=new StructuredSelection();
        else{
            selection=new StructuredSelection(selectedFeature);
        }
        
        map.getEditManager().addListener(new IEditManagerListener(){

            public void changed( EditManagerEvent event ) {
                if( event.getType()== EditManagerEvent.EDIT_FEATURE ){
                    AdaptableFeature selectedFeature = (AdaptableFeature) event.getNewValue();
                    if( selectedFeature==null )
                        selection=new StructuredSelection();
                    else{
                        selection=new StructuredSelection(selectedFeature);
                    }
                    notifyListeners();
                }
            }
            
        });
    }


}
