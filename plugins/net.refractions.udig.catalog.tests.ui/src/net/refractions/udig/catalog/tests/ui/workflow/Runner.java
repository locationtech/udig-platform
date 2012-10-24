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

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class Runner extends Job {
	
	public static final int OK = 0;
	public static final int REPEAT = 1;
	
	Runnable[] actions;
	int index;
	int delay;
	private Timer timer;

	public Runner(Runnable[] actions, int delay) {
		super("runner"); //$NON-NLS-1$
		
		this.actions = actions;
		this.delay = delay;
		
		index = 0;
	}

	@Override
	public boolean belongsTo(Object family) {
		return family==this;
	}
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			if (index == actions.length)
				return Status.OK_STATUS;
			
			Runnable r = actions[index];
			switch(r.run(monitor)) {
				case OK:
					index++;
					break;
				
			}
			
			if (index != actions.length) {
				timer=new Timer();
				timer.schedule(new TimerTask(){

					@Override
					public void run() {
						schedule();
					}
					
				}, Runner.this.delay);
			}
			
		}
		catch(Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
		
		return Status.OK_STATUS;
	}
	
	public interface Runnable {
		int run(IProgressMonitor monitor);
	}
}