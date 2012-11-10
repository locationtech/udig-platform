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
package net.refractions.udig.tutorials.genericprojectelement;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class MyElementEditor extends EditorPart {

	private boolean dirty = false;
	private String newLabel = "";
	private Text text;
	
	@Override
	public MyElementEditorInput getEditorInput() {
		return (MyElementEditorInput) super.getEditorInput();
	}
	
	public MyProjectElement getMyProjectElement() {
		return getEditorInput().getBackingObject();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		String oldLabel = getMyProjectElement().getLabel();
		getMyProjectElement().setLabel(newLabel);
		dirty=false;
		firePropertyChange(PROP_DIRTY);
		// fire EMF event to get the label in the project explorer to update
		getMyProjectElement().firePropertyEvent(MyProjectElement.PROP_UPDATE_EMF, null, null);
		// now we can optionally fire our own custom event
		getMyProjectElement().firePropertyEvent(MyProjectElement.PROP_LABEL_CHANGE, oldLabel, newLabel);
	}


	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// The site and input must be set or the framework explodes.  It is part of eclipse
		// so just make sure you do it.
        setSite(site);
        setInput(input);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return isDirty();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		Label label = new Label(comp,SWT.NONE);
		label.setText("Enter new label for MyProjectElement");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(label);
		text = new Text(comp, SWT.BORDER|SWT.SINGLE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
		String elemLabel = getMyProjectElement().getLabel();
		if(elemLabel==null){
			elemLabel="";
		}
		text.setText(elemLabel);
		text.addListener(SWT.Modify, new Listener(){

			public void handleEvent(Event event) {
				dirty = true;
				newLabel = text.getText();
				firePropertyChange(PROP_DIRTY);
			}
			
		});
	}

	@Override
	public void setFocus() {
		text.setFocus();
	}

	@Override
	public void dispose() {
		try {
			getMyProjectElement().save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
