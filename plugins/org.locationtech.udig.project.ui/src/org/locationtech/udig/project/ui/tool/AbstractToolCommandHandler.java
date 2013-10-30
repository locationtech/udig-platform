/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
