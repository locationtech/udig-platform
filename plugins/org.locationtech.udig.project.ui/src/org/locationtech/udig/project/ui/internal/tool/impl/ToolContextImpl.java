/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.locationtech.udig.project.ui.internal.tool.impl;

import java.awt.Rectangle;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IServiceLocator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.EditCommand;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.NavCommand;
import org.locationtech.udig.project.command.factory.BasicCommandFactory;
import org.locationtech.udig.project.command.factory.EditCommandFactory;
import org.locationtech.udig.project.command.factory.NavigationCommandFactory;
import org.locationtech.udig.project.command.factory.SelectionCommandFactory;
import org.locationtech.udig.project.internal.impl.AbstractContextImpl;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.impl.ScaleUtils;
import org.locationtech.udig.project.internal.render.impl.ScaleUtils.CalculateZoomLevelParameter;
import org.locationtech.udig.project.ui.commands.DrawCommandFactory;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.tool.ToolContext;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;

/**
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getContextModel <em>Context Model</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getPixelSize <em>Pixel Size</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getViewportModel <em>Viewport Model</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getEditManager <em>Edit Manager</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getRenderManager <em>Render Manager</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getMapDisplay <em>Map Display</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getMap <em>Map</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getProject <em>Project</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getViewportPane <em>Viewport Pane</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getDrawFactory <em>Draw Factory</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getEditFactory <em>Edit Factory</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getNavigationFactory <em>Navigation Factory</em>}</li>
 * <li>{@link org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl#getSelectionFactory <em>Selection Factory</em>}</li>
 * </ul>
 * </p>
 * 
 */
public class ToolContextImpl extends AbstractContextImpl implements ToolContext {

    private final class IActionBars2Adapter implements IActionBars2 {
		IActionBars bars;

		private IActionBars2Adapter(IViewPart view) {
			bars = view.getViewSite().getActionBars();
		}

		public void clearGlobalActionHandlers() {
			bars.clearGlobalActionHandlers();
		}

		public IAction getGlobalActionHandler(String actionId) {
			return bars.getGlobalActionHandler(actionId);
		}

		public IMenuManager getMenuManager() {
			return bars.getMenuManager();
		}

		public IServiceLocator getServiceLocator() {
			return bars.getServiceLocator();
		}

		public IStatusLineManager getStatusLineManager() {
			return bars.getStatusLineManager();
		}

		public IToolBarManager getToolBarManager() {
			return bars.getToolBarManager();
		}

		public void setGlobalActionHandler(String actionId,
				IAction handler) {
			bars.setGlobalActionHandler(actionId, handler);
		}

		public void updateActionBars() {
			bars.updateActionBars();
		}

		public ICoolBarManager getCoolBarManager() {
			return null;
		}
	}

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
    private final EditCommandFactory editFactory = EditCommandFactory
            .getInstance();

    /**
     * The cached value of the '{@link #getNavigationFactory() <em>Navigation Factory</em>}'
     * attribute.
     * 
     * @see #getNavigationFactory()
     */
    private final NavigationCommandFactory navigationFactory = NavigationCommandFactory
            .getInstance();

    /**
     * The cached value of the '{@link #getSelectionFactory() <em>Selection Factory</em>}'
     * attribute. 
     * 
     * @see #getSelectionFactory()
     */
    private final SelectionCommandFactory selectionFactory = SelectionCommandFactory
            .getInstance();

    private final BasicCommandFactory basicCommandFactory=BasicCommandFactory.getInstance();

    public ToolContextImpl( ) {
        super();
    }
    public ToolContextImpl( ToolContextImpl impl ) {
        super(impl);
    }
    public DrawCommandFactory getDrawFactory() {
        return drawFactory;
    }
    public EditCommandFactory getEditFactory() {
        return editFactory;
    }
    public NavigationCommandFactory getNavigationFactory() {
        return navigationFactory;
    }
    public SelectionCommandFactory getSelectionFactory() {
        return selectionFactory;
    }
    public void sendCommand( IDrawCommand command ) {
        command.setMap(getMap());
        ViewportPane viewportPane = ((ViewportPane) getRenderManagerInternal().getMapDisplay());
        viewportPane.addDrawCommand(command);
        Rectangle validArea;
        try{
        validArea = command.getValidArea();
        }catch (Exception e) {
            validArea=null;
        }
        if( validArea!=null )
            viewportPane.repaint(validArea.x, validArea.y, validArea.width, validArea.height);
        else
            viewportPane.repaint();
    }

    public void sendASyncCommand( Command command ) {
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

    public void sendSyncCommand( Command command ) {
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

    public ViewportPane getViewportPane() {
        return (ViewportPane) getMapDisplay();
    }
    public IWorkbench getWorkbench() {
        return PlatformUI.getWorkbench();
    }
    public Display getDisplay() {
        return Display.getDefault();
    }
    public void log( Plugin currentPlugin, String message, int severity, Throwable exception ) {
        String ID1 = currentPlugin.getBundle().getSymbolicName();
        currentPlugin.getLog().log(new Status(severity, ID1, 0, message, exception));
    }

    @Override
    public RenderManager getRenderManagerInternal() {
        return getMapInternal().getRenderManagerInternal();
    }
    public IStatusLineManager getStatusBar() {
        IActionBars2 bars = getActionBars();
        if (bars == null)
            return null;
        return bars.getStatusLineManager();
    }
    
    public IActionBars2 getActionBars() {
        IWorkbenchWindow window = getWindow();
        if (window == null)
            return null;
        IWorkbenchPage page = window.getActivePage();
        if (page == null)
            return null;
        IEditorReference[] editors = page.getEditorReferences();
        
        for (IEditorReference ref : editors) {
			IEditorPart editor = ref.getEditor(false);
			if (editor instanceof MapPart) {
				MapPart mapPart = (MapPart) editor;
				if(getMap() == mapPart.getMap()) {
					return (IActionBars2) editor.getEditorSite().getActionBars();
				}
			}
		}

        IViewReference[] views = page.getViewReferences();
        
        for (IViewReference ref : views) {
			final IViewPart view = ref.getView(false);
			if (view instanceof MapPart) {
				MapPart mapPart = (MapPart) view;
				if(getMap() == mapPart.getMap()) {
					return new IActionBars2Adapter(view);
				}
			}
		}

        
        return null;
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

    public void updateUI( Runnable runnable ) {
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
    public BasicCommandFactory getBasicCommandFactory() {
        return this.basicCommandFactory;
    }

    public ToolContextImpl copy() {
        return new ToolContextImpl(this);
    }
	public double calculateZoomLevel(double previousZoom, double zoom, Coordinate fixedPoint,
			boolean alwayUsePreferredZoomLevels, boolean alwaysChangeZoom) {
		CalculateZoomLevelParameter params = new CalculateZoomLevelParameter(getViewportModelInternal(), getViewportPane(), previousZoom, zoom, fixedPoint,
						alwayUsePreferredZoomLevels, alwaysChangeZoom, ScaleUtils.zoomClosenessPreference());
		return ScaleUtils.calculateZoomLevel(params);
	}
} // Impl
