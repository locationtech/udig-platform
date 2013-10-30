/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

/**
 * An IProgressMonitor that guarantees that the proviced IProgressMonitor part is only updated from the display thread.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class OffThreadProgressMonitor implements IProgressMonitor {

    private IProgressMonitor monitor;
    private Display display;
    private Widget widget;

    public OffThreadProgressMonitor( IProgressMonitor part, Display display ) {
        this(part);
        this.display=display;
    }

    public OffThreadProgressMonitor( IProgressMonitor monitor2 ) {
        display = Display.getCurrent();
        if( display==null ){
            display = Display.getDefault();
        }

        if( monitor2 instanceof Widget ){
            widget=(Widget) monitor2;
            display = widget.getDisplay();
        }

        monitor = monitor2;
    }

    /**
     * Because the methods are ran asynchronously in the display it is possible that the monitor is disposed by the 
     * time the runnable executed... So the runnable this method returns executes the parameters and handles SWTExceptions
     * @param runnable the runnable to execute
     */
    private Runnable runSafely( final Runnable runnable ) {
        return new Runnable(){
            public void run() {
                try {
                    runnable.run();
                } catch (SWTException e) {
                    // ignore disposed exceptions (see javadoc)
                    if( e.code != SWT.ERROR_WIDGET_DISPOSED ){
                        throw e;
                    }
                }
            }
        };
    }

    public void beginTask( final String name, final int totalWork ) {
        Runnable runnable = new Runnable(){
            public void run() {
                
                monitor.beginTask(name, totalWork);
            }
        };
        PlatformGIS.asyncInDisplayThread(display, runSafely(runnable), true);
    }

    public void done() {
        Runnable runnable = new Runnable(){
                    public void run() {
                        monitor.done();
                    }
                };
        PlatformGIS.asyncInDisplayThread(display, runSafely(runnable), true);
    }

    public void internalWorked( final double work ) {
        PlatformGIS.asyncInDisplayThread(display, new Runnable(){
            public void run() {
                if (widget!=null && widget.isDisposed() )
                    return;
                monitor.internalWorked(work);
            }
        }, true);
    }

    public boolean isCanceled() {
        final boolean[] cancelled=new boolean[1];
        Runnable runnable = new Runnable(){
                    public void run() {
                        if (widget!=null && widget.isDisposed() )
                            return;
                        cancelled[0]=monitor.isCanceled();
                    }
                };
        PlatformGIS.asyncInDisplayThread(display, runSafely(runnable), true);
        return cancelled[0];
    }

    public void setCanceled( final boolean value ) {
        Runnable runnable = new Runnable(){
                    public void run() {
                        if (widget!=null && widget.isDisposed() )
                            return;
                        monitor.setCanceled(value);
                    }
                };
        PlatformGIS.asyncInDisplayThread(display, runSafely(runnable), true);
    }

    public void setTaskName( final String name ) {
        Runnable runnable = new Runnable(){
                    public void run() {
                        if (widget!=null && widget.isDisposed() )
                            return;
                        monitor.setTaskName(name);
                    }
                };
        PlatformGIS.asyncInDisplayThread(display, runSafely(runnable), true);
    }

    public void subTask( final String name ) {
        Runnable runnable = new Runnable(){
                    public void run() {
                        if (widget!=null && widget.isDisposed() )
                            return;
                        monitor.subTask(name);
                    }
                };
        PlatformGIS.asyncInDisplayThread(display, runSafely(runnable), true);
    }

    public void worked( final int work ) {
        Runnable runnable = new Runnable(){
                    public void run() {
                        if (widget!=null && widget.isDisposed() )
                            return;
                        monitor.worked(work);
                    }
                };
        PlatformGIS.asyncInDisplayThread(display, runSafely(runnable), true);
    }

}