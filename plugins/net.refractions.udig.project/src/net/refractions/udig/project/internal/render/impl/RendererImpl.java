/**
 * <copyright></copyright> $Id: RendererImpl.java 30936 2008-10-29 12:21:56Z jeichar $
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
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;

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

    private volatile ReferencedEnvelope renderbounds2;

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
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RenderPackage.RENDERER__STATE,
                    oldState, state));
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

    public synchronized void setRenderBounds( Envelope boundsToRender ) {

        if( boundsToRender!=null && !(boundsToRender instanceof ReferencedEnvelope) ){
            renderbounds2 = new ReferencedEnvelope(boundsToRender, getContext().getCRS());
        } else {
            renderbounds2 = (ReferencedEnvelope) boundsToRender;
        }
    }

    public synchronized void setRenderBounds( Rectangle screenArea ) {

        Coordinate min = getContext().pixelToWorld(screenArea.x, screenArea.y);
        Coordinate max = getContext().pixelToWorld(screenArea.width + screenArea.x,
                screenArea.height + screenArea.y);
        setRenderBounds(new Envelope(min, max));
    }
    public synchronized ReferencedEnvelope getRenderBounds() {
        return renderbounds2;
    }

} // RendererImpl
