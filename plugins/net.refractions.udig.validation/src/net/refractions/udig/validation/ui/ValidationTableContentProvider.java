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
