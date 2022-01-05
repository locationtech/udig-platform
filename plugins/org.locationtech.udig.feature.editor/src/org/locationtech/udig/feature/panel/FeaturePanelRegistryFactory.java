/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2008 IBM Corporation and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 * Contributors:
 *     Refractions Research - adapt to Feature Model
 *     IBM Corporation - initial API and implementation
 */
/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.locationtech.udig.feature.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Tabbed property registry factory. Caches the tabbed property registry by tabbed property
 * contributor ID.
 *
 * @author Jody Garnett
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

    protected Map<SimpleFeatureType, CacheData> idToCacheData; // cache

    /**
     * Creates a registry for the given contributor.
     *
     * @param target the contributor.
     * @return a registry for the given contributor.
     */
    public FeaturePanelRegistry createRegistry(FeaturePanelPageContributor target) {
        /**
         * Get the contributor id from the ITabbedPropertySheetPageContributor interface
         */
        SimpleFeatureType key = target.getSchema();
        CacheData data = idToCacheData.get(key);
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
     * Indicates that the given contributor no longer needs a registry. The registry will be
     * disposed when no other contributor of the same type needs it.
     *
     * @param target the contributor;
     */
    public void disposeRegistry(FeaturePanelPageContributor target) {
        /**
         * Get the contributor id from the ITabbedPropertySheetPageContributor interface
         */
        SimpleFeatureType key = target.getSchema();
        CacheData data = idToCacheData.get(key);
        if (data != null) {
            data.references.remove(target);
            if (data.references.isEmpty()) {
                idToCacheData.remove(key);
            }
        }
    }
}
