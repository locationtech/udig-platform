/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.refractions.udig.internal.ui.ImageConstants;
import net.refractions.udig.internal.ui.Images;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.internal.Messages;
import net.refractions.udig.ui.preferences.PreferenceConstants;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
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
import org.eclipse.ui.actions.ActionFactory;
import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.feature.SchemaException;
import org.geotools.feature.type.GeometricAttributeType;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.metadata.Identifier;
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
 * @since 1.1.0
 */
public class FeatureTypeEditor {

    private static final int MAX_ATTRIBUTE_LENGTH = 10485759;  //Maximum allows by postgis and is "big enough"
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
	private FeatureTypeBuilder builder;

    /**
     * Create the table control and set the input.
     *
     * @param parent the composite that will be used as the TreeViewer's parent.
     * @param layoutData the layout data to use to layout the editor. If null GridData(Fill_Both)
     */
    public void createTable( Composite parent, Object layoutData ) {
        createTable(parent, layoutData, builder, true);
    }
    /**
     * Create the table control and set the input.
     *
     * @param parent the composite that will be used as the TreeViewer's parent.
     * @param layoutData the layout data to use to layout the editor. If null GridData(Fill_Both)
     */
    public void createTable( Composite parent, Object layoutData, FeatureType type ) {
        FeatureTypeBuilder builder = FeatureTypeBuilder.newInstance(type.getTypeName());
        builder.importType(type, false);
        createTable(parent, layoutData, builder, true);
    }
    /**
     * Create the table control and set the input.
     *
     * @param parent the composite that will be used as the TreeViewer's parent.
     * @param layoutData the layout data to use to layout the editor. If null GridData(Fill_Both)
     */
    public void createTable( Composite parent, Object layoutData, FeatureTypeBuilder builder,
            boolean editable ) {

        viewer = new TreeViewer(parent, SWT.MULTI|SWT.FULL_SELECTION);

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

        tree.setLayout(tableLayout);

        TreeColumn column = new TreeColumn(tree, SWT.CENTER);
        column.setResizable(true);
        column.setText(Messages.FeatureTypeEditor_nameColumnName);

        column = new TreeColumn(tree, SWT.CENTER);
        column.setResizable(true);
        column.setText(Messages.FeatureTypeEditor_typeColumnName);

        column = new TreeColumn(tree, SWT.CENTER);
        column.setResizable(true);

        viewer.setContentProvider(new FeatureTypeContentProvider(viewer));
        viewer.setLabelProvider(new FeatureTypeLabelProvider());
        viewer.setColumnProperties(new String[]{String.valueOf(NAME_COLUMN),
                String.valueOf(TYPE_COLUMN),
                String.valueOf(OTHER_COLUMN)
                });

        setEditable(editable);
        setFeatureTypeBuilder(builder);
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
			DialogCellEditor crsEditor = createCRSEditor(tree);
			viewer.setCellEditors(new CellEditor[]{attributeNameEditor,
                    attributeTypeEditor,
                    crsEditor
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
     * Sets the FeatureType being edited. If type is null then a new featureType is created. Must be
     * called in the display thread.
     *
     * @param type then new FeatureType to be edited, or null to create a new type.
     */
    public void setFeatureType( FeatureType type ) {
        FeatureTypeBuilder builder = null;
        if (type != null) {
            builder = FeatureTypeBuilder.newInstance(type.getTypeName());
            builder.importType(type, false);
        }
        setFeatureTypeBuilder(builder);
    }

    /**
     * Sets the FeatureTypeBuilder used for creating the feature type.
     *
     * @param builder
     */
    public final void setFeatureTypeBuilder( FeatureTypeBuilder newBuilder ) {
        if (newBuilder == null) {
            builder = createDefaultFeatureType();
        } else
            builder = newBuilder;

        if( viewer!=null ){
        	setInput(builder);
        }
    }
    /**
     * Creates a default FeatureTypeBuilder.  The default builder has a geometry attribute and a name attribute.
     * The geometry attribute is a linestring.
     *
     * @return a default FeatureTypeBuilder.
     */
    public FeatureTypeBuilder createDefaultFeatureType() {
        FeatureTypeBuilder builder;
        builder = FeatureTypeBuilder.newInstance(
        		Messages.FeatureTypeEditor_newFeatureTypeName);
        builder.addType(AttributeTypeFactory.newAttributeType(
        		Messages.FeatureTypeEditor_defaultNameAttributeName, String.class, true, MAX_ATTRIBUTE_LENGTH));
        builder.setDefaultGeometry((GeometryAttributeType) AttributeTypeFactory.newAttributeType(
        		Messages.FeatureTypeEditor_defaultGeometryName, LineString.class,true, MAX_ATTRIBUTE_LENGTH, null, getDefaultCRS()));
        return builder;
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

        Set<Identifier> identifiers = crs.getIdentifiers();
        for( Identifier identifier : identifiers ) {
            if( identifier.toString().startsWith("EPSG") ){ //$NON-NLS-1$
                crsInfo=identifier.toString();
                break;
            }
        }

        if( crsInfo==null )
            crsInfo=crs.toWKT();

        UiPlugin.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_DEFAULT_GEOMEMTRY_CRS, crsInfo);
    }

    private void setInput( FeatureTypeBuilder builder ) {
        viewer.setInput(builder);
        if (nameText != null && !nameText.isDisposed()) {
            nameText.setText(builder.getName());
        }
    }

    /**
     * Returns an action that will add a new attribute to the FeatureType.
     *
     * @return an action that will add a new attribute to the FeatureType.
     */
    public synchronized IAction getCreateAttributeAction() {
        if (createAttributeAction == null) {
            createAttributeAction = new Action(){
                @Override
                public void runWithEvent( Event event ) {
                    FeatureTypeBuilder ft = (FeatureTypeBuilder) viewer.getInput();
                    int index = 0;
                    while( true ) {
                        try {
                            ft.addType(AttributeTypeFactory
                                            .newAttributeType(
                                                    Messages.FeatureTypeEditor_newAttributeTypeDefaultName + index, String.class));
                            break;
                        } catch (IllegalArgumentException e) {
                            index++;
                        }
                    }
                    viewer.refresh(false);
                }
            };
            createAttributeAction.setId("net.refractions.udig.ui.FeatureTypeEditor.createAttributeAction"); //$NON-NLS-1$
            createAttributeAction.setToolTipText("Add Attribute");
            createAttributeAction.setImageDescriptor(Images.getDescriptor("elcl16/new_attribute.gif")); //$NON-NLS-1$
        }
        return createAttributeAction;
    }

    /**
     * Returns an action that will delete the selected attributes from the FeatureType.
     *
     * @return an action that will delete the selected attributes from the FeatureType.
     */
    public synchronized IAction getDeleteAction() {
        if (deleteAttributeAction == null) {
            deleteAttributeAction = new Action(){

                @SuppressWarnings("unchecked")
                @Override
                public void runWithEvent( Event event ) {
                    FeatureTypeBuilder ft = (FeatureTypeBuilder) viewer.getInput();
                    IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                    for( Iterator<AttributeType> iter = selection.iterator(); iter.hasNext(); ) {
                        AttributeType element = iter.next();
                        ft.removeType(element);
                    }
                    viewer.refresh(false);
                }
            };
            deleteAttributeAction.setText(Messages.deleteAttributeAction_label);
            deleteAttributeAction.setToolTipText(Messages.deleteAttributeAction_tooltip);
            deleteAttributeAction.setImageDescriptor(Images.getDescriptor("elcl16/delete.gif")); //$NON-NLS-1$
            deleteAttributeAction.setDescription(Messages.deleteAttributeAction_description);
            deleteAttributeAction.setId("net.refractions.udig.ui.FeatureTypeEditor.deleteAttributeAction"); //$NON-NLS-1$
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
        if (viewer != null) {
            FeatureTypeBuilder input = ((FeatureTypeBuilder) viewer.getInput());
            if( input!=null )
            nameText.setText(input.getName());
        }
        if (layoutData != null)
            nameText.setLayoutData(layoutData);
        else {
            nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        }
        class NameListener implements KeyListener, FocusListener {

            public void keyPressed( KeyEvent e ) {
                FeatureTypeBuilder builder = (FeatureTypeBuilder) viewer.getInput();
                if (e.character == SWT.ESC) {
                    nameText.setText(builder.getName());
                }
                if (e.character == SWT.Selection) {
                    builder.setName(nameText.getText());
                }
            }

            public void keyReleased( KeyEvent e ) {
                FeatureTypeBuilder builder = (FeatureTypeBuilder) viewer.getInput();
                builder.setName(nameText.getText());
            }

            public void focusGained( FocusEvent e ) {
                int end = nameText.getText().length();
                nameText.setSelection(0, end);
            }

            public void focusLost( FocusEvent e ) {
                FeatureTypeBuilder builder = (FeatureTypeBuilder) viewer.getInput();
                builder.setName(nameText.getText());
            }

        }

        nameText.setFocus();
        NameListener listener = new NameListener();
        nameText.addKeyListener(listener);
        nameText.addFocusListener(listener);


    }

    /**
     * Retrieves the new FeatureType. Must be called in the display thread. May return null.
     *
     * @return the new FeatureType.
     * @throws SchemaException
     */
    public FeatureType getFeatureType() throws SchemaException {
        if( viewer==null )
            return null;
        return ((FeatureTypeBuilder) viewer.getInput()).getFeatureType();
    }

    /**
     * Returns the FeatureTypeBuilder that is used for editing the feature type.
     *
     * @return the FeatureTypeBuilder that is used for editing the feature type.
     */
    public FeatureTypeBuilder getFeatureTypeBuilder() {
        if( viewer==null )
            return null;
        return (FeatureTypeBuilder) viewer.getInput();
    }

    /**
     * Returns the control that is the FeatureTypeEditor
     *
     * @return the control that is the FeatureTypeEditor
     */
    public Control getControl() {
        return viewer.getControl();
    }

    /**
     * Label provider for labelling AttributeTypes
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
            AttributeType attribute = (AttributeType) element;
            switch( columnIndex ) {
            case 0: // Attribute Name element
                return attribute.getName();
            case 1: // Attribute Type element
                return attribute.getType().getSimpleName();
            case 2: // Attribute Type element
                if (attribute instanceof GeometricAttributeType) {
                    CoordinateReferenceSystem coordinateSystem = ((GeometricAttributeType)attribute).getCoordinateSystem();
                    if(coordinateSystem!=null){
                        return coordinateSystem.getName().toString();
                    }else {
                        return "Unspecified";
                    }
                }

            default:
                break;
            }
            return null;
        }

    }

    /**
     * A Tree Content Provider that serves up attributeTypes from a FeatureType as a parent.
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
            if (parentElement instanceof FeatureTypeBuilder) {
                FeatureTypeBuilder builder = (FeatureTypeBuilder) parentElement;
                Object[] attributes = new Object[builder.getAttributeCount()];
                for( int i = 0; i < attributes.length; i++ ) {
                    attributes[i] = builder.get(i);
                }
                return attributes;
            }
            return null;
        }

        public Object getParent( Object element ) {
            if (element instanceof AttributeType) {
                return viewer.getInput();
            }
            return null;
        }

        public boolean hasChildren( Object element ) {
            if (element instanceof FeatureTypeBuilder)
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
            if (String.valueOf(OTHER_COLUMN).equals(property) && !(element instanceof GeometricAttributeType))
                return false;
            return true;
        }

        public Object getValue( Object element, String property ) {
            AttributeType editElement = (AttributeType) element;
            switch( Integer.parseInt(property) ) {
            case NAME_COLUMN:
                return editElement.getName();

            case TYPE_COLUMN:
                for( int i = 0; i < legalTypes.size(); i++ ) {
                    if (legalTypes.get(i).getType() == editElement.getType())
                        return i;
                }
                return -1;
            case OTHER_COLUMN:
                return ((GeometricAttributeType)element).getCoordinateSystem();
            }

            return null;
        }

        public void modify( Object element, String property, Object value ) {
            if( element==null || property==null || value==null ){
                return;
            }
            AttributeType editElement = (AttributeType) ((TreeItem) element).getData();
            FeatureTypeBuilder builder = (FeatureTypeBuilder) viewer.getInput();
            AttributeType newAttr = createNewAttributeType(editElement, property, value);
            if (newAttr == null)
                return;
            int index = 0;
            for( ; index < builder.getAttributeCount(); index++ ) {
                if (builder.get(index) == editElement)
                    break;
            }
            if (index == builder.getAttributeCount())
                return;
            builder.removeType(index);
            builder.addType(index, newAttr);
            viewer.refresh(true);
        }

        private AttributeType createNewAttributeType( AttributeType editElement, String property,
                Object value ) {
            switch( Integer.parseInt(property) ) {
            case NAME_COLUMN:
                if (editElement instanceof GeometricAttributeType) {
                    return AttributeTypeFactory.newAttributeType((String) value, editElement
                            .getType(), editElement.isNillable(), editElement.getRestriction(),
                            editElement.createDefaultValue(),
                            ((GeometricAttributeType) editElement).getCoordinateSystem());
                } else {
                    return AttributeTypeFactory.newAttributeType((String) value, editElement
                            .getType(), editElement.isNillable(), editElement.getRestriction(),
                            editElement.createDefaultValue(), null);
                }
            case TYPE_COLUMN:
                int choice = (Integer) value;

                if (choice == -1)
                    return null;
                else {
                    Class type = legalTypes.get(choice).getType();
                    Object metadata=null;
                    if( Geometry.class.isAssignableFrom(type))
                        metadata=lastCRS;
                    return AttributeTypeFactory.newAttributeType(editElement.getName(), type, editElement.isNillable(), editElement.getRestriction(), editElement.createDefaultValue(), metadata);
                }
            case OTHER_COLUMN:
                lastCRS=value;

                setDefaultCRS((CoordinateReferenceSystem) value);

                if (editElement instanceof GeometricAttributeType) {
                    return AttributeTypeFactory.newAttributeType(editElement.getName(), editElement
                            .getType(), editElement.isNillable(), editElement.getRestriction(),
                            editElement.createDefaultValue(),
                            value);
                } else {
                    return AttributeTypeFactory.newAttributeType((String) value, editElement
                            .getType(), editElement.isNillable(), editElement.getRestriction(),
                            editElement.createDefaultValue(), null);
                }
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
            if( viewer.getInput()!=null)
                nameText.setText(((FeatureTypeBuilder) viewer.getInput()).getName());
        }
    }

}
