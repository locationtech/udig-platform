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
package net.refractions.udig.printing.model;

import java.awt.Graphics2D;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IMemento;


/**
 * Provides simple/stupid implementation for the optional methods in BoxPrinter.
 * <p>
 * Nothing is saved when save is called.  So everything must be hard coded.  Preview simply calls
 * draw() and only does so once.
 * </p>
 *
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractBoxPrinter implements BoxPrinter {

    private Box box;
    private boolean dirty=false;
    private PropertyListener listener=new PropertyListener(){
        @Override
        protected void locationChanged() {
            boxLocationChanged();
        }

        @Override
        protected void sizeChanged() {
            boxSizeChanged();
        }
    };

    /**
     * called when the location of the box has changed
     */
    protected void boxLocationChanged(){
        dirty=true;
    }

    /**
     * called when the size of the box has changed
     */
    protected void boxSizeChanged(){
        dirty=true;
    }

    /**
     * By default this method does nothing
     */
    public void save( IMemento memento ) {
    }

    /**
     * By default this method does nothing
     */
    public void load( IMemento memento ) {
    }

    /**
     * By default this method calls draw and sets dirty to be false.
     */
    public void createPreview( Graphics2D graphics, IProgressMonitor monitor ) {
        draw(graphics, monitor);
        dirty=false;
    }

    /**
     * By default this will return false when ever the size of location of the box has been changed.
     */
    public boolean isNewPreviewNeeded() {
        return dirty;
    }

    public void setDirty(boolean dirty ) {
        boolean oldDirty = this.dirty;
        this.dirty = dirty;
        // trigger re-render
        if( dirty && getBox()!=null){
            getBox().notifyPropertyChange(
                    new PropertyChangeEvent(this,
                            "dirty", oldDirty, dirty)); //$NON-NLS-1$
        }
    }

    public Box getBox() {
        return box;
    }

    @SuppressWarnings("unchecked")
    public void setBox( Box box2 ) {
        if( box!=null )
            box.eAdapters().remove(listener);
        this.box=box2;
        if( box!=null)
            box.eAdapters().add(listener);
    }

}
