/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2016, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.testsupport.command;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableCommand;

public class BlankUndoableCommand extends AbstractCommand implements MapCommand, UndoableCommand {

    public void run( IProgressMonitor monitor ) throws Exception {
    }

    public String getName() {
        return null;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
    }

}