package net.refractions.udig.tools.edit;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public class BehaviourCommandTest extends TestCase {

    /*
     * Test method for 'net.refractions.udig.tools.edit.EventBehaviourCommand.run(IProgressMonitor)'
     */
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
