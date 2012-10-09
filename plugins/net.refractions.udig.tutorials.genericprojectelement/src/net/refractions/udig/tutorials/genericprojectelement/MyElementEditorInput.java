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
package net.refractions.udig.tutorials.genericprojectelement;

import net.refractions.udig.project.element.ProjectElementAdapter;
import net.refractions.udig.project.ui.UDIGEditorInput;

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
