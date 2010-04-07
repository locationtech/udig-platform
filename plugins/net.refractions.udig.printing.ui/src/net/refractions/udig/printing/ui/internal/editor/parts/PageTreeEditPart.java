/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.printing.ui.internal.editor.parts;

import java.util.List;

import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.model.PropertyListener;

import org.eclipse.gef.editparts.AbstractTreeEditPart;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p><p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p><p>
 * Example Use:<pre><code>
 * PageTreeEditPart x = new PageTreeEditPart( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class PageTreeEditPart extends AbstractTreeEditPart {
    private InternalPropertyListener listener = new InternalPropertyListener();

    public PageTreeEditPart( Page page) {
        super(page);
    }
    
    public void activate() {
        if (isActive()) {
            return;
        }
        super.activate();
        ((Page) getModel()).eAdapters().add(this.listener);
    }
    public void deactivate() {
        if (!isActive()) {
            return;
        }
        super.deactivate();
        ((Page) getModel()).eAdapters().remove(this.listener);
    }
    
    protected List getModelChildren() {
        return ((Page) getModel()).getBoxes();
    }
    
    protected class InternalPropertyListener extends PropertyListener {
        
            /**
         * TODO summary sentence for boxesChanged ...
         * 
         * @see net.refractions.udig.printing.model.PropertyListener#boxesChanged()
         * 
         */
        protected void boxesChanged() {
            refreshChildren();
        }
    }

}
