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
package net.refractions.udig.internal.ui;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.ui.IDropAction;
import net.refractions.udig.ui.IDropHandlerListener;
import net.refractions.udig.ui.ProgressMonitorTaskNamer;
import net.refractions.udig.ui.UDIGDragDropUtilities;
import net.refractions.udig.ui.ViewerDropLocation;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Control;

/**
 * A drop event handler which delegates to an extension point in order to perform drag and drop
 * actions.
 * <p>
 * The drop handler interacts with three objects.
 * <ol>
 * <li>The <b>type</b> object, which is the entity being dropped.
 * <li>The <b>destination</b> object, which is where the drop is occurring. This is usually a view
 * or an editor.
 * <li>The <b>target</b> object (optional), which is the specific object within the destination.
 * </ol>
 * </p>
 * <p>
 * A UDIGDropHandler is delegated to by another DropTargetAdapter. Depending on the type of drop
 * adapter, location, and target may not be set.
 * </p>
 * 
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 * @since 0.6.0
 */
public class UDIGDropHandler extends DropTargetAdapter {

    /** job queue shared by all drop handlers * */
    private static final CompositeDropActionJob actionJob=new CompositeDropActionJob();

    /** the target object * */
    private Object target;

    /** the location of the drop * */
    private ViewerDropLocation location = ViewerDropLocation.ON;

    private Set<IDropHandlerListener> listeners=new CopyOnWriteArraySet<IDropHandlerListener>();

    public UDIGDropHandler() {
    }

    /**
     * Gets the Object the Drop event targets. For example in LayersView it would be the layer it
     * hit.
     * <p>
     * This is defined by the extension and should not be called by non-framework code
     * </p>
     */
    public void setTarget( Object target ) {
        this.target = target;
    }

    /**
     * Gets the Object the Drop event targets. For example in LayersView it would be the layer it
     * hit. The target is dependent on the dropListener.
     */
    public Object getTarget() {
        return target;
    }
    /**
     * a constant describing the position of the mouse relative to the target (before, on, or after
     * the target.
     * <p>
     * This is set by the framework
     * </p>
     * 
     * @param location one of the <code>LOCATION_* </code> constants defined in this type
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#getCurrentLocation()
     */

    public void setViewerLocation( ViewerDropLocation location ) {
        this.location = location;
    }
    /**
     * Returns a constant describing the position of the mouse relative to the target (before, on,
     * or after the target.
     * 
     * @return one of the <code>LOCATION_* </code> constants defined in this type
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#getCurrentLocation()
     */
    public ViewerDropLocation getViewerLocation() {
        return location;
    }

    @Override
    public void dragLeave( DropTargetEvent event ) {
        super.dragLeave(event);
    }

    @Override
    public void dragOver( DropTargetEvent event ) {
        event.detail = DND.DROP_COPY;

        if ((event.operations & DND.DROP_LINK) == DND.DROP_LINK) {
            event.detail = DND.DROP_LINK;
        } else if ((event.operations & DND.DROP_MOVE) == DND.DROP_MOVE) {
            event.detail = DND.DROP_MOVE;
        } else if ((event.operations & DND.DROP_COPY) == DND.DROP_COPY) {
            event.detail = DND.DROP_COPY;
        }
    }

    @Override
    public void dropAccept( DropTargetEvent event ) {
        super.dropAccept(event);
    }

    private Object getJavaObject( Transfer transfer, TransferData data ) {
        try{
            if( transfer instanceof UDIGTransfer ){
                return ((UDIGTransfer)transfer).nativeToJava(data);
            }
            Method m=transfer.getClass().getMethod("nativeToJava", new Class[]{TransferData.class}); //$NON-NLS-1$
            return m.invoke(transfer, new Object[]{data});
        }catch(Throwable t){
            return null;            
        }
    }

    @Override
    public void dragEnter( DropTargetEvent event ) {
        if (UiPlugin.isDebugging(Trace.DND)) {
            DropTarget target = (DropTarget) event.getSource();
            Control control = target.getControl();
            
            System.out.println("UDIGDropHandler.dragEnter "+control.toString()+": Setting event.detail to COPY"); //$NON-NLS-1$
        }
        event.detail = DND.DROP_COPY;
    }

    @Override
    public void dragOperationChanged( DropTargetEvent event ) {
        if (UiPlugin.isDebugging(Trace.DND)) {
            System.out
                    .println("UDIGDropHandler.dragOperationChanged: Setting event.detail to COPY"); //$NON-NLS-1$
        }
        event.detail = DND.DROP_COPY;
    }

    public void drop( DropTargetEvent event ) {
        Set<Transfer> t = UDIGDragDropUtilities.getTransfers();
        List<IDropAction> actions = null;
        for( Transfer transfer : t ) {
            if (event.data != null){
                actions = findDropActions(event.data, event);
                if( !actions.isEmpty() ) {
                    break;
                }
            }
            TransferData[] types = transfer.getSupportedTypes();
            for( TransferData data : types ) {
                if (transfer.isSupportedType(data)) {
                    Object object = getJavaObject(transfer, data);
                    if( object == null ) {
                        continue;
                    }
                    actions = findDropActions(object, event);
                    if( !actions.isEmpty() ) break;
                }
            }
        }
        
        CompositeDropActionJob actionJob = getActionJob();
        if (actions != null && !actions.isEmpty()){
            List<IDropAction> filteredActions = filterActions(event,actions);
            
            UiPlugin.trace(Trace.DND, getClass(), "Target "+getTarget()+" found "+filteredActions.size()+" drop actions",null); //$NON-NLS-1$ //$NON-NLS-2$ 
            for( IDropAction action : filteredActions ){
                UiPlugin.trace(Trace.DND, getClass(), " * Action "+action.getName()+" implementation "+action.getClass().getSimpleName(), null ); //$NON-NLS-1$
            }

            actionJob.addActions(this, filteredActions);
        } else {
            UiPlugin.trace(Trace.DND, getClass(), event.data+" dropped on "+getTarget()+" found no actions for processing it",null );  //$NON-NLS-1$//$NON-NLS-2$
            notifyNoDropAction(event.data);
        }
    }
    /**
     * A hook for subclasses to modify the list of actions that will be executed for this drop event.
     * 
     * The intention is to be able to use the same set of drop extensions for the views but allow a
     * subset to be used for different views.
     * 
     * Default action is to return all actions in the same order as the extension mechanism found.
     * 
     * @param event the event that has just occurred and is being processed
     * @param the action extensions that were found that claim to be able to process the drop event
     */
    protected List<IDropAction> filterActions(DropTargetEvent event, List<IDropAction> actions) {
        return actions;
    }
    /**
     * Go through and determine if we have any IDropActions based on the provided event.
     * 
     * @param data
     * @param event
     * @return
     */
    private List<IDropAction> findDropActions( Object data, DropTargetEvent event ) {
        if (UiPlugin.isDebugging(Trace.DND)) {
            String type = data.getClass().getSimpleName();
            String value = data.getClass().isArray() ?
                    Array.getLength( data )+" items" :
                    data.toString();
            
            UiPlugin.trace(Trace.DND, UDIGDropHandler.class,
                    "Find drop actions for " + type +": "+value ,null); //$NON-NLS-1$ //$NON-NLS-2$        
        }
        // do a check for a multi-object and separate the children out
        Class<?> type = data.getClass();

        Object[] objects = null;
        if (type.isArray()) {
            objects = (Object[]) data;
        } else if (data instanceof Collection) {
            objects = ((Collection<?>) data).toArray();
        } else if (data instanceof IStructuredSelection) {
            objects = ((IStructuredSelection) data).toArray();
        } else {
            objects = new Object[]{data};
        }
        List<IDropAction> actions = UDIGDNDProcessor.process(objects, UDIGDropHandler.this, event);
        return actions;
    }



    /**
     * Find drop actions for data.
     *
     * @param data data dropped
     * @param event drop event.  Maybe null.
     */

    public void performDrop( Object data, DropTargetEvent event ) {
        if (UiPlugin.isDebugging(Trace.DND)) {
            System.out.println("PerformDrop called on " + data + "(" + data.getClass() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        // do a check for a multi object and separate the children out
        List<IDropAction> actions = findDropActions(data, event);

        CompositeDropActionJob actionJob = getActionJob();
        if (!actions.isEmpty()){
            UiPlugin.trace(Trace.DND, getClass(), data+" dropped on "+getTarget()+" found "+actions.size()+" drop actions",null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            for( IDropAction action : actions ){
                UiPlugin.trace(Trace.DND, getClass(), " * Action "+action.getName()+" implementation "+action.getClass().getSimpleName(), null ); //$NON-NLS-1$
            }
            
            actionJob.addActions(this, actions);
        } else {
            UiPlugin.trace(Trace.DND, getClass(), data+" dropped on "+getTarget()+" found no actions for processing it",null );  //$NON-NLS-1$//$NON-NLS-2$
            notifyNoDropAction(data);
        }
    }

    private void notifyNoDropAction( Object object ) {
        Set<IDropHandlerListener> set = listeners;
        for( IDropHandlerListener listener : set ) {
            listener.noAction(object);
        }
    }

    public static CompositeDropActionJob getActionJob() {
        return actionJob;
    }

    public static class CompositeDropActionJob extends Job {

        private final Queue<Collection<DropActionRunnable>> queue;

        public CompositeDropActionJob() {
            super(Messages.UDIGDropHandler_jobName);  

            queue = new ConcurrentLinkedQueue<Collection<DropActionRunnable>>();
            setUser(true);
        }

        /**
         * Returns the drag and drop job queue.  It contains all the actions that have not been executed. It is not modifiable
         *
         * @return
         */
        public synchronized List<Collection<DropActionRunnable>> getJobQueue() {
            return Collections.unmodifiableList(new ArrayList<Collection<DropActionRunnable>>(queue));
        }
        
        
        /**
         * Adds an action to be executed by drop handler.
         *
         * @param handler
         * @param actions
         */
        synchronized void addActions( UDIGDropHandler handler, Collection<IDropAction> actions ) {
            List<DropActionRunnable> runnables = new ArrayList<DropActionRunnable>();
            for( IDropAction action : actions ) {
                runnables.add(new DropActionRunnable(handler, action));
            }
            
            queue.offer(runnables);
            
            if( !actions.isEmpty() )
                actionJob.schedule();
        }

        @Override
        public boolean belongsTo( Object family ) {
            return family == CompositeDropActionJob.class;
        }

        @Override
        protected IStatus run( IProgressMonitor monitor2 ) {
            monitor2.beginTask(Messages.UDIGDropHandler_performing_task, IProgressMonitor.UNKNOWN);
            Collection<DropActionRunnable> next;
            while( !Thread.currentThread().isInterrupted() ) {
                next = null;

                synchronized (this) {
                    next = queue.poll();
                    
                    if( next==null || Thread.currentThread().isInterrupted() ){
                        return Status.OK_STATUS;
                    }
                }

                
                boolean foundGoodAction=false;
                for( Iterator<DropActionRunnable> iterator = next.iterator(); iterator.hasNext() && !foundGoodAction; ) {
                    DropActionRunnable action =  iterator.next();
                    IProgressMonitor monitor=new ProgressMonitorTaskNamer(monitor2, 10);
                    
                    monitor2.setTaskName(Messages.UDIGDropHandler_performing_task + ": "+ action.action.getName()); //$NON-NLS-1$
                    
                    // run the next job
                    if( action.run(monitor).getCode()==Status.OK ){
                        foundGoodAction=true;
                    }
                    
                }
                
            }
            
            return Status.OK_STATUS;
        }
    }

    private static class DropActionRunnable{

        IDropAction action;
        UDIGDropHandler handler;

        public DropActionRunnable( UDIGDropHandler handler, IDropAction action ) {
            super(); 

            this.handler = handler;
            this.action = action;
        }
        protected IStatus run( IProgressMonitor monitor ) {
            notifyStart(action);
            try {
                action.perform(monitor);
                notifyDone(action, null);
                return Status.OK_STATUS;
            } catch (Throwable t) {
                String msg = Messages.UDIGDropHandler_error; 
                String ns = action.getElement().getNamespaceIdentifier();

                Status s = new Status(IStatus.WARNING, ns, 0, msg, t);
                UiPlugin.getDefault().getLog().log(s);
                notifyDone(action, t);
                return Status.CANCEL_STATUS;
            }
        }
        
        private void notifyDone(IDropAction action, Throwable t){
            Set<IDropHandlerListener> set = handler.listeners;
            for( IDropHandlerListener listener : set ) {
                listener.done(action, t);
            }
        }

        private void notifyStart(IDropAction action){
            Set<IDropHandlerListener> set = handler.listeners;
            for( IDropHandlerListener listener : set ) {
                listener.starting(action);
            }
        }
    }
    

    /**
     * Remove listener from set of listeners.
     *
     * @param listener listener to remove
     */
    public void removeListener( IDropHandlerListener listener ) {
        listeners.remove(listener);
    }
    /**
     * Add listener to set of drop listeners.  A listener can only be added once.
     *
     * @param listener listener to add.
     */
    public void addListener( IDropHandlerListener listener ) {
        listeners.add(listener);
    }

}
