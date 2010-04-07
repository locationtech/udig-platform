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
package net.refractions.udig.project.ui.tool;

import org.eclipse.core.commands.AbstractHandler;

/**
 * A convenience class. 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractToolCommandHandler extends AbstractHandler implements IToolHandler {

    private String currentCommandId;
    private Tool currentTool;

    public void setTool( Tool tool ) {
        this.currentTool=tool;
    }

    public void setCurrentCommandId( String currentCommandId ) {
        this.currentCommandId=currentCommandId;
    }

    /**
     * @return Returns the currentTool.
     */
    public Tool getCurrentTool() {
        return currentTool;
    }

    /**
     * @return Returns the currentCommandId.
     */
    public String getCurrentCommandId() {
        return currentCommandId;
    }


}
