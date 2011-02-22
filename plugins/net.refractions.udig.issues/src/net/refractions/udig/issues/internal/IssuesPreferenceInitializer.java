/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.issues.internal;

import static net.refractions.udig.issues.internal.PreferenceConstants.KEY_ACTIVE_LIST;
import static net.refractions.udig.issues.internal.PreferenceConstants.KEY_VIEW_CONTENT_PROVIDER;
import static net.refractions.udig.issues.internal.PreferenceConstants.KEY_VIEW_EXPANSION_PROVIDER;
import static net.refractions.udig.issues.internal.PreferenceConstants.KEY_VIEW_SORTER;
import static net.refractions.udig.issues.internal.PreferenceConstants.VALUE_MEMORY_LIST;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

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
            IssuesActivator.log("", e); //$NON-NLS-1$
            preferenceStore.setDefault(KEY_ACTIVE_LIST, VALUE_MEMORY_LIST);
        }finally{
            try {
                if( in!=null )
                in.close();
                if( reader!=null)
                    reader.close();
            } catch (IOException e) {
                IssuesActivator.log("", e); //$NON-NLS-1$
            }
        }
    }

}
