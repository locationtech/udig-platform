/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.internal.tool.display;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

/**
 * Wraps a ToolProxy.
 *
 * @author jeichar
 * @since 0.9.0
 */
public class ToolAction extends Action {

    ToolProxy tool;

    ToolAction( ToolProxy proxy ) {
        this.tool = proxy;

        setId(tool.getId());
        setText(tool.getName());
        setToolTipText(tool.getToolTipText());
        if( tool.getImageDescriptor()!=null)
            setImageDescriptor(tool.getImageDescriptor());
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
      tool.run();
    }

}
