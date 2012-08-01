/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package eu.udig.tools.merge;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import eu.udig.tools.merge.internal.view.MergeView;

/**
 * @author Marco Foi
 * 
 */
public class MergeStartup implements IStartup {

    /**
     * 
     */
    public MergeStartup() {
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    @Override
    public void earlyStartup() {
        IWorkbench wb = PlatformUI.getWorkbench();
        // IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        IWorkbenchWindow[] winArray = wb.getWorkbenchWindows();
        final IWorkbenchPage page = winArray[0].getActivePage();
        // IWorkbenchPage page = win.getActivePage();
        final IViewReference viewRef = page.findViewReference(MergeView.ID);
        // If there is an opened MergeView then close it!
        if (viewRef != null) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    page.hideView(viewRef.getView(false));
                }
            });
        }
    }
}
