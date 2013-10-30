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
package org.locationtech.udig.project.element;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.internal.ProjectPlugin;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IMemento;

public abstract class AbstractGenericProjectElement implements
		IGenericProjectElement {

	/**
	 * This is an id for the {@link #firePropertyEvent(String, Object, Object)} 
	 * indicating that an unspecified change has occurred and emf should update.
	 * 
	 * EMF is the framework that provides the labels and icons in the different
	 * views in the system. Without triggering this event the labels and icons
	 * will not update.
	 * 
	 * EMF will ignore any before and after values.
	 */
	public static final String PROP_UPDATE_EMF = "UPDATE_EMF";

	private String m_extId;
	private ProjectElementAdapter projectElementAdapter;
	private Collection<IPropertyChangeListener> listeners = new CopyOnWriteArraySet<IPropertyChangeListener>();

	public String getExtensionId() {
		return m_extId;
	}

	public void init(IMemento memento) {
	}

	public void save(IMemento memento) {
	}

	public void setExtensionId(String extId) {
		this.m_extId = extId;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (IProjectElement.class.isAssignableFrom(adapter)) {
			return projectElementAdapter;
		}
		return null;
	}

	public void setProjectElementAdapter(ProjectElementAdapter adapter) {
		this.projectElementAdapter = adapter;
	}

	protected ProjectElementAdapter getProjectElementAdapter() {
		return projectElementAdapter;
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		listeners.add(listener);
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Fires an event to the registered listeners.
	 * 
	 * @see #PROP_UPDATE_EMF
	 * 
	 * @param eventId
	 *            the id of the event. There is a special {@link #PROP_UPDATE_EMF}
	 *            event to update labels and icons in the project explorer and
	 *            other viewers that use the EMF ItemProviders for labels.
	 * 
	 * @param oldValue
	 *            the value before the event.
	 * @param newValue
	 *            the value after the event.
	 */
	public void firePropertyEvent(String eventId, Object oldValue,
			Object newValue) {
		if (eventId.equals(PROP_UPDATE_EMF)) {
			getProjectElementAdapter().setBackingObject(this); // trigger EMF
																// to update
		}
		PropertyChangeEvent event = new PropertyChangeEvent(this, eventId,
				oldValue, newValue);
		for (IPropertyChangeListener listener : listeners) {
			listener.propertyChange(event);
		}
	}
	
	/**
	 * Updates the resource responsible for saving the element that the
	 * element has data that needs to be saved.
	 * 
	 * @param dirty if true then a save will occur when save is called or shutdown takes place
	 */
	protected void setDirty(boolean dirty){
		getProjectElementAdapter().eResource().setModified(dirty);
	}
	
	/**
	 * Save the resource
	 * @throws IOException
	 */
	public void save() throws IOException{
		Map<String, String> defaultSaveOptions = ProjectPlugin.getPlugin().saveOptions;
		getProjectElementAdapter().eResource().save(defaultSaveOptions);
	}
}
