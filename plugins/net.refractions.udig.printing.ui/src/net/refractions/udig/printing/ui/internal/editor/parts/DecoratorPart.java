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
import net.refractions.udig.printing.ui.internal.editor.figures.BoxFigure;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;

/**
 * @author Richard Gould
 * @since 0.3
 */
public class DecoratorPart extends BoxPart {
    
    protected DirectEditManager manager;
    protected InternalPropertyListener listener = new InternalPropertyListener();
    
    public void activate() {
        if (isActive()) {
            return;
        }
        
        super.activate();
        ((Box) getModel()).eAdapters().add(this.listener);
    }
    
    public void deactivate() {
        if (!isActive()) {
            return;
        }
        super.deactivate();
        ((Box) getModel()).eAdapters().remove(this.listener);
    }
    
    protected void refreshVisuals() {
        Box scalebarBox = (Box) this.getModel();
        Point loc = scalebarBox.getLocation();
        Dimension size = scalebarBox.getSize();
        Rectangle rectangle = new Rectangle(loc, size);
        
        ((BoxFigure) getFigure()).setBox((Box) getModel());
        
        ((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), rectangle);
    }
    
    public void performRequest(Request request) {
        if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
            if (manager == null) {
                BoxFigure nodeFigure = (BoxFigure) getFigure();
                manager = new LabelDirectEditManager(this, TextCellEditor.class, new LabelCellEditorLocator(nodeFigure), nodeFigure);
            }
            manager.show();
        }
    }
    
    protected IFigure createFigure() {
        return new BoxFigure();
    }
    
    protected void createEditPolicies() {
        super.createEditPolicies();
//        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new LabelDirectEditPolicy()); 
    }
    
    protected class InternalPropertyListener extends PropertyListener {
        
        protected void textChanged() {
            refreshVisuals();
        }
        protected void locationChanged() {
            refreshVisuals();
        }
        protected void sizeChanged() {
            refreshVisuals();
        }
    }
}
