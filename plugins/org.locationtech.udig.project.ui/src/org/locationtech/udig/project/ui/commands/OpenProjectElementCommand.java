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
package org.locationtech.udig.project.ui.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.UndoableCommand;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.UDIGEditorInput;
import org.locationtech.udig.project.ui.internal.MapEditorPart;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectExplorer;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.ui.PlatformGIS;

public class OpenProjectElementCommand implements UndoableCommand {

    IProjectElement element;
    private IEditorPart previous;

    public OpenProjectElementCommand( IProjectElement element ) {
        this.element = element;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        try {
            if (PlatformUI.getWorkbench().isClosing())
                return;
            
            monitor.beginTask(Messages.OpenMapCommand_taskName, IProgressMonitor.UNKNOWN); 
            final UDIGEditorInput input = ApplicationGIS.getInput(element);
//          if (element instanceof Map) {
//              Map map = (Map) element;
//              if (map.getViewportModel().getBounds().isNull()) {
//                  Envelope bounds = map.getBounds(monitor);
//                  map.getViewportModelInternal().setBounds(bounds);
//              }
//          }
            if (input == null) {
                return;
            }
            input.setProjectElement(element);

            PlatformGIS.syncInDisplayThread(new Runnable(){
                public void run() {
                    IWorkbenchPage activePage = PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getActivePage();
                    IEditorReference[] editors = activePage.getEditorReferences();
                    for( IEditorReference reference : editors ) {
                        try {
                            if (reference.getEditorInput().equals(input)) {
                                previous=activePage.getActiveEditor();
                                activePage.activate(reference.getPart(true));
                                return;
                            }
                        } catch (PartInitException e) {
                            // ignore
                        }
                    }
                    openMap(input);
                    }

                });
        } finally {
            monitor.done();
        }
    }

    private void openMap( final UDIGEditorInput input ) {
        try {
            IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
                    input.getEditorId(), true, IWorkbenchPage.MATCH_NONE);
            
            ProjectExplorer explorer = ProjectExplorer.getProjectExplorer();
            explorer.setSelection(Collections.singleton(input.getProjectElement()), true);

            if( part instanceof MapEditorPart ){
                MapEditorPart mapEditor=(MapEditorPart) part;
                while( !mapEditor.getComposite().isVisible() || !mapEditor.getComposite().isEnabled() ){
                    if( !Display.getCurrent().readAndDispatch() ){
                        Thread.sleep(300);
                    }
                }
            }
        } catch (PartInitException e) {
            ProjectUIPlugin.log(e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

    public String getName() {
        return Messages.OpenMapCommand_commandName; 
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        if (previous!=null){
            previous.getEditorSite().getPage().activate(previous);
            return;
        }
        PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                UDIGEditorInput input = ApplicationGIS.getInput(element);
                IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage();
                IEditorReference[] editors = activePage.getEditorReferences();
                List<IEditorReference> matches = new ArrayList<IEditorReference>();
                for( IEditorReference reference : editors ) {
                    try {
                        if (reference.getEditorInput().equals(input)) {
                            matches.add(reference);
                        }
                    } catch (PartInitException e) {
                        // do nothing
                    }
                }

                activePage
                        .closeEditors(matches.toArray(new IEditorReference[matches.size()]), true);
            }
        });
    }

    public Command copy() {
        return null;
    }

}
