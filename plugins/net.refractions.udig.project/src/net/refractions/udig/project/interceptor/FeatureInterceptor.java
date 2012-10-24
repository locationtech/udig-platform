/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.project.interceptor;

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
    String EXTENSION_ID = "net.refractions.udig.project.featureInterceptor"; //$NON-NLS-1$
    
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
