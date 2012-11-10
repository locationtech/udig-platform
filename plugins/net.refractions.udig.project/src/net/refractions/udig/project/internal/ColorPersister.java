/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.internal;

import java.awt.Color;

import net.refractions.udig.project.IPersister;

import org.eclipse.ui.IMemento;

/**
 * Persister for persisting colors on blackboard.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ColorPersister extends IPersister {

    @Override
    public Class getPersistee() {
        return Color.class;
    }

    @Override
    public Object load( IMemento memento ) {
        Integer red = memento.getInteger("red"); //$NON-NLS-1$
        Integer green = memento.getInteger("green"); //$NON-NLS-1$
        Integer blue = memento.getInteger("blue"); //$NON-NLS-1$
        Integer alpha = memento.getInteger("alpha"); //$NON-NLS-1$
        return new Color(red, green, blue, alpha);
    }

    @Override
    public void save( Object object, IMemento memento ) {
        Color color=(Color)object;
        memento.putInteger("red", color.getRed()); //$NON-NLS-1$
        memento.putInteger("green", color.getGreen()); //$NON-NLS-1$
        memento.putInteger("blue", color.getBlue()); //$NON-NLS-1$
        memento.putInteger("alpha", color.getAlpha()); //$NON-NLS-1$
    }

}
