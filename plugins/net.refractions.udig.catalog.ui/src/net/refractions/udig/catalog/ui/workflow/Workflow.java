package  net.refractions.udig.catalog.ui.workflow;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class Workflow {

	/** set of primary states **/
	State[] states;

	/** map of class to objects for states **/
	Map<Class<State>, State> lookup;

	/** queue of primary states**/
	LinkedList<State> queue;

	/** current state **/
	State current;

	/** listeners **/
	Set<Listener> listeners = new CopyOnWriteArraySet<Listener>();

	/** concurrent access lock **/
	Lock lock = new ReentrantLock();

	/** flag to indicate wither the pipe is started/finished **/
	boolean started = false;
	boolean finished = false;

	/** context object, states use this object as a seed to perform work **/
	Object context;

	/**
	 * Creates an empty workflow. When using this constructor the states of the
	 * workflow must be set before the workflow can be started.
	 *
	 */
	public Workflow() {
		//do nothing
	}

	/**
	 * Creates a workflow from a set of workflow states.
	 *
	 * @param states The states of the workflow.
	 */
	public Workflow(State[] states) {
		setStates(states);
	}

	/**
	 * Adds a listener to the workflow. The listener collection is a set which
	 * prevents duplicates. For this reason clients may call this method
	 * multiple times with the same object.
	 *
	 * @param l The listening object.
	 */
	public void addListener(Listener l) {
		listeners.add(l);
	}

	public void removeListener(Listener l) {
		listeners.remove(l);
	}

	/**
	 * Returns an object representing a context for which the states can feed
	 * off of. The context is often provided via a workbench selection.
	 *
	 * @return The context object, or null if none has been set
	 */
	public Object getContext() {
		return context;
	}

	/**
	 * Sets the object representing a context for which states can feed off of.
	 * The context is often provided via a workbench selection.
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
        if( states!=null )
            i2=states.length;
        State[] s=new State[i2];
        if( states!=null )
            System.arraycopy(states, 0, s, 0, s.length);

		this.states = s;
		queue = new LinkedList<State>();
		lookup = new HashMap<Class<State>, State>();
		for (int i = 0; i < s.length; i++) {
			s[i].setWorkflow(this);
			queue.addLast(s[i]);
			lookup.put((Class<State>)s[i].getClass(), s[i]);
		}
	}

	/**
	 * @return the primary set of states of the workflow.
	 */
	public State[] getStates() {
        State[] s=new State[states.length];
        System.arraycopy(states, 0, s, 0, s.length);
		return s;
	}

	/**
	 * Returns a state of a specific class.
	 *
	 * @param <T> The type of the state.
	 * @param c The class of the state.
	 *
	 * @return The state instance, or null if none exists.
	 */
	public <T> T getState(Class<T> c) {
		return c.cast(lookup.get(c));
	}

	/**
	 * Starts the workflow by moving to the first state. This method must only
	 * be called once. This method executes asynchronously performing work in a
	 * separate thread and does not block.
	 */
	public void start() {

		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {

				start(monitor);
			}
		};

		//synchronized (mutex) {
		//lock.acquire();

		//try {
			//assertNotStarted();
			PlatformGIS.run(runnable);
	//	}
//		catch(IllegalStateException e) {
//			lock.release();
//			throw new IllegalStateException(e);
//		}
//		catch(Throwable t) {
//			lock.release();
//			CatalogUIPlugin.log(t.getLocalizedMessage(),t);
//		}
		//}
	}

	/**
	 * Starts the workflow by moving to the first state. This method must only
	 * be called once. This method executes synchronously performing work in
	 * the current thread and blocks.
	 */
	public void start(IProgressMonitor monitor) {
		//synchronized (mutex) {
		//lock.steal();
		try {
            lock.lockInterruptibly();
        } catch (InterruptedException e1) {
            return;
        }

		try {
			//move to first state
			current = queue.removeFirst();
			current.setPrevious(null);
			current.init(monitor);

			started = true;
			dispatchStarted(current);
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			lock.unlock();
		}
		//}
	}

	/**
	 * Moves the workflow to the next state. This method executes asynchronously
	 * performing work in a separate thread and does not block.
	 *
	 */
	public void next() {

		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {

				next(monitor);
			}
		};

		PlatformGIS.run(runnable);

	}

	/**
	 * Moves the workflow to the next state. This method executes synchronously
	 * performing work in the current thread and blocks.
	 *
	 * @return True if the state
	 */
	@SuppressWarnings("unchecked")
    public void next (IProgressMonitor monitor) {
        IProgressMonitor monitor2 = monitor;
		if ( monitor2==null )
			monitor2=new NullProgressMonitor();
		//synchronized (mutex) {
		//lock.steal();
		try {
            lock.lockInterruptibly();
        } catch (InterruptedException e1) {
            return;
        }

		try {
			assertStarted();
			assertNotFinished();


            String name = getCurrentState().getName();
            String string = name!=null?name:Messages.Workflow_busy;
            monitor2.beginTask(string, 20);
            monitor2.setTaskName(string);

			if (queue == null) {
				String msg = "No states";  //$NON-NLS-1$
				throw new IllegalStateException(msg);
			}

			//run it
			boolean ok = false;
			SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor2, 10);
			try {
				ok = current.run(subProgressMonitor) && !monitor2.isCanceled();
			}
			catch(Throwable t) {
				CatalogUIPlugin.log(t.getLocalizedMessage(), t);
			}finally{
				subProgressMonitor.done();
			}

			if (ok) {
				//dispatch the event
				dispatchPassed(current);

				//grab the next state, try pulling one from the current state
				State next = current.next();
				if (next == null) {
					//try pulling from the queue
					if (!queue.isEmpty())
						next = queue.removeFirst();
				}
				else {
					//add to lookup tables
					lookup.put((Class<State>) next.getClass(), next);
				}

				if (next != null) {
					//set the back pointer and call lifecyclmutexe events
					next.setWorkflow(this);
					next.setPrevious(current);
					try{
						subProgressMonitor=new SubProgressMonitor(monitor2,10);
						next.init(subProgressMonitor);
					}finally{
						subProgressMonitor.done();
					}
					State prev = current;
					current = next;

					dispatchForward(current, prev);
				}
				else {
					// no more states, we are finished
					State last = current;
					current = null;

					finished = true;
					dispatchFinished(last);
				}
			}
			else {
				//run did not succeed
				dispatchFailed(current);
			}
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			lock.unlock();
		}
		//}
	}

	/**
	 * Moves the workflow to the previous state. This method executes
	 * asynchronously performing work in a separate thread and does not block.
	 */
	public void previous() {

		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {

				previous(monitor);
			}
		};

		//synchronized (mutex) {
		try {
            lock.lockInterruptibly();
        } catch (InterruptedException e1) {
            return;
        }
		try {
			assertStarted();
			assertNotFinished();
            runnable.run(new NullProgressMonitor());
		}
		catch(IllegalStateException e) {
			//lock.release();
			throw new IllegalStateException(e);
		}
		catch(Throwable t) {
			//lock.release();
			CatalogUIPlugin.log(t.getLocalizedMessage(),t);
		}
		finally {
		    lock.unlock();
        }
		//}
	}

	/**
	 * Moves the workflow to the previous state. This method executes
	 * synchronously performing work in the current thread and blocks.
	 */
	public void previous(IProgressMonitor monitor) {
		//synchronized (mutex) {
			lock.lock();

		try {
			assertStarted();
			assertNotFinished();

			if (current.getPreviousState() != null) {
				// if this state is a "primary" state, place back in front of queue
				if (isPrimaryState(current))
					queue.addFirst(current);

				State next = current;
				current = current.getPreviousState();

				//renitialize the state and dispatch the started event
				current.init(monitor);
				dispatchBackward(current, next);

			}
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			lock.unlock();
		}
		//}
	}

	/**
	 * @return True if the workflow has been started with a call to #start().
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * @return True if the workflow has been finished. The workflow is
	 * considered finished after the call to #next(), while in the final state.
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
	 * Determines if the workflow has more states. It is important to note
	 * that this method may not 100% accurate depending on the behaviour of
	 * states dynamically creating new states.
	 *
	 * @return True if there are more states, otherwise false.
	 */
	public boolean hasMoreStates() {
		//if the queue is not empty, we definitely have more states
		if (!queue.isEmpty())
			return true;

		//ask the current state
		if (current != null)
			return current.hasNext();

		return false;
	}

	/**
	 * Runs the workflow from its current state. The workflow will continue
	 * to walk through the states while the state is finished.
	 *
	 * @param monitor A progress monitor.
	 *
	 * @return True if the pipe was able to run to completion, otherwise false.
	 */
	public boolean run(IProgressMonitor monitor) {
		Runner runner = new Runner(this);
		return runner.run(monitor);
	}

	/**
	 * Resets the workflow. This method may only be called if the workflow is
	 * in a finished state. Once reset the workflow lifecycle starts again with
	 * a call to @see DataPipeline#start().
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

	protected void dispatchStarted(State start) {
		try {
			for (Listener l : listeners) {
				l.started(start);
			}
		}
		catch(Throwable t) {
			CatalogUIPlugin.log(t.getLocalizedMessage(), t);
		}
	}

	protected void dispatchForward(State current, State prev) {
		try {
			for (Listener l : listeners) {
				l.forward(current, prev);
			}
		}
		catch(Throwable t) {
			CatalogUIPlugin.log(t.getLocalizedMessage(), t);
		}
	}

	protected void dispatchBackward(State current, State next) {
		try {
			for (Listener l : listeners) {
				l.backward(current,next);
			}
		}
		catch(Throwable t) {
			CatalogUIPlugin.log(t.getLocalizedMessage(), t);
		}
	}

	protected void dispatchPassed(State state) {
		try {
			for (Listener l : listeners) {
				l.statePassed(state);
			}
		}
		catch(Throwable t) {
			CatalogUIPlugin.log(t.getLocalizedMessage(), t);
		}
	}

	protected void dispatchFailed( State state ) {
        for( Listener l : listeners ) {
            try {
                l.stateFailed(state);
            } catch (Throwable t) {
                CatalogUIPlugin.log(t.getLocalizedMessage(), t);
            }
        }
    }

	protected void dispatchFinished(State last) {
		try {
			for (Listener l : listeners) {
				l.finished(last);
			}
		}
		catch(Throwable t) {
			CatalogUIPlugin.log(t.getLocalizedMessage(), t);
		}
	}

	public static class Runner implements Listener {
		Workflow pipe;
		boolean stopped;

		Runner(Workflow pipe) {
			this.pipe = pipe;
		}

		public boolean run(IProgressMonitor monitor) {
			try{
			monitor.beginTask(Messages.Workflow_task_name, IProgressMonitor.UNKNOWN);
			stopped = false;
			pipe.addListener(this);

			//first check if the pipe is already finished
			if (pipe.isFinished())
				return true;

			//may need to start
			if (!pipe.isStarted()) {
				pipe.start(monitor);
			}

			while(!stopped && !pipe.isFinished()) {
				pipe.next(new SubProgressMonitor(monitor,10, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
			}

			pipe.removeListener(this);
			return !stopped;
			}finally{
				monitor.done();
			}
		}

		public void forward(State current, State prev) {
			// do nothing
		}

		public void backward(State current, State next) {
			// do nothing
		}

		public void statePassed(State state) {
			// do nothing
		}

		public void stateFailed(State state) {
			stopped = true;
		}

		public void started(State first) {
			// do nothing
		}

		public void finished(State last) {
			// do nothing
		}
	}

	public static abstract class State {

		/** previous state **/
		State previous;

		/** the workflow **/
		Workflow workflow;

		/**
		 * @param workflow The workflow containing the state.
		 */
		public void setWorkflow(Workflow workflow) {
			this.workflow = workflow;
		}

		public abstract String getName();

		/**
		 * @return the workflow containing all the states.
		 */
		public Workflow getWorkflow() {
			return workflow;
		}

		/**
		 * Sets the previous state. The first method in the lifecycle of the
		 * state which is called by the data workflow to track the states that
		 * have been completed. Should not be called by client code.
		 *
		 * @param previous The previous state.
		 */
		public void setPrevious(State previous) {
			this.previous = previous;
		}

		/**
		 * Returns the previous state.
		 *
		 * @return The state previous to this state, or null if no such state
		 * exists.
		 */
		public State getPreviousState() {
			return previous;
		}

		/**
		 * Initialize the state. This is the second method in the lifecycle of
		 * the state. It is called after #setPrevious(). If the state needs to
		 * "seed" itself with any context, that should occur here.
		 */
		public void init(IProgressMonitor monitor) throws IOException {
			//do nothing
		}

		/**
		 * Performs any "hard" work. This method is provided is provided for
		 * states which have to block to get work done. For instance, making
		 * a connection to a remote service. This method returns a boolean
		 * which signals weather the state was able to get the work done.
		 *
		 * @param monitor A progress monitor.
		 *
		 * @return True if the state was able to complete its job, otherwise
		 *  false.
		 * @throws IOException
		 */
		public boolean run(IProgressMonitor monitor)
			throws IOException {

			return true;
		}

		/**
		 * Determines if the state can dynamically create a new state to be
		 * the next active state of the workflow. Note, in most cases this is
		 * equivalent to <code>next() != null</code>. However some
		 * implementations require that next() be called only once, as it is a
		 * lifecycle event.
		 *
		 * @return true if the state can create a new state, otherwise false.
		 */
		public boolean hasNext() {
			return false;
		}

		/**
		 * The final method in the lifecycle of the state. This method is
		 * used for states to dynamically link to each other. This method
		 * returns null to indicate no state.
		 *
		 * @return A new state which is to become the next active state,
		 * 	otherwise null.
		 */
		public State next() {
			return null;
		}
	}

	public static interface Listener {

		/**
		 * Event thrown when the pipe moves to a new state in the forward
		 * direction.
		 *
		 * @param current The current state.
		 * @param prev The state before the current state.
		 */
		void forward(State current, State prev);

		/**
		 * Event thrown when the pipe moves to a new state in a backward
		 * direction.
		 *
		 * @param current The current state.
		 * @param next The state after the current state.
		 */
		void backward(State current, State next);

		/**
		 * Event thrown when a state successfully completes its job.
		 *
		 * @param state The current state.
		 */
		void statePassed(State state);

		/**
		 * Event thrown when a state can not complete its job.
		 *
		 * @param state The current state.
		 */
		void stateFailed(State state);

		/**
		 * Event thrown when the workflow is started.
		 *
		 * @param first The first state of the pipe
		 */
		void started(State first);

		/**
		 * Event thrown when workflow is finished.
		 *
		 * @param last The last state of the pipe
		 */
		void finished(State last);
	}

}
