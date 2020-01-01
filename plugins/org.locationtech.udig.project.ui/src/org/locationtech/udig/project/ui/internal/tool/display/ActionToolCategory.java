/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.tool.display;

import java.util.List;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.locationtech.udig.project.ui.tool.IToolManager;

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
         * @see org.locationtech.udig.project.ui.internal.tool.display.CurrentContributionItem#setSelection(boolean)
         */
        public void setSelection( boolean checked, ModalItem proxy ) {
            // do nothing.
        }

        /**
         * @see org.locationtech.udig.project.ui.internal.tool.display.CurrentContributionItem#isChecked()
         */
        protected boolean isChecked() {
            return false;
        }

        /**
         * @see org.locationtech.udig.project.ui.internal.tool.display.AbstractToolbarContributionItem#isDefaultItem()
         */
        protected boolean isDefaultItem() {
            return false;
        }

        /**
         * @see org.locationtech.udig.project.ui.internal.tool.display.AbstractToolbarContributionItem#createToolItem(org.eclipse.swt.widgets.ToolBar,
         *      int)
         */
        protected ToolItem createToolItem( ToolBar parent, int index ) {
            return toolItem = new ToolItem(parent, SWT.PUSH, index);
        }

        /**
         * @see org.locationtech.udig.project.ui.internal.tool.display.AbstractToolbarContributionItem#getTools()
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
     * @see org.locationtech.udig.project.ui.internal.tool.display.ToolCategory#getHandlerSubmission(java.lang.String)
     */
    protected IHandler getHandler() {
        return null;
    }

}
