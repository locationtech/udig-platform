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
		context = new Mockery();
		mockEventJob = context.mock(EventJob.class);
		this.handler = new EventHandlerJava(mockEventJob);
	}
	
	public void testMouseClicked() {
		MouseEvent e=new MouseEvent(null, 0, 0L, 0, 0,0,1,false);
		
//		context.checking(new Expectations(){{
//			MapMouseEvent mapMouseEvent;
//			mockEventJob.fire(EventJob.PRESSED, mapMouseEvent);
//		}}
//		);  
		
//		adapter.mouseClicked(e);
	}

	public void testMouseEntered() {
		fail("Not yet implemented");
	}

	public void testMouseExited() {
		fail("Not yet implemented");
	}

	public void testMousePressed() {
		fail("Not yet implemented");
	}

	public void testMouseReleased() {
		fail("Not yet implemented");
	}

	public void testMouseDragged() {
		fail("Not yet implemented");
	}

	public void testMouseMoved() {
		fail("Not yet implemented");
	}

	public void testMouseWheelMoved() {
		fail("Not yet implemented");
	}

}
