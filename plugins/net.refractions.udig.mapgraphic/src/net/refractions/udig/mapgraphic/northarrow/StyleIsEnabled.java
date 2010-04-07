/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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