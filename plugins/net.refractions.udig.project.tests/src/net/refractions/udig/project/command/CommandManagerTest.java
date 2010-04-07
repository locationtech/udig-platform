/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.command;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.commands.DefaultErrorHandler;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

public class CommandManagerTest extends AbstractProjectTestCase {
	
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

    public void testExecuteSyncDeadLocks() {
        final CommandManager manager=new CommandManager("test", new DefaultErrorHandler(), new CommandListener(){ //$NON-NLS-1$

            public void commandExecuted( int commandType ) {
            }
            
        }, 2000);
        final Result result=new Result();
        final Result resultInner=new Result();
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
    
    public void testTimeoutWithIO() throws Exception {
    	//TODO test IO blocking.  Command should still have been removed
    	// and second command should be executed.
	}
    
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
    
    public void testRedo() {
    }

    public void testUndo() {
    }

    private class Result{
        volatile boolean ran=false;
    }
    
}
