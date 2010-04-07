package net.refractions.udig.validation.ui;
   
import net.refractions.udig.validation.ValidationProcessor;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.geotools.validation.dto.PlugInDTO;
import org.geotools.validation.dto.TestDTO;

    /**
     * Content provider for representing launch configuration types & launch configurations in a tree.
     * 
     */
    public class ValidationTreeContentProvider implements ITreeContentProvider { //, ICheckable {

        /**
         * Empty Object array
         */
        private static final Object[] EMPTY_ARRAY = new Object[0];  
        
        /**
         * The Shell context
         */
		private ValidationProcessor validationProcessor;
        
        public ValidationTreeContentProvider() {
        }

        /**
         * Returns the children of the element:
         * - for a ValidationProcessor, returns the list of available validations (plugins)
         * - for a validation plugin, returns a list of configured validation tests
         */
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof ValidationProcessor) {
                // this is the root of our tree; at this point we want to return
				// the list of all available plugins as children
                ValidationProcessor processor = (ValidationProcessor) parentElement;
                return processor.getPlugins().toArray();
            } else if (parentElement instanceof PlugInDTO) {
            	// this is a plugin (validation test), so we'll grab instances of
				// each test for this type
        		return validationProcessor.getTests(parentElement);
            }
            return EMPTY_ARRAY;
        }

        /**
         * Return the available elements.
         * 
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements(Object inputElement) {
        	return getChildren(inputElement);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        	if( newInput == oldInput ) return;
        	ValidationProcessor newProcessor = (ValidationProcessor) newInput;
        	validationProcessor = newProcessor;        	
        }

		public Object getParent(Object element) {
            if (element instanceof TestDTO) {
                TestDTO test = (TestDTO) element;
                return test.getPlugIn();
            }
			return null;
		}

		public boolean hasChildren(Object element) {
            if (element instanceof TestDTO) {
            	return false;
            } else if (element instanceof PlugInDTO) {
            	//count tests for this plugin
            	int count = validationProcessor.getTests(element).length;
            	if (count > 0) return true;
            	else return false;
            }
			return false;
		}
}
