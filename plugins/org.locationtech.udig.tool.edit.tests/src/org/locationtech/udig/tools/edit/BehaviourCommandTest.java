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
package org.locationtech.udig.tools.edit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

public class BehaviourCommandTest {

    /*
     * Test method for 'org.locationtech.udig.tools.edit.EventBehaviourCommand.run(IProgressMonitor)'
     */
    @Test
    public void testRun() throws Exception {

        TestHandler handler=new TestHandler();
        
        RunBehaviour runBehaviour = new RunBehaviour();
        RunBehaviour runBehaviour2 = new RunBehaviour();
        List<Behaviour> list=new ArrayList<Behaviour>();
        
        list.add( runBehaviour);
        list.add( runBehaviour2);
        list.add( new NoRunBehaviour());
        
        
        BehaviourCommand command=new BehaviourCommand(list, handler );
        command.setMap( (Map) handler.getContext().getMap());
        assertFalse( runBehaviour.ran );
        assertFalse( runBehaviour2.ran );
        
        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.execute(nullProgressMonitor);
        
        assertTrue( runBehaviour.ran );
        assertTrue( runBehaviour2.ran );
        
        nullProgressMonitor = new NullProgressMonitor();
        command.rollback(nullProgressMonitor);
        assertFalse( runBehaviour.ran );
        assertFalse( runBehaviour2.ran );
        
    }
    
    @Test
    public void testInOrderRunAndRollback() throws Exception {

        TestHandler handler=new TestHandler();
        
        RunBehaviour runBehaviour = new RunBehaviour();
        RunBehaviour runBehaviour2 = new RunBehaviour();
        List<Behaviour> list=new ArrayList<Behaviour>();
        
        list.add( runBehaviour);
        list.add( runBehaviour2);
                
        
        BehaviourCommand command=new BehaviourCommand(list, handler );
        command.setMap( (Map) handler.getContext().getMap());
        assertFalse( runBehaviour.ran );
        assertFalse( runBehaviour2.ran );
        
        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.execute(nullProgressMonitor);
        
        assertTrue( runBehaviour.ran );
        assertTrue( runBehaviour2.ran );
        assertTrue( runBehaviour2.time.getTime() > runBehaviour.time.getTime());
        
        nullProgressMonitor = new NullProgressMonitor();
        command.rollback(nullProgressMonitor);
        assertFalse( runBehaviour.ran );
        assertFalse( runBehaviour2.ran );
        assertFalse( runBehaviour2.time.getTime() > runBehaviour.time.getTime());
        
    }
    
    class RunBehaviour implements Behaviour{
        int id=0;
        boolean ran;
        Timestamp time;
        public boolean isValid( EditToolHandler handler ) {
            return true;
        }

        public UndoableMapCommand getCommand( EditToolHandler handler) {
            return new UndoableMapCommand(){

                
                private Map map;

                public void setMap( IMap map ) {
                    if( map==null)
                        fail();
                    this.map=(Map) map;
                }

                public Map getMap() {
                    return map;
                }

                public void run( IProgressMonitor monitor ) throws Exception {
                    if( ran )
                        fail();
                    Thread.sleep(10);
                    time = new Timestamp(System.currentTimeMillis());
                    ran=true;
                }

                public Command copy() {
                    return null;
                }

                public String getName() {
                    return "Run Command"; //$NON-NLS-1$
                }

                public void rollback( IProgressMonitor monitor ) throws Exception {
                    if( !ran )
                        fail();
                    Thread.sleep(10);
                    time = new Timestamp(System.currentTimeMillis());
                    ran=false;
                }
                
            };
        }

        public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
            fail();
        }
        
    }
    
    class NoRunBehaviour implements Behaviour{

        public boolean isValid( EditToolHandler handler ) {
            return false;
        }

        public UndoableMapCommand getCommand( EditToolHandler handler) {
            fail();
            return null;
        }

        public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
            fail();
        }
        
    }
    
   
}
