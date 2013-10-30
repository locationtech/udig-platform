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
package org.locationtech.udig.catalog.tests.ui.workflow;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

public class BlockingState extends SimpleState {

	@Override
	public void init(IProgressMonitor monitor) {
		block(monitor, "Initializing"); //$NON-NLS-1$
	}
	
	@Override
	public boolean run(IProgressMonitor monitor) throws IOException {
		boolean run = super.run(monitor);
		block(monitor,"Running"); //$NON-NLS-1$
		return run;
	}
	
	private void block(IProgressMonitor monitor, String message) {
		monitor.beginTask(message, 10);
		for (int i = 0; i < 10; i++) {
			monitor.worked(1);
			try {
				Thread.sleep(100);
			} 
			catch (InterruptedException e) {
				return;
			}
		}
	}
}

