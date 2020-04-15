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
package org.locationtech.udig.project.ui.wizard.export.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.Project;

/**
 * Helper class that does project exporting.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ExportProjectUtils {

    public static void exportProject( Project project, String destinationDirectory, IProgressMonitor monitor ) {
        if (project == null)
            return;

        monitor.beginTask(Messages.ExportProjectWizard_Exporting + project.getName(), 1);

        // this represents the file we are going to write out to
        Resource resource = project.eResource();
        if (resource == null) {
            // this project has never been saved to a file yet; so we are in
            // a bit of trouble knowing how to write it out
            throw new NullPointerException("Project does not have a file"); //$NON-NLS-1$
        }
        // destination ends up being a file path
        String destination = generateDestinationProjectFilePath(resource, project.getName(), destinationDirectory);

        // creates a new resource (ie copy) and gathers everything into it
        Resource copy = collectAllAndCopyIntoDestResource(resource, destination);
        saveResource(copy);
        monitor.worked(1);
    }

    private static String generateDestinationProjectFilePath( Resource resource, String name, String destinationDirectory ) {
        URI origURI = resource.getURI();
        File file = new File(origURI.toFileString());
        return destinationDirectory + File.separator + name + ".udig" + File.separator + file.getName(); //$NON-NLS-1$
    }

    private static void saveResource( Resource copy ) {
        try {
            copy.save(null);
        } catch (IOException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        }
    }

    /**
     * It looks like this method collects all the contents of the project (maps and pages and other)
     * and some how adds them to a single resource defined by the dest file path.
     * 
     * @param projectResource Contains the selected project we want to write out
     * @param dest the file path used to write out the resources
     * @return Resource used to store everything
     */
    private static Resource collectAllAndCopyIntoDestResource( Resource projectResource, String dest ) {
        // this is the contents of our project (ie maps and pages and other)
        // note these resources in the resource set (ie the raw form not objects)
        //
        ResourceSet resourceSet = projectResource.getResourceSet();

        // start with the project resource
        List<Resource> resources = gatherAllResourcesToList(projectResource);

        // let us make the new file to write out
        File destFile = new File(dest);
        String absoluteDestPath = destFile.getAbsolutePath();

        URI destURI = URI.createFileURI(absoluteDestPath);
        Resource destResource = resourceSet.createResource(destURI);

        Collection<EObject> collection = new ArrayList<EObject>();
        for( Resource curResource : resources ) {
            collection.addAll(curResource.getContents());
        }
        // here is where we actually do the copy!
        Collection<EObject> copyAll = EcoreUtil.copyAll(collection);
        destResource.getContents().addAll(copyAll);

        return destResource;
    }

    /**
     * We need a list of all the resources in a project so we can save them.
     * <p>
     * Handy tip - the project resource itself is one of the resources that must be included
     * in the list! (along with all the contents of the project).
     * 
     * @param projectResource
     * @return list of resources for projectResoruce
     */
    private static List<Resource> gatherAllResourcesToList( Resource projectResource ) {
        List<Resource> resources = new ArrayList<Resource>();
        resources.add(projectResource);
        addReferencedResourcesToList(resources);

        return resources;
    }
    /**
     * Go through the provided list of resources; and tack any referenced resoruces
     * on to the end of the list recursively.
     * <p>
     * At the end of this method the resource list will contain an entry
     * for every resource.
     * 
     * @param resources
     */
    private static void addReferencedResourcesToList( List<Resource> resources ) {
        for( int i = 0; i < resources.size(); i++ ) {
            Resource r = resources.get(i);
            for( Iterator<EObject> j = r.getAllContents(); j.hasNext(); ) {
                for( Object object : j.next().eCrossReferences() ) {
                    if (object instanceof EObject) {
                        EObject eObject = (EObject) object;
                        Resource otherResource = eObject.eResource();
                        if (otherResource != null && !resources.contains(otherResource)) {
                            resources.add(otherResource);
                        }
                    }
                }
            }
        }
    }
}
