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
package org.locationtech.udig.tutorials.genericprojectelement;

import java.util.Random;

import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.element.ProjectElementAdapter;
import org.locationtech.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * This is here so that we can create elements through the UI.  It randomly chooses a 
 * label.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class AddElementAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {

    public final Random random = new Random(); 
    
    public void dispose() {
    }

    public void init( IWorkbenchWindow window ) {
    }
    
    public void run( IAction action ) {
        IProject project = ApplicationGIS.getActiveProject();
        ProjectElementAdapter element = ApplicationGIS.createGeneralProjectElement(project , 
                MyProjectElement.class, MyProjectElement.EXT_ID);
        MyProjectElement myElement = (MyProjectElement) element.getBackingObject();
        if( random.nextBoolean() ){
            StringBuilder builder = new StringBuilder();
            for(int i =0; i<(random.nextInt(5)+2);i++ ){
                builder.append(String.valueOf(random.nextInt(9)));
            }
            myElement.setLabel(builder.toString());
            element.setName(builder.toString());
        }
    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }

}
