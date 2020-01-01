/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.commands.draw;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.project.ui.IAnimation;
import org.locationtech.udig.project.ui.internal.Messages;

/**
 * Starts an animation on run and stops it on rollback.
 * 
 * @author jones
 * @since 1.1.0
 */
public class StopAnimationCommand extends AbstractCommand implements UndoableMapCommand {


    private List<IAnimation> animations;
    private IMapDisplay display;

    /**
     * New instance
     * @param animations animations to run.
     */
    public StopAnimationCommand( IMapDisplay display, List<IAnimation> animations ) {
        this.animations=animations;
        this.display=display;
    }


    public void run( IProgressMonitor monitor ) throws Exception {
        for( IAnimation anim : animations ) {
            anim.setValid(false);            
        }
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        for( IAnimation anim : animations ) {
            anim.setValid(true);            
            AnimationUpdater.runTimer(display, anim);
        }
    }

    public String getName() {
        return Messages.StopAnimationCommand_name; 
    }

}
