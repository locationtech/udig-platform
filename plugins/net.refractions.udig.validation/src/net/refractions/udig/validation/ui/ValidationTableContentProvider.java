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
