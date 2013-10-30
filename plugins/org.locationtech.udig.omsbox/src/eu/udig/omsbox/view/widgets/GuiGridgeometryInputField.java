/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.view.widgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.coverage.grid.GridGeometry2D;

import org.locationtech.udig.omsbox.core.FieldData;
import org.locationtech.udig.omsbox.core.ModuleDescription;
import org.locationtech.udig.omsbox.core.OmsModulesManager;

/**
 * Class representing an gridgeometry input selector gui.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GuiGridgeometryInputField extends ModuleGuiElement {

    private final String constraints;

    private FieldData data;

    private static ModuleDescription selectedGridgeometryReader;

    public GuiGridgeometryInputField( FieldData data, String constraints ) {
        this.data = data;
        this.constraints = constraints;
    }

    @Override
    public Control makeGui( Composite parent ) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(constraints);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);

        final Button browseButton = new Button(composite, SWT.PUSH);
        browseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        browseButton.setText("...");
        browseButton.addSelectionListener(new SelectionAdapter(){

            public void widgetSelected( SelectionEvent e ) {
                List<ModuleDescription> gridReaders = OmsModulesManager.getInstance().getGridReaders();
                MultipleModuleDescriptionDialog dialog = new MultipleModuleDescriptionDialog("Choose Input Gridgeometry Reader",
                        gridReaders);
                if (selectedGridgeometryReader != null) {
                    dialog.setLastUsedModuleDescription(selectedGridgeometryReader);
                }
                dialog.open(browseButton.getShell());
                selectedGridgeometryReader = dialog.getModuleDescription();

                // find the field, assuming that IO modules can have only one connecting type.
                List<FieldData> outputList = selectedGridgeometryReader.getOutputsList();
                for( FieldData fieldData : outputList ) {
                    if (fieldData.fieldType.equals(GridGeometry2D.class.getCanonicalName())) {
                        data.otherFieldName = fieldData.fieldName;
                        data.otherModule = selectedGridgeometryReader;
                        break;
                    }
                }
            }
        });
        return null;
    }

    public FieldData getFieldData() {
        return data;
    }

    public boolean hasData() {
        return true;
    }

    @Override
    public String validateContent() {
        return null;
    }

}
