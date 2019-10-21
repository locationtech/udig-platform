/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.render.impl;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.MessageFormat;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.Trace;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * Abstract class for Renderers to extend.
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public abstract class RendererImpl extends EObjectImpl implements Renderer {
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
     * The cached value of the '{@link #getState() <em>State</em>}' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see #getState()
     * @generated
     * @ordered
     */
    protected int state = STATE_EDEFAULT;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
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
     * @generated
     */
    protected RendererImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RenderPackage.Literals.RENDERER;
    }

    /**
     * @see org.locationtech.udig.project.internal.render.Renderer#getName()
     * @uml.property name="name"
     */
    public String getName() {
        if (name == null) {
            ILayer layer = getContext().getLayer();
            if (layer == null)
                return ""; //$NON-NLS-1$
            if (layer instanceof SelectionLayer)
                return MessageFormat.format(Messages.RendererImpl_selectionFor,
                        new Object[] { layer.getName() });
            else
                return layer.getName();
        } else
            return name;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RenderPackage.RENDERER__NAME,
                    oldName, name));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public int getState() {
        return state;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setState(int newState) {
        int oldState = state;
        state = newState;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RenderPackage.RENDERER__STATE,
                    oldState, state));
    }

    public IRenderContext getContext() {
        return context;
    }

    public void setContext(IRenderContext newContext) {
        ProjectPlugin.trace(Trace.RENDER, getClass(),
                "RenderContext changed. \nOld:" + context + "\nNew:" + newContext, null); //$NON-NLS-1$ //$NON-NLS-2$
        context = newContext;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @throws RenderException
     * @generated NOT
     */
    public abstract void render(Graphics2D destination, IProgressMonitor monitor)
            throws RenderException;

    public abstract void render(IProgressMonitor monitor) throws RenderException;

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
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case RenderPackage.RENDERER__STATE:
            return getState();
        case RenderPackage.RENDERER__NAME:
            return getName();
        case RenderPackage.RENDERER__CONTEXT:
            return getContext();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case RenderPackage.RENDERER__STATE:
            setState((Integer) newValue);
            return;
        case RenderPackage.RENDERER__NAME:
            setName((String) newValue);
            return;
        case RenderPackage.RENDERER__CONTEXT:
            setContext((IRenderContext) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
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
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case RenderPackage.RENDERER__STATE:
            return state != STATE_EDEFAULT;
        case RenderPackage.RENDERER__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case RenderPackage.RENDERER__CONTEXT:
            return context != null;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
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
    public synchronized void setRenderBounds(Envelope boundsToRender) {
        if (boundsToRender == null) {
            renderbounds = null;
        } else if (boundsToRender instanceof ReferencedEnvelope) {
            ReferencedEnvelope referencedEnvelope = (ReferencedEnvelope) boundsToRender;

            if (referencedEnvelope.getCoordinateReferenceSystem() == null) {
                throw new IllegalArgumentException(
                        "The provided referenced envelope does not have a CRS, did you mean getContext().getCRS()?"); //$NON-NLS-1$
            }
            renderbounds = referencedEnvelope;
        } else {
            CoordinateReferenceSystem crs = getContext().getCRS();
            if (crs == null) {
                throw new IllegalArgumentException(
                        "We cannot determine the CRS for the provided envelope, please supply a ReferencedEnvelope"); //$NON-NLS-1$
            }
            // Assume the Map CRS will do
            ReferencedEnvelope referencedEnvelope = new ReferencedEnvelope(boundsToRender, crs);
            renderbounds = referencedEnvelope;
        }
    }

    /**
     * Set the bounds to the indicated screenArea (by using the pixelToWorld method to produced a ReferencedEnvelope.
     */
    public synchronized void setRenderBounds(Rectangle screenArea) {
        Coordinate min = getContext().pixelToWorld(screenArea.x, screenArea.y);
        Coordinate max = getContext().pixelToWorld(screenArea.width + screenArea.x,
                screenArea.height + screenArea.y);
        ReferencedEnvelope worldArea = new ReferencedEnvelope(min.x, max.x, min.y, max.y,
                getContext().getCRS());
        setRenderBounds(worldArea);
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
