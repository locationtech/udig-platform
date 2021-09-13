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
import org.locationtech.udig.feature.panel.FeaturePanelWidgetFactory;
import org.locationtech.udig.project.ui.IFeatureSite;

import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;

/**
 * Country Feature Panel used to show the use of AttributeFields.
 *
 * @since 1.2.0
 */
public class CountryFeaturePanel2 extends FeaturePanel {
    private static final String CURR_TYPE = "CURR_TYPE";

    /** Attribute name for attribute GMI_CNTRY */
    public static final String GMI_CNTRY = "GMI_CNTRY";

    /** Attribute name for attribute REGION */
    public static final String COLOR_MAP = "COLOR_MAP";

    /** Attribute name for attribute NAME */
    public static final String CNTRY_NAME = "CNTRY_NAME";

    public static final Object[] COLOR_MAP_OPTS = new Object[]{"1", "2", "3", "4", "5", "6", "7",
            "8"};

    private StringAttributeField currType;

    private StringAttributeField currCode;

    /**
     * Step 0 - Default constructor.
     */
    public CountryFeaturePanel2() {
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
        addField(new StringAttributeField("LONG_NAME", "Name", getParent()));
        addField(new IntegerAttributeField("POP_CNTRY", "Population", getParent()));
        addField(new StringAttributeField(CNTRY_NAME, "Country", getParent()));

        // one way to do things!
        currCode = addField(new StringAttributeField("CURR_CODE", "Currancy", getParent()));

        // another way to do things
        currType = new StringAttributeField(CURR_TYPE, "Currancy Type", getParent());
        addField(currType);

        addField(new StringAttributeField("FIPS_CNTRY", "FIPS", getParent()));
        addField(new StringAttributeField(GMI_CNTRY, "Code", getParent()));
        addField(new StringAttributeField("ISO_3DIGIT", "ISO 3", getParent()));
        addField(new StringAttributeField("ISO_2DIGIT", "ISO 2", getParent()));

        // combo box!
        addField(new ComboAttributeField2("LANDLOCKED", "Landbound", Arrays.asList(new Object[]{
                "Y", "N"}), getParent()));
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
        return "Country Details";
    }

}
