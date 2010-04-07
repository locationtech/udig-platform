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

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.PropertyListener;
import net.refractions.udig.printing.ui.internal.editor.policies.PageElementEditPolicy;

import org.eclipse.gef.EditPolicy;
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
 * BoxTreeEditPart x = new BoxTreeEditPart( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class BoxTreeEditPart extends AbstractTreeEditPart {

    private InternalPropertyListener listener = new InternalPropertyListener();
    
    /**
     * Construct <code>BoxTreeEditPart</code>.
     *
     * @param model
     */
    public BoxTreeEditPart( Box model ) {
        super(model);
    }
    /**
     * TODO summary sentence for activate ...
     * 
     * @see org.eclipse.gef.EditPart#activate()
     * 
     */
    public void activate() {
        if (isActive()) {
            return;
        }
        super.activate();
        ((Box) getModel()).eAdapters().add(this.listener);
    }
    /**
     * TODO summary sentence for deactivate ...
     * 
     * @see org.eclipse.gef.EditPart#deactivate()
     * 
     */
    public void deactivate() {
        if (!isActive()) {
            return;
        }
        super.deactivate();
        ((Box) getModel()).eAdapters().remove(this.listener);
    }
    
    /**
     * TODO summary sentence for createEditPolicies ...
     * 
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     * 
     */
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new PageElementEditPolicy());
    }
    
    /**
     * TODO summary sentence for refreshVisuals ...
     * 
     * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
     * 
     */
    protected void refreshVisuals() {
        setWidgetText(getModel().toString());
    }
    private class InternalPropertyListener extends PropertyListener {
        
            /**
         * TODO summary sentence for locationChanged ...
         * 
         * @see net.refractions.udig.printing.model.PropertyListener#locationChanged()
         * 
         */
        protected void locationChanged() {
            refreshVisuals();
        }
        /**
         * TODO summary sentence for sizeChanged ...
         * 
         * @see net.refractions.udig.printing.model.PropertyListener#sizeChanged()
         * 
         */
        protected void sizeChanged() {
            refreshVisuals();
        }
    }
}
