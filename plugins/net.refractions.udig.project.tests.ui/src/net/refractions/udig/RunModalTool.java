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
package net.refractions.udig;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.tool.ActionTool;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.project.ui.tool.ToolLifecycleListener;

import org.eclipse.jface.action.IAction;

/**
 * Tests activating a modal tool from another tool.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class RunModalTool implements ActionTool {

    public void run() {
        IAction tool = ApplicationGIS.getToolManager().getTool("net.refractions.udig.tools.Pan",  //$NON-NLS-1$
                "net.refractions.udig.tool.category.pan"); //$NON-NLS-1$

        tool.run();
    }

    public void addListener( ToolLifecycleListener listener ) {
    }

    public void dispose() {
    }

    public IToolContext getContext() {
        return null;
    }

    public Object getProperty( String key ) {
        return null;
    }

    public boolean isEnabled() {
        return false;
    }

    public void removeListener( ToolLifecycleListener listener ) {
    }

    public void setContext( IToolContext tools ) {
    }

    public void setEnabled( boolean enable ) {
    }

    public void setProperty( String key, Object value ) {
    }

}
