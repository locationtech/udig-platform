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
import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;

/**
 * Wizard to create a new project.
 * 
 * @author jgarnett
 * @author vitalus
 * 
 * @since 0.3
 * 
 * @version 1.1.0
 */
public class NewProjectWizard extends Wizard implements INewWizard {

    /**
     * Wizard page used to fill parameters of new project
     */
    protected NewProjectWizardPage page;

    /**
     * Override to make title reflect current page.
     * 
     * @param page The page to set.
     */
    public void setPage(NewProjectWizardPage page) {
        this.page = page;
        setWindowTitle(page.getTitle());
    }

    /**
     * The <code>Wizard</code> implementation of this <code>IWizard</code> method does nothing.
     * Subclasses should extend if extra pages need to be added before the wizard opens. New pages
     * should be added by calling <code>addPage</code>.
     */
    public void addPages() {
        page = new NewProjectWizardPage();
        addPage(page);
        setWindowTitle(page.getTitle());
        setHelpAvailable(true);
    }

    /**
     * Completes the wizard if new project parameters are valid.
     * <p>
     * 
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @return <code>true</code> when project successfully created
     */
    public boolean performFinish() {
        if (!page.validate()) {
            return false;
        }

        String projectPath = page.getProjectPath();
        final String projectName = page.getProjectName();
        projectPath = projectPath.replaceAll("\\\\", "/"); //$NON-NLS-1$//$NON-NLS-2$

        while (projectPath.endsWith("/")) { //$NON-NLS-1$
            projectPath = projectPath.substring(0, projectPath.length() - 2);
        }
        Project project = ProjectPlugin.getPlugin().getProjectRegistry()
                .getProject(projectPath + File.separator + projectName + ".udig"); //$NON-NLS-1$ //$NON-NLS-2$
        project.setName(projectName);
        Resource projectResource = project.eResource();
        try {
            projectResource.save(Collections.EMPTY_MAP);
        } catch (IOException e) {
            ProjectUIPlugin.log(
                    "Error during saving the project file of an anew created project", e); //$NON-NLS-1$
        }

        return true;
    }

    /**
     * We can finish if the user has entered a file.
     * 
     * @return true if we can finish
     */
    public boolean canFinish() {
        return page.isPageComplete();
    }

    /**
     * Hook us up to the world .. I mean workbench.
     * <p>
     * This is where all the magic is supposed to happen to remember the previous directory and so
     * on.
     * </p>
     * <p>
     * </p>
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     * @param workbench
     * @param selection
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // TODO: Magic to remember previous directory
    }

}
