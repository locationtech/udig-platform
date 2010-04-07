/**
 * <copyright></copyright> $Id$
 */
package net.refractions.udig.project.internal.render.impl;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.MessageFormat;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.Trace;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.internal.render.SelectionLayer;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Abstract class for Renderers to extend.
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public abstract class RendererImpl extends EObjectImpl implements Renderer {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getState() <em>State</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getState()
     * @generated NOT
     * @ordered
     */
    protected static final int STATE_EDEFAULT = NEVER;

    /**
     * The cached value of the '{@link #getState() <em>State</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getState()
     * @generated
     * @ordered
     */
    protected int state = STATE_EDEFAULT;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The cached value of the '{@link #getContext() <em>Context</em>}' reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getContext()
     * @generated NOT
     * @ordered
     */
    protected volatile IRenderContext context = null;

    /**
     * Seems to be a cache of the current bounds to renderer.
     */
    private volatile ReferencedEnvelope renderbounds;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected RendererImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected EClass eStaticClass() {
        return RenderPackage.eINSTANCE.getRenderer();
    }

    /**
     * @see net.refractions.udig.project.internal.render.Renderer#getName()
     * @uml.property name="name"
     */
    public String getName() {
        if (name == null) {
            ILayer layer = getContext().getLayer();
            if( layer==null )
                return ""; //$NON-NLS-1$
            if (layer instanceof SelectionLayer)
                return MessageFormat
                        .format(
                                Messages.RendererImpl_selectionFor, new Object[]{layer.getName()}); 
            else
                return layer.getName();
        } else
            return name;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setName( String newName ) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RenderPackage.RENDERER__NAME,
                    oldName, name));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public int getState() {
        return state;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setState( int newState ) {
        int oldState = state;
        state = newState;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, RenderPackage.RENDERER__STATE,
                    oldState, state));
        }

    }

    public IRenderContext getContext() {
        return context;
    }

    public  void setContext(IRenderContext newContext) {
    	ProjectPlugin.trace(Trace.RENDER, getClass(), "RenderContext changed. \nOld:"+context+"\nNew:"+newContext, null); //$NON-NLS-1$ //$NON-NLS-2$
        context = newContext;
    }
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @throws RenderException
     * @generated NOT
     */
    public abstract void render( Graphics2D destination, IProgressMonitor monitor )
            throws RenderException;

    public abstract void render(IProgressMonitor monitor ) throws RenderException;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated public InfoList getInfo(Point screenLocation) { // TODO: implement this method //
     *            Ensure that you remove
     * @generated or mark it
     * @generated NOT throw new UnsupportedOperationException(); }
     */

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void dispose() {
        // do nothing
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Object eGet( EStructuralFeature eFeature, boolean resolve ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case RenderPackage.RENDERER__STATE:
            return new Integer(getState());
        case RenderPackage.RENDERER__NAME:
            return getName();
        case RenderPackage.RENDERER__CONTEXT:
            return getContext();
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
        case RenderPackage.RENDERER__STATE:
            setState(((Integer) newValue).intValue());
            return;
        case RenderPackage.RENDERER__NAME:
            setName((String) newValue);
            return;
        case RenderPackage.RENDERER__CONTEXT:
            setContext((IRenderContext) newValue);
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
        case RenderPackage.RENDERER__STATE:
            setState(STATE_EDEFAULT);
            return;
        case RenderPackage.RENDERER__NAME:
            setName(NAME_EDEFAULT);
            return;
        case RenderPackage.RENDERER__CONTEXT:
            setContext((IRenderContext) null);
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
        case RenderPackage.RENDERER__STATE:
            return state != STATE_EDEFAULT;
        case RenderPackage.RENDERER__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case RenderPackage.RENDERER__CONTEXT:
            return context != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (state: "); //$NON-NLS-1$
        result.append(state);
        result.append(", name: "); //$NON-NLS-1$
        result.append(name);
        result.append(')');
        return result.toString();
    }

    /**
     * Default renderer may be cached.
     */
    public boolean isCacheable() {
    	return true;
    }
    
    /**
     * Set the bounds you wish to have drawn.
     * <p>
     * If a ReferencedEnvelope is provided it would be best; if not the context CoordinateReferenceSystem (ie world crs) will be assumed.
     * </p>
     */
    public synchronized void setRenderBounds( Envelope boundsToRender ) {
        if( boundsToRender == null ){
        	renderbounds = null;
        }
        else if( boundsToRender instanceof ReferencedEnvelope){
        	ReferencedEnvelope referencedEnvelope = (ReferencedEnvelope) boundsToRender;
        	
        	if( referencedEnvelope.getCoordinateReferenceSystem() == null ){
        		throw new IllegalArgumentException("The provided referenced envelope does not have a CRS, did you mean getContext().getCRS()?"); //$NON-NLS-1$
        	}
        	renderbounds = referencedEnvelope;
        } else {
            CoordinateReferenceSystem crs = getContext().getCRS();
            if( crs == null ){
            	throw new IllegalArgumentException("We cannot determine the CRS for the provided envelope, please supply a ReferencedEnvelope"); //$NON-NLS-1$
            }
            // Assume the Map CRS will do
        	ReferencedEnvelope referencedEnvelope = new ReferencedEnvelope(boundsToRender, crs);        	
        	renderbounds = referencedEnvelope;
        }
    }
    /**
     * Set the bounds to the indicated screenArea (by using the pixelToWorld method to produced a ReferencedEnvelope.
     */
    public synchronized void setRenderBounds( Rectangle screenArea ) {
        Coordinate min = getContext().pixelToWorld(screenArea.x, screenArea.y);
        Coordinate max = getContext().pixelToWorld(screenArea.width + screenArea.x,
                screenArea.height + screenArea.y);
        ReferencedEnvelope worldArea = new ReferencedEnvelope( min.x, max.x, min.y,max.y, getContext().getCRS() );
        setRenderBounds( worldArea );
    }
    /**
     * The area of the world that we wish to draw.
     * <p>
     * This is a RefernecedEnvelope and may (or may not) exactly match the data CRS you are working with. To be sure you
     * should always transform this bounds into your data crs.
     * @return area of the world to draw
     */
    public synchronized ReferencedEnvelope getRenderBounds() {
        return renderbounds;
    }
    
} // RendererImpl
