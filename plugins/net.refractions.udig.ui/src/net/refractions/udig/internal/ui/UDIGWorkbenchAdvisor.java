/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.internal.ui;

import net.refractions.udig.ui.preferences.PreferenceConstants;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Public base class for configuring the UDIG workbench.
 * <p>
 * Note that the workbench advisor object is created in advance of creating the workbench. However,
 * by the time the workbench starts calling methods on this class,
 * <code>PlatformUI.getWorkbench</code> is guaranteed to have been properly initialized.
 * <p>
 * If you wanted to perform any system startup checks please
 * look into overriding the UDIGApplication or UDIGStartup in addition to this class.
 * <p>
 * Most of the structure of the UDIGApplication is dictated by the Eclipse User Interface
 * Guidelines; we have had to supplement this advice in a few places in order to document the
 * conventions we are following when working with spatial content.
 * <p>
 * If you are used to Eclipse RCP development you fill find that everything is normal; our main
 * Editor (ie MapEditor) is *modal* and changes what it is up to based on the current tool. This
 * results in the contents of the Edit menu changing a bit more often then what you are used to when
 * working with a Text editor (where the only kind of content is selected text).
 * 
 * @author Richar Gould, Refractions Research
 * @version $Revision: 1.9 $
 */
public class UDIGWorkbenchAdvisor extends WorkbenchAdvisor {
    
    @Override
    public void initialize( IWorkbenchConfigurer configurer ) {
        super.initialize(configurer);

        // make sure we always save and restore workbench state
        configurer.setSaveAndRestore(true);
    }

    @Override
    public String getInitialWindowPerspectiveId() {
        IPreferenceStore preferenceStore = UiPlugin.getDefault().getPreferenceStore();
		String perspective = preferenceStore.getString(PreferenceConstants.P_DEFAULT_PERSPECTIVE);
		if( perspective==null || perspective.trim().length()==0 )
			return MapPerspective.ID_PERSPECTIVE;
	
		return perspective;
    }
    
    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new UDIGWorkbenchWindowAdvisor(configurer);
    }
    

}
