/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.command.navigation;

import java.util.Iterator;
import java.util.List;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.command.PostDeterminedEffectCommand;
import net.refractions.udig.project.command.UndoableCommand;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.internal.render.ViewportModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * TODO Purpose of net.refractions.udig.project.internal.command.navigation
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class NavComposite extends UndoableComposite implements NavCommand {

	ViewportModel model;

	/**
	 * Creates a new instance of NavComposite
	 * 
	 * @param navCommands
	 *            an ordered list of Nav commands
	 */
	public NavComposite(List navCommands) {
		super(navCommands);
	}

	/**
	 * @see net.refractions.udig.project.internal.command.navigation.NavCommand#setViewportModel(net.refractions.udig.project.ViewportModelControl)
	 */
	public void setViewportModel(ViewportModel model) {
		this.model = model;
		for (Iterator iter = commands.iterator(); iter.hasNext();) {
			NavCommand command = (NavCommand) iter.next();
			command.setViewportModel(model);
		}
	}

	public void run(IProgressMonitor monitor) throws Exception {
		execute(monitor);
	}

	public boolean execute(IProgressMonitor monitor) throws Exception {
		monitor.beginTask(getName(), 12 * commands.size());
		monitor.worked(2);
		final boolean previousDeliver = model.eDeliver();
		model.eSetDeliver(false);
		boolean changedState = false;
		try {
			for (Iterator<? extends MapCommand> iter = commands.iterator(); iter
					.hasNext();) {
				NavCommand command = (NavCommand) iter.next();

				// reset the eSetDeliverState so that the last change will trigger a re-render
				if (!iter.hasNext())
					model.eSetDeliver(previousDeliver);
				
                // initialize command
                command.setMap(getMap());
                command.setViewportModel(model);
               
                changedState |= runCommand(command, monitor);
			}
			monitor.done();
		} finally {
			model.eSetDeliver(previousDeliver);
		}

		return changedState;
	}

	
	private boolean runCommand( NavCommand command, IProgressMonitor monitor ) throws Exception {

        SubProgressMonitor subProgressMonitor = new SubProgressMonitor(
                monitor, 10);
        boolean changed;
        if (command instanceof PostDeterminedEffectCommand) {
            changed = ((PostDeterminedEffectCommand) command)
                    .execute(subProgressMonitor);
        } else {
            command.run(subProgressMonitor);
            changed = true;
        }
        subProgressMonitor.done();
        return changed;
    }

    @Override
	public void rollback( IProgressMonitor monitor ) throws Exception {
        final boolean previousDeliver = model.eDeliver();
        model.eSetDeliver(false);
        try{
        for (int i = finalizerCommands.size() - 1; i > -1; i--) {
            UndoableCommand command = (UndoableCommand) finalizerCommands.get(i);
            command.rollback(monitor);
        }

        for (int i = commands.size() - 1; i > -1; i--) {
            // reset the eSetDeliverState so that the last change will trigger a re-render
            if( i==0 ){
                model.eSetDeliver(previousDeliver);
            }
            UndoableCommand command = (UndoableCommand) commands.get(i);
            command.rollback(monitor);
        }	
        
        }finally{
            model.eSetDeliver(previousDeliver);
        }
	}
}
