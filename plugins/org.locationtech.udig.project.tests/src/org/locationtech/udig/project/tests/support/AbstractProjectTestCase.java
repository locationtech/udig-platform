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
package org.locationtech.udig.project.tests.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.After;
import org.junit.Before;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.commands.NullCommand;

/**
 * Attempts to clean up after running by clearing the project registry, maps, and projects.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractProjectTestCase {
    
    /**
     * Should be called first by overriding tests.
     */
    @Before
    public void abstractProjectTestCaseSetUp() throws Exception {
		ProjectPlugin.getPlugin().setUndoableCommandWarning(false);
	}
    
    @After
    public void abstractProjectTestCaseTearDown() throws Exception {
        List<Project> projects = ProjectPlugin.getPlugin().getProjectRegistry().getProjects();
        List<Resource> resources=new ArrayList<Resource>();
        for( Project project : projects ) {
        	// make sure there are no commands executing on the project stack
			project.eSetDeliver(false);
        	project.sendSync(new NullCommand());
			project.eSetDeliver(true);

            
			List<IProjectElement> elements = project.getElements();
        	for (IProjectElement element : elements) {
				if( element instanceof Map){
					Map map=(Map) element;
					map.eSetDeliver(false);
					map.sendCommandSync(new NullCommand());
					map.eSetDeliver(true);
				}
			}
            
            project.getElementsInternal().clear();
        	// Map commands could have put another command on the project stack so
        	// make sure there are no commands executing on the project stack
        	project.eSetDeliver(false);
        	project.sendSync(new NullCommand());
			project.eSetDeliver(true);

			Resource resource = project.eResource();
            resources.add(resource);
            if( resource!=null)
                resource.unload();
        }
        
        projects.clear();
        
        for( Resource r : resources ) {
            File file=new File(r.getURI().toFileString());
            deleteFile(file);
        }
        
        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
        List< ? extends IResolve> services = localCatalog.members(null);
        for( IResolve resolve : services ) {
            localCatalog.remove((IService) resolve);
        }
        
        
    }

    private void deleteFile( File file ) {
        if( !file.exists() )
            return;
        if( file.isDirectory() ){
            File[] files = file.listFiles();
            for( File file2 : files ) {
                deleteFile(file2);
            }
        }
        
        file.delete();
    }
    
    
}
