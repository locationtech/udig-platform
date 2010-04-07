/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.printing.ui.internal.editor;

import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;

/**
 * Adds a reference to the PageEditor so that EditParts can get access to the PageEditor and add
 * actions to its ActionRegistry.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class PrintingScrollingGraphicalViewer extends ScrollingGraphicalViewer{

    private PageEditor editor;

    PrintingScrollingGraphicalViewer( PageEditor editor ){
        super();
        this.editor=editor;
    }

    /**
     * @return Returns the editor.
     */
    public PageEditor getEditor() {
        return editor;
    }
    
}
