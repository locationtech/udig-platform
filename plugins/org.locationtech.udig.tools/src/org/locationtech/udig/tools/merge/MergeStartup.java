/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.merge;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.tools.merge.internal.view.MergeView;

/**
 * This class implements the org.eclipse.ui.startup extension point which is used to perform a
 * key-action for the coexistence of the two workflows available for MErgeTool usage ('classic'
 * workflow -> tool activated by using Feature Editing -> Merge tool || new 'operation' workflow ->
 * tool activated using right click in map -> "Operations -> Merge Selected")
 * 
 * The key-action consists in removing (actually hiding) an eventually present MergeView during uDig
 * startup. The MegeView window could be present as left opened by the user in the last uDig
 * shutdown and, so, is reopened in an inconsistent state by Eclipse workbench restoring activities.
 * For the MergeView to operate correctly in each workflow ('classic' and 'operation mode') it must
 * be opened by either the MergeTool or by the MergeOperation classes, through the relative UI-user
 * interacitons.
 * 
 * @author Marco Foi (www.mcfoi.it)
 * 
 */
public class MergeStartup implements IStartup {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    @Override
    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                if (window != null && window.getActivePage() != null) {
                    IViewReference viewRef = window.getActivePage().findViewReference(MergeView.ID);
                    if (viewRef != null) {
                        IViewPart mergeViewPart = viewRef.getView(false);
                        if (mergeViewPart != null) {
                            // If there is an opened MergeView then close it!
                            window.getActivePage().hideView(mergeViewPart);
                        }
                    }
                }
            }
        });
    }
}
