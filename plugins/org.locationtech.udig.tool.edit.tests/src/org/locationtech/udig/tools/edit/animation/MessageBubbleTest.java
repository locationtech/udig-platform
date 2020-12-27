package org.locationtech.udig.tools.edit.animation;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.ui.graphics.ViewportGraphics;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MessageBubbleTest {

    @Mock
    ViewportPane viewportPaneMock;

    @Mock
    ViewportGraphics viewportGraphicsMock;

    MessageBubble messageBubble = new MessageBubble(0, 0, "whatever", (short) 1);

    @Test
    public void withoutGraphicContextNoNPEonSetValid() {
        messageBubble.setValid(false);
        assertFalse(messageBubble.isValid());
    }

    @Test
    public void withoutsetGraphicContextWithNonNullDisplayRegistersListeners() {
        messageBubble.setGraphics(viewportGraphicsMock, viewportPaneMock);

        verify(viewportPaneMock, atLeastOnce()).addMouseListener(any());
        verify(viewportPaneMock, atLeastOnce()).addMouseWheelListener(any());

        verifyNoMoreInteractions(viewportGraphicsMock, viewportPaneMock);
    }

    @Test
    public void withoutGraphicContextListenersRemoveNeverCalled() {
        messageBubble.setGraphics(viewportGraphicsMock, viewportPaneMock);

        messageBubble.setValid(false);
        assertFalse(messageBubble.isValid());

        verify(viewportPaneMock, atLeastOnce()).addMouseListener(any());
        verify(viewportPaneMock, atLeastOnce()).addMouseWheelListener(any());
        verify(viewportPaneMock, atLeastOnce()).removeMouseListener(any());
        verify(viewportPaneMock, atLeastOnce()).removeMouseWheelListener(any());
        verifyNoMoreInteractions(viewportGraphicsMock, viewportPaneMock);
    }

    @Test
    public void runWithoutViewportPaneDoesNotDoAnything() throws Exception {
        messageBubble.setGraphics(viewportGraphicsMock, null);

        messageBubble.run(new NullProgressMonitor());
        verifyNoMoreInteractions(viewportGraphicsMock);
    }
}
