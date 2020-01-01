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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.style.internal.StyleLayer;
import org.locationtech.udig.style.sld.IStyleEditorPageContainer;
import org.locationtech.udig.style.sld.SLDContent;
import org.locationtech.udig.style.sld.editor.internal.FilteredEditorDialog;
import org.locationtech.udig.style.sld.editor.internal.IEditorNode;
import org.locationtech.udig.style.sld.internal.Messages;
import org.opengis.util.ProgressListener;
import org.geotools.data.util.NullProgressListener;

/**
 * Preference dialog for the workbench including the ability to load/save preferences.
 */
public class StyleEditorDialog extends FilteredEditorDialog implements IStyleEditorPageContainer {

    public final static int IMPORT_ID = 32;
    public final static int EXPORT_ID = 33;
    public final static int APPLY_ID = 34;
    public final static int REVERT_ID = 35;
    public final static int DEFAULTS_ID = 36;
    public final static int OK_ID = 37;
    public final static int CANCEL_ID = 38;

    StyleLayer selectedLayer;

    public ProgressListener getProgressListener() {
        ProgressListener cancelProgress = new NullProgressListener();
        return cancelProgress ;
    }

    /**
     * Creates an style editor dialog open to a particular page. It is the responsibility of the
     * caller to then call <code>open()</code>. The call to <code>open()</code> will not return
     * until the dialog closes, so this is the last chance to manipulate the dialog.
     * 
     * @param shell The Shell to parent the dialog off of if it is not already created. May be
     *        <code>null</code> in which case the active workbench window will be used if
     *        available.
     * @param pageId The identifier of the page to open; may be <code>null</code>.
     * @return The dialog
     */
    public static final StyleEditorDialog createDialogOn( Shell shell, final String pageId,
            Layer selectedLayer, EditorPageManager manager ) {
        final StyleEditorDialog dialog;

        Shell parentShell = shell;
        if (parentShell == null) {
            // Determine a decent parent shell.
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
            if (workbenchWindow != null) {
                parentShell = workbenchWindow.getShell();
            } else {
                parentShell = null;
            }
        }

        dialog = new StyleEditorDialog(parentShell, manager);
        dialog.setSelectedNode(pageId);
        dialog.setSelectedLayer(selectedLayer);
        dialog.create();
        dialog.getShell().setText(Messages.StyleEditor_name); 
        dialog.filteredTree.getFilterCombo().setEnabled(true); // allow filtering

        if (pageId != null) {
            dialog.findNodeMatching(pageId);
        }
        return dialog;
    }

    /**
     * Creates a new dialog under the control of the given manager manager.
     * 
     * @param parentShell the parent shell
     * @param manager the preference manager
     */
    protected StyleEditorDialog( Shell parentShell, EditorPageManager manager ) {
        super(parentShell, manager);
    }

    @Override
    protected void setShellStyle( int newShellStyle ) {
        super.setShellStyle(SWT.SHELL_TRIM|SWT.APPLICATION_MODAL|SWT.RESIZE);
    }
    
    public void setSelectedLayer( Layer layer ) {
        if (selectedLayer == null && layer == null) {
            return;
        }
        if (layer != null && layer.equals(selectedLayer)) {
            return;
        }
        if (layer == null) {
            selectedLayer = null;
        } else {
            selectedLayer = new StyleLayer(layer);
        }
        // TODO: determine if we need to deal with layer listeners
    }

    public StyleLayer getSelectedLayer() {
        return selectedLayer;
    }

    public Style getStyle() {
        if (selectedLayer != null) {
            Object styleObject = selectedLayer.getStyleBlackboard().get(SLDContent.ID);
            if (styleObject instanceof Style) {
                // the style blackboard is a clone, therefore we don't need to clone the style or
                // anything like that
                return (Style) styleObject;
            }
        }
        return null;
    }

    public void setStyle( Style newStyle ) {
        StyleBlackboard styleBlackboard = selectedLayer.getStyleBlackboard();
        // put the style on the blackboard
        styleBlackboard.put(SLDContent.ID, newStyle);        
        styleBlackboard.setSelected(new String[]{SLDContent.ID});
    }

    private StyledLayerDescriptor getSLD( Style style ) {
        if (style != null) {
            StyledLayerDescriptor sld = null; // SLDs.styledLayerDescriptor(style);
            if (sld == null) {
                sld = SLDContent.createDefaultStyledLayerDescriptor(style);
            }
            return sld;
        }
        return null;
    }

    public StyledLayerDescriptor getSLD() {
        Style style = getStyle();
        return getSLD(style);
    }

    @Override
    public boolean showPage( IEditorNode node ) {
        return super.showPage(node);
    }

    @Override
    protected Control createButtonBar( Composite parent ) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        composite.setLayout(layout);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        composite.setLayoutData(data);
        composite.setFont(parent.getFont());

        // add import/export buttons
        addImportExportButtons(composite);

        // add apply/revert/close buttons
        addOkCancelRevertApplyButtons(parent, composite);

        return composite;
    }

    private void addOkCancelRevertApplyButtons( Composite parent, Composite composite ) {
        GridLayout layout;
        GridData data;
        Composite compRight = new Composite(composite, SWT.NONE);
        layout = new GridLayout(0, true); // columns are set at end because createButton sets them but we want 2x2
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        compRight.setLayout(layout);
        data = new GridData(SWT.END, SWT.CENTER, true, false);
        compRight.setLayoutData(data);
        compRight.setFont(parent.getFont());

        Button defaultsButton = createButton(compRight, DEFAULTS_ID,
                Messages.StyleEditorDialog_defaults, false); 
        defaultsButton.setEnabled(true);
        defaultsButton.addListener(SWT.Selection, new StyleEditorButtonListener(this));

        Button revertButton = createButton(compRight, REVERT_ID,
                Messages.StyleEditor_revert, false); 
        revertButton.setEnabled(false);
        revertButton.addListener(SWT.Selection, new StyleEditorButtonListener(this));
        
        Button applyButton = createButton(compRight, APPLY_ID,
                Messages.StyleEditor_apply, false); 
        applyButton.setEnabled(false);
        applyButton.addListener(SWT.Selection, new StyleEditorButtonListener(this));
        
        new Label(compRight, SWT.None);
        
        Button closeButton = createButton(compRight, CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false); 
        closeButton.setEnabled(true);
        closeButton.addListener(SWT.Selection, new StyleEditorButtonListener(this));
        
        Button okButton = createButton(compRight, OK_ID,
                IDialogConstants.OK_LABEL, false); 
        okButton.setEnabled(true);
        okButton.addListener(SWT.Selection, new StyleEditorButtonListener(this));
        
        layout.numColumns=3;
    }

    private void addImportExportButtons( Composite composite ) {
        GridLayout layout;
        Composite compLeft = new Composite(composite, SWT.NONE);
        layout = new GridLayout(0, true); // columns are incremented by createButton
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        compLeft.setLayout(layout);
        compLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));

        Button importButton = createButton(compLeft, IMPORT_ID,
                Messages.StyleEditor_import, false); 
        importButton.setEnabled(false);
        importButton.addListener(SWT.Selection, new StyleEditorButtonListener(this));
        Button exportButton = createButton(compLeft, EXPORT_ID,
                Messages.StyleEditor_export, false); 
        exportButton.setEnabled(false);
        exportButton.addListener(SWT.Selection, new StyleEditorButtonListener(this));
    }

    @Override
    public void updateButtons() {
        // TODO: button logic, SLD listeners
        getButton(IMPORT_ID).setEnabled(true);
        getButton(EXPORT_ID).setEnabled(true);

        getButton(APPLY_ID).setEnabled(true);
        getButton(REVERT_ID).setEnabled(true);
        getButton(OK_ID).setEnabled(true);
        getButton(CANCEL_ID).setEnabled(true);
    }
    
    /**
     * Used to enable the Apply button.
     */
    public void setExitButtonState() {
        getButton(APPLY_ID).setEnabled(true);
    }

    /**
     * Get the action used to apply changes.
     */
    public IAction getApplyAction() {
        final Button applyButton=getButton(APPLY_ID);
        return new Action(){
            @Override
            public void setText( String text ) {
                applyButton.setText(text);
            }
            
            @Override
            public String getText() {
                return applyButton.getText();
            }
            
            @Override
            public void setToolTipText( String toolTipText ) {
                applyButton.setToolTipText(toolTipText);
            }
            
            @Override
            public String getToolTipText() {
                return applyButton.getToolTipText();
            }
            
            @Override
            public void setEnabled( boolean enabled ) {
                applyButton.setEnabled(enabled);
            }
            
            @Override
            public void run() {
                Event event = new Event();
                event.display = applyButton.getDisplay();
                event.button = 1;
                event.widget = applyButton;
                applyButton.notifyListeners(SWT.Selection, event);
            }
            
            @Override
            public void setChecked( boolean checked ) {
                applyButton.setSelection(checked);
            }
            
        };
    }

}
