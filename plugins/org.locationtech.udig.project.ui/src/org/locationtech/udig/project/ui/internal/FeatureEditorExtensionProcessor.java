/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.ui.IUDIGDialogPage;
import org.locationtech.udig.project.ui.IUDIGView;
import org.locationtech.udig.project.ui.internal.tool.ToolContext;
import org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Processes the feature editor extension points and creates the menu items.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public class FeatureEditorExtensionProcessor {

    FeatureEditorViewpartListener partListener;

    Map<SimpleFeatureType, EditActionContribution> selectedEditors = new HashMap<SimpleFeatureType, EditActionContribution>();

    List<FeatureEditorLoader> editorLoaders = new ArrayList<FeatureEditorLoader>();

    static final String CURRENT_LOADER_ID = "FeatureEditorCurrentLoader"; //$NON-NLS-1$

    private static final String FEATURE_EDITOR_ID = "org.locationtech.udig.project.ui.featureEditor"; //$NON-NLS-1$

    private boolean running;

    /**
     * Construct <code>FeatureEditorExtensionProcessor</code>.
     */
    public FeatureEditorExtensionProcessor() {

        List<IConfigurationElement> list = ExtensionPointList
                .getExtensionPointList(FEATURE_EDITOR_ID);
        for( IConfigurationElement element : list ) {
            FeatureEditorLoader loader = new FeatureEditorLoader(this, element);
            ScopedPreferenceStore preferences = ProjectPlugin.getPlugin().getPreferenceStore();
            if (loader.id.equals(preferences
                    .getString(PreferenceConstants.P_DEFAULT_FEATURE_EDITOR)))
                editorLoaders.add(0, loader);
            else
                editorLoaders.add(loader);
        }
    }

    /**
     * Creates the edit feature action
     * 
     * @return the edit feature action
     */
    public IContributionItem getEditFeatureAction( final ISelection selection ) {
        if (selection.isEmpty())
            return new GroupMarker("editAction"); //$NON-NLS-1$

        if (!(selection instanceof IStructuredSelection))
            return new GroupMarker("editAction"); //$NON-NLS-1$
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        if (!sameFeatureTypes(structuredSelection))
            return new GroupMarker("editAction"); //$NON-NLS-1$

        SimpleFeature feature = (SimpleFeature) structuredSelection.getFirstElement();
        IContributionItem item = selectedEditors.get(feature.getFeatureType());
        if (item != null)
            return item;

        FeatureEditorLoader loader = getClosestMatch(selection);
        if (loader != null)
            return createEditAction(loader, selection, feature);

        return new GroupMarker("editAction"); //$NON-NLS-1$
    }

    /**
     * returns the feature editor that is customized closest for the feature(s) in the selection
     * 
     * @param selection a selection that contains 1 or more features.
     * @return the feature editor that is customized closest for the feature(s) in the selection.
     */
    public FeatureEditorLoader getClosestMatch( ISelection selection ) {
        int bestValue = Integer.MAX_VALUE;
        FeatureEditorLoader best = null;
        for( FeatureEditorLoader loader : editorLoaders ) {
            int value = loader.match(selection);
            if (value == -1)
                continue;
            if (value == 0)
                return loader;
            if (value < bestValue) {
                bestValue = value;
                best = loader;
            }
        }

        return best;
    }

    /**
     * Creates the edit menu item.
     * 
     * @param loader
     * @param selection
     * @param feature
     * @return
     */
    EditActionContribution createEditAction( final FeatureEditorLoader loader,
            final ISelection selection, SimpleFeature feature ) {
        EditActionContribution item;
        item = new EditActionContribution(new EditAction(loader, selection));
        selectedEditors.put(feature.getFeatureType(), item);
        return item;
    }

    static class EditAction extends Action {
        private ISelection selection;
        private final FeatureEditorLoader loader;

        public EditAction( FeatureEditorLoader loader, ISelection selection ) {
            super(Messages.FeatureEditorExtensionProcessor_editMenu, loader.icon);
            this.selection = selection;
            this.loader = loader;
            setId(loader.id);
        }

        @Override
        public void runWithEvent( Event event ) {
            loader.open(event.display, selection);
        }

        public void setSelection( ISelection selection ) {
            this.selection = selection;
        }
    }

    static class EditActionContribution extends ActionContributionItem {

        private EditAction editAction;

        public EditActionContribution( IAction action ) {
            super(action);
            editAction = (EditAction) action;
        }

        public void setSelection( ISelection selection ) {
            editAction.setSelection(selection);
        }

    }

    boolean sameFeatureTypes( IStructuredSelection structuredSelection ) {
        SimpleFeatureType type = null;
        for( Iterator iter = structuredSelection.iterator(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (!(obj instanceof SimpleFeature))
                return false;

            SimpleFeature feature = (SimpleFeature) obj;
            if (type == null)
                type = feature.getFeatureType();
            else if (!type.equals(feature.getFeatureType()))
                return false;
        }
        return true;
    }

    /**
     * Creates the edit/editWith menu items if the current selection is a feature.
     * 
     * @return the edit/editWith menu items if the current selection is a feature.
     */
    public IContributionItem getEditWithFeatureMenu( ISelection selection ) {
        if (selection.isEmpty())
            return new GroupMarker("editWithMenu"); //$NON-NLS-1$
        MenuManager editWithMenu = new MenuManager(
                Messages.FeatureEditorExtensionProcessor_editWithMenu);
        for( FeatureEditorLoader loader : editorLoaders ) {
            IAction editorAction = loader.getAction(selection);
            if (editorAction != null)
                editWithMenu.add(editorAction);
        }
        editWithMenu.setVisible(true);
        if (editWithMenu.getItems().length == 0)
            return new GroupMarker("editWithMenu"); //$NON-NLS-1$
        return editWithMenu;
    }

    /**
     * Creates and registers the Workbench part listener with the workbench window.
     */
    public void startPartListener() {
        if (partListener == null) {
            partListener = new FeatureEditorViewpartListener();
        }
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(
                    partListener);
            running = true;
        } catch (Exception e) {
            ProjectUIPlugin.log(null, e);
        }
    }

    /**
     * De-registers the Workbench part listener with the workbench window.
     */
    public void stopPartListener() {
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(
                    partListener);
        } catch (Exception e) {
            // do nothing
        }
        running = false;
    }

    static class FeatureEditorViewpartListener extends AdapterImpl implements IPartListener2 {

        ToolContext currentContext;

        private List<IUDIGView> views = new ArrayList<IUDIGView>();

        FeatureEditorViewpartListener() {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage();
            IViewReference[] refs = page.getViewReferences();

            for( IViewReference reference : refs ) {
                IWorkbenchPart part = reference.getPart(false);
                if (page.isPartVisible(part) && part instanceof IUDIGView)
                    views.add((IUDIGView) part);
            }
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partActivated( IWorkbenchPartReference partRef ) {
            partVisible(partRef);

        }

        /**
         * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partBroughtToTop( IWorkbenchPartReference partRef ) {
            partVisible(partRef);
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partClosed( IWorkbenchPartReference partRef ) {
            if (partRef.getPart(false) instanceof IUDIGView) {
                views.remove(partRef.getPart(false));
            } else if (partRef.getPart(false) instanceof MapPart) {

                MapPart editor = (MapPart) partRef.getPart(false);
                synchronized (this) {

                    if (currentContext == null
                            || currentContext.getMapInternal() != editor.getMap())
                        return;

                    currentContext.getEditManagerInternal().eAdapters().remove(this);
                    currentContext = null;
                }
                for( IUDIGView view : views ) {
                    view.setContext(null);
                }
            }
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partDeactivated( IWorkbenchPartReference partRef ) {
            // do nothing
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
         */
        public synchronized void partOpened( IWorkbenchPartReference partRef ) {
            partVisible(partRef);
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partHidden( IWorkbenchPartReference partRef ) {
            // do nothing
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
         */
        @SuppressWarnings("unchecked")
        public void partVisible( IWorkbenchPartReference partRef ) {
            if (partRef.getPart(false) instanceof IUDIGView) {
                IUDIGView udigview = (IUDIGView) partRef.getPart(false);
                if (!views.contains(udigview))
                    views.add(udigview);
                SimpleFeature editFeature;
                ToolContext copy;
                synchronized (this) {
                    if (!validateContext(currentContext))
                        return;
                    copy = currentContext.copy();
                    editFeature = currentContext.getEditManager().getEditFeature();
                }
                try {
                    udigview.setContext(copy);
                    if (editFeature != null)
                        udigview.editFeatureChanged(editFeature);
                } catch (Throwable e) {
                    UiPlugin.log(udigview + " threw an exception", e); //$NON-NLS-1$
                }

            } else if (partRef.getPart(false) instanceof MapPart) {

                MapPart editor = (MapPart) partRef.getPart(false);
                synchronized (this) {
                    if (currentContext != null
                            && currentContext.getMapInternal() == editor.getMap())
                        return;
                    if (currentContext != null)
                        currentContext.getEditManagerInternal().eAdapters().remove(this);

                    currentContext = new ToolContextImpl();
                    currentContext.setMapInternal(editor.getMap());
                    currentContext.setRenderManagerInternal(editor.getMap()
                            .getRenderManagerInternal());
                    currentContext.getEditManagerInternal().eAdapters().add(this);
                    for( IUDIGView view : views ) {
                        try {
                            view.setContext(currentContext);
                        } catch (Throwable e) {
                            UiPlugin.log(view + " threw an exception", e); //$NON-NLS-1$
                        }
                    }
                }
            }
        }

        private boolean validateContext( ToolContext currentContext2 ) {
            if (currentContext2 == null)
                return false;
            if (currentContext2.getMap() == null)
                return false;
            if (currentContext2.getViewportModel() == null)
                return false;
            if (currentContext2.getRenderManager() == null)
                return false;
            if (currentContext2.getDisplay() == null)
                return false;
            if (currentContext2.getEditManager() == null)
                return false;

            return true;
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partInputChanged( IWorkbenchPartReference partRef ) {
            // do nothing
        }

        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged( final Notification msg ) {
            if (msg.getNotifier() instanceof EditManager) {
                if (msg.getFeatureID(EditManager.class) == ProjectPackage.EDIT_MANAGER__EDIT_FEATURE) {
                    PlatformGIS.syncInDisplayThread(new Runnable(){
                        public void run() {
                            updateEditFeatureViews(msg);
                        }
                    });
                }
            }
        }

        private void updateEditFeatureViews( Notification msg ) {
            SimpleFeature newFeature = (SimpleFeature) msg.getNewValue();
            for( IUDIGView view : views ) {
                try {
                    view.editFeatureChanged(newFeature);
                } catch (Throwable e) {
                    UiPlugin.log(view + " threw an exception", e); //$NON-NLS-1$
                }
            }
        }

    }

    static class EditorDialog extends Dialog {

        private IUDIGDialogPage page;

        /**
         * Construct <code>EditorDialog</code>.
         * 
         * @param parentShell
         */
        protected EditorDialog( Shell parentShell, IUDIGDialogPage page ) {
            super(parentShell);
            this.page = page;
        }

        /**
         * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
         */
        protected Control createDialogArea( Composite parent ) {
            Composite composite = (Composite) super.createDialogArea(parent);
            page.createControl(composite);
            GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
            composite.setLayoutData(data);
            page.getControl().setLayoutData(data);
            return composite;
        }

    }

    /**
     * Indicates whether the listener has been added to the workbench
     * 
     * @return
     */
    public boolean isRunning() {
        return running;
    }

    public FeatureEditorLoader[] getEditorLoaders() {
        return editorLoaders.toArray(new FeatureEditorLoader[0]);
    }

}
