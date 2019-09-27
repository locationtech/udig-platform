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
import org.locationtech.udig.project.AdaptableFeature;
import org.locationtech.udig.project.EditManagerEvent;
import org.locationtech.udig.project.IEditManagerListener;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.tool.IMapEditorSelectionProvider;

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
