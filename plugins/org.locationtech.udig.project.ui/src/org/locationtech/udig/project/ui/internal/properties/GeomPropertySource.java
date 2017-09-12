/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geotools.factory.Hints;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.EditCommand;
import org.locationtech.udig.project.command.factory.EditCommandFactory;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.ui.BasicTypeCellEditor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.util.GeometryEditor;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * TODO provide type description
 * 
 * @author jeichar
 */
public class GeomPropertySource implements IPropertySource2 {
    private static final String TYPE = "TYPE"; //$NON-NLS-1$
    private static final String AREA = "AREA"; //$NON-NLS-1$
    private static final String LENGTH = "LENGTH"; //$NON-NLS-1$
    private static final String GEOM = "GEOM"; //$NON-NLS-1$

    private Geometry geom;
    private SimpleFeature feature;
    private final Geometry original;
    private final IPropertyDescriptor[] propertyDescriptors;
    

    /**
     *  Creates a new instance of GeomPropertySource
     *      
     * @param geometry
     */
    public GeomPropertySource( Geometry geometry ) {
        this(geometry, null);
    }
    
    /**
     * Creates a new instance of GeomPropertySource
     * 
     * @param geometry the geometry that is the source for this PropertySource
     */
    public GeomPropertySource( Geometry geometry, SimpleFeature feature2 ) {
        this.geom = geometry;
        this.feature = feature2;
        this.original = (Geometry) geometry.clone();
        propertyDescriptors = new IPropertyDescriptor[3];
        propertyDescriptors[0] = new PropertyDescriptor(new ID(AREA), 
                Messages.GeomPropertySource_area); 
        propertyDescriptors[1] = new PropertyDescriptor(new ID(LENGTH), 
                Messages.GeomPropertySource_length); 
        propertyDescriptors[2] = new TextPropertyDescriptor(new ID(GEOM), 
                Messages.GeomPropertySource_WKT) {

            @Override
            public CellEditor createPropertyEditor(Composite parent) {
                CellEditor editor = new TextCellEditor(parent) {

                    @Override
                    protected void keyReleaseOccured(KeyEvent keyEvent) {
                        if (keyEvent.stateMask == SWT.CTRL && keyEvent.keyCode == SWT.ALT) {
                            new Dialog(Display.getCurrent().getActiveShell()) {

                                private static final int ADD_COORD_ID =  IDialogConstants.CLIENT_ID+1;
                                private static final int REMOVE_COORD_ID =  IDialogConstants.CLIENT_ID+2;
                                private TableViewer coordinateViewer;
                                private Point lastMenuLocation;
                                private boolean passedZeroIndex=false;

                                private boolean coordAdded;
                                private boolean coordRemoved;
                                private boolean coordChanged;

                                /* (non-Javadoc)
                                 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
                                 */
                                 @Override
                                 protected Control createDialogArea(
                                         Composite parent) {
                                     Composite container = (Composite) super.createDialogArea(parent);
                                     container.setLayout(new GridLayout(1, false));

                                     Label label = new Label(container, SWT.NONE);
                                     label.setText(geom.getGeometryType() + " vertex(es)" + (feature != null ? " (FID=" + feature.getIdentifier() + ")" : ""));              

                                     coordinateViewer = new TableViewer(container, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER);

                                     TableViewerColumn indexCol = new TableViewerColumn(coordinateViewer, SWT.NONE);
                                     indexCol.getColumn().setWidth(120);
                                     indexCol.getColumn().setText("index");
                                     indexCol.setLabelProvider(new ColumnLabelProvider() {
                                         @Override
                                         public String getText(Object element) {
                                             Coordinate coord = (Coordinate) element;
                                             TableItem[] items = coordinateViewer.getTable().getItems();
                                             for (int i = 0; i <items.length; i++) {
                                                 if (element.equals(items[i].getData())) {
                                                     if (i==0 && !passedZeroIndex) {
                                                         passedZeroIndex = true;
                                                     } else if (i==0 && passedZeroIndex) {
                                                         passedZeroIndex=false;
                                                         continue;
                                                     }
                                                     return String.valueOf(i);
                                                 }
                                             }
                                             return null;
                                         }                                       
                                     });

                                     TableViewerColumn xCol = new TableViewerColumn(coordinateViewer, SWT.NONE);
                                     xCol.getColumn().setWidth(120);
                                     xCol.getColumn().setText("X");
                                     xCol.setLabelProvider(new ColumnLabelProvider() {
                                         @Override
                                         public String getText(Object element) {
                                             Coordinate coord = (Coordinate) element;
                                             return String.valueOf(coord.x);
                                         }
                                     });
                                     xCol.setEditingSupport(new EditingSupport(coordinateViewer) {

                                         @Override
                                         protected CellEditor getCellEditor(
                                                 Object element) {
                                             return new BasicTypeCellEditor(coordinateViewer.getTable(), Double.class);
                                         }

                                         @Override
                                         protected boolean canEdit(
                                                 Object element) {
                                             return true;
                                         }

                                         @Override
                                         protected Object getValue(
                                                 Object element) {
                                             return ((Coordinate)element).x;
                                         }

                                         @Override
                                         protected void setValue(
                                                 Object element,
                                                 Object value) {
                                             if (((Coordinate)element).x != (Double)value) {
                                                 ((Coordinate)element).x = (Double) value;
                                                 coordChanged = true;
                                                 coordinateViewer.update(element, null);
                                             }

                                         }

                                     }); 


                                     TableViewerColumn yCol = new TableViewerColumn(coordinateViewer, SWT.NONE);
                                     yCol.getColumn().setWidth(120);
                                     yCol.getColumn().setText("Y");
                                     yCol.setLabelProvider(new ColumnLabelProvider() {
                                         @Override
                                         public String getText(Object element) {
                                             Coordinate coord = (Coordinate) element;
                                             return String.valueOf(coord.y);
                                         }
                                     });
                                     yCol.setEditingSupport(new EditingSupport(coordinateViewer) {

                                         @Override
                                         protected CellEditor getCellEditor(
                                                 Object element) {
                                             return new BasicTypeCellEditor(coordinateViewer.getTable(), Double.class);
                                         }

                                         @Override
                                         protected boolean canEdit(
                                                 Object element) {
                                             return true;
                                         }

                                         @Override
                                         protected Object getValue(
                                                 Object element) {
                                             return ((Coordinate)element).y;
                                         }

                                         @Override
                                         protected void setValue(
                                                 Object element,
                                                 Object value) {
                                             if (((Coordinate)element).y != (Double)value) {
                                                 ((Coordinate)element).y = (Double) value;
                                                 coordChanged = true;
                                                 coordinateViewer.update(element, null);
                                             }                                                                                                       
                                         }

                                     }); 
                                     coordinateViewer.getTable().setHeaderVisible(true);
                                     coordinateViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                                     coordinateViewer.setContentProvider(ArrayContentProvider.getInstance());
                                     coordinateViewer.setInput(geom.getCoordinates());

                                     MenuManager menuManager = new MenuManager();
                                     Menu menu = menuManager.createContextMenu(coordinateViewer.getControl());
                                     coordinateViewer.getControl().setMenu(menu);
                                     menuManager.add(new Action("add coordinate") {

                                         /* (non-Javadoc)
                                          * @see org.eclipse.jface.action.Action#run()
                                          */
                                          @Override
                                          public void run() {
                                             //we cannot add coordinate for a POINT geometry
                                             if (Geometries.get(geom) == Geometries.POINT) {
                                                 return;
                                             }
                                             IStructuredSelection selection = (IStructuredSelection) coordinateViewer.getSelection();
                                             TableItem item = coordinateViewer.getTable().getItem(lastMenuLocation);
                                             int index = coordinateViewer.getTable().indexOf(item);

                                             List<Coordinate> coords = new ArrayList<Coordinate>(Arrays.asList((Coordinate[]) coordinateViewer.getInput()));
                                             coords.add(index, new Coordinate(0, 0));
                                             coordAdded = true;
                                             coordinateViewer.setInput(coords.toArray(new Coordinate[0]));
                                             coordinateViewer.refresh();     

                                          }

                                     });

                                     menuManager.add(new Action("remove coordinate") {

                                         /* (non-Javadoc)
                                          * @see org.eclipse.jface.action.Action#run()
                                          */
                                         @Override
                                         public void run() {
                                             if (!addCoordAllowed(geom)) {
                                                 return;
                                             }

                                             IStructuredSelection selection = (IStructuredSelection) coordinateViewer.getSelection();
                                             TableItem item = coordinateViewer.getTable().getItem(lastMenuLocation);
                                             int index = coordinateViewer.getTable().indexOf(item);

                                             List<Coordinate> coords = new ArrayList<Coordinate>(Arrays.asList((Coordinate[]) coordinateViewer.getInput()));
                                             coords.remove(index);
                                             coordRemoved = true;
                                             coordinateViewer.setInput(coords.toArray(new Coordinate[0]));
                                             coordinateViewer.refresh();                                                                                                     
                                         }

                                     });

                                     //add a menu detect listener to get the Point where the menu popup
                                     coordinateViewer.getTable().addMenuDetectListener(new MenuDetectListener() {                                                                                        
                                         @Override
                                         public void menuDetected(MenuDetectEvent e) {
                                             lastMenuLocation = coordinateViewer.getTable().toControl(e.x, e.y);
                                         }
                                     });

                                     return container;       
                                 }


                                 @Override
                                 protected int getShellStyle() {
                                     return SWT.RESIZE|SWT.DIALOG_TRIM|SWT.CLOSE;
                                 }


                                 /* (non-Javadoc)
                                  * @see org.eclipse.jface.dialogs.Dialog#okPressed()
                                  */
                                 @Override
                                 protected void okPressed() {

                                     //if coordinate has been added or removed then edit geometry using appropriate CoordinateOperation
                                     if (coordAdded || coordRemoved) {                                                       
                                         Hints hints = new Hints( Hints.CRS, ApplicationGIS.getActiveMap().getViewportModel().getCRS());
                                         GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(hints);
                                         GeometryEditor editor = new GeometryEditor(geometryFactory);
                                         GeomAdaptCoordinateOperation geomOperation = new GeomAdaptCoordinateOperation((Coordinate[]) coordinateViewer.getInput());
                                         geom = editor.edit(geom, geomOperation);
                                     }

                                     if (!geom.isValid()) {
                                         Display.getCurrent().asyncExec(new Runnable(){
                                             public void run() {
                                                 MessageDialog.openWarning(Display.getCurrent().getActiveShell(), null, "invalid geometry detected");
                                                 resetPropertyValue(new ID(GEOM));
                                             }
                                         });     
                                     }

                                     if (!geom.equalsTopo(original)) {
                                         setPropertyValue(new ID(GEOM), geomToText());
                                         //if feature info is available use set Geometry with feature and layer info.
                                         ILayer layer = null;
                                         if( feature instanceof IAdaptable ){
                                             IAdaptable adaptable = (IAdaptable)feature;
                                             layer = (ILayer) adaptable.getAdapter(ILayer.class); 
                                         }
                                         EditCommand command = null;
                                         if (layer != null) {                
                                             command = (EditCommand) EditCommandFactory.getInstance().createSetGeomteryCommand(feature, layer, (Geometry) geom);
                                             layer.getMap().sendCommandASync(command);
                                         } else {
                                             command = (EditCommand) EditCommandFactory.getInstance().createSetGeometryCommand((Geometry) geom);
                                             ApplicationGIS.getActiveMap().sendCommandASync(command);
                                         }


                                     }
                                     super.okPressed();
                                 }


                                 /**
                                  * checks whether add of coordination is allwed based on Geom type
                                  */
                                 private boolean  addCoordAllowed(Geometry geom) {
                                     switch (Geometries.get(geom)) {
                                     case POINT:
                                         return false;
                                     case LINESTRING:
                                     case MULTILINESTRING:
                                         if (coordinateViewer.getTable().getItems().length < 3) {
                                             return false;
                                         }
                                         break;
                                     case POLYGON:
                                     case MULTIPOLYGON:
                                         if (coordinateViewer.getTable().getItems().length < 4) {
                                             return false;
                                         }
                                     default:
                                         break;
                                     }
                                     return true;
                                 }


                            }.open();


                        } else {
                            super.keyReleaseOccured(keyEvent);
                        }
                    }

                };
                if (getValidator() != null) {
                    editor.setValidator(getValidator());
                }
                return editor;
            }

        };
    }


    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    public Object getEditableValue() {
        return geom;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {

        IPropertyDescriptor[] c=new IPropertyDescriptor[propertyDescriptors.length];
        System.arraycopy(propertyDescriptors, 0, c, 0, c.length);
        return c;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    public Object getPropertyValue( Object idObject ) {
        ID id = (ID) idObject;
        if (id.id == TYPE)
            return geom.getGeometryType();
        if (id.id == AREA)
            return String.valueOf(geom.getArea());
        if (id.id == LENGTH)
            return String.valueOf(geom.getLength());
        if (id.id == GEOM) {
            return geomToText();
        }
        return null;
    }


    private Object geomToText() {
        WKTWriter writer = new WKTWriter();
        String text = writer.write(geom);
        text = text.replaceAll("[\\n\\r\\t]", " ");
        return text;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     */
    public boolean isPropertySet( Object id ) {
        return false;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    public void resetPropertyValue( Object id ) {
        geom = original;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object,
     *      java.lang.Object)
     */
    public void setPropertyValue( Object idObject, Object value ) {
        ID id = (ID) idObject;
        if (id.id == GEOM) {
            WKTReader reader = new WKTReader();
            try {
                geom = reader.read((String) value);
            } catch (ParseException e) {
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
        
    }

    static class ID {
        String id;
        ID( String id ) {
            this.id = id;
        }
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertyResettable(java.lang.Object)
     */
    public boolean isPropertyResettable( Object id ) {
        return true;
    }
    
    

    /**
     * 
     * @author Nikolaos Pringouris <nprigour@gmail.com>
     *
     */
    public class GeomAdaptCoordinateOperation extends GeometryEditor.CoordinateOperation {

        private boolean changed;
        private Coordinate[] modifiedCoordinates;

        /**
         * 
         * @param geom
         * @param tolerance
         */
        public GeomAdaptCoordinateOperation(Coordinate[] modifiedCoordinates) {
            super();
            this.changed = false;
            this.modifiedCoordinates = modifiedCoordinates;

        }

        /**
         * @see com.vividsolutions.jts.geom.util.GeometryEditor.CoordinateOperation#edit(com.vividsolutions.jts.geom.Coordinate[], com.vividsolutions.jts.geom.Geometry)
         */
        @Override
        public Coordinate[] edit(Coordinate[] coordinates, Geometry geom) {
            changed = false;

            switch (Geometries.get(geom)) {
            case POINT:
                if (!coordinates[0].equals(modifiedCoordinates[0])) {
                    changed = true;
                }
                break;
            case MULTIPOINT:
                if (coordinates.length != modifiedCoordinates.length) {
                    changed = true;
                } else {
                    for (int i = 0; i < coordinates.length; i ++) {
                        if (!coordinates[i].equals(modifiedCoordinates[i])) {
                            changed = true;
                            break;
                        }
                    }
                }
                break;
            case LINESTRING:
            case MULTILINESTRING:
            case POLYGON:
            case MULTIPOLYGON:
                if (coordinates.length != modifiedCoordinates.length) {
                    changed = true;
                } else {
                    for (int i = 0; i < coordinates.length; i ++) {
                        if (!coordinates[i].equals(modifiedCoordinates[i])) {
                            changed = true;
                            break;
                        }
                    }
                }
                break;
            default:

            }
            return changed ? modifiedCoordinates : coordinates;
        }


        /**
         * 
         * @return
         */
        public boolean isChanged() {
            return changed;
        }
    }
}
