package net.refractions.udig.project.ui.feature;

import java.awt.Rectangle;

import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.EditCommand;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.command.factory.BasicCommandFactory;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.command.factory.NavigationCommandFactory;
import net.refractions.udig.project.command.factory.SelectionCommandFactory;
import net.refractions.udig.project.internal.impl.AbstractContextImpl;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.ui.commands.DrawCommandFactory;
import net.refractions.udig.project.ui.commands.IDrawCommand;
import net.refractions.udig.project.ui.internal.MapEditor;
import net.refractions.udig.project.ui.internal.MapEditorPart;
import net.refractions.udig.project.ui.internal.MapEditorSite;
import net.refractions.udig.project.ui.internal.tool.ToolContext;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getContextModel <em>Context Model</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getPixelSize <em>Pixel Size</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getViewportModel <em>Viewport Model</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getEditManager <em>Edit Manager</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getRenderManager <em>Render Manager</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getMapDisplay <em>Map Display</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getMap <em>Map</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getProject <em>Project</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getViewportPane <em>Viewport Pane</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getDrawFactory <em>Draw Factory</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getEditFactory <em>Edit Factory</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getNavigationFactory <em>Navigation Factory</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getSelectionFactory <em>Selection Factory</em>}</li>
 * </ul>
 * </p>
 * 
 */
public class FeatureSiteImpl extends AbstractContextImpl implements ToolContext {

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

    public FeatureSiteImpl( ) {        
    }
    
    /**
     * Copy the provided FeatureSite.
     * @param copy
     */
    public FeatureSiteImpl( FeatureSiteImpl copy ) {
        super( copy );
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
        MapEditorSite site = getEditorSite();
        if (site == null)
            return null;
        return site.getActionBars().getStatusLineManager();
    }
    
    public IActionBars2 getActionBars() {
        IEditorSite site = getEditorSite();
        if (site == null)
            return null;
        return (IActionBars2) site.getActionBars();
    }

    private MapEditorSite getEditorSite() {
        IWorkbenchWindow window = getWindow();
        if (window == null){
            return null;
        }
        IWorkbenchPage page = window.getActivePage();
        if (page == null){
            return null;
        }
        IEditorPart part = page.getActiveEditor();
        if (part == null || !(part instanceof MapEditor) )
            return null;
        return ((MapEditorPart)part).getMapEditorSite();
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

    public FeatureSiteImpl copy() {
        return new FeatureSiteImpl(this);
    }
} // Impl
