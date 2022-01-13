/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal;

import static org.locationtech.udig.issues.internal.PreferenceConstants.KEY_ACTIVE_LIST;
import static org.locationtech.udig.issues.internal.PreferenceConstants.KEY_VIEW_CONTENT_PROVIDER;
import static org.locationtech.udig.issues.internal.PreferenceConstants.KEY_VIEW_EXPANSION_PROVIDER;
import static org.locationtech.udig.issues.internal.PreferenceConstants.KEY_VIEW_SORTER;
import static org.locationtech.udig.issues.internal.PreferenceConstants.VALUE_MEMORY_LIST;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.locationtech.udig.core.logging.LoggingSupport;

/**
 * Initialize preferences
 *
 * @author Jesse
 * @since 1.1.0
 */
public class IssuesPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore preferenceStore = IssuesActivator.getDefault().getPreferenceStore();

        preferenceStore.setDefault(KEY_VIEW_CONTENT_PROVIDER, ""); //$NON-NLS-1$
        preferenceStore.setDefault(KEY_VIEW_EXPANSION_PROVIDER, ""); //$NON-NLS-1$
        preferenceStore.setDefault(KEY_VIEW_SORTER, ""); //$NON-NLS-1$

        URL url=IssuesActivator.getDefault().getBundle().getEntry(".config"); //$NON-NLS-1$

        InputStream in = null;
        InputStreamReader reader = null;
        try {
            in = new BufferedInputStream(url.openStream());
            reader = new InputStreamReader(in);
            StringBuffer buffer=new StringBuffer();
            int read;
            String last;
            do{
                read=reader.read();
                last=String.valueOf((char)read);
                buffer.append(last);
            }while( read!=-1 && last.length()>0 && last.matches("\\S")); //$NON-NLS-1$
            String substring = buffer.substring(0, buffer.length()-1);
            preferenceStore.setDefault(KEY_ACTIVE_LIST, substring);
        } catch (IOException e) {
            LoggingSupport.log(IssuesActivator.getDefault(), e);
            preferenceStore.setDefault(KEY_ACTIVE_LIST, VALUE_MEMORY_LIST);
        }finally{
            try {
                if( in!=null )
                in.close();
                if( reader!=null)
                    reader.close();
            } catch (IOException e) {
                LoggingSupport.log(IssuesActivator.getDefault(), e);
            }
        }
    }

}
