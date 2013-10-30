/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.mapgraphic.northarrow;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.operations.AbstractPropertyValue;
import net.refractions.udig.ui.operations.PropertyValue;

/**
 * Used to enable either tool or operations based on the presence of a value on
 * the style blackboard.
 *   
 * @author Jody Garnett, Refractions Research
 */
public class StyleIsEnabled extends AbstractPropertyValue<Object> implements PropertyValue<Object> {
	/**
	 * Checks that the provide layer contains a value for the provided key
	 * @param object layer
	 * @param ket key to test
	 * @returns true if the associated value is not null
	 */
	public boolean isTrue(Object object, String key) {
		ILayer layer = (ILayer) object;
		return layer.getStyleBlackboard().get( key ) != null;
	}

	/**
	 * There is no point in caching the result, as the style blackboard
	 * is already a cache.
	 */
	public boolean canCacheResult() {
		return false;
	}

	/**
	 * Access to the style blackboard is non blocking.
	 */
	public boolean isBlocking() {
		return false;
	}
}