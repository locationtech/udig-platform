/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * A content provider that the issues view can use to determine the issues it displays.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IIssuesContentProvider extends ITreeContentProvider{

    /**
     * Returns the extension id so that the system can instantiate the 
     * sorter again in the future after the workbench has been shutdown.
     * 
     * @return pluginID.extensionid.
     */
    String getExtensionID();

}
