/**
 * 
 */
package net.refractions.udig.internal.limit;

import net.refractions.udig.limit.ILimitService;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Responsible for creating our internal LimitServiceImpl.
 * 
 * @author pfeiffp
 */
public class LimitServiceFactory extends AbstractServiceFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.services.AbstractServiceFactory#create(java.lang.Class, org.eclipse.ui.services.IServiceLocator, org.eclipse.ui.services.IServiceLocator)
	 */
	@Override
	public ILimitService create(Class serviceInterface, IServiceLocator parentLocator,
			IServiceLocator locator) {
		
		if (ILimitService.class.equals(serviceInterface)) {
			return new LimitServiceImpl();
		}
		
		return null;
	
	}

}
