/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.internal.tool.display;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ui.internal.ApplicationGISInternal;
import net.refractions.udig.project.ui.internal.MapEditor;
import net.refractions.udig.project.ui.internal.MapEditorSelectionProvider;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;
import net.refractions.udig.project.ui.tool.IToolManager;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A category object that contributes to a toolbar.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public class ModalToolCategory extends ToolCategory {
    
    /** Alternate: displayed as a menu contribution */
    private CurrentModalToolContribution contribution;

    /** Alternate: displayed as a palette container */
    private PaletteContainer container;

    /** Handler created for this category; should cycle between tools */
    private IHandler handler;

    /**
     * Optional default selection provided used for all tools in this category As an example
     * selection tools may be able to just report the selection fromt he current layer
     */
    private IMapEditorSelectionProvider selectionProviderInstance;

    /**
     * Construct <code>ToolbarToolCategory</code>.
     * 
     * @param element
     * @param manager
     */
    public ModalToolCategory( IConfigurationElement element, IToolManager manager ) {
        super(element, manager);
    }

    /**
     * Construct <code>ModalToolCategory</code>.
     * 
     * @param manager
     */
    public ModalToolCategory( IToolManager manager ) {
        super(manager);
    }

    /**
     * @see net.refractions.udig.project.ui.internal.tool.display.ToolCategory#getHandlerSubmission(java.lang.String)
     */
    protected IHandler getHandler() {
        if (handler == null) {
            handler = new ToolCommandHandler(this);
        }

        return handler;
    }

    /**
     * Returns  a  current UI contribution item for this category of modal tools.
     * 
     * @return
     */
    public AbstractToolbarContributionItem getContribution() {
        return contribution;
    }
    /**
     * Hooks the provided container up to this cateogry; so that it can update
     * in response to key presses
     * 
     * @param container
     */
    public void hook( PaletteContainer container ){
        this.container = container;
    }
    
    public void container( PaletteContainer container ){
        this.container = container;
    }
    
    public void contribute( IToolBarManager manager ) {
        CurrentContributionItem old = contribution;
        contribution = new CurrentModalToolContribution();
        manager.add(contribution);
        //Add CurrentModalToolContribution as a contribution to each item of themodal category
        for( ModalItem item : this ) {
            ToolProxy tool = (ToolProxy) item;
            if (tool.getType() == ToolProxy.MODAL) {
                if(old != null){
                    /*
                     * Vitalis:
                     * Just to clean old contributions being collected
                     * during maps opening/closing.
                     */
                    tool.removeContribution(old);
                }
                tool.addContribution(contribution);
            }
        }
    }

    public IMapEditorSelectionProvider getSelectionProvider() {
        if( selectionProviderInstance==null ){
            if (element.getAttribute("selectionProvider") != null) { //$NON-NLS-1$
                try {
                    selectionProviderInstance = (IMapEditorSelectionProvider) element
                            .createExecutableExtension("selectionProvider"); //$NON-NLS-1$
                } catch (CoreException e) {
                    ProjectUIPlugin
                            .log(
                                    "Error instantiating selection provider for " + element.getNamespace() + "/" + element.getName(), e); //$NON-NLS-1$//$NON-NLS-2$
                    selectionProviderInstance = new MapEditorSelectionProvider();
                }
            }else{
                selectionProviderInstance = new MapEditorSelectionProvider();
            }
        }
        return selectionProviderInstance;
    }

    /**
     * The class representing contribution item for modal tools on the toolbar.
     * 
     * @author jeichar
     * @author Vitalus
     * 
     */
    protected class CurrentModalToolContribution extends AbstractToolbarContributionItem {

        /**
         * @see net.refractions.udig.project.ui.internal.tool.display.AbstractToolbarContributionItem#createToolItem(org.eclipse.swt.widgets.ToolBar,
         *      int)
         */
        protected ToolItem createToolItem( final ToolBar parent, int index ) {
            final ToolItem item = new ToolItem(parent, SWT.DROP_DOWN, index);
            return item;
        }

        @Override
        protected void runCurrentTool( ) {
            super.runCurrentTool();
            IMapEditorSelectionProvider provider = ((ToolProxy)currentTool).getSelectionProvider();
            MapEditor editor = ApplicationGISInternal.getActiveEditor();
            if( editor!=null && editor.getSite()!=null && provider!=editor.getSite().getSelectionProvider() )
                editor.setSelectionProvider(provider);

        }
        
        /**
         * @see net.refractions.udig.project.ui.internal.tool.display.AbstractToolbarContributionItem#getTools()
         */
        protected List<ModalItem> getTools() {
            List<ModalItem> onToolbar=new ArrayList<ModalItem>();
            ModalItem enabled=null;
            
            for( ModalItem item : items ) {
                if( ((ToolProxy)item).isOnToolbar() )
                    onToolbar.add(item);
                if( item.isEnabled() && enabled==null && ((ToolProxy)item).isOnToolbar())
                    enabled=item;
            }
            if( enabled!=null ){
                onToolbar.remove(enabled);
                onToolbar.add(0, enabled);
            }
                
            return onToolbar;
        }

        /**
         * @see net.refractions.udig.project.ui.internal.tool.display.AbstractToolbarContributionItem#isDefaultItem()
         */
        protected boolean isDefaultItem() {
            return getTools().contains(((ToolManager)manager).defaultModalToolProxy);
        }

		/**
		 *  (non-Javadoc)
		 * @see net.refractions.udig.project.ui.internal.tool.display.AbstractToolbarContributionItem#isActiveItem()
		 */
		@Override
		protected boolean isActiveItem() {
			return getTools().contains( ((ToolManager)manager).getActiveToolProxy());
		}
    }

    /** As an alternative to a contirbution; we can use a palette container */
    public PaletteContainer getContainer() {
        return container;
    }

}
