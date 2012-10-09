/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.tutorials.featureeditor;

import java.util.Arrays;

import net.refractions.udig.feature.editor.field.AttributeField;
import net.refractions.udig.feature.editor.field.ComboAttributeField2;
import net.refractions.udig.feature.editor.field.FeaturePanel;
import net.refractions.udig.feature.editor.field.IntegerAttributeField;
import net.refractions.udig.feature.editor.field.StringAttributeField;
import net.refractions.udig.feature.panel.FeaturePanelWidgetFactory;
import net.refractions.udig.project.ui.IFeatureSite;

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
