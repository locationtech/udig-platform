package net.refractions.udig.catalog.ui;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;

/**
 * Implementations of this class provide connection information based on
 * context. 
 * <p>
 * The connection information can be in the form of a map of connection 
 * Parameters, or a url, or both.
 * </p>
 * <p>
 * Implementations of this class have two responsibilities. The first is to
 * create a set of connection parameters based on context. The second is to 
 * create a user interface capable of capturing user connection parameters.
 * </p>
 * <p>
 * Implementations of this class are provided via the 
 * net.refractions.udig.catalog.ui.connectionFactory extension point.
 * </p>
 *  
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 *
 */
public abstract class UDIGConnectionFactory {
	
	/** extension point id **/
	public static final String XPID = 
		"net.refractions.udig.catalog.ui.connectionFactory"; //$NON-NLS-1$
	
	protected UDIGConnectionFactoryDescriptor descriptor;
	
	
	/**
	 * Determines if the connection factory is capable of providing some 
	 * connection information based on the context object.
	 * 
	 * @param object The object to be "processed" or "adapted" into connection
	 * information.
	 * 
	 * @return True if the info can be returned based on the context, otherwise
	 * false.
	 */
	public abstract boolean canProcess(Object context);
	
	/**
     * Get the connection parameters based on the provided context.
     * <p>
     * Context is often data from a workbench selection, but does not have to 
     * be.
     * </p>
	 * @param object The object to be "processed" or "adapted" into a map of 
	 * connection parameters.
	 * @return Map of connection parameters, or null if no such parameters could
     * be created.
     */
	public abstract Map<String,Serializable> createConnectionParameters(Object context);
	
	/**
	 * Get a connection url based on the provided context.
	 * <p>
     * Context is often data from a workbench selection, but does not have to 
     * be.
     * </p>
     * @param object The object to be "processed" or "adapted" into a url.
     * 
	 * @return An url, or null if no such url can be created.
	 */
	public abstract URL createConnectionURL(Object context);
	
	/**
	 * Sets the descriptor which describes the connection factory.
	 */
	public void setDescriptor(UDIGConnectionFactoryDescriptor descriptor) {
		this.descriptor = descriptor;
	}
	
	/**
	 * This method returns the wizard page used to capture connection 
	 * parameters. Subclasses may extend, but not override this method.
	 * 
	 * @return A wizard connection page used to capture connection parameters.
	 */
	public UDIGConnectionPage createConnectionPage(int pageIndex) {
		try {
			return descriptor.createConnectionPage(pageIndex);
		}
		catch (CoreException e) {
			CatalogUIPlugin.log(e.getLocalizedMessage(),e);
		}
		
		return null;
	}
    
}