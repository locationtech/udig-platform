/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.render.displayAdapter.impl;

import java.awt.Dimension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.locationtech.udig.project.render.displayAdapter.MapDisplayEvent;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.internal.Trace;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;

public class EventHandler implements Listener {
    EventJob eventJob;

    ViewportPane pane;

    public EventHandler(ViewportPane pane, EventJob eventJob) {
        this.eventJob = eventJob;
        this.pane = pane;
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.type) {
        case SWT.MouseMove:
            mouseMove(event);
            break;
        case SWT.MouseDown:
            mouseDown(event);
            ProjectUIPlugin.trace(Trace.VIEWPORT, getClass(), "mouse down", null); //$NON-NLS-1$
            break;
        case SWT.MouseEnter:
            mouseEnter(event);
            break;
        case SWT.MouseExit:
            mouseExit(event);
            break;
        case SWT.MouseHover:
            mouseHover(event);
            break;
        case SWT.MouseUp:
            mouseUp(event);
            ProjectUIPlugin.trace(Trace.VIEWPORT, getClass(), "mouse up", null); //$NON-NLS-1$
            break;
        case SWT.MouseWheel:
            mouseWheel(event);
            break;
        case SWT.Resize:
            controlResized(event);
            break;
        default:
            break;
        }
    }

    private void mouseWheel(Event e) {
        MapMouseEvent m = new MapMouseWheelEvent(pane, e.x, e.y, e.stateMask, getButtonsDown(e),
                getButton(e.button), e.count);
        eventJob.fire(EventJob.WHEEL, m);
    }

    Dimension size;

    private long scheduledTime;

    public void controlResized(final Event e) {
        if (e == null)
            return;
        synchronized (this) {
            scheduledTime = System.currentTimeMillis() + 400;
        }
        org.locationtech.udig.ui.PlatformGIS.asyncInDisplayThread(new Runnable() {
            @Override
            public void run() {
                e.display.timerExec(500, new Runnable() {
                    @Override
                    public void run() {
                        long currentTimeMillis = System.currentTimeMillis();
                        long l;
                        synchronized (EventHandler.this) {
                            l = scheduledTime;
                        }
                        if (l <= currentTimeMillis) {
                            eventJob.fire(EventJob.RESIZED,
                                    new MapDisplayEvent(pane, size, pane.getDisplaySize()));
                            size = pane.getDisplaySize();
                        }
                    }
                });
            }
        }, true);

    }

    public void mouseDoubleClick(Event e) {
        MapMouseEvent m = createMapMouseEvent(e);
        m = new MapMouseEvent(pane, m.x, m.y, m.modifiers, m.buttons | m.button, m.button);
        eventJob.fire(EventJob.DOUBLE_CLICK, m);
    }

    private MapMouseEvent createMapMouseEvent(Event e) {
        return new MapMouseEvent(pane, e.x, e.y, e.stateMask, getButtonsDown(e),
                getButton(e.button));
    }

    private int getButtonsDown(Event e) {
        int button1 = (e.stateMask & SWT.BUTTON1) != 0 ? 1 : -1;
        int button2 = (e.stateMask & SWT.BUTTON2) != 0 ? 2 : -1;
        int button3 = (e.stateMask & SWT.BUTTON3) != 0 ? 3 : -1;

        return getButton(button1) | getButton(button2) | getButton(button3);
    }

    private int getButton(int button) {
        int state = 0;
        if (button == 1)
            state = state | MapMouseEvent.BUTTON1;
        if (button == 2)
            state = state | MapMouseEvent.BUTTON2;
        if (button == 3)
            state = state | MapMouseEvent.BUTTON3;
        return state;
    }

    public void mouseDown(Event e) {
        MapMouseEvent m = createMapMouseEvent(e);
        m = new MapMouseEvent(pane, m.x, m.y, m.modifiers, m.buttons | m.button, m.button);
        eventJob.fire(EventJob.PRESSED, m);
    }

    public void mouseUp(Event e) {
        MapMouseEvent m = createMapMouseEvent(e);
        if ((m.button & m.buttons) != 0)
            m = new MapMouseEvent(m.source, m.x, m.y, m.modifiers, m.buttons ^ m.button, m.button);
        eventJob.fire(EventJob.RELEASED, m);
    }

    public void mouseMove(Event e) {
        MapMouseEvent m = createMapMouseEvent(e);
        if (m.buttons == 0)
            eventJob.fire(EventJob.MOVED, m);
        else {
            eventJob.fire(EventJob.DRAGGED, m);
        }
    }

    public void mouseEnter(Event e) {
        MapMouseEvent m = createMapMouseEvent(e);
        eventJob.fire(EventJob.ENTERED, m);
    }

    public void mouseExit(Event e) {
        MapMouseEvent m = createMapMouseEvent(e);
        eventJob.fire(EventJob.EXITED, m);
    }

    public void mouseHover(Event e) {
        MapMouseEvent m = createMapMouseEvent(e);
        eventJob.fire(EventJob.HOVERED, m);
    }
}
