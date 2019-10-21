/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.ui.internal;

import static org.locationtech.udig.project.ui.internal.Trace.RENDER;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.ContextModelListenerAdapter;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.ExecutorVisitor;
import org.locationtech.udig.project.internal.render.MultiLayerRenderer;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RenderFactory;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.internal.render.impl.CompositeRenderContextImpl;
import org.locationtech.udig.project.internal.render.impl.CompositeRendererImpl;
import org.locationtech.udig.project.internal.render.impl.RenderExecutorComposite;
import org.locationtech.udig.project.internal.render.impl.RenderExecutorMultiLayer;
import org.locationtech.udig.project.internal.render.impl.RenderManagerImpl;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;

/**
 * A IRenderManager that is reacts to events such as viewport model changes.
 * <p>
 * These are the events we have found this beastie pays attention to:
 * <ul>
 * <li>viewportListner will trigger some kind of refresh when you change view
 * size
 * <li>renderExecuttorListener does not actually do much; it passes along
 * something else that actually does the listeneing. The composite render
 * exector
 * <li>contextModelAdapter watches the IMap and notices new layers, deleted
 * layers, zorder changing and the occasional style change
 * </ul>
 * The Map owns one of these things; uses it to scribble on the screen to the
 * user.
 * 
 * @generated
 */
public class RenderManagerDynamic extends RenderManagerImpl {
	/**
	 * Watches the layer add / delete / change of zorder and style changes.
	 */
	ContextModelListenerAdapter contextModelAdapter = RenderManagerAdapters
			.createContextModelListener(this);
	/**
	 * Watches the viewport model and triggers some kind of refresh. This is the
	 * bounds and the crs changing; panning zooming swooshing and other assorted
	 * user drive fun.
	 */
	Adapter viewportListener = RenderManagerAdapters
			.createViewportListener(this);

	/**
	 * listents to the viewport model (using the provided viewportListener)
	 * and morphing the events into something for the map to be updated with?
	 * 
	 */
	private Adapter viewportModelChangeListener = RenderManagerAdapters
            .createViewportModelChangeListener(this, viewportListener, contextModelAdapter);

	/**
	 * Collection of contexts used to draw the images.
	 */
    public volatile Collection<RenderContext> configuration = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
	public RenderManagerDynamic() {
		super();
		eAdapters().add(viewportModelChangeListener);
	}

	/**
	 * @see org.locationtech.udig.project.render.impl.RenderManagerImpl#refresh(org.locationtech.udig.project.Layer)
	 */
	public void refresh(final ILayer layer, Envelope bounds) {
		checkState();
		if (!renderingEnabled) {
			return;
		}

		if (getMapDisplay() == null || getRenderExecutor() == null) {
			return; // we are not set up to renderer yet!
		}
		getRendererCreator().reset();
		validateRendererConfiguration();
		final SelectionLayer selectionLayer = getRendererCreator()
				.findSelectionLayer(layer);

		final ReferencedEnvelope bbox = bounds == null
				|| bounds instanceof ReferencedEnvelope ? (ReferencedEnvelope) bounds
				: new ReferencedEnvelope(bounds, getRenderExecutor()
						.getContext().getCRS());

		getRenderExecutor().visit(new ExecutorVisitor() {
			public void visit(RenderExecutor executor) {
				if (executor.getContext().getLayer() == layer
						|| selectionLayer == executor.getContext().getLayer()) {
					// tell the renderer the area of the screen to refresh
					executor.getRenderer().setRenderBounds(bbox);
					// register our interest in seeing the screen redrawn
					executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
				}
			}

			public void visit(RenderExecutorMultiLayer executor) {
				if (executor.getContext().getLayers().contains(layer)
						|| executor.getContext().getLayers().contains(
								selectionLayer))
					executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
			}

			public void visit(RenderExecutorComposite executor) {
				List<RenderExecutor> executors = new ArrayList<RenderExecutor>(
						executor.getRenderer().getRenderExecutors());
				for (RenderExecutor child : executors) {
					child.visit(this);
				}
			}
		});
	}
	
	@Override
    public void refreshImage() {
	    try {
            ((CompositeRendererImpl)getRenderExecutor().getRenderer()).refreshImage();
            getRenderExecutor().setState(IRenderer.DONE);
        } catch (RenderException e) {
            ProjectPlugin.log("", e); //$NON-NLS-1$
        }
    }

	/**
	 * @see org.locationtech.udig.project.render.impl.RenderManagerImpl#refreshSelection(org.locationtech.jts.geom.Envelope)
	 */
	public void refreshSelection(final ILayer layer, final Envelope bounds) {
		checkState();
		if (!renderingEnabled) {
			return;
		}

		if (getMapDisplay() == null || getRenderExecutor() == null)
			return;

		final Layer selectionLayer = getRendererCreator().findSelectionLayer(
				layer);

		if (selectionLayer == null)
			return;

		getRendererCreator().reset();
		validateRendererConfiguration();

		getRenderExecutor().visit(new ExecutorVisitor() {
			public void visit(RenderExecutor executor) {
				IRenderContext context = executor.getContext();
				if (selectionLayer == context.getLayer()) {
					executor.getRenderer().setRenderBounds(bounds);
					if (bounds != null) {
						Rectangle bounds2 = context.toShape(
								new ReferencedEnvelope(bounds,
										getViewportModelInternal().getCRS()))
								.getBounds();
						context.clearImage(bounds2);
					} else {
						context.clearImage();
					}
					executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
				}
			}

			public void visit(RenderExecutorMultiLayer executor) {
				CompositeRenderContext contexts = executor.getContext();
				for (IRenderContext context : contexts.getContexts()) {
					if (context.getLayer() == selectionLayer) {
						executor.getRenderer().setState(
								IRenderer.RENDER_REQUEST);
						return;
					}
				}
			}

			public void visit(RenderExecutorComposite executor) {
				for (RenderExecutor child : executor.getRenderer()
						.getRenderExecutors())
					child.visit(this);
			}
		});
	}

	/**
	 * @see org.locationtech.udig.project.render.IRenderManager#clearSelection(ILayer)
	 */
	public void clearSelection(ILayer layer) {
		checkState();
		if (getMapDisplay() == null || getRenderExecutor() == null)
			return;
		final Layer selectionLayer = getRendererCreator().findSelectionLayer(
				layer);

		if (selectionLayer == null)
			return;

		try {
			CompositeRendererImpl compositeRendererImpl = (CompositeRendererImpl) getRenderExecutor()
					.getRenderer();
			compositeRendererImpl.refreshImage();
			compositeRendererImpl.setState(IRenderer.DONE);

		} catch (RenderException e) {
			ProjectUIPlugin.log("", e); //$NON-NLS-1$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@SuppressWarnings("unchecked")
	public void refresh(final Envelope bounds) {
		checkState();
		if (!renderingEnabled) {
			return;
		}
		getViewportModelInternal().setInitialized(true);
		if (getMapDisplay() == null) {
			return;
		}
		if (getMapDisplay().getWidth() < 1 || getMapDisplay().getHeight() < 1)
			return;

		getRendererCreator().reset();
		validateRendererConfiguration();

		if (!getMapInternal().getContextModel().eAdapters().contains(
				contextModelAdapter))
			getMapInternal().getContextModel().eAdapters().add(
					contextModelAdapter);

		try {
			getRenderExecutor().setRenderBounds(bounds);
			getRenderExecutor().render();
		} catch (RenderException e) {
			// Won't happen
		}
	}

	void initRenderCreator(RenderContext context) {
		checkState();
		List<Layer> layers = getMapInternal().getLayersInternal();
		// make sure renderer creator is initialized.
		getRendererCreator().setContext(context);
		ENotificationImpl notification = new ENotificationImpl(this,
				Notification.ADD_MANY, ProjectPackage.CONTEXT_MODEL__LAYERS,
				null, layers);
		getRendererCreator().changed(notification);

	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@SuppressWarnings("unchecked")
	public void dispose() {
		configuration = null;
		Set<EObject> set = new HashSet<EObject>();
		set.add(getMapInternal());
		Iterator<EObject> iter = getMapInternal().eAllContents();
		while (iter.hasNext()) {
			EObject obj = iter.next();
			removeAdapters(obj);
		}
		removeAdapters(getRenderExecutor());
		((ViewportPane) mapDisplay).setRenderManager(null);
		super.dispose();
	}

	/**
	 * @param obj
	 */
	private void removeAdapters(EObject obj) {
		obj.eAdapters().remove(this.viewportListener);
		obj.eAdapters().remove(this.viewportModelChangeListener);
		obj.eAdapters().remove(this.contextModelAdapter);
		obj.eAdapters().remove(this.renderExecutorListener);
		obj.eAdapters().remove(this.selectionListener);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDisplayGen(IMapDisplay newDisplay) {
		IMapDisplay oldDisplay = mapDisplay;
		mapDisplay = newDisplay;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					RenderPackage.RENDER_MANAGER__MAP_DISPLAY, oldDisplay,
					mapDisplay));
	}

	/**
	 * TODO summary sentence for setViewport ...
	 * 
	 * @see org.locationtech.udig.project.render.RenderManager#setDisplay(org.locationtech.udig.project.render.displayAdapter.IMapDisplay)
	 * @param value
	 */
	public void setDisplay(IMapDisplay value) {
		checkState();
		((ViewportPane) value).setRenderManager(this);
		setDisplayGen(value);
	}

	Adapter renderExecutorListener = RenderManagerAdapters
			.getRenderExecutorListener(this);

	/**
	 * @see org.locationtech.udig.project.render.impl.RenderManagerImpl#setRenderExecutor(org.locationtech.udig.project.render.RenderExecutor)
	 */
	@SuppressWarnings("unchecked")
	public void setRenderExecutor(RenderExecutor newRenderExecutor) {
		checkState();
		if (renderExecutor != null) {
			renderExecutor.eAdapters().remove(renderExecutorListener);
		}
		if (newRenderExecutor != null
				&& !newRenderExecutor.eAdapters().contains(
						renderExecutorListener)) {
			newRenderExecutor.eAdapters().add(renderExecutorListener);
		}
		super.setRenderExecutor(newRenderExecutor);
	}

	Adapter selectionListener = RenderManagerAdapters.createLayerListener(this);

	/**
	 * @see org.locationtech.udig.project.render.impl.RenderManagerImpl#basicSetMap(org.locationtech.udig.project.Map,
	 *      org.eclipse.emf.common.notify.NotificationChain)
	 */
	@SuppressWarnings("unchecked")
	public NotificationChain basicSetMapInternal(Map newMap,
			NotificationChain msgs) {
		if (getMapInternal() != null) {
			getMapInternal().eAdapters().remove(selectionListener);
			getMapInternal().removeDeepAdapter(selectionListener);
		}
		if (newMap != null) {
			newMap.eAdapters().add(selectionListener);
			newMap.addDeepAdapter(selectionListener);
		}
		return super.basicSetMapInternal(newMap, msgs);
	}

	/**
	 * Ensures that the current configuration of renderer is a valid choice. For
	 * example the each of the layers in the map has a renderer that can render
	 * it.
	 */
	public void validateRendererConfiguration() {
		checkState();
		Collection<RenderContext> configuration;
		synchronized (this) {
			if (this.configuration == null) {
				configuration = null;
			} else {
				configuration = new ArrayList<RenderContext>(this.configuration);
			}
		}
		Collection<RenderContext> configuration2;
		if (configuration != null) {
			configuration2 = getRendererCreator().getConfiguration();
			List<RenderContext> removeList = new ArrayList<RenderContext>();
			List<RenderContext> addList = new ArrayList<RenderContext>();
			for (IRenderContext context : configuration) {
				if (!configuration2.contains(context))
					removeList.add((RenderContext) context);
			}
			for (RenderContext context : configuration2) {
				if (configuration.contains(context))
					continue;

				addList.add(context);
			}
			CompositeRenderContext compositecontext = (CompositeRenderContext) getRenderExecutor()
					.getContext();
			compositecontext.removeContexts(removeList);
			if (!addList.isEmpty())
				compositecontext.removeContexts(addList);
			compositecontext.addContexts(addList);

		} else {
			initRenderExecutor();
			configuration2 = getRendererCreator().getConfiguration();

			CompositeRenderContext compositecontext = (CompositeRenderContext) getRenderExecutor()
					.getContext();
			// need this because this is taking place in a non-synchronized
			// block so it is possible for
			// this code to be executed twice. I want the second run to be
			// accurate.
			// might need to be thought about more.
			compositecontext.clear();
			compositecontext.addContexts(configuration2);
		}
		synchronized (this) {
			this.configuration = configuration2;
		}
		logRendererTypes();

	}

	/**
     * 
     */
	private void initRenderExecutor() {
		checkState();
		MultiLayerRenderer renderExecutor = RenderFactory.eINSTANCE
				.createCompositeRenderer();
		CompositeRenderContext context = new CompositeRenderContextImpl() {
			@Override
			public synchronized BufferedImage getImage(int width, int height) {
				if (image == null || image.getWidth() < width
						|| image.getHeight() < height) {
					image = new BufferedImage(width, height,
							BufferedImage.TYPE_3BYTE_BGR);
					Graphics2D g = image.createGraphics();
					g.setBackground(Color.WHITE);
					g.clearRect(0, 0, width, height);
					g.dispose();
				}

				return image;
			}

			@Override
			public synchronized void clearImage(Rectangle paintArea) {
				if (image == null)
					return;
				Graphics2D g = image.createGraphics();
				g.setBackground(Color.WHITE);
				g.clearRect(paintArea.x, paintArea.y, paintArea.width,
						paintArea.height);
				g.dispose();
				// // FIXME Arbonaut Oy , Vitali Diatchkov
				// System.out.println(
				// "synchronized CompositeRenderContext.clearImage()");
			}

		};
		context.setMapInternal(getMapInternal());
		context.setRenderManagerInternal(this);
		
		renderExecutor.setContext(context);

		initRenderCreator(context);

		renderExecutor.setName(Messages.RenderManagerDynamic_allLayers);
		setRenderExecutor(RenderFactory.eINSTANCE
				.createRenderExecutor(renderExecutor));
	}

	private void logRendererTypes() {
		if (ProjectUIPlugin.isDebugging(RENDER)) {

			final StringBuffer log = new StringBuffer("Current Renderers:"); //$NON-NLS-1$
			getRenderExecutor().visit(new ExecutorVisitor() {
				public void visit(RenderExecutor executor) {
					log
							.append("\n\t" + executor.getClass().getSimpleName() + ":" + //$NON-NLS-1$ //$NON-NLS-2$
									executor.getRenderer().getClass()
											.getSimpleName() + "-" + //$NON-NLS-1$
									executor.getContext().getLayer().getName());
				}

				public void visit(RenderExecutorComposite executor) {
					log
							.append("\n\t" + executor.getRenderer().getClass().getSimpleName()); //$NON-NLS-1$
					for (RenderExecutor child : executor.getRenderer()
							.getRenderExecutors())
						child.visit(this);
				}

				public void visit(RenderExecutorMultiLayer executor) {
					log
							.append("\n\t" + executor.getRenderer().getClass().getSimpleName()); //$NON-NLS-1$
				}
			});
			System.out.println(log);
		}
	}

	public RenderExecutor getRenderExecutor() {
		checkState();
		if (renderExecutor == null) {
			initRenderExecutor();
		}

		return renderExecutor;
	}
} // RenderManagerImpl
