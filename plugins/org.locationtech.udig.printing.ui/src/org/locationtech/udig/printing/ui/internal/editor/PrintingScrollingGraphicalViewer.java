/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.ui.internal.editor;

import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;

/**
 * Adds a reference to the PageEditor so that EditParts can get access to the PageEditor and add
 * actions to its ActionRegistry.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class PrintingScrollingGraphicalViewer extends ScrollingGraphicalViewer {

    private PageEditor editor;

    PrintingScrollingGraphicalViewer(PageEditor editor) {
        super();
        this.editor = editor;
    }

    /**
     * @return Returns the editor.
     */
    public PageEditor getEditor() {
        return editor;
    }

}
