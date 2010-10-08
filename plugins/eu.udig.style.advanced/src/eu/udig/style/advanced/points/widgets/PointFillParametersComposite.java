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
import eu.udig.style.advanced.utils.StolenColorEditor;

/**
 * A composite that holds widgets for fill parameter setting.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class PointFillParametersComposite extends ParameterComposite {

    private final Composite parent;
    private final String[] numericAttributesArrays;

    private Composite mainComposite;
    private Button fillEnableButton;
    private StolenColorEditor fillColorEditor;
    private Button fillColorButton;
    private Spinner fillOpacitySpinner;
    private Combo fillOpacityAttributecombo;

    public PointFillParametersComposite( Composite parent, String[] numericAttributesArrays ) {
        this.parent = parent;
        this.numericAttributesArrays = numericAttributesArrays;
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
        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(PointSymbolizerWrapper.class);

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(3, true));

        boolean widgetEnabled = pointSymbolizerWrapper.hasFill();

        fillEnableButton = new Button(mainComposite, SWT.CHECK);
        GridData fillEnableButtonGD = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        fillEnableButtonGD.horizontalSpan = 3;
        fillEnableButton.setLayoutData(fillEnableButtonGD);
        fillEnableButton.setText("enable/disable fill");
        fillEnableButton.setSelection(widgetEnabled);
        fillEnableButton.addSelectionListener(this);

        // header
        new Label(mainComposite, SWT.NONE);
        Label valueLabel = new Label(mainComposite, SWT.NONE);
        valueLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        valueLabel.setText("Manual");
        Label fieldsLabel = new Label(mainComposite, SWT.NONE);
        fieldsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        fieldsLabel.setText("Field based");
        
        // border alpha
        Label fillOpactityLabel = new Label(mainComposite, SWT.NONE);
        fillOpactityLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fillOpactityLabel.setText("opacity");
        fillOpacitySpinner = new Spinner(mainComposite, SWT.BORDER);
        fillOpacitySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fillOpacitySpinner.setMaximum(100);
        fillOpacitySpinner.setMinimum(0);
        fillOpacitySpinner.setIncrement(10);
        String opacity = pointSymbolizerWrapper.getFillOpacity();
        Double tmpOpacity = isDouble(opacity);
        int tmp = 100;
        if (tmpOpacity != null) {
            tmp = (int) (tmpOpacity.doubleValue() * 100);
        }
        fillOpacitySpinner.setSelection(tmp);
        fillOpacitySpinner.addSelectionListener(this);
        fillOpacityAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        fillOpacityAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fillOpacityAttributecombo.setItems(numericAttributesArrays);
        fillOpacityAttributecombo.addSelectionListener(this);
        fillOpacityAttributecombo.select(0);
        if (tmpOpacity == null) {
            int index = getAttributeIndex(opacity, numericAttributesArrays);
            if (index != -1) {
                fillOpacityAttributecombo.select(index);
            }
        }

        // fill color
        Label fillColorLabel = new Label(mainComposite, SWT.NONE);
        fillColorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fillColorLabel.setText("color");
        Color tmpColor = null;;
        try {
            tmpColor = Color.decode(pointSymbolizerWrapper.getFillColor());
        } catch (Exception e) {
            tmpColor = Color.gray;
        }
        fillColorEditor = new StolenColorEditor(mainComposite, this);
        fillColorEditor.setColor(tmpColor);
        fillColorButton = fillColorEditor.getButton();
        GridData fillColorButtonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        fillColorButtonGD.horizontalSpan = 2;
        fillColorButton.setLayoutData(fillColorButtonGD);
    }

    /**
     * Initialize the composite with values from a rule.
     * 
     * @param ruleWrapper the rule to take the info from.
     */
    public void update( RuleWrapper ruleWrapper ) {
        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(PointSymbolizerWrapper.class);

        boolean widgetEnabled = pointSymbolizerWrapper.hasFill();
        fillEnableButton.setSelection(widgetEnabled);

        Color tmpColor = null;
        try {
            tmpColor = Color.decode(pointSymbolizerWrapper.getFillColor());
        } catch (Exception e) {
            tmpColor = Color.gray;
        }
        fillColorEditor.setColor(tmpColor);

        // fill alpha
        String opacity = pointSymbolizerWrapper.getFillOpacity();
        Double tmpOpacity = isDouble(opacity);
        int tmp = 100;
        if (tmpOpacity != null) {
            tmp = (int) (tmpOpacity.doubleValue() * 100);
        }
        fillOpacitySpinner.setSelection(tmp);
        if (tmpOpacity == null) {
            int index = getAttributeIndex(opacity, numericAttributesArrays);
            if (index != -1) {
                fillOpacityAttributecombo.select(index);
            }
        }
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(fillEnableButton)) {
            boolean selected = fillEnableButton.getSelection();
            notifyListeners(String.valueOf(selected), false, STYLEEVENTTYPE.FILLENABLE);
        } else if (source.equals(fillColorButton)) {
            Color color = fillColorEditor.getColor();
            Expression colorExpr = ff.literal(color);
            String fillColor = colorExpr.evaluate(null, String.class);
            notifyListeners(fillColor, false, STYLEEVENTTYPE.FILLCOLOR);
        } else if (source.equals(fillOpacitySpinner)) {
            int opacity = fillOpacitySpinner.getSelection();
            float opacityNorm = opacity / 100f;
            String fillOpacity = String.valueOf(opacityNorm);
            notifyListeners(fillOpacity, false, STYLEEVENTTYPE.FILLOPACITY);
        } else if (source.equals(fillOpacityAttributecombo)) {
            int index = fillOpacityAttributecombo.getSelectionIndex();
            String field = fillOpacityAttributecombo.getItem(index);
            if (field.length() == 0) {
                return;
            }
            notifyListeners(field, true, STYLEEVENTTYPE.FILLOPACITY);
        }
    }

}
