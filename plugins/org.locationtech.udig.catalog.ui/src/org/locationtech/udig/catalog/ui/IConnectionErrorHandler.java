/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.locationtech.udig.catalog.IService;

/**
 * A class which is used to handle an error which occurs when attempting to connection to a service.
 * <p>
 * Connection error handlers have the following responsibilites:
 * <ul>
 * <li>Determining from a throwable object, if they can handle the error.
 * <li>Providing a ui which provides feedback about an error, and possibly a way to recover from it.
 * </ul>
 * </p>
 * <p>
 * Connection error handlers are contributed by extending the
 * net.refractions.catalog.ui.connectionErrorHandler extension point.
 * </p>
 * <p>
 * Subclasses may overide the following methods.
 * <ul>
 * <li>canHandle(Throwable)
 * <li>canRecover()
 * <li>create(Composite)
 * </ul>
 * </p>
 *
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 */
public abstract class IConnectionErrorHandler {

    /** extension point id * */
    public static final String XPID = "net.refractions.catalog.ui.connectionErrorHandler"; //$NON-NLS-1$

    /** name of handler, used in ui when more then one available * */
    private String name;

    /** the control used by the handler * */
    private Control control;

    /** the error * */
    private Throwable t;

    /** the service handle * */
    private IService service;

    /**
     * Sets the name for the handler. This name will be used to identify the handler in cases where
     * multiple handlers may wish to handle a single error.
     *
     * @param name The name of the handler.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the name of the handler. This name will be used to identify the handler in cases where
     * multiple handlers may wish to handle a single error.
     *
     * @return The name of the handler.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the error being handled.
     *
     * @param t The throwable object representing the error.
     */
    public void setThrowable(Throwable t) {
        this.t = t;
    }

    /**
     * @return the error being handled.
     */
    public Throwable getThrowable() {
        return t;
    }

    /**
     * @return the service being connected to.
     */
    public IService getService() {
        return service;
    }

    /**
     * @param service The service being connected to.
     */
    public void setService(IService service) {
        this.service = service;
    }

    /**
     * @return the control used by the handler to provide feedback / recover from the error.
     */
    public Control getControl() {
        return control;
    }

    /**
     * Creates the control used by the handler to provide feedback / recover from the error.
     *
     * @param parent The parent widget.
     */
    public void createControl(Composite parent) {
        if (control != null && !control.isDisposed()) {
            control.dispose();
        }

        control = create(parent);
    }

    /**
     * Determines if the handler is done handling the error, and another connection may be
     * attempted. If the handler does not have the ability to recover from a connection error (ie.
     * canRecover() return false), this method should return false.
     *
     * @return true if the error has been handled, otherwise.
     */
    public boolean isComplete() {
        return false;
    }

    /**
     * Determines if the handler has the ability to recover from the error so that another
     * connection may be attempted.
     *
     * @return True if the handler will try to recover, otherwise false.
     */
    public boolean canRecover() {
        return false;
    }

    /**
     * This method is called in the event in which a handler can recover from a connection error.
     * This method blocks and allows the handler to make remote connections.
     *
     * @param monitor A progress monitor.
     * @throws IOException
     */
    public void recover(IProgressMonitor monitor) throws IOException {
        // do nothing
    }

    /**
     * Determines if the handler can handle the error in question.
     *
     * @param t The error in question.
     * @return True if the handler can handle, otherwise false.
     */
    public abstract boolean canHandle(IService service, Throwable t);

    /**
     * @param parent
     * @return
     */
    protected abstract Control create(Composite parent);

}
