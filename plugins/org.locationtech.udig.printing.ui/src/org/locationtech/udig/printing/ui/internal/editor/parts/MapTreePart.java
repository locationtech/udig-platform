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
package org.locationtech.udig.printing.ui.internal.editor.parts;

import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.model.PropertyListener;
import org.locationtech.udig.printing.model.impl.MapBoxPrinter;
import org.locationtech.udig.printing.ui.internal.Messages;
import org.locationtech.udig.printing.ui.internal.editor.figures.BoxFigure;
import org.locationtech.udig.printing.ui.internal.editor.policies.MapEditPolicy;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.gef.tools.DirectEditManager;

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
 * MapTreePart x = new MapTreePart( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class MapTreePart extends AbstractTreeEditPart {
    protected DirectEditManager manager;
    private InternalPropertyListener listener = new InternalPropertyListener();
    
    /**
     * Construct <code>MapTreePart</code>.
     *
     * @param box
     */
    public MapTreePart( Box box ) {
        super(box);
    }

    @SuppressWarnings("unchecked")
    public void activate() {
        if (isActive()) {
            return;
        }
        
        super.activate();
        ((Box)getModel()).eAdapters().add(listener);
    }
    
    public void deactivate() {
        if (!isActive()) {
            return;
        }
        super.deactivate();
        ((Box) getModel()).eAdapters().remove(listener);
    }
    
    protected void refreshVisuals() {
        setWidgetText(Messages.MapTreePart_mapLabel +((MapBoxPrinter)((Box) getModel()).getBoxPrinter()).getMap().getName()); 
    }
    
    public void performRequest(Request request) {
        super.performRequest(request);
    }
    
    protected IFigure createFigure() {
        return new BoxFigure();
    }
    
    protected void createEditPolicies() {
        super.createEditPolicies();
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new MapEditPolicy());    
    }
        
    protected class InternalPropertyListener extends PropertyListener {
        
        protected void locationChanged() {
            refreshVisuals();
        }
        protected void sizeChanged() {
            refreshVisuals();
        }
        
        @Override
        protected void boxesChanged() {
            super.boxesChanged();
        }
    }
}
