/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

public class ShutdownTaskListTest {

    private ShutdownTaskList list;

    @Before
    public void setUp() throws Exception {
        list = new ShutdownTaskList();
    }

    @Test
    public void testPostShutdown() {
        final boolean[] ran = new boolean[1];
        ran[0] = false;
        list.addPostShutdownTask(new TestShutdownTask(){
            @Override
            public int getProgressMonitorSteps() {
                return 6;
            }

            @Override
            public void postShutdown( IProgressMonitor monitor, IWorkbench workbench ) {
                ran[0] = true;
            }
            
            @Override
            public void handlePostShutdownException( Throwable t) {
                throw (RuntimeException)t;
            }

        });

        list.postShutdown(PlatformUI.getWorkbench());

        assertTrue(ran[0]);

        // now handle case where exception is thrown.
        ran[0] = false;

        final boolean[] exceptionHandled = new boolean[1];
        exceptionHandled[0] = false;
        final RuntimeException exception = new RuntimeException();

        list=new ShutdownTaskList();

        list.addPostShutdownTask(new TestShutdownTask(){
            @Override
            public int getProgressMonitorSteps() {
                return 6;
            }

            @Override
            public void postShutdown( IProgressMonitor monitor, IWorkbench workbench ) {
                ran[0] = true;
                throw exception;
            }

            @Override
            public void handlePostShutdownException( Throwable t ) {
                assertEquals(exception, t);
                exceptionHandled[0] = true;
            }
        });

        list.postShutdown(PlatformUI.getWorkbench());

        assertTrue(ran[0]);
        assertTrue(exceptionHandled[0]);

    }

    @Test
    public void testPreShutdown() {
        final boolean[] ran = new boolean[1];
        ran[0] = false;

        final boolean[] forcedVal = new boolean[1];
        forcedVal[0]=false;
        final boolean[] retVal = new boolean[1];
        retVal[0]=false;
        
        list.addPreShutdownTask(new TestShutdownTask(){
            @Override
            public int getProgressMonitorSteps() {
                return 6;
            }

            @Override
            public boolean preShutdown( IProgressMonitor subMonitor, IWorkbench workbench,
                    boolean forced ) {
                assertNotNull(subMonitor);
                assertEquals(forcedVal[0], forced);
                ran[0] = true;
                return retVal[0];
            }
            
            @Override
            public boolean handlePreShutdownException( Throwable t, boolean forced ) {
                if( t instanceof RuntimeException)
                    throw (RuntimeException)t;
                else 
                    throw (Error)t;
            }
        });

        assertFalse(list.preShutdown(PlatformUI.getWorkbench(),forcedVal[0]));
        assertTrue(ran[0]);

        ran[0]=false;
        retVal[0]=true;
        forcedVal[0]=false;
        assertTrue(list.preShutdown(PlatformUI.getWorkbench(),forcedVal[0]));
        assertTrue(ran[0]);
        
        ran[0]=false;
        retVal[0]=false;
        forcedVal[0]=true;
        assertTrue(list.preShutdown(PlatformUI.getWorkbench(),forcedVal[0]));
        assertTrue(ran[0]);
        
        // now handle case where exception is thrown.
        ran[0] = false;

        final boolean[] exceptionHandled = new boolean[1];
        exceptionHandled[0] = false;
        final RuntimeException exception = new RuntimeException();

        list=new ShutdownTaskList();
        
        list.addPreShutdownTask(new TestShutdownTask(){
            @Override
            public int getProgressMonitorSteps() {
                return 6;
            }

            @Override
            public boolean preShutdown( IProgressMonitor subMonitor, IWorkbench workbench,
                    boolean forced ) {
                assertNotNull(subMonitor);
                ran[0] = true;
                throw exception;
            }

            @Override
            public boolean handlePreShutdownException( Throwable t, boolean forced ) {
                assertEquals(exception, t);
                exceptionHandled[0] = true;
                return retVal[0];
            }
        });

        exceptionHandled[0] = false;
        ran[0]=false;
        retVal[0]=true;
        forcedVal[0]=true;
        assertTrue(list.preShutdown(PlatformUI.getWorkbench(),forcedVal[0]));
        assertTrue(ran[0]);
        assertTrue(exceptionHandled[0]);

        ran[0]=false;
        exceptionHandled[0] = false;
        retVal[0]=false;
        forcedVal[0]=true;
        assertTrue(list.preShutdown(PlatformUI.getWorkbench(),forcedVal[0]));
        assertTrue(ran[0]);
        assertTrue(exceptionHandled[0]);


        ran[0]=false;
        exceptionHandled[0] = false;
        retVal[0]=true;
        forcedVal[0]=false;
        assertTrue(list.preShutdown(PlatformUI.getWorkbench(),forcedVal[0]));
        assertTrue(ran[0]);
        assertTrue(exceptionHandled[0]);

        
    }

    class TestShutdownTask implements PreShutdownTask, PostShutdownTask {

        public int getProgressMonitorSteps() {
            throw new RuntimeException("Not allowed"); //$NON-NLS-1$
        }

        public void handlePostShutdownException( Throwable t ) {
            throw new RuntimeException("Not allowed"); //$NON-NLS-1$
        }

        public boolean handlePreShutdownException( Throwable t, boolean forced ) {
            throw new RuntimeException("Not allowed"); //$NON-NLS-1$
        }

        public void postShutdown( IProgressMonitor subMonitor, IWorkbench workbench ) {
            throw new RuntimeException("Not allowed"); //$NON-NLS-1$
        }

        public boolean preShutdown( IProgressMonitor subMonitor, IWorkbench workbench,
                boolean forced ) {
            throw new RuntimeException("Not allowed"); //$NON-NLS-1$
        }

    }
}
