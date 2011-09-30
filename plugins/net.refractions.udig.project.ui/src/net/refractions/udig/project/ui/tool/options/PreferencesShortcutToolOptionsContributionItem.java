package net.refractions.udig.project.ui.tool.options;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.tool.display.ToolManager;
import net.refractions.udig.project.ui.internal.tool.display.ToolProxy;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * Adds a button to the status bar that allows quick access to the active modal tool preference
 * page, if no preference page is defined it will open the parent tools preference page.
 * 
 * @see tool.exsd
 * @author leviputna
 * @since 1.2.0
 */
public class PreferencesShortcutToolOptionsContributionItem extends ContributionItem {
    final IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
            IHandlerService.class);
    final ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
    
    final String defultPreferencePage = "net.refractions.udig.tools.edit.preferences";
    
    private ToolBar toolBar;
    private ToolItem button;

    @Override
    public void fill( Composite parent ) {
        ToolManager toolManager = (ToolManager) ApplicationGIS.getToolManager();
        final ToolProxy activeTool = toolManager.getActiveToolProxy();

        toolBar = new ToolBar(parent, SWT.NONE);

        button = new ToolItem(toolBar, SWT.PUSH);
        button.setImage(activeTool.getImage());
        button.setToolTipText(activeTool.getName());

        Listener listener = new Listener(){

            @Override
            public void handleEvent( Event event ) {
                try {
                    
                    String page;
                    
                    if(activeTool.getPreferencePageId() == null){
                        page = defultPreferencePage;
                    }else{
                        page = activeTool.getPreferencePageId();
                    }
                    
                    final ParameterizedCommand command = commandService.deserialize("org.eclipse.ui.window.preferences(preferencePageId="
                            + page + ")");
                    
                    handlerService.executeCommand(command, null);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        };

        button.addListener(SWT.Selection, listener);
        toolBar.pack();

        new Label(parent, SWT.SEPARATOR);
    }

    public void update() {
        ToolManager toolManager = (ToolManager) ApplicationGIS.getToolManager();
        ToolProxy activeTool = toolManager.getActiveToolProxy();
        button.setImage(activeTool.getImage());
    }
    
    public boolean isDisposed(){
        return toolBar.isDisposed();
    }
    
    @Override
    public void dispose(){
        super.dispose();
        
        toolBar.dispose();

        button.dispose();
    }

}
