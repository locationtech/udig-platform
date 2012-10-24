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
package net.refractions.udig.tools.edit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

public class BehaviourCommandTest {

    /*
     * Test method for 'net.refractions.udig.tools.edit.EventBehaviourCommand.run(IProgressMonitor)'
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
    
    class RunBehaviour implements Behaviour{
        int id=0;
        boolean ran;
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
