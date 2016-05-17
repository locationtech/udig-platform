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
package org.locationtech.udig.style.sld.editor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.core.internal.ExtensionPointUtil;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.style.sld.SLD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * Open the style editor dialog and add its pages
 */
public class OpenStyleEditorAction extends Action implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

    public static final String ATT_ID = "id"; //$NON-NLS-1$
    public static final String ATT_CLASS = "class"; //$NON-NLS-1$
    public static final String ATT_LABEL = "label"; //$NON-NLS-1$
    public static final String ATT_REQUIRES = "requires"; //$NON-NLS-1$
    public static final String STYLE_ID = "org.locationtech.udig.style.sld"; //$NON-NLS-1$

    private Layer selectedLayer;

    private Plugin plugin;

    /**
     * The workbench window; or <code>null</code> if this
     * action has been <code>dispose</code>d.
     */
    private IWorkbenchWindow workbenchWindow;

    /**
     * Create a new <code>OpenPreferenceAction</code>
     * This default constructor allows the the action to be called from the welcome page.
     */
    public OpenStyleEditorAction() {
        this(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
    }

    /**
     * Create a new <code>OpenPreferenceAction</code> and initialize it 
     * from the given resource bundle.
     * @param window
     */
    public OpenStyleEditorAction( IWorkbenchWindow window ) {
        super();
        if (window == null) {
            throw new IllegalArgumentException();
        }
        this.workbenchWindow = window;
        setActionDefinitionId("net.refractions.style.sld.editor"); //$NON-NLS-1$
        // setToolTipText(WorkbenchMessages.OpenPreferences_toolTip);
        // window.getWorkbench().getHelpSystem().setHelp(this,
        // IWorkbenchHelpContextIds.OPEN_PREFERENCES_ACTION);
    }

    public void run( IAction action ) {
        if (workbenchWindow == null) {
            return; // action has been disposed
        }

        Shell shell = workbenchWindow.getShell();
        // the page to select by default
        String pageId = "simple"; //$NON-NLS-1$
        // the filter to apply, if defined
        // String[] displayedIds = null;

        final EditorPageManager manager = EditorPageManager.loadManager(plugin, selectedLayer);
        List<?> elements = manager.getElements(EditorPageManager.PRE_ORDER);
        Set<String> ids = new HashSet<String>();
        for(Object element : elements) {
        	ids.add(((EditorNode)element).getId());
        }
		try {
	        if (SLD.POINT.supports(selectedLayer)) {
	            if(ids.contains("org.locationtech.udig.style.advanced.editorpages.SimplePointEditorPage")) {//$NON-NLS-1$
	            	pageId = "org.locationtech.udig.style.advanced.editorpages.SimplePointEditorPage";//$NON-NLS-1$
	            }
	        } else if (SLD.LINE.supports(selectedLayer)) {
	            if(ids.contains("org.locationtech.udig.style.advanced.editorpages.SimpleLineEditorPage")) {//$NON-NLS-1$
	            	pageId = "org.locationtech.udig.style.advanced.editorpages.SimpleLineEditorPage";//$NON-NLS-1$
	            }
	        } else if (SLD.POLYGON.supports(selectedLayer)) {
	        	if(ids.contains("org.locationtech.udig.style.advanced.editorpages.SimplePolygonEditorPage")) {//$NON-NLS-1$
	            	pageId = "org.locationtech.udig.style.advanced.editorpages.SimplePolygonEditorPage";//$NON-NLS-1$
	            }
	        } else if (selectedLayer.getGeoResource().getInfo(new NullProgressMonitor()).getDescription()
	        		.equals("grassbinaryraster")) { //$NON-NLS-1$
	        	if(ids.contains("org.locationtech.udig.style.jgrass.colors.JGrassRasterStyleEditorPage")) {//$NON-NLS-1$
				 	pageId = "org.locationtech.udig.style.jgrass.colors.JGrassRasterStyleEditorPage";//$NON-NLS-1$
				}
	        }
		} catch (IOException e) {
			pageId = "simple";//$NON-NLS-1$
		}

        StyleEditorDialog dialog = StyleEditorDialog.createDialogOn(shell, pageId, selectedLayer, manager);
        dialog.open();
    }

    public void selectionChanged( IAction action, ISelection selection ) {
        if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
            return;

        IStructuredSelection sselection = (IStructuredSelection) selection;
        if (sselection.getFirstElement() instanceof Layer) {
            selectedLayer = (Layer) sselection.getFirstElement();
        }
    }

    public void dispose() {
        plugin = null;
        workbenchWindow = null;
    }

    public void init( IWorkbenchWindow window ) {
    }

    public void init( IViewPart view ) {
    }

}
