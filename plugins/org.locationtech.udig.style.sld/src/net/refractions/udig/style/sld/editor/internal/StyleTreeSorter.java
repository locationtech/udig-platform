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
package net.refractions.udig.style.sld.editor.internal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class StyleTreeSorter extends ViewerSorter {
	
	/**
	 * Orders the contents of the tree with respect to their names (the
	 * StyleGenerator named them rule01, rule02, so they are in the default
	 * order). Alternatively, this method could be changed to actually look at
	 * the title of each rule, which is either of the type "1..5" or "1, 2, 3").
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
        return 0;
//		int cat1 = category(e1);
//        int cat2 = category(e2);
//
//        if (cat1 != cat2)
//            return cat1 - cat2;
//
//        // cat1 == cat2
//        String name1 = null;
//        String name2 = null;
//
//        if (e1 instanceof Rule) {
//        	name1 = ((Rule) e1).getName();
//        }
//        if (e2 instanceof Rule) {
//        	name2 = ((Rule) e2).getName();
//        }
//        
//        if (name1 == null)
//            name1 = "";//$NON-NLS-1$
//        if (name2 == null)
//            name2 = "";//$NON-NLS-1$
//        
//        if (name1.startsWith("rule") && name2.startsWith("rule")) {
//            return collator.compare(name1, name2);
//        } else {
//            return 0; //pretend they are equal (don't mess with non-rules)
//        }
	}
}
