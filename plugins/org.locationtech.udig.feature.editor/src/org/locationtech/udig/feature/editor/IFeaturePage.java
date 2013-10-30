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
package org.locationtech.udig.feature.editor;

import org.locationtech.udig.project.ui.IFeatureSite;
import org.locationtech.udig.project.ui.IUDIGView;

import org.eclipse.ui.part.IPage;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A page set up to accept a feature for editing.
 * <p>
 * This is similar to how a property sheet checks for selectionChanged(part,selection); instead
 * we are tacking the edit feature, edit and change of schema.
 * 
 * @see IUDIGView
 * @author Jody Garnett
 * @since 1.2.0
 */
public interface IFeaturePage extends IPage {

    /**
     * Sets the current site for editing; this object represents the view
     * or dialog hosting the page.<p>
     * You are not required to listen to anything:
     * <ul>
     * <li>editFeatureChanged will be called when something amusing is going on.
     * </ul>
     */
    void setFeatureSite( IFeatureSite newContext );

    /**
     * Returns the current site for editing; implementors are expected to hold
     * on to the value provided by setFeatureSite above.
     * 
     * @return the current context object
     */
    IFeatureSite getFeatureSite();

    /**
     * Called when the the current edit feature changes.
     * 
     * @param feature
     */
    void editFeatureChanged( SimpleFeature feature );
    
}
