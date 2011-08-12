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
package eu.udig.style.advanced.polygons.widgets;

import static eu.udig.style.advanced.utils.Utilities.ff;

import java.awt.Color;

import net.refractions.udig.style.sld.SLD;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.geotools.styling.TextSymbolizer;
import org.opengis.filter.expression.Expression;

import eu.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import eu.udig.style.advanced.common.ParameterComposite;
import eu.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.TextSymbolizerWrapper;
import eu.udig.style.advanced.utils.FontEditor;
import eu.udig.style.advanced.utils.StolenColorEditor;
import eu.udig.style.advanced.utils.Utilities;
import eu.udig.style.advanced.utils.VendorOptions;

/**
 * A composite that holds widgets for labels parameter setting.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class PolygonLabelsParametersComposite extends ParameterComposite {

    private final Composite parent;
    private final String[] numericAttributesArrays;

    private Composite mainComposite;
    private Button labelEnableButton;
    private Spinner labelOpacitySpinner;
    private Combo labelOpacityAttributecombo;
    private Button haloColorButton;
    private StolenColorEditor haloColorEditor;
    private Spinner haloRadiusSpinner;
    private Spinner rotationSpinner;
    private Combo rotationAttributecombo;
    private Text maxDisplacementText;
    private Text autoWrapText;
    private Text spaceAroundText;
    private FontEditor fontEditor;
    private Button fontButton;
    private StolenColorEditor fontColorEditor;
    private Button fontColorButton;
    private String[] allAttributesArrays;
    private Text labelNameText;
    private Combo labelNameAttributecombo;

    public PolygonLabelsParametersComposite( Composite parent, String[] numericAttributesArrays, String[] allAttributesArrays ) {
        this.parent = parent;
        this.numericAttributesArrays = numericAttributesArrays;
        this.allAttributesArrays = allAttributesArrays;
    }

    public Composite getComposite() {
        return mainComposite;
    }

    /**
     * Initialize the composite with values from a rule.
     * 
     * @param ruleWrapper the rule to take the info from.
     */
    public void init( RuleWrapper ruleWrapper ) {
        TextSymbolizerWrapper textSymbolizerWrapper = ruleWrapper.getTextSymbolizersWrapper();
        boolean widgetEnabled = true;
        if (textSymbolizerWrapper == null) {
            widgetEnabled = false;
            /*
             * create a dummy local one to create the widgets
             */
            TextSymbolizer newSymbolizer = Utilities.createDefaultTextSymbolizer(SLD.POLYGON);
            textSymbolizerWrapper = new TextSymbolizerWrapper(newSymbolizer, null, SLD.POLYGON);
        }

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(3, true));

        labelEnableButton = new Button(mainComposite, SWT.CHECK);
        GridData labelEnableButtonGD = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        labelEnableButtonGD.horizontalSpan = 3;
        labelEnableButton.setLayoutData(labelEnableButtonGD);
        labelEnableButton.setText("enable/disable labelling");
        labelEnableButton.setSelection(widgetEnabled);
        labelEnableButton.addSelectionListener(this);

        // label name
        Label labelNameLabel = new Label(mainComposite, SWT.NONE);
        labelNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        labelNameLabel.setText("label");

        labelNameText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData labelNameTextGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelNameText.setLayoutData(labelNameTextGD);
        labelNameText.addFocusListener(this);
        labelNameAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData labelNameAttributecomboGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelNameAttributecombo.setLayoutData(labelNameAttributecomboGD);
        labelNameAttributecombo.setItems(allAttributesArrays);
        labelNameAttributecombo.addSelectionListener(this);
        labelNameAttributecombo.select(0);
        String labelName = textSymbolizerWrapper.getLabelName();
        if (labelName != null) {
            int index = getAttributeIndex(labelName, allAttributesArrays);
            if (index != -1) {
                labelNameAttributecombo.select(index);
            } else {
                labelNameText.setText(labelName);
            }
        } else {
            labelNameText.setText("");
        }

        // label alpha
        Label labelOpactityLabel = new Label(mainComposite, SWT.NONE);
        labelOpactityLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        labelOpactityLabel.setText("opacity");
        labelOpacitySpinner = new Spinner(mainComposite, SWT.BORDER);
        labelOpacitySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        labelOpacitySpinner.setMaximum(100);
        labelOpacitySpinner.setMinimum(0);
        labelOpacitySpinner.setIncrement(10);
        String opacity = textSymbolizerWrapper.getOpacity();
        Double tmpOpacity = isDouble(opacity);
        int tmp = 100;
        if (tmpOpacity != null) {
            tmp = (int) (tmpOpacity.doubleValue() * 100);
        }
        labelOpacitySpinner.setSelection(tmp);
        labelOpacitySpinner.addSelectionListener(this);
        labelOpacityAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        labelOpacityAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        labelOpacityAttributecombo.setItems(numericAttributesArrays);
        labelOpacityAttributecombo.addSelectionListener(this);
        labelOpacityAttributecombo.select(0);
        if (tmpOpacity == null) {
            int index = getAttributeIndex(opacity, numericAttributesArrays);
            if (index != -1) {
                labelOpacityAttributecombo.select(index);
            }
        }

        // rotation
        Label rotationLabel = new Label(mainComposite, SWT.NONE);
        rotationLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        rotationLabel.setText("rotation");
        rotationSpinner = new Spinner(mainComposite, SWT.BORDER);
        rotationSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        rotationSpinner.setMaximum(360);
        rotationSpinner.setMinimum(-360);
        rotationSpinner.setIncrement(45);
        String rotationStr = textSymbolizerWrapper.getRotation();
        Double tmpRotation = isDouble(rotationStr);
        tmp = 0;
        if (tmpRotation != null) {
            tmp = tmpRotation.intValue();
        }
        rotationSpinner.setSelection(tmp);
        rotationSpinner.addSelectionListener(this);
        rotationAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        rotationAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        rotationAttributecombo.setItems(numericAttributesArrays);
        rotationAttributecombo.addSelectionListener(this);
        rotationAttributecombo.select(0);
        if (tmpRotation == null) {
            int index = getAttributeIndex(rotationStr, numericAttributesArrays);
            if (index != -1) {
                rotationAttributecombo.select(index);
            }
        }

        // font
        Label fontLabel = new Label(mainComposite, SWT.NONE);
        fontLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fontLabel.setText("font");

        fontEditor = new FontEditor(mainComposite);
        GridData fontButtonGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        fontButtonGD.horizontalSpan = 2;
        fontButton = fontEditor.getButton();
        fontButton.setLayoutData(fontButtonGD);
        fontEditor.setListener(this);

        FontData[] fontData = textSymbolizerWrapper.getFontData();
        if (fontData != null) {
            fontEditor.setFontList(fontData);
        }

        // font color
        Label fontColorLabel = new Label(mainComposite, SWT.NONE);
        fontColorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fontColorLabel.setText("font color");

        fontColorEditor = new StolenColorEditor(mainComposite, this);
        fontColorButton = fontColorEditor.getButton();
        GridData fontColorButtonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        fontColorButtonGD.horizontalSpan = 2;
        fontColorButton.setLayoutData(fontColorButtonGD);
        Color tmpColor = null;;
        try {
            tmpColor = Color.decode(textSymbolizerWrapper.getColor());
        } catch (Exception e) {
            tmpColor = Color.black;
        }
        fontColorEditor.setColor(tmpColor);

        // label halo
        Label haloLabel = new Label(mainComposite, SWT.NONE);
        haloLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        haloLabel.setText("halo");

        haloColorEditor = new StolenColorEditor(mainComposite, this);
        haloColorButton = haloColorEditor.getButton();
        GridData haloColorButtonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        haloColorButton.setLayoutData(haloColorButtonGD);
        tmpColor = null;;
        try {
            tmpColor = Color.decode(textSymbolizerWrapper.getHaloColor());
        } catch (Exception e) {
            tmpColor = Color.black;
        }
        haloColorEditor.setColor(tmpColor);

        haloRadiusSpinner = new Spinner(mainComposite, SWT.BORDER);
        haloRadiusSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        haloRadiusSpinner.setMaximum(20);
        haloRadiusSpinner.setMinimum(0);
        haloRadiusSpinner.setIncrement(1);
        String haloRadius = textSymbolizerWrapper.getHaloRadius();
        Double tmpRadius = isDouble(haloRadius);
        tmp = 0;
        if (tmpRadius != null) {
            tmp = tmpRadius.intValue();
        }
        haloRadiusSpinner.setSelection(tmp);
        haloRadiusSpinner.addSelectionListener(this);

        // vendor options
        Group vendorOptionsGroup = new Group(mainComposite, SWT.SHADOW_ETCHED_IN);
        GridData vendorOptionsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        vendorOptionsGD.horizontalSpan = 3;
        vendorOptionsGroup.setLayoutData(vendorOptionsGD);
        vendorOptionsGroup.setLayout(new GridLayout(2, false));
        vendorOptionsGroup.setText("Vendor Options");

        // max displacement
        Label maxDisplacementLabel = new Label(vendorOptionsGroup, SWT.NONE);
        maxDisplacementLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxDisplacementLabel.setText(VendorOptions.VENDOROPTION_MAXDISPLACEMENT.toGuiString());
        maxDisplacementText = new Text(vendorOptionsGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData maxDisplacementTextGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        maxDisplacementText.setLayoutData(maxDisplacementTextGD);
        maxDisplacementText.addFocusListener(this);
        String maxDisplacementVO = textSymbolizerWrapper.getMaxDisplacementVO();
        if (maxDisplacementVO != null) {
            maxDisplacementText.setText(maxDisplacementVO);
        } else {
            maxDisplacementText.setText("");
        }

        // autoWrap
        Label autoWrapLabel = new Label(vendorOptionsGroup, SWT.NONE);
        autoWrapLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        autoWrapLabel.setText(VendorOptions.VENDOROPTION_AUTOWRAP.toGuiString());
        autoWrapText = new Text(vendorOptionsGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData autoWrapTextGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        autoWrapText.setLayoutData(autoWrapTextGD);
        autoWrapText.addFocusListener(this);
        String autoWrapVO = textSymbolizerWrapper.getAutoWrapVO();
        if (autoWrapVO != null) {
            autoWrapText.setText(autoWrapVO);
        } else {
            autoWrapText.setText("");
        }

        // spaceAround
        Label spaceAroundLabel = new Label(vendorOptionsGroup, SWT.NONE);
        spaceAroundLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        spaceAroundLabel.setText(VendorOptions.VENDOROPTION_SPACEAROUND.toGuiString());
        spaceAroundText = new Text(vendorOptionsGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData spaceAroundTextGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        spaceAroundText.setLayoutData(spaceAroundTextGD);
        spaceAroundText.addFocusListener(this);
        String spaceAroundVO = textSymbolizerWrapper.getSpaceAroundVO();
        if (spaceAroundVO != null) {
            spaceAroundText.setText(spaceAroundVO);
        } else {
            spaceAroundText.setText("");
        }

        checkEnablements();

    }

    /**
     * Initialize the composite with values from a rule.
     * 
     * @param ruleWrapper the rule to take the info from.
     */
    public void update( RuleWrapper ruleWrapper ) {
        TextSymbolizerWrapper textSymbolizerWrapper = ruleWrapper.getTextSymbolizersWrapper();
        if (textSymbolizerWrapper == null) {
            labelEnableButton.setSelection(false);
            return;
        } else {
            labelEnableButton.setSelection(true);
        }

        String labelName = textSymbolizerWrapper.getLabelName();
        if (labelName != null) {
            int index = getAttributeIndex(labelName, allAttributesArrays);
            if (index != -1) {
                labelNameAttributecombo.select(index);
            } else {
                labelNameText.setText(labelName);
            }
        } else {
            labelNameText.setText("");
        }

        FontData[] fontData = textSymbolizerWrapper.getFontData();
        if (fontData != null) {
            fontEditor.setFontList(fontData);
        }

        String color = textSymbolizerWrapper.getColor();
        if (color != null) {
            fontColorEditor.setColor(Color.decode(color));
        }

        String opacity = textSymbolizerWrapper.getOpacity();
        Double tmpOpacity = isDouble(opacity);
        int tmp = 100;
        if (tmpOpacity != null) {
            tmp = (int) (tmpOpacity.doubleValue() * 100);
        }
        labelOpacitySpinner.setSelection(tmp);
        if (tmpOpacity == null) {
            int index = getAttributeIndex(opacity, numericAttributesArrays);
            if (index != -1) {
                labelOpacityAttributecombo.select(index);
            }
        }

        Color tmpColor = null;;
        try {
            tmpColor = Color.decode(textSymbolizerWrapper.getHaloColor());
        } catch (Exception e) {
            tmpColor = Color.black;
        }
        haloColorEditor.setColor(tmpColor);

        String haloRadius = textSymbolizerWrapper.getHaloRadius();
        Double tmpRadius = isDouble(haloRadius);
        tmp = 0;
        if (tmpRadius != null) {
            tmp = tmpRadius.intValue();
        }
        haloRadiusSpinner.setSelection(tmp);

        // rotation
        String rotationStr = textSymbolizerWrapper.getRotation();
        Double tmpRotation = isDouble(rotationStr);
        tmp = 0;
        if (tmpRotation != null) {
            tmp = tmpRotation.intValue();
        }
        rotationSpinner.setSelection(tmp);
        if (tmpRotation == null) {
            int index = getAttributeIndex(rotationStr, numericAttributesArrays);
            if (index != -1) {
                rotationAttributecombo.select(index);
            }
        }

        // max displacement
        String maxDisplacementVO = textSymbolizerWrapper.getMaxDisplacementVO();
        if (maxDisplacementVO != null) {
            maxDisplacementText.setText(maxDisplacementVO);
        } else {
            maxDisplacementText.setText("");
        }

        // autoWrap
        String autoWrapVO = textSymbolizerWrapper.getAutoWrapVO();
        if (autoWrapVO != null) {
            autoWrapText.setText(autoWrapVO);
        } else {
            autoWrapText.setText("");
        }

        // spaceAround
        String spaceAroundVO = textSymbolizerWrapper.getSpaceAroundVO();
        if (spaceAroundVO != null) {
            spaceAroundText.setText(spaceAroundVO);
        } else {
            spaceAroundText.setText("");
        }

        checkEnablements();
    }

    private void checkEnablements() {
        boolean comboIsNone = comboIsNone(labelNameAttributecombo);
        boolean selected = labelEnableButton.getSelection();
        if (!selected) {
            setEnabled(false);
        } else {
            labelNameText.setEnabled(comboIsNone);
            comboIsNone = comboIsNone(labelOpacityAttributecombo);
            labelOpacitySpinner.setEnabled(comboIsNone);
            comboIsNone = comboIsNone(rotationAttributecombo);
            rotationSpinner.setEnabled(comboIsNone);
        }

    }

    private void setEnabled( boolean enable ) {
        labelOpacitySpinner.setEnabled(enable);
        labelOpacityAttributecombo.setEnabled(enable);
        haloColorButton.setEnabled(enable);
        haloColorEditor.setEnabled(enable);
        haloRadiusSpinner.setEnabled(enable);
        rotationSpinner.setEnabled(enable);
        rotationAttributecombo.setEnabled(enable);
        maxDisplacementText.setEnabled(enable);
        autoWrapText.setEnabled(enable);
        spaceAroundText.setEnabled(enable);
        fontEditor.setEnabled(enable);
        fontButton.setEnabled(enable);
        fontColorEditor.setEnabled(enable);
        fontColorButton.setEnabled(enable);
        labelNameText.setEnabled(enable);
        labelNameAttributecombo.setEnabled(enable);
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(labelEnableButton)) {
            boolean selected = labelEnableButton.getSelection();
            setEnabled(labelEnableButton.getSelection());
            notifyListeners(String.valueOf(selected), false, STYLEEVENTTYPE.LABELENABLE);
        } else if (source.equals(labelNameAttributecombo)) {
            boolean comboIsNone = comboIsNone(labelNameAttributecombo);
            if (comboIsNone) {
                String text = labelNameText.getText();
                notifyListeners(text, false, STYLEEVENTTYPE.LABEL);
            } else {
                int index = labelNameAttributecombo.getSelectionIndex();
                String nameField = labelNameAttributecombo.getItem(index);
                notifyListeners(nameField, true, STYLEEVENTTYPE.LABEL);
            }
        } else if (source.equals(fontButton)) {
            FontData[] fontData = fontEditor.getFontList();
            if (fontData.length > 0) {
                FontData fd = fontData[0];
                String name = fd.getName();
                String style = String.valueOf(fd.getStyle());
                String height = String.valueOf(fd.getHeight());
                Color color = fontEditor.getAWTColor();
                Expression colorExpr = ff.literal(color);
                String fontColor = colorExpr.evaluate(null, String.class);

                notifyListeners(new String[]{name, style, height, fontColor}, false, STYLEEVENTTYPE.LABELFONT);
            }
        } else if (source.equals(fontColorButton)) {
            Color color = fontColorEditor.getColor();
            Expression colorExpr = ff.literal(color);
            String fontColor = colorExpr.evaluate(null, String.class);

            notifyListeners(fontColor, false, STYLEEVENTTYPE.LABELCOLOR);
        } else if (source.equals(haloColorButton)) {
            Color color = haloColorEditor.getColor();
            Expression colorExpr = ff.literal(color);
            String haloColor = colorExpr.evaluate(null, String.class);

            notifyListeners(haloColor, false, STYLEEVENTTYPE.LABELHALOCOLOR);
        } else if (source.equals(haloRadiusSpinner)) {
            int radius = haloRadiusSpinner.getSelection();

            notifyListeners(String.valueOf(radius), false, STYLEEVENTTYPE.LABELHALORADIUS);
        } else if (source.equals(rotationSpinner) || source.equals(rotationAttributecombo)) {
            boolean comboIsNone = comboIsNone(rotationAttributecombo);
            if (comboIsNone) {
                int rotation = rotationSpinner.getSelection();
                String rotationStr = String.valueOf(rotation);

                notifyListeners(rotationStr, false, STYLEEVENTTYPE.LABELROTATION);
            } else {
                int index = rotationAttributecombo.getSelectionIndex();
                String rotationField = rotationAttributecombo.getItem(index);

                notifyListeners(rotationField, true, STYLEEVENTTYPE.LABELROTATION);
            }
        } else if (source.equals(labelOpacitySpinner) || source.equals(labelOpacityAttributecombo)) {
            boolean comboIsNone = comboIsNone(labelOpacityAttributecombo);
            if (comboIsNone) {
                int opacity = labelOpacitySpinner.getSelection();
                String opacityStr = String.valueOf(opacity);

                notifyListeners(opacityStr, false, STYLEEVENTTYPE.LABELOPACITY);
            } else {
                int index = labelOpacityAttributecombo.getSelectionIndex();
                String opacityField = labelOpacityAttributecombo.getItem(index);

                notifyListeners(opacityField, true, STYLEEVENTTYPE.LABELOPACITY);
            }
        }

        checkEnablements();
    }

    public void focusGained( FocusEvent e ) {
    }

    public void focusLost( FocusEvent e ) {
        Object source = e.getSource();
        if (source.equals(maxDisplacementText)) {
            String text = maxDisplacementText.getText();
            notifyListeners(text, false, STYLEEVENTTYPE.LABELMAXDISPLACEMENT_VO);
        } else if (source.equals(spaceAroundText)) {
            String text = spaceAroundText.getText();
            notifyListeners(text, false, STYLEEVENTTYPE.LABELSPACEAROUND_VO);
        } else if (source.equals(autoWrapText)) {
            String text = autoWrapText.getText();
            notifyListeners(text, false, STYLEEVENTTYPE.LABELAUTOWRAP_VO);
        } else if (source.equals(labelNameText)) {
            String text = labelNameText.getText();
            notifyListeners(text, false, STYLEEVENTTYPE.LABEL);
        }
        checkEnablements();
    }
}
