/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.printing.ui.internal.editor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.EventObject;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.ui.internal.PrintAction;
import net.refractions.udig.printing.ui.internal.PrintingPlugin;
import net.refractions.udig.printing.ui.internal.editor.parts.PartFactory;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHelper;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ViewportMouseWheelHelper;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * An editor for print pages. Uses GEF to edit the layout of the pages.
 * 
 * @author Richard Gould
 * @since 0.3
 */
public class PageEditor extends GraphicalEditorWithFlyoutPalette implements IAdaptable {

    public final static String EDIT_MAP = "edit map"; //$NON-NLS-1$
    public final static String ID = "net.refractions.udig.printing.ui.internal.editor.pageEditor"; //$NON-NLS-1$

    private boolean savePreviouslyNeeded;
    private Page page;
    private PaletteRoot paletteRoot;
    private PageEditorOutlinePage outlinePage;
    private ZoomManager zoomManager;

    public PageEditor() {
        setEditDomain(new DefaultEditDomain(this));
    }

    protected void configureGraphicalViewer() {
        super.configureGraphicalViewer();

        GraphicalViewer graphicalViewer = getGraphicalViewer();
        ScalableFreeformRootEditPart scalableFreeformRootEditPart = new ScalableFreeformRootEditPart();
        graphicalViewer.setRootEditPart(scalableFreeformRootEditPart);
        zoomManager = scalableFreeformRootEditPart.getZoomManager();

        graphicalViewer.setEditPartFactory(new PartFactory());

        ContextMenuProvider provider = new PageContextMenuProvider(graphicalViewer,
                getActionRegistry());
        graphicalViewer.setContextMenu(provider);
        getSite().registerContextMenu("net.refractions.udig.printing.editor.contextmenu", //$NON-NLS-1$
                provider, graphicalViewer);

    }

    public void commandStackChanged( EventObject event ) {
        if (isDirty()) {
            if (!this.savePreviouslyNeeded) {
                this.savePreviouslyNeeded = true;
                firePropertyChange(IEditorPart.PROP_DIRTY);
            }
        } else {
            savePreviouslyNeeded = false;
            firePropertyChange(IEditorPart.PROP_DIRTY);
        }
        super.commandStackChanged(event);
    }

    protected void setInput( IEditorInput input ) {
        super.setInput(input);

        page = (Page) ((PageEditorInput) input).getProjectElement();

        setPartName(page.getName());
    }

    protected void initializeGraphicalViewer() {
        getGraphicalViewer().setContents(this.page);
    }

    @Override
    protected void createGraphicalViewer( Composite parent ) {
        GraphicalViewer viewer = new PrintingScrollingGraphicalViewer(this);
        viewer.createControl(parent);
        setGraphicalViewer(viewer);
        configureGraphicalViewer();
        hookGraphicalViewer();
        initializeGraphicalViewer();

    }

    public void doSave( IProgressMonitor monitor ) {
        // TODO fix this at some point, currently doesn't work
        // Platform.run(new SafeRunnable(){
        //
        // public void run() throws Exception {
        // page.getProject().eResource().save(null);
        // }
        //            
        // });
        // setDirty(false);
    }

    public void doSaveAs() {
    }

    /* TODO Permit saving at some point */
    public boolean isDirty() {
        return false;
    }

    /* TODO Permit saving at some point */
    public boolean isSaveAsAllowed() {
        return true;
    }

    @SuppressWarnings("unchecked")
    protected void createActions() {
        super.createActions();
        ActionRegistry registry = getActionRegistry();

        Collection<BoxAction> actions = PrintingPlugin.getBoxExtensionActions(this);

        for( IAction action : actions ) {
            registry.registerAction(action);
            getSelectionActions().add(action.getId());
        }

    }

    protected PaletteRoot getPaletteRoot() {

        if (paletteRoot == null) {
            paletteRoot = PageEditorPaletteFactory.createPalette();
        }

        return paletteRoot;
    }

    protected FlyoutPreferences getPalettePreferences() {
        return PageEditorPaletteFactory.createPalettePreferences();
    }

    protected PaletteViewerProvider createPaletteViewerProvider() {
        return new PaletteViewerProvider(getEditDomain()){
            protected void configurePaletteViewer( PaletteViewer viewer ) {
                super.configurePaletteViewer(viewer);
                // create a drag source listener for this palette viewer
                // together with an appropriate transfer drop target listener, this will enable
                // model element creation by dragging a CombinatedTemplateCreationEntries
                // from the palette into the editor
                // @see ShapesEditor#createTransferDropTargetListener()
                viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
            }
        };
    }

    public void createPartControl( Composite parent ) {
        super.createPartControl(parent);

        // enable printing
        IActionBars actionBars = getEditorSite().getActionBars();
        actionBars.setGlobalActionHandler(ActionFactory.PRINT.getId(), new PrintAction());
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter( Class type ) {
        if (type == IContentOutlinePage.class) {
            if (outlinePage == null) {
                outlinePage = new PageEditorOutlinePage(this, new TreeViewer());
            }
            return outlinePage;
        }
        if (type.isAssignableFrom(Map.class)) {
            Map found = null;
            for( Box box : page.getBoxes() ) {
                if (box instanceof IAdaptable) {
                    Object obj2 = ((IAdaptable) box).getAdapter(Map.class);
                    if (obj2 instanceof Map) {
                        /*
                         * If multiple maps are found, return null. This will prevent entities
                         * that operate only on one map from losing their context.
                         * (Imagine the layers view with two map objects selected. It would 
                         * only display the layers from one of the objects - confusing to user)
                         */
                        if (found != null) {
                            found = null;
                            break;
                        }
                        found = (Map) obj2;
                    }
                }
            }
            if (found != null) {
                return found;
            }
        }
        if (type.isAssignableFrom(GraphicalViewer.class)) {
            return getGraphicalViewer();
        }
        if (type.isAssignableFrom(ZoomManager.class)) {
            return zoomManager;
        }

        return super.getAdapter(type);
    }
    void disposeOutlinePage() {
        this.outlinePage = null;
    }

    Page getModel() {
        return page;
    }

    @Override
    public void dispose() {

        if (PlatformUI.getWorkbench().isClosing()) {
            ProjectPlugin.getPlugin().turnOffEvents();

            // save the map's URI in the preferences so that it will be loaded the next time udig is
            // run.

            Resource resource = this.page.eResource();
            if (resource != null) {
                try {
                    IPreferenceStore p = ProjectUIPlugin.getDefault().getPreferenceStore();
                    int numEditors = p.getInt(ID);
                    String id = ID + ":" + numEditors; //$NON-NLS-1$
                    numEditors++;
                    p.setValue(ID, numEditors);
                    String value = resource.getURI().toString();
                    p.setValue(id, value);
                } catch (Exception e) {
                    ProjectUIPlugin.log("Failure saving which maps are open", e); //$NON-NLS-1$
                }
            }
        }

        try {
            getSite().getWorkbenchWindow().run(false, false, new IRunnableWithProgress(){

                public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                        InterruptedException {
                    Project p = page.getProjectInternal();
                    try {
                        if (p != null) {
                            if (p.eResource() != null && p.eResource().isModified()) {
                                p.eResource().save(ProjectPlugin.getPlugin().saveOptions);
                            }
                            saveAndUnload();
                        } else {
                            saveAndUnload();
                        }
                    } catch (IOException e) {
                        ProjectPlugin.log("", e); //$NON-NLS-1$
                    }

                    // need to kick the Project so viewers will update
                    p.eNotify(new ENotificationImpl((InternalEObject) p, Notification.SET,
                            ProjectPackage.PROJECT__ELEMENTS_INTERNAL, null, null));
                }

                private void saveAndUnload() throws IOException {
                    final Resource resource = page.eResource();

                    resource.save(ProjectPlugin.getPlugin().saveOptions);
                    if (!resource.getContents().contains(
                            ProjectPlugin.getPlugin().getProjectRegistry()))
                        ;
                    resource.unload();
                }
            });
        } catch (Exception e) {
            ProjectPlugin.log("", e); //$NON-NLS-1$
        }
        super.dispose();
    }

    // declare methods for the PageEditorOutlinePage so it can access them

    @Override
    protected SelectionSynchronizer getSelectionSynchronizer() {
        return super.getSelectionSynchronizer();
    }

    @Override
    protected DefaultEditDomain getEditDomain() {
        return super.getEditDomain();
    }

    @Override
    protected ActionRegistry getActionRegistry() {
        return super.getActionRegistry();
    }

    // done declarations for PageEditorOutlinePage
}