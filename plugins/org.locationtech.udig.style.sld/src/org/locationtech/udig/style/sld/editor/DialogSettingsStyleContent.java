/**
 * 
 */
package org.locationtech.udig.style.sld.editor;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.StyleContent;

/**
 * Persist an IMemento onto the style blackboard to save Style Dilog Settings between runs.
 * <p>
 * This is mostly used by the theme page right now.
 * @author Jesse Eichar
 */
public class DialogSettingsStyleContent extends StyleContent{

	public static final String EXTENSION_ID = "org.locationtech.udig.style.dialogSettings"; //$NON-NLS-1$
	private static final String MEMENTO_KEY = "DialogSettings"; //$NON-NLS-1$

	public DialogSettingsStyleContent() {
		super(EXTENSION_ID);
	}

	@Override
	public Object createDefaultStyle(IGeoResource resource, Color colour,
			IProgressMonitor monitor) throws IOException {
		return null;
	}

	@Override
	public Class<? extends Object> getStyleClass() {
		return IMemento.class;
	}

	@Override
	public Object load(IMemento memento) {
		return memento.getChild(MEMENTO_KEY);
	}

	@Override
	public Object load(URL url, IProgressMonitor monitor) throws IOException {
		return null;
	}

	@Override
	public void save(IMemento memento, Object value) {
		memento.createChild(MEMENTO_KEY).putMemento(((IMemento) value));
	}

}
