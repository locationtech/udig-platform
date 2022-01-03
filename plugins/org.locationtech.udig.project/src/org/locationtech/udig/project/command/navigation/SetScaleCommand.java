/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command.navigation;

import java.text.MessageFormat;
import java.text.NumberFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.project.command.UndoableCommand;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.render.ViewportModel;

/**
 * Sets the scale denominator of the map.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class SetScaleCommand extends AbstractNavCommand implements UndoableCommand {

    private double oldScale;

    private double newScale;

    /**
     *
     * @param newScale Scale Denominator
     */
    public SetScaleCommand(double newScale) {
        this.newScale = newScale;
    }

    @Override
    public void rollback(IProgressMonitor monitor) throws Exception {
        if (model != null) {
            model.setScale(oldScale);
        } else {
            getMap().getViewportModelInternal().setScale(oldScale);
        }
    }

    @Override
    public String getName() {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        return MessageFormat.format(Messages.SetScaleCommand_name, numberFormat.format(newScale));
    }

    @Override
    public void run(IProgressMonitor monitor) throws Exception {
        if (model != null) {
            oldScale = model.getScaleDenominator();
            model.setScale(newScale);
        } else {
            ViewportModel viewportModel = getMap().getViewportModelInternal();
            this.oldScale = viewportModel.getScaleDenominator();
            viewportModel.setScale(newScale);
        }
    }

    @Override
    protected void runImpl(IProgressMonitor monitor) throws Exception {
        run(monitor);
    }

}
