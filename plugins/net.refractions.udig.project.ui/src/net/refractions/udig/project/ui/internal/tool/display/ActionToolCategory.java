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

import java.util.List;

import net.refractions.udig.project.ui.tool.IToolManager;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * All Fire and forget actions are in this category.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public class ActionToolCategory extends ToolCategory {


    /**
     * Construct <code>ActionCategory</code>.
     * 
     * @param element
     * @param manager
     */
    public ActionToolCategory( IConfigurationElement element, IToolManager manager ) {
        super(element, manager);
    }
    /**
     * Construct <code>ActionToolCategory2</code>.
     * 
     * @param manager
     */
    public ActionToolCategory( IToolManager manager ) {
        super(manager);
    }

    public void contribute( IToolBarManager manager ) {
        for( ModalItem item : items ) {
            ToolProxy toolProxy = ((ToolProxy) item);
            if( toolProxy.isOnToolbar() )
                manager.add(toolProxy.getAction());
        }
    }

    /**
     * Contribution class for action tools on the toolbar.
     * 
     * @author jeichar
     * @author Vitalus
     *
     */
    protected class ActionToolContribution extends AbstractToolbarContributionItem {

        /**
         * @see net.refractions.udig.project.ui.internal.tool.display.CurrentContributionItem#setSelection(boolean)
         */
        public void setSelection( boolean checked, ModalItem proxy ) {
            // do nothing.
        }

        /**
         * @see net.refractions.udig.project.ui.internal.tool.display.CurrentContributionItem#isChecked()
         */
        protected boolean isChecked() {
            return false;
        }

        /**
         * @see net.refractions.udig.project.ui.internal.tool.display.AbstractToolbarContributionItem#isDefaultItem()
         */
        protected boolean isDefaultItem() {
            return false;
        }

        /**
         * @see net.refractions.udig.project.ui.internal.tool.display.AbstractToolbarContributionItem#createToolItem(org.eclipse.swt.widgets.ToolBar,
         *      int)
         */
        protected ToolItem createToolItem( ToolBar parent, int index ) {
            return toolItem = new ToolItem(parent, SWT.PUSH, index);
        }

        /**
         * @see net.refractions.udig.project.ui.internal.tool.display.AbstractToolbarContributionItem#getTools()
         */
        protected List<ModalItem> getTools() {
            return items;
        }

		@Override
		protected boolean isActiveItem() {
			return false;
		}

    }

    /**
     * @see net.refractions.udig.project.ui.internal.tool.display.ToolCategory#getHandlerSubmission(java.lang.String)
     */
    protected IHandler getHandler() {
        return null;
    }

}
