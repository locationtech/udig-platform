/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.internal;

import java.awt.Point;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.command.factory.NavigationCommandFactory;
import net.refractions.udig.project.internal.command.navigation.NavComposite;
import net.refractions.udig.project.internal.command.navigation.PanCommand;
import net.refractions.udig.project.internal.command.navigation.ZoomCommand;
import net.refractions.udig.project.ui.commands.TransformDrawCommand;
import net.refractions.udig.project.ui.tool.IToolContext;

/**
 * Waits 1 second after the most recent request before running operation.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class UpdateThread implements Runnable {

    private static final int PAN_AMOUNT = 30;
    private static final double FACTOR = 1.01;
    private static volatile long request = Long.MAX_VALUE;
    private static Thread thread;

    private static TransformDrawCommand command;
    private static final UpdateThread updater=new UpdateThread();
    private NavigationCommandFactory factory = NavigationCommandFactory.getInstance();
    int amount = 0;
    private int vertical=0;
    private int horizontal=0;
    private IToolContext context;
	private volatile long updateDelay = 1000;
    private Coordinate fixedPoint;
    
    private UpdateThread(){}
    
    /**
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public final void run() {
        synchronized (UpdateThread.class) {
            if (thread != Thread.currentThread())
                return;
        }
        while( getElapsedTimeSinceLastRequest() < updateDelay  ) {
            synchronized (UpdateThread.class) {
                if (thread != Thread.currentThread())
                    return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                ToolsPlugin.log("", e); //$NON-NLS-1$
                synchronized (UpdateThread.class) {
                    if (thread != Thread.currentThread())
                        return;
                    thread = null;
                }
                cancel();
                return;
            }
        }
        synchronized (UpdateThread.class) {
            if (thread != Thread.currentThread())
                return;
            thread = null;
        }
        performChange();
    }
    /**
     * @return
     */
    private synchronized long getElapsedTimeSinceLastRequest() {
        return System.currentTimeMillis() - request;
    }
    

    public synchronized void requestStart() {
        request = System.currentTimeMillis();
        synchronized (UpdateThread.class) {
            if (thread == null) {
                thread = new Thread(this);
                thread.setName(Messages.ScrollZoom_scroll_zoom); 
                thread.start();
            }
        }
    }

    
    public synchronized void left(IToolContext context, int updateDelay) {
        horizontal++;
        update(context, updateDelay);
    }
    public synchronized void right(IToolContext context, int updateDelay) {
        horizontal--;
        update(context, updateDelay);
    }
    public synchronized void up(IToolContext context, int updateDelay) {
        vertical++;
        update(context, updateDelay);
    }
    public synchronized void down(IToolContext context, int updateDelay) {
        vertical--;
        update(context, updateDelay);
    }
    
    private void update(IToolContext context, int updateDelay2) {
        synchronized (UpdateThread.class) {
        	this.updateDelay = updateDelay2;
            this.context=context;
            if (command == null) {
                TransformDrawCommand transformDrawCommand = new TransformDrawCommand();
                command=transformDrawCommand;
                transformDrawCommand.pan(horizontal*PAN_AMOUNT, vertical*PAN_AMOUNT);
                context.sendASyncCommand(transformDrawCommand);
            } else {
                command.pan(horizontal*PAN_AMOUNT, vertical*PAN_AMOUNT);
                context.getViewportPane().repaint();
            }
        }
        requestStart();
    }

    public void zoom( int change, IToolContext context, int updateDelay ) {
        zoomWithFixedPoint(change, context, updateDelay, null);
    }
    /**
     * Makes zoom and keeps fixedPoint at the same place.
     */
    public void zoomWithFixedPoint( int change, IToolContext context, int updateDelay,
            Point fixedPoint ) {
        amount += change;
        this.updateDelay = updateDelay;

        double zoom = Math.abs(Math.pow(FACTOR, amount));

        synchronized (UpdateThread.class) {
            if (command == null) {
                TransformDrawCommand transformDrawCommand = new TransformDrawCommand();
                command = transformDrawCommand;
                if (fixedPoint == null) {
                    fixedPoint = new Point(context.getViewportPane().getWidth() / 2, context
                            .getViewportPane().getHeight() / 2);
                }
                transformDrawCommand.fixPoint(fixedPoint);
                this.fixedPoint = context.pixelToWorld(fixedPoint.x, fixedPoint.y);
                transformDrawCommand.zoom(zoom, zoom);
                context.sendASyncCommand(transformDrawCommand);
            } else {
                command.zoom(zoom, zoom);
                context.getViewportPane().repaint();
            }
        }

        this.context = context;

        requestStart();
    }

    protected synchronized void performChange() {
        
        double zoom = Math.abs(Math.pow(FACTOR, amount));
        ArrayList<NavCommand> commands=new ArrayList<NavCommand>();
        if( horizontal!=0 || vertical!=0 ){
            commands.add(
                    (NavCommand) new PanCommand((horizontal*-PAN_AMOUNT), (vertical*-PAN_AMOUNT)));
        }
        if( zoom>0.00000001 ){
            ZoomCommand zoomCommand = new ZoomCommand(zoom);
            zoomCommand.setFixedPoint(fixedPoint);
            commands.add(zoomCommand);
        }
        if( commands.size()>0 ){
            NavComposite composite = new NavComposite(commands);
            composite.addFinalizerCommand(new InvalidateCommand(command));
			context.sendASyncCommand( composite );
        }
        amount=0;
        if( command!=null ){
            command.setValid(false);
        }
        command=null;
        horizontal=0;
        vertical=0;
    }

    protected void cancel() {
        amount=0;
        if( command!=null ){
            command.setValid(false);
        }
        command=null;
        horizontal=0;
        vertical=0;
        context.getViewportPane().repaint();
    }
    
    /**
     * @return Returns the updater.
     */
    public static UpdateThread getUpdater() {
        return updater;
    }

}
