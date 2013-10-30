/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package eu.udig.image.georeferencing.internal.ui;

import net.refractions.udig.project.ui.tool.IToolContext;

/**
 * Interface implemented by all the composites.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
public interface GeoReferencingComposite {

	/**
	 * Used to set the context to all the composite presenters.
	 * 
	 * @param newContext
	 *            ItoolContext.
	 */
	public void setContext(IToolContext newContext);

}
