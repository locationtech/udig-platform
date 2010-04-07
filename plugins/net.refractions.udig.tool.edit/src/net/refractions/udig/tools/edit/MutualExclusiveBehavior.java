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
package net.refractions.udig.tools.edit;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.command.UndoableMapCommand;

/**
 * A Composite Mode (See GOF Composite Pattern) where contained modes are ordered by priority and only the
 * mode with the highest priority and is valid for the current context is ran.
 * 
 * @author jones
 * @since 1.1.0
 */
public class MutualExclusiveBehavior implements Behaviour{

    private List<Behaviour> behaviours=new ArrayList<Behaviour>();
    
    public MutualExclusiveBehavior(List<Behaviour> modes) {
        this.behaviours=modes;
    }
    
    /**
     * @param behaviour
     */
    public MutualExclusiveBehavior( Behaviour behaviour ) {
        behaviours.add(behaviour);
    }

    /**
     * Create an empty behaviour.  Behaviours must be added via the {@link #getBehaviours()} method
     */
    public MutualExclusiveBehavior() {
    }

    public boolean isValid( EditToolHandler handler ) {
        for( Behaviour mode : behaviours ) {
            EditPlugin.trace(EditPlugin.BEHAVIOUR,
                    "  Validating mode: " + mode.getClass().getName(), null); //$NON-NLS-1$
            if( mode.isValid(handler) )
                return true;
        }
        return false;
    }

    public UndoableMapCommand getCommand( EditToolHandler handler ) {
        List<Behaviour> behaviours=new ArrayList<Behaviour>(this.behaviours);
        for( Behaviour mode : behaviours ) {
            if( mode.isValid(handler) ){
                UndoableMapCommand c=null;
                try{
                    c=mode.getCommand(handler);
                    EditPlugin.trace(EditPlugin.BEHAVIOUR,
                            "  Running mode: " + mode.getClass().getName(), null); //$NON-NLS-1$
                    
                    return c;
                } catch (Throwable error) {
                    EditPlugin.trace(EditPlugin.BEHAVIOUR,
                            "Error running mode: " + mode.getClass().getName(), null); //$NON-NLS-1$
                    mode.handleError(handler, error, c);
                }
            }
        }
        return null;
        
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("Very Strange I don't know how this happenned...", error); //$NON-NLS-1$
    }
    
    @Override
    public String toString() {
        StringBuffer buffer=new StringBuffer("["); //$NON-NLS-1$
        for( Behaviour behaviour : this.behaviours ) {
            buffer.append(behaviour.toString());
            buffer.append("||"); //$NON-NLS-1$
        }
         buffer.reverse();
         buffer.append("  "); //$NON-NLS-1$
         buffer.reverse();
        buffer.append("]"); //$NON-NLS-1$
        return buffer.toString();
    }

    /**
     * @return Returns the behaviours.
     */
    public List<Behaviour> getBehaviours() {
        return this.behaviours;
    }

}
