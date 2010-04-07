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