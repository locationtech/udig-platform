/**
 * 
 */
package net.refractions.udig.catalog.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * Provides access to the connection Factories and their wizard pages.
 * 
 * @author jeichar
 */
public class ConnectionFactoryManager {
	Map<Descriptor<UDIGConnectionFactory>, List<Descriptor<UDIGConnectionPage>>> factoryToPage=
		new HashMap<Descriptor<UDIGConnectionFactory>, List<Descriptor<UDIGConnectionPage>>>();
	Map<List<Descriptor<UDIGConnectionPage>>, Descriptor<UDIGConnectionFactory>> pageToFactory=
		new HashMap<List<Descriptor<UDIGConnectionPage>>, Descriptor<UDIGConnectionFactory>>();
	List<UDIGConnectionFactoryDescriptor> descriptors;
	private static ConnectionFactoryManager manager;
	
	protected ConnectionFactoryManager() {
		IExtension[] extension = Platform.getExtensionRegistry().getExtensionPoint(UDIGConnectionFactory.XPID).getExtensions();
		
		Descriptor<UDIGConnectionFactory> factory=null;
		List<Descriptor<UDIGConnectionPage>> wizardPages=new ArrayList<Descriptor<UDIGConnectionPage>>();
		
		for (IExtension e : extension) {
            IConfigurationElement[] elements = e.getConfigurationElements();
			wizardPages = new ArrayList<Descriptor<UDIGConnectionPage>>();
            factory=null;
			for (IConfigurationElement element : elements) {
				if ("factory".equals(element.getName())) { //$NON-NLS-1$
					factory = new Descriptor<UDIGConnectionFactory>("class", element); //$NON-NLS-1$
				}
				if ("wizardPage".equals(element.getName() )){ //$NON-NLS-1$ 
					wizardPages.add(new Descriptor<UDIGConnectionPage>("class", element)); //$NON-NLS-1$
                }
			}
			if( factory!=null )
				factoryToPage.put(factory, wizardPages);
			if( factory!=null )
				pageToFactory.put( wizardPages, factory );
		}
	}
	
	public static synchronized ConnectionFactoryManager instance(){
		if (manager == null) {
			manager = new ConnectionFactoryManager();
		}

		return manager;
	}

	public List<Descriptor<UDIGConnectionPage>> getPageDescriptor(Descriptor<UDIGConnectionFactory> factory) throws CoreException{
		return factoryToPage.get(factory);
	}
		
//	public Descriptor<UDIGConnectionFactory> getFactoryDescriptor(List<Descriptor<UDIGConnectionPage>> page) throws CoreException{
//		return pageToFactory.get(page);
//	}
	
	public Collection<List<Descriptor<UDIGConnectionPage>>> getPages(){
		return factoryToPage.values();
	}
	
	public Collection<Descriptor<UDIGConnectionFactory>> getFactories(){
		return factoryToPage.keySet();
	}

	public synchronized List<UDIGConnectionFactoryDescriptor> getConnectionFactoryDescriptors() {
		if (descriptors == null) {
			descriptors = new ArrayList<UDIGConnectionFactoryDescriptor>();
			Collection<Descriptor<UDIGConnectionFactory>> factories = getFactories();
			for (Descriptor<UDIGConnectionFactory> factoryDescriptor : factories) {
				try{
					if( !getPageDescriptor(factoryDescriptor).isEmpty() )
						descriptors.add(new UDIGConnectionFactoryDescriptor(factoryDescriptor));
				}catch (CoreException e) {
					CatalogUIPlugin.log("", e); //$NON-NLS-1$
				}
			}
			Collections.sort(descriptors, new Comparator<UDIGConnectionFactoryDescriptor>() {
	            public int compare(UDIGConnectionFactoryDescriptor o1, UDIGConnectionFactoryDescriptor o2) {
	                String s1 = o1.getLabel(0);
	                String s2 = o2.getLabel(0);
	                return s1.compareTo(s2);
	            }
	        });
		}

		return descriptors;
	}
	
	/**
	 * Gets the UDIGConnectionFactoryDescriptors that match the ids in the list
	 */
	public List<UDIGConnectionFactoryDescriptor> getConnectionFactoryDescriptors(List<String> ids) {
		List<UDIGConnectionFactoryDescriptor> tmp = instance()
				.getConnectionFactoryDescriptors();
		List<UDIGConnectionFactoryDescriptor> result=new ArrayList<UDIGConnectionFactoryDescriptor>();
		for (UDIGConnectionFactoryDescriptor descriptor : tmp) {
			for (String id : ids) {
				if (id.equals(descriptor.getId()))
					result.add(descriptor);
			}
		}
		return result;
	}

	/**
	 * Provides lazy loading for a class declared in an extension
	 * 
	 * @author jeichar
	 *
	 * @param <T> The class type that will be created
	 */
	public static class Descriptor<T>{
		private IConfigurationElement element;
		private String classAttribute;
		private T instance;
		/**
		 * Creates a new Descriptor
		 * @param classAttribute the attribute name of the element that is to be create
		 */
		Descriptor(String classAttribute, IConfigurationElement element){
			this.classAttribute=classAttribute;
			this.element=element;
		}
		
		@SuppressWarnings("unchecked") 
		public synchronized T getConcreteInstance() throws CoreException{
			if( instance==null ){
			    String toLoad = element.getAttribute( classAttribute );
			    try {
			        instance=(T) element.createExecutableExtension( classAttribute );
			    }
			    catch( CoreException eek){			        
			        throw eek;
			    }
			}
			return instance;
		}
		
		public IConfigurationElement getConfigurationElement(){
			return element;
		}
	}
}
