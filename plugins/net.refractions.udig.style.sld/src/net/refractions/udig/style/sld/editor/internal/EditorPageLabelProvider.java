package net.refractions.udig.style.sld.editor.internal;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Provides labels for <code>IEditorNode</code> objects.
 */
public class EditorPageLabelProvider extends LabelProvider {

	    @Override
        public String getText(Object element) {
	        return ((IEditorNode) element).getLabelText();
	    }

	    @Override
        public Image getImage(Object element) {
	        return ((IEditorNode) element).getLabelImage();
	    }
	}
