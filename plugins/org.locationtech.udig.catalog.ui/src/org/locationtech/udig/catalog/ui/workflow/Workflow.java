/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.workflow;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.core.Pair;
import org.locationtech.udig.ui.OffThreadProgressMonitor;

/**
 * Basically is a state machine. It has a set of states and handles the stepping through the states
 * when next, previous or run are called.
 * <p>
 * This is an active object IE it has a thread and most method calls to the object are ran in that
 * thread. The method calls are blocking and if the call is in the display thread it is blocked in a
 * "nice" manner. IE the Display.readAndDispatch method is called.
 *
 * @author justin
 * @since 1.0
 */
public class Workflow {

    /** set of primary states */
    private State[] states;

    /** map of class to objects for states */
    private Map<Class<State>, State> lookup;

    /** queue of primary states* */
    private LinkedList<State> queue;

    /** current state * */
    private State current;

    /** listeners * */
    private Set<Listener> listeners = new CopyOnWriteArraySet<>();

    /** flag to indicate wither the pipe is started/finished * */
    boolean started = false;

    private boolean finished = false;

    /** context object, states use this object as a seed to perform work * */
    private Object context;

    private ThreadingStrategy threading;

    /**
     * Creates an empty workflow. When using this constructor the states of the workflow must be set
     * before the workflow can be started.
     */
    public Workflow(ThreadingStrategy threading) {
        threading.init();
        this.threading = threading;
    }

    /**
     * Creates a workflow from a set of workflow states.
     *
     * @param states The states of the workflow.
     */
    public Workflow(ThreadingStrategy strategy, State[] states) {
        this(strategy);
        setStates(states);
    }

    /**
     * Creates an empty workflow. When using this constructor the states of the workflow must be set
     * before the workflow can be started.
     */
    public Workflow() {
        this(new DefaultThreading());
    }

    /**
     * Creates a workflow from a set of workflow states.
     *
     * @param states The states of the workflow.
     */
    public Workflow(State[] states) {
        this(new DefaultThreading(), states);
    }

    /**
     * Adds a listener to the workflow. The listener collection is a set which prevents duplicates.
     * For this resason clients may call this method multiple times with the same object.
     *
     * @param l The listening object.
     */
    public void addListener(Listener l) {
        listeners.add(l);
    }

    public void removeListener(Listener l) {
        listeners.remove(l);
    }

    public void shutdown() {
        threading.shutdown();
    }

    /**
     * Returns an object representing a context for which the states can feed off of. The context is
     * often provided via a workbench selection.
     *
     * @return The context object, or null if none has been set
     */
    public Object getContext() {
        return context;
    }

    /**
     * Sets the object representing a context for which states can feed off of. The context is often
     * provided via a workbench selection.
     *
     * @param context The context object to set.
     */
    public void setContext(Object context) {
        this.context = context;
    }

    /**
     * Sets the primary set of states of the workflow.
     *
     * @param states An array of states.
     */
    @SuppressWarnings("unchecked")
    public void setStates(State[] states) {
        int i2 = 0;
        if (states != null)
            i2 = states.length;
        State[] s = new State[i2];
        if (states != null)
            System.arraycopy(states, 0, s, 0, s.length);

        this.states = s;
        queue = new LinkedList<>();
        lookup = new HashMap<>();
        for (int i = 0; i < s.length; i++) {
            s[i].setWorkflow(this);
            queue.addLast(s[i]);
            lookup.put((Class<State>) s[i].getClass(), s[i]);
        }
    }

    /**
     * @return the primary set of states of the workflow.
     */
    public State[] getStates() {
        State[] s = new State[states.length];
        System.arraycopy(states, 0, s, 0, s.length);
        return s;
    }

    /**
     * Goes through the lookup values and find the first match for the provided c.
     *
     * @param <T> The type of the state.
     * @param c The class of the state.
     * @return The state instance, or null if none exists.
     */
    public <T> T getState(Class<T> c) {

        State state = lookup.get(c);
        if (state == null) {
            // see if we have a subclass of the type
            for (State current : lookup.values()) {
                if (c.isAssignableFrom(current.getClass())) {
                    state = current;
                    break;
                }
            }
        }
        return c.cast(state);
    }

    /**
     * Starts the workflow by moving to the first state. This method must only be called once. This
     * method executes asynchronously performing work in a seperate thread and does not block.
     */
    public void start() {
        start(new NullProgressMonitor());
    }

    /**
     * Starts the workflow by moving to the first state. This method must only be called once. This
     * method executes synchronously performing work in the current thread and blocks.
     */
    public void start(final IProgressMonitor monitor) {
        final IProgressMonitor progressMonitor = checkMonitor(monitor);

        threading.init();

        Runnable request = new Runnable() {
            @Override
            public void run() {
                try {
                    // move to first state
                    current = queue.removeFirst();
                    current.setPrevious(null);
                    current.init(progressMonitor);

                    started = true;
                    dispatchStarted(current);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String toString() {
                return "Workflow.java start() task"; //$NON-NLS-1$
            }
        };

        threading.run(request);
    }

    /**
     * This is an estimate of whether or not the Workflow can be run to completion.
     *
     * @return true if it is likely that the workflow can be run to completion.
     */
    public boolean dryRun() {
        Queue<State> copiedQueue = new LinkedList<>(queue);
        State state = getCurrentState();

        while (state != null) {
            Pair<Boolean, State> dryRunResult = state.dryRun();
            if (!dryRunResult.getLeft()) {
                // the dry run predicts failure
                return false;
            }

            state = dryRunResult.getRight();
            if (state == null) {
                state = copiedQueue.poll();
            }
        }

        // we finished with no failures
        return true;
    }

    /**
     * Moves the workflow to the next state. This method executes asynchronously performing work in
     * a seperate thread and does not block.
     */
    public void next() {
        next(new NullProgressMonitor());
    }

    /**
     * Moves the workflow to the next state. This method executes synchronously performing work in
     * the current thread and blocks.
     *
     * @return True if the state
     */
    public void next(final IProgressMonitor monitor) {

        final IProgressMonitor progressMonitor = checkMonitor(monitor);

        Runnable request = new Runnable() {
            @Override
            public void run() {
                doNextInternal(progressMonitor);
            }

            @Override
            public String toString() {
                return "Workflow.java run() task"; //$NON-NLS-1$
            }
        };

        threading.run(request);
    }

    private IProgressMonitor checkMonitor(final IProgressMonitor monitor) {
        if (monitor == null) {
            throw new NullPointerException("monitor is null"); //$NON-NLS-1$
        }

        final IProgressMonitor progressMonitor;
        if (monitor instanceof OffThreadProgressMonitor) {
            progressMonitor = monitor;
        } else {
            progressMonitor = new OffThreadProgressMonitor(monitor);
        }
        return progressMonitor;
    }

    @SuppressWarnings("unchecked")
    private void doNextInternal(IProgressMonitor monitor) {

        try {
            assertStarted();
            assertNotFinished();

            String name = getCurrentState().getName();
            String string = name != null ? name : Messages.Workflow_busy;
            monitor.beginTask(string, 20);
            monitor.setTaskName(string);

            if (queue == null) {
                String msg = "No states"; //$NON-NLS-1$
                throw new IllegalStateException(msg);
            }

            // run it
            boolean ok = false;
            SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
            try {
                ok = current.run(subMonitor) && !monitor.isCanceled();
            } catch (Throwable t) {
                CatalogUIPlugin.log(t.getLocalizedMessage(), t);
                if (Platform.inDevelopmentMode()) {
                    System.out.println(
                            "Could not " + current.getName() + ":" + t.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
                    t.printStackTrace();
                }
            } finally {
                subMonitor.done();
            }

            if (ok) {
                // dispatch the event
                dispatchPassed(current);

                // grab the next state, try pulling one from the current state
                State next = current.next();
                if (next == null) {
                    // try pulling from the queue
                    if (!queue.isEmpty())
                        next = queue.removeFirst();
                } else {
                    // add to lookup tables
                    lookup.put((Class<State>) next.getClass(), next);
                }

                if (next != null) {
                    // set the back pointer and call lifecyclmutexe events
                    next.setWorkflow(this);
                    next.setPrevious(current);
                    try {
                        subMonitor = SubMonitor.convert(monitor, 10);
                        next.init(subMonitor);
                    } finally {
                        subMonitor.done();
                    }
                    State prev = current;
                    current = next;

                    dispatchForward(current, prev);
                } else {
                    // no more states, we are finished
                    State last = current;
                    current = null;

                    finished = true;
                    threading.shutdown();
                    dispatchFinished(last);
                }
            } else {
                // run did not succeed
                dispatchFailed(current);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Moves the workflow to the previous state. This method executes asynchronously performing work
     * in a seperate thread and does not block.
     */
    public void previous() {
        previous(new NullProgressMonitor());
    }

    /**
     * Moves the workflow to the previous state. This method executes synchronously performing work
     * in the current thread and blocks.
     */
    public void previous(final IProgressMonitor monitor) {
        final IProgressMonitor progressMonitor = checkMonitor(monitor);

        Runnable request = new Runnable() {
            @Override
            public void run() {
                try {
                    assertStarted();
                    assertNotFinished();

                    if (current.getPreviousState() != null) {
                        // if this state is a "primary" state, place back in front of queue
                        if (isPrimaryState(current))
                            queue.addFirst(current);

                        State next = current;
                        current = current.getPreviousState();

                        // renitialize the state and dispatch the started event
                        current.init(progressMonitor);
                        dispatchBackward(current, next);

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String toString() {
                return "Workflow.java run() task"; //$NON-NLS-1$
            }
        };

        threading.run(request);
    }

    /**
     * @return True if the workflow has been started with a call to #start().
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * @return True if the workflow has been finished. The workflow is considered finished after the
     *         call to #next(), while in the final state.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * @return the current state of the workflow.
     */
    public State getCurrentState() {
        return current;
    }

    /**
     * Determines if the workflow has more states. It is important to note that this method may not
     * 100% accurate depending on the behaviour of states dynamically creating new states.
     *
     * @return True if there are more states, otherwise false.
     */
    public boolean hasMoreStates() {
        // if the queue is not empty, we definitely have more states
        if (!queue.isEmpty())
            return true;

        // ask the current state
        if (current != null)
            return current.hasNext();

        return false;
    }

    /**
     * Runs the workflow from its current state. The workflow will continue to walk through the
     * states while the state is finished.
     *
     * @param monitor A progress monitor.
     * @return True if the pipe was able to run to completion, otherwise false.
     */
    public boolean run(IProgressMonitor monitor) {
        if (monitor == null) {
            throw new NullPointerException("monitor is null"); //$NON-NLS-1$
        }

        WorkflowRunner runner = new WorkflowRunner(this);
        return runner.run(monitor);
    }

    /**
     * Resets the workflow. This method may only be called if the workflow is in a finished state.
     * Once reset the workflow lifecycle starts again with a call to
     *
     * @see DataPipeline#start().
     */
    public void reset() {
        assertFinished();

        started = finished = false;
        setStates(states);
    }

    protected void assertStarted() {
        if (!started) {
            String msg = "Not started"; //$NON-NLS-1$
            throw new IllegalStateException(msg);
        }
    }

    protected void assertNotStarted() {
        if (started) {
            String msg = "Already started"; //$NON-NLS-1$
            throw new IllegalStateException(msg);
        }
    }

    protected void assertFinished() {
        if (!finished) {
            String msg = "Not finished"; //$NON-NLS-1$
            throw new IllegalStateException(msg);
        }
    }

    protected void assertNotFinished() {
        if (finished) {
            String msg = "Already finished"; //$NON-NLS-1$
            throw new IllegalStateException(msg);
        }
    }

    protected boolean isPrimaryState(State state) {
        for (int i = 0; i < states.length; i++) {
            if (states[i].equals(state))
                return true;
        }

        return false;
    }

    protected void dispatchStarted(final State start) {
        for (final Listener l : listeners) {
            l.started(start);
        }
    }

    protected void dispatchForward(final State current, final State prev) {
        for (final Listener l : listeners) {
            l.forward(current, prev);
        }
    }

    protected void dispatchBackward(final State current, final State next) {
        for (final Listener l : listeners) {
            l.backward(current, next);
        }
    }

    protected void dispatchPassed(final State state) {
        for (final Listener l : listeners) {
            l.statePassed(state);
        }
    }

    protected void dispatchFailed(final State state) {
        for (final Listener l : listeners) {
            l.stateFailed(state);
        }
    }

    protected void dispatchFinished(final State last) {
        for (final Listener l : listeners) {
            l.finished(last);
        }
    }

    @Override
    public String toString() {
        StringBuffer text = new StringBuffer();
        text.append("Workflow: Start "); //$NON-NLS-1$
        if (started) {
            text.append(" -> "); //$NON-NLS-1$
        } else {
            text.append(" -- "); //$NON-NLS-1$
        }
        for (State state : states) {
            if (state == current) {
                text.append("["); //$NON-NLS-1$
            }
            text.append(state.getName());
            if (state == current) {
                text.append("]"); //$NON-NLS-1$
            }
            if (this.queue.contains(state)) {
                text.append(" -- "); //$NON-NLS-1$
            } else {
                text.append(" -> "); //$NON-NLS-1$
            }
        }
        if (this.finished) {
            text.append("[Finish]"); //$NON-NLS-1$
        } else {
            text.append("Finish"); //$NON-NLS-1$
        }
        return text.toString();
    }

    /**
     * Listens to the workflow as it runs; will run each stage in turn.
     */
    public static class WorkflowRunner implements Listener {
        Workflow pipe;

        boolean stopped;

        WorkflowRunner(Workflow pipe) {
            this.pipe = pipe;
        }

        /**
         * Will run the workflow; and return true if the workflow if completed
         *
         * @param monitor
         * @return true if completed
         */
        public boolean run(final IProgressMonitor monitor) {
            final boolean[] result = new boolean[1];

            // run in the Workflow thread
            pipe.threading.run(new Runnable() {
                @Override
                public void run() {
                    result[0] = runInternal(monitor);
                }
            });
            return result[0];
        }

        /**
         * Carefully runs the workflow step by step until finished or stoppped.
         *
         * @param monitor
         * @return true if completed; false if stopped
         */
        private boolean runInternal(IProgressMonitor monitor) {
            try {
                monitor.beginTask(Messages.Workflow_task_name, IProgressMonitor.UNKNOWN);
                stopped = false;
                pipe.addListener(this);

                // first check if the pipe is already finished
                if (pipe.isFinished())
                    return true;

                // may need to start
                if (!pipe.isStarted()) {
                    pipe.start(monitor);
                }

                while (!stopped && !pipe.isFinished()) {
                    pipe.next(SubMonitor.convert(monitor, 10));
                }

                pipe.removeListener(this);
                return !stopped;
            } finally {
                monitor.done();
            }
        }

        /**
         * We are not interesed in the workflow moving forward
         */
        @Override
        public void forward(State current, State prev) {
            // do nothing
        }

        /**
         * We are not interested in the workflow moving backward
         */
        @Override
        public void backward(State current, State next) {
            // do nothing
        }

        /**
         * We are not interested in a state validating
         */
        @Override
        public void statePassed(State state) {
            // do nothing
        }

        /**
         * If the state has failed our workflow has stopped - and we need human intervention
         */
        @Override
        public void stateFailed(State state) {
            stopped = true;
        }

        /**
         * We are not interested in starting
         */
        @Override
        public void started(State first) {
            // do nothing
        }

        /**
         * We are not interested in the workflow finishing
         */
        @Override
        public void finished(State last) {
            // do nothing
        }
    }

}
