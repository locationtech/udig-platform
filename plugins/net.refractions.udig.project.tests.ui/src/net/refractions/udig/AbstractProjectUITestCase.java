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
package net.refractions.udig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import net.refractions.udig.internal.ui.UDIGDropHandler;
import net.refractions.udig.internal.ui.UDIGDropHandler.CompositeDropActionJob;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.ui.internal.MapEditorPart;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

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
