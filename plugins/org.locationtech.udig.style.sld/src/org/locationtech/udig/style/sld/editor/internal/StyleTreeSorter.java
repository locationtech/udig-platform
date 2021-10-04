/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor.internal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class StyleTreeSorter extends ViewerComparator {

    /**
     * Orders the contents of the tree with respect to their names (the StyleGenerator named them
     * rule01, rule02, so they are in the default order). Alternatively, this method could be
     * changed to actually look at the title of each rule, which is either of the type "1..5" or "1,
     * 2, 3").
     */
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        return 0;
    }
}
