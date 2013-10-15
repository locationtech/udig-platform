/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2006 IBM Corporation and others
 * ------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 * --------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.refractions.udig.feature.panel;

import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Interface for a workbench part to contribute content to the feature panel view.
 * <p>
 * It is expected that a schema is unique for a configuration of feature panels.
 * Editors and views can share a configuration by sharing a schema.
 * </p>
 */
public interface FeaturePanelPageContributor {

	/**
	 * Returns the SimpleFeatureType for the feature panel sheet page.
	 * 
	 * @return the schema for a feature panel sheet page
	 */
	public SimpleFeatureType getSchema();
	
}
