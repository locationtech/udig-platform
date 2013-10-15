/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
