/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.animation;

import org.locationtech.udig.core.IProvider;
import org.locationtech.udig.project.ui.IAnimation;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;

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
