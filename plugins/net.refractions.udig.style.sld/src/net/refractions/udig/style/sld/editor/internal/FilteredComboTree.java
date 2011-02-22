/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.refractions.udig.style.sld.editor.internal;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * The FilteredComboTree is a filtered tree that uses an
 * editable combo rather than just a text.
 */
public class FilteredComboTree extends FilteredTree {

    private Combo filterCombo;

    private static final String SEARCHHISTORY = "search"; //$NON-NLS-1$

    //Set a number limitation of items to be saved in combo
    private static final int maxNumItems = 20;
    /**
     * Create a new instance of the receiver.
     * @param parent
     * @param treeStyle
     */
    public FilteredComboTree(Composite parent, int treeStyle) {
        super(parent, treeStyle);
    }

    /**
     *  Create a new instance of the receiver with a supplied filter.
     * @param parent
     * @param treeStyle
     * @param filter
     */
    public FilteredComboTree(Composite parent, int treeStyle, PatternItemFilter filter) {
        super(parent, treeStyle, filter);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.dialogs.FilteredTree#createFilterControl(org.eclipse.swt.widgets.Composite)
     */
    protected void createFilterControl(Composite parent) {
        filterCombo = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER);
        filterCombo.setFont(parent.getFont());
        getPreferenceSearchHistory();
        filterCombo.addTraverseListener( new TraverseListener () {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                    if (getViewer().getTree().getItemCount() == 0) {
                        setFilterText(""); //$NON-NLS-1$
                    } else {
                        getViewer().getTree().setFocus();
                    }
                }
            }
        });
        filterCombo.addFocusListener(new FocusAdapter(){
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
             */
            public void focusLost(FocusEvent e) {
                String [] textValues = filterCombo.getItems();
                String newText = filterCombo.getText();

                if((newText.equals(""))||(newText .equals(initialText)))//$NON-NLS-1$
                    return;

                for (int i = 0; i < textValues.length; i++) {
                    if(textValues[i].equals(newText))
                        return;
                }

                if(textValues.length >= maxNumItems)
                    //Discard the oldest search to get space for new search
                    filterCombo.remove(maxNumItems-1);

                filterCombo.add(newText,0);
            }
        });
        filterCombo.addSelectionListener(new SelectionAdapter(){
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            public void widgetSelected(SelectionEvent e) {
                textChanged();
            }
        });

        filterCombo.addDisposeListener(new DisposeListener() {

            /* (non-Javadoc)
             * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
             */
            public void widgetDisposed(DisposeEvent e) {
                saveDialogSettings();
            }
        });

        filterCombo.getAccessible().addAccessibleListener(getAccessibleListener());

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.dialogs.FilteredTree#getFilterControl()
     */
    public Control getFilterControl() {
        return filterCombo;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.dialogs.FilteredTree#getFilterControlText()
     */
    protected String getFilterControlText() {
        return filterCombo.getText();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.dialogs.FilteredTree#setFilterText(java.lang.String)
     */
    protected void setFilterText(String string) {
        filterCombo.setText(string);
        selectAll();
    }

    protected void selectAll() {
        filterCombo.setSelection(new Point(0,filterCombo.getText().length()));
    }

    /**
     * Get the combo box used by the receiver.
     * @return Combo
     */
    public Combo getFilterCombo() {
        return filterCombo;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.dialogs.FilteredTree#getFilterText()
     */
    protected String getFilterText() {
        return filterCombo.getText();
    }

    /**
     * Return a dialog setting section for this dialog
     */
    private IDialogSettings getDialogSettings() {
        IDialogSettings settings = WorkbenchPlugin.getDefault()
                .getDialogSettings();
        IDialogSettings thisSettings = settings
                .getSection(getClass().getName());
        if (thisSettings == null)
            thisSettings = settings.addNewSection(getClass().getName());
        return thisSettings;
    }


    /**
     * Get the preferences search history for this eclipse's start,
     * Note that this history will not be cleared until this eclipse closes
     *
     */
    public void getPreferenceSearchHistory(){
        IDialogSettings settings = getDialogSettings();
        String[] search = settings.getArray(SEARCHHISTORY);

        if(search == null)
            return;

        for(int i = 0; i < search.length;i++){
            filterCombo.add(search[i]);
        }

    }

     /**
     * Saves the search history.
     */
    private void saveDialogSettings() {
        IDialogSettings settings =getDialogSettings();

        //If the settings contains the same key, the previous value will be replaced by new one
        settings.put(SEARCHHISTORY,filterCombo.getItems());

    }
}
