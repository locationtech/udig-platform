/**
 * 
 */
package org.locationtech.udig.project.internal.ui.wizard;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import org.locationtech.udig.catalog.ui.UDIGConnectionFactory;

/**
 * @author jeichar
 *
 */
public class URLConnectionFactory extends UDIGConnectionFactory {

	/**
	 * @see org.locationtech.udig.catalog.ui.UDIGConnectionFactory#canProcess(java.lang.Object)
	 */
	@Override
	public boolean canProcess(Object context) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see org.locationtech.udig.catalog.ui.UDIGConnectionFactory#createConnectionParameters(java.lang.Object)
	 */
	@Override
	public Map<String, Serializable> createConnectionParameters(Object context) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.locationtech.udig.catalog.ui.UDIGConnectionFactory#createConnectionURL(java.lang.Object)
	 */
	@Override
	public URL createConnectionURL(Object context) {
		if ( context instanceof URL)
			return (URL) context;
		return null;
	}

}
