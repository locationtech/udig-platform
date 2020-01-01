/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.wizard;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.locationtech.udig.project.ui.internal.ISharedImages;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;

/**
 * A wizard page to create a new project.
 * 
 * @author vitalus
 * 
 * @since 0.3
 */
public class NewProjectWizardPage extends WizardPage {

    DirectoryFieldEditor projectDirectoryEditor;

    StringFieldEditor projectNameEditor;

    /**
     * Construct <code>NewProjectWizardPage</code>.
     */
    public NewProjectWizardPage() {
        super(Messages.NewProjectWizardPage_newProject, Messages.NewProjectWizardPage_newProject,
                ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.NEWPROJECT_WIZBAN));
        setDescription(Messages.NewProjectWizardPage_newProject_description);
    }

    /**
     * Set up this page for use.
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @param parent
     */
    public void createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);

        projectNameEditor = new StringFieldEditor("newproject.name",
                Messages.NewProjectWizardPage_label_projectName, composite) {
            protected boolean doCheckState() {
                return validate();
            }
        };
        projectNameEditor.setPage(this);
        projectNameEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
        Text textControl = projectNameEditor.getTextControl(composite);
        GridData gd = new GridData(SWT.LEFT, SWT.NONE, false, false);
        gd.widthHint = 100;
        gd.horizontalSpan = 2;
        textControl.setLayoutData(gd);

        projectDirectoryEditor = new DirectoryFieldEditor("newproject.directory",
                Messages.NewProjectWizardPage_label_projectDir, composite) {
            protected boolean doCheckState() {
                return validate();
            }
        };
        projectDirectoryEditor.setPage(this);
        projectDirectoryEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
        projectDirectoryEditor.fillIntoGrid(composite, 3);

        String defaultProjectName = Messages.NewProjectWizardPage_default_name;

        final IPath homepath = Platform.getLocation();
        String projectPath = new File(homepath.toString()).getAbsolutePath();

        projectNameEditor.setStringValue(defaultProjectName);
        projectDirectoryEditor.setStringValue(projectPath);

        composite.pack();

        setControl(composite);
        setPageComplete(true);
    }

    /**
     * Returns specified project name.
     * 
     * @return
     */
    public String getProjectName() {
        return projectNameEditor.getStringValue();
    }

    /**
     * Returns specified project path.
     * 
     * @return
     */
    public String getProjectPath() {
        return projectDirectoryEditor.getStringValue();
    }

    /**
     * Validates the form with project name and path.
     * 
     * @return <code>true</code> if valid
     */
    public boolean validate() {

        final String projectPath = getProjectPath();
        final String projectName = getProjectName();

        if (projectPath == null || projectPath.length() == 0) {
            setErrorMessage(Messages.NewProjectWizardPage_err_project_dir_valid);
            setPageComplete(false);
            return false;
        }

        File f = new File(projectPath + File.separator + projectName + ".udig");
        if (f.exists()) {
            setErrorMessage(Messages.NewProjectWizardPage_err_project_exists);
            setPageComplete(false);
            return false;
        }

        File projectPathFolder = null;
        try {
            URL projectURL = new URL("file:///" + projectPath); //$NON-NLS-1$
            projectPathFolder = new File(projectURL.getFile());

            String absolutePath = projectPathFolder.getAbsolutePath();
            if (!projectPath.equals(absolutePath)) {
                setErrorMessage(Messages.NewProjectWizardPage_err_project_dir_absolute);
                setPageComplete(false);
                return false;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            setPageComplete(false);
            return false;
        }

        if (projectPathFolder.exists()) {
            String projectFileAbsolutePath = projectPathFolder.getAbsolutePath()
                    + File.separatorChar + "project.uprj"; //$NON-NLS-1$;
            File projectFile = new File(projectFileAbsolutePath);
            if (projectFile.exists()) {
                setErrorMessage(Messages.NewProjectWizardPage_err_project_exists);
                setPageComplete(false);
                return false;
            }
        } else {
            setErrorMessage(Messages.NewProjectWizardPage_err_project_dir_valid);
            setPageComplete(false);
            return false;
        }

        if (projectName == null || projectName.length() == 0) {
            setErrorMessage(Messages.NewProjectWizardPage_err_project_name);
            setPageComplete(false);
            return false;
        }
        setPageComplete(true);
        return true;
    }

}
