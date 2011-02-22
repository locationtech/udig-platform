/**
 *
 */
package net.refractions.udig.catalog.tests.ui.workflow;

import java.io.IOException;

import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.Workflow.State;

import org.eclipse.core.runtime.IProgressMonitor;

public class SimpleState extends Workflow.State {
	public boolean run = true;
	public boolean ran = false;
	public Workflow.State next = null;

	@Override
	public boolean run(IProgressMonitor monitor) throws IOException {
		ran = true;
		return run;
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public State next() {
		return next;
	}

	@Override
	public String getName() {
		return "Simple Test"; //$NON-NLS-1$
	}
}
