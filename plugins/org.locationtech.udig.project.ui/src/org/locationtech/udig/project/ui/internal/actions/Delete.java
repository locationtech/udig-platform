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
package org.locationtech.udig.project.ui.internal.actions;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.locationtech.udig.core.Pair;
import org.locationtech.udig.core.filter.AdaptingFilter;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.command.factory.EditCommandFactory;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectElement;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.commands.edit.DeleteManyFeaturesCommand;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.UDIGGenericAction;
import org.locationtech.udig.project.ui.commands.DrawCommandFactory;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.internal.UDIGEditorInputDescriptor;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Deletes the selected elements from a project.
 * 
 * @author jeichar
 * @since 0.3
 */
public class Delete extends UDIGGenericAction {
    
    /**
     * Indicates whether to run the commands synchronously or not.
     */
    private boolean runSync = false;
    private boolean canDeleteProjectElements;

    public Delete( boolean canDeleteProjectElements){
        this.canDeleteProjectElements = canDeleteProjectElements;
    }
    /**
     * Run has been overriden to ignore deleting maps.
     * <p>
     * This is due to UDIG-1836 where it is noted that the default selection
     * provided by a MapEditor is a MapImpl (and thus 90% of the time hitting delete
     * will result in the map being removed).
     * <p>
     * The ProjectExplorer view and LayerView make use of their own delete action and thus are
     * not effected here.
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action ) {
        ISelection sel = getSelection();
        if (sel == null || sel.isEmpty() || !(sel instanceof IStructuredSelection)) {
            return; // nothing selected to delete
        }
        IStructuredSelection selection = (IStructuredSelection) sel;
        
        /*
         * Optimization for a set of objects in selection of the same nature. The goal: run an
         * operation once over all selected objects.
         */
        ArrayList<Layer> layers = new ArrayList<Layer>(selection.size());
        
        Object firstElem = selection.iterator().next();
        
        Pair<Boolean, Integer> stateData; 
        
        if (canDeleteProjectElements && firstElem instanceof Project) {
            stateData = showErrorMessage(selection.size(), (Project) firstElem);
        } else if (canDeleteProjectElements && firstElem instanceof IProjectElement) {
            stateData = showErrorMessage(selection.size(), (ProjectElement) firstElem);
        } else if (firstElem instanceof Layer) {
            stateData = showErrorMessage(selection.size(), (Layer) firstElem);
        } else if (firstElem instanceof SimpleFeature) {
            stateData = showErrorMessage(selection.size(), (SimpleFeature) firstElem);
        } else if (firstElem instanceof AdaptingFilter) {
            AdaptingFilter<?> f = (AdaptingFilter<?>) firstElem;
            ILayer layer = (ILayer) f.getAdapter(ILayer.class);
            stateData = showErrorMessage(selection.size(), layer,f);
        } else {
            stateData = null;
        }
        
        if( stateData == null || stateData.getRight() == Window.CANCEL ){
            return; // thanks for playing
        }
        
        for( Iterator<?> iter = selection.iterator(); iter.hasNext(); ) {
            Object element = iter.next();

            if (canDeleteProjectElements && element instanceof Project) {
                operate((Project) element, stateData);
            } else if (canDeleteProjectElements && element instanceof IProjectElement) {
                operate((ProjectElement) element, stateData);
            } else if (element instanceof Layer) {
                layers.add((Layer) element);
            } else if (element instanceof SimpleFeature) {
                operate((SimpleFeature) element, stateData);
            }else if (element instanceof AdaptingFilter) {
                AdaptingFilter<?> f = (AdaptingFilter<?>) element;
                ILayer layer = (ILayer) f.getAdapter(ILayer.class);
                operate(layer,f, stateData);
            }
        }

        if (!layers.isEmpty()) {
            operate(layers.toArray(new Layer[layers.size()]), stateData);
        }

        // layers = null;
    }
    
    /**
     * @see org.locationtech.udig.project.ui.UDIGGenericAction#operate(org.locationtech.udig.project.Layer)
     */
    protected void operate( Layer layer ) {
        if (layer == null || layer.getMap() == null)
            return;

        UndoableMapCommand command = EditCommandFactory.getInstance().createDeleteLayers(
                new ILayer[]{layer});
        // UndoableMapCommand command =
        // EditCommandFactory.getInstance().createDeleteLayer((ILayer)layer);
        executeCommand(command, layer.getMapInternal());
    }

    /**
     * @see org.locationtech.udig.project.ui.UDIGGenericAction#operate(org.locationtech.udig.project.internal.Layer[])
     */
    @Override
    protected void operate( Layer[] layers, Object context ) {
        Pair<Boolean, Integer> pair = (Pair<Boolean, Integer>) context;
        if( pair == null || pair.right() == Window.CANCEL){
            return; // nothing to do
        }
        if (layers != null && layers.length > 0) {
            /*
             * Layers can exist in different maps. For each map the standalone command should be
             * used to remove only layers that are contained in this map.
             */
            HashMap<IMap, List<Layer>> distributor = new HashMap<IMap, List<Layer>>();

            for( Layer layer : layers ) {
                IMap map = layer.getMap();

                if (distributor.containsKey(map)) {
                    distributor.get(map).add(layer);

                } else {
                    List<Layer> list = new ArrayList<Layer>();
                    list.add(layer);
                    distributor.put(map, list);
                }
            }
            for( Entry<IMap, List<Layer>> entry : distributor.entrySet() ) {
                IMap map = entry.getKey();
                Layer[] removedLayers = entry.getValue().toArray(new Layer[0]);
                UndoableMapCommand command = EditCommandFactory.getInstance().createDeleteLayers(
                        removedLayers);
                executeCommand(command, map);
            }

        }
    }

    void executeCommand( MapCommand command, IMap map ) {
        if (runSync) {
            map.sendCommandSync(command);
        } else {
            map.sendCommandASync(command);
        }
    }

    /**
     * @see org.locationtech.udig.project.ui.UDIGGenericAction#operate(org.locationtech.udig.project.IProjectElement)
     */
    @SuppressWarnings("unchecked")
    protected void operate( ProjectElement element, Object context ) {
        if (element == null)
            return;
        Pair<Boolean, Integer> pair = (Pair<Boolean, Integer>) context;
        if( pair == null || pair.right() == Window.CANCEL){
            return; // nothing to do
        }
        boolean deleteFiles = pair.left();
        int returnCode = pair.right();
        doDelete(element, deleteFiles, returnCode);
    }
    
    @Override
    protected Pair<Boolean, Integer> showErrorMessage( int size, ILayer layer, AdaptingFilter firstElement ) {
        return new Pair<Boolean, Integer>(false,Window.OK);
    }
    @Override
    protected Pair<Boolean, Integer> showErrorMessage( int size, Layer firstElement ) {
        return new Pair<Boolean, Integer>(false,Window.OK);
    }
    
    @Override
    protected Pair<Boolean, Integer> showErrorMessage( int size, SimpleFeature firstElement ) {
        return new Pair<Boolean, Integer>(false,Window.OK);
    }

    @Override
    protected Pair<Boolean, Integer> showErrorMessage( int size, ProjectElement element ) {
        String deleteOne = Messages.Delete_deleteElement;
        String name = element.getName();
        String deleteMany = Messages.Delete_deleteMultipleElements;
        return dialog(size, deleteOne, name, deleteMany);
    }

    @Override
    protected Pair<Boolean, Integer> showErrorMessage( int size, Project element ) {
        String deleteOne = Messages.Delete_deleteProject;
        String name = element.getName();
        String deleteMany = Messages.Delete_deleteMultipleProjects;
        return dialog(size, deleteOne, name, deleteMany);
    }

    private Pair<Boolean, Integer> dialog( int size, String deleteOne, String name, String deleteMany ) {
        String message;
        
        if (size == 1) {
            message = MessageFormat.format(deleteOne, name);
        } else {
            message = MessageFormat.format(deleteMany, size);
        }

        /*
         *  FIXME as of https://jira.codehaus.org/browse/UDIG-1974
         *  there is a bug in the maps removal.
         *  The below should be fixed once the bug has been fixed.
         */
        
        // START OLD CODE
            //        MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(Display
            //                .getCurrent().getActiveShell(), Messages.Delete_delete, message,
            //                Messages.Delete_filesystem, getDoDelete(), null, null);
                    // note: we will do our own preference store persistence, since the built in one is
                    // backwards
            //        boolean deleteFiles = dialog.getToggleState();
            //        int returnCode = dialog.getReturnCode();
            //        if (returnCode == Window.OK) {
        // END OLD CODE
        
        // START TEMPORARY NEW CODE
        boolean delete = MessageDialog.openConfirm( Display.getCurrent().getActiveShell(), Messages.Delete_delete, message );
        boolean deleteFiles = getDoDelete();
        // END TEMPORARY NEW CODE
        
        
        if( delete ){
            //if (deleteFiles != getDoDelete()) {
            //    setDoDelete(deleteFiles);
            //}
            return Pair.create(deleteFiles, Window.OK);
        } else {
            // Window.CANCEL
            return Pair.create(null, Window.CANCEL);
        }
    }

    @Override
    protected void operate( ILayer layer, AdaptingFilter filter, Object context ) {
        Pair<Boolean, Integer> pair = (Pair<Boolean, Integer>) context;
        if( pair == null || pair.right() == Window.CANCEL){
            return; // nothing to do
        }
        layer.getMap().sendCommandASync(new DeleteManyFeaturesCommand(layer, filter));
    }

    protected final void doDelete( ProjectElement element, boolean deleteFiles, int returncode ) {
        if (returncode != Window.CANCEL) {
            for( UDIGEditorInputDescriptor desc : ApplicationGIS.getEditorInputs(element) ) {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage();
                IEditorPart editor = page.findEditor(desc.createInput(element));
                if (editor != null)
                    page.closeEditor(editor, false);
            }

            List<ProjectElement> elements = element.getElements(ProjectElement.class);
            for( ProjectElement projectElement : elements ) {
                doDelete(projectElement, deleteFiles, returncode);
            }

            Project projectInternal = element.getProjectInternal();
            if (projectInternal != null)
                projectInternal.getElementsInternal().remove(element);
            else {
                Project project = findProject(element);
                if (project != null)
                    project.getElementsInternal().remove(element);
            }
            Resource resource = element.eResource();
            if (resource != null) {
                resource.getContents().remove(element);
                resource.unload();
            }
            if (deleteFiles) {
                try {
                    if (resource == null) {
                        return;
                    }
                    URI resourceUri = resource.getURI();
                    String path = resourceUri.toFileString();
                    resource.unload();
                    if (resourceUri.hasAuthority()) {
                        path = StringUtils.removeStart(path, "//");
                    }
                    /*
                    int lastIndexOf = path.lastIndexOf('/');
                    if (lastIndexOf == -1)
                        lastIndexOf = path.length();
                    path = path.substring(0, lastIndexOf);
                    */
                    final File file = new File(path);
                    deleteFile(file);
                } catch (Exception e) {
                    ProjectUIPlugin.log("Error deleting project element file", e); //$NON-NLS-1$
                }
            }
        }
    }

    private Project findProject( ProjectElement element ) {
        List< ? extends Project> projects = ApplicationGISInternal.getProjects();
        for( Project project : projects ) {
            if (project.getElements().contains(element))
                return project;
        }
        return null;
    }

    /**
     * @see org.locationtech.udig.project.ui.UDIGGenericAction#operate(org.locationtech.udig.project.Project)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void operate( Project project, Object context ) {
        if (project == null || context == null){
            return;
        }
        Pair<Boolean, Integer> pair = (Pair<Boolean, Integer>) context;
        boolean deleteFiles = pair.left();
        int returnCode = pair.right();
        doDelete(project, deleteFiles, returnCode);
    }

    protected final void doDelete( Project project, boolean deleteProjectFiles, int returncode ) {
        if (returncode != Window.CANCEL) {
            Resource resource = project.eResource();
            if (!deleteProjectFiles) {
                try {
                    resource.save(null);
                    resource.getContents().remove(project);
                } catch (IOException e) {
                    ProjectUIPlugin.log(null, e);
                }
            }

            List<ProjectElement> toRemove = new ArrayList<ProjectElement>();
            toRemove.addAll(project.getElementsInternal());
            boolean oldrunSyn = runSync;

            this.runSync = true;
            for( ProjectElement element : toRemove ) {
                doDelete(element, deleteProjectFiles, returncode);
            }
            runSync = oldrunSyn;

            resource.setModified(false);
            if (ApplicationGIS.getActiveProject() == project)
                ProjectPlugin.getPlugin().getProjectRegistry().setCurrentProject(null);

            ProjectPlugin.getPlugin().getProjectRegistry().getProjects().remove(project);
            resource.getContents().clear();
            ResourceSet resourceSet = resource.getResourceSet();
            String path = resource.getURI().toFileString();

            resource.unload();

            if (deleteProjectFiles) {
                try {
                    resourceSet.getResources().remove(resource);
                    resource.unload();
                    /*
                    int lastIndexOf = path.lastIndexOf('/');
                    if (lastIndexOf == -1)
                        lastIndexOf = path.length();
                    path = path.substring(0, lastIndexOf);
                    */
                    final File file = new File(path);
                    deleteFile(file);
                    //also delete the directory containing the files
                    String dirPath = FilenameUtils.getFullPathNoEndSeparator(path);
                    if (dirPath.endsWith(".udig")) {
                        deleteFile(new File(dirPath));
                    }
                } catch (Exception e) {
                    ProjectUIPlugin.log("Error deleting project file", e); //$NON-NLS-1$
                }
            }
        }
    }

    private void deleteFile( File file ) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for( File file2 : files ) {
                deleteFile(file2);
            }
        }

        file.delete();

    }

    /**
     * @see org.locationtech.udig.project.ui.UDIGGenericAction#operate(org.geotools.feature.SimpleFeature)
     */
    @Override
    protected void operate( final SimpleFeature feature, Object c ) {
        IAdaptable adaptableFeature = null;
        if (feature instanceof IAdaptable) {
            adaptableFeature = (IAdaptable) feature;
        }
        if (adaptableFeature == null) {
            adaptableFeature = (IAdaptable) Platform.getAdapterManager().getAdapter(feature,
                    IAdaptable.class);
        }
        if (adaptableFeature == null)
            return;

        final Layer layer = (Layer) adaptableFeature.getAdapter(Layer.class);

        IDrawCommand command = DrawCommandFactory.getInstance().createDrawFeatureCommand(feature,
                layer);

        ViewportPane pane = (ViewportPane) layer.getMapInternal().getRenderManager()
                .getMapDisplay();
        pane.addDrawCommand(command);

        PlatformGIS.syncInDisplayThread(PlatformUI.getWorkbench().getDisplay(), new Runnable(){
            public void run() {

                boolean result;
                result = MessageDialog.openConfirm(PlatformUI.getWorkbench().getDisplay()
                        .getActiveShell(), Messages.DeleteFeature_confirmation_title,
                        Messages.DeleteFeature_confirmation_text);

                if (result) {
                    UndoableMapCommand c = EditCommandFactory.getInstance().createDeleteFeature(
                            feature, layer);
                    executeCommand(c, layer.getMap());
                }
            }
        });
        command.setValid(false);
        pane.repaint();

    }

    /**
     * Determines whether the command executions should happen synchronously or not.
     * 
     * @param runSync
     */
    public void setRunSync( boolean runSync ) {
        this.runSync = runSync;
    }

    private boolean getDoDelete() {
        return ProjectPlugin.getPlugin().getPreferenceStore().getBoolean(
                PreferenceConstants.P_PROJECT_DELETE_FILES);
    }

    private void setDoDelete( boolean deleteFiles ) {
        ProjectPlugin.getPlugin().getPreferenceStore().setValue(
                PreferenceConstants.P_PROJECT_DELETE_FILES, deleteFiles);
    }

}
