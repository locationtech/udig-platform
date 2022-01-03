/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command.navigation;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.NavCommand;
import org.locationtech.udig.project.command.PostDeterminedEffectCommand;
import org.locationtech.udig.project.command.UndoableCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.internal.render.ViewportModel;

/**
 * TODO Purpose of org.locationtech.udig.project.internal.command.navigation
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
     * @param navCommands an ordered list of NavCommands
     */
    public NavComposite(List navCommands) {
        super(navCommands);
    }

    /**
     * @see org.locationtech.udig.project.internal.command.navigation.NavCommand#setViewportModel(org.locationtech.udig.project.ViewportModelControl)
     */
    @Override
    public void setViewportModel(ViewportModel model) {
        this.model = model;
        for (Iterator iter = commands.iterator(); iter.hasNext();) {
            NavCommand command = (NavCommand) iter.next();
            command.setViewportModel(model);
        }
    }

    @Override
    public void run(IProgressMonitor monitor) throws Exception {
        execute(monitor);
    }

    @Override
    public boolean execute(IProgressMonitor monitor) throws Exception {
        monitor.beginTask(getName(), 12 * commands.size());
        monitor.worked(2);
        final boolean previousDeliver = model.eDeliver();
        model.eSetDeliver(false);
        boolean changedState = false;
        try {
            for (Iterator<? extends MapCommand> iter = commands.iterator(); iter.hasNext();) {
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

    private boolean runCommand(NavCommand command, IProgressMonitor monitor) throws Exception {

        SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
        boolean changed;
        if (command instanceof PostDeterminedEffectCommand) {
            changed = ((PostDeterminedEffectCommand) command).execute(subMonitor);
        } else {
            command.run(subMonitor);
            changed = true;
        }
        subMonitor.done();
        return changed;
    }

    @Override
    public void rollback(IProgressMonitor monitor) throws Exception {
        final boolean previousDeliver = model.eDeliver();
        model.eSetDeliver(false);
        try {
            for (int i = finalizerCommands.size() - 1; i > -1; i--) {
                UndoableCommand command = (UndoableCommand) finalizerCommands.get(i);
                command.rollback(monitor);
            }

            for (int i = commands.size() - 1; i > -1; i--) {
                // reset the eSetDeliverState so that the last change will trigger a re-render
                if (i == 0) {
                    model.eSetDeliver(previousDeliver);
                }
                UndoableCommand command = (UndoableCommand) commands.get(i);
                command.rollback(monitor);
            }

        } finally {
            model.eSetDeliver(previousDeliver);
        }
    }
}
