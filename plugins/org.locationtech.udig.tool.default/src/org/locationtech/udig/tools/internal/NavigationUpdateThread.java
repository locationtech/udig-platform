/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.internal;

import java.awt.Point;
import java.util.ArrayList;

import org.locationtech.udig.project.command.NavCommand;
import org.locationtech.udig.project.internal.command.navigation.NavComposite;
import org.locationtech.udig.project.internal.command.navigation.PanCommand;
import org.locationtech.udig.project.internal.command.navigation.ZoomCommand;
import org.locationtech.udig.project.ui.commands.TransformDrawCommand;
import org.locationtech.udig.project.ui.tool.IToolContext;

import org.locationtech.jts.geom.Coordinate;

/**
 * Waits 1 second after the most recent request before running operation.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class NavigationUpdateThread implements Runnable {

    private static final int PAN_AMOUNT = 30;
    private static final double FACTOR = 1.01;
    private static volatile long request = Long.MAX_VALUE;
    private static Thread thread;

    private static TransformDrawCommand command;
    private static final NavigationUpdateThread updater=new NavigationUpdateThread();
	public static final int DEFAULT_DELAY = 500;
    int zoomAmount = 0;
    private int vertical=0;
    private int horizontal=0;
    private IToolContext context;
	private volatile long updateDelay = 1000;
    private Coordinate fixedPoint;
	private double previousZoom = 1.0;
    
    private NavigationUpdateThread(){}
    
    /**
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public final void run() {
        synchronized (NavigationUpdateThread.class) {
            if (thread != Thread.currentThread())
                return;
        }
        while( getElapsedTimeSinceLastRequest() < updateDelay  ) {
            synchronized (NavigationUpdateThread.class) {
                if (thread != Thread.currentThread())
                    return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                ToolsPlugin.log("", e); //$NON-NLS-1$
                synchronized (NavigationUpdateThread.class) {
                    if (thread != Thread.currentThread())
                        return;
                    thread = null;
                }
                cancel();
                return;
            }
        }
        synchronized (NavigationUpdateThread.class) {
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
        synchronized (NavigationUpdateThread.class) {
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
        synchronized (NavigationUpdateThread.class) {
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

    	double zoomChange = Math.pow(FACTOR, change);
    	if(change < 0) zoomChange = -zoomChange;
    	
        synchronized (NavigationUpdateThread.class) {
        	this.updateDelay = updateDelay;
        	double targetZoom;
            if (command == null) {
                TransformDrawCommand transformDrawCommand = new TransformDrawCommand();
                command = transformDrawCommand;
                if (fixedPoint == null) {
                    fixedPoint = new Point(context.getViewportPane().getWidth() / 2, context
                            .getViewportPane().getHeight() / 2);
                }
                transformDrawCommand.fixPoint(fixedPoint);
                this.fixedPoint = context.pixelToWorld(fixedPoint.x, fixedPoint.y);
                targetZoom = context.calculateZoomLevel(previousZoom, zoomChange,this.fixedPoint,false, change != 0);
                if(Math.abs(targetZoom - previousZoom) > 0.0001) {
                	transformDrawCommand.zoom(targetZoom, targetZoom);
                	context.sendASyncCommand(transformDrawCommand);
                }
            } else {
				targetZoom = context.calculateZoomLevel(previousZoom,zoomChange,this.fixedPoint,false, change != 0);
                command.zoom(targetZoom, targetZoom);
                if(Math.abs(targetZoom - previousZoom) > 0.0001) {
                	context.getViewportPane().repaint();
                }
            }
            if(Math.abs(targetZoom - previousZoom) > 0.0001) {
                zoomAmount += change;
                this.previousZoom = targetZoom;
            }
        }

        this.context = context;

        requestStart();
    }

	protected synchronized void performChange() {
        double zoom = Math.abs(Math.pow(FACTOR, previousZoom));
        ArrayList<NavCommand> commands=new ArrayList<NavCommand>();
        if( horizontal!=0 || vertical!=0 ){
            commands.add(
                    new PanCommand((horizontal*-PAN_AMOUNT), (vertical*-PAN_AMOUNT)));
        }
        if( zoom>0.00000001 ){
        	double targetZoom = context.calculateZoomLevel(1,previousZoom, fixedPoint, false, zoomAmount != 0);
            ZoomCommand zoomCommand = new ZoomCommand(previousZoom);
            zoomCommand.setFixedPoint(fixedPoint);
            commands.add(zoomCommand);
        }
        if( commands.size()>0 ){
            NavComposite composite = new NavComposite(commands);
			context.sendASyncCommand( composite );
        }
        previousZoom = 1.0;
        zoomAmount=0;
        if( command!=null ){
            command.setValid(false);
        }
        command=null;
        horizontal=0;
        vertical=0;
    }

    protected void cancel() {
    	if(context != null) {
            zoomAmount=0;
            previousZoom=1.0;
	        boolean requireUpdate = false;
			if( command!=null ) {
	        	requireUpdate  = true;
	            command.setValid(false);
	        }
	        command=null;
	        horizontal=0;
	        vertical=0;
	        if(context.getViewportPane() !=null && requireUpdate)
	        	context.getViewportPane().repaint();
    	}
    }
    
    /**
     * @return Returns the updater.
     */
    public static NavigationUpdateThread getUpdater() {
        return updater;
    }

}
