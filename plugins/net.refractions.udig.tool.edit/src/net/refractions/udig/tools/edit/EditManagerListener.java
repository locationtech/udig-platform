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
import java.util.concurrent.locks.Lock;

import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.tool.Tool;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

import org.opengis.filter.Filter;

/** 
 * If a commit occurs it:
 * <ul>
 * <li>Pre Commit:
 * <ul><li>Runs the accept command so the current changes are committed</li>
 * <li> XXX: Should it run it for each layer?</li>
 * </ul>
 * </li>
 * <li>Post Commit:
 * <ul>
 * <li>Clears all edit blackboards</li>
 * <li>Clears the current edit shape and edit state</li>
 * <li>Clears the hide selected layer filter on all layers... see {@link EditUtils#cancelHideSelection(ILayer)}</li>
 * <li>If not currently a edit tool then stop listening</li>
 * </ul>
 * </li
 * <li>Pre Rollback:
 * <ul>
 * <li>Clears all edit blackboards</li>
 * <li>Clears the current edit shape and edit state</li>
 * <li>Clears the hide selected layer filter on all layers... see {@link EditUtils#cancelHideSelection(ILayer)}</li>
 * </ul>
 * </li>
 * <li>Post Rollback:
 * <ul>
 * <li>If not currently a edit tool then stop listening</li>
 * </ul>
 * </li<
 * <li>Selected Layer Change:
 * <ul>
 * <li>This is handled by {@link EditToolHandler} in the listenToSelectedLayer method.</li>
 * </ul>
 * </li<
 * </ul> 
 */
class EditManagerListener implements IEditManagerListener {

    private static final String EDITING_EDIT_MANAGER_LISTENER = "EDITING_EDIT_MANAGER_LISTENER_234234567"; //$NON-NLS-1$
    // Not used yet.  I think it is for when
    // the EditBlackboardUtil class listens to edit events and
    // updates the editblackboards.
    volatile static IEditManager committing=null;

    /**
     * Enable listener that listens for commit, rollback and selected layer changes.
     *
     * @param editManager
     * @see #disableEditManagerListener()
     */
    static synchronized void enableEditManagerListener( EditToolHandler handler ) {
        IEditManager editManager=handler.getContext().getEditManager();
        
        // add listener if editManager is not null and map blackboard indicates that a listener has not be previously added
        if ( getRegisteredListener(editManager)==null ) {
            EditManagerListener editManagerListener = new EditManagerListener(editManager,handler);
            editManager.addListener(editManagerListener);
            editManager.getMap().getBlackboard().put(EDITING_EDIT_MANAGER_LISTENER, editManagerListener);
        }else{
            getRegisteredListener(editManager).handler=handler;
        }
    }
    private static EditManagerListener getRegisteredListener( final IEditManager editManager ) {
        return (EditManagerListener) editManager.getMap().getBlackboard().get(EDITING_EDIT_MANAGER_LISTENER);
    }

    IEditManager editManager;
    private EditToolHandler handler;
    EditManagerListener( IEditManager em, EditToolHandler handler2 ) {
        this.editManager = em;
        this.handler=handler2;
    }
    public IEditManager getEditManager() {
        return editManager;
    }
    public void changed( EditManagerEvent event ) {
        synchronized (this) {
            if (getRegisteredListener(editManager) == null || getRegisteredListener(editManager) != this) {
                event.getSource().removeListener(this);
                return;
            }
        }

        ILayer selectedLayer = event.getSource().getSelectedLayer();
        switch( event.getType() ) {
        case EditManagerEvent.PRE_COMMIT:{
            Tool tool = ApplicationGIS.getToolManager().getActiveTool();
            if (tool instanceof AbstractEditTool) {
                AbstractEditTool aet = (AbstractEditTool) tool;
                PrimitiveShape shape = aet.getHandler().getCurrentShape();
                if (shape != null && shape.getEditGeom().isChanged())
                    aet.getContext()
                            .sendSyncCommand(
                                    aet.getHandler().getCommand(
                                            aet.getHandler().getAcceptBehaviours()));
                committing=event.getSource();
            }
            break;
        }case EditManagerEvent.POST_COMMIT:{
            committing=null;
            stopListening();

            resetEditState(selectedLayer);
            break;
        }case EditManagerEvent.PRE_ROLLBACK:
            resetEditState(selectedLayer);
            committing=event.getSource();
            break;
        case EditManagerEvent.POST_ROLLBACK:
            stopListening();
            committing=null;
            break;
        case EditManagerEvent.SELECTED_LAYER:
            ILayer oldValue = (ILayer) event.getOldValue();
            ILayer newValue = (ILayer)event.getNewValue();
            
            Lock lock2 = handler.getLock();
            lock2.lock();
            try {
                if (handler.getCurrentState() != EditState.BUSY){
                    // TODO what to do when busy?  Should probably wait until not busy?
                }
                if( handler.getCurrentState()!=EditState.ILLEGAL ){
                    storeCurrentState(oldValue);  
                    getPreviousStateFromLayer(newValue);
                    
                    storeCurrentShape(oldValue);
                    setCurrentShape(newValue);
                }
            } finally {
                lock2.unlock();
            }
            if( !active() )
                return;

            if (ApplicationGIS.getOpenMaps().contains(newValue.getMap())) {
                EditUtils.instance.cancelHideSelection(oldValue);
                EditUtils.instance.hideSelectedFeatures(handler, newValue);
            }
//            Display.getDefault().asyncExec(new Runnable(){
//
//                public void run() {
//                    handler.basicDisablement();
//                    handler.basicEnablement();
//                }
//                
//            });
            break;

        default:
            break;
        }
    }

    private boolean active() {
        return ApplicationGIS.getToolManager().getActiveTool() instanceof AbstractEditTool;
    }
    private void setCurrentShape( ILayer layer ) {
    	PrimitiveShape shapeToRestoreToCurrent;
    	if( layer==null ){
    		// Can't edit this layer but we don't want a shape from another layer to show up so 
    		// set the current shape to null;
    		shapeToRestoreToCurrent = null;
    	}else{
	        IBlackboard blackboard = layer.getBlackboard();
			shapeToRestoreToCurrent = (PrimitiveShape)blackboard.get(EditToolHandler.STORED_CURRENT_SHAPE);
	        blackboard.put(EditToolHandler.STORED_CURRENT_SHAPE, null);
    	}
    	handler.setCurrentShape(shapeToRestoreToCurrent);
    }

    private void storeCurrentShape( ILayer layer ) {
    	if( layer!=null ){
	        PrimitiveShape currentShape = handler.getCurrentShape();
	        layer.getBlackboard().put(EditToolHandler.STORED_CURRENT_SHAPE, currentShape);
    	}
    }

    /**
     * Store's the current state on the layer's blackboard
     * @param layer
     */
    private void storeCurrentState( ILayer layer ) {
    	if( layer!=null ){
	        IBlackboard blackboard = layer.getBlackboard();
	        blackboard.put(EditToolHandler.STORED_CURRENT_STATE, handler.getCurrentState());
	        blackboard.put( EditToolHandler.STORED_CURRENT_STATE, null );
    	}
    }

    private void getPreviousStateFromLayer(ILayer layer) {
    	EditState stateToRestore;
    	if( layer ==  null ){
    		// can't get a new state so set the state to Illegal (can't edit a null layer)
    		stateToRestore = EditState.ILLEGAL;
    	}else{
			stateToRestore = (EditState)layer.getBlackboard().get(EditToolHandler.STORED_CURRENT_STATE);
	        if( stateToRestore==null )
	            stateToRestore=EditState.NONE;
    	}
    	handler.setCurrentState(stateToRestore);
    }

    
    private synchronized void stopListening() {
        if( !active() ){
            editManager.removeListener(this);
            editManager.getMap().getBlackboard().put(EDITING_EDIT_MANAGER_LISTENER, null);
        }
    }
    /**
     * Clears current state and shape.  Clears layer caches of old states and shapes.  
     * Clears edit blackboards
     *
     * @param selectedLayer
     */
    private void resetEditState( ILayer selectedLayer ) {
        EditBlackboardUtil.resetBlackboards(editManager.getMap());
        editManager.getMap().getBlackboard().put(EditToolHandler.CURRENT_SHAPE, null);
        editManager.getMap().getBlackboard().put(EditToolHandler.EDITSTATE, EditState.NONE);
        ((EditManager) editManager).setEditFeature(null, null);
        EditUtils.instance.cancelHideSelection(selectedLayer);
        List<ILayer> mapLayers = editManager.getMap().getMapLayers();
        for( ILayer layer : mapLayers ) {
            ((Layer)layer).setFilter(Filter.EXCLUDE);
        }
        EditUtils.instance.clearLayerStateShapeCache(mapLayers);
    }

}

