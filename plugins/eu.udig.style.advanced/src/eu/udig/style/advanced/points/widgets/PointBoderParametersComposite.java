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
package eu.udig.style.advanced.points.widgets;

import static eu.udig.style.advanced.utils.Utilities.ff;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.opengis.filter.expression.Expression;

import eu.udig.style.advanced.common.ParameterComposite;
import eu.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import eu.udig.style.advanced.common.styleattributeclasses.PointSymbolizerWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import eu.udig.style.advanced.internal.Messages;
import eu.udig.style.advanced.utils.StolenColorEditor;

/**
 * A composite that holds widgets for border parameter setting.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class PointBoderParametersComposite extends ParameterComposite {

    private final Composite parent;
    private final String[] numericAttributesArrays;

    private Composite mainComposite;
    private Button borderEnableButton;
    private Spinner borderWidthSpinner;
    private Combo borderWidthAttributecombo;
    private StolenColorEditor borderColorEditor;
    private Spinner borderOpacitySpinner;
    private Combo borderOpacityAttributecombo;
    private Button borderColorButton;

    public PointBoderParametersComposite( Composite parent, String[] numericAttributesArrays ) {
        this.parent = parent;
        this.numericAttributesArrays = numericAttributesArrays;
    }

    @Override
    public Composite getComposite() {
        return mainComposite;
    }

    /**
     * Initialize the composite with values from a rule.
     * 
     * @param ruleWrapper the rule to take the info from.
     */
    public void init( RuleWrapper ruleWrapper ) {
        PointSymbolizerWrapper symbolizersWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(3, true));

        boolean widgetEnabled = symbolizersWrapper.hasStroke();

        borderEnableButton = new Button(mainComposite, SWT.CHECK);
        GridData borderEnableButtonGD = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        borderEnableButtonGD.horizontalSpan = 3;
        borderEnableButton.setLayoutData(borderEnableButtonGD);
        borderEnableButton.setText(Messages.PointBoderParametersComposite_0);
        borderEnableButton.setSelection(widgetEnabled);
        borderEnableButton.addSelectionListener(this);

        // header
        new Label(mainComposite, SWT.NONE);
        Label valueLabel = new Label(mainComposite, SWT.NONE);
        valueLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        valueLabel.setText(Messages.PointBoderParametersComposite_1);
        Label fieldsLabel = new Label(mainComposite, SWT.NONE);
        fieldsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        fieldsLabel.setText(Messages.PointBoderParametersComposite_2);

        // border width
        Label borderWidthLabel = new Label(mainComposite, SWT.NONE);
        borderWidthLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderWidthLabel.setText(Messages.PointBoderParametersComposite_3);
        borderWidthSpinner = new Spinner(mainComposite, SWT.BORDER);
        borderWidthSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderWidthSpinner.setMaximum(500);
        borderWidthSpinner.setMinimum(0);
        borderWidthSpinner.setIncrement(10);

        String width = symbolizersWrapper.getStrokeWidth();
        Double tmpWidth = isDouble(width);
        int tmp = 3;
        if (tmpWidth != null) {
            tmp = tmpWidth.intValue();
        }
        borderWidthSpinner.setSelection(tmp * 10);
        borderWidthSpinner.setDigits(1);
        borderWidthSpinner.addSelectionListener(this);
        borderWidthAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        borderWidthAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderWidthAttributecombo.setItems(numericAttributesArrays);
        borderWidthAttributecombo.addSelectionListener(this);
        borderWidthAttributecombo.select(0);
        if (tmpWidth == null) {
            int index = getAttributeIndex(width, numericAttributesArrays);
            if (index != -1) {
                borderWidthAttributecombo.select(index);
            }
        }

        // border alpha
        Label borderOpactityLabel = new Label(mainComposite, SWT.NONE);
        borderOpactityLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderOpactityLabel.setText(Messages.PointBoderParametersComposite_4);
        borderOpacitySpinner = new Spinner(mainComposite, SWT.BORDER);
        borderOpacitySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderOpacitySpinner.setMaximum(100);
        borderOpacitySpinner.setMinimum(0);
        borderOpacitySpinner.setIncrement(10);
        String opacity = symbolizersWrapper.getStrokeOpacity();
        Double tmpOpacity = isDouble(opacity);
        tmp = 100;
        if (tmpOpacity != null) {
            tmp = (int) (tmpOpacity.doubleValue() * 100);
        }
        borderOpacitySpinner.setSelection(tmp);
        borderOpacitySpinner.addSelectionListener(this);
        borderOpacityAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        borderOpacityAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderOpacityAttributecombo.setItems(numericAttributesArrays);
        borderOpacityAttributecombo.addSelectionListener(this);
        borderOpacityAttributecombo.select(0);
        if (tmpOpacity == null) {
            int index = getAttributeIndex(opacity, numericAttributesArrays);
            if (index != -1) {
                borderOpacityAttributecombo.select(index);
            }
        }

        // border color
        Label borderColorLabel = new Label(mainComposite, SWT.NONE);
        borderColorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderColorLabel.setText(Messages.PointBoderParametersComposite_5);
        Color tmpColor;
        try {
            tmpColor = Color.decode(symbolizersWrapper.getStrokeColor());
        } catch (Exception e) {
            tmpColor = Color.gray;
        }
        borderColorEditor = new StolenColorEditor(mainComposite, this);
        borderColorEditor.setColor(tmpColor);
        borderColorButton = borderColorEditor.getButton();
        GridData borderColorButtonSIMPLEGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        borderColorButtonSIMPLEGD.horizontalSpan = 2;
        borderColorButton.setLayoutData(borderColorButtonSIMPLEGD);

        checkEnablements();
    }

    /**
     * Initialize the composite with values from a rule.
     * 
     * @param ruleWrapper the rule to take the info from.
     */
    public void update( RuleWrapper ruleWrapper ) {
        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);
        boolean widgetEnabled = pointSymbolizerWrapper.hasStroke();
        // border
        borderEnableButton.setSelection(widgetEnabled);

        String width = pointSymbolizerWrapper.getStrokeWidth();
        Double tmpWidth = isDouble(width);
        int tmp = 3;
        if (tmpWidth != null) {
            tmp = tmpWidth.intValue();
        }
        borderWidthSpinner.setSelection(tmp * 10);
        if (tmpWidth == null) {
            int index = getAttributeIndex(width, numericAttributesArrays);
            if (index != -1) {
                borderWidthAttributecombo.select(index);
            }
        }

        // border color
        Color tmpColor;
        try {
            tmpColor = Color.decode(pointSymbolizerWrapper.getStrokeColor());
        } catch (Exception e) {
            tmpColor = Color.gray;
        }
        borderColorEditor.setColor(tmpColor);

        // border alpha
        String opacity = pointSymbolizerWrapper.getStrokeOpacity();
        Double tmpOpacity = isDouble(opacity);
        tmp = 100;
        if (tmpOpacity != null) {
            tmp = (int) (tmpOpacity.doubleValue() * 100);
        }
        borderOpacitySpinner.setSelection(tmp);
        if (tmpOpacity == null) {
            int index = getAttributeIndex(opacity, numericAttributesArrays);
            if (index != -1) {
                borderOpacityAttributecombo.select(index);
            }
        }

        checkEnablements();
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(borderEnableButton)) {
            boolean selected = borderEnableButton.getSelection();
            notifyListeners(String.valueOf(selected), false, STYLEEVENTTYPE.BORDERENABLE);
        } else if (source.equals(borderWidthSpinner) || source.equals(borderWidthAttributecombo)) {
            boolean comboIsNone = comboIsNone(borderWidthAttributecombo);
            if (comboIsNone) {
                int selection = borderWidthSpinner.getSelection();
                int digits = borderWidthSpinner.getDigits();
                double value = selection / Math.pow(10, digits);
                String strokeWidth = String.valueOf(value);
                notifyListeners(strokeWidth, false, STYLEEVENTTYPE.BORDERWIDTH);
            } else {
                int index = borderWidthAttributecombo.getSelectionIndex();
                String field = borderWidthAttributecombo.getItem(index);
                if (field.length() == 0) {
                    return;
                }
                notifyListeners(field, true, STYLEEVENTTYPE.BORDERWIDTH);
            }
        } else if (source.equals(borderColorButton)) {
            Color color = borderColorEditor.getColor();
            Expression colorExpr = ff.literal(color);
            String strokeColor = colorExpr.evaluate(null, String.class);
            notifyListeners(strokeColor, false, STYLEEVENTTYPE.BORDERCOLOR);
        } else if (source.equals(borderOpacitySpinner) || source.equals(borderOpacityAttributecombo)) {
            boolean comboIsNone = comboIsNone(borderOpacityAttributecombo);
            if (comboIsNone) {
                int opacity = borderOpacitySpinner.getSelection();
                float opacityNorm = opacity / 100f;
                String strokeOpacity = String.valueOf(opacityNorm);
                notifyListeners(strokeOpacity, false, STYLEEVENTTYPE.BORDEROPACITY);
            } else {
                int index = borderOpacityAttributecombo.getSelectionIndex();
                String field = borderOpacityAttributecombo.getItem(index);
                if (field.length() == 0) {
                    return;
                }
                notifyListeners(field, true, STYLEEVENTTYPE.BORDEROPACITY);
            }
        }

        checkEnablements();
    }

    private void checkEnablements() {
        boolean comboIsNone = comboIsNone(borderWidthAttributecombo);
        borderWidthSpinner.setEnabled(comboIsNone);
        comboIsNone = comboIsNone(borderOpacityAttributecombo);
        borderOpacitySpinner.setEnabled(comboIsNone);
    }

}
