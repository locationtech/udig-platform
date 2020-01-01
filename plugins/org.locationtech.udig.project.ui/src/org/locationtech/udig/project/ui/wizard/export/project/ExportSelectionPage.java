/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2006 IBM Corporation and others
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.locationtech.udig.project.ui.wizard.export.project;

import java.io.File;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.ProjectRegistry;

public class ExportSelectionPage extends WizardPage {

    private DirectoryFieldEditor editor;
    private ComboFieldEditor project;
    private Project selectedProject;
    
    public ExportSelectionPage(String title, String description, ImageDescriptor pageIcon) {
        super("Export Selection Page", title, pageIcon);
        setDescription(description);
    }
    
    public void createControl( Composite parent ) {
        Composite fileSelectionArea = new Composite(parent, SWT.NONE);
        GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL
                | GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        fileSelectionArea.setLayoutData(fileSelectionData);
        fileSelectionArea.setLayout(new GridLayout(1, false));
        createProjectEditor( fileSelectionArea );
        createFileEditor(fileSelectionArea);
        fileSelectionArea.moveAbove(null);
        setControl(fileSelectionArea);
        setPageComplete(false);
        setMessage(null);
        setErrorMessage(null);
    }

    private void createProjectEditor( Composite parent ) {
        ProjectRegistry registery = ProjectPlugin.getPlugin().getProjectRegistry();
        List<Project> list = registery.getProjects();
        String[][] projects = new String[ list.size()][];
        int index = 0;
        for( Project project : list ){
            projects[index]=new String[]{ project.getName(), project.getID().toString()};
            index++;
        }
        if( getDialogSettings() != null){
            if( registery.getCurrentProject() != null ){
                URI uri = registery.getCurrentProject().getID();
                if( uri != null ){
                    String selected = uri.toFileString();
                    this.getDialogSettings().put("projectSelect", selected );
                }                        
            }
        } 

		Composite projectPanel = new Composite(parent, SWT.NONE);
		projectPanel.setLayout(new GridLayout(1, false));
		projectPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        project = new ComboFieldEditor("projectSelect",
        		Messages.ExportSelectionPage_Project, projects, projectPanel );
        project.setPage( this ); 
        project.setPropertyChangeListener( new IPropertyChangeListener(){
            public void propertyChange( PropertyChangeEvent event ) {
                selectProject( (String) event.getNewValue() );
                check();
            }            
        });
    }
    
    private void createFileEditor( Composite parent ) {
		Composite filePanel = new Composite(parent, SWT.NONE);
		filePanel.setLayout(new GridLayout(1, false));
		filePanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        editor = new DirectoryFieldEditor("directorySelect",
        		Messages.ExportSelectionPage_Destination, filePanel){
            {
                setValidateStrategy( VALIDATE_ON_KEY_STROKE );
                setEmptyStringAllowed(false);                
            }
            @Override
            public boolean isValid() {
                File file = new File(getStringValue());
                if( file.isDirectory()){
                    return true;
                }
                else {
                    setErrorMessage(Messages.ExportSelectionPage_MissingDir);
                    return false;
                }
            }
        };
        editor.setPage(this);
        editor.getTextControl(filePanel).addModifyListener(new ModifyListener(){
            public void modifyText( ModifyEvent e ) {
                check();
            }
        });
    }
    public void check(){
        if( !editor.isValid()){
            setPageComplete( false );
            setMessage( editor.getErrorMessage(), ERROR );
            return;
        }
        if( getProject() == null ){
            setPageComplete( false );
            setErrorMessage(Messages.ExportSelectionPage_SelectProject);
            return;
        }
        setPageComplete(true);
        setMessage(Messages.ExportSelectionPage_ExportProject, INFORMATION );
    }
    public void selectProject( String uri ){
        ProjectRegistry registery = ProjectPlugin.getPlugin().getProjectRegistry();
        List<Project> list = registery.getProjects();
        for( Project project : list ){
           if( uri.equals( project.getID().toString() ) ){
               selectedProject = project;
               return;
           }
        }
        selectedProject = null;
    }
    public Project getProject(){
        return selectedProject;
    }
    public String getDestinationDirectory(){
        return editor.getStringValue();
    }
}
