/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.internal.ui.ImageConstants;
import org.locationtech.udig.internal.ui.UiPlugin;

/**
 * Contribution item that will add the "NewObjectDelegate" actions to a drop down button
 *
 * @author jeichar
 * @since 0.6.0
 */
public class NewObjectContribution extends ContributionItem {
    public static final String NEW_ACTION_ID = "org.locationtech.udig.ui.newObjectAction"; //$NON-NLS-1$

    private ArrayList<NewObjectDelegate> newItems;

    NewObjectDelegate current;

    /**
     * Construct <code>UDIGActionBarAdvisor.NewContribution</code>.
     *
     * @param window The window this action will operate in.
     */
    public NewObjectContribution(IWorkbenchWindow window) {
        List<IConfigurationElement> extensions = ExtensionPointList
                .getExtensionPointList(NEW_ACTION_ID);
        Collections.sort(extensions, new NewObjectDelegateComparator());
        newItems = new ArrayList<>();
        for (IConfigurationElement element : extensions) {
            newItems.add(new NewObjectDelegate(element, window));
        }
        if (!newItems.isEmpty()) {
            current = newItems.get(0);
        }
    }

    @Override
    public void fill(final ToolBar parent, int index) {
        ToolItem item = new ToolItem(parent, SWT.DROP_DOWN, index);
        item.setImage(UiPlugin.getDefault().getImage(ImageConstants.NEW_WIZ));
        item.addSelectionListener(new SelectionListener() {

            protected ImageRegistry registry;

            @Override
            public void widgetSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                if (e.detail == SWT.ARROW) {
                    Menu menu = new Menu(parent);
                    registry = UiPlugin.getDefault().getImageRegistry();
                    for (final NewObjectDelegate newItem : newItems) {
                        MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
                        if (newItem.text != null) {
                            menuItem.setText(newItem.text);
                        }
                        menuItem.setImage(getImage(newItem.id, newItem.icon));
                        menuItem.addSelectionListener(new SelectionListener() {

                            @Override
                            public void widgetSelected(SelectionEvent se) {
                                widgetDefaultSelected(se);
                            }

                            @Override
                            public void widgetDefaultSelected(SelectionEvent se) {
                                current = newItem;
                                newItem.runAction();
                            }

                        });
                    }
                    menu.setVisible(true);
                } else {
                    if (current != null)
                        current.runAction();
                }
            }

            private Image getImage(String id, ImageDescriptor descriptor) {
                if (registry.get(id) == null)
                    registry.put(id, descriptor);
                return registry.get(id);
            }

        });
    }

}
