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
package org.locationtech.udig.project.ui.tool;

import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;
import org.locationtech.udig.project.ui.internal.MapEditorPart;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelListener;

/**
 * The Abstract base class for items. Provides support for enabling and disabling modal items.
 * <p>
 * Tool developers that extend this class override the event notification methods. This class
 * handles registering the tool with the current map display.
 * </p>
 * <p>
 * Note: subclasses must have a single public parameterless constructor that calls the super
 * constructor with a number indicating which event they want to listen for.
 * </p>
 * <p>
 * Example:
 * 
 * <pre><code>
 *  super( MOUSE|MOTION ) indicates that the items will be receive all mouse and mouse motion commands.
 * </code></pre>
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 * @see Tool
 * @see MapMouseListener
 * @see MapMouseMotionListener
 * @see MapMouseWheelListener
 * @see EventListener
 */
public abstract class AbstractTool
        implements
            Tool,
            MapMouseListener,
            MapMouseMotionListener,
            MapMouseWheelListener,
            EventListener {

    /** Flag indicating that the tool does not listen for events from the ViewportPane */
    public final static int NONE = 0;    
    
    /** Flag indicating the tool is a {@linkplain MapMouseListener} */
    public final static int MOUSE = 1;

    /** Flag indicating the tool is a {@linkplain MapMouseMotionListener} */
    public final static int MOTION = 2;

    /** Flag indicating the tool is a {@linkplain MapMouseWheelListener} */
    public final static int WHEEL = 4;
    /** Flag indicating that the tool can drag from the Map Editor.  (drag in terms of drag-drop) */
    public final static int DRAG_DROP = 8;

    /** The items to use for tool actions */
    protected IToolContext context;

    private int targets;
    
    private Map<String, Object> properties = new HashMap<String, Object>();
    
    private IMapEditorSelectionProvider selectionProvider;
    
    /**
     * Tool's lifecycle listeners.
     */
    private Set<ToolLifecycleListener> listeners = new HashSet<ToolLifecycleListener>();
    
    
    /**
     * Enablement of the tool.
     */
    protected boolean enabled = true;
    
    private boolean isNotify = true;

    /**
     * Assigns renderManager to field and registers as a listener with source. The target field is
     * used to determine which type of listeners to register as.
     * 
     * @param targets Used to determine which listeners to register as. The following choices from
     *        the following list can be combined using | and this tool will be registered as both
     *        types of listeners:
     *        <ul>
     *        <li>{@link #MOUSE}- Register as a {@link MapMouseListener} </li>
     *        <li>{@link #MOTION}- Register as a (@link MapMouseMotionListener}</li>
     *        <li>{@link #WHEEL}- Register as a (@link MapMouseWheelListener} </li>
     *        </ul>
     */
    public AbstractTool( int targets ) {
        if ((targets & (MOUSE | MOTION | WHEEL | DRAG_DROP)) != targets)
            throw new RuntimeException("Argument targets indicated that the" //$NON-NLS-1$
                    + " items was not to be registered with any component.\n" //$NON-NLS-1$
                    + "Argument targets = " + targets); //$NON-NLS-1$

        this.targets = targets;
    }
    /**
     * Permits the tool to perform some initialization.  
     * <p> Default implementation does nothing</p>
     * @param element the configuration element that defines the tool extension
     */
    public void init(IConfigurationElement element){
        
    }

    /**
     * Registers this object as a listener.
     * <p>
     * The events this object listens for are indicated by the targets parameter in the constructor.
     * This method is called by setActive() and only needs to be called by client code if setActive
     * is overridden and not called with super().
     * </p>
     * This method does not need to be overridden by subclasses normally.
     */
    protected final void registerMouseListeners() {
        if (context == null || context.getViewportPane() == null)
            return;

        if ((targets & MOUSE) != 0)
            context.getViewportPane().addMouseListener(this);
        if ((targets & MOTION) != 0)
            context.getViewportPane().addMouseMotionListener(this);
        if ((targets & WHEEL) != 0)
            context.getViewportPane().addMouseWheelListener(this);
        if ((targets & DRAG_DROP) != 0){
            // yes I do want to do this in async even if I am in the display thread
            // because if I try to get the editor now, the editor may not be open yet.
            Display display = Display.getCurrent();
            if( display==null ){
                display=Display.getDefault();
            }
            final IToolContext context=this.context;
            display.asyncExec(new Runnable(){
                public void run() {
                    MapEditorPart editor = ApplicationGISInternal.findMapEditor(context.getMap());
                    if( editor !=null  && !editor.isDragging() ){
                        editor.setDragging(true);
                    }
                }
            });
        }
        
    }

    /**
     * Deregisters this object as a listener.
     * <p>
     * The events this object listens for are indicated by the targets parameter in the constructor.
     * This method is called by setActive() and only needs to be called by client code if setActive
     * is overridden and not called with super().
     * </p>
     * This method does not need to be overridden by subclasses normally.
     */
    protected final void deregisterMouseListeners() {
        if (context == null || context.getViewportPane() == null)
            return;

        context.getViewportPane().removeMouseListener(this);
        context.getViewportPane().removeMouseMotionListener(this);
        context.getViewportPane().removeMouseWheelListener(this);
        if ((targets & DRAG_DROP) != 0){
            // yes I do want to do this in async even if I am in the display thread
            // because if I try to get the editor now, the editor may not be open yet.
            Display display = Display.getCurrent();
            if( display==null ){
                display=Display.getDefault();
            }
            final IToolContext context=this.context;
            display.asyncExec(new Runnable(){
                public void run() {
                    MapEditorPart editor = ApplicationGISInternal.findMapEditor(context.getMap());
                    if( editor !=null  && editor.isDragging() ){
                        editor.setDragging(false);
                    }
                }
            });
        }
    }

    /**
     * Called each time an eclipse editor is activated. The RenderManager and ViewportPane are those
     * that are associated with the newly actived Eclipse view. Intended to be used if something
     * other just changing the current state happens. if false the tool is set as inactive and
     * deregistered with the component. This method does not need to be overridden by subclasses
     * normally.
     * 
     * @param items The items that the tool can use in its operations
     * @see IToolContext
     */
    public void setContext( IToolContext tools ) {
        deregisterMouseListeners();

        this.context = tools;

        registerMouseListeners();
    }
    


    /**
     * This method does not need to be overridden by subclasses normally.
     * 
     * @see org.locationtech.udig.project.ui.tool.Tool#getContext()
     * @see IToolContext
     */
    public IToolContext getContext() {
        return context;
    }
    /**
     * @see org.locationtech.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
        deregisterMouseListeners();
    }
    
    /**
     * @see org.locationtech.udig.project.ui.tool.Tool#getProperty()
     */
	public Object getProperty(String key) {
		return properties.get(key);

	}

	
	/**
	 *  (non-Javadoc)
	 * @see org.locationtech.udig.project.ui.tool.Tool#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/** (non-Javadoc)
	 * @see org.locationtech.udig.project.ui.tool.Tool#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		boolean oldValue = this.enabled; 
		this.enabled = enabled;
		
		if(isNotify && oldValue != enabled){
			ToolLifecycleEvent event = new ToolLifecycleEvent(this, ToolLifecycleEvent.Type.ENABLE, enabled, oldValue);
			fireEvent(event);
		}
	}
	
    /**
     * @see org.locationtech.udig.project.ui.tool.Tool#setProperty()
     */
	public void setProperty(String key, Object value) {
		properties.put(key, value);
	}
	
	
	
	
	
	/* (non-Javadoc)
	 * @see org.locationtech.udig.project.ui.tool.Tool#getSelectionProvider()
	 */
	public IMapEditorSelectionProvider getSelectionProvider() {
		return selectionProvider;
	}

	/* (non-Javadoc)
	 * @see org.locationtech.udig.project.ui.tool.Tool#setSelectionProvider(org.locationtech.udig.project.ui.tool.IMapEditorSelectionProvider)
	 */
	public void setSelectionProvider(IMapEditorSelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
	}

	/** (non-Javadoc)
	 * @see org.locationtech.udig.project.ui.tool.Tool#addListener(org.locationtech.udig.project.ui.tool.ToolLifecycleListener)
	 */
	public void addListener(ToolLifecycleListener listener){
		listeners.add(listener);
	}
	
	/** (non-Javadoc)
	 * @see org.locationtech.udig.project.ui.tool.Tool#removeListener(org.locationtech.udig.project.ui.tool.ToolLifecycleListener)
	 */
	public void removeListener(ToolLifecycleListener listener){
		listeners.remove(listener);
	}
	
	protected void setNotifyListeners(boolean isNotify){
		this.isNotify = isNotify;
	}
	
	
	/**
	 * @return
	 */
	protected boolean isNotifyListeners(){
		return isNotify;
	}
	
	
	/**
	 * @param event
	 */
	protected void fireEvent(ToolLifecycleEvent event){
		for (ToolLifecycleListener listener : listeners) {
			listener.changed(event);
		}
	}

    /**
     * This method may be overridden by subclasses
     * 
     * @see org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener#mouseEntered(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     * @see MapMouseEvent
     */
    public void mouseEntered( MapMouseEvent e ) { // do nothing
    }

    /**
     * This method may be overridden by subclasses
     * 
     * @see org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener#mouseExited(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     * @see MapMouseEvent
     */
    public void mouseExited( MapMouseEvent e ) { // do nothing
    }

    /**
     * This method may be overridden by subclasses
     * 
     * @see org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener#mousePressed(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     * @see MapMouseEvent
     */
    public void mousePressed( MapMouseEvent e ) { // do nothing
    }

    /**
     * This method may be overridden by subclasses
     * 
     * @see org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener#mouseReleased(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     * @see MapMouseEvent
     */
    public void mouseReleased( MapMouseEvent e ) { // do nothing
    }

    /**
     * This method may be overridden by subclasses
     * 
     * @see org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener#mouseDragged(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     * @see MapMouseEvent
     */
    public void mouseDragged( MapMouseEvent e ) { // do nothing
    }

    /**
     * This method may be overridden by subclasses
     * 
     * @see org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener#mouseMoved(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseMoved( MapMouseEvent e ) { // do nothing
    }   
    
    /**
     * This method may be overridden by subclasses
     * 
     * @see org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener#mouseHovered(MapMouseEvent)
     */
    public void mouseHovered( MapMouseEvent e ) { // do nothing
    }

    /**
     * This method may be overridden by subclasses
     * 
     * @see org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelListener#mouseWheelMoved(org.locationtech.udig.project.render.displayAdapter.MapMouseWheelEvent)
     * @see MapMouseEvent
     */
    public void mouseWheelMoved( MapMouseWheelEvent e ) { // do nothing
    }

    /**
     * This method may be overridden by subclasses
     * 
     * @see org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener#mouseDoubleClicked(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     * @see MapMouseEvent
     */
    public void mouseDoubleClicked( MapMouseEvent event ) { // do nothing
    }
}
