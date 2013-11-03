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
package org.locationtech.udig.tutorials.genericprojectelement;

import org.locationtech.udig.project.element.ProjectElementAdapter;
import org.locationtech.udig.project.ui.UDIGEditorInput;

import org.eclipse.jface.resource.ImageDescriptor;

public class MyElementEditorInput extends UDIGEditorInput {
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return "MyProjectElement";
	}

	public String getToolTipText() {
		return "A tutorial editor";
	}
	
	public MyProjectElement getBackingObject(){
		return (MyProjectElement) ((ProjectElementAdapter) getProjectElement()).getBackingObject();
	}

}
