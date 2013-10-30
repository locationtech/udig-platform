/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.featureeditor;

import java.util.Arrays;

import org.locationtech.udig.feature.editor.field.AttributeField;
import org.locationtech.udig.feature.editor.field.ComboAttributeField2;
import org.locationtech.udig.feature.editor.field.FeaturePanel;
import org.locationtech.udig.feature.editor.field.IntegerAttributeField;
import org.locationtech.udig.feature.editor.field.StringAttributeField;
import org.locationtech.udig.project.ui.IFeatureSite;

import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PartInitException;

/**
 * Country Feature Panel used to show the use of AttributeFields.
 * 
 * @since 1.2.0
 */
public class CountryFeaturePanel3 extends FeaturePanel {
    private static final String CURR_TYPE = "CURR_TYPE";

    /** Attribute name for attribute GMI_CNTRY */
    public final static String GMI_CNTRY = "GMI_CNTRY";

    /** Attribute name for attribute REGION */
    public final static String COLOR_MAP = "COLOR_MAP";

    /** Attribute name for attribute NAME */
    public final static String CNTRY_NAME = "CNTRY_NAME";

    public final static Object[] COLOR_MAP_OPTS = new Object[]{"1", "2", "3", "4", "5", "6", "7",
            "8"};

    private StringAttributeField currType;

    private StringAttributeField currCode;

    /**
     * Step 0 - Default constructor.
     */
    public CountryFeaturePanel3() {
    }

    /**
     * Step 1 - init using the editor site and memento holding any information from last time
     */
    @Override
    public void init( IFeatureSite site, IMemento memento ) throws PartInitException {
        super.init(site, memento);
    }

    @Override
    public void createFieldEditors() {
        StringAttributeField field = addField(new StringAttributeField("SQKM", "Area (square km)",
                getParent()));
        field.getLabelControl(getParent()).setToolTipText("Area km");

        field = addField(new StringAttributeField("SQMI", "Square Miles:", getParent()));
        field.getLabelControl(getParent()).setToolTipText("Area miles");

        ComboAttributeField2 combo = addField(new ComboAttributeField2(COLOR_MAP, "Color Map",
                Arrays.asList(COLOR_MAP_OPTS), getParent()));
        ComboViewer viewer = combo.getViewer();
        viewer.setLabelProvider(new LabelProvider(){
            @Override
            public String getText( Object element ) {
                return "Color " + element;
            }
        });
    }

    @Override
    public void propertyChange( PropertyChangeEvent event ) {
        super.propertyChange(event);
        if (event.getProperty().equals(AttributeField.IS_VALID)) {
            // a field has told us it is valid/invalid
            //
            AttributeField field = (AttributeField) event.getSource();
            String name = field.getAttributeName();
            boolean isValid = (Boolean) event.getNewValue();
            
            if (CURR_TYPE.equals( name )) {
                // if the curr_type field is not valid; disable currency code
                currCode.setEnabled(isValid);
            }
        }
    }

    @Override
    public String getDescription() {
        return "Details on the selected country.";
    }

    @Override
    public String getName() {
        return "Country2";
    }

    @Override
    public String getTitle() {
        return "Country Statis";
    }

}
