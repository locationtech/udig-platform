/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
 * Stops an animation on run and starts it on rollback.
 * 
 * @author jones
 * @since 1.1.0
 */
public class StartAnimationCommand extends AbstractCommand implements UndoableMapCommand {

    private List<IAnimation> animations;
    private IMapDisplay display;

    /**
     * New instance
     * @param animations animations to run.
     */
    public StartAnimationCommand( IMapDisplay display, List<IAnimation> animations ) {
        this.animations=animations;
        this.display=display;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        for( IAnimation anim : animations ) {
            anim.setValid(true);            
            AnimationUpdater.runTimer(display, anim);
        }
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        for( IAnimation anim : animations ) {
            anim.setValid(false);            
        }
    }

    public String getName() {
        return Messages.StartAnimationCommand_name; 
    }

}
