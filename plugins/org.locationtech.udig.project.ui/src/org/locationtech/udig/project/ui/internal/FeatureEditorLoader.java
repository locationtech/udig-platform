/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.project.ui.IUDIGDialogPage;
import org.locationtech.udig.project.ui.IUDIGView;
import org.locationtech.udig.project.ui.internal.FeatureEditorExtensionProcessor.EditActionContribution;
import org.locationtech.udig.project.ui.internal.tool.ToolContext;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Class used to represent a feature Editor; can perform "matches" against
 * a provided feature; and will create the view or dialog on request.
 * 
 * @author jones
 * @since 1.1.0
 */
public class FeatureEditorLoader {
    /** FeatureEditorLoader processor field */
    final FeatureEditorExtensionProcessor processor;

    private String dialogPage;

    String viewId;

    private String name;

    String id;

    ImageDescriptor icon;

    IAction contribution;

    private IConfigurationElement definition;

    private FeatureTypeMatch featureTypeMatcher;

    FeatureEditorLoader( FeatureEditorExtensionProcessor processor, IConfigurationElement definition ) {
        this.processor = processor;
        String iconID = definition.getAttribute("icon"); //$NON-NLS-1$
        if (iconID != null) {
            icon = AbstractUIPlugin.imageDescriptorFromPlugin(definition.getNamespaceIdentifier(),
                    iconID);
        }

        id = definition.getAttribute("id"); //$NON-NLS-1$
        name = definition.getAttribute("name"); //$NON-NLS-1$
        viewId = definition.getAttribute("viewId"); //$NON-NLS-1$
        dialogPage = definition.getAttribute("dialogPage"); //$NON-NLS-1$
        this.definition = definition;
        IConfigurationElement[] featureTypeDef = definition.getChildren("featureType");//$NON-NLS-1$
        if (featureTypeDef.length == 1)
            this.featureTypeMatcher = new FeatureTypeMatch(featureTypeDef[0]);
        else
            this.featureTypeMatcher = FeatureTypeMatch.ALL;
    }

    IAction getAction( final ISelection selection ) {
        if (!(selection instanceof IStructuredSelection)){
            return null;
        }

        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        if (!processor.sameFeatureTypes(structuredSelection)){
            return null;
        }

        final SimpleFeature feature = (SimpleFeature) structuredSelection.getFirstElement();
        if (featureTypeMatcher.matches(feature) == -1){
            return null; // no match
        }
        contribution = new Action(name, IAction.AS_RADIO_BUTTON){
            public void runWithEvent( org.eclipse.swt.widgets.Event event ) {

                // note: when the gui is used the contribution is checked by the framework.
                // so the gui must be used in order to test this.
                if (contribution.isChecked()) {
                    processor.createEditAction(FeatureEditorLoader.this, selection, feature);
                    open(event.display, selection);
                }
            }
        };
        contribution.setId(id);
        contribution.setImageDescriptor(icon);
        EditActionContribution selected = this.processor.selectedEditors.get(feature
                .getFeatureType());
        if (selected == null) {
            selected = processor.createEditAction(processor.getClosestMatch(selection), selection,
                    feature);
        } else {
            selected.setSelection(selection);
        }
        if (selected != null && selected.getId().equals(id))
            contribution.setChecked(true);
        else
            contribution.setChecked(false);

        return contribution;
    }

    public void open( Display display, ISelection selection ) {
        SimpleFeature feature = (SimpleFeature) ((IStructuredSelection) selection)
                .getFirstElement();

        if (viewId != null) {
            try {
                IUDIGView view = (IUDIGView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().showView(viewId, null, IWorkbenchPage.VIEW_VISIBLE);
                try {
                    view.editFeatureChanged(feature);
                } catch (Throwable e) {
                    UiPlugin.log(view + " threw an exception", e); //$NON-NLS-1$
                }
            } catch (PartInitException e) {
                ProjectUIPlugin.log(null, e);
            }
        } else if (dialogPage != null) {
            try {
                IUDIGDialogPage page = (IUDIGDialogPage) definition
                        .createExecutableExtension("dialogPage"); //$NON-NLS-1$
                ToolContext toolContext;
                synchronized (this.processor.partListener) {
                    toolContext = this.processor.partListener.currentContext;
                }
                page.setContext(toolContext);
                Dialog dialog = new FeatureEditorExtensionProcessor.EditorDialog(new Shell(display
                        .getActiveShell(), SWT.RESIZE | SWT.PRIMARY_MODAL), page);
                dialog.setBlockOnOpen(false);
                page.setFeature(feature);
                dialog.open();

                PlatformUI.getPreferenceStore().putValue(
                        FeatureEditorExtensionProcessor.CURRENT_LOADER_ID, id);

            } catch (CoreException e) {
                ProjectUIPlugin.log(null, e);
            }

        }
    }

    /**
     * Returns an integer indicating the accuracy of the match between a featureType and the one
     * declared in the FeatureEditor extension declaration.
     * <p>
     * -1 indicates no match, 0 a perfect match and each number higher is a match but a poorer
     * match.
     * </p>
     * 
     * @param selection
     * @return
     */
    public int match( ISelection selection ) {
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        if (!processor.sameFeatureTypes(structuredSelection))
            return -1;

        final SimpleFeature feature = (SimpleFeature) structuredSelection.getFirstElement();
        return featureTypeMatcher.matches(feature);
    }
    /**
     * Returns the dialog page if the feature editor is a dialog, or null otherwise.
     * 
     * @return the dialog page if the feature editor is a dialog, or null otherwise.
     */
    public String getDialogPage() {
        return dialogPage;
    }

    /**
     * Returns the name of the feature editor
     * 
     * @returnthe name of the feature editor
     */
    public String getName() {
        return name;
    }
    /**
     * Returns the viewid if the feature editor is a view, or null otherwise.
     * 
     * @return the dviewid if the feature editor is a view, or null otherwise.
     */
    public String getViewId() {
        return viewId;
    }

    public String getId() {
        return id;
    }
}
