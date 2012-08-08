/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.document;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.tool.info.internal.Messages;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog window to add or edit documents from the {@link DocumentView}.
 * 
 * @author Naz Chan
 */
public class DocumentDialog extends IconAndMessageDialog {

    private Composite composite;
    
    private Text name;
    private Text description;
    private ComboViewer type;
    private Label infoLbl;
    private Text info;
    private ControlDecoration infoDecoration;
    private Button infoBtn;
    
    private Type typeValue;
    private Map<String, Object> values;
    private boolean isInfoOnly;
    
    public static final String NAME = "NAME";  //$NON-NLS-1$
    public static final String DESCRIPTION = "DESCRIPTION"; //$NON-NLS-1$
    public static final String TYPE = "TYPE"; //$NON-NLS-1$
    public static final String INFO = "INFO"; //$NON-NLS-1$
    
    /**
     * Constructor for add mode with blank fields.
     * 
     * @param parentShell
     */
    public DocumentDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Constructor for edit mode with initial values and option to only allow info editing.
     * 
     * @param parentShell
     * @param values
     * @param isInfoOnly
     */
    public DocumentDialog(Shell parentShell, Map<String, Object> values, boolean isInfoOnly) {
        super(parentShell);
        this.values = values;
        this.isInfoOnly = isInfoOnly;
    }
    
    public Map<String, Object> getValues() {
        return values;
    }
    
    public String getName() {
        return (String) values.get(NAME);
    }
    
    public String getDescription() {
        return (String) values.get(DESCRIPTION);
    }
    
    public Type getType() {
        return (Type) values.get(TYPE);
    }
    
    public String getInfo() {
        return (String) values.get(INFO);
    }
    
    private boolean isAddMode() {
        return values == null;
    }
    
    @Override
    protected Image getImage() {
        return getQuestionImage();
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setBounds(shell.getBounds().x, shell.getBounds().y, 400, 240);
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        
        composite = new Composite(parent, SWT.NONE);
        final String layoutCons = "insets 0, fillx, wrap 2"; //$NON-NLS-1$
        final String columnCons = "[20%, right]8[80%]"; //$NON-NLS-1$
        final String rowCons = ""; //$NON-NLS-1$
        composite.setLayout(new MigLayout(layoutCons, columnCons, rowCons));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        final ModifyListener modListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                refreshBtns(false);
            }
        };
        
        final Label nameLbl = new Label(composite, SWT.NONE);
        nameLbl.setText(Messages.DocumentDialog_nameLabel);
        nameLbl.setLayoutData(""); //$NON-NLS-1$

        name = new Text(composite, SWT.SINGLE | SWT.BORDER);
        name.setLayoutData("growx"); //$NON-NLS-1$
        name.addModifyListener(modListener);
        
        final Label descriptionLbl = new Label(composite, SWT.NONE);
        descriptionLbl.setText(Messages.DocumentDialog_descriptionLabel);
        descriptionLbl.setLayoutData(""); //$NON-NLS-1$
                    
        description = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        description.setLayoutData("growx, h 60!"); //$NON-NLS-1$
        
        final Label typeLbl = new Label(composite, SWT.NONE);
        typeLbl.setText(Messages.DocumentDialog_typeLabel);
        typeLbl.setLayoutData(""); //$NON-NLS-1$

        type = new ComboViewer(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
        type.getControl().setLayoutData(""); //$NON-NLS-1$
        type.setContentProvider(ArrayContentProvider.getInstance());
        type.addSelectionChangedListener( new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                final Type newTypeValue = getTypeComboValue();
                if (typeValue != newTypeValue) {
                    typeValue = newTypeValue;
                    if (newTypeValue != null) {
                        info.setText(""); //$NON-NLS-1$
                        refreshBtns(!validateInfo());
                        configInfoControls(newTypeValue);
                    }    
                }
            }
        });
        type.setInput(IDocument.Type.values());
        
        infoLbl = new Label(composite, SWT.NONE);
        infoLbl.setText(Messages.DocumentDialog_fileLabel);
        infoLbl.setLayoutData(""); //$NON-NLS-1$

        info = new Text(composite, SWT.SINGLE | SWT.BORDER);
        info.setLayoutData("split 2, growx"); //$NON-NLS-1$
        info.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                refreshBtns(!validateInfo());
            }
        });
        
        infoDecoration = new ControlDecoration(info, SWT.TOP | SWT.LEFT);
        final FieldDecoration errorFieldIndicator = FieldDecorationRegistry.getDefault()
                .getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
        infoDecoration.setImage(errorFieldIndicator.getImage());
        infoDecoration.hide();
        
        infoBtn = new Button(composite, SWT.PUSH);
        infoBtn.setText(Messages.DocumentDialog_fileBtn);
        infoBtn.setLayoutData(""); //$NON-NLS-1$
        infoBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final File file = openFileDialog();
                info.setText(file.getAbsolutePath());
                refreshBtns(!validateInfo());
            }
        });

        return composite;

    }
    
    @Override
    protected Control createContents(Composite parent) {
        final Control control = super.createContents(parent);
        // Set values here
        if (isAddMode()) {
            type.setSelection(new StructuredSelection(Type.FILE));    
        } else {
            name.setText(getName());
            name.setEnabled(!isInfoOnly);
            description.setText(getDescription());
            description.setEnabled(!isInfoOnly);
            type.setSelection(new StructuredSelection(getType()));
            type.getControl().setEnabled(!isInfoOnly);
            info.setText(getInfo());
        }
        // Refresh buttons
        refreshBtns(!validateInfo());
        return control;
    }
    
    @Override
    protected void okPressed() {
        
        if (values == null) {
            values = new HashMap<String, Object>();    
        }
        values.put(NAME, name.getText());
        values.put(DESCRIPTION, description.getText());
        values.put(TYPE, getTypeComboValue());
        values.put(INFO, info.getText());
        
        super.okPressed();
    }
    
    @Override
    protected void cancelPressed() {
        super.cancelPressed();
    }
    
    /**
     * Configure the visibility and enablement of info controls depending on the type.
     * 
     * @param type
     */
    private void configInfoControls(Type type) {
        if (type != null) {
            switch (type) {
            case FILE:
                infoLbl.setText(Messages.DocumentDialog_fileLabel);
                info.setLayoutData("split 2, growx"); //$NON-NLS-1$
                infoBtn.setVisible(true);
                break;
            case WEB:
                infoLbl.setText(Messages.DocumentDialog_urlLabel);
                info.setLayoutData("growx"); //$NON-NLS-1$
                infoBtn.setVisible(false);
                break;
            case ACTION:
                infoLbl.setText(Messages.DocumentDialog_actionLabel);
                info.setLayoutData("growx"); //$NON-NLS-1$
                infoBtn.setVisible(false);
                break;
            default:
                break;
            }
            composite.layout();
        }
    }
    
    /**
     * Validate info input with respect to the type.
     * 
     * @return true if input is valid, otherwise false
     */
    private boolean validateInfo() {
        
        infoDecoration.hide();
        
        final String infoValue = info.getText().trim();
        if (infoValue != null && infoValue.length() > 0) {
            switch (getTypeComboValue()) {
            case FILE:
                final File file = new File(info.getText());
                if (!file.exists()) {
                    infoDecoration.setDescriptionText(Messages.DocumentDialog_errValidFile);
                    infoDecoration.show();
                    return false;
                }
                break;
            case WEB:
                try {
                    @SuppressWarnings("unused") // Used to validate URL
                    final URL url = new URL(info.getText());
                } catch (MalformedURLException e) {
                    infoDecoration.setDescriptionText(Messages.DocumentDialog_errValidURL);
                    infoDecoration.show();
                    return false;
                }
                break;
            case ACTION:
                // Do check here
                break;
            default:
                break;
            }            
        }
        
        return true;
    }
    
    /**
     * Opens the file selection dialog.
     * 
     * @return selected file
     */
    private File openFileDialog() {
        return openFileDialog(false).get(0);
    }
    
    /**
     * Opens the file selection dialog.
     * 
     * @param isMultiSelect
     * @return list of selected files
     */
    private List<File> openFileDialog(boolean isMultiSelect) {
        
        final int style = isMultiSelect ? (SWT.SAVE | SWT.MULTI) : SWT.SAVE; 
        final FileDialog fileDialog = new FileDialog(infoBtn.getShell(), style);
        fileDialog.setText(Messages.docView_openDialogTitle);
        
        final String hasSelection = fileDialog.open();
        if (hasSelection != null) {
            final String[] filenames = fileDialog.getFileNames();
            if (filenames != null && filenames.length > 0) {
                final List<File> fileList = new ArrayList<File>();
                final String filePath = fileDialog.getFilterPath();
                for (int i = 0, n = filenames.length; i < n; i++) {
                    String filename = filePath;
                    if (filePath.charAt(filePath.length() - 1) != File.separatorChar) {
                        filename += File.separatorChar;
                    }
                    filename += filenames[i];
                    fileList.add(new File(filename));
                }
                return fileList;
            }
        }
        
        return null;
    }
    
    /**
     * Refreshes the buttons depending on the field's inputs.
     */
    private void refreshBtns(boolean forceDisable) {
        if (forceDisable) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);    
        } else {
            getButton(IDialogConstants.OK_ID).setEnabled(!isEmpty());
        }
    }
    
    /**
     * Checks if the required fields are filled up.
     * 
     * @return true if one or more is not filled up, otherwise false
     */
    private boolean isEmpty() {
        if (isEmpty(name)) {
            return true;
        }
        if (isEmpty(type)) {
            return true;
        }
        if (isEmpty(info)) {
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the field is filled up.
     * 
     * @param control
     * @return true if it is not filled up, otherwise false
     */
    private boolean isEmpty(Object control) {
        if (control instanceof Text) {
            final Text textCtrl = (Text) control;
            final String textCtrlValue = textCtrl.getText().trim();
            if (textCtrlValue == null || textCtrlValue.length() == 0) {
                return true;
            }
        } else if (control instanceof ComboViewer) {
            final ComboViewer comboCtrl = (ComboViewer) control;
            if (getComboValue(comboCtrl) == null) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the selected value of the combo.
     * 
     * @param combo
     * @return selected value
     */
    private Object getComboValue(ComboViewer combo) {
        final ISelection selection = combo.getSelection();
        if( !selection.isEmpty() && selection instanceof StructuredSelection ){
            final StructuredSelection structSelection = (StructuredSelection) selection;
            return structSelection.getFirstElement();
        }
        return null;
    }
    
    /**
     * Gets the selected value of the type field.
     * 
     * @return select value
     */
    private Type getTypeComboValue() {
        return (Type) getComboValue(type);
    }
    
}
