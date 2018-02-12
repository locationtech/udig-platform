/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.command;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.commands.DefaultErrorHandler;
import org.locationtech.udig.project.tests.support.AbstractProjectTestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.junit.Ignore;
import org.junit.Test;

public class CommandManagerTest extends AbstractProjectTestCase {
	
    @Test
    public void testExecuteASync() {
        CommandManager manager=new CommandManager("test", new DefaultErrorHandler(), new CommandListener(){ //$NON-NLS-1$

            public void commandExecuted( int commandType ) {
            }
            
        });
        final Result result=new Result();
        manager.aSyncExecute(new Command(){

            public void run( IProgressMonitor monitor ) throws Exception {
                result.ran=true;
            }

            public Command copy() {
                return null;
            }

            public String getName() {
                return null;
            }
            
        });
        synchronized (this) {
            
            try {
                for (int i=0; i<3 && !result.ran; i++)
                    wait(1000);
            } catch (InterruptedException e) {
                // TODO Catch e
            }
        }
        assertTrue( result.ran );
    }

    @Test
    public void testExecuteSync() {
        CommandManager manager=new CommandManager("test", new DefaultErrorHandler(), new CommandListener(){ //$NON-NLS-1$

            public void commandExecuted( int commandType ) {
            }
            
        });
        final Result result=new Result();
        manager.syncExecute(new Command(){

            public void run( IProgressMonitor monitor ) throws Exception {
                result.ran=true;
            }

            public Command copy() {
                return null;
            }

            public String getName() {
                return null;
            }
            
        });
        
        assertTrue( result.ran );
    }

    @Test
    @Ignore(" ")
    public void testExecuteSyncDeadLocks() {
        final CommandManager manager=new CommandManager("test", new DefaultErrorHandler(), new CommandListener(){ //$NON-NLS-1$

            public void commandExecuted( int commandType ) {
            }
            
        }, 2000);
        final Result result=new Result();
        result.ran = false;
        final Result resultInner=new Result();
        resultInner.ran = false;
        manager.syncExecute(new Command(){

            public void run( IProgressMonitor monitor ) throws Exception {
                manager.syncExecute(new Command(){

                    public void run( IProgressMonitor monitor ) throws Exception {
                        resultInner.ran=true;
                    }

                    public Command copy() {
                        return null;
                    }

                    public String getName() {
                        return null;
                    }
                    
                });
                result.ran=true;
            }

            public Command copy() {
                return null;
            }

            public String getName() {
                return null;
            }
            
        });
        Thread.yield();
        assertTrue( result.ran );
        assertTrue( resultInner.ran );
    }

    @Test
    public void testExecuteSyncInDisplayThread() {
        final CommandManager manager=new CommandManager("test", new DefaultErrorHandler(), new CommandListener(){ //$NON-NLS-1$

            public void commandExecuted( int commandType ) {
            }
            
        }, -1);
        final Result result=new Result();
        final Result resultInner=new Result();
         
        manager.syncExecute(new Command(){

            public void run( IProgressMonitor monitor ) throws Exception {
            	Display.getDefault().syncExec(new Runnable() {
					public void run() {
		                manager.syncExecute(new Command(){

		                    public void run( IProgressMonitor monitor ) throws Exception {
		                    	Display.getDefault().syncExec(new Runnable(){

									public void run() {
				                        resultInner.ran=true;										
									}
		                    		
		                    	});
		                    }

		                    public Command copy() {
		                        return null;
		                    }

		                    public String getName() {
		                        return null;
		                    }
		                    
		                });
						
					}
				});
                result.ran=true;
            }

            public Command copy() {
                return null;
            }

            public String getName() {
                return null;
            }
            
        });
        Thread.yield();
        assertTrue( result.ran );
        assertTrue( resultInner.ran );
    }

    @Test
    public void testTimeoutOn() throws Exception {
    	//test timeout
    	CommandManager manager=new CommandManager("test", new DefaultErrorHandler(), new CommandListener(){ //$NON-NLS-1$

            public void commandExecuted( int commandType ) {
            }
            
        }, -2);

        final Result result=new Result();
    	boolean completed=manager.syncExecute(new Command(){

            public void run( IProgressMonitor monitor ) throws Exception {
            	Thread.sleep(1000);
                result.ran=true;
            }

            public Command copy() {
                return null;
            }

            public String getName() {
                return null;
            }
            
        });
    	assertFalse(completed);
    	assertFalse(result.ran);
    	
	}
    
    @Test
    public void testTimeoutOff() throws Exception {
//    	test no timeout
    	CommandManager manager = new CommandManager("test", new DefaultErrorHandler(), new CommandListener(){ //$NON-NLS-1$

            public void commandExecuted( int commandType ) {
            }
            
        }, -1);

        final Result result=new Result();
    	boolean completed = manager.syncExecute(new Command(){

			public void run(IProgressMonitor monitor) throws Exception {
				Thread.sleep(1000);
				result.ran=true;
			}

			public Command copy() {
				return null;
			}

			public String getName() {
				return null;
			}
    		
    	});
    	
    	assertTrue(completed);
    	assertTrue(result.ran);
    	 
	}
    
    @Test
    public void testTimeoutWithIO() throws Exception {
    	//TODO test IO blocking.  Command should still have been removed
    	// and second command should be executed.
	}
    
    @Test
    public void testPostDeterminedEffectCommandExecution() throws Exception {
        CommandManager manager=new CommandManager("test", new DefaultErrorHandler(), new CommandListener(){ //$NON-NLS-1$

            public void commandExecuted( int commandType ) {
            }
            
        });
        final Result result=new Result();
        manager.syncExecute(new PostDeterminedEffectCommand(){

            public void run( IProgressMonitor monitor ) throws Exception {
                fail();
            }

            public Command copy() {
                return null;
            }

            public String getName() {
                return null;
            }

            public boolean execute( IProgressMonitor monitor ) throws Exception {
                if( result.ran )
                    fail();                
                result.ran=true;
                return true;
            }

            public void setMap( IMap map ) {
            }

            public Map getMap() {
                return null;
            }

            public void rollback( IProgressMonitor monitor ) throws Exception {
                if( !result.ran )
                    fail();
                result.ran=false;
            }
            
        });
        
        assertTrue( result.ran );
        
        manager.undo(false);
        
        assertFalse( result.ran );
        
        manager.redo(false);
        
        assertTrue( result.ran );
        
        manager.undo(false);
        
        assertFalse( result.ran );
        
        manager.syncExecute(new PostDeterminedEffectCommand(){

            public void run( IProgressMonitor monitor ) throws Exception {
                fail();
            }

            public Command copy() {
                return null;
            }

            public String getName() {
                return null;
            }

            public boolean execute( IProgressMonitor monitor ) throws Exception {
                if( result.ran )
                    fail();                
                result.ran=true;
                return false;
            }

            public void setMap( IMap map ) {
            }

            public Map getMap() {
                return null;
            }

            public void rollback( IProgressMonitor monitor ) throws Exception {
                fail();
            }
            
        });

        assertTrue( result.ran );
        
        assertFalse(manager.canUndo());
    }
    
    @Test
    public void testRedo() {
    }

    @Test
    public void testUndo() {
    }

    private class Result{
        volatile boolean ran=false;
    }
    
}
