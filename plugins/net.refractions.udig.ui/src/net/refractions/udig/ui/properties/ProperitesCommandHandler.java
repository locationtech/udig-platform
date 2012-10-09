/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.ui.properties;

import net.refractions.udig.core.SelectionProviderForwarder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Used to pop open a {@link PreferenceDialog} taking care to filter the
 * current selection the indicated forward type.
 * <p>
 * This is intended to be extended as a stand alone command handler:<pre>
 * public class MapPropertiesCommandHandler extends ProperitesCommandHandler {
 *    public MapPropertiesCommandHandler(){
 *        super(IMap.class);
 *    }
 * }
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * @author jody
 * @since 1.2.0
 */
public class ProperitesCommandHandler extends AbstractHandler {
    private Class<?> forwardType;

    public ProperitesCommandHandler(Class<?> fowardType) {
        this.forwardType = fowardType;
    }
    public Object execute(ExecutionEvent event) throws ExecutionException {

        final IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);

        IShellProvider shellProvider = new IShellProvider() {
            public Shell getShell() {
                return new Shell(activeWorkbenchWindow.getShell());
            }
        };

        IWorkbenchPart activePart = activeWorkbenchWindow.getActivePage().getActivePart();
        ISelectionProvider provider = activePart.getSite().getSelectionProvider();

        ISelectionProvider selectionProvider = new SelectionProviderForwarder(provider,forwardType);

        PropertyDialogAction action = new PropertyDialogAction(shellProvider, selectionProvider);
        PreferenceDialog dialog = action.createDialog();
        dialog.open();

        return null;
    }

}