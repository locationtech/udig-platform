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

