/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.workflow;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ui.IConnectionErrorHandler;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.core.Pair;

import org.eclipse.core.runtime.IProgressMonitor;

public class ConnectionErrorState extends State {

    Map<IService, Throwable> errors;

    Map<IService, List<IConnectionErrorHandler>> handlers;

    public ConnectionErrorState( Map<IService, Throwable> errors ) {
        this.errors = errors;
    }

    @Override
    public Pair<Boolean, State> dryRun() {
        return new Pair<Boolean, State>(false, null);
    }

    @Override
    public void init( IProgressMonitor monitor ) throws IOException {
        super.init(monitor);

        // process error handling extension point to get the list of
        // handlers
        ConnectionErrorHandlerProcessor p = new ConnectionErrorHandlerProcessor();
        handlers = new HashMap<IService, List<IConnectionErrorHandler>>();

        for( Map.Entry<IService, Throwable> e : errors.entrySet() ) {
            List<IConnectionErrorHandler> l = p.process(e.getKey(), e.getValue());

            if (l != null && !l.isEmpty()) {
                handlers.put(e.getKey(), l);
            }
        }
    }

    @Override
    // TODO: process the handlers to try and recover
    public boolean run( IProgressMonitor monitor ) throws IOException {
        // always return false
    	monitor.beginTask(getName(), 1);
    	monitor.done();
        return false;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public State next() {
        // always return back to the previous state
        return getPreviousState();
    }

    public Map<IService, List<IConnectionErrorHandler>> getHandlers() {
        return handlers;
    }

    public Map<IService, Throwable> getErrors() {
        return errors;
    }

	@Override
	public String getName() {
		return Messages.ConnectionErrorState_error_name;
	}

}
