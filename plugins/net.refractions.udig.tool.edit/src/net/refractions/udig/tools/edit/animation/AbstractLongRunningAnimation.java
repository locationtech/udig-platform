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
package net.refractions.udig.tools.edit.animation;

import net.refractions.udig.core.IProvider;
import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;

/**
 * Provides framework for commands that run an unknown length of
 * time.
 * 
 * @author jones
 * @since 1.1.0
 */
public abstract class AbstractLongRunningAnimation extends AbstractDrawCommand
    implements IAnimation{
    protected int maxSize;
    protected int frame;
    boolean smaller = true;
    private IProvider<Boolean> isValidProvider;

    AbstractLongRunningAnimation(int maxSize, IProvider<Boolean> isValidProvider){
        frame=maxSize;
        
        this.maxSize=maxSize==0?1:maxSize;
        this.isValidProvider=isValidProvider;
    }
    
    public void nextFrame() {
        if (frame == 1)
            smaller = false;
        else if (frame == maxSize - 1)
            smaller = true;
    
        if (smaller)
            frame--;
        else
            frame++;
    
        frame = frame % maxSize;
    }

    public boolean hasNext() {
        return isValid();
    }
    @Override
    public boolean isValid() {
        return super.isValid()&& isValidProvider.get();
    }

}
