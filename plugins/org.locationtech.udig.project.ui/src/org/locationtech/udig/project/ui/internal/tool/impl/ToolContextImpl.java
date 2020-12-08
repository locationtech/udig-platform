/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.tool.impl;

import java.awt.Rectangle;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.EditCommand;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.NavCommand;
import org.locationtech.udig.project.command.factory.EditCommandFactory;
import org.locationtech.udig.project.command.factory.SelectionCommandFactory;
import org.locationtech.udig.project.internal.impl.AbstractContextImpl;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.impl.ScaleUtils;
import org.locationtech.udig.project.internal.render.impl.ScaleUtils.CalculateZoomLevelParameter;
import org.locationtech.udig.project.ui.commands.DrawCommandFactory;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.MapSite;
import org.locationtech.udig.project.ui.internal.tool.ToolContext;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;

/**
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getContextModel
 * <em>Context Model</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getPixelSize
 * <em>Pixel Size</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getViewportModel
 * <em>Viewport Model</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getEditManager
 * <em>Edit Manager</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getRenderManager
 * <em>Render Manager</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getMapDisplay
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getSelectionFactory <em>Selection Factory</em>}</li>
 * </ul>
 * </p>
 *
 */
public class ToolContextImpl extends AbstractContextImpl implements ToolContext {

    /**
     * The cached value of the '{@link #getDrawFactory() <em>Draw Factory</em>}' attribute.
     *
     * @see #getDrawFactory()
     */
    private final DrawCommandFactory drawFactory = DrawCommandFactory.getInstance();

    /**
     * The cached value of the '{@link #getEditFactory() <em>Edit Factory</em>}' attribute.
     *
     * @see #getEditFactory()
     */
    private final EditCommandFactory editFactory = EditCommandFactory.getInstance();
     * The cached value of the '{@link #getSelectionFactory() <em>Selection Factory</em>}'
     * attribute.
     *
     * @see #getSelectionFactory()
     */
    private final SelectionCommandFactory selectionFactory = SelectionCommandFactory.getInstance();

    public ToolContextImpl() {
        super();
    }

    public ToolContextImpl(ToolContextImpl impl) {
        super(impl);
    }

    @Override
    public DrawCommandFactory getDrawFactory() {
        return drawFactory;
    }

    @Override
    public EditCommandFactory getEditFactory() {
        return editFactory;
    }

    @Override
    public SelectionCommandFactory getSelectionFactory() {
        return selectionFactory;
    }

    public void sendCommand(IDrawCommand command) {
        command.setMap(getMap());
        ViewportPane viewportPane = ((ViewportPane) getRenderManagerInternal().getMapDisplay());
        viewportPane.addDrawCommand(command);
        Rectangle validArea;
        try {
            validArea = command.getValidArea();
        } catch (Exception e) {
            validArea = null;
        }
        if (validArea != null)
            viewportPane.repaint(validArea.x, validArea.y, validArea.width, validArea.height);
        else
            viewportPane.repaint();
    }

    @Override
    public void sendASyncCommand(Command command) {
        if (command instanceof NavCommand)
            getMapInternal().sendCommandASync((NavCommand) command);
        else if (command instanceof EditCommand)
            getMapInternal().sendCommandASync((EditCommand) command);
        else if (command instanceof IDrawCommand)
            sendCommand((IDrawCommand) command);
        else if (command instanceof MapCommand)
            getMapInternal().sendCommandASync((MapCommand) command);
        else
            getProjectInternal().sendASync(command);
    }

    @Override
    public void sendSyncCommand(Command command) {
        if (command instanceof NavCommand)
            getMapInternal().sendCommandSync((NavCommand) command);
        else if (command instanceof EditCommand)
            getMapInternal().sendCommandSync((EditCommand) command);
        else if (command instanceof IDrawCommand)
            sendCommand((IDrawCommand) command);
        else if (command instanceof MapCommand)
            getMapInternal().sendCommandSync((MapCommand) command);
        else
            getProjectInternal().sendSync(command);
    }

    @Override
    public ViewportPane getViewportPane() {
        return (ViewportPane) getMapDisplay();
    }

    @Override
    public IWorkbench getWorkbench() {
        return PlatformUI.getWorkbench();
    }

    @Override
    public Display getDisplay() {
        return Display.getDefault();
    }

    @Override
    public void log(Plugin currentPlugin, String message, int severity, Throwable exception) {
        String ID1 = currentPlugin.getBundle().getSymbolicName();
        currentPlugin.getLog().log(new Status(severity, ID1, 0, message, exception));
    }

    @Override
    public RenderManager getRenderManagerInternal() {
        return getMapInternal().getRenderManagerInternal();
    }

    @Override
    public IActionBars getActionBars() {
        MapSite workbenchPartSite = getSite();
        if (workbenchPartSite != null) {
            return workbenchPartSite.getActionBars();
        }

        return null;
    }

    private MapSite getSite() {
        IWorkbenchWindow window = getWindow();
        if (window == null)
            return null;
        IWorkbenchPage page = window.getActivePage();
        if (page == null)
            return null;
        IEditorReference[] editors = page.getEditorReferences();

        for (IEditorReference ref : editors) {
            IEditorPart editor = ref.getEditor(false);
            if (isMapPart(editor)) {
                return ((MapPart) editor).getMapSite();
            }
        }

        IViewReference[] views = page.getViewReferences();

        for (IViewReference ref : views) {
            final IViewPart view = ref.getView(false);
            if (isMapPart(view)) {
                return ((MapPart) view).getMapSite();
            }
        }

        return null;
    }

    private boolean isMapPart(IWorkbenchPart workbenchPart) {
        return (workbenchPart instanceof MapPart);
    }

    private IWorkbenchWindow getWindow() {
        IWorkbench bench = PlatformUI.getWorkbench();
        if (bench == null)
            return null;
        IWorkbenchWindow window = bench.getActiveWorkbenchWindow();
        if (window == null) {
            if (bench.getWorkbenchWindowCount() > 0)
                window = bench.getWorkbenchWindows()[0];
        }
        return window;
    }

    @Override
    public void updateUI(Runnable runnable) {
        if (Display.getCurrent() != null) {
            runnable.run();
            return;
        }
        IWorkbench bench = PlatformUI.getWorkbench();
        Display display = null;
        if (bench != null)
            display = bench.getDisplay();

        if (display == null)
            display = Display.getDefault();

        display.asyncExec(runnable);
    }

    @Override

    @Override
    public ToolContextImpl copy() {
        return new ToolContextImpl(this);
    }

    @Override
    public double calculateZoomLevel(double previousZoom, double zoom, Coordinate fixedPoint,
            boolean alwayUsePreferredZoomLevels, boolean alwaysChangeZoom) {
        CalculateZoomLevelParameter params = new CalculateZoomLevelParameter(
                getViewportModelInternal(), getViewportPane(), previousZoom, zoom, fixedPoint,
                alwayUsePreferredZoomLevels, alwaysChangeZoom,
                ScaleUtils.zoomClosenessPreference());
        return ScaleUtils.calculateZoomLevel(params);
    }

} // Impl
