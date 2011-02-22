/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.refractions.udig.internal.ui.operations;

import java.util.ArrayList;
import java.util.Iterator;

import net.refractions.udig.ui.internal.Messages;
import net.refractions.udig.ui.operations.OpAction;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.WorkbenchPlugin;
/**
 * The Run Operation Dialog
 */
public class RunOperationDialog extends Dialog implements
        ISelectionChangedListener, IDoubleClickListener {

    private static final String DIALOG_SETTING_SECTION_NAME = "RunOperationDialog"; //$NON-NLS-1$

    private static final int LIST_HEIGHT = 300;

    private static final int LIST_WIDTH = 250;

    private static final String STORE_EXPANDED_CATEGORIES_ID = DIALOG_SETTING_SECTION_NAME
            + ".STORE_EXPANDED_CATEGORIES_ID"; //$NON-NLS-1$

    private static final String STORE_SELECTED_OPERATION_ID = DIALOG_SETTING_SECTION_NAME
            + ".STORE_SELECTED_OPERATION_ID"; //$NON-NLS-1$

    private TreeViewer tree;

    private Button okButton;

    private OpAction[] opActions = new OpAction[0];

    private OperationMenuFactory opMenuFactory;

    public RunOperationDialog(Shell parentShell, OperationMenuFactory opMenuFactory) {
        super(parentShell);
        this.opMenuFactory = opMenuFactory;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**
     * This method is called if a button has been pressed.
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID)
            saveWidgetValues();
        super.buttonPressed(buttonId);
    }

    /**
     * Notifies that the cancel button of this dialog has been pressed.
     */
    protected void cancelPressed() {
        opActions = new OpAction[0];
        super.cancelPressed();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.RunOperationDialog_run_operation);
    }

    /**
     * Adds buttons to this dialog's button bar.
     * <p>
     * The default implementation of this framework method adds standard ok and
     * cancel buttons using the <code>createButton</code> framework method.
     * Subclasses may override.
     * </p>
     *
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
        updateButtons();
    }

    /**
     * Creates and returns the contents of the upper part of this dialog (above
     * the button bar).
     *
     * @param parent the parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        // Run super.
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setFont(parent.getFont());

        createViewer(composite);

        layoutTopControl(tree.getControl());

        // Restore the last state
        restoreWidgetValues();

        // Return results.
        return composite;
    }

    /**
     * Create a new viewer in the parent.
     *
     * @param parent the parent <code>Composite</code>.
     */
    private void createViewer(Composite parent) {
        tree = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.BORDER);
        tree.setLabelProvider(new OperationLabelProvider());
        tree.setContentProvider(new OperationContentProvider());
        tree.setSorter(new OperationSorter());
        tree.setInput(opMenuFactory);
        tree.addSelectionChangedListener(this);
        tree.addDoubleClickListener(this);
        tree.getTree().setFont(parent.getFont());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
     */
    public void doubleClick(DoubleClickEvent event) {
        IStructuredSelection s = (IStructuredSelection) event.getSelection();
        Object element = s.getFirstElement();
        if (tree.isExpandable(element)) {
            tree.setExpandedState(element, !tree.getExpandedState(element));
        } else if (opActions.length > 0) {
            saveWidgetValues();
            setReturnCode(OK);
            close();
        }
    }

    /**
     * Return the dialog store to cache values into
     */
    protected IDialogSettings getDialogSettings() {
        IDialogSettings workbenchSettings = WorkbenchPlugin.getDefault()
                .getDialogSettings();
        IDialogSettings section = workbenchSettings
                .getSection(DIALOG_SETTING_SECTION_NAME);
        if (section == null)
            section = workbenchSettings
                    .addNewSection(DIALOG_SETTING_SECTION_NAME);
        return section;
    }

    public OpAction[] getSelection() {

        OpAction[] copy=new OpAction[opActions.length];
        System.arraycopy(opActions, 0, copy, 0, opActions.length);
        return copy;
    }

    /**
     * Layout the top control.
     *
     * @param control the control.
     */
    private void layoutTopControl(Control control) {
        GridData spec = new GridData(GridData.FILL_BOTH);
        spec.widthHint = LIST_WIDTH;
        spec.heightHint = LIST_HEIGHT;
        control.setLayoutData(spec);
    }

    /**
     * Use the dialog store to restore widget values to the values that they
     * held last time this dialog was used to completion.
     */
    protected void restoreWidgetValues() {
        IDialogSettings settings = getDialogSettings();

        String[] expandedCategoryIds = settings
                .getArray(STORE_EXPANDED_CATEGORIES_ID);
        if (expandedCategoryIds == null)
            return;

        ArrayList categoriesToExpand = new ArrayList(expandedCategoryIds.length);
        for (int i = 0; i < expandedCategoryIds.length; i++) {
            OperationCategory category = opMenuFactory.findCategory(expandedCategoryIds[i]);
            if (category != null) // ie.- it still exists
                categoriesToExpand.add(category);
        }

        if (!categoriesToExpand.isEmpty())
            tree.setExpandedElements(categoriesToExpand.toArray());

        String selectedOperationID = settings.get(STORE_SELECTED_OPERATION_ID);
        if (selectedOperationID != null) {
            OpAction action = opMenuFactory.find(selectedOperationID);
            if (action != null) {
                tree.setSelection(new StructuredSelection(action), true);
            }
        }
    }

    /**
     * Since OK was pressed, write widget values to the dialog store so that
     * they will persist into the next invocation of this dialog
     */
    protected void saveWidgetValues() {
        IDialogSettings settings = getDialogSettings();

        // Collect the ids of the all expanded categories
        Object[] expandedElements = tree.getExpandedElements();
        String[] expandedCategoryIds = new String[expandedElements.length];
        for (int i = 0; i < expandedElements.length; ++i)
            expandedCategoryIds[i] = ((OperationCategory) expandedElements[i]).getId();

        // Save them for next time.
        settings.put(STORE_EXPANDED_CATEGORIES_ID, expandedCategoryIds);

        String selectedOperationID = ""; //$NON-NLS-1$
        if (opActions.length > 0) {
            // in the case of a multi-selection, it's probably less confusing
            // to store just the first rather than the whole multi-selection
            selectedOperationID = opActions[0].getId();
        }
        settings.put(STORE_SELECTED_OPERATION_ID, selectedOperationID);
    }

    /**
     * Notifies that the selection has changed.
     *
     * @param event event object describing the change
     */
    public void selectionChanged(SelectionChangedEvent event) {
        updateSelection(event);
        updateButtons();
    }

    /**
     * Update the button enablement state.
     */
    protected void updateButtons() {
        if (okButton != null) {
            okButton.setEnabled(getSelection().length > 0);
        }
    }

    /**
     * Update the selection object.
     */
    protected void updateSelection(SelectionChangedEvent event) {
        ArrayList descs = new ArrayList();
        IStructuredSelection sel = (IStructuredSelection) event.getSelection();
        for (Iterator i = sel.iterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof OpAction) {
                descs.add(o);
            }
        }
        opActions = new OpAction[descs.size()];
        descs.toArray(opActions);
    }
}

