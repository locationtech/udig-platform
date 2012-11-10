/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit;

import java.util.List;

import net.refractions.udig.project.AdaptableFeature;
import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.MapPart;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.project.ui.tool.selection.provider.AbstractMapEditorSelectionProvider;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditBlackboardEvent;
import net.refractions.udig.tools.edit.support.EditBlackboardListener;

import org.eclipse.jface.viewers.StructuredSelection;

/**
 * The selection provided by this provider is first the vertices of the edit geom, if there are any selected.
 * 
 *  If not then it is the EditFeature in the edit manager.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class EditSelectionProvider extends AbstractMapEditorSelectionProvider {

    private EditBlackboardListener listener=new EditBlackboardListener(){

        public void changed( EditBlackboardEvent e ) {
            EditBlackboard old = EditBlackboardUtil.getEditBlackboard(context, map.getEditManager().getSelectedLayer());

            if( e.getEditBlackboard()!=old ){
                e.getEditBlackboard().getListeners().remove(this);
                return;
            }
            
            if( e.getType()==EditBlackboardEvent.EventType.SELECTION ){
                selection();
            }
        }

        public void batchChange( List<EditBlackboardEvent> e ) {
            EditBlackboardEvent lastSelecion=null;
            for( EditBlackboardEvent event : e ) {

                if( event.getType()==EditBlackboardEvent.EventType.SELECTION ){
                    lastSelecion=event;
                }
            }
            
            if( lastSelecion!=null )
                changed(lastSelecion);
        }
        
    };
    private IMap map;
    private final IEditManagerListener editManagerListener=new IEditManagerListener(){

        public void changed( EditManagerEvent event ) {
            if( event.getSource()!=map.getEditManager() ){
                event.getSource().removeListener(this);
                return;
            }
            switch( event.getType() ) {
            case EditManagerEvent.EDIT_FEATURE:
                AdaptableFeature selectedFeature = (AdaptableFeature) event.getNewValue();
                if( selectedFeature==null )
                    selection=new StructuredSelection();
                else{
                    selection=new StructuredSelection(selectedFeature);
                }
                notifyListeners();
                break;
            case EditManagerEvent.SELECTED_LAYER:
                if( event.getNewValue()==event.getOldValue() )
                    return;
                
                EditBlackboard old = EditBlackboardUtil.getEditBlackboard(context, (ILayer) event.getOldValue());
                old.getListeners().remove(listener);
                
                selection();
                break;
            default:
                break;
            }
        }
        
    };


    private IToolContext context;    
    
    public void setActiveMap(IMap map, MapPart editor) {
        this.map=map;
        context=ApplicationGIS.createContext(map);
        selection();

        map.getEditManager().addListener(editManagerListener);
        EditBlackboard editBlackboard = EditBlackboardUtil.getEditBlackboard(context, map.getEditManager().getSelectedLayer());
        editBlackboard.getListeners().add(listener);
    }

    /**
     *
     */
    private void selection() {
        if( !selectVertices() )
            selectEditFeature();
    }

    /**
     * Sets the selection to be a selection of vertices and returns true if there are any, otherwise
     * return false.
     */
    private boolean selectVertices( ) {
        EditBlackboard editBlackboard = EditBlackboardUtil.getEditBlackboard(context, map.getEditManager().getSelectedLayer());
        
        if( editBlackboard==null || editBlackboard.getSelection().isEmpty() )
            return false;
        selection=new StructuredSelection(editBlackboard.getSelection().toArray());

        notifyListeners();
        return true;
    }

    private void selectEditFeature( ) {

        AdaptableFeature selectedFeature = (AdaptableFeature) map.getEditManager().getEditFeature();
        if( selectedFeature==null )
            selection=new StructuredSelection();
        else{
            selection=new StructuredSelection(selectedFeature);
        }

        notifyListeners();
        
    }

}
