/**
 * <copyright></copyright> $Id$
 */
package net.refractions.udig.project.internal.render.impl;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.CompositeRenderContext;
import net.refractions.udig.project.internal.render.ExecutorVisitor;
import net.refractions.udig.project.internal.render.RenderContext;
import net.refractions.udig.project.internal.render.RenderExecutor;
import net.refractions.udig.project.internal.render.RenderFactory;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.internal.render.RendererCreator;
import net.refractions.udig.project.internal.render.SelectionLayer;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderManager;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.render.Tile;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.vividsolutions.jts.geom.Envelope;

/**
 * An unresponsive implementation of IRenderManager.  It will not rerender if the viewport model changes.  
 * In fact it does not register for any events.
 * 
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class RenderManagerImpl extends EObjectImpl implements RenderManager {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getRenderExecutor() <em>Render Executor</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getRenderExecutor()
     * @generated NOT
     * @ordered
     */
    protected RenderExecutor renderExecutor ;

    /**
     * The cached value of the '{@link #getRendererCreator() <em>Renderer Creator</em>}'
     * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getRendererCreator()
     * @generated NOT
     * @ordered
     */
    protected RendererCreator rendererCreator;

    /**
     * The default value of the '{@link #getMapDisplay() <em>Map Display</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMapDisplay()
     * @generated
     * @ordered
     */
    protected static final IMapDisplay MAP_DISPLAY_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getMapDisplay() <em>Map Display</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMapDisplay()
     * @generated
     * @ordered
     */
    protected IMapDisplay mapDisplay = MAP_DISPLAY_EDEFAULT;

    /**
     * The cached value of the '{@link #getMapInternal() <em>Map Internal</em>}' reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMapInternal()
     * @generated
     * @ordered
     */
    protected Map mapInternal = null;

    /**
     * The cached value of the '{@link #getViewportModelInternal() <em>Viewport Model Internal</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getViewportModelInternal()
     * @generated
     * @ordered
     */
    protected ViewportModel viewportModelInternal = null;

    /**
	 * Indicate if the RenderManager has been disposed.
	 */
	protected volatile boolean disposed = false;

	/**
	 * Indicates if the current render manager is a viewer or is the "real"
	 * render manager for the map
	 */
	boolean viewer = false;

    /** indicate whether rendering is permitted */
	protected volatile boolean renderingEnabled = true;

	
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected RenderManagerImpl() {
        super();

        rendererCreator=new RendererCreatorImpl();
        RenderContext context = new RenderContextImpl(){
            @Override
            public Map getMapInternal() {
                return RenderManagerImpl.this.getMapInternal();
            }
            @Override
            public Map getMap() {
                return RenderManagerImpl.this.getMapInternal();
            }
            @Override
            public RenderManager getRenderManagerInternal() {
                return RenderManagerImpl.this;
            }
            
            @Override
            public IRenderManager getRenderManager() {
                return RenderManagerImpl.this;
            }
        };
        rendererCreator.setContext(context);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected EClass eStaticClass() {
        return RenderPackage.eINSTANCE.getRenderManager();
    }

    /**
     * Prints the current renderstack.
     */
    public void printRenderStack() {
        checkState();
        if (getRenderExecutor() != null) {
            final StringBuffer msg = new StringBuffer();
            getRenderExecutor().visit(new ExecutorVisitor(){
                int i = 0;
                public void visit( RenderExecutor executor ) {
                    i++;
                    String layername = executor.getContext().getLayer().getID().toString();
                    if (executor.getContext().getLayer() instanceof SelectionLayer) {
                        layername += "<Selection>"; //$NON-NLS-1$
                    }
                    msg.append("\n" + i + " - " + layername); //$NON-NLS-1$ //$NON-NLS-2$

                }

                public void visit( RenderExecutorMultiLayer executor ) {
                    for( ILayer layer : executor.getContext().getLayers() ) {
                        i++;
                        String layername = layer.getID().toString();
                        if (layer instanceof SelectionLayer) {
                            layername += "<Selection>"; //$NON-NLS-1$
                        }
                        msg.append("\n" + i + " - " + layername); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }

                public void visit( RenderExecutorComposite executor ) {
                    for( RenderExecutor renderer : executor.getRenderer().getRenderExecutors() ) {
                        renderer.visit(this);
                    }
                }
            });
            ProjectPlugin.log(msg.toString(), null);
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Map getMapInternal() {
        checkState();
        if (mapInternal != null && mapInternal.eIsProxy()) {
            Map oldMapInternal = mapInternal;
            mapInternal = (Map) eResolveProxy((InternalEObject) mapInternal);
            if (mapInternal != oldMapInternal) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                            RenderPackage.RENDER_MANAGER__MAP_INTERNAL, oldMapInternal, mapInternal));
            }
        }
        return mapInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Map basicGetMapInternal() {
        return mapInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain basicSetMapInternal( Map newMapInternal, NotificationChain msgs ) {
        Map oldMapInternal = mapInternal;
        mapInternal = newMapInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    RenderPackage.RENDER_MANAGER__MAP_INTERNAL, oldMapInternal, newMapInternal);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setMapInternalGen( Map newMapInternal ) {
        checkState();
        if (newMapInternal != mapInternal) {
            NotificationChain msgs = null;
            if (mapInternal != null)
                msgs = ((InternalEObject) mapInternal).eInverseRemove(this,
                        ProjectPackage.MAP__RENDER_MANAGER_INTERNAL, Map.class, msgs);
            if (newMapInternal != null)
                msgs = ((InternalEObject) newMapInternal).eInverseAdd(this,
                        ProjectPackage.MAP__RENDER_MANAGER_INTERNAL, Map.class, msgs);
            msgs = basicSetMapInternal(newMapInternal, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.RENDER_MANAGER__MAP_INTERNAL, newMapInternal, newMapInternal));
    }

    /**
     * @return Returns the viewer.
     * @uml.property name="viewer"
     */
    public boolean isViewer() {
        checkState();
        return viewer;
    }

    /**
     * @param viewer The viewer to set.
     * @uml.property name="viewer"
     */
    public void setViewer( boolean viewer ) {
        checkState();
        this.viewer = viewer;
    }

    /**
     * @see net.refractions.udig.project.internal.render.RenderManager#setMap(IMap)
     * @uml.property name="mapInternal"
     */
    public void setMapInternal( Map newMap ) {
        checkState();
        if (isViewer()) {
            basicSetMapInternal(newMap, new NotificationChainImpl());
        } else
            setMapInternalGen(newMap);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public IMapDisplay getMapDisplay() {
        checkState();
        return mapDisplay;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setMapDisplay( IMapDisplay newMapDisplay ) {
        IMapDisplay oldMapDisplay = mapDisplay;
        mapDisplay = newMapDisplay;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.RENDER_MANAGER__MAP_DISPLAY, oldMapDisplay, mapDisplay));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public RendererCreator getRendererCreator() {
        checkState();
        return rendererCreator;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public ViewportModel getViewportModelInternal() {
        checkState();
        return viewportModelInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain basicSetViewportModelInternal( ViewportModel newViewportModelInternal,
            NotificationChain msgs ) {
        ViewportModel oldViewportModelInternal = viewportModelInternal;
        viewportModelInternal = newViewportModelInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL,
                    oldViewportModelInternal, newViewportModelInternal);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void setViewportModelInternal( ViewportModel newViewportModelInternal ) {
        if (newViewportModelInternal != viewportModelInternal) {
            NotificationChain msgs = null;
            if (viewportModelInternal != null)
                msgs = ((InternalEObject) viewportModelInternal).eInverseRemove(this,
                        RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL, ViewportModel.class,
                        msgs);
            if (newViewportModelInternal != null)
                msgs = ((InternalEObject) newViewportModelInternal).eInverseAdd(this,
                        RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL, ViewportModel.class,
                        msgs);
            msgs = basicSetViewportModelInternal(newViewportModelInternal, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL,
                    newViewportModelInternal, newViewportModelInternal));
    }

    /**
     * @throws IOException
     * @see net.refractions.udig.project.internal.render.RenderManager#getInfo(java.awt.Point)
     *      public List getInfo(Point screenLocation) throws IOException { return
     *      getRenderExecutor().getInfo(screenLocation); }
     */

    /**
     * @throws IOException
     * @see net.refractions.udig.project.internal.render.RenderManager#createInfo(java.awt.Point)
     *      public List getInfo(Point screenLocation) throws IOException { return
     *      getRenderExecutor().getInfo(screenLocation); }
     * @uml.property name="renderExecutor"
     * @generated NOT
     */
    public RenderExecutor getRenderExecutor() {
        checkState();
        if (renderExecutor == null)
            setRenderExecutor(RenderFactory.eINSTANCE.createRenderExecutor());

        return renderExecutor;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setRenderExecutor( RenderExecutor newRenderExecutor ) {
        RenderExecutor oldRenderExecutor = renderExecutor;
        renderExecutor = newRenderExecutor;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.RENDER_MANAGER__RENDER_EXECUTOR, oldRenderExecutor,
                    renderExecutor));
    }

    /**
     * If subclasses override this method make sure that super.dispose() is called <b>AT THE END<b>
     * of the overridding method.
     * 
     * @generated NOT
     */
    public void dispose() {
        checkState();
        if (renderExecutor != null) {
            ((RenderContextImpl) renderExecutor.getContext()).dispose();
            getRenderExecutor().dispose();
        }
        
        mapDisplay=null;
        
        renderExecutor = null;

        getMapInternal().setRenderManagerInternal(null);

        disposed = true;

        // Runtime.getRuntime().gc();
    }

    public void checkState() throws IllegalStateException {
        if (disposed)
            throw new IllegalStateException("RenderManager is disposed"); //$NON-NLS-1$
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain eInverseAdd( InternalEObject otherEnd, int featureID, Class baseClass,
            NotificationChain msgs ) {
        if (featureID >= 0) {
            switch( eDerivedStructuralFeatureID(featureID, baseClass) ) {
            case RenderPackage.RENDER_MANAGER__MAP_INTERNAL:
                if (mapInternal != null)
                    msgs = ((InternalEObject) mapInternal).eInverseRemove(this,
                            ProjectPackage.MAP__RENDER_MANAGER_INTERNAL, Map.class, msgs);
                return basicSetMapInternal((Map) otherEnd, msgs);
            case RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL:
                if (viewportModelInternal != null)
                    msgs = ((InternalEObject) viewportModelInternal).eInverseRemove(this,
                            RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL,
                            ViewportModel.class, msgs);
                return basicSetViewportModelInternal((ViewportModel) otherEnd, msgs);
            default:
                return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain eInverseRemove( InternalEObject otherEnd, int featureID,
            Class baseClass, NotificationChain msgs ) {
        if (featureID >= 0) {
            switch( eDerivedStructuralFeatureID(featureID, baseClass) ) {
            case RenderPackage.RENDER_MANAGER__MAP_INTERNAL:
                return basicSetMapInternal(null, msgs);
            case RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL:
                return basicSetViewportModelInternal(null, msgs);
            default:
                return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Object eGet( EStructuralFeature eFeature, boolean resolve ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case RenderPackage.RENDER_MANAGER__RENDER_EXECUTOR:
            return getRenderExecutor();
        case RenderPackage.RENDER_MANAGER__MAP_DISPLAY:
            return getMapDisplay();
        case RenderPackage.RENDER_MANAGER__MAP_INTERNAL:
            if (resolve)
                return getMapInternal();
            return basicGetMapInternal();
        case RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL:
            return getViewportModelInternal();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void eSet( EStructuralFeature eFeature, Object newValue ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case RenderPackage.RENDER_MANAGER__RENDER_EXECUTOR:
            setRenderExecutor((RenderExecutor) newValue);
            return;
        case RenderPackage.RENDER_MANAGER__MAP_DISPLAY:
            setMapDisplay((IMapDisplay) newValue);
            return;
        case RenderPackage.RENDER_MANAGER__MAP_INTERNAL:
            setMapInternal((Map) newValue);
            return;
        case RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL:
            setViewportModelInternal((ViewportModel) newValue);
            return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void eUnset( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case RenderPackage.RENDER_MANAGER__RENDER_EXECUTOR:
            setRenderExecutor((RenderExecutor) null);
            return;
        case RenderPackage.RENDER_MANAGER__MAP_DISPLAY:
            setMapDisplay(MAP_DISPLAY_EDEFAULT);
            return;
        case RenderPackage.RENDER_MANAGER__MAP_INTERNAL:
            setMapInternal((Map) null);
            return;
        case RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL:
            setViewportModelInternal((ViewportModel) null);
            return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case RenderPackage.RENDER_MANAGER__RENDER_EXECUTOR:
            return renderExecutor != null;
        case RenderPackage.RENDER_MANAGER__MAP_DISPLAY:
            return MAP_DISPLAY_EDEFAULT == null ? mapDisplay != null : !MAP_DISPLAY_EDEFAULT
                    .equals(mapDisplay);
        case RenderPackage.RENDER_MANAGER__MAP_INTERNAL:
            return mapInternal != null;
        case RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL:
            return viewportModelInternal != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String toString() {
        checkState();
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (mapDisplay: "); //$NON-NLS-1$
        result.append(mapDisplay);
        result.append(')');
        return result.toString();
    }

    /**
     * @see net.refractions.udig.project.internal.render.RenderManager#refresh(net.refractions.udig.project.Layer,
     *      com.vividsolutions.jts.geom.Envelope)
     */
    public void refresh( ILayer layer, Envelope bounds ) {
        throw new UnsupportedOperationException();

    }

    /**
     * @see net.refractions.udig.project.internal.render.RenderManager#refresh(com.vividsolutions.jts.geom.Envelope)
     */
    public void refresh( Envelope bounds ) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see net.refractions.udig.project.internal.render.RenderManager#refreshImage()
     */
    public void refreshImage() {
        throw new UnsupportedOperationException();
    }
    
    
    /**
     * @see net.refractions.udig.project.internal.render.RenderManager#refreshSelection(com.vividsolutions.jts.geom.Envelope)
     */
    public void refreshSelection( ILayer layer, Envelope bounds ) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see net.refractions.udig.project.render.IRenderManager#getMap()
     */
    public IMap getMap() {
        checkState();
        return getMapInternal();
    }

    /**
     * @see net.refractions.udig.project.render.IRenderManager#getRenderers()
     */
    public List<IRenderer> getRenderers() {
        checkState();
        final List<IRenderer> renderers = new ArrayList<IRenderer>();
        getRenderExecutor().visit(new ExecutorVisitor(){

            public void visit( RenderExecutor executor ) {
                if ( !(executor.getContext().getLayer() instanceof SelectionLayer) ){
                    Renderer renderer = executor.getRenderer();
                    renderers.add(renderer);
                }
            }

            public void visit( RenderExecutorMultiLayer executor ) {
                renderers.add(executor.getRenderer());
            }

            public void visit( RenderExecutorComposite executor ) {
                for( RenderExecutor currentExecutor : executor.getRenderer().getRenderExecutors() )
                    currentExecutor.visit(this);
            }

        });
        
        Collections.sort(renderers, new Comparator<IRenderer>(){

            public int compare( IRenderer arg0, IRenderer arg1 ) {
                return arg0.getContext().getLayer().compareTo(arg1.getContext().getLayer());
            }

        });
        return renderers;
    }

    public void stopRendering() {
        checkState();
        if (getRenderExecutor() != null)
            getRenderExecutor().stopRendering();
    }

    public boolean isDisposed() {
        return disposed;
    }

    public RenderedImage getImage() {
        if( renderExecutor==null || renderExecutor.getState()==IRenderer.NEVER || getRenderExecutor().getContext()==null )
            return null;
//        try {
//            ((CompositeRendererImpl)getRenderExecutor()).refreshImage();
//        } catch (RenderException e) {
//            throw (RuntimeException) new RuntimeException( e );
//        }
        return getRenderExecutor().getContext().getImage();
    }

	public void clearSelection(ILayer layer) {
		throw new UnsupportedOperationException();
	}

	public void disableRendering() {
		renderingEnabled  = false;
	}

	public void enableRendering() {
		renderingEnabled = true;
	}
	
	public boolean isRenderingEnabled() {
		return renderingEnabled;
	}
	
	
	 /**
     * 
     * Returns true if these two layers are related in any way.  Two layers are related if:
     * <ul>
     *   <li>They are the same (layer = contained)
     *   <li>Layer is a part of a composite context and contained is also part of that context.</li>
     * </ul>
     *
     * @param layer
     * @param contained
     * 
     * @returns true if the two layers are part of the same context
     */
    public boolean areLayersRelatedByContext(ILayer layer, ILayer contained){
        //they are the same; so they are related
        if (layer == contained) return true;

        //for each renderer check the contexts
        for( Iterator<IRenderer> iterator = getRenderers().iterator(); iterator.hasNext(); ) {
            IRenderContext context = ((IRenderer) iterator.next()).getContext();
            if (context instanceof CompositeRenderContext){
                if (((CompositeRenderContext) context).getLayers().contains(layer)){
                    if( ((CompositeRenderContext) context).getLayers().contains(contained)){
                        //composite context and contains layer + contained
                        //these layers are related through a composite context
                        return true;
                    }else{
                        //composite context that contains layer but not contained
                        //cannot be related
                        return false;
                    }
                }
            }else{
                if (context.getLayer() == layer){
                    //layer is not part of a composite context and cannot be related in 
                    //any way to contained
                    return false;
                }
            }
        }
        return false;
    }
	public Collection<ReferencedEnvelope> computeTileBounds(
			ReferencedEnvelope viewBounds, double worldunitsperpixel) {
		return null;
	}

	public java.util.Map<ReferencedEnvelope, Tile> getTiles(
			Collection<ReferencedEnvelope> bounds) {
		return null;
	}
} // RenderManagerImpl
