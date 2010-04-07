/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.command;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.edit.RollbackCommand;
import net.refractions.udig.project.internal.commands.selection.CommitCommand;
import net.refractions.udig.project.internal.impl.MapImpl.MapCommandListener;
import net.refractions.udig.project.preferences.PreferenceConstants;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.ProgressMonitorTaskNamer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * A commands Manager executes commands in a seperate thread, either synchronously or a
 * synchronously.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class CommandManager implements CommandStack, NavCommandStack {

    private static final String TRACE_ID = "net.refractions.udig.project/debug/commands/manager/trace"; //$NON-NLS-1$
    /**
     * If -1 then Synchronous commands will wait indefinately. Otherwise they will try for this many
     * milliseconds.
     */
    private long timeout = -1;

    Set<ErrorHandler> handlers = new CopyOnWriteArraySet<ErrorHandler>();
    Set<CommandListener> completionHandlers = new CopyOnWriteArraySet<CommandListener>();

    Executor commandExecutor;
    private final String managerName;

    /**
     * Creates a new instance of CommandManager
     * 
     * @param handler an error handler to use to handle thrown exceptions.
     */
    public CommandManager( String managerName, ErrorHandler handler, CommandListener commandCompletionListener ) {
        this.managerName=managerName;
        handlers.add(handler);
        if( commandCompletionListener!=null )
            completionHandlers.add(commandCompletionListener);
    }
    /**
     * Creates a new instance of CommandManager
     * 
     * @param handler an error handler to use to handle thrown exceptions.
     */
    public CommandManager( String managerName, ErrorHandler handler ) {
        this(managerName, handler, null);
    }
    /**
     * Creates a new instance of CommandManager
     * 
     * @param handler an error handler to use to handle thrown exceptions.
     */
    public CommandManager( String managerName, ErrorHandler handler, CommandListener commandCompletionListener,
            long timeout2 ) {
        this(managerName, handler, commandCompletionListener);
        this.timeout = timeout2;
        
    }

    /**
     * Executes a command. Calls the Errorhandler if an exception is thrown.
     * 
     * @param command The command to execute
     * @param async flag indicating wether command should be executed sync vs async.
     * @return true if no problems were encountered while queueing command. Problems will typically
     *         occur when the command is synchronous and it times out or is interrupted.
     */
    public boolean execute( final Command command, boolean async ) {
        int type = Request.RUN;
        return doMakeRequest(command, async, type);

    }
    
    /**
     * @param command command to perform
     * @param async whether to do request synchronously
     * @param type type of request (REDO UNOD RUN )
     * @return
     */
    private boolean doMakeRequest( final Command command, boolean async, int type ) {
        Request request = new Request(type, command, async, Display.getCurrent() != null);
        synchronized (this) {
            if (commandExecutor == null) {
                commandExecutor = new Executor(managerName); 
            }
        }
        commandExecutor.addRequest(request);
        if (request.isSync()) {

            // synchronous execution, current thread needs to wait
            // it is unlikely that the command will be complete by the time
            // we get here but better to be safe than sorry
            try {
                Display current = Display.getCurrent();
                if (current != null)
                    waitInDisplay(current, request);
                else
                    waitOnRequest(request);
                if (!request.completed) {
                    ProjectPlugin.trace(TRACE_ID, getClass(), "Request didn't complete", null); //$NON-NLS-1$
                    commandExecutor.removeCommand(request);
                    return false;
                }
                ProjectPlugin.trace(TRACE_ID, getClass(), "Request completed", null); //$NON-NLS-1$
            } catch (InterruptedException e) {
                ProjectPlugin.log("Error running commands synchronously", e); //$NON-NLS-1$
                return false;
            }
        }
        return true;
    }

    private long waitOnRequest( Request request ) throws InterruptedException {
        ProjectPlugin.trace(TRACE_ID,getClass(), 
                "synchronous command NOT in display thread\nTimout=" + timeout, null); //$NON-NLS-1$
        long tries = 0;

        while( mustWait(request, tries) ) {
            tries += 500;
            synchronized (request) {
                request.wait(500);
            }
        }
        return tries;
    }
    /**
     * This method is special wait command that ensures that the display does not block. It executes
     * the jobs waiting for display.
     * 
     * @param current the current display
     * @param request
     * @throws InterruptedException
     * @see Display#readAndDispatch()
     */
    private long waitInDisplay( Display current, Request request ) throws InterruptedException {
        ProjectPlugin.trace(TRACE_ID,getClass(), 
                "synchronous command IN display thread\nTimout=" + timeout, null); //$NON-NLS-1$
        long start = System.currentTimeMillis();

        while( mustWait(request, System.currentTimeMillis() - start) ) {
            // run a display event continue if there is more work todo.
            if (current.readAndDispatch()) {
                continue;
            }

            // no more work to do in display thread, wait on request if request has not
            // finished
            if (!mustWait(request, System.currentTimeMillis() - start))
                return System.currentTimeMillis() - start;

            synchronized (request) {
                request.wait(300);
            }
        }
        return System.currentTimeMillis() - start;
    }
    private boolean mustWait( Request request, long tries ) {
        ProjectPlugin
                .trace(
                        TRACE_ID, getClass(), 
                        "timeout :" + timeout + ", tries: " + tries + ", completed:" + request.completed, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return !request.completed && (tries < timeout || timeout == -1);
    }
    /**
     * Executes the last undone command, if there are any commands to undo.
     * 
     * @param runAsync true to run undo asynchronously
     */
    public void redo( boolean runAsync ) {
        doMakeRequest(null, runAsync, Request.REDO);
    }

    /**
     * Undoes the last command if possible.
     * 
     * @param runAsync true to run undo asynchronously
     */
    public void undo( boolean runAsync ) {
        doMakeRequest(null, runAsync, Request.UNDO);
    }

    /**
     * Adds an Errorhandler to the list of error handlers
     * 
     * @param handler the error handler to add.
     * @see ErrorHandler
     */
    public void addErrorHandler( ErrorHandler handler ) {
        handlers.add(handler);
    }

    /**
     * Removes an Errorhandler from the list of error handlers
     * 
     * @param handler the error handler to remove.
     * @see ErrorHandler
     */
    public void removeErrorHandler( ErrorHandler handler ) {
        handlers.remove(handler);
    }

    /**
     * @see net.refractions.udig.project.command.CommandStack#canUndo()
     */
    public boolean canUndo() {
        if( commandExecutor ==null )
            return false;
        
        Command c;
        if (!commandExecutor.history.isEmpty()) {
            c = (Command) commandExecutor.history.peek();
            if (c instanceof UndoableCommand)
                return true;
        }
        return false;
    }

    /**
     * @see net.refractions.udig.project.command.CommandStack#canRedo()
     */
    public boolean canRedo() {
        if (commandExecutor!=null && !commandExecutor.undone.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * @see net.refractions.udig.project.command.NavCommandStack#hasBackHistory()
     */
    public boolean hasBackHistory() {
        return canUndo();
    }

    /**
     * @see net.refractions.udig.project.command.NavCommandStack#hasForwardHistory()
     */
    public boolean hasForwardHistory() {
        return canRedo();
    }

    /**
     * Executes commands in a seperate thread from the requesting thread. JONES: Should support
     * force kill of a command.
     * 
     * @author Jesse
     * @since 1.0.0
     */
    public class Executor extends Job {
        LinkedList<Command> history = new LinkedList<Command>();

        LinkedList<Command> undone = new LinkedList<Command>();

        Queue<Request> commands = new ConcurrentLinkedQueue<Request>();

        /**
         * Construct <code>Executor</code>.
         * 
         * @param name the name of the job
         * @param type the type of the executor. (RUN, UNDO, REDO)
         */
        public Executor( String name ) {
            super(name);
            setSystem(false);
        }

        IProgressMonitor progressMonitor;
        Request currentRequest;
        @Override
        protected IStatus run( IProgressMonitor monitor ) {
            monitor.beginTask(Messages.CommandManager_ProgressMonitor, IProgressMonitor.UNKNOWN); 
            while( !getThread().isInterrupted() ) {
                
                synchronized (this) {
                    currentRequest = commands.poll();
                    if( currentRequest==null )
                        return Status.OK_STATUS;
                }
                progressMonitor = new ProgressMonitorTaskNamer(monitor, 10);
                run(progressMonitor, currentRequest);

                if (currentRequest.isSync()) {
                    // notify those wating for command to finish
                    synchronized (currentRequest) {
                        currentRequest.notifyAll();
                    }
                }
            }
            return Status.OK_STATUS;
        }
        private void run( IProgressMonitor monitor, Request request ) {
            switch( request.type ) {
            case Request.RUN:
                execute(request.command, monitor);
                break;
            case Request.UNDO:
                undo(monitor);
                break;
            case Request.REDO:
                redo(monitor);
                break;
            case Request.RERUN:
                rerunCommands(monitor);
                break;
            }

            request.completed = true;
        }

        /**
         * Adds a command to the stack of commands that needs to be executed.
         * 
         * @param request
         */
        public void addRequest( Request request ) {
            if (getThread() == Thread.currentThread() || isDisplayThreadDeadlockDetected(request)) {
                run(progressMonitor, request);
            } else {
                synchronized( this ){
                    commands.offer(request);
                    schedule();
                }
                Thread.yield();
            }
        }
        private boolean isDisplayThreadDeadlockDetected( Request request ) {
            return (Display.getCurrent() != null && currentRequest != null
                    && currentRequest.requestByDisplay && currentRequest.isSync());
        }

        /**
         * This method is only called by {@linkplain CommandManager#execute(Command, boolean)}
         */
        synchronized void removeCommand( Request request ) {
            if (commands.remove(request))
                return;

            if (currentRequest == request) {
                // JONES interrupt running command if command is running
            }

            // If it has already been executed then don't worry.
        }

        /**
         * Executes a command. Calls the Errorhandler if an exception is thrown.
         * 
         * @param command The command to execute
         */
        private void execute( final Command command, IProgressMonitor monitor ) {

            long time = System.currentTimeMillis();
            if( command.getName()!=null )
            monitor.beginTask(command.getName(), IProgressMonitor.UNKNOWN);
            try {
                final boolean runCommand = openWarning(command);
                if (!runCommand)
                    return;

                if (command instanceof PostDeterminedEffectCommand) {

                    PostDeterminedEffectCommand c = (PostDeterminedEffectCommand) command;
                    if (c.execute(new SubProgressMonitor(monitor, 1000))) {
                        undone.clear();
                        addToHistory(command);
                    }

                } else {
                    command.run(new SubProgressMonitor(monitor, 1000));

                    undone.clear();
                    addToHistory(command);

                }

                if( ProjectPlugin.isDebugging(TRACE_ID) ){
                long l = (System.currentTimeMillis()-time);
                    if( l>100){
                        System.out.println(command.toString()+"--"+l); //$NON-NLS-1$
                    }
                }
                if (history.size() > getMaxHistorySize())
                    history.removeFirst();
                notifyOwner(command);

            } catch (Throwable e) {
                undone.clear();
                handleError(command, e);
            }

        }
        private void addToHistory( final Command command ) {
            if( history.size()>ProjectPlugin.getPlugin().getPreferenceStore().getInt(PreferenceConstants.P_MAX_UNDO))
                history.removeFirst();
            history.addLast(command);
        }
        
        private boolean openWarning( final Command command ) {
            final boolean[] runCommand=new boolean[1];
            if (!(command instanceof UndoableCommand)
                    && ProjectPlugin.getPlugin().getUndoableCommandWarning()) {
                final IPreferenceStore preferenceStore = ProjectPlugin.getPlugin()
                        .getPreferenceStore();
                if (!preferenceStore.getBoolean(PreferenceConstants.P_WARN_IRREVERSIBLE_COMMAND)) {
                    return preferenceStore
                            .getBoolean(PreferenceConstants.P_IRREVERSIBLE_COMMAND_VALUE);
                }
                PlatformGIS.syncInDisplayThread(new Runnable(){
                    public void run() {
                        String string = Messages.CommandManager_warning + command.getName();
                        if ( command instanceof RollbackCommand || 
                                command instanceof CommitCommand )
                            string += "?"; //$NON-NLS-1$
                        else
                            string+=Messages.CommandManager_warning2;
                        MessageDialogWithToggle dialog = MessageDialogWithToggle
                                .openOkCancelConfirm(
                                        PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                                .getShell(),
                                        Messages.CommandManager_warningTitle, string,  
                                        Messages.CommandManager_toggleMessage, false, preferenceStore, PreferenceConstants.P_WARN_IRREVERSIBLE_COMMAND); 
                        runCommand[0] = dialog.getReturnCode() == IDialogConstants.OK_ID;
                        if (dialog.getToggleState()) {
                            preferenceStore
                                    .setValue(PreferenceConstants.P_IRREVERSIBLE_COMMAND_VALUE,
                                            runCommand[0]);
                        }
                    }
                });
            }else{
                return true;
            }
            return runCommand[0];
        }

        /**
         * Notifies the owner that the command has been executed.
         */
        private void notifyOwner( Command command ) {
            
            for( CommandListener listener : completionHandlers ) {
                if (command instanceof NavCommand) {
                    listener.commandExecuted(MapCommandListener.NAV_COMMAND);
                } else {
                    listener.commandExecuted(MapCommandListener.COMMAND);
                }
                
            }

        }

        /**
         * Executes the last undone command, if there are any commands to undo.
         */
        private void redo( IProgressMonitor monitor ) {
            if( undone.isEmpty() )
                return;
            Command command = undone.removeLast();
            monitor.beginTask(Messages.CommandManager_redo + command.getName(), 1000); 
            try {
                if (command instanceof PostDeterminedEffectCommand) {
                    PostDeterminedEffectCommand post = (PostDeterminedEffectCommand) command;
                    post.execute(new SubProgressMonitor(monitor, 1000));
                } else {
                    command.run(new SubProgressMonitor(monitor, 1000));
                }
                addToHistory(command);
                notifyOwner(command);
            } catch (Exception e) {
                handleError(command, e);
            }
        }

        /**
         * Undoes the last command if possible.
         */
        private void undo( IProgressMonitor monitor ) {
            Command c;
            // First check if there's a command on the commands stack that hasn't
            // been executed and should just be removed
            if (!commands.isEmpty()) {
                Request r;
                synchronized (this) {
                    r = commands.peek();
                    if( r.type!=Request.UNDO ){
                    commands.remove(0);
                    c = r.command;
                    if (c instanceof UndoableCommand) {
                        // We've already popped it off...so we're done
                    } else {
                        throw new RuntimeException(
                                "Undoing Commands (No Undoable Command) is not handled " //$NON-NLS-1$
                                        + "yet because it involves rolling back the current transaction and redoing all " //$NON-NLS-1$
                                        + "the commands in the stack"); //$NON-NLS-1$
                    }                    return;
                    }
                }
            }
            // Nothing on the Command stack, we'll actually undo something
            if (!history.isEmpty()) {
                c = history.removeLast();
                if (c instanceof UndoableCommand) {
                    UndoableCommand command = (UndoableCommand) c;
                    monitor.beginTask(Messages.CommandManager_undo + command.getName(), 1000); 
                    try {
                        command.rollback(new SubProgressMonitor(monitor, 1000));
                        addToUndone(command);
                    } catch (Throwable e) {
                        handleRollbackError(command, e);
                    }
                } else {
                    throw new RuntimeException(
                            "Undoing Commands (No Undoable Command) is not handled " //$NON-NLS-1$
                                    + "yet because it involves rolling back the current transaction and redoing all " //$NON-NLS-1$
                                    + "the commands in the stack"); //$NON-NLS-1$
                }
                notifyOwner(c);
            }
        }
        private void addToUndone( UndoableCommand command ) {
            if( undone.size()>ProjectPlugin.getPlugin().getPreferenceStore().getInt(PreferenceConstants.P_MAX_UNDO) )
                undone.removeFirst();
            undone.add(command);
        }

        private void handleError( Command command, Throwable e ) {
            for( ErrorHandler handler : handlers ) {
                handler.handleError(command, e);
            }
        }

        private void handleRollbackError( UndoableCommand command, Throwable e ) {
            for( ErrorHandler handler : handlers ) {
                handler.handleRollbackError(command, e);
            }
        }

        /**
         * Executes all the commands in the history again.
         */
        public void rerunCommands( IProgressMonitor monitor ) {
            Queue<Command> q = history;
            history = new LinkedList<Command>();
            for( Iterator<Command> iter = q.iterator(); iter.hasNext(); ) {
                Command command = (Command) iter.next();
                execute(command, monitor);
            }
        }
    }
    /**
     * TODO Purpose of net.refractions.udig.project.command
     * <p>
     * </p>
     * 
     * @author Jesse
     * @since 1.0.0
     */
    public static class Request {
        /** <code>RUN</code> field */
        public static final int RUN = 0;
        /** <code>UNDO</code> field */
        public static final int UNDO = 1;
        /** <code>REDO</code> field */
        public static final int REDO = 2;
        /** <code>RERUN</code> field */
        public static final int RERUN = 4;

        /** the type of request */
        public final int type;
        /** sync/async * */
        public final boolean async;
        /** the command to be done/undone/redone */
        public final Command command;
        /** flag to signal wether command is complete * */
        public volatile boolean completed;
        public final boolean requestByDisplay;

        /**
         * Construct <code>Request</code>.
         * 
         * @param type the type of request
         * @param command the command to be done/undone/redone
         */
        public Request( int type, Command command, boolean async, boolean requestByDisplay2 ) {
            this.requestByDisplay = requestByDisplay2;
            this.command = command;
            this.type = type;
            this.async = async;
            completed = false;
        }

        /**
         * Determines if the request is synchronous.
         * 
         * @return
         */
        public boolean isSync() {
            return !async;
        }
    }
    /**
     * Execute Command syncrounously. IE wait until command is complete before returning.
     * 
     * @return true if no problems were encountered while queueing command. Problems will typically
     *         occur when the command is synchronous and it times out or is interrupted.
     */
    public boolean syncExecute( Command command ) {
        return execute(command, false);
    }
    public int getMaxHistorySize() {
        return 20;
    }
    /**
     * Execute Command asyncrounously. IE return immediately, do not wait until command is complete
     * before returning.
     * 
     * @return true if no problems were encountered while queueing command. Problems will typically
     *         occur when the command is synchronous and it times out or is interrupted.
     */
    public boolean aSyncExecute( Command command ) {
        return execute(command, true);
    }
    
}
