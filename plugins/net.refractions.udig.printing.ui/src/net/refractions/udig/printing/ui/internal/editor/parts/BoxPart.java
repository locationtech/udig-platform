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

import java.util.Collection;
import java.util.List;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.BoxPrinter;
import net.refractions.udig.printing.model.Connection;
import net.refractions.udig.printing.model.PropertyListener;
import net.refractions.udig.printing.ui.IBoxEditAction;
import net.refractions.udig.printing.ui.internal.BoxFactory;
import net.refractions.udig.printing.ui.internal.PrintingPlugin;
import net.refractions.udig.printing.ui.internal.editor.BoxAction;
import net.refractions.udig.printing.ui.internal.editor.commands.ConnectionCreateCommand;
import net.refractions.udig.printing.ui.internal.editor.commands.ConnectionReconnectCommand;
import net.refractions.udig.printing.ui.internal.editor.figures.BoxFigure;
import net.refractions.udig.printing.ui.internal.editor.policies.PageElementEditPolicy;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;

/**
 * A part for all boxes.
 * 
 * @author Richard Gould
 * @author Jesse
 * @since 0.3
 */
public class BoxPart extends AbstractGraphicalEditPart implements NodeEditPart, IAdaptable {

    protected InternalPropertyListener listener = new InternalPropertyListener();
    private BoxAction defaultAction;

    @SuppressWarnings("unchecked")
    public void activate() {
        if (isActive()) {
            return;
        }

        super.activate();
        Box box = ((Box) getModel());
        box.eAdapters().add(this.listener);
        box.addPropertyChangeListener(listener);
    }

    public void deactivate() {
        if (!isActive()) {
            return;
        }
        super.deactivate();
        Box box = ((Box) getModel());
        box.eAdapters().remove(this.listener);
        box.removePropertyChangeListener(listener);
    }
    
    @Override
	public Object getAdapter(Class key) {
    	Box box = ((Box) getModel());
    	if (box instanceof IAdaptable) {
    		Object obj = ((IAdaptable) box).getAdapter(key);
    		if (obj != null) {
    			return obj;
    		}
    	}
		return super.getAdapter(key);
	}

	protected void refreshVisuals() {
        Box labelBox = (Box) this.getModel();
        Point loc = labelBox.getLocation();
        Dimension size = labelBox.getSize();
        Rectangle rectangle = new Rectangle(loc, size);

        ((BoxFigure) this.getFigure()).setBox((Box) this.getModel());

        ((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), rectangle);
    }
    
    public BoxPrinter getBoxPrinter(){
        return ((Box)getModel()).getBoxPrinter();
    }

    

    public void performRequest( Request request ) {
        EditPolicy policy = getEditPolicy(request.getType());
        if( policy instanceof PrintingEditPolicy ){
            PrintingEditPolicy editPolicy = (PrintingEditPolicy)policy;
            IBoxEditAction action = editPolicy.getAction().getBoxEditAction();
            action.init(this);
            action.perform();
            Display display = getViewer().getControl().getDisplay();
            while( !action.isDone() ){
                if( !display.readAndDispatch() ){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
            if( !action.isDone() )
                return;
            
            Command command = action.getCommand();
            
            if( command!=null && command.canExecute() ){
                getViewer().getEditDomain().getCommandStack().execute(command);
            }
        }
    }

    @Override
    public void setModel( Object model ) {
        // TODO Auto-generated method stub
        super.setModel(model);
    }
    
	protected List getModelSourceConnections() {
		return ((Box) getModel()).getSourceConnections();
	}
	protected List getModelTargetConnections() {
		return ((Box) getModel()).getTargetConnections();
	}
   
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new PageElementEditPolicy());
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy(){

            protected Command getConnectionCompleteCommand( CreateConnectionRequest request ) {
                ConnectionCreateCommand cmd = (ConnectionCreateCommand) request.getStartCommand();
                cmd.setTarget((Box) getHost().getModel());
                return cmd;
            }

            protected Command getConnectionCreateCommand( CreateConnectionRequest request ) {
                Box source = (Box) getHost().getModel();
                ConnectionCreateCommand cmd = new ConnectionCreateCommand(source);
                request.setStartCommand(cmd);
                return cmd;
            }

            protected Command getReconnectTargetCommand( ReconnectRequest request ) {
                Connection conn = (Connection) request.getConnectionEditPart().getModel();
                Box newTarget = (Box) getHost().getModel();
                ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
                cmd.setNewTarget(newTarget);
                return cmd;
            }

            protected Command getReconnectSourceCommand( ReconnectRequest request ) {
                Connection conn = (Connection) request.getConnectionEditPart().getModel();
                Box newSource = (Box) getHost().getModel();
                ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
                cmd.setNewSource(newSource);
                return cmd;
            }

        });

        List<BoxFactory> boxes = PrintingPlugin.getDefault().getBoxes();
        
        Box model = (Box) getModel();
        BoxPrinter printer = model.getBoxPrinter();
        String defaultActionID = null;
        for( BoxFactory boxFactory : boxes ) {
            if( boxFactory.getType() == printer.getClass() ){
                defaultActionID = boxFactory.getDefaultActionID();
                break;
            }
        }
        
        Collection<BoxAction> actions = PrintingPlugin.getBoxExtensionActions(null);
        for( BoxAction element : actions ) {
            try {

                boolean isAcceptable;
                if (element.getId() != null && element.getId().equals(defaultActionID)) {
                    isAcceptable = true;
                    this.defaultAction = element;
                } else {
                    isAcceptable = element.isAcceptableBoxPrinter(printer);
                }
                
                if (isAcceptable) {
                    EditPolicy policyObj = element.getEditPolicy();
                    installEditPolicy(element.getRequest().getType(), policyObj);
                }
            } catch (Throwable e) {
                continue;
            }
        }

    }

    protected IFigure createFigure() {
        return new BoxFigure();
    }
    
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}


    protected class InternalPropertyListener extends PropertyListener implements IPropertyChangeListener {

        protected void textChanged() {
            refreshVisuals();
        }
        protected void locationChanged() {
            refreshVisuals();
        }
        protected void sizeChanged() {
            refreshVisuals();
        }
        public void propertyChange( PropertyChangeEvent event ) {
            refreshVisuals();
        }
    }


    /**
     * Returns the default action for the box.  See {@link BoxFactory#getDefaultActionID()} for more details
     *
     * @return the default action for the box
     * 
     * @see BoxFactory#getDefaultActionID()
     */
    public BoxAction getDefaultAction() {
        return defaultAction;
    }
    
}
