/**
 * 
 */
package net.refractions.udig.project.internal.ui.wizard;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.ui.UDIGConnectionFactory;

/**
 * @author jeichar
 *
 */
public class URLConnectionFactory extends UDIGConnectionFactory {

	/**
	 * @see net.refractions.udig.catalog.ui.UDIGConnectionFactory#canProcess(java.lang.Object)
	 */
	@Override
	public boolean canProcess(Object context) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.refractions.udig.catalog.ui.UDIGConnectionFactory#createConnectionParameters(java.lang.Object)
	 */
	@Override
	public Map<String, Serializable> createConnectionParameters(Object context) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.refractions.udig.catalog.ui.UDIGConnectionFactory#createConnectionURL(java.lang.Object)
	 */
	@Override
	public URL createConnectionURL(Object context) {
		if ( context instanceof URL)
			return (URL) context;
		return null;
	}

}
