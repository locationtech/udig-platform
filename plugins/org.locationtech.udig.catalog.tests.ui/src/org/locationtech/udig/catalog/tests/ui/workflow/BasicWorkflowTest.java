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
package org.locationtech.udig.catalog.tests.ui.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.locationtech.udig.catalog.ui.workflow.Listener;
import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.Workflow;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BasicWorkflowTest {
	
	int i = 0;
	Workflow pipe;
	State1 s1;
	State4 s4;
	
	@Before
	public void setUp() throws Exception {
		pipe = new Workflow();
		s1 = new State1();
		s4 = new State4();
		
		pipe.setStates(new State[]{s1,s4});
		i = 1;
	}
	
	@Test(expected = IllegalStateException.class)
	public void testPipeState() {
		pipe.next(new DummyMonitor());
	}
	
	@Test
	public void testNonBlocking() {
		Shell shell = new Shell(Display.getDefault());
		final Dialog dialog = new Dialog(shell) {};
		
		Listener1 l = new Listener1() {
			
			@Override
			public void finished(State state) {
				super.finished(state);
				
				Display.getDefault().asyncExec(
					new Runnable() {

						public void run() {
							dialog.close();
						}
					}
				);
			}
		};
		pipe.addListener(l);
	
		pipe.start();
		pipe.next();
		pipe.next();
		pipe.next();
		pipe.next();
		pipe.next();
		
		//need to open a dialog here to "halt" the ui thread so that the 
		// the workbench doesn't close while the pipe is still running
		dialog.setBlockOnOpen(true);
		dialog.open();
		if (!shell.isDisposed())
			shell.dispose();
		
		assertTrue(l.state1);
		assertTrue(l.state2);
		assertTrue(l.state3);
		assertTrue(l.state4);
		assertTrue(l.state5);
		assertTrue(l.finished);
		assertTrue(!l.fail);
		assertEquals(i,6);

		assertNotNull(pipe.getState(State1.class));
		assertNotNull(pipe.getState(State2.class));
		assertNotNull(pipe.getState(State3.class));
		assertNotNull(pipe.getState(State4.class));
		assertNotNull(pipe.getState(State5.class));
		
		assertTrue(pipe.getState(State1.class).ran);
		assertTrue(pipe.getState(State2.class).ran);
		assertTrue(pipe.getState(State3.class).ran);
		assertTrue(pipe.getState(State4.class).ran);
		assertTrue(pipe.getState(State5.class).ran);
	}
	
	@Test
	public void testBlocking() {
		Listener1 l = new Listener1();
		pipe.addListener(l);
	
		pipe.start(new NullProgressMonitor());
		pipe.next(new NullProgressMonitor());
		pipe.next(new NullProgressMonitor());
		pipe.next(new NullProgressMonitor());
		pipe.next(new NullProgressMonitor());
		pipe.next(new NullProgressMonitor());
		
//		int x = 0;
//		while(!l.finished && x++ < 10) {
//			try {
//				Thread.sleep(500);
//			} 
//			catch (InterruptedException e) {
//				e.printStackTrace();
//				fail();
//			}
//		}
		
		assertTrue(l.state1);
		assertTrue(l.state2);
		assertTrue(l.state3);
		assertTrue(l.state4);
		assertTrue(l.state5);
		assertTrue(l.finished);
		assertTrue(!l.fail);
		assertEquals(i,6);

		assertNotNull(pipe.getState(State1.class));
		assertNotNull(pipe.getState(State2.class));
		assertNotNull(pipe.getState(State3.class));
		assertNotNull(pipe.getState(State4.class));
		assertNotNull(pipe.getState(State5.class));
		
		assertTrue(pipe.getState(State1.class).ran);
		assertTrue(pipe.getState(State2.class).ran);
		assertTrue(pipe.getState(State3.class).ran);
		assertTrue(pipe.getState(State4.class).ran);
		assertTrue(pipe.getState(State5.class).ran);
	}
	
	@Ignore
	@Test
	public void testStateFailureNonBlocking() {
		Shell shell = new Shell(Display.getDefault());
		final Dialog dialog = new Dialog(shell) {};
		
		//test where one state craps out
		s4.run = false;
		
		Listener1 l = new Listener2() {
			@Override
			public void stateFailed(State state) {
				super.stateFailed(state);
				
				if (dialog.getShell().isVisible()) {
					
					dialog.getShell().getDisplay().asyncExec(
						new Runnable() {
							public void run() {
								dialog.close();	
							};
						}
					);	
				}
				
			}
			
			@Override
			public void finished(State state) {
				super.finished(state);
				dialog.close();
			}
		};
		pipe.addListener(l);
	
		pipe.start();
		pipe.next();
		pipe.next();
		pipe.next();
		pipe.next();
		pipe.next();
		
		//need to open a dialog here to "halt" the ui thread so that the 
		// the workbench doesn't close while the pipe is still running
		// create a watchdog to kill it after a specified amount of time
		Runnable runnable = new Runnable() {

			public void run(){
				System.out.println("Running"); //$NON-NLS-1$
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Is dialog active dialog"); //$NON-NLS-1$
				Display.getDefault().syncExec(
					new Runnable() {
						public void run() {
							if (dialog.getShell().isVisible()) {
								dialog.close();
							}		
						}
					}
				);
			}
		};
		new Thread(runnable).start();
		
		dialog.setBlockOnOpen(true);
		if( !l.finished )
		dialog.open();
		if (!shell.isDisposed())
			shell.dispose();
		
		assertTrue(l.state1);
		assertTrue(l.state2);
		assertTrue(l.state3);
		assertTrue(!l.state4);	
		assertTrue(!l.state5);
		assertTrue(!l.finished);
		assertTrue(!l.fail);
		assertEquals(i,4);

		assertNotNull(pipe.getState(State1.class));
		assertNotNull(pipe.getState(State2.class));
		assertNotNull(pipe.getState(State3.class));
		assertNotNull(pipe.getState(State4.class));
		assertNull(pipe.getState(State5.class));
		
		assertTrue(pipe.getState(State1.class).ran);
		assertTrue(pipe.getState(State2.class).ran);
		assertTrue(pipe.getState(State3.class).ran);
		assertTrue(pipe.getState(State4.class).ran);
		
		assertEquals(pipe.getCurrentState(),s4);
	}
	
	@Ignore
    @Test
	public void testStateFailureBlocking() {
		//test where one state craps out
		s4.run = false;
		
		Listener1 l = new Listener2() {
			@Override
			public void finished(State state) {
				super.finished(state);
			}
		};
		pipe.addListener(l);
	
		pipe.start(null);
		pipe.next(null);
		pipe.next(null);
		pipe.next(null);
		pipe.next(null);
		pipe.next(null);
		
		assertTrue(l.state1);
		assertTrue(l.state2);
		assertTrue(l.state3);
		assertTrue(!l.state4);
		assertTrue(!l.state5);
		assertTrue(!l.finished);
		assertTrue(!l.fail);
		assertEquals(i,4);

		assertNotNull(pipe.getState(State1.class));
		assertNotNull(pipe.getState(State2.class));
		assertNotNull(pipe.getState(State3.class));
		assertNotNull(pipe.getState(State4.class));
		assertNull(pipe.getState(State5.class));
		
		assertTrue(pipe.getState(State1.class).ran);
		assertTrue(pipe.getState(State2.class).ran);
		assertTrue(pipe.getState(State3.class).ran);
		assertTrue(pipe.getState(State4.class).ran);
		
		assertEquals(pipe.getCurrentState(),s4);
	}

	@Ignore
	@Test
	public void testRun() {
		
		assertTrue(!pipe.isFinished());
		assertTrue(!pipe.isStarted());
		assertTrue(!pipe.getState(State1.class).ran);
		assertTrue(!pipe.getState(State4.class).ran);
		
		
		pipe.run(new DummyMonitor());
		
		assertTrue(pipe.isFinished());
		assertTrue(pipe.isStarted());
		assertTrue(pipe.getState(State1.class).ran);
		assertTrue(pipe.getState(State2.class).ran);
		assertTrue(pipe.getState(State3.class).ran);
		assertTrue(pipe.getState(State4.class).ran);
		
	}
	
	class Listener1 implements Listener {

		boolean state1 = false;
		boolean state2 = false;
		boolean state3 = false;
		boolean state4 = false;
		boolean state5 = false;
		
		boolean fail = false;
		boolean finished = false;
		
		public void started(State first) {
			
			
		}
		
		public void forward(State state, State prev) {
			
		}

		public void backward(State current, State next) {
			// TODO Auto-generated method stub
		}
		
		public void statePassed(State state) {
			switch(i) {
				case 1:
					state1 = state instanceof State1;
					i++;
					break;
				case 2:
					state2 = state instanceof State2;
					i++;
					break;
				case 3:
					state3 = state instanceof State3;
					i++;
					break;
				case 4:
					state4 = state instanceof State4;
					i++;
					break;
				case 5:
					state5 = state instanceof State5;
					i++;
					break;
					
				default:
					fail = true;
			}
		}
		
		public void stateFailed(State state) {
			fail = true;
		}

		public void finished(State last) {
			finished = true;
		}
		
	}
	
	class Listener2 extends Listener1 {
		
		boolean incomplete = false;
	
		@Override
		public void stateFailed(State state) {
			incomplete = state instanceof State4; 
		}
	}
	
	private class State1 extends SimpleState {
		@Override
		public State next() {
			return new State2();
		}
	}
	
	private class State2 extends SimpleState {
		@Override
		public State next() {
			return new State3();
		}
	}
	
	private class State3 extends SimpleState {

		@Override
		public State next() {
			return null;
		}
		
	}
	
	private class State4 extends SimpleState {
		
			@Override
		public State next() {
			return new State5();
		}
	}
	
	private class State5 extends SimpleState {
		
		@Override
		public State next() {
			return null;
		}
	}
	
	
}
