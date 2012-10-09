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
package net.refractions.udig.project.tests.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.NullCommand;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.After;
import org.junit.Before;

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
