/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.validation.ui;
   
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.geotools.validation.dto.ArgumentDTO;
import org.geotools.validation.dto.TestDTO;

    /**
     * Content provider for representing launch configuration types & launch configurations in a tree.
     * 
     */
    public class ValidationTableContentProvider implements IStructuredContentProvider{

    	private static final Object[] EMPTY_ARRAY = new Object[0];  

		public ValidationTableContentProvider() {
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof TestDTO) {
				TestDTO test = (TestDTO) inputElement;
				Set argumentSet = new HashSet();
				Map args = test.getArgs();
				//add each argument to the set
				for (Iterator i = args.keySet().iterator(); i.hasNext();) {
					ArgumentDTO currentArg = (ArgumentDTO) args.get(i.next());
					argumentSet.add(currentArg);
				}
				return argumentSet.toArray();
			}
			return EMPTY_ARRAY;
		}
}
