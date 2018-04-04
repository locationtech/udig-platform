/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.ui.internal.Messages;
import org.locationtech.udig.ui.preferences.PreferenceConstants;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * A composite editor based on a JFace TreeViewer for creating and editing feature types.
 * 
 * @author jones
 * @author Andrea Antonello (www.hydrologis.com)
 * @since 1.1.0
 */
public class FeatureTypeEditor {

    private static final int MAX_ATTRIBUTE_LENGTH = 10485759;  //Maximum allows by postgis and is "big enough" 
    
    private static final List ALLOWED_BINDINGS_FOR_LENGHT_SET = Arrays.asList(String.class, Double.class, Long.class, Integer.class, Short.class);
    /**
     * The index of the name column in the viewer.
     */
    private static final int NAME_COLUMN = 0;
    /**
     * The index of the type column in the viewer.
     */
    private static final int TYPE_COLUMN = 1;
    /**
     * The index of the type column in the viewer.
     */
    private static final int OTHER_COLUMN = 2;
    /**
     * The index of the length column in the viewer.
     */
    private static final int LENGTH_COLUMN = 3;
    /**
     * The index of the NULL column in the viewer.
     */
    private static final int IS_NULL_COLUMN = 4;
    
    private static final List<LegalAttributeTypes> TYPES;
    static {
        List<LegalAttributeTypes> types = new ArrayList<LegalAttributeTypes>();
        types
        .add(new LegalAttributeTypes(
                Messages.FeatureTypeEditor_stringType, String.class)); 
        types
        .add(new LegalAttributeTypes(Messages.FeatureTypeEditor_booleanType, Boolean.class)); 
        types.add(new LegalAttributeTypes(Messages.FeatureTypeEditor_dateType, Date.class)); 
        types.add(new LegalAttributeTypes(
                Messages.FeatureTypeEditor_integerType, Integer.class)); 
        types.add(new LegalAttributeTypes(
                Messages.FeatureTypeEditor_longType, Long.class)); 
        types.add(new LegalAttributeTypes(Messages.FeatureTypeEditor_floatType, Float.class)); 
        types.add(new LegalAttributeTypes(Messages.FeatureTypeEditor_doubleType, Double.class)); 
        types.add(new LegalAttributeTypes(Messages.FeatureTypeEditor_pointType, Point.class)); 
        types.add(new LegalAttributeTypes(
                Messages.FeatureTypeEditor_lineStringType, LineString.class)); 
        types.add(new LegalAttributeTypes(
                Messages.FeatureTypeEditor_polygonType, Polygon.class)); 
        types.add(new LegalAttributeTypes(
                Messages.FeatureTypeEditor_geometryType, Geometry.class)); 
        types.add(new LegalAttributeTypes(
                Messages.FeatureTypeEditor_multiPointType, MultiPoint.class)); 
        types.add(new LegalAttributeTypes(
                Messages.FeatureTypeEditor_multiLineStringType, MultiLineString.class)); 
        types.add(new LegalAttributeTypes(
                Messages.FeatureTypeEditor_multiPolygonType, MultiPolygon.class)); 

        TYPES = Collections.unmodifiableList(types);
    }

    private TreeViewer viewer;
    private IAction createAttributeAction;
    private IAction deleteAttributeAction;
    private Text nameText;
    private List<LegalAttributeTypes> legalTypes=TYPES;
	private SimpleFeatureType featureType;
    private ControlDecoration errorDecorator;

    /**
     * Create the table control and set the input.
     * 
     * @param parent the composite that will be used as the TreeViewer's parent.
     * @param layoutData the layout data to use to layout the editor. If null GridData(Fill_Both)
     */
    public void createTable( Composite parent, Object layoutData ) {
        createTable(parent, layoutData, featureType, true);
    }
    /**
     * Create the table control and set the input.
     * 
     * @param parent the composite that will be used as the TreeViewer's parent.
     * @param layoutData the layout data to use to layout the editor. If null GridData(Fill_Both)
     */
    public void createTable( Composite parent, Object layoutData, SimpleFeatureType type ) {
    	SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    	builder.setName(type.getName());
        builder.init(type);
        createTable(parent, layoutData, builder.buildFeatureType(), true);
    }
    /**
     * Create the table control and set the input.
     * 
     * @param parent the composite that will be used as the TreeViewer's parent.
     * @param layoutData the layout data to use to layout the editor. If null GridData(Fill_Both).
     * @param featureType the {@link FeatureType} to use to populate the table.
     * @param editable the editable flag of the table
     */
    public void createTable( Composite parent, Object layoutData, SimpleFeatureType featureType,
            boolean editable ) {

        viewer = new TreeViewer(parent, SWT.FULL_SELECTION);

        Tree tree = viewer.getTree();
        if (layoutData == null)
            tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        else
            tree.setLayoutData(layoutData);

        tree.setHeaderVisible(true);
        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1));
        tableLayout.addColumnData(new ColumnWeightData(1));
        tableLayout.addColumnData(new ColumnWeightData(1));
        tableLayout.addColumnData(new ColumnWeightData(1));
        tableLayout.addColumnData(new ColumnWeightData(1));

        tree.setLayout(tableLayout);

        TreeColumn column = new TreeColumn(tree, SWT.CENTER);
        column.setResizable(true);
        column.setText(Messages.FeatureTypeEditor_nameColumnName); 

        column = new TreeColumn(tree, SWT.CENTER);
        column.setResizable(true);
        column.setText(Messages.FeatureTypeEditor_typeColumnName); 

        column = new TreeColumn(tree, SWT.CENTER);
        column.setResizable(true);
        
        column = new TreeColumn(tree, SWT.CENTER);
        column.setResizable(true);
        column.setText(Messages.FeatureTypeEditor_lengthColumnName); 

        column = new TreeColumn(tree, SWT.CENTER);
        column.setResizable(true);
        column.setText(Messages.FeatureTypeEditor_isNullColumnName); 

        viewer.setContentProvider(new FeatureTypeContentProvider(viewer));
        viewer.setLabelProvider(new FeatureTypeLabelProvider());
        viewer.setColumnProperties(new String[]{String.valueOf(NAME_COLUMN),
                String.valueOf(TYPE_COLUMN),
                String.valueOf(OTHER_COLUMN),
                String.valueOf(LENGTH_COLUMN),
                String.valueOf(IS_NULL_COLUMN)
                });

        setEditable(editable);
        setFeatureType(featureType);
    }

    /**
     * Declares what types are permitted as attributes.  For example Shapefiles do not permit
     * Geometry as a legal type.
     *
     * @param legalTypes the List of legal types in the order they will be displayed.
     */
    public void setLegalTypes(List<LegalAttributeTypes> legalTypes){
        this.legalTypes=Collections.unmodifiableList(legalTypes);
    }
    
    /**
     * @return Returns the list of types that this editor will allow the use to select
     */
    public List<LegalAttributeTypes> getLegalTypes(){
        return Collections.unmodifiableList(legalTypes);
    }
    
    /**
     * Sets whether the table is editable or just a viewer.
     * 
     * @param editable if true then the table can be edited
     */
    public void setEditable( boolean editable ) {
        if (editable) {
            Tree tree = viewer.getTree();
            String[] comboItems = new String[legalTypes.size()];
            for( int i = 0; i < comboItems.length; i++ ) {
                comboItems[i] = legalTypes.get(i).getName();
            }

            TextCellEditor attributeNameEditor = new TextCellEditor(tree);
            ComboBoxCellEditor attributeTypeEditor = new ComboBoxCellEditor(tree, comboItems, SWT.READ_ONLY|SWT.FULL_SELECTION);
            TextCellEditor attributeLengthEditor = new TextCellEditor(tree);
            BooleanCellEditor attributeIsNullEditor = new BooleanCellEditor(tree);
            DialogCellEditor crsEditor = createCRSEditor(tree);
            viewer.setCellEditors(new CellEditor[]{attributeNameEditor,
                    attributeTypeEditor,
                    crsEditor,
                    attributeLengthEditor,
                    attributeIsNullEditor
            });


            viewer.setCellModifier(new AttributeCellModifier());
        } else {
            viewer.setCellEditors(null);
            viewer.setCellModifier(null);
        }
    }
    private DialogCellEditor createCRSEditor( Tree tree ) {
        return new CRSDialogCellEditor(tree);
    }
    
    public SimpleFeatureTypeBuilder builderFromFeatureType( SimpleFeatureType ft ) {
        SimpleFeatureTypeBuilder ftB;
        ftB = new SimpleFeatureTypeBuilder();
        ftB.init(ft);
        ftB.setName(ft.getName());
        return ftB;
    }

    /**
     * Creates a ContextMenu (the menu is created using the Table's composite as a parent) and returns
     * the contextMenu.
     *
     * <p>It is recommended that the MenuManager be registered with an IWorkbenchPartSite</p>
     * @return a MenuManager for the contextMenu.
     */
    public MenuManager createContextMenu(){
        final MenuManager contextMenu = new MenuManager();

        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener(){
            public void menuAboutToShow( IMenuManager mgr ) {
                contextMenu.add(getCreateAttributeAction());
                contextMenu.add(getDeleteAction());
            }
        });

        Menu menu = contextMenu.createContextMenu(viewer.getTree());
        viewer.getControl().setMenu(menu);
        
        return contextMenu;
    }

    /**
     * Sets the Global actions that apply.  IE sets the delete global action.
     *
     * @param actionBars
     */
    public void setGlobalActions( IActionBars actionBars){
        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), getDeleteAction());
    }
    
    /**
     * Sets the {@link SimpleFeatureType} being edited.
     * 
     * <p>If type is null then a new featureType is created. Must be
     * called in the display thread.</p>
     * 
     * @param type then new SimpleFeatureType to be edited, or null to create a new type.
     */
    public void setFeatureType( SimpleFeatureType type ) {
        SimpleFeatureTypeBuilder builder = null;
        if (type != null) {
            builder = new SimpleFeatureTypeBuilder();
            builder.init(type);
            builder.setName(type.getName());
            featureType = builder.buildFeatureType();
        }else{
            featureType = createDefaultFeatureType();
        }
        if (viewer != null) {
            setInput(featureType);
        }
        
    }

     /**
    * Sets the FeatureTypeBuilder used for creating the feature type.
    *
    * @param builder
    * @deprecated with the new {@link SimpleFeatureTypeBuilder} this is no 
    *             more possible, therefore deprecating.
    */
    public final void setFeatureTypeBuilder( SimpleFeatureTypeBuilder newBuilder ) {
        // if (newBuilder == null) {
        // featureType = createDefaultFeatureType();
        // } else
        // featureType = newBuilder;
        //
        // if (viewer != null) {
        // setInput(featureType);
        // }
    }
    
    /**
     * Creates a default {@link FeatureType}.
     * 
     * <p>
     * The default type has a {@link Geometry} attribute and a name 
     * attribute.
     * The geometry attribute is a {@link LineString}.
     * </p>
     * 
     * @return a default FeatureType.
     */
    public SimpleFeatureType createDefaultFeatureType() {
        SimpleFeatureTypeBuilder builder;
        builder = new SimpleFeatureTypeBuilder();
        builder.setName(Messages.FeatureTypeEditor_newFeatureTypeName); 
        builder.setCRS(getDefaultCRS());
        //no need to provide a default length since length can be specified via the UI
        //builder.length(MAX_ATTRIBUTE_LENGTH).add(Messages.FeatureTypeEditor_defaultNameAttributeName, String.class); 
        builder.add(Messages.FeatureTypeEditor_defaultNameAttributeName, String.class); 
        
        //add geometry attribute as not null
        AttributeTypeBuilder attribBuilder = new AttributeTypeBuilder();
        attribBuilder.setName(Messages.FeatureTypeEditor_defaultGeometryName);
        attribBuilder.setCRS(builder.getCRS());
        attribBuilder.nillable(false);
        attribBuilder.binding(LineString.class);
        builder.add(attribBuilder.buildDescriptor(Messages.FeatureTypeEditor_defaultGeometryName)); 
        return builder.buildFeatureType();
    }

    private CoordinateReferenceSystem getDefaultCRS() {
        String crsInfo=UiPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_DEFAULT_GEOMEMTRY_CRS);
        if( crsInfo!=null && crsInfo.trim().length()>0 ){
            try{
                crsInfo=crsInfo.trim();
                if( crsInfo.startsWith("EPSG") ){ //$NON-NLS-1$
                    return CRS.decode(crsInfo);
                }
                return CRS.parseWKT(crsInfo);
            }catch(Throwable t){
                UiPlugin.log("",t); //$NON-NLS-1$
            }
        }
        return DefaultGeographicCRS.WGS84;
    }
    
    public void setDefaultCRS(CoordinateReferenceSystem crs ){
        String crsInfo=null;
        
        Set<ReferenceIdentifier> identifiers = crs.getIdentifiers();
        for( Identifier identifier : identifiers ) {
            if( identifier.toString().startsWith("EPSG") ){ //$NON-NLS-1$
                crsInfo=identifier.toString();
                break;
            }
        }
        
        if( crsInfo==null )
            crsInfo=crs.toWKT();
         
        UiPlugin.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_DEFAULT_GEOMEMTRY_CRS, crsInfo);
        
        SimpleFeatureTypeBuilder tmpBuilder = new SimpleFeatureTypeBuilder();
        tmpBuilder.init(featureType);
        tmpBuilder.setName(featureType.getTypeName());
        tmpBuilder.setCRS(crs);
        featureType = tmpBuilder.buildFeatureType();
        
    }
    
    private void setInput( SimpleFeatureType featureType ) {
        viewer.setInput(featureType);
        if (nameText != null && !nameText.isDisposed()) {
            nameText.setText(featureType.getTypeName());
        }
    }

    /**
     * Returns an action that will add a new attribute to the SimpleFeatureType.
     * 
     * @return an action that will add a new attribute to the SimpleFeatureType.
     */
    public synchronized IAction getCreateAttributeAction() {
        if (createAttributeAction == null) {
            createAttributeAction = new Action(){
                @Override
                public void runWithEvent( Event event ) {
                    SimpleFeatureType ft = (SimpleFeatureType) viewer.getInput();
                    SimpleFeatureTypeBuilder ftB = builderFromFeatureType(ft);

                    int index = 0;
                    List<Integer> usedIndices = new ArrayList<Integer>();

                    // get max Index of added attributes
                    for (AttributeDescriptor at: ft.getAttributeDescriptors()) {
                        if (at.getLocalName().startsWith(Messages.FeatureTypeEditor_newAttributeTypeDefaultName)) {
                            try {
                                usedIndices.add(Integer.valueOf(at.getLocalName().replace(Messages.FeatureTypeEditor_newAttributeTypeDefaultName, "")));
                            } catch (Exception e) {
                                // ignore,  user has changed attribute
                            }
                            index++;

                        }
                    }

                    Collections.sort(usedIndices, Collections.reverseOrder());
                    // TODO improve : implement algorithm to find first unused number in sorted list
                    if (usedIndices.size() > 0) {
                        index = usedIndices.get(0)  + 1; // reverse ordered, highest first
                    }

                    ftB.add(Messages.FeatureTypeEditor_newAttributeTypeDefaultName + index, String.class);
                    featureType = ftB.buildFeatureType();
                    viewer.setInput(featureType);
                    // TODO check if it is better to do something and then: viewer.refresh(false);
                }
            };
            createAttributeAction.setId("org.locationtech.udig.ui.FeatureTypeEditor.createAttributeAction"); //$NON-NLS-1$
            createAttributeAction.setText(Messages.addAttributeAction_label); 
            createAttributeAction.setToolTipText(Messages.addAttributeAction_label); 
            createAttributeAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor("elcl16/new_attribute.gif")); //$NON-NLS-1$
        }
        return createAttributeAction;
    }

    /**
     * Returns an action that will delete the selected attributes from the SimpleFeatureType.
     * 
     * @return an action that will delete the selected attributes from the SimpleFeatureType.
     */
    public synchronized IAction getDeleteAction() {
        if (deleteAttributeAction == null) {
            deleteAttributeAction = new Action(){

                @SuppressWarnings("unchecked") 
                @Override
                public void runWithEvent( Event event ) {
                    SimpleFeatureType ft = (SimpleFeatureType) viewer.getInput();
                    SimpleFeatureTypeBuilder ftB = builderFromFeatureType(ft);
                    IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                    for( Iterator<AttributeDescriptor> iter = selection.iterator(); iter.hasNext(); ) {
                        AttributeDescriptor element = iter.next();
                        ftB.remove(element.getLocalName());
                    }
                    featureType = ftB.buildFeatureType();
                    viewer.setInput(featureType);
                }
            };
            deleteAttributeAction.setText(Messages.deleteAttributeAction_label);
            deleteAttributeAction.setToolTipText(Messages.deleteAttributeAction_tooltip);
            deleteAttributeAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor("elcl16/delete.gif")); //$NON-NLS-1$
            deleteAttributeAction.setDescription(Messages.deleteAttributeAction_description);
            deleteAttributeAction.setId("org.locationtech.udig.ui.FeatureTypeEditor.deleteAttributeAction"); //$NON-NLS-1$
        }
        return deleteAttributeAction;
    }

    /**
     * Creates a Text input object that for modify the feature type name.
     * 
     * @param parent the parent of the text object
     * @return
     */
    public void createFeatureTypeNameText( Composite parent, Object layoutData ) {
        
        nameText = new Text(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        errorDecorator = new ControlDecoration(nameText, SWT.TOP|SWT.LEFT);
        Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
        errorDecorator.setImage(image);
        
        if (viewer != null) {
            SimpleFeatureType input = ((SimpleFeatureType) viewer.getInput());
            if( input!=null )
            nameText.setText(input.getTypeName());
        }
        if (layoutData != null)
            nameText.setLayoutData(layoutData);
        else {
            nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        }
        
        class NameListener implements KeyListener, FocusListener {

            public void keyPressed( KeyEvent e ) {
                SimpleFeatureType ft = (SimpleFeatureType) viewer.getInput();
                if (e.character == SWT.ESC) {
                    nameText.setText(ft.getTypeName());
                } else 
                if (e.character == SWT.Selection) {
                    SimpleFeatureTypeBuilder ftB = new SimpleFeatureTypeBuilder();
                    ftB.init(ft);
                    ftB.setName(nameText.getText());
                    featureType = ftB.buildFeatureType();
                    viewer.setInput(featureType);
                } else {
                    errorDecorator.hide();
                }
            }

            public void keyReleased( KeyEvent e ) {
                SimpleFeatureType ft = (SimpleFeatureType) viewer.getInput();
                SimpleFeatureTypeBuilder ftB = new SimpleFeatureTypeBuilder();
                ftB.init(ft);
                ftB.setName(nameText.getText());
                featureType = ftB.buildFeatureType();
                viewer.setInput(featureType);
            }

            public void focusGained( FocusEvent e ) {
                int end = nameText.getText().length();
                nameText.setSelection(0, end);
            }

            public void focusLost( FocusEvent e ) {
                SimpleFeatureType ft = (SimpleFeatureType) viewer.getInput();
                SimpleFeatureTypeBuilder ftB = new SimpleFeatureTypeBuilder();
                ftB.init(ft);
                ftB.setName(nameText.getText());
                featureType = ftB.buildFeatureType();
                viewer.setInput(featureType);
            }

        }

        nameText.setFocus();
        NameListener listener = new NameListener();
        nameText.addKeyListener(listener);
        nameText.addFocusListener(listener);
        

    }

    /**
     * Retrieves the new SimpleFeatureType. Must be called in the display thread. May return null.
     * 
     * @return the new SimpleFeatureType.
     */
    public SimpleFeatureType getFeatureType() {
        if( viewer==null )
            return null;
        return (SimpleFeatureType) viewer.getInput();
    }

    /**
     * Returns the FeatureTypeBuilder that is used for editing the feature type.
     * 
     * @return the FeatureTypeBuilder that is used for editing the feature type.
     */
    public SimpleFeatureTypeBuilder getFeatureTypeBuilder() {
        if( viewer==null )
            return null;
        
        return builderFromFeatureType((SimpleFeatureType) viewer.getInput());
    }

    /**
     * Returns the control that is the FeatureTypeEditor.
     * 
     * @return the control that is the FeatureTypeEditor.
     */
    public Control getControl() {
        return viewer.getControl();
    }

    /**
     * Label provider for labeling AttributeTypes.
     * 
     * @author jones
     * @since 1.1.0
     */
    public static class FeatureTypeLabelProvider extends LabelProvider
            implements
                IBaseLabelProvider,
                ITableLabelProvider {

        public Image getColumnImage( Object element, int columnIndex ) {
            return null;
        }

        public String getColumnText( Object element, int columnIndex ) {
            AttributeDescriptor attribute = (AttributeDescriptor) element;
            switch( columnIndex ) {
            case 0: // Attribute Name element
                return attribute.getLocalName();
            case 1: // Attribute Type element
                return attribute.getType().getBinding().getSimpleName();
            case 2: // Attribute Type element
                if (attribute instanceof GeometryDescriptor) {
                    CoordinateReferenceSystem crs = ((GeometryDescriptor)attribute).getCoordinateReferenceSystem();
                    if(crs!=null){
                        return crs.getName().toString();
                    }else {
                        return "Unspecified";
                    }
                }
                break;
            case 3: // Attribute Type element
                return getAttributeRestriction(attribute, "length", String.class); //$NON-NLS-1$
            case 4: // Attribute Type element
                return Boolean.toString(attribute.isNillable());

            default:
                break;
            }
            return null;
        }

    }

    /**
     * A Tree Content Provider that serves up attributeTypes from a SimpleFeatureType as a parent.
     * 
     * @author jones
     * @since 1.1.0
     */
    public static class FeatureTypeContentProvider implements ITreeContentProvider {

        private TreeViewer viewer;

        public FeatureTypeContentProvider( TreeViewer viewer ) {
            this.viewer = viewer;
        }

        public void dispose() {
        }

        public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        }

        public Object[] getChildren( Object parentElement ) {
            if (parentElement instanceof SimpleFeatureType) {
                SimpleFeatureType featureType = (SimpleFeatureType) parentElement;
				Object[] attributes = new Object[featureType.getAttributeCount()];
                for( int i = 0; i < attributes.length; i++ ) {
                    attributes[i] = featureType.getDescriptor(i);
                }
                return attributes;
            }
            return null;
        }

        public Object getParent( Object element ) {
            if (element instanceof AttributeDescriptor) {
                return viewer.getInput();
            }
            return null;
        }

        public boolean hasChildren( Object element ) {
            if (element instanceof SimpleFeatureType)
                return true;
            return false;
        }

        public Object[] getElements( Object inputElement ) {
            return getChildren(inputElement);
        }

    }

    public class AttributeCellModifier implements ICellModifier {

        private Object lastCRS=getDefaultCRS();

        public boolean canModify( Object element, String property ) {
            if (String.valueOf(OTHER_COLUMN).equals(property) && !(element instanceof GeometryDescriptor))
                return false;
            if (String.valueOf(LENGTH_COLUMN).equals(property) && 
                    !(ALLOWED_BINDINGS_FOR_LENGHT_SET.contains(((AttributeDescriptor)element).getType().getBinding()))) {
                return false;
            }
            return true;
        }

        public Object getValue( Object element, String property ) {
            AttributeDescriptor editElement = (AttributeDescriptor) element;
            switch( Integer.parseInt(property) ) {
            case NAME_COLUMN:
                return editElement.getName().toString();
            case TYPE_COLUMN:
                for( int i = 0; i < legalTypes.size(); i++ ) {
                    if (legalTypes.get(i).getType() == editElement.getType().getBinding())
                        return i;
                }
                return -1;
            case OTHER_COLUMN:
                return ((GeometryDescriptor)element).getCoordinateReferenceSystem();
            case LENGTH_COLUMN:
                String lengthValue = getAttributeRestriction(editElement, "length", String.class); //$NON-NLS-1$
                return lengthValue != null ? lengthValue : "";
            case IS_NULL_COLUMN:
                String nullValue = getAttributeRestriction(editElement, "nillable", String.class); //$NON-NLS-1$
                return nullValue == null ? editElement.isNillable() : Boolean.getBoolean(nullValue);
            }

            return null;
        }

        public void modify( Object element, String property, Object value ) {
            if( element==null || property==null || value==null ){
                return;
            }
            
            AttributeDescriptor editElement = (AttributeDescriptor) ((TreeItem) element).getData();
            SimpleFeatureType ft = (SimpleFeatureType) viewer.getInput();
            AttributeDescriptor newAttr = createNewAttributeType(editElement, property, value );
            
            if (newAttr == null)
                return;
            int index = 0;
            for( ; index < ft.getAttributeCount(); index++ ) {
                if (ft.getDescriptor(index) == editElement)
                    break;
            }
            if (index == ft.getAttributeCount())
                return;
            SimpleFeatureTypeBuilder builder = builderFromFeatureType(ft);
            builder.remove(ft.getDescriptor(index).getLocalName());
            builder.add(index, newAttr);
            featureType = builder.buildFeatureType();
            viewer.setInput(featureType);
        }

        private AttributeDescriptor createNewAttributeType( AttributeDescriptor editElement, String property,
                Object value ) {
        	AttributeTypeBuilder builder = new AttributeTypeBuilder();
        	builder.init(editElement);
        	//builder.setName((String)property);
        	
            switch( Integer.parseInt(property) ) {
            case NAME_COLUMN:
                return builder.buildDescriptor((String)value);
            case TYPE_COLUMN:
                int choice = -1;
                if( value instanceof Integer) {
                    choice = (Integer) value;
                }
                else if( value instanceof String) {
                    choice = Integer.parseInt( (String) value );
                }
                
                if (choice == -1)
                    return null;
                else {
                    Class type = legalTypes.get(choice).getType();
                    builder.setBinding(type);
                    return builder.buildDescriptor( editElement.getLocalName());
                }
            case OTHER_COLUMN:
                lastCRS=value;
                
                CoordinateReferenceSystem crs = (CoordinateReferenceSystem) value;
                if( FeatureTypeEditor.this.featureType.getGeometryDescriptor()==editElement ){
					setDefaultCRS(crs);
                }

                builder.setCRS(crs);
                return builder.buildDescriptor( editElement.getLocalName());
            case LENGTH_COLUMN:
                try {
                    int length = Integer.parseInt((String)value);
                    builder.length(length);
                    return builder.buildDescriptor(editElement.getLocalName());
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                    return null;
                } 
            case IS_NULL_COLUMN:
                boolean isNull = true;
                if( value instanceof Boolean) {
                    isNull = (Boolean) value;
                }
                else if( value instanceof String) {
                    isNull = Boolean.getBoolean((String)value);
                }
                builder.nillable(isNull);
                builder.setMinOccurs(1);
                return builder.buildDescriptor(editElement.getLocalName());

            default:
                return null;
            }
        }

    }

    /**
     * PUBLIC <b>ONLY</b> so tests can verify the correct behaviour.
     */
    public TreeViewer testingGetViewer() {
        return viewer;
    }

    /**
     * PUBLIC <b>ONLY</b> so tests can verify the correct behaviour.
     */
    public static List<LegalAttributeTypes> testingGetTYPES() {
        return TYPES;
    }

    public Text testingGetNameText() {
        return nameText;
    }
    /**
     * Updates the viewer so it matches the state of the builder.
     */
    public void builderChanged() {
        viewer.refresh();
        if (nameText != null && !nameText.isDisposed()) {
            if( viewer.getInput()!=null){
                String typeName = ((SimpleFeatureType) viewer.getInput()).getTypeName();
                nameText.setText(typeName);
            }
        }
    }
    
    /**
     * 
     * @param errorMessage
     */
    public void setErrorMessage( String errorMessage ) {
        errorDecorator.setDescriptionText(errorMessage);
        errorDecorator.show();
        errorDecorator.showHoverText(errorMessage);
    }

    /**
     * 
     * @param editElement
     * @param restrictionName
     * @param type
     * @return
     */
    protected static <T> T getAttributeRestriction(AttributeDescriptor editElement, String restrictionName, Class<T> type) {
        if (restrictionName == null) {
            return null;
        }
        for ( Filter r :editElement.getType().getRestrictions() ) {
            if( r instanceof PropertyIsLessThanOrEqualTo ) {
                PropertyIsLessThanOrEqualTo c = (PropertyIsLessThanOrEqualTo) r;
                if ( c.getExpression1() instanceof Function &&
                        ((Function) c.getExpression1()).getName().toLowerCase().endsWith( restrictionName.toLowerCase()) ) {
                    if ( c.getExpression2() instanceof Literal ) {
                        T value = c.getExpression2().evaluate(null,type);
                        if ( value != null ) {
                            return value;
                        }
                    }
                }
            } 
        }
        return null;
    }
}
