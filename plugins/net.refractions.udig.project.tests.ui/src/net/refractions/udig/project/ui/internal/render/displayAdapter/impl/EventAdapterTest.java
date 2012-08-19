package net.refractions.udig.project.ui.internal.render.displayAdapter.impl;

import static org.junit.Assert.fail;

import java.awt.event.MouseEvent;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EventAdapterTest {

	private EventHandlerJava handler;
	private Mockery context;
	private EventJob mockEventJob;

	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		mockEventJob = context.mock(EventJob.class);
		this.handler = new EventHandlerJava(mockEventJob);
	}
	
	@Ignore
	@Test
	public void testMouseClicked() {
		MouseEvent e=new MouseEvent(null, 0, 0L, 0, 0,0,1,false);
		
//		context.checking(new Expectations(){{
//			MapMouseEvent mapMouseEvent;
//			mockEventJob.fire(EventJob.PRESSED, mapMouseEvent);
//		}}
//		);
		
//		adapter.mouseClicked(e);
	}

	@Ignore
    @Test
	public void testMouseEntered() {
		fail("Not yet implemented");
	}

	@Ignore
    @Test
	public void testMouseExited() {
		fail("Not yet implemented");
	}

	@Ignore
    @Test
	public void testMousePressed() {
		fail("Not yet implemented");
	}

	@Ignore
    @Test
	public void testMouseReleased() {
		fail("Not yet implemented");
	}

	@Ignore
    @Test
	public void testMouseDragged() {
		fail("Not yet implemented");
	}

	@Ignore
    @Test
	public void testMouseMoved() {
		fail("Not yet implemented");
	}

	@Ignore
    @Test
	public void testMouseWheelMoved() {
		fail("Not yet implemented");
	}

}
