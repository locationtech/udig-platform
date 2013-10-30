/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.interceptor;

import org.opengis.feature.Feature;

/**
 * Used to process a feature.
 * 
 * @author Jody
 * @since 1.2.0
 */
public interface FeatureInterceptor {
    /**
     * Extension Point ID of feature interceptors
     */
    String EXTENSION_ID = "org.locationtech.udig.project.featureInterceptor"; //$NON-NLS-1$
    
    /**
     * Attribute name of layer created interceptors
     */
    String CREATED_ID = "featureCreated"; //$NON-NLS-1$

    /**
     * Performs an action on a feature.
     * <p>
     * You can look up the current EditLayer from the EditManager if you
     * want to check out what is going on. Chances are the provided feature
     * is adaptable and can adapt to the current layer anyways.
     * <p>
     * @param feature 
     */
    public void run(Feature feature);
}
