/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
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
