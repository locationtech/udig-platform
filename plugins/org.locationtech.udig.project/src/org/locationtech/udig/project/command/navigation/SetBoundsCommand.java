package org.locationtech.udig.project.command.navigation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.project.internal.Messages;

public class SetBoundsCommand extends AbstractNavCommand {

    private Envelope newBounds;

    public SetBoundsCommand(Envelope newBounds) {
        this.newBounds = newBounds;
    }

    @Override
    protected void runImpl(IProgressMonitor monitor) throws Exception {
        model.setBounds(newBounds);
    }

    @Override
    public String getName() {
        return Messages.SetBoundsCommand_name;
    }
}
