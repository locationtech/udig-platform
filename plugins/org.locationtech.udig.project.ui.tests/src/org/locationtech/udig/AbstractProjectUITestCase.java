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
package org.locationtech.udig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.locationtech.udig.internal.ui.UDIGDropHandler;
import org.locationtech.udig.internal.ui.UDIGDropHandler.CompositeDropActionJob;
import org.locationtech.udig.project.tests.support.AbstractProjectTestCase;
import org.locationtech.udig.project.ui.internal.MapEditorPart;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ErrorEditorPart;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractProjectUITestCase extends AbstractProjectTestCase {

    @Before
    public void abstractProjectUITestCaseSetUp() throws Exception {
        final IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
		IIntroPart intro = introManager.getIntro();
		if( intro!=null ){
			introManager.closeIntro(intro);
	        UDIGTestUtil.inDisplayThreadWait(3000, new WaitCondition(){

				public boolean isTrue() {
					return introManager.getIntro()==null;
				}
	        	
	        }, true);
		}
    }

    @After
    public void abstractProjectUITestCaseTearDown() throws Exception {
        UDIGTestUtil.inDisplayThreadWait(10000, new WaitCondition(){

            public boolean isTrue() {
                CompositeDropActionJob actionJob = UDIGDropHandler.getActionJob();
                return actionJob.getJobQueue().isEmpty() && actionJob.getState()==Job.NONE;
            }
            
        }, false);

        final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorReference[] editors = activePage.getEditorReferences();
        for( IEditorReference reference : editors ) {
            IEditorPart editor = reference.getEditor(false);
            if( editor instanceof ErrorEditorPart){
            	System.out.println("Error opening "+editor.getTitle());
            	continue;
            }
            assertFalse( "Error encountered loading", editor instanceof ErrorEditorPart);
			
            if (editor instanceof MapEditorPart) {
                MapEditorPart mapEditor = (MapEditorPart) editor;
                
                if( mapEditor!=null ) {
                    mapEditor.setTesting(true);
                }
            }
        }
        while ( !activePage.closeAllEditors(false)  ||  activePage.getEditorReferences().length!=0);         
        //super.tearDown();
        
        editors = activePage.getEditorReferences();
        for( IEditorReference reference : editors ) {
        	IEditorPart editor = reference.getEditor(false);            
        	if( editor instanceof ErrorEditorPart){
            	System.out.println("Error opening "+editor.getTitle());
            	continue;
            }
        	MapEditorPart mapEditor = (MapEditorPart) editor;
            if( mapEditor!=null ){
                mapEditor.setTesting(true);
            }
        }
        while ( !activePage.closeAllEditors(false)  ||  activePage.getEditorReferences().length!=0);         

        editors = activePage.getEditorReferences();
        assertEquals(0,editors.length);
        
    }

}
