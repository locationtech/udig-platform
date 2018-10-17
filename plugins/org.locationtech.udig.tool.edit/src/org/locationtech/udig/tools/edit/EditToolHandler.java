/*
* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.EditUtils;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * This is the class an edit tool configures to do all the work.
 * <p>
 * The tool receives events from the view port, and forwards those events to 
 * this handler. The handler will figure out behaviour to run; taking the
 * resulting commands to execute.
 * 
 * @author jones
 * @since 1.1.0
 */
public class EditToolHandler {

    /**
     * The key for the currently selected/edit state.  
     * It is put on the map referenced by the context (see {@link #getContext()})
     */
    public static final String EDITSTATE = "EDIT_TOOL_HANDLER_EDIT_STATE_KEY_33847562"; //$NON-NLS-1$
    /**
     * The key for the lock required if modifying the edit state or shape.  
     * It is put on the map referenced by the context (see {@link #getContext()})
     */
    private static final String LOCK = "EDIT_TOOL_HANDLER_LOCK_KEY_345280194"; //$NON-NLS-1$
    /**
     * The key for the currently selected/edit shape.  
     * It is put on the map referenced by the context (see {@link #getContext()})
     */
    public static final String CURRENT_SHAPE = "EDIT_TOOL_HANDLER_CURRENT_SHAPE_KEY_872839"; //$NON-NLS-1$
    /** When there is a switch in the currently selected layer the current state is stored on the old layer so if the
     * layer selected layer the state can be restored.
     * 
     * <p>Modify with <em>Care</em> this is primarily used by the framework for its workflow but if the workflow 
     * is not pleasing then modification is permitted.  </p>
     */
    public static final String STORED_CURRENT_STATE = "STORED_CURRENT_STATE"; //$NON-NLS-1$
    /** When there is a switch in the currently selected layer the current shape is stored on the old layer so if the
     * layer selected layer the state can be restored.
     * 
     * <p>Modify with <em>Care</em> this is primarily used by the framework for its workflow but if the workflow 
     * is not pleasing then modification is permitted.  </p>
     */
    public static final String STORED_CURRENT_SHAPE = "STORED_CURRENT_SHAPE"; //$NON-NLS-1$
    /**
     * Cursor that should be set when a selection can occur.
     */
    public final Cursor selectionCursor;
    /**
     * Cursor that should be set when editing can occur.
     */
    public final Cursor editCursor;

    private List<EventBehaviour> behaviour = new CopyOnWriteArrayList<EventBehaviour>();
    private List<EnablementBehaviour> enablementBehaviours= new CopyOnWriteArrayList<EnablementBehaviour>();
    private List<Behaviour> acceptBehaviours = new CopyOnWriteArrayList<Behaviour>();
    private List<Behaviour> cancelBehaviours = new CopyOnWriteArrayList<Behaviour>();
    private Set<Activator> activators = new CopyOnWriteArraySet<Activator>();
    private List<IDrawCommand> drawCommands = Collections
            .synchronizedList(new ArrayList<IDrawCommand>());
    private IToolContext context;
    private MouseTracker mouseTracker = new MouseTracker(this);
    protected boolean testing = false;
    protected AbstractEditTool tool;
    

    // see #lock
    Object behaviourLock;

    private volatile boolean needRepaint;
    private volatile boolean processingEvent;

    public EditToolHandler( Cursor selectionCursor, Cursor editCursor ) {
        this.selectionCursor = selectionCursor;
        this.editCursor = editCursor;
    }

    /**
     * Called by AbstractEditTool when activated. 
     * 
     * @param active
     */
    protected void setActive( boolean active ) {
        if (active) {
            oldState=EditState.NONE;
            
            // if current geom no longer on BB then delete
            ILayer editLayer = getEditLayer();
            if (!getEditBlackboard(editLayer).getGeoms().contains(
                    getCurrentGeom())) {
                setCurrentShape(null);
            }
            basicEnablement();
            enableListeners();

        } else {

            List<Behaviour> list = acceptBehaviours;

            BehaviourCommand command = getCommand(list);
            getContext().sendSyncCommand(command);
            
            basicDisablement();
            disableListeners();
            setCurrentState(EditState.NONE);
        }
    }

    /**
     * disables the activators and stops listening
     */
    void basicDisablement() {
        for( Activator runnable : activators ) {
            try {
                runnable.deactivate(this);
            } catch (Throwable error) {
                runnable.handleDeactivateError(this, error);
            }
        }
        
        EditUtils.instance.clearLayerStateShapeCache(getContext().getMapLayers());

        for( IDrawCommand drawCommand : drawCommands ) {
            drawCommand.setValid(false);
        }
        drawCommands.clear();

    }

    private void disableListeners() {
        EditBlackboardUtil.doneListening();
        
        EditBlackboardUtil.disableClearBlackboardCommand();
    }

    /**
     * enables the activators and starts listening
     */
    void basicEnablement() {
        for( Activator runnable : activators ) {
            try {
                runnable.activate(this);
            } catch (Throwable error) {
                runnable.handleActivateError(this, error);
            }
        }
    }

    private void enableListeners() {
        EditManagerListener.enableEditManagerListener(this);
        
        EditBlackboardUtil.enableClearBlackboardCommand(context);
    }

    // This state is used to store the state before it is set to Illegal by
    // the enablement behaviours.  Since enablement behaviours can set the state to 
    // illegal based on unknown reasons the previous state has to be maintaned
    // by the framework to remove that burden from the
    // enablement behaviour implementors.
    private EditState oldState;
    
    /**
     * Runs a list of behaviours. Expected uses are
     * handler.runBehaviours(handler.getAcceptBehaviours()); or
     * handler.runBehaviours(handler.getCancelBehaviours());
     * 
     * @param list
     */
    public BehaviourCommand getCommand( List<Behaviour> list ) {
        return new BehaviourCommand(list, this);
    }

    /**
     * Runs through the list of modes and runs all the modes that are valid in the current context.
     * 
     * @param e mouse event that just occurred.
     * @param eventType the type of event that just occurred
     */
    protected void handleEvent( MapMouseEvent e, EventType eventType ) {
        
        synchronized (this) {
            needRepaint = false;
            this.processingEvent = true;
        }
        try {
            if (getCurrentState() == EditState.BUSY)
                return;
            
            runEnablementBehaviours(e,eventType);
            
            if (getCurrentState() == EditState.ILLEGAL )
                return;

            mouseTracker.updateState(e, eventType);
           
            runEventBehaviours(e, eventType);
        } finally {
            synchronized (this) {
                if (needRepaint) {
                    getContext().getViewportPane().repaint();
                }
                needRepaint = false;
                this.processingEvent = false;
            }
        }
    }

    private void runEnablementBehaviours( MapMouseEvent e, EventType eventType) {
        String errorMessage=null;
        for( EnablementBehaviour b : enablementBehaviours ) {
                errorMessage = b.isEnabled(this, e, eventType);
                if( errorMessage!=null ){
                    break;
                }
        }
        
        if( errorMessage==null  ){
            if( getCurrentState()==EditState.ILLEGAL ){
                setCurrentState(oldState);
                getContext().getActionBars().getStatusLineManager().setErrorMessage(errorMessage);
            }
        }else{
            getContext().getActionBars().getStatusLineManager().setErrorMessage(errorMessage);
            if( getCurrentState()!=EditState.ILLEGAL ){
                oldState=getCurrentState();
                setCurrentState(EditState.ILLEGAL);
            }
        }
    }

    private void runEventBehaviours( MapMouseEvent e, EventType eventType ) {
        for( EventBehaviour b : behaviour ) {

            if (canUnlock(b) && b.isValid(this, e, eventType)) {
                UndoableMapCommand c = null;
                c = b.getCommand(this, e, eventType);
                if (c == null)
                    continue;

                if (testing) {
                    c.setMap((Map) getContext().getMap());
                    try {
                        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
                        c.run(nullProgressMonitor);
                    } catch (Exception e1) {
                        throw (RuntimeException) new RuntimeException().initCause(e1);
                    }
                } else{
                    getContext().sendASyncCommand(c);
                }
            }
        }
    }
    /**
     * Returns true if the handler is unlocked or the behaviour has the correct key.
     * 
     * @param behaviour trying to run
     * @return Returns true if the handler is unlocked or the behaviour has the correct key.
     */
    private boolean canUnlock( EventBehaviour behaviour ) {
        if (!isLocked())
            return true;
        if (behaviour instanceof LockingBehaviour) {
            LockingBehaviour locker = (LockingBehaviour) behaviour;

            return behaviourLock == locker.getKey(this);
        }
        return false;
    }

    /**
     * @return Returns the currentGeom.
     */
    public EditGeom getCurrentGeom() {
        Lock lock2 = getLock();
        (lock2).lock();
        try {
            PrimitiveShape currentShape = getCurrentShape();
            return currentShape == null ? null : currentShape.getEditGeom();
        } finally {
            lock2.unlock();
        }
    }

    /**
     */
    public void setCurrentShape( PrimitiveShape currentShape ) {
        Lock lock2 = getLock();
        lock2.lock();
        try {
            getContext().getMap().getBlackboard().put(CURRENT_SHAPE, currentShape);
        } finally {
            lock2.unlock();
        }
    }

    /**
     * @return Returns the currentShape.
     */
    public PrimitiveShape getCurrentShape() {
        Lock lock2 = getLock();
        lock2.lock();
        try {
            return (PrimitiveShape) getContext().getMap().getBlackboard().get(CURRENT_SHAPE);
        } finally {
            lock2.unlock();
        }
    }

    /**
     * @return Returns the currentState.
     */
    public EditState getCurrentState() {
        Lock lock2 = getLock();
        lock2.lock();
        try {
            EditState editState2 = (EditState) getContext().getMap().getBlackboard().get(EDITSTATE);
            return editState2 == null ? EditState.NONE : editState2;
        } finally {
            lock2.unlock();
        }
    }

    synchronized Lock getLock() {
        Lock lock2 = (Lock) getContext().getMap().getBlackboard().get(LOCK);
        if (lock2 == null) {
            lock2 = new ReentrantLock();
            getContext().getMap().getBlackboard().put(LOCK, lock2);
        }
        return lock2;
    }

    /**
     * @param currentState The currentState to set.
     */
    public void setCurrentState( EditState currentState ) {
        if (currentState == null)
            throw new NullPointerException("Edit state is null"); //$NON-NLS-1$
        if (currentState == getCurrentState())
            return;
        getContext().getMap().getBlackboard().put(EDITSTATE, currentState);

      }

    /**
     * Returns the EventBehaviours that may be run when an event occurs. This list is thread safe and may be
     * modified.
     * 
     * @return the EventBehaviours that may be run when an event occurs. This list is thread safe and may be
     * modified.
     */
    public List<EventBehaviour> getBehaviours() {
        return behaviour;
    }

    /**
     * Returns the behaviours that determine whether the tool is active at the current locations
     *
     * @return the behaviours that determine whether the tool is active at the current locations
     */
    public List<EnablementBehaviour> getEnablementBehaviours() {
        return enablementBehaviours;
    }


    /**
     * Gets the EditBlackboard of the map.
     * 
     * @return
     */
    public EditBlackboard getEditBlackboard( ILayer layer ) {

        return EditBlackboardUtil.getEditBlackboard(getContext(), layer);
    }
    
    /**
     * Returns the currently selected layer, or if the EditManager is locked,
     * it will return the edit layer.
     *
     * @return
     */
    public ILayer getEditLayer() {
        ILayer editLayer = getContext().getSelectedLayer();
        if (getContext().getEditManager().getEditLayer() != null && getContext().getEditManager().isEditLayerLocked()) {
            editLayer = getContext().getEditManager().getEditLayer();
        }
        return editLayer;
    }

    /**
     * Returns the Activators that are run during activation and deactivation This list is thread
     * safe and may be modified.
     * 
     * @return Returns the activationActions.
     */
    public Set<Activator> getActivators() {
        return activators;
    }

    /**
     * Returns the draw actions that need to be deactivated when the tool is deactivated.
     * <p>
     * This list is thread safe and may be modified.
     * </p>
     * 
     * @return Returns the drawCommands.
     */
    public List<IDrawCommand> getDrawCommands() {
        return drawCommands;
    }

    /**
     * Gets the tool context object that Modes and Activators may use.
     * 
     * @return
     */
    public IToolContext getContext() {
        return context;
    }

    /**
     * @param context2 The context to set.
     */
    protected void setContext( IToolContext context2 ) {
        this.context = context2;
    }

    /**
     * Sets the ViewportPane's cursor
     * 
     * @param cursor_id the SWT.CURSOR_XXX id of the cursor to set.
     * @deprecated
     */
    public void setCursor( final int cursor_id ) {
        if (Display.getCurrent() != null) {
        	
        	if(tool != null){
        		tool.setCursorID(cursor_id+""); //$NON-NLS-1$
        	}
//            setCursor(Display.getCurrent().getSystemCursor(cursor_id));
        } else {
            final Display display = PlatformUI.getWorkbench().getDisplay();
            display.asyncExec(new Runnable(){

                public void run() {
                	if(tool != null){
                		tool.setCursorID(cursor_id+""); //$NON-NLS-1$
                	}
//                    setCursor(display.getSystemCursor(cursor_id));
                }
            });
        }
    }
    
    /**
     * The method gets ID of the cursor as configured by extension or
     * by <code>ModalTool.*_CURSOR</code> value corresponding to <i>SWT.CURSOR_*</i> constants
     *  and delegates the call to <code>ModalTool</code> to find the cursor
     *  in cache and set it.
     * 
     * @param cursorID
     */
    public void setCursor(final String cursorID) {
    	
        if (Display.getCurrent() != null) {
        	if(tool != null){
        		tool.setCursorID(cursorID);
        	}
        } else {
            final Display display = PlatformUI.getWorkbench().getDisplay();
            display.asyncExec(new Runnable(){
                public void run() {
                	if(tool != null){
                		tool.setCursorID(cursorID);
                	}
                }
            });
        }
    }

    /**
     * Sets the ViewportPane's cursor
     * 
     * @param cursor new cursor
     * @deprecated
     */
    public void setCursor( final Cursor cursor ) {       

        if( Display.getCurrent()!=null ){
            getContext().getViewportPane().setCursor(cursor);
        }else{
            final Display display = PlatformUI.getWorkbench().getDisplay();
            display.asyncExec(new Runnable(){
    
                public void run() {
                    getContext().getViewportPane().setCursor(cursor);
                }
            });
        }
    }

    /**
     * @return Returns the mouseTracker.
     */
    public MouseTracker getMouseTracker() {
        return mouseTracker;
    }

    /**
     * Returns the list of behaviours that are run when the Enter key is pressed. EventBehaviours
     * are welcome to run these behaviours as well if they wish to accept the current edit. The list
     * is thread safe and can be modified.
     * 
     * @return Returns the acceptBehaviours.
     */
    public List<Behaviour> getAcceptBehaviours() {
        return acceptBehaviours;
    }

    /**
     * Returns the list of behaviours that are run when the Esc key is pressed. The list is thread
     * safe and can be modified.
     * 
     * @see #getCommand(List)
     * @return Returns the cancelBehaviours.
     */
    public List<Behaviour> getCancelBehaviours() {
        return cancelBehaviours;
    }

    /**
     * Locks the handler so only the only behaviours that can run are {@link LockingBehaviour}s
     * who's {@link LockingBehaviour#getKey(EditToolHandler)} method returns the same object as the
     * locking {@link LockingBehaviour}'s {@link LockingBehaviour#getKey(EditToolHandler)} method.
     * <p>
     * This is not a reentrant lock so it cannot be locked multiple times. Also the lock cannot be
     * null
     * </p>
     * 
     * @param behaviour the behaviour that is locking the handler
     */
    public void lock( LockingBehaviour behaviour ) {
        if (behaviourLock != null) {
            throw new IllegalArgumentException("Handler is locked and cannot be relocked"); //$NON-NLS-1$
        }
        this.behaviourLock = behaviour.getKey(this);
        if (behaviourLock == null)
            throw new IllegalArgumentException("Null is not a legal key"); //$NON-NLS-1$
    }

    /**
     * Returns true if Handler has been locked by {@link #lock(LockingBehaviour)}
     * 
     * @return Returns true if Handler has been locked by {@link #lock(LockingBehaviour)}
     */
    public boolean isLocked() {
        return behaviourLock != null;
    }

    /**
     * Unlocks the handler so all behaviours can run. The behaviour's
     * {@link LockingBehaviour#getKey(EditToolHandler)} method must return the same object as the
     * locking behaviours {@link LockingBehaviour#getKey(EditToolHandler)} method.
     * 
     * @param behaviour
     */
    public void unlock( LockingBehaviour behaviour ) {
        if (behaviour.getKey(this) != behaviourLock) {
            throw new IllegalArgumentException("Locking behaviour does not have the correct key"); //$NON-NLS-1$
        }
        this.behaviourLock = null;
    }

    /**
     * Returns true if the behaviour's {@link LockingBehaviour#getKey(EditToolHandler)} returns the
     * key for the lock.
     * 
     * @param behaviour the behaviour to test
     * @return Returns true if the behaviour's {@link LockingBehaviour#getKey(EditToolHandler)}
     *         returns the key for the lock.
     */
    public boolean isLockOwner( LockingBehaviour behaviour ) {
        if (behaviour.getKey(this) == null)
            throw new IllegalArgumentException("Null is not a legal key"); //$NON-NLS-1$

        return behaviour.getKey(this) == behaviourLock;
    }

    @Override
    public String toString() {
        return getCurrentState() + ", " + getCurrentShape().getEditGeom() + ", " + mouseTracker; //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * All behaviours and listeners should call this method so that only one redraw is done per
     * mouse event.
     */
    public synchronized void repaint() {
        if (!processingEvent) {
            getContext().getViewportPane().repaint();
        } else {
            needRepaint = true;
        }
    }
    
    public void setTool(AbstractEditTool tool){
    	this.tool = tool;
    }

    /**
     * Returns the tool that the handler works with
     *
     * @return the tool that the handler works with
     */
    public AbstractEditTool getTool(){
    	return tool;
    }

    /**
     * A convenience method for obtaining the current edit blackboard.
     */
    public EditBlackboard getCurrentEditBlackboard() {
        return getEditBlackboard(getEditLayer());
    }

}
