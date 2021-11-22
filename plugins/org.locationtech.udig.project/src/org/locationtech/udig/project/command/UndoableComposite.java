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
package org.locationtech.udig.project.command;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

/**
 * A UndoableCommand composed of multiple UndoableCommands. Executes and rollsback as a atomic
 * command. See Composite Pattern.
 *
 * @author jeichar
 * @since 0.3
 * @see CompositeCommand
 * @see UndoableCommand
 */
public class UndoableComposite extends CompositeCommand
        implements UndoableMapCommand, PostDeterminedEffectCommand {

    /**
     * Creates a new instance of UndoableComposite
     *
     * @param undoableCommands an ordered list of UndoableCommands
     */
    public UndoableComposite() {
        super();
    }

    /**
     * Creates a new instance of UndoableComposite
     *
     * API List<UndoableCommand>
     *
     * @param undoableCommands an ordered list of UndoableCommands
     */
    public UndoableComposite(List undoableCommands) {
        super(undoableCommands);
    }

    @Override
    public void run(IProgressMonitor monitor) throws Exception {
        execute(monitor);
    }

    /**
     * @see org.locationtech.udig.project.internal.command.UndoableCommand#rollback()
     */
    @Override
    public void rollback(IProgressMonitor monitor) throws Exception {

        for (int i = finalizerCommands.size() - 1; i > -1; i--) {
            UndoableCommand command = (UndoableCommand) finalizerCommands.get(i);
            command.rollback(monitor);
        }

        for (int i = commands.size() - 1; i > -1; i--) {
            UndoableCommand command = (UndoableCommand) commands.get(i);
            command.rollback(monitor);
        }
    }

    @Override
    public boolean execute(IProgressMonitor monitor) throws Exception {
        monitor.beginTask(getName(), 2 + 10 * commands.size() + 10 * finalizerCommands.size());
        monitor.worked(2);
        boolean changedState = false;
        try {
            for (MapCommand command : commands) {
                command.setMap(getMap());
                SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
                if (command instanceof PostDeterminedEffectCommand) {
                    boolean change = ((PostDeterminedEffectCommand) command).execute(subMonitor);
                    changedState = changedState || change;
                } else {
                    command.run(subMonitor);
                    changedState = true;
                }
                subMonitor.done();
            }
        } finally {
            for (MapCommand command : finalizerCommands) {
                command.setMap(getMap());
                SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
                if (command instanceof PostDeterminedEffectCommand) {
                    boolean change = ((PostDeterminedEffectCommand) command).execute(subMonitor);
                    changedState = changedState || change;
                } else {
                    command.run(subMonitor);
                    changedState = true;
                }
                subMonitor.done();
            }

        }
        monitor.done();

        return changedState;
    }

    public void add(MapCommand command) {
        commands.add(command);
    }

}
