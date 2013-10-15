/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.style;

import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.internal.Layer;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

/**
 * 
 * Configures a style object. 
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Creating a ui to allow user configuration of a style object.
 * <li>Placing style object information onto the style blackboard of a layer.
 * <li>Determining if a style configurator can be used to configure the style
 * of a particular layer.
 * </ul>
 * </p>
 * <p>
 * Style objects are stored a StyleBlackboard. Configurators use the blackboard 
 * to collaborate. Objects are stored on the blackboard by id. When a configurator 
 * queries the blackboard for an object and it does not exist, a default object 
 * should be created and placed on the blackboard. The following is an example:
 * <code>
 *      ...
 *      StyleBlackboard styleBlackboard = getStyleBlackboard();
 *      Point style = styleBlackboard.lookup("point.style");
 *      
 *      if (style == null) {
 *          style = new Point();    
 *          style.setX(0);
 *          style.setY(0);
 *          styleBlackboard.put("point.style", style);
 *      }
 *      ...
 * </code>
 * </p>
 * <p>
 * Each Layer has a StyleBlackboard. Configurators should not write to this
 * blackboard directly. Each configurator is supplied with a copy of the actual
 * layer blackboard.
 * </p>
 * <p>
 * <b>Note:</b><i>Each time a style object is changed, it must be replaced onto
 * the blackboard for persistance reasons.</i>
 * <code>
 *      StyleBlackboard styleBlackboard = getStyleBlackboard();
 *      Point style = styleBlackboard.lookup("point.style");
 *      ...
 *      style.setX(10);
 *      style.setY(10);
 *      
 *      styleBlackboard.put("point.style", style);
 * </code>
 * </p>
 * <p>
 * The StyleConfigurator should store no state. All state should be stored in
 * the style objects on the style blackboard. When a ui widget changes state, the
 * style object should be written to immediately to reflect the change.
 * When the configurator becomes active, the ui widgets should be initialized
 * from the values of style objects on the blackboard. This should be performed
 * every time refresh() is called. 
 * </p>
 * <p>
 * Whenever style objects are read from the blackboard, 
 * </p>
  
 * <p>
 * <code>
 *  void apply() {
 *      StyleBlackboard styleBlackboard = getStyleBlackboard();
 *      Point style = styleBlackboard.lookup("point.style");
 *      
 *      if (style == null) {
 *          style = new Point();    
 *          styleBlackboard.put("point.style", style);
 *      }
 *      
 *      style.setX(...) //set to some value from ui
 *      style.setY(...) //set to some value from ui
 *  }
 *  
 *  void init() {
 *      StyleBlackboard styleBlackboard = getStyleBlackboard();
 *      Point style = styleBlackboard.lookup("point.style");
 *      if (style != null) {
 *          //set some ui widget to value of style.getX();
 *          //set some ui widget to value of style.getY();
 *      }
 *  }
 * </code>
 * </p>
 * <p>
 * A StyleConfigurator is not considered active until its ui has been created.
 *
 * </p>
 * @author Justin Deoliveira
 * @since 0.6.0
 * 
 */
public abstract class IStyleConfigurator {
    
    /** extension point id **/
    public static final String XPID = "net.refractions.udig.style.styleConfigurator"; //$NON-NLS-1$
    private String styleId;
    private String label;
    private IViewSite site;
    private Layer layer;
    private IAction applyAction;

    
    /**
     * Sets the apply action. 
     * 
     * @param applyAction1
     */
    public final void setAction(IAction applyAction1) {
        this.applyAction = applyAction1;
    }
    
    /**
     * Runs the apply action.
     */
    protected void makeActionDoStuff() {
        if(this.applyAction != null) {
            this.applyAction.run();
        }
    }

    protected IAction getApplyAction(){
        return applyAction;
    }
    /**
     * Called after apply action has been triggeredbefore apply is executed.
     */
    public void preApply(){
        //
    }
    
    /**
     * Returns the declared style id of the style the configurator depends on.
     * <p>
     * This is provided by SetStyleId by the extention point.
     * <p>
     * <p>
     * When keeping information associated with a IStyleConfigurator
     * (in a Map, or Memento) use this as a KEY. Don't use label,
     * two IStyleConfigurator may have the same label.
     * </p>
     * @return styleId The style id.
     */
    public final String getStyleId(){
        return styleId;
    }

    /**
     * Sets the declared style id of the style the configurator depends on.
     * <p>
     * Called by the extention point processor.
     * </p>
     * @param id The style id.
     */
    public final void setStyleId(String id){
        this.styleId = id;
    }

    /**
     * Returns the label describing the configurator. Used mainly for ui purposes.
     * 
     * @return A short description of the configurator.
     */
    public final String getLabel(){
        return label;
    }
    
    /**
     * Sets the label describing the configurator. Used mainly for ui purposes.
     * 
     * @param label A short description of the configurator.
     */
    public void setLabel(String label){
        this.label = label;
    }
    
    /**
     * Returns the site for this view. 
     * This method is equivalent to <code>(IViewSite) getSite()</code>.
     * <p>  
     * The site can be <code>null</code> while the view is being initialized. 
     * After the initialization is complete, this value must be non-<code>null</code>
     * for the remainder of the view's life cycle.
     * </p>
     * 
     * @return the view site; this value may be <code>null</code> if the view
     *         has not yet been initialized
     */
    public IViewSite getViewSite(){
        return site;
    }

    /**
     * Initializes this view with the given view site. 
     * <p>
     * This method is automatically shortly after the part is instantiated.
     * It marks the start of the views's lifecycle.  Clients must not call this 
     * method.
     * </p>
     *
     * @param viewSite the view site
     * @throws PartInitException 
     */
    public void init(IViewSite viewSite) throws PartInitException{
        this.site = viewSite;
        init();
    }
    /**
     * Initialize this style configurator.
     * <p>
     * You must call super.init();
     * </p>
     * @throws PartInitException 
     */
    protected void init() throws PartInitException {
        // subclass can override
    }
    
    /**
     * Determines if the configurator can be used to configure the style
     * for a specified layer.
     * 
     * @param aLayer The layer to be styled.
     * @return true if the configurator can work with the layer, otherwise false.
     */
    public abstract boolean canStyle(Layer aLayer);

    /**
     * Gets the current layer to which the current style being configured is
     * to be applied to.
     * <p>
     * The layer can be <code>null</code> while the view is being initialized or created. 
     * This value must be non-<code>null</code> when the IStyleConfigurator.getControl()
     * is visiable.
     * </p> 
     * @return Layer being edited at the moment
     */
    public final Layer getLayer(){
        return layer;
    }
     
    
    /**
     * Returns the style blackboard that the configurator is populating with 
     * style information.
     * <p>
     * <p>
     * The blackboard can be <code>null</code> while the view is being initialized or created. 
     * This value must be non-<code>null</code> when the IStyleConfigurator.getControl()
     * is visiable.
     * </p>
     * @return A style blackboard.
     */
    public final IBlackboard getStyleBlackboard(){
        //return layer.style();
        if (layer == null) {
            return null;
        }
    	return layer.getStyleBlackboard();
    }
    
    /**
     * Sets the layer (and thus changes the style blackboard) being targetted.
     * <p>
     * When this method is called  the user interface state should be re-initialised against
     * the new layer and blackboard.
     * <p>
     * When using this method to change between layers:
     * <ul>
     * <li>getLayer() is updated
     * <li>getBlackboard() is updated (actually calls layer.getStyleBlackboard()
     * <li>refresh() is called to update the user interface
     * </p> 
     * 
     * @param targetLayer The layer being styled.
     */
    public void focus(Layer targetLayer){
        this.layer = targetLayer;
        refresh();        
    }
    
    /** 
     * Called when new layer and blackbard values are available.
     * <p>
     * This provides update information as a callback (rather than an
     * event listener).
     * </p>
     * This should only be called after create part control has had a chance to
     * be called.
     */
    protected abstract void refresh(); 
     
    /**
     * Creates the control that is to be used to configure the style.
     * <p>
     * This method uses a template pattern to get the subclass to create
     * the control. This method will not be called until after init and
     * setViewPart. The parent container (composite) passed in is for the 
     * explicit use of the configurator, this method must set a layout for
     * the container. 
     * </p>  
     * <p>
     * You can set the layout to the parent to be whatever you want.
     * </p>
     * @param parent 
     */
    public abstract void createControl( Composite parent );
    
    /**
     * Cleans up any resources (like icons) held by this StyleConfigurator.
     * <p>
     * You should not assume that create, or even init has been called.
     * You must call super.dispose();
     * </p>
     */
    public void dispose(){
        // subclass should override
    }

}