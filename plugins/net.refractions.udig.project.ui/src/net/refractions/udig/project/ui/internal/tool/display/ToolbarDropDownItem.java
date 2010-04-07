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

import net.refractions.udig.project.ui.internal.ImageConstants;
import net.refractions.udig.project.ui.internal.Images;
import net.refractions.udig.project.ui.internal.Messages;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A Toolbar item that creates a drop down menu of all the objects in the category.
 * 
 * @author jeichar
 * @since 0.9.0
 * 
 * @deprecated
 */
public class ToolbarDropDownItem extends ContributionItem {

    ToolCategory category;
    AbstractToolbarContributionItem currentItem;
    /**
     * Construct <code>ToolbarDropDownItem</code>.
     */
    public ToolbarDropDownItem( ToolCategory category, AbstractToolbarContributionItem currentItem ) {
        this.category = category;
        this.currentItem = currentItem;
    }
    /**
     * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.ToolBar, int)
     */
    public void fill( final ToolBar parent, int index ) {
        super.fill(parent, index);
        ToolItem arrow = new ToolItem(parent, SWT.PUSH, index);
        arrow.setImage(Images.get(ImageConstants.DROP_DOWN_BUTTON));
        arrow.setWidth(16);
        arrow.setToolTipText(Messages.ToolbarDropDownItem_chooseTool); 
        arrow.addSelectionListener(new SelectionListener(){

            public void widgetSelected( SelectionEvent e ) {
                widgetDefaultSelected(e);
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                final Menu menu = new Menu(parent.getParent());
                for( final ModalItem tool : category ) {
                    MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
                    menuItem.setImage(tool.getImage());
                    menuItem.setText(tool.getName());
                    menuItem.addSelectionListener(new SelectionListener(){

                        public void widgetSelected( SelectionEvent e ) {
                            widgetDefaultSelected(e);
                        }

                        public void widgetDefaultSelected( SelectionEvent e ) {
                            currentItem.setCurrentTool(tool);
                            currentItem.runCurrentTool();
                            menu.dispose();
                        }
                    });
                }
                menu.setVisible(true);
            }

        });
    }
}
