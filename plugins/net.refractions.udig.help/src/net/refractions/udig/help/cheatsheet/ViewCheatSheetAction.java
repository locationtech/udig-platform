/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.help.cheatsheet;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

/**
 * TODO Purpose of
 * <p>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * 
 * @author leviputna
 * @since 1.2.0
 */
public class ViewCheatSheetAction extends Action implements ICheatSheetAction {

    private String VIEW_ID;

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
     * org.eclipse.ui.cheatsheets.ICheatSheetManager)
     */
    public void run( String[] params, ICheatSheetManager manager ) {
        VIEW_ID = params[0];

        if (VIEW_ID.isEmpty() || VIEW_ID == null) {
            return;
        }

        final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();

        try {
            workbenchWindow.getActivePage().showView(VIEW_ID);
        } catch (PartInitException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
