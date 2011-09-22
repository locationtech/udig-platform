/**
 * 
 */
package net.refractions.udig.internal.boundary;

import net.refractions.udig.boundary.IBoundaryService;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Responsible for creating our internal BoundaryServiceImpl.
 * 
 * @author pfeiffp
 */
public class BoundaryServiceFactory extends AbstractServiceFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.services.AbstractServiceFactory#create(java.lang.Class, org.eclipse.ui.services.IServiceLocator, org.eclipse.ui.services.IServiceLocator)
	 */
	@Override
	public IBoundaryService create(Class serviceInterface, IServiceLocator parentLocator,
			IServiceLocator locator) {
		
		if (IBoundaryService.class.equals(serviceInterface)) {
			return new BoundaryServiceImpl();
		}
		
		return null;
	
	}

}
