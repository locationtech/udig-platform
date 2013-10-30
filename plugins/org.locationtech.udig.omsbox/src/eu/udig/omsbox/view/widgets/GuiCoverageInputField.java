/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.omsbox.view.widgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.coverage.grid.GridCoverage2D;

import eu.udig.omsbox.core.FieldData;
import eu.udig.omsbox.core.ModuleDescription;
import eu.udig.omsbox.core.OmsModulesManager;

/**
 * Class representing an coverage input selector gui.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GuiCoverageInputField extends ModuleGuiElement {

    private final String constraints;

    private FieldData data;

    private static ModuleDescription selectedRasterReader;

    public GuiCoverageInputField( FieldData data, String constraints ) {
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
                List<ModuleDescription> rasterReaders = OmsModulesManager.getInstance().getRasterReaders();
                MultipleModuleDescriptionDialog dialog = new MultipleModuleDescriptionDialog("Choose Input Raster Reader",
                        rasterReaders);
                if (selectedRasterReader != null) {
                    dialog.setLastUsedModuleDescription(selectedRasterReader);
                }
                dialog.open(browseButton.getShell());
                selectedRasterReader = dialog.getModuleDescription();

                if (selectedRasterReader == null) {
                    data.otherModule = null;
                } else {
                    // find the field, assuming that IO modules can have only one connecting type.
                    List<FieldData> outputList = selectedRasterReader.getOutputsList();
                    for( FieldData fieldData : outputList ) {
                        if (fieldData.fieldType.equals(GridCoverage2D.class.getCanonicalName())) {
                            data.otherFieldName = fieldData.fieldName;
                            data.otherModule = selectedRasterReader;
                            break;
                        }
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
