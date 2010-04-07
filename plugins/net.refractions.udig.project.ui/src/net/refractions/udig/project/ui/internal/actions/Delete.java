/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.project.ui.internal.actions;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.refractions.udig.core.Pair;
import net.refractions.udig.core.filter.AdaptingFilter;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectElement;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.edit.DeleteManyFeaturesCommand;
import net.refractions.udig.project.preferences.PreferenceConstants;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.UDIGGenericAction;
import net.refractions.udig.project.ui.commands.DrawCommandFactory;
import net.refractions.udig.project.ui.commands.IDrawCommand;
import net.refractions.udig.project.ui.internal.ApplicationGISInternal;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.internal.UDIGEditorInputDescriptor;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
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

    /**
     * @see net.refractions.udig.project.ui.UDIGGenericAction#operate(net.refractions.udig.project.Layer)
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
     * @see net.refractions.udig.project.ui.UDIGGenericAction#operate(net.refractions.udig.project.internal.Layer[])
     */
    @Override
    protected void operate( Layer[] layers, Object c ) {

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
     * @see net.refractions.udig.project.ui.UDIGGenericAction#operate(net.refractions.udig.project.IProjectElement)
     */
    @SuppressWarnings("unchecked")
    protected void operate( ProjectElement element, Object context ) {
        if (element == null)
            return;
        Pair<Boolean, Integer> pair = (Pair<Boolean, Integer>) context;
        boolean deleteFiles = pair.left();
        int returnCode = pair.right();
        doDelete(element, deleteFiles, returnCode);
    }

    @Override
    protected Object showErrorMessage( int size, ProjectElement element ) {
        String deleteOne = Messages.Delete_deleteElement;
        String name = element.getName();
        String deleteMany = Messages.Delete_deleteMultipleElements;
        return dialog(size, deleteOne, name, deleteMany);
    }

    @Override
    protected Object showErrorMessage( int size, Project element ) {
        String deleteOne = Messages.Delete_deleteProject;
        String name = element.getName();
        String deleteMany = Messages.Delete_deleteMultipleProjects;
        return dialog(size, deleteOne, name, deleteMany);
    }

    private Object dialog( int size, String deleteOne, String name, String deleteMany ) {
        String message;
        if (size == 1) {
            message = MessageFormat.format(deleteOne, name);
        } else {
            message = MessageFormat.format(deleteMany, size);
        }

        MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(Display
                .getCurrent().getActiveShell(), Messages.Delete_delete, message,
                Messages.Delete_filesystem, getDoDelete(), null, null);
        // note: we will do our own preference store persistence, since the built in one is
        // backwards
        boolean deleteFiles = dialog.getToggleState();
        int returnCode = dialog.getReturnCode();
        if (returnCode == 0) {
            if (deleteFiles != getDoDelete()) {
                setDoDelete(deleteFiles);
            }
            return Pair.create(deleteFiles, returnCode);
        } else {
            return null;
        }
    }

    @Override
    protected void operate( ILayer layer, AdaptingFilter filter, Object c ) {
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
                    String path = resource.getURI().toFileString();
                    resource.unload();
                    int lastIndexOf = path.lastIndexOf('/');
                    if (lastIndexOf == -1)
                        lastIndexOf = path.length();
                    path = path.substring(0, lastIndexOf);
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
     * @see net.refractions.udig.project.ui.UDIGGenericAction#operate(net.refractions.udig.project.Project)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void operate( Project project, Object context ) {
        if (project == null)
            return;
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
                    int lastIndexOf = path.lastIndexOf('/');
                    if (lastIndexOf == -1)
                        lastIndexOf = path.length();
                    path = path.substring(0, lastIndexOf);
                    final File file = new File(path);
                    deleteFile(file);
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
     * @see net.refractions.udig.project.ui.UDIGGenericAction#operate(org.geotools.feature.SimpleFeature)
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
