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
package org.locationtech.udig.project.ui;

/**
 * This class is called to check if a feature panel can be enabled; it is passed the
 * same "FeatureSite" information that is provided to a FeaturePanel when initialized.
 * 
 * @since 1.2.0
 */
public abstract class IFeaturePanelCheck {
    /**
     * Used to indicate this feature panel is statically checked; and does not need to evaulate
     * against the provided site.
     */
    public static IFeaturePanelCheck NONE = new IFeaturePanelCheck(){
        public boolean check( IFeatureSite site ) {
            return false; // not dynamic
        }
    };
    
    /**
     * Check the indicated feature site to see if your feature form
     * can operate.
     * 
     * @param site
     * @return true if your feature form can operate.
     */
    public abstract boolean check( IFeatureSite site );

}
