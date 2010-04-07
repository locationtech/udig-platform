package net.refractions.udig.validation.ui;


import net.refractions.udig.validation.internal.Messages;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.geotools.validation.dto.ArgumentDTO;

public class ValidationTableLabelProvider implements ITableLabelProvider {

	public ValidationTableLabelProvider() {
	}

	public Image getImage(Object element) {
		return null;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return ((ArgumentDTO) element).getName();
		case 1:
			Object value = ((ArgumentDTO) element).getValue();
			if (value == null) {
				return ""; //$NON-NLS-1$
			}
			ArgumentDTO arg = (ArgumentDTO) element;
			//determine if the element came from a combobox or a textbox
			if (ValidationDialog.isTypeRef(arg.getName())) {
				//it's a typeRef, so this is a comboBox... get the label
				// (we could try to chop up the string we have and to get the
				// layer, but we'll play it safe and get it from the list of
				// layers and typeRefs in ValidationDialog)
				return ValidationDialog.getTypeRefLayer(value.toString());
			} else {
				//just return the value of the arg
				return value.toString();
			}
		default:
			return Messages.ValidationTableLabelProvider_invalidColumn + columnIndex; 
		}
	}

}
