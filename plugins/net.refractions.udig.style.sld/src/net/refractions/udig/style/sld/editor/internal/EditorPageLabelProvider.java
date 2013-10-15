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
