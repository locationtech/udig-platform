/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.ui.internal.commands.draw;

import java.util.List;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

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
