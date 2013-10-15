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
package net.refractions.udig.tutorials.template;

import net.refractions.udig.printing.ui.Template;
import net.refractions.udig.printing.ui.TemplateFactory;

public class ImageTemplateFactory implements TemplateFactory {

	public Template createTemplate() {
		return new ImageTemplate();
	}

	public String getName() {
		return "Image template"; // should be internationalized
	}

}
