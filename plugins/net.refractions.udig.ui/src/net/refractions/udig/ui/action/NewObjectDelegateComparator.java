/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui.action;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Used to hack the contributions into an expected order.
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class NewObjectDelegateComparator implements Comparator<IConfigurationElement>, Serializable {
    /** long serialVersionUID field */
    private static final long serialVersionUID = 1L;

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( IConfigurationElement arg0, IConfigurationElement arg1 ) {
        String id0 = arg0.getAttribute("id"); //$NON-NLS-1$
        if (id0.equals("net.refractions.udig.project.ui.newLayer")) //$NON-NLS-1$
            return -1;
        String id1 = arg1.getAttribute("id"); //$NON-NLS-1$
        if (id0.equals("net.refractions.udig.project.ui.newMap") && //$NON-NLS-1$
                !id1.equals("net.refractions.udig.project.ui.newLayer")) //$NON-NLS-1$
            return -1;

        return 1;
    }

}