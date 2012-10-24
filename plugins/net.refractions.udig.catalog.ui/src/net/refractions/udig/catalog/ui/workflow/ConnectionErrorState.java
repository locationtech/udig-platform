/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.ui.workflow;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.IConnectionErrorHandler;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.core.Pair;

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