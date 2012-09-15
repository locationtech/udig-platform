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
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

public class EventBehaviourCommandTest {

    /*
     * Test method for 'net.refractions.udig.tools.edit.EventBehaviourCommand.run(IProgressMonitor)'
     */
    @Test
    public void testRun() throws Exception {

        TestHandler handler=new TestHandler();
        
        RunBehaviour runBehaviour = new RunBehaviour();
        RunBehaviour runBehaviour2 = new RunBehaviour();
        List<EventBehaviour> list=new ArrayList<EventBehaviour>();
        
        list.add( runBehaviour);
        list.add( runBehaviour2);
        list.add( new NoRunBehaviour());
        
        MapMouseEvent event = new MapMouseEvent(null, 0,0,0,0,0);
        
        EventBehaviourCommand command=new EventBehaviourCommand(list, handler, event, EventType.RELEASED);
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
    
    class RunBehaviour implements EventBehaviour{
        int id=0;
        boolean ran;
        public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
            return true;
        }

        public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
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
    
    class NoRunBehaviour implements EventBehaviour{

        public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
            return false;
        }

        public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
            fail();
            return null;
        }

        public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
            fail();
        }
        
    }
    
    

}
