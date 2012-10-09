/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.tutorials.genericprojectelement;

import java.util.Random;

import net.refractions.udig.project.IProject;
import net.refractions.udig.project.element.ProjectElementAdapter;
import net.refractions.udig.project.ui.ApplicationGIS;

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
