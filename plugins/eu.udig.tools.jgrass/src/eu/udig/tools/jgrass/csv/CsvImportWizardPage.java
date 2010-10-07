/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.tools.jgrass.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.geotools.gce.grassraster.JGrassConstants;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.udig.catalog.jgrass.core.ChooseCoordinateReferenceSystemDialog;
import eu.udig.tools.jgrass.i18n.Messages;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class CsvImportWizardPage extends WizardPage {

    public static final String ID = "CsvImportWizardPage"; //$NON-NLS-1$

    private String SEPARATOR = ",";
    private CoordinateReferenceSystem readCrs;
    private File csvFile = null;
    private List<Object[]> tableValues = new ArrayList<Object[]>();
    private TableViewer tableViewer;
    private boolean is3d = false;

    public CsvImportWizardPage( String pageName, Map<String, String> params ) {
        super(ID);
        setTitle(pageName);
        setDescription(Messages.getString("CsvImportWizardPage.importasshape")); //$NON-NLS-1$
    }

    public void createControl( Composite parent ) {
        Composite fileSelectionArea = new Composite(parent, SWT.NONE);
        fileSelectionArea.setLayout(new GridLayout());

        Group inputGroup = new Group(fileSelectionArea, SWT.None);
        inputGroup.setText("Choose the CSV file");
        inputGroup.setLayout(new GridLayout(2, false));
        inputGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData1.horizontalSpan = 2;

        final Text csvText = new Text(inputGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        csvText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        csvText.setText("");
        final Button csvButton = new Button(inputGroup, SWT.PUSH);
        csvButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        csvButton.setText("...");
        csvButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FileDialog fileDialog = new FileDialog(csvButton.getShell(), SWT.OPEN);
                String path = fileDialog.open();
                if (path != null) {
                    File f = new File(path);
                    if (f.exists()) {
                        csvText.setText(path);
                        csvFile = f;
                        try {
                            fillTableView();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                checkFinish();
            }
        });

        // the crs choice group
        Group crsGroup = new Group(fileSelectionArea, SWT.None);
        crsGroup.setLayout(new GridLayout(2, false));
        crsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        crsGroup.setText("Coordinate reference system for the data");

        final Text crsText = new Text(crsGroup, SWT.BORDER);
        crsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        crsText.setEditable(false);

        final Button crsButton = new Button(crsGroup, SWT.BORDER);
        crsButton.setText(" Choose CRS ");
        crsButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                final ChooseCoordinateReferenceSystemDialog crsChooser = new ChooseCoordinateReferenceSystemDialog();
                crsChooser.open(new Shell(Display.getDefault()));
                CoordinateReferenceSystem crs = crsChooser.getCrs();
                if (crs == null)
                    return;
                crsText.setText(crs.getName().toString());
                readCrs = crs;
                checkFinish();
            }
        });

        Group separatorGroup = new Group(fileSelectionArea, SWT.None);
        separatorGroup.setLayout(new GridLayout(2, false));
        separatorGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        separatorGroup.setText("The CSV separator string");

        final Text separatorText = new Text(separatorGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        separatorText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        separatorText.setText(SEPARATOR);
        separatorText.addKeyListener(new KeyAdapter(){
            public void keyReleased( KeyEvent e ) {
                String sep = separatorText.getText();
                if (sep.length() > 0) {
                    SEPARATOR = sep;
                    try {
                        fillTableView();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    checkFinish();
                }
            }
        });

        createTableArea(fileSelectionArea);

        checkFinish();
        setControl(fileSelectionArea);
    }

    private void createTableArea( Composite fileSelectionArea ) {

        GridData gridData1 = new GridData();
        gridData1.horizontalSpan = 2;
        gridData1.horizontalAlignment = GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = true;
        gridData1.verticalAlignment = GridData.FILL;
        Composite comp = new Composite(fileSelectionArea, SWT.NONE);
        comp.setLayout(new FillLayout());
        comp.setLayoutData(gridData1);
        // table
        tableViewer = new TableViewer(comp, SWT.BORDER | SWT.V_SCROLL);
        final Table table = tableViewer.getTable();
        // table.setLayoutData(gridData1);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        final String nameString = "Fieldname";
        final String valueString = "Example value";
        final String typeString = "Type";
        // columns
        TableColumn name = new TableColumn(table, SWT.NONE);
        name.setText(nameString);
        TableColumn value = new TableColumn(table, SWT.NONE);
        value.setText(valueString);
        TableColumn type = new TableColumn(table, SWT.NONE);
        type.setText(typeString);
        // layout
        TableColumnLayout layout = new TableColumnLayout();
        layout.setColumnData(name, new ColumnWeightData(40, true));
        layout.setColumnData(value, new ColumnWeightData(40, true));
        layout.setColumnData(type, new ColumnWeightData(20, true));
        comp.setLayout(layout);

        // activate editing
        tableViewer.setColumnProperties(new String[]{nameString, valueString, typeString});
        tableViewer.setCellModifier(new ICellModifier(){

            public boolean canModify( Object element, String property ) {
                if (property.equals(valueString)) {
                    return false;
                }
                return true;
            }

            public Object getValue( Object element, String property ) {
                Object[] e = (Object[]) element;
                if (property.equals(nameString)) {
                    return e[0];
                }
                if (property.equals(typeString)) {
                    return e[2];
                }
                return "";
            }

            public void modify( Object element, String property, Object value ) {
                TableItem tabItem = (TableItem) element;
                Object[] data = (Object[]) tabItem.getData();
                if (property.equals(nameString)) {
                    data[0] = value;
                }
                if (property.equals(typeString)) {
                    data[2] = value;
                }
                tableViewer.refresh(data);
                checkFinish();
            }

        });
        tableViewer.setCellEditors(new CellEditor[]{new TextCellEditor(table), new TextCellEditor(table),
                new ComboBoxCellEditor(table, JGrassConstants.CSVTYPESARRAY)});

        // the label provider
        tableViewer.setLabelProvider(new ITableLabelProvider(){

            public Image getColumnImage( Object element, int columnIndex ) {
                return null;
            }

            public String getColumnText( Object element, int columnIndex ) {
                Object[] e = (Object[]) element;
                switch( columnIndex ) {
                case 0:
                    return (String) e[0];
                case 1:
                    return (String) e[1];
                case 2:
                    return JGrassConstants.CSVTYPESARRAY[(Integer) e[2]];
                default:
                    break;
                }
                return "";
            }

            public void addListener( ILabelProviderListener listener ) {
            }

            public void dispose() {
            }

            public boolean isLabelProperty( Object element, String property ) {
                return false;
            }

            public void removeListener( ILabelProviderListener listener ) {
            }

        });
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(tableValues);

    }

    private void fillTableView() throws IOException {
        if (SEPARATOR.length() > 0 && csvFile != null && csvFile.exists()) {
            // read the first line and guess
            BufferedReader bR = new BufferedReader(new FileReader(csvFile));
            String line = null;
            String[] lineSplit = null;
            while( (line = bR.readLine()) != null ) {
                if (line.trim().length() == 0) {
                    continue;
                }
                lineSplit = line.trim().split(SEPARATOR);
                break;
            }
            bR.close();
            if (lineSplit != null) {
                tableValues.clear();

                for( int i = 0; i < lineSplit.length; i++ ) {
                    Object[] value = new Object[3];
                    value[0] = "Field" + i;
                    value[1] = lineSplit[i];

                    if (i == 0) {
                        try {
                            Double.parseDouble(lineSplit[i]);
                            value[2] = 0;
                        } catch (NumberFormatException e) {
                            value[2] = 3;
                        }
                    } else if (i == 1) {
                        try {
                            Double.parseDouble(lineSplit[i]);
                            value[2] = 1;
                        } catch (NumberFormatException e) {
                            value[2] = 3;
                        }
                    } else {
                        value[2] = 3;
                    }
                    tableValues.add(value);
                }
                tableViewer.setInput(tableValues);
            }
        }
    }

    public File getCsvFile() {
        return csvFile;
    }

    public String getSeparator() {
        return SEPARATOR;
    }

    public LinkedHashMap<String, Integer> getFieldsAndTypesIndex() {
        LinkedHashMap<String, Integer> fieldNamesToTypesIndex = new LinkedHashMap<String, Integer>();
        for( int i = 0; i < tableValues.size(); i++ ) {
            Object[] values = tableValues.get(i);
            fieldNamesToTypesIndex.put((String) values[0], (Integer) values[2]);
        }
        return fieldNamesToTypesIndex;
    }

    public boolean is3d() {
        return is3d;
    }

    public CoordinateReferenceSystem getCrs() {
        return readCrs;
    }

    private void checkFinish() {
        boolean hasX = false;
        boolean hasY = false;
        for( int i = 0; i < tableValues.size(); i++ ) {
            Object[] values = tableValues.get(i);
            Integer type = (Integer) values[2];
            if (type == 0) {
                hasX = true;
            }
            if (type == 1) {
                hasY = true;
            }
        }

        if (!hasX || !hasY || csvFile == null || !csvFile.exists() || readCrs == null || SEPARATOR.length() == 0) {
            CsvImportWizard.canFinish = false;
        } else {
            CsvImportWizard.canFinish = true;
        }

        getWizard().getContainer().updateButtons();
    }
}
