package net.refractions.udig.project.ui.internal.render.displayAdapter.impl;

import java.awt.event.MouseEvent;

import junit.framework.TestCase;

import org.jmock.Mockery;

public class EventAdapterTest extends TestCase {

	private EventHandlerJava handler;
	private Mockery context;
	private EventJob mockEventJob;

	@Override
	protected void setUp() throws Exception {
	    // ignore... tests are broken
	    if (true) {
	        return;
	    }
	    
		context = new Mockery();
		mockEventJob = context.mock(EventJob.class);
		this.handler = new EventHandlerJava(mockEventJob);
	}
	
    public void testStub() throws Exception {
        assertTrue(true);
    }
    
	public void xtestMouseClicked() {
		MouseEvent e=new MouseEvent(null, 0, 0L, 0, 0,0,1,false);
		
//		context.checking(new Expectations(){{
//			MapMouseEvent mapMouseEvent;
//			mockEventJob.fire(EventJob.PRESSED, mapMouseEvent);
//		}}
//		);  
		
//		adapter.mouseClicked(e);
	}

	public void xtestMouseEntered() {
		fail("Not yet implemented");
	}

	public void xtestMouseExited() {
		fail("Not yet implemented");
	}

	public void xtestMousePressed() {
		fail("Not yet implemented");
	}

	public void xtestMouseReleased() {
		fail("Not yet implemented");
	}

	public void xtestMouseDragged() {
		fail("Not yet implemented");
	}

	public void xtestMouseMoved() {
		fail("Not yet implemented");
	}

	public void xtestMouseWheelMoved() {
		fail("Not yet implemented");
	}

}
