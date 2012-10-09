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
package net.refractions.udig.catalog.tests.ui.workflow;

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

