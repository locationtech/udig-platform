/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.tool.display;

import org.eclipse.jface.action.Action;

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
