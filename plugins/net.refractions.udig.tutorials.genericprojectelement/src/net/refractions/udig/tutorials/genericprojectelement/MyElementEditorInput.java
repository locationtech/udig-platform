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
