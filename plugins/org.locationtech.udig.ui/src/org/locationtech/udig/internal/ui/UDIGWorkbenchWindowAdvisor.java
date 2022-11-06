/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.locationtech.udig.core.internal.CorePlugin;
import org.locationtech.udig.ui.UDIGDragDropUtilities;
import org.locationtech.udig.ui.WorkbenchConfiguration;
import org.locationtech.udig.ui.preferences.PreferenceConstants;
import org.osgi.service.prefs.Preferences;

/**
 * Public base class for configuring a workbench window.
 * <p>
 * The workbench window advisor object is created in response to a workbench window being created
 * (one per window), and is used to configure the window.
 * </p>
 * <p>
 * An application should declare a subclass of <code>WorkbenchWindowAdvisor</code> and override
 * methods to configure workbench windows to suit the needs of the particular application.
 * </p>
 * <p>
 * The following advisor methods are called at strategic points in the workbench window's lifecycle
 * (as with the workbench advisor, all occur within the dynamic scope of the call to
 * {@link PlatformUI#createAndRunWorkbench PlatformUI.createAndRunWorkbench}):
 * <ul>
 * <li><code>preWindowOpen</code> - called as the window is being opened; use to configure aspects
 * of the window other than actions bars</li>
 * <li><code>postWindowRestore</code> - called after the window has been recreated from a previously
 * saved state; use to adjust the restored window</li>
 * <li><code>postWindowCreate</code> - called after the window has been created, either from an
 * initial state or from a restored state; used to adjust the window</li>
 * <li><code>openIntro</code> - called immediately before the window is opened in order to create
 * the introduction component, if any</li>
 * <li><code>postWindowOpen</code> - called after the window has been opened; use to hook window
 * listeners, etc.</li>
 * <li><code>preWindowShellClose</code> - called when the window's shell is closed by the user; use
 * to pre-screen window closings</li>
 * </ul>
 * </p>
 *
 * @author cole.markham
 * @since 1.0.0
 */
public class UDIGWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    /**
     * Constructor
     *
     * @param configurer
     */
    public UDIGWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    @Override
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        WorkbenchConfiguration configuration = lookupConfiguration();
        configuration.configureWorkbench(configurer);

        UDIGDragDropUtilities.registerUDigDND(configurer);
    }

    /**
     * Look up configuration object, using UDIGWorkbenchConfiguration as a default.
     *
     * @return WorkbenchConfiguration from preferences, or UDIGWorkbenchConfiguration if not found
     */
    private WorkbenchConfiguration lookupConfiguration() {

        Class<WorkbenchConfiguration> interfaceClass = WorkbenchConfiguration.class;
        String prefConstant = PreferenceConstants.P_WORKBENCH_CONFIGURATION;
        String xpid = WorkbenchConfiguration.XPID;
        String idField = WorkbenchConfiguration.ATTR_ID;
        String classField = WorkbenchConfiguration.ATTR_CLASS;

        WorkbenchConfiguration config = (WorkbenchConfiguration) UiPlugin.lookupConfigurationObject(
                interfaceClass, UiPlugin.getDefault().getPreferenceStore(), UiPlugin.ID,
                prefConstant, xpid, idField, classField);
        if (config == null) {
            return new UDIGWorkbenchConfiguration();
        }
        return config;
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new UDIGActionBarAdvisor(configurer);
    }

    @Override
    public void postWindowOpen() {
        super.postWindowOpen();
        try {
            Preferences userPreferences = UiPlugin.getUserPreferences();
            if (!userPreferences.nodeExists("org.locationtech.udig.ui.firstRun")) { //$NON-NLS-1$
                firstRun();
            } else {
                showTip();
            }
        } catch (Exception e) {
            UiPlugin.log("", e); //$NON-NLS-1$
        }
    }

    private void showTip() {

        try {
            IPreferenceStore store = UiPlugin.getDefault().getPreferenceStore();
            if (store.getBoolean(PreferenceConstants.P_SHOW_TIPS) && TipDialog.hasTips()
                    && !CorePlugin.isDeveloping()) {
                TipDialog dialog = new TipDialog(this.getWindowConfigurer().getWindow().getShell());
                dialog.setBlockOnOpen(false);
                dialog.open();
            }
        } catch (Exception e) {
            UiPlugin.log("", e); //$NON-NLS-1$
        }
    }

    private void firstRun() throws Exception {
        Preferences userPreferences = UiPlugin.getUserPreferences();
        userPreferences.node("org.locationtech.udig.ui.firstRun") //$NON-NLS-1$
                .putBoolean("org.locationtech.udig.ui.isFirstRun", false); //$NON-NLS-1$
    }
}
