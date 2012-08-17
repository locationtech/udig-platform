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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.document.DocumentPropertyPage;
import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocument.DocType;
import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;
import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import net.refractions.udig.tool.info.internal.Messages;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * Dialog window to add or edit documents from the {@link DocumentView}.
 * 
 * @author Naz Chan
 */
public class DocumentDialog extends IconAndMessageDialog {

    private Composite composite;
    
    private Label headerImg;
    private Label headerText;
    private Label headerSubText;
    
    private Label infoLbl;
    private Text info;
    private ControlDecoration infoDecoration;
    
    private Composite infoBtnComposite;
    private Button infoOpenBtn;
    private Button infoBrowseBtn;
    private Button infoNewBtn;
    
    private Label infoGoActionLbl;
    private ComboViewer infoGoAction;
    private Button infoGoActionBtn;
    
    private Label attributeLbl;
    private Text attribute;
    
    private Text document;
    private ComboViewer type;
    
    private Button templateCheckBtn;
    
    private Label labelLbl;
    private Text label;
    private Text description;
    
    private boolean hasError = false;
    private Type typeValue;
    
    private Map<String, Object> values;
    public static final String V_INFO = "INFO"; //$NON-NLS-1$
    public static final String V_TYPE = "TYPE"; //$NON-NLS-1$
    public static final String V_LABEL = "LABEL";  //$NON-NLS-1$
    public static final String V_DESCRIPTION = "DESCRIPTION"; //$NON-NLS-1$
    public static final String V_ATTRIBUTE = "ATTRIBUTE";  //$NON-NLS-1$
    public static final String V_TEMPLATE = "TEMPLATE";  //$NON-NLS-1$
    public static final String V_DOCUMENT = "DOCUMENT";  //$NON-NLS-1$
    public static final String V_ACTIONS = "ACTIONS";  //$NON-NLS-1$
    
    private Map<String, Object> params;
    public static final String P_DOC_TYPE = "DOC_TYPE"; //$NON-NLS-1$
    public static final String P_MODE = "MODE"; //$NON-NLS-1$
    public static final String P_FEATURE_NAME = "FEATURE_NAME"; //$NON-NLS-1$
    public static final String P_SHAPEFILE_NAME = "SHAPEFILE_NAME"; //$NON-NLS-1$
    public static final String P_TEMPLATES = "TEMPLATES"; //$NON-NLS-1$
    
    public enum Mode { 
        ADD, EDIT;
    }
    
    private boolean isAttachment = true;
    private boolean isAddMode = true;
    
    public static final String LABEL_FORMAT = "%s%s:"; //$NON-NLS-1$
    public static final String MANDATORY = "*"; //$NON-NLS-1$
    
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
    public DocumentDialog(Shell parentShell, Map<String, Object> values, Map<String, Object> params) {
        this(parentShell);
        this.values = values;
        this.params = params;
        this.isAttachment = isAttachment();
        this.isAddMode = isAddMode();
    }
    
    public Map<String, Object> getValues() {
        return values;
    }
    
    public String getLabel() {
        return (String) values.get(V_LABEL);
    }
    
    public String getDescription() {
        return (String) values.get(V_DESCRIPTION);
    }
    
    public Type getType() {
        return (Type) values.get(V_TYPE);
    }
    
    public String getInfo() {
        return (String) values.get(V_INFO);
    }
    
    public boolean isTemplate() {
        final Object isTemplateObj = values.get(V_TEMPLATE);
        if (isTemplateObj != null) {
            return (Boolean) isTemplateObj;    
        }
        return false;
    }
    
    public DocumentInfo getDocInfo() {
        return (new DocumentInfo(getLabel(), getDescription(), getInfo(), getType(), isTemplate()));
    }
    
    public File getFileInfo() {
        final File file = new File(getInfo());
        if (file.exists()) {
            return file;
        }
        return null;
    }
    
    public URL getUrlInfo() {
        try {
            final URL url = new URL(getInfo());
            return url;
        } catch (MalformedURLException e) {
            return null;
        }
    }
    
    private String getAttribute() {
        return (String) values.get(V_ATTRIBUTE);
    }
    
    @SuppressWarnings("unchecked")
    private List<HotlinkDescriptor> getActions() {
        return (List<HotlinkDescriptor>) values.get(V_ACTIONS);
    }
    
    private boolean isAttachment() {
        if (params != null) {
            final DocType docType = (DocType) params.get(P_DOC_TYPE);
            if (DocType.HOTLINK == docType) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isAddMode() {
        if (params != null) {
            if (isAttachment()) {
                final Mode mode = (Mode) params.get(P_MODE);
                if (Mode.ADD == mode) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    private String getFeatureName() {
        return (String) params.get(P_FEATURE_NAME);
    }
    
    private String getShapefileName() {
        return (String) params.get(P_SHAPEFILE_NAME);
    }
    
    @SuppressWarnings("unchecked")
    private List<IDocument> getTemplates() {
        return (List<IDocument>) params.get(P_TEMPLATES);
    }
    
    private String getLabel(String label) {
        return getLabel(label, false);
    }
    
    private String getLabel(String label, boolean isMandatory) {
        return String.format(LABEL_FORMAT, label, (isMandatory ? MANDATORY : "")); //$NON-NLS-1$
    }
    
    @Override
    protected Image getImage() {
        return getQuestionImage();
    }

    @Override
    protected void configureShell(Shell shell) {
        
        final int HEIGHT = 380;
        final int WIDTH = 400;

        final Display display = PlatformUI.getWorkbench().getDisplay();
        final Point size = (new Shell(display)).computeSize(-1, -1);
        final Rectangle screen = display.getMonitors()[0].getBounds();

        final int xPos = (screen.width - size.x) / 2 - WIDTH / 2;
        final int yPos = (screen.height - size.y) / 2 - HEIGHT / 2;

        shell.setBounds(xPos, yPos, WIDTH, HEIGHT);

        super.configureShell(shell);
        
    }
    
    private static final String LABEL_WIDTH = "20%";  //$NON-NLS-1$
    private static final String CONTROL_WIDTH = "80%"; //$NON-NLS-1$
    private static final String LAYOUT_FORMAT = "[%s, right]8[%s]"; //$NON-NLS-1$
    private static final String WIDTH_LAYOUT_FORMAT = "w %s!"; //$NON-NLS-1$
    
    @Override
    protected Control createDialogArea(Composite parent) {
        
        composite = new Composite(parent, SWT.NONE);
        final String layoutCons = "insets 0, fillx, wrap 2, hidemode 3"; //$NON-NLS-1$
        final String columnCons = String.format(LAYOUT_FORMAT, LABEL_WIDTH, CONTROL_WIDTH);
        final String rowCons = "[]15[][]"; //$NON-NLS-1$
        composite.setLayout(new MigLayout(layoutCons, columnCons, rowCons));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        createHeader();
        
        createInfoControls();
        createInfoBtnControls();
        createInfoGoActionControls();

        attributeLbl = new Label(composite, SWT.NONE);
        attributeLbl.setText(getLabel(Messages.DocumentDialog_attributeLabel));
        attributeLbl.setLayoutData(""); //$NON-NLS-1$

        attribute = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        attribute.setLayoutData(String.format(WIDTH_LAYOUT_FORMAT, CONTROL_WIDTH));
        
        final Label docLbl = new Label(composite, SWT.NONE);
        docLbl.setText(getLabel(Messages.DocumentDialog_documentLabel));
        docLbl.setLayoutData(""); //$NON-NLS-1$

        document = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        document.setLayoutData(String.format(WIDTH_LAYOUT_FORMAT, CONTROL_WIDTH));

        createTypeControls();
        
        templateCheckBtn = new Button(composite, SWT.CHECK);
        templateCheckBtn.setText(Messages.DocumentDialog_templateLabel);
        templateCheckBtn.setLayoutData("skip 1"); //$NON-NLS-1$
        
        labelLbl = new Label(composite, SWT.NONE);
        labelLbl.setText(getLabel(Messages.DocumentDialog_labelLabel, isAttachment));
        labelLbl.setLayoutData(""); //$NON-NLS-1$

        label = new Text(composite, SWT.SINGLE | SWT.BORDER);
        label.setLayoutData(String.format(WIDTH_LAYOUT_FORMAT, CONTROL_WIDTH));
        label.addModifyListener(new BasicModifyListener());
        
        final Label descriptionLbl = new Label(composite, SWT.NONE);
        descriptionLbl.setText(getLabel(Messages.DocumentDialog_descriptionLabel));
        descriptionLbl.setLayoutData(""); //$NON-NLS-1$
                    
        description = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        description.setLayoutData(String.format(WIDTH_LAYOUT_FORMAT, CONTROL_WIDTH) + ", h 60!"); //$NON-NLS-1$

        return composite;

    }
    
    /**
     * Creates the header section display image, header text and sub-text.
     */
    private void createHeader() {

        final Composite headerComposite = new Composite(composite, SWT.NONE);
        headerComposite.setLayoutData("growx, span 2"); //$NON-NLS-1$
        final String layoutCons = "insets 0, fillx, wrap 2"; //$NON-NLS-1$
        final String columnCons = "[]10[]"; //$NON-NLS-1$
        final String rowCons = ""; //$NON-NLS-1$
        headerComposite.setLayout(new MigLayout(layoutCons, columnCons, rowCons));
        
        final Image image = getImage();
        if (image != null) {
                headerImg = new Label(headerComposite, SWT.NULL);
                image.setBackground(headerImg.getBackground());
                headerImg.setImage(image);
                headerImg.setLayoutData("sy 2"); //$NON-NLS-1$
        }

        headerText = new Label(headerComposite, SWT.NONE);
        headerText.setLayoutData("push"); //$NON-NLS-1$
        final FontData[] fontData = headerText.getFont().getFontData(); 
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(14);
        };
        headerText.setFont(new Font(null, fontData));
        
        headerSubText = new Label(headerComposite, SWT.NONE);
        headerSubText.setLayoutData(""); //$NON-NLS-1$
        
    }
    
    /**
     * Creates the info controls.
     */
    private void createInfoControls() {
        
        infoLbl = new Label(composite, SWT.NONE);
        infoLbl.setText(getLabel(Messages.DocumentDialog_fileLabel));
        infoLbl.setLayoutData(""); //$NON-NLS-1$

        info = new Text(composite, SWT.SINGLE | SWT.BORDER);
        info.setLayoutData(String.format(WIDTH_LAYOUT_FORMAT, CONTROL_WIDTH));
        info.addModifyListener(new ValidatingModifyListener());
        
        infoDecoration = new ControlDecoration(info, SWT.TOP | SWT.LEFT);
        final FieldDecoration errorFieldIndicator = FieldDecorationRegistry.getDefault()
                .getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
        infoDecoration.setImage(errorFieldIndicator.getImage());
        infoDecoration.hide();
        
    }
    
    /**
     * Creates the info button controls.
     */
    private void createInfoBtnControls() {
        
        infoBtnComposite = new Composite(composite, SWT.NONE);
        infoBtnComposite.setLayoutData("skip, growx"); //$NON-NLS-1$
        infoBtnComposite.setLayout(new MigLayout("insets 0, nogrid, fillx")); //$NON-NLS-1$
        
        infoOpenBtn = new Button(infoBtnComposite, SWT.PUSH);
        infoOpenBtn.setText(Messages.DocumentDialog_openBtn);
        infoOpenBtn.setLayoutData("sg btnGrp"); //$NON-NLS-1$
        infoOpenBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(info.getText());
            }
        });
        
        infoBrowseBtn = new Button(infoBtnComposite, SWT.PUSH);
        infoBrowseBtn.setText(Messages.DocumentDialog_fileBtn);
        infoBrowseBtn.setLayoutData("sg btnGrp, gap push"); //$NON-NLS-1$
        infoBrowseBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final File file = openFileDialog();
                if (file != null) {
                    info.setText(file.getAbsolutePath());
                    refreshBtns();    
                }
            }
        });
        
        infoNewBtn = new Button(infoBtnComposite, SWT.PUSH);
        infoNewBtn.setText(Messages.DocumentDialog_newBtn);
        infoNewBtn.setLayoutData("sg btnGrp"); //$NON-NLS-1$
        infoNewBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final File newFile = createFromTemplate();
                if (newFile != null) {
                    info.setText(newFile.getAbsolutePath());    
                }
            }
        }); 
        
    }
    
    /**
     * Creates a new file from a template.
     * 
     * @return new file
     */
    private File createFromTemplate() {
        final File templateFile = openSelectTemplateDialog();
        if (templateFile != null) {
            final String filePath = openSaveFileDialog(templateFile);
            if (filePath != null) {
                return createFileFromTemplate(templateFile, filePath);
            }
        }
        return null;
    }
    
    /**
     * Opens a list selection dialog to select templates from.
     * 
     * @return template file
     */
    private File openSelectTemplateDialog() { 

        final ListDialog selectDialog = new ListDialog(infoNewBtn.getShell());
        selectDialog.setTitle(Messages.DocumentDialog_selectTemplateTitle);
        selectDialog.setMessage(Messages.DocumentDialog_selectTemplateMsg);
        selectDialog.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                final IDocument doc = (IDocument) element;
                return DocUtils.getLabelAndDescDisplay(DocUtils.getDocStr(doc),
                        doc.getDescription());
            }
        });
        selectDialog.setContentProvider(new ArrayContentProvider());
        final Object[] templates = getTemplates().toArray();
        selectDialog.setInput(templates);
        selectDialog.setInitialElementSelections(Collections.singletonList(templates[0]));
        if (Dialog.OK == selectDialog.open()) {
            final Object[] results = selectDialog.getResult();
            if (results != null && results.length > 0) {
                final IDocument doc = (IDocument) results[0];
                if (doc != null) {
                    return (File) doc.getValue();
                }
            }
        }
        
        
        return null;
        
    }
    
    /**
     * Opens an input dialog to enter a filename.
     * 
     * @return filename
     */
    private String openSaveFileDialog(File templateFile) {
        final FileDialog dialog = new FileDialog(infoNewBtn.getShell(), SWT.SAVE);
        dialog.setText(Messages.DocumentDialog_enterFilenameTitle);
        dialog.setOverwrite(true);
        dialog.setFileName(DocUtils.getDefaultFilename(templateFile));
        final String filePath = dialog.open();
        if (filePath != null) {
            return filePath;
        }
        return null;
    }

    /**
     * Creates a new file with the filename from the template.
     * 
     * @param templateFile
     * @param filePath
     * @return new file
     */
    private File createFileFromTemplate(File templateFile, String filePath) {
        try {
            final File file = new File(DocUtils.cleanFilename(filePath, templateFile));
            FileUtils.copyFile(templateFile, file);
            MessageDialog.openInformation(infoNewBtn.getShell(),
                    Messages.DocumentDialog_createFileFromTemplateTitle,
                    Messages.DocumentDialog_createFileFromTemplateSuccess);
            return file;
        } catch (IOException e) {
            MessageDialog.openError(infoNewBtn.getShell(),
                    Messages.DocumentDialog_createFileFromTemplateTitle,
                    Messages.DocumentDialog_createFileFromTemplateError);
        }
        return null;
    }
    
    /**
     * Creates the info (action type) controls.
     */
    private void createInfoGoActionControls() {
        
        infoGoActionLbl = new Label(composite, SWT.NONE);
        infoGoActionLbl.setText(getLabel(Messages.DocumentDialog_actionLabel));
        infoGoActionLbl.setLayoutData(""); //$NON-NLS-1$

        infoGoAction = new ComboViewer(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
        infoGoAction.getControl().setLayoutData("split 2"); //$NON-NLS-1$
        infoGoAction.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                final HotlinkDescriptor descriptor = (HotlinkDescriptor) element;
                return descriptor.getLabel();
            }
        });
        infoGoAction.setContentProvider(ArrayContentProvider.getInstance());
        infoGoAction.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                final StructuredSelection selection = (StructuredSelection) event.getSelection();
                final HotlinkDescriptor descriptor = (HotlinkDescriptor) selection.getFirstElement();
                label.setText(descriptor.getLabel());
                final String descriptionStr = descriptor.getDescription();
                if (descriptionStr == null) {
                    description.setText("");     //$NON-NLS-1$
                } else {
                    description.setText(descriptionStr);
                }
            }
        });
        final List<HotlinkDescriptor> actions = getActions();
        if (actions != null) {
            infoGoAction.setInput(actions.toArray());    
        }
        
        infoGoActionBtn = new Button(composite, SWT.PUSH);
        infoGoActionBtn.setText(Messages.DocumentDialog_goBtn);
        infoGoActionBtn.setLayoutData(""); //$NON-NLS-1$
        infoGoActionBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final StructuredSelection selection = (StructuredSelection) infoGoAction.getSelection();
                final HotlinkDescriptor descriptor = (HotlinkDescriptor) selection.getFirstElement();
                final String action = descriptor.getConfig().replace(
                        DocumentPropertyPage.ACTION_PARAM, info.getText());
                Program.launch(action);
            }
        }); 
        
    }
    
    /**
     * Creates the type controls
     */
    private void createTypeControls() {
        
        final Label typeLbl = new Label(composite, SWT.NONE);
        typeLbl.setText(getLabel(Messages.DocumentDialog_typeLabel, true));
        typeLbl.setLayoutData(""); //$NON-NLS-1$

        type = new ComboViewer(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
        type.getControl().setLayoutData(""); //$NON-NLS-1$
        type.setContentProvider(ArrayContentProvider.getInstance());
        type.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return DocUtils.toCamelCase(element.toString());
            }
        });
        type.addSelectionChangedListener( new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                final Type newTypeValue = getTypeComboValue();
                if (typeValue != newTypeValue) {
                    typeValue = newTypeValue;
                    if (newTypeValue != null) {
                        validateInfo();
                        refreshBtns();
                        configInfoControls(newTypeValue);
                    }    
                }
            }
        });
        
        if (isAttachment) {
            final Type[] types = Type.values();
            final List<Type> typeList = new ArrayList<Type>();;
            for (int i = 0; i < types.length; i++) {
                if (Type.ACTION != types[i]) {
                    typeList.add(types[i]);    
                }
            }
            type.setInput(typeList.toArray());
        } else {
            type.setInput(IDocument.Type.values());    
        }
        
    }
    
    /**
     * Setup before dialog contents creation. Initialisations that will affect creation of contents
     * should be set here.
     */
    private void beforeCreateContents() {
        // Do something
    }
    
    @Override
    protected Control createContents(Composite parent) {
        beforeCreateContents();
        final Control control = super.createContents(parent);
        afterCreateContents();
        return control;
    }
    
    /**
     * Setup after dialog contents creation. Initialisations that needs the contents should be set
     * here. Eg. setting values, enablements, initial validations, etc.
     */
    private void afterCreateContents() {
        // Set values here
        setValues();
        // Set title
        configHeaderDisplay();
        // Set controls
        configControlEnablements();
        // Refresh buttons
        refreshBtns();
    }
    
    /**
     * Sets the values into the controls.
     */
    private void setValues() {
        
        if (isAddMode) {
            type.setSelection(new StructuredSelection(Type.FILE));
        } else {
            type.setSelection(new StructuredSelection(getType()));
            setValue(info, getInfo());
            setValue(label, getLabel());
            setValue(description, getDescription());
            setValue(attribute, getAttribute());
            templateCheckBtn.setSelection(isTemplate());
        }
        
    }
    
    private void setValue(Text text, String value) {
        if (value != null) {
            text.setText(value);
        }
    }
    
    /**
     * Sets the window, header and sub-header texts.
     */
    private void configHeaderDisplay() {

        String header = ""; //$NON-NLS-1$
        String subHeader = ""; //$NON-NLS-1$

        if (isAttachment) {
            if (isAddMode) {
                header = Messages.DocumentDialog_addAttachHeader;
            } else {
                header = Messages.DocumentDialog_editAttachHeader;
            }

            String featureName = getFeatureName();
            String shapefileName = getShapefileName();
            
            if (shapefileName != null) {
                shapefileName = DocUtils.toCamelCase(shapefileName);
                if (featureName != null) {
                    featureName = DocUtils.toCamelCase(featureName);
                    subHeader = String.format(Messages.DocumentDialog_attachSubHeaderFeature,
                            featureName, shapefileName);
                } else {
                    subHeader = String.format(Messages.DocumentDialog_attachSubHeaderShapefile,
                            shapefileName);
                }
            } else {
                subHeader = Messages.DocumentDialog_attachSubHeader;
            }

        } else {
            header = String.format(Messages.DocumentDialog_hotlinkHeader,
                    DocUtils.toCamelCase(attribute.getText()));
            subHeader = description.getText();
        }

        getShell().setText(header);
        headerText.setText(header);
        headerSubText.setText(subHeader);

    }
    
    /**
     * Sets controls' enablement and visibility settings with respect to the dialog's type and mode.
     */
    private void configControlEnablements() {        
        if (isAttachment) {
            configAttributeControls(false);
            configMetadataControls(true);
            type.getControl().setEnabled(isAddMode);
        } else {
            configAttributeControls(true);
            configMetadataControls(false);
            type.getControl().setEnabled(false);
            templateCheckBtn.setVisible(false);
        }
    }
    
    @Override
    protected void okPressed() {
        
        if (values == null) {
            values = new HashMap<String, Object>();    
        }
        values.put(V_LABEL, label.getText());
        values.put(V_DESCRIPTION, description.getText());
        values.put(V_TYPE, getTypeComboValue());
        values.put(V_INFO, info.getText());
        values.put(V_TEMPLATE, templateCheckBtn.getSelection());
        
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
        switch (type) {
        case FILE:
            infoLbl.setText(isAttachment ? getLabel(Messages.DocumentDialog_fileLabel, true)
                    : getLabel(Messages.DocumentDialog_valueLabel));
            configInfoBtnControls(true, true);
            configInfoGoActionControls(false);
            templateCheckBtn.setVisible(true);
            break;
        case WEB:
            infoLbl.setText(isAttachment ? getLabel(Messages.DocumentDialog_urlLabel, true)
                    : getLabel(Messages.DocumentDialog_valueLabel));
            configInfoBtnControls(true, false);
            configInfoGoActionControls(false);
            templateCheckBtn.setVisible(false);
            break;
        case ACTION:
            infoLbl.setText(isAttachment ? getLabel(Messages.DocumentDialog_actionLabel, true)
                    : getLabel(Messages.DocumentDialog_valueLabel));
            configInfoBtnControls(false, false);
            configInfoGoActionControls(true);
            templateCheckBtn.setVisible(false);
            break;
        default:
            break;
        }
        composite.layout();
    }

    /**
     * Sets the info buttons' enablement and visibility settings.
     * 
     * @param isVisible
     * @param isFile
     */
    private void configInfoBtnControls(boolean isVisible, boolean isFile) {
        infoBtnComposite.setVisible(isVisible);
        if (isVisible) {
            infoBrowseBtn.setVisible(isFile);
            infoNewBtn.setVisible(isFile);
            final List<IDocument> templates = getTemplates();
            final boolean hasTemplates = (templates != null && templates.size() > 0);
            infoNewBtn.setEnabled(hasTemplates);
        }
    }
    
    /**
     * Sets the info (action controls) visibility setttings.
     * 
     * @param isVisible
     */
    private void configInfoGoActionControls(boolean isVisible) {
        infoGoActionLbl.setVisible(isVisible);
        infoGoAction.getControl().setVisible(isVisible);
        infoGoActionBtn.setVisible(isVisible);
    }
    
    /**
     * Sets the attribute controls' visbility settings.
     * 
     * @param isVisible
     */
    private void configAttributeControls(boolean isVisible) {
        attributeLbl.setVisible(isVisible);
        attribute.setVisible(isVisible);
    }
    
    /**
     * Sets the metadata (label and description) controls' enablement settings.
     * 
     * @param isEnabled
     */
    private void configMetadataControls(boolean isEnabled) {
        label.setEnabled(isEnabled);
        description.setEnabled(isEnabled);
    }
    
    /**
     * Opens the file selection dialog.
     * 
     * @return selected file
     */
    private File openFileDialog() {
        final List<File> fileList = openFileDialog(false);
        if (fileList != null && fileList.size() > 0) {
            return fileList.get(0);
        }
        return null;
    }
    
    /**
     * Opens the file selection dialog.
     * 
     * @param isMultiSelect
     * @return list of selected files
     */
    private List<File> openFileDialog(boolean isMultiSelect) {
        
        final int style = isMultiSelect ? (SWT.SAVE | SWT.MULTI) : SWT.SAVE; 
        final FileDialog fileDialog = new FileDialog(infoBrowseBtn.getShell(), style);
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
    private void refreshBtns() {
        if (hasError) {
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
        if (isAttachment && isEmpty(label)) {
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
    
    /**
     * Validate info input with respect to the type.
     * 
     * @return true if input is valid, otherwise false
     */
    private void validateInfo() {
        
        infoOpenBtn.setEnabled(false);
        infoDecoration.hide();
        
        final String infoValue = info.getText().trim();
        if (infoValue != null && infoValue.length() > 0) {
            switch (getTypeComboValue()) {
            case FILE:
                final File file = new File(infoValue);
                if (file.exists() && file.isFile()) {
                    infoOpenBtn.setEnabled(true);
                } else {
                    infoDecoration.setDescriptionText(Messages.DocumentDialog_errValidFile);
                    infoDecoration.show();
                    hasError = true;
                    return;
                }
                break;
            case WEB:
                try {
                    new URL(infoValue);
                    infoOpenBtn.setEnabled(true);
                } catch (MalformedURLException e) {
                    infoDecoration.setDescriptionText(Messages.DocumentDialog_errValidURL);
                    infoDecoration.show();
                    hasError = true;
                    return;
                }
                break;
            case ACTION:
                // Do check here
                break;
            default:
                break;
            }            
        }
        
        hasError = false;
    }
    
    /**
     * Sets the document control's value.
     */
    private void setDocumentValue() {
        if (!hasError) {
            
            final String infoValue = info.getText().trim();
            if (infoValue != null && infoValue.length() > 0) {

                String labelValue = label.getText().trim();
                if (labelValue == null || labelValue.length() == 0) {
                    if (!isAttachment) {
                        labelValue = attribute.getText();
                    }
                }
                document.setText(DocUtils.getDocStr(getTypeComboValue(), infoValue, labelValue));
                
            } else {
                document.setText("");   //$NON-NLS-1$
            }
            
        }
    }
    
    /**
     * Modify listener that sets the document text and refreshes buttons' state.
     */
    private class BasicModifyListener implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent e) {
            setDocumentValue();
            refreshBtns();
        }
    }
    
    /**
     * Modify listener that adds validation to {@link BasicModifyListener}'s functionalities.
     */
    private class ValidatingModifyListener extends BasicModifyListener {
        @Override
        public void modifyText(ModifyEvent e) {
            validateInfo();
            super.modifyText(e);
        }
    }
    
}
