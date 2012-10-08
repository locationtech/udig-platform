/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2006, Refractions Research Inc.
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
package org.tcat.citd.sim.udig.bookmarks.internal;

import java.util.Collection;
import java.util.Vector;

import net.refractions.udig.project.IProject;

/**
 * This class provides a wrapper for displaying <code>IProject</code> objects as folders in the
 * <code>BookmarksView</code>.<BR>
 * <BR>
 * This gives the advantage of more easily displaying custom menus and icons.
 * <p>
 * </p>
 * 
 * @author cole.markham
 * @since 1.0.0
 */
public class ProjectWrapper {
    private IProject project;

    /**
     * Default constructor
     * 
     * @param project The project that this object will wrap.
     */
    public ProjectWrapper( IProject project ) {
        this.project = project;
    }

    /**
     * @return Returns the project.
     */
    public IProject getProject() {
        return project;
    }

    /**
     * @param project The project to set.
     */
    public void setProject( IProject project ) {
        this.project = project;
    }

    /**
     * Get the name for the project
     * 
     * @return the name
     */
    public String getName() {
        return project.getName();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * Unwrap all of the projects in the given list
     * 
     * @param wrappedProjects
     * @return a List of IProject objects
     */
    public static Collection<IProject> unwrap( Collection wrappedProjects ) {
        Vector<IProject> projects = new Vector<IProject>(wrappedProjects.size());
        for( Object element : wrappedProjects ) {
            if (element instanceof ProjectWrapper) {
                ProjectWrapper wrapper = (ProjectWrapper) element;
                projects.add(wrapper.getProject());
            }
        }
        return projects;
    }

    /**
     * Wrap the projects in the given list
     * 
     * @param projects
     * @return a List of ProjectWrapper objects
     */
    public static Collection<ProjectWrapper> wrap( Collection<IProject> projects ) {
        Vector<ProjectWrapper> wrapped = new Vector<ProjectWrapper>(projects.size());
        for( IProject project : projects ) {
            wrapped.add(new ProjectWrapper(project));
        }
        return wrapped;
    }
}
