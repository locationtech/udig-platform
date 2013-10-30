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
package org.locationtech.udig.printing.ui.internal;

import org.locationtech.udig.printing.ui.Template;
import org.locationtech.udig.printing.ui.TemplateFactory;

public class BasicTemplateFactory implements TemplateFactory {

	public Template createTemplate() {
		return new BasicTemplate();
	}

	public String getName() {
		return createTemplate().getName();
	}
}
