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
		
		

/*
		IWorkbenchLocationService wls = (IWorkbenchLocationService) locator
				.getService(IWorkbenchLocationService.class);
		final IWorkbench wb = wls.getWorkbench();
		if (wb == null) {
			return null;
		}
		final IWorkbenchWindow window = wls.getWorkbenchWindow();
		final IWorkbenchPartSite site = wls.getPartSite();
		Object parent = parentLocator.getService(serviceInterface);

		if (parent == null) {
			
			// return top level services
			if (IProgressService.class.equals(serviceInterface)) {
				return wb.getProgressService();
			}
			if (IWorkbenchSiteProgressService.class.equals(serviceInterface)) {
				if (site instanceof PartSite) {
					return ((PartSite) site).getSiteProgressService();
				}
			}
			if (IPartService.class.equals(serviceInterface)) {
				if (window != null) {
					return window.getPartService();
				}
			}
			if (IPageService.class.equals(serviceInterface)) {
				if (window != null) {
					return window;
				}
			}
			if (ISelectionService.class.equals(serviceInterface)) {
				if (window != null) {
					return window.getSelectionService();
				}
			}
			return null;
		}
		
		

		if (ISelectionService.class.equals(serviceInterface)) {
			if (parent instanceof WindowSelectionService && window != null
					&& window.getActivePage() != null) {
				return new SlaveSelectionService(window.getActivePage());
			}
			return new SlaveSelectionService((ISelectionService) parent);
		}

		if (IProgressService.class.equals(serviceInterface)) {
			if (site instanceof PartSite) {
				return ((PartSite) site).getSiteProgressService();
			}
		}
		if (IPartService.class.equals(serviceInterface)) {
			return new SlavePartService((IPartService) parent);
		}
		if (IPageService.class.equals(serviceInterface)) {
			return new SlavePageService((IPageService) parent);
		}*/

		
		return null;
	
	}

}
