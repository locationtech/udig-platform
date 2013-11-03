/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui;

import org.locationtech.udig.project.ui.feature.EditFeature;
import org.locationtech.udig.project.ui.tool.IToolContext;

/**
 * Allows access to the site hosting the IFeaturePanel.
 * 
 * @author Myles
 * @since 1.2
 */
public interface IFeatureSite extends IToolContext {
    
    public EditFeature getEditFeature();
    
    public IFeatureSite copy();
}
