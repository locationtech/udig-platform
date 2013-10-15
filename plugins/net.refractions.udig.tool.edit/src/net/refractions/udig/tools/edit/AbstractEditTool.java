/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit;

import java.util.List;
import java.util.Set;

import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.tool.display.CursorProxy;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseWheelEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.project.ui.tool.SimpleTool;
import net.refractions.udig.tools.edit.activator.EnableAcceptEditCommandHandlerActivator;
import net.refractions.udig.tools.edit.support.EditUtils;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

/**
 * Super class for edit tools.  This class delegates to the EditToolHandler which must be initialized by
 * the subclass during construction.
 * 
 * @author jones
 * @since 1.1.0
 */
public abstract class AbstractEditTool extends SimpleTool {

    protected EditToolHandler handler;
    
    public AbstractEditTool( ) {
        super(MOUSE|WHEEL|MOTION);
    }

    @Override
    public void init( IConfigurationElement element ) {
        Cursor editCursor=null;
        IToolManager toolManager = ApplicationGIS.getToolManager();
        
        /*
         * Vitalus: The modern tool cursor approach is used - the order of finding the
         * default cursor:
         * 1) Try to find default cursor from "toolCursorId" attribute
         * 2) Try to find child configuration element "cursor"
         * 3) Set default system arrow cursor
         */
        String cursorId = element.getAttribute("toolCursorId"); //$NON-NLS-1$
        if(cursorId != null){
            editCursor = toolManager.findToolCursor(cursorId);
        }
        if(editCursor == null){
            IConfigurationElement[] cursorElement = element.getChildren("cursor"); //$NON-NLS-1$
            editCursor = new CursorProxy(cursorElement[0]).getCursor();
        }
        
        if( editCursor==null ){
            editCursor=Display.getDefault().getSystemCursor(SWT.CURSOR_ARROW);
        }

        //Must be configured in "net.refractions.udig.tool.default" plugin
        Cursor selectionCursor = toolManager.findToolCursor("boxSelectionCursor"); //$NON-NLS-1$
        handler=new EditToolHandler(selectionCursor, editCursor);
        initRequiredActivators();
        EditToolConfigurationHelper editToolConfigurationHelper = new EditToolConfigurationHelper(getHandler().getBehaviours());
        initEventBehaviours(editToolConfigurationHelper);
        initEnablementBehaviours(handler.getEnablementBehaviours());
        initRequiredAcceptBehaviours();
        
        initCancelBehaviours(handler.getCancelBehaviours());
        handler.setTool(this);
        
        if( !editToolConfigurationHelper.isDone() )
            throw new IllegalStateException("configurator's done method was not called."); //$NON-NLS-1$
    }
    
	private void initRequiredActivators() {
		initActivators(handler.getActivators());
        handler.getActivators().add(new EnableAcceptEditCommandHandlerActivator());
	}

	private void initRequiredAcceptBehaviours() {
		initAcceptBehaviours(handler.getAcceptBehaviours());
        // make sure that the layer cache is cleaned up to prevent memory leaks.
        handler.getAcceptBehaviours().add(new Behaviour(){

            public UndoableMapCommand getCommand( EditToolHandler handler ) {
                EditUtils.instance.clearLayerStateShapeCache(handler.getContext().getMapLayers());
                return null;
            }

            public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
                EditPlugin.log("", error); //$NON-NLS-1$
            }

            public boolean isValid( EditToolHandler handler ) {
                return true;
            }
            
        });
	}
    
    /**
     * Initializes the list of Activators that are ran when the tool is activated and deactivated.
     * 
     * @see DefaultEditToolBehaviour
     *
     * @param activators an empty list.
     */
    protected abstract  void initActivators( Set<Activator> activators );
    /**
     * Initializes the list of Behaviours to run when the current edit has been accepted.  
     * Acceptance is signalled by a double click or the Enter key
     * 
     * @see DefaultEditToolBehaviour
     *
     * @param acceptBehaviours an empty list
     */
    protected abstract  void initAcceptBehaviours( List<Behaviour> acceptBehaviours );
    /**
     * Initializes the behaviours that are ran when a cancel signal is received (the ESC key).
     * 
     * @see DefaultEditToolBehaviour
     *
     * @param cancelBehaviours an empty list
     */
    protected abstract  void initCancelBehaviours( List<Behaviour> cancelBehaviours );
    /**
     * Initializes the Event Behaviours that are run when an event occurs.  Since this can be complex a helper
     * class is provided to build the complex datastructure of Behaviours.
     * 
     * @see DefaultEditToolBehaviour 
     * @see EditToolConfigurationHelper
     *
     * @param helper a helper for constructing the complicated structure of EventBehaviours.
     */
    protected abstract  void initEventBehaviours( EditToolConfigurationHelper helper );
    /**
     * Initializes the list of {@link EnablementBehaviour}s that are ran to determine if the tool is enabled given an
     * event.  For example if the mouse cursor is outside the valid bounds of a CRS for a layer an EnablementBehaviour might
     * signal that editing is illegal and provide a message for the user indicating why. 
     * 
     * @see DefaultEditToolBehaviour
     *
     * @param enablementBehaviours an empty list
     */
    protected abstract  void initEnablementBehaviours( List<EnablementBehaviour> enablementBehaviours );
    
    /**
     * Called only by unit tests.  Has no effect on state of tool.
     */
    public void testinitActivators( Set<Activator> activators ){
        initActivators(activators);
    }
    /**
     * Called only by unit tests.  Has no effect on state of tool.
     */
    public void testinitAcceptBehaviours( List<Behaviour> acceptBehaviours ){
        initAcceptBehaviours(acceptBehaviours);
    }
    /**
     * Called only by unit tests.  Has no effect on state of tool.
     */
    public void testinitCancelBehaviours( List<Behaviour> cancelBehaviours ){
        initCancelBehaviours(cancelBehaviours);
    }
    /**
     * Called only by unit tests.  Has no effect on state of tool.
     */
    public void testinitEventBehaviours( EditToolConfigurationHelper helper ){
        initEventBehaviours(helper);
    }

    @Override
    public void setContext( IToolContext context ) {
        if( getContext()!=null && getContext().getViewportPane()!=null)
            getContext().getViewportPane().repaint();
        super.setContext(context);
        handler.setContext(context);
        if( isActive() ){
            handler.basicDisablement();
            handler.basicEnablement();
        }
        handler.repaint();
    }
    
    @Override
    public void setActive( boolean active ) {
        super.setActive(active);
        handler.setActive(active);
    }
    
    @Override
    protected void onMouseDoubleClicked( MapMouseEvent e ) {
        handler.handleEvent(e, EventType.DOUBLE_CLICK);
    }
    
    @Override
    protected void onMouseDragged( MapMouseEvent e ) {
        handler.handleEvent(e, EventType.DRAGGED);
    }
    
    @Override
    protected void onMouseEntered( MapMouseEvent e ) {
        handler.handleEvent(e, EventType.ENTERED);
    }
    
    @Override
    protected void onMouseExited( MapMouseEvent e ) {
        handler.handleEvent(e, EventType.EXITED);
    }
    
    @Override
    protected void onMouseMoved( MapMouseEvent e ) {
        handler.handleEvent(e, EventType.MOVED);
    }
    
    @Override
    protected void onMousePressed( MapMouseEvent e ) {
        handler.handleEvent(e, EventType.PRESSED);
    }
    
    @Override
    protected void onMouseReleased( MapMouseEvent e ) {
        handler.handleEvent(e, EventType.RELEASED);
    }
    
    @Override
    protected void onMouseWheelMoved( MapMouseWheelEvent e ) {
        handler.handleEvent(e, EventType.WHEEL);
    }

    @Override
    protected void onMouseHovered( MapMouseEvent e ) {
        handler.handleEvent(e, EventType.HOVERED);
    }
    
    /**
     * @return Returns the handler.
     */
    public EditToolHandler getHandler() {
        return handler;
    }

    /**
     * @param handler The handler to set.
     */
    public void setHandler( EditToolHandler handler ) {
        this.handler = handler;
    }
    
    
}
