package org.locationtech.udig.tools.edit.animation;

import static org.junit.Assert.assertFalse;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

public class MessageBubbleTest {

    ViewportPane viewportPaneMock = EasyMock.createNiceMock(ViewportPane.class);
    
    ViewportGraphics viewportGraphicsMock = EasyMock.createNiceMock(ViewportGraphics.class);

    MessageBubble messageBubble = new MessageBubble(0, 0, "whatever", (short) 1);
    
    @Test
    public void withoutGraphicContextNoNPEonSetValid() {
        messageBubble.setValid(false);
        assertFalse(messageBubble.isValid());
    }


    @Test
    public void withoutsetGraphicContextWithNonNullDisplayRegistersListeners() {
        viewportPaneMock.addMouseListener(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        viewportPaneMock.addMouseWheelListener(EasyMock.anyObject());
        EasyMock.expectLastCall().once();

        EasyMock.replay(viewportGraphicsMock, viewportPaneMock);
        messageBubble.setGraphics(viewportGraphicsMock, viewportPaneMock);
        EasyMock.verify(viewportGraphicsMock, viewportPaneMock);
    }

    @Test
    public void withoutGraphicContextListenersRemoveNeverCalled() {
        viewportPaneMock.removeMouseListener(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        viewportPaneMock.removeMouseWheelListener(EasyMock.anyObject());
        EasyMock.expectLastCall().once();

        EasyMock.replay(viewportGraphicsMock, viewportPaneMock);

        messageBubble.setGraphics(viewportGraphicsMock, viewportPaneMock);

        messageBubble.setValid(false);
        assertFalse(messageBubble.isValid());

        EasyMock.verify(viewportGraphicsMock, viewportPaneMock);
    }

    @Test
    public void runWithoutViewportPaneDoesNotDoAnything() throws Exception {
        EasyMock.replay(viewportGraphicsMock);
        
        messageBubble.setGraphics(viewportGraphicsMock, null);
        
        messageBubble.run(new NullProgressMonitor());
        EasyMock.verify(viewportGraphicsMock);
    }
}
