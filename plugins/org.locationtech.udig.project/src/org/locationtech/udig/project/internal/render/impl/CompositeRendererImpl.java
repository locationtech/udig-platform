/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.MultiLayerRenderer;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RenderFactory;
import org.locationtech.udig.project.internal.render.RenderListenerAdapter;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.RendererCreator;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.ILabelPainter;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.RenderException;

/**
 * <ul>
 * <li>Combines the output from several renderer into a single image.</li>
 * <li>Listens to its context (CompositeContext) and creates new RenderExecutors when required.</li>
 * </ul>
 *
 * @author Jesse
 * @since 1.0.0
 */
public class CompositeRendererImpl extends RendererImpl implements MultiLayerRenderer {

    private static final Comparator<? super RenderExecutor> comparator = new Comparator<RenderExecutor>() {

        @Override
        public int compare(RenderExecutor e1, RenderExecutor e2) {

            return e1.getContext().getLayer().compareTo(e2.getContext().getLayer());

        }
    };

    static final AffineTransform IDENTITY = new AffineTransform();

    CompositeContextListener contextListener = new CompositeContextListener() {

        private void add(List<RenderContext> contexts) {
            List<RenderExecutor> renderers = new ArrayList<>();
            for (RenderContext context : contexts) {
                if (findExecutor(context) == null) {
                    Renderer renderer = getRendererCreator(context).getRenderer(context);
                    renderers.add(createRenderExecutor(renderer));
                }
            }
            if (!renderers.isEmpty()) {
                getRenderExecutors().addAll(renderers);
            }
        }

        @Override
        public void notifyChanged(CompositeRenderContext context, List<RenderContext> contexts,
                boolean added) {
            if (added) {
                add(contexts);
            } else {
                remove(contexts);
            }
        }

        private void remove(List<RenderContext> contexts) {
            for (RenderContext context : contexts) {
                RenderExecutor executor = findExecutor(context);
                if (executor != null) {
                    getRenderExecutors().remove(executor);
                    executor.dispose();
                }
            }
        }
    };

    RendererCreator rendererCreator = null;

    private final Set<RenderExecutor> renderExecutors;

    {
        renderExecutors = new CopyOnWriteArraySet<>();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    protected CompositeRendererImpl() {
        super();
    }

    /**
     * Returns a list that is a copy of all the children render.
     *
     * @see org.locationtech.udig.project.internal.render.MultiLayerRenderer#children()
     */
    public List<Renderer> children() {
        List<Renderer> children = new ArrayList<>();
        for (RenderExecutor victim : getRenderExecutors()) {
            children.add(victim.getRenderer());
        }
        return children;
    }

    /**
     * @param renderer
     */
    protected RenderExecutor createRenderExecutor(Renderer renderer) {
        final RenderExecutor executor = RenderFactory.eINSTANCE.createRenderExecutor(renderer);
        executor.eAdapters().add(new RenderListenerAdapter() {

            @Override
            protected void renderDisposed(Notification msg) {
                EObject obj = (EObject) getTarget();
                obj.eAdapters().remove(this);
            }

            @Override
            protected void renderDone() {
                setState(DONE);
            }

            @Override
            protected void renderRequest() {
                setRenderBounds(executor.getRenderBounds());
                setState(RENDER_REQUEST);
            }

            @Override
            protected void renderStarting() {
                // setState(STARTING);
            }

            @Override
            protected void renderUpdate() {
                synchronized (CompositeRendererImpl.this) {
                    setState(RENDERING);
                }
            }
        });
        return executor;
    }

    @Override
    public synchronized void dispose() {
        for (RenderExecutor renderer : getRenderExecutors()) {
            renderer.dispose();
        }
        getRenderExecutors().clear();
    }

    /**
     * Called to remove the corresponding RenderExecutor from the list of RenderExecutors.
     *
     * @param renderer the renderer that has been removed
     */
    protected RenderExecutor findExecutor(IRenderContext context) {
        for (Iterator<RenderExecutor> eIter = getRenderExecutors().iterator(); eIter.hasNext();) {
            RenderExecutor executor = eIter.next();
            if (executor.getRenderer().getContext().equals(context))
                return executor;
        }
        return null;
    }

    @Override
    public CompositeRenderContext getContext() {
        return (CompositeRenderContext) super.getContext();
    }

    /**
     * @see org.locationtech.udig.project.render.IMultiLayerRenderer#getIChildren()
     */
    public List getIChildren() {
        return children();
    }

    public IRenderContext getIContext() {
        return getContext();
    }

    RendererCreator getRendererCreator(IRenderContext context) {
        rendererCreator = ((RenderManager) context.getRenderManager()).getRendererCreator();
        rendererCreator.setContext((RenderContext) context);
        return rendererCreator;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @uml.property name="renderExecutors"
     * @generated NOT
     */
    public Collection<RenderExecutor> getRenderExecutors() {
        return renderExecutors;
    }

    private boolean isFullAlphaUsed(RenderExecutor executor) {

        Object object = getContext().getMap().getBlackboard().get("MYLAR"); //$NON-NLS-1$

        if (object == null || !((Boolean) object).booleanValue())
            return true;

        if (executor.getContext() instanceof CompositeRenderContext) {
            CompositeRenderContext context = (CompositeRenderContext) executor.getContext();
            if (context.getLayers().contains(getContext().getSelectedLayer()))
                return true;

            return false;
        }

        if (executor.getContext().getLayer() == getContext().getSelectedLayer())
            return true;

        return false;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @throws RenderException
     * @generated NOT
     */
    @Override
    public void refreshImage() throws RenderException {
        refreshImage(true);
    }

    /**
     * Vitalus: Refreshes map image from buffered images of renderers with or without labels cache
     * painting.
     *
     * @param paintLabels
     */
    void refreshImage(boolean paintLabels) throws RenderException {
        if (getContext().getMapDisplay() == null) {
            // we've been disposed lets bail
            return;
        }
        synchronized (getContext()) {
            Graphics2D g = null;
            try {
                BufferedImage current = getContext().getImage();

                // create a copy of the image to draw into

                BufferedImage copy = new BufferedImage(current.getWidth(), current.getHeight(),
                        current.getType());

                g = (Graphics2D) copy.getGraphics();

                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                g.setRenderingHint(RenderingHints.KEY_DITHERING,
                        RenderingHints.VALUE_DITHER_DISABLE);
                g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
                g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                        RenderingHints.VALUE_COLOR_RENDER_SPEED);
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                        RenderingHints.VALUE_STROKE_PURE);
                g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                        RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_OFF);

                IMap map = getContext().getMap();
                Object object = map.getBlackboard()
                        .get(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR);
                if (object == null) {
                    IPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
                    RGB background = PreferenceConverter.getColor(store,
                            PreferenceConstants.P_BACKGROUND);
                    map.getBlackboard().put(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR,
                            new Color(background.red, background.green, background.blue));
                    object = map.getBlackboard()
                            .get(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR);
                }
                g.setBackground((Color) object);
                g.clearRect(0, 0, copy.getWidth(), copy.getHeight());

                SortedSet<RenderExecutor> executors;
                synchronized (renderExecutors) {
                    executors = new TreeSet<>(comparator);
                    executors.addAll(getRenderExecutors());
                }
                ILabelPainter cache = getContext().getLabelPainter();

                RENDERERS: for (RenderExecutor executor : executors) {

                    if (!executor.getContext().isVisible()) {
                        if (paintLabels
                                && !(executor.getContext().getLayer() instanceof SelectionLayer)) {
                            // disable layer from label cache
                            cache.disableLayer(executor.getContext().getLayer().getID().toString());
                        }
                        continue RENDERERS;
                    }

                    if (executor.getState() == NEVER || executor.getState() == STARTING
                            || executor.getState() == RENDER_REQUEST) {
                        continue RENDERERS;
                    }
                    if (isFullAlphaUsed(executor)) {
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    } else {
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    }
                    g.drawRenderedImage(executor.getContext().getImage(), IDENTITY);
                }
                if (paintLabels) {
                    RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHints(hints);
                    Dimension displaySize = getContext().getMapDisplay().getDisplaySize();
                    try {
                        cache.end(g, new Rectangle(displaySize));
                    } catch (Throwable t) {
                        ProjectPlugin.trace(CompositeRenderContextImpl.class,
                                "painting labels failed", t);
                    }
                }

                // update the context with the new image
                ((RenderContextImpl) getContext()).setImage(copy);

            } catch (IllegalStateException e) {
                stopRendering();
                // e.printStackTrace();
                return;
            } finally {
                if (g != null)
                    g.dispose();
            }
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @throws RenderException
     * @generated NOT
     */
    @Override
    public void render(Graphics2D destination, IProgressMonitor monitor) throws RenderException {
        // notify that they are starting
        for (RenderExecutor executor : renderExecutors) {
            executor.getRenderer().setState(STARTING);
        }
        for (RenderExecutor executor : renderExecutors) {
            executor.render();
        }
    }

    /**
     * @throws RenderException
     */
    @Override
    public void render(IProgressMonitor monitor) throws RenderException {
        if (getRenderExecutors().isEmpty())
            setState(DONE);
        for (RenderExecutor renderExecutor : getRenderExecutors()) {
            renderExecutor.setRenderBounds(getRenderBounds());
            renderExecutor.render();
        }
    }

    @Override
    public void setContext(IRenderContext newContext) {
        if (context != null) {
            ((CompositeRenderContext) context).removeListener(contextListener);
        }
        if (newContext != null) {
            CompositeRenderContext compositeRenderContext = (CompositeRenderContext) newContext;
            compositeRenderContext.addListener(contextListener);
            for (IRenderContext context : compositeRenderContext.getContexts()) {
                if (findExecutor(context) == null) {
                    Renderer renderer = getRendererCreator(context)
                            .getRenderer((RenderContext) context);
                    getRenderExecutors().add(createRenderExecutor(renderer));
                }
            }
            super.setContext(newContext);
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public void stopRendering() {
        for (RenderExecutor element : getRenderExecutors()) {
            element.stopRendering();
        }
    }

} // CompositeRendererImpl
