package net.refractions.udig.ui.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.internal.ui.ImageConstants;
import net.refractions.udig.internal.ui.Images;
import net.refractions.udig.internal.ui.UiPlugin;

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

/**
 * Contribution item that will add the "NewObjectDelegate" actions to a drop down button
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class NewObjectContribution extends ContributionItem {
    public static final String NEW_ACTION_ID = "net.refractions.udig.ui.newObjectAction"; //$NON-NLS-1$

    
    private ArrayList<NewObjectDelegate> newItems;
    NewObjectDelegate current;
    /**
     * Construct <code>UDIGActionBarAdvisor.NewContribution</code>.
     * 
     * @param window The window this action will operate in.
     */
    public NewObjectContribution( IWorkbenchWindow window ) {
        List<IConfigurationElement> extensions = ExtensionPointList
                .getExtensionPointList(NEW_ACTION_ID);
        Collections.sort(extensions, new NewObjectDelegateComparator());
        newItems = new ArrayList<NewObjectDelegate>();
        for( IConfigurationElement element : extensions ) {
            newItems.add(new NewObjectDelegate(element, window));
        }
        if (newItems.size() > 0)
            current = newItems.get(0);
    }
    /**
     * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.ToolBar, int)
     */
    public void fill( final ToolBar parent, int index ) {
        ToolItem item = new ToolItem(parent, SWT.DROP_DOWN, index);
        item.setImage(Images.get(ImageConstants.NEW_WIZ));
        item.addSelectionListener(new SelectionListener(){

            protected ImageRegistry registry;
            public void widgetSelected( SelectionEvent e ) {
                widgetDefaultSelected(e);
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                if (e.detail == SWT.ARROW) {
                    Menu menu = new Menu(parent);
                    registry = UiPlugin.getDefault().getImageRegistry();
                    for( final NewObjectDelegate newItem : newItems ) {
                        MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
                        if (newItem.text != null)
                            menuItem.setText(newItem.text);
                        menuItem.setImage(getImage(newItem.id, newItem.icon));
                        menuItem.addSelectionListener(new SelectionListener(){

                            public void widgetSelected( SelectionEvent se ) {
                                widgetDefaultSelected(se);
                            }

                            public void widgetDefaultSelected( SelectionEvent se ) {
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

            private Image getImage( String id, ImageDescriptor descriptor ) {
                if (registry.get(id) == null)
                    registry.put(id, descriptor);
                return registry.get(id);
            }

        });
    }

}