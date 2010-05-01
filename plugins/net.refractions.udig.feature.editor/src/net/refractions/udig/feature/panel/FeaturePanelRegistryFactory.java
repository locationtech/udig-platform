/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2007 IBM Corporation and others
 * ------
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * --------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.refractions.udig.feature.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * tabbed property registry factory. Caches the tabbed property registry by
 * tabbed property contributor ID.
 * 
 * @author Anthony Hunter
 */
public class FeaturePanelRegistryFactory {

	class CacheData {
	    FeaturePanelRegistry registry;
		List references;
	}

	/**
	 * singleton instance of this class
	 */
	private static FeaturePanelRegistryFactory INSTANCE = new FeaturePanelRegistryFactory();

	/**
	 * get the singleton instance of this class.
	 * 
	 * @return the FeaturePanelRegistryFactory instance.
	 */
	public static FeaturePanelRegistryFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * private constructor.
	 */
	private FeaturePanelRegistryFactory() {
		super();
		idToCacheData = new HashMap();
	}

	protected Map<SimpleFeatureType,CacheData> idToCacheData; // cache

	/**
	 * Creates a registry for the given contributor.
	 * 
	 * @param target
	 *            the contributor.
	 * @return a registry for the given contributor.
	 */
	public FeaturePanelRegistry createRegistry(
			FeaturePanelPageContributor target) {
		/**
		 * Get the contributor id from the ITabbedPropertySheetPageContributor
		 * interface
		 */
		SimpleFeatureType key = target.getSchema();
		CacheData data = (CacheData) idToCacheData.get(key);
		if (data == null) {
			data = new CacheData();
			data.registry = new FeaturePanelRegistry(key);
			data.references = new ArrayList(5);
			idToCacheData.put(key, data);
		}
		data.references.add(target);
		// keeps track of contributor using the same registry
		return data.registry;
	}

	/**
	 * Indicates that the given contributor no longer needs a registry. The
	 * registry will be disposed when no other contributor of the same type
	 * needs it.
	 * 
	 * @param target
	 *            the contributor;
	 */
	public void disposeRegistry(FeaturePanelPageContributor target) {
		/**
		 * Get the contributor id from the ITabbedPropertySheetPageContributor
		 * interface
		 */
		SimpleFeatureType key = target.getSchema();
		CacheData data = (CacheData) idToCacheData.get(key);
		if (data != null) {
			data.references.remove(target);
			if (data.references.isEmpty()) {
				idToCacheData.remove(key);
			}
		}
	}
}
