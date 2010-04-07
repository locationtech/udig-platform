package net.refractions.udig.feature.editor;


import net.refractions.udig.project.ui.IUDIGDialogPage;
import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.PropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A basic, default example of a Dialog feature Editor.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public class DialogEditor implements IUDIGDialogPage {
	private IToolContext context;

	private PropertySheetPage featureDisplay;

	private PropertySheetEntry entry;

	public DialogEditor() {
		featureDisplay = new PropertySheetPage();
		entry = new PropertySheetEntry();
		featureDisplay.setRootEntry(entry);
	}
	/**
	 * @see net.refractions.udig.project.ui.IUDIGView#setContext()
	 */
	public void setContext(IToolContext context) {
		this.context = context;
	}

	/**
	 * @see net.refractions.udig.project.ui.IUDIGView#getContext()
	 */
	public IToolContext getContext() {
		return context;
	}

	public void createControl(Composite parent) {
		featureDisplay.createControl(parent);	
	}

	public Point getPreferredSize() {
		return new Point(400, 600);
	}

	public Control getControl() {
		return featureDisplay.getControl();
	}

	public void setFeature(SimpleFeature feature) {
		entry.setValues(new Object[]{feature});
	}

}
