/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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
package net.refractions.udig.tutorials.featureeditor;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;

import net.refractions.udig.project.EditFeature;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.EditFeature.AttributeStatus;
import net.refractions.udig.project.internal.commands.selection.CommitCommand;
import net.refractions.udig.project.listener.EditFeatureListener;
import net.refractions.udig.project.listener.EditFeatureStateChangeEvent;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tutorials.celleditor.FormComboBoxCellEditor;
import net.refractions.udig.tutorials.celleditor.FormTextCellEditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.IMessagePrefixProvider;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A remake of the {@link CountryPanelFormCellEditor} with the ff changes:
 * <p>
 * <ul>
 * <li>use EditFeature and EditManager for feature editing</li>
 * <li>use EditFeature listeners and interceptors to simulate real time change detection and
 * interaction with other views using the same EditFeature</li>
 * </ul>
 * </p>
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls") //TODO - Remove this suppress warning
public class CountryPanelFormEditFeature implements KeyListener, ISelectionChangedListener,
        EditFeatureListener, FocusListener, SelectionListener {

    /** Attribute name for attribute NAME_FORMA */
    public final static String NAME_FORMA = "NAME_FORMA";
    public final static String NAME_FORMA_LBL = "Name (formal)";

    /** Attribute name for attribute NAME_SORT */
    public final static String NAME_SORT = "NAME_SORT";
    public final static String NAME_SORT_LBL = "Name (short)";

    /** Attribute name for attribute POP_EST */
    public final static String POP_EST = "POP_EST";
    public final static String POP_EST_LBL = "Population Estimate";

    /** Attribute name for attribute TYPE */
    public final static String TYPE = "TYPE";
    public final static String TYPE_LBL = "Status";
    public final static String TYPE_SOV_LBL = "Sovereign Country";
    public final static String TYPE_COU_LBL = "Country";
    public final static String[] TYPE_OPTS = new String[] {TYPE_SOV_LBL, TYPE_COU_LBL};
    
    /** Attribute name for attribute MAP_COLOR */
    public final static String COLOR_MAP = "MAP_COLOR";
    public final static String COLOR_MAP_LBL = "Map Color";
    public final static Double[] COLOR_MAP_OPTS;
    static {
        COLOR_MAP_OPTS = new Double[13];
        for( int i = 0; i < COLOR_MAP_OPTS.length; i++ ) {
            COLOR_MAP_OPTS[i] = Double.valueOf(Integer.toString(i + 1));
        }
    }

    public final static String REMARKS_LBL = "Remarks";
    
    /**
     * Used to construct UI
     */
    private static final int MAX_COLS = 2;
    private static final int MIN_COLS = 1;
    private static final int LABEL_STYLE = SWT.SHADOW_IN;
    private static final int FIELD_STYLE = SWT.SHADOW_IN | SWT.BORDER;
    private static final int SECTION_STYLE = Section.TWISTIE | Section.TITLE_BAR
            | Section.DESCRIPTION | Section.EXPANDED;

    private FormToolkit toolkit;
    private ScrolledForm form;

    private Text nameSort;
    private Text nameFormal;
    private Text population;
    private CCombo type;
    private ComboViewer colorMap;

    private Action apply;
    private Action reset;

    /**
     * Used for editing the feature
     */
    private SimpleFeature baseFeature;
    private EditFeature editFeature;
    
    /**
     * Used send commands to the edit blackboard
     */
    private IToolContext context;
    
    public EditFeature getEditFeature() {
        return editFeature;
    }

    public void createControl( Composite parent, FormToolkit toolkit ) {
        
        this.toolkit = toolkit;

        form = toolkit.createScrolledForm(parent);
        form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter(){
            @Override
            public void linkActivated( HyperlinkEvent e ) {
                final IMessage[] errors = (IMessage[]) e.data;
                if (errors.length > 0) {
                    final IMessage topError = errors[0];
                    topError.getControl().setFocus();
                }
            }
        });
        form.getMessageManager().setMessagePrefixProvider(new IMessagePrefixProvider(){
            @Override
            public String getPrefix( Control control ) {
                return control.getData().toString() + " - ";
            }
        });
        form.setText("Country Form");
        final ColumnLayout layout = new ColumnLayout();
        layout.maxNumColumns = MAX_COLS;
        layout.minNumColumns = MIN_COLS;
        form.getBody().setLayout(layout);
        toolkit.decorateFormHeading(form.getForm());
        
        final Section section = toolkit.createSection(form.getBody(), SECTION_STYLE);
        section.setText("Country Details");
        section.setDescription("Update country details below.");
        section.addExpansionListener(new ExpansionAdapter(){
            public void expansionStateChanged( ExpansionEvent e ) {
                // Nothing
            }
        });
        final Composite client = toolkit.createComposite(section, SWT.NONE);
        GridLayout sectionLayout = new GridLayout();
        sectionLayout.numColumns = 4;
        client.setLayout(sectionLayout);
        section.setClient(client);

        // SWT Widgets
        Label label = toolkit.createLabel(client, NAME_FORMA_LBL, LABEL_STYLE);
        FormTextCellEditor nameFormalEditor = new FormTextCellEditor(client, form.getMessageManager());
        nameFormal = (Text) nameFormalEditor.getControl();
        nameFormal.setData(NAME_FORMA_LBL);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 3;
        nameFormal.setLayoutData(layoutData);

        label = toolkit.createLabel(client, NAME_SORT_LBL, LABEL_STYLE);
        FormTextCellEditor nameSortEditor = new FormTextCellEditor(client, form.getMessageManager());
        nameSort = (Text) nameSortEditor.getControl();
        nameSort.setData(NAME_SORT_LBL);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 3;
        nameSort.setLayoutData(layoutData);

        label = toolkit.createLabel(client, POP_EST_LBL, LABEL_STYLE);
        FormTextCellEditor populationEditor = new FormTextCellEditor(client, form.getMessageManager());
        population = (Text) populationEditor.getControl();
        population.setData(POP_EST_LBL);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 3;
        population.setLayoutData(layoutData);
        
        label = toolkit.createLabel(client, TYPE_LBL, LABEL_STYLE);
        FormComboBoxCellEditor typeEditor = new FormComboBoxCellEditor(client, TYPE_OPTS, form.getMessageManager());
        type = (CCombo) typeEditor.getControl();
        layoutData = new GridData();
        layoutData.horizontalSpan = 3;
        type.setLayoutData(layoutData);
        
        // JFace Viewer
        label = toolkit.createLabel(client, COLOR_MAP_LBL, LABEL_STYLE);
        FormComboBoxCellEditor colorEditor = new FormComboBoxCellEditor(client, new String[]{}, form.getMessageManager());
        CCombo colorCombo = (CCombo) colorEditor.getControl();
        colorMap = new ComboViewer(colorCombo);
        layoutData = new GridData();
        layoutData.horizontalSpan = 3;
        colorMap.getControl().setLayoutData(layoutData);

        // hook up to data
        colorMap.setContentProvider(new IStructuredContentProvider(){
            public Object[] getElements( Object inputElement ) {
                if (inputElement instanceof Object[]) {
                    return (Object[]) inputElement;
                }
                return null;
            }
            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
                // For dynamic content we would register listeners here
            }
            public void dispose() {
                // Nothing
            }
        });
        colorMap.setLabelProvider(new LabelProvider(){
            public String getText( Object element ) {
                return " " + element + " color";
            }
        });
        colorMap.setInput(COLOR_MAP_OPTS);

        // Other sample section - to try out ColumnLayout
        final Section sectionOther = toolkit.createSection(form.getBody(), SECTION_STYLE);
        sectionOther.setText("Others");
        sectionOther
                .setDescription("Sample section to demo ColumnLayout, make the view width smaller to force it to relayout.");
        sectionOther.addExpansionListener(new ExpansionAdapter(){
            public void expansionStateChanged( ExpansionEvent e ) {
                // Nothing
            }
        });
        final Composite clientOther = toolkit.createComposite(sectionOther, SWT.NONE);
        sectionLayout = new GridLayout();
        sectionLayout.numColumns = 4;
        clientOther.setLayout(sectionLayout);
        sectionOther.setClient(clientOther);

        Label remarksLbl = toolkit.createLabel(clientOther, REMARKS_LBL, LABEL_STYLE);
        FormTextCellEditor remarksEditor = new FormTextCellEditor(clientOther, form.getMessageManager());
        Text remarks = (Text) remarksEditor.getControl();
        remarks.setData(REMARKS_LBL);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 3;
        remarks.setLayoutData(layoutData);

        // Buttons
        apply = new Action("Commit"){
            @Override
            public void run() {
                applyChanges();
            }
        };
        apply.setEnabled(false);
        form.getToolBarManager().add(apply);

        reset = new Action("Rollback"){
            @Override
            public void run() {
                resetChanges();
            }
        };
        reset.setEnabled(false);
        form.getToolBarManager().add(reset);

        form.getToolBarManager().update(true);

    }

    private void addListeners() {
        nameFormal.addKeyListener(this);
        nameFormal.addFocusListener(this);
        nameSort.addKeyListener(this);
        nameSort.addFocusListener(this);
        population.addKeyListener(this);
        population.addFocusListener(this);
        type.addSelectionListener(this);
        colorMap.addSelectionChangedListener(this);
    }
    
    private void removeListeners() {
        nameFormal.removeKeyListener(this);
        nameFormal.removeFocusListener(this);
        nameSort.removeKeyListener(this);
        nameSort.removeFocusListener(this);
        population.removeKeyListener(this);
        population.removeFocusListener(this);
        type.removeSelectionListener(this);
        colorMap.removeSelectionChangedListener(this);
    }
    
    public void setFocus() {
        nameFormal.setFocus();
    }

    private void setEnabled( boolean enabled ) {
        if (baseFeature == null && enabled) {
            return;
        }
        apply.setEnabled(enabled);
        reset.setEnabled(enabled);
    }

    public void setEditFeature( SimpleFeature newFeature, IToolContext newContext ) {
        
        removeListeners();
        
        this.context = newContext;
        baseFeature = SimpleFeatureBuilder.copy(newFeature);

        if (baseFeature != null) {
            try {
                final IEditManager editManager = ApplicationGIS.getActiveMap().getEditManager();
                editFeature = editManager.toEditFeature(newFeature, null);
                editFeature.addEditFeatureListener(this);
            } catch (IllegalAttributeException e) {
                // shouldn't happen
                e.printStackTrace();
            }
        } else {
            editFeature = null;
        }
        
        if (baseFeature == null) {
            nameSort.setText("");
            colorMap.setSelection(new StructuredSelection());
            nameFormal.setText("");
        } else {

            // Set UI value for NAME_FORMA
            String nameFormalVal = (String) baseFeature.getAttribute(NAME_FORMA);
            if (nameFormalVal == null) nameFormalVal = "";
            nameFormal.setText(nameFormalVal);

            // Set UI value for NAME_SORT
            String nameSortStr = (String) baseFeature.getAttribute(NAME_SORT);
            if (nameSortStr == null) nameSortStr = "";
            nameSort.setText(nameSortStr);

            // Set UI value for POP_EST
            BigDecimal popEst = new BigDecimal((Double) baseFeature.getAttribute(POP_EST));
            population.setText(popEst.toString());

            // Set UI value for TYPE
            String typeStr = (String) baseFeature.getAttribute(TYPE);
            int selectedIndex = 0;
            if (TYPE_COU_LBL.equalsIgnoreCase(typeStr)) {
                selectedIndex = 1;
            }
            type.select(selectedIndex);

            // Set UI value for COLOR_MAP
            Double colorText = (Double) baseFeature.getAttribute(COLOR_MAP);
            if (colorText != null) {
                colorMap.setSelection(new StructuredSelection(colorText));
            } else {
                colorMap.setSelection(new StructuredSelection());
            }

        }
        
        setEnabled(false);
        addListeners();
        
    }

    private void resetChanges() {
        editFeature.setAttribute(NAME_FORMA, baseFeature.getAttribute(NAME_FORMA));
        editFeature.setAttribute(NAME_SORT, baseFeature.getAttribute(NAME_SORT));    
        editFeature.setAttribute(POP_EST, baseFeature.getAttribute(POP_EST));   
        editFeature.setAttribute(TYPE, baseFeature.getAttribute(TYPE));    
        editFeature.setAttribute(COLOR_MAP, baseFeature.getAttribute(COLOR_MAP));    
        applyChanges();
        form.getMessageManager().removeAllMessages();
        setEditFeature(baseFeature, context);
    }

    private void applyChanges() {
        context.sendSyncCommand(new CommitCommand());
        setDirty(false);
        setEnabled(false);
    }

    private void setDirty(boolean isDirty) {
        editFeature.getState(NAME_FORMA).setDirty(isDirty);
        editFeature.getState(NAME_SORT).setDirty(isDirty);
        editFeature.getState(POP_EST).setDirty(isDirty);
        editFeature.getState(TYPE).setDirty(isDirty);
        editFeature.getState(COLOR_MAP).setDirty(isDirty);
    }
    
    private static final String MANDATORY_MSG = "Must not be blank.";
    
    private boolean isMandatory(Text text, String attributeName) {
        if (editFeature != null) {
            form.getMessageManager().removeMessages(text);
            editFeature.getState(attributeName).removeError(MANDATORY_MSG);
            if (text.getText() == null || "".equals(text.getText())) {
                form.getMessageManager().addMessage(text, MANDATORY_MSG, null, IMessage.ERROR, text);
                editFeature.getState(attributeName).addError(MANDATORY_MSG);
                return false;
            }
        }
        return true;
    }

    private static final String NUMBER_MSG = "Must not a number.";
    
    private boolean isNumber( Text text, String attributeName ) {
        if (editFeature != null) {
            form.getMessageManager().removeMessages(text);
            editFeature.getState(attributeName).removeError(NUMBER_MSG);
            try {
                Double.valueOf(text.getText());
            } catch (NumberFormatException e) {
                form.getMessageManager().addMessage(text, NUMBER_MSG, null, IMessage.ERROR, text);
                editFeature.getState(attributeName).addError(NUMBER_MSG);
                return false;
            }
        }
        return true;
    }

    @Override
    public void attributeValueBeforeChange(PropertyChangeEvent event) {
        final String attributeName = event.getPropertyName();
        if (NAME_FORMA.equals(attributeName)) {
            isMandatory(nameFormal, NAME_FORMA);
        } else if (NAME_SORT.equals(attributeName)) {
            isMandatory(nameSort, NAME_SORT);
        } else if (POP_EST.equals(attributeName)) {
            isMandatory(population, POP_EST);
            isNumber(population, POP_EST);
        }
    }

    @Override
    public void attributeValueChange(PropertyChangeEvent event) {
        final AttributeStatus status = editFeature.getState(event.getPropertyName());
        status.setDirty(true);
        // Set enabled, visible and editable here
    }

    @Override
    public void attributeStateChange(EditFeatureStateChangeEvent stateChangeEvent) {
        // Update UI based on state change here
    }

    @Override
    public void focusGained(FocusEvent e) {
        // Nothing        
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (editFeature != null) {
            final Object source = e.getSource();
            if (source == nameFormal) {
                final String nameFormalValue = (String) editFeature.getAttribute(NAME_FORMA);
                if (!nameFormalValue.equals(nameFormal.getText())) {
                    editFeature.setAttribute(NAME_FORMA, nameFormal.getText());
                }
            } else if (source == nameSort) {
                final String nameSortValue = (String) editFeature.getAttribute(NAME_SORT);
                if (!nameSortValue.equals(nameSort.getText())) {
                    editFeature.setAttribute(NAME_SORT, nameSort.getText());    
                }
            } else if (source == population) {
                final Double nameSortOldValue = (Double) editFeature.getAttribute(POP_EST);
                final Double nameSortNewValue = Double.valueOf(population.getText());
                if (nameSortOldValue.compareTo(nameSortNewValue) != 0) {
                    editFeature.setAttribute(POP_EST, nameSortNewValue);    
                }
            }    
        }
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        setEnabled(true);
        if (editFeature != null) {
            final Object source = e.getSource();
            if (source == type) {
                final String typeNewStr = type.getSelectionIndex() == 0 ? TYPE_SOV_LBL : TYPE_COU_LBL;
                final String typeOldValue = (String) editFeature.getAttribute(TYPE);
                if (!typeOldValue.equals(typeNewStr)) {
                    editFeature.setAttribute(TYPE, typeNewStr);    
                }
            }    
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // Nothing
    }

    @Override
    public void selectionChanged( SelectionChangedEvent event ) {
        setEnabled(true);
        if (editFeature != null) {
            final Object source = event.getSource();
            if (source == colorMap) {
                final StructuredSelection colorSelection = (StructuredSelection) colorMap.getSelection();
                final Object typeOldValue = editFeature.getAttribute(COLOR_MAP);
                if (typeOldValue != colorSelection.getFirstElement()) {
                    editFeature.setAttribute(COLOR_MAP, colorSelection.getFirstElement());    
                }    
            }    
        }
    }

    @Override
    public void keyPressed( KeyEvent e ) {
        // Nothing
    }

    @Override
    public void keyReleased( KeyEvent e ) {
        setEnabled(true);
    }
    
}
