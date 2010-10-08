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
import java.io.File;
import java.net.MalformedURLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.opengis.filter.expression.Expression;

import eu.udig.style.advanced.common.ParameterComposite;
import eu.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import eu.udig.style.advanced.common.styleattributeclasses.PolygonSymbolizerWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import eu.udig.style.advanced.utils.StolenColorEditor;
import eu.udig.style.advanced.utils.Utilities;

/**
 * A composite that holds widgets for polygon fill parameter setting.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class PolygonFillParametersComposite extends ParameterComposite implements ModifyListener {

    private final Composite parent;
    private final String[] numericAttributesArrays;

    private Composite mainComposite;
    private Button fillEnableButton;
    private StolenColorEditor fillColorEditor;
    private Button fillColorButton;
    private Spinner fillOpacitySpinner;
    private Combo fillOpacityAttributecombo;
    private Combo wkmarkNameCombo;
    private Spinner wkmWidthSpinner;
    private StolenColorEditor wkmColorEditor;
    private Text graphicsPathText;
    private Button wkmColorButton;
    private Spinner wkmSizeSpinner;

    public PolygonFillParametersComposite( Composite parent, String[] numericAttributesArrays ) {
        this.parent = parent;
        this.numericAttributesArrays = numericAttributesArrays;
    }

    public Composite getComposite() {
        return mainComposite;
    }

    /**
     * Initialize the panel with pre-existing values.
     * 
     * @param ruleWrapper the {@link RuleWrapper}.
     */
    public void init( RuleWrapper ruleWrapper ) {
        PolygonSymbolizerWrapper polygonSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PolygonSymbolizerWrapper.class);

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(3, true));

        boolean widgetEnabled = polygonSymbolizerWrapper.hasFill();

        fillEnableButton = new Button(mainComposite, SWT.CHECK);
        GridData fillEnableButtonGD = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        fillEnableButtonGD.horizontalSpan = 3;
        fillEnableButton.setLayoutData(fillEnableButtonGD);
        fillEnableButton.setText("enable/disable fill");
        fillEnableButton.setSelection(widgetEnabled);
        fillEnableButton.addSelectionListener(this);

        /*
         * radio panel for fill choice
         */
        // fill color
        Label colorLabel = new Label(mainComposite, SWT.RADIO);
        colorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        colorLabel.setText("color");

        String color = polygonSymbolizerWrapper.getFillColor();
        Color tmpColor = null;
        try {
            tmpColor = Color.decode(color);
        } catch (Exception e) {
            tmpColor = Color.gray;
        }
        fillColorEditor = new StolenColorEditor(mainComposite, this);
        fillColorEditor.setColor(tmpColor);
        fillColorButton = fillColorEditor.getButton();
        GridData fillColorButtonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        fillColorButtonGD.horizontalSpan = 2;
        fillColorButton.setLayoutData(fillColorButtonGD);

        // graphics fill
        Label graphicsFillLabel = new Label(mainComposite, SWT.RADIO);
        graphicsFillLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        graphicsFillLabel.setText("graphics");

        Composite pathComposite = new Composite(mainComposite, SWT.NONE);
        GridData pathCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        pathCompositeGD.horizontalSpan = 2;
        pathComposite.setLayoutData(pathCompositeGD);
        GridLayout pathLayout = new GridLayout(2, false);
        pathLayout.marginWidth = 0;
        pathLayout.marginHeight = 0;
        pathComposite.setLayout(pathLayout);
        graphicsPathText = new Text(pathComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        graphicsPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        try {
            graphicsPathText.setText(polygonSymbolizerWrapper.getFillExternalGraphicFillPath());
        } catch (Exception e) {
            graphicsPathText.setText(""); //$NON-NLS-1$
        }
        graphicsPathText.addModifyListener(this);
        Button pathButton = new Button(pathComposite, SWT.PUSH);
        pathButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        pathButton.setText("..."); //$NON-NLS-1$
        pathButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FileDialog fileDialog = new FileDialog(graphicsPathText.getShell(), SWT.OPEN);
                String path = fileDialog.open();
                if (path == null || path.length() < 1) {
                    graphicsPathText.setText(""); //$NON-NLS-1$
                } else {
                    graphicsPathText.setText(path);
                }
            }
        });

        // well known marks
        Label wkmLabel = new Label(mainComposite, SWT.RADIO);
        wkmLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        wkmLabel.setText("well known mark");

        wkmarkNameCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData wkmarkNameComboGD = new GridData(SWT.FILL, SWT.FILL, false, false);
        wkmarkNameComboGD.horizontalSpan = 2;
        wkmarkNameCombo.setLayoutData(wkmarkNameComboGD);
        wkmarkNameCombo.setItems(Utilities.getAllMarksArray());
        wkmarkNameCombo.addSelectionListener(this);

        String wkMarkNameFill = polygonSymbolizerWrapper.getWkMarkNameFill();
        int attributeIndex = getAttributeIndex(wkMarkNameFill, Utilities.getAllMarksArray());
        if (attributeIndex != -1) {
            wkmarkNameCombo.select(attributeIndex);
        }

        new Label(mainComposite, SWT.NONE);

        Composite wkmarkComposite = new Composite(mainComposite, SWT.NONE);
        GridData wkmarkCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        wkmarkCompositeGD.horizontalSpan = 2;
        wkmarkComposite.setLayoutData(wkmarkCompositeGD);
        GridLayout wkmarkCompositeLayout = new GridLayout(3, false);
        wkmarkCompositeLayout.marginWidth = 0;
        wkmarkCompositeLayout.marginHeight = 0;
        wkmarkComposite.setLayout(wkmarkCompositeLayout);

        // mark width
        wkmWidthSpinner = new Spinner(wkmarkComposite, SWT.BORDER);
        wkmWidthSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        wkmWidthSpinner.setMaximum(200);
        wkmWidthSpinner.setMinimum(0);
        wkmWidthSpinner.setIncrement(1);
        wkmWidthSpinner.setToolTipText("Width");

        String wkMarkWidth = polygonSymbolizerWrapper.getWkMarkWidthFill();
        Double tmpWidth = isDouble(wkMarkWidth);
        int tmpWidthInt = 1;
        if (tmpWidth != null) {
            tmpWidthInt = tmpWidth.intValue();
        }
        wkmWidthSpinner.setSelection(tmpWidthInt * 10);
        wkmWidthSpinner.setDigits(1);
        wkmWidthSpinner.addSelectionListener(this);

        // mark size
        wkmSizeSpinner = new Spinner(wkmarkComposite, SWT.BORDER);
        wkmSizeSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        wkmSizeSpinner.setMaximum(1000);
        wkmSizeSpinner.setMinimum(0);
        wkmSizeSpinner.setIncrement(1);
        wkmSizeSpinner.setToolTipText("Size");

        String wkMarkSize = polygonSymbolizerWrapper.getWkMarkSizeFill();
        Double tmpSize = isDouble(wkMarkSize);
        int tmpSizeInt = 5;
        if (tmpSize != null) {
            tmpSizeInt = tmpSize.intValue();
        }
        wkmSizeSpinner.setSelection(tmpSizeInt * 10);
        wkmSizeSpinner.setDigits(1);
        wkmSizeSpinner.addSelectionListener(this);

        // mark color
        String wkMarkColor = polygonSymbolizerWrapper.getWkMarkColorFill();
        Color tmpWkmColor;
        try {
            tmpWkmColor = Color.decode(wkMarkColor);
        } catch (Exception e) {
            tmpWkmColor = Color.gray;
        }
        wkmColorEditor = new StolenColorEditor(wkmarkComposite, this);
        wkmColorEditor.setColor(tmpWkmColor);
        wkmColorButton = wkmColorEditor.getButton();
        GridData wkmColorButtonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        wkmColorButton.setLayoutData(wkmColorButtonGD);

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

        String opacity = polygonSymbolizerWrapper.getFillOpacity();
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

        checkEnablements();
    }

    /**
     * Update the panel.
     * 
     * @param ruleWrapper the {@link RuleWrapper}.
     */
    public void update( RuleWrapper ruleWrapper ) {
        PolygonSymbolizerWrapper polygonSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PolygonSymbolizerWrapper.class);
        boolean widgetEnabled = polygonSymbolizerWrapper.hasFill();
        fillEnableButton.setSelection(widgetEnabled);

        String color = polygonSymbolizerWrapper.getFillColor();
        Color tmpColor = null;
        try {
            tmpColor = Color.decode(color);
        } catch (Exception e) {
            tmpColor = Color.gray;
        }
        fillColorEditor.setColor(tmpColor);

        // graphics path
        try {
            graphicsPathText.setText(polygonSymbolizerWrapper.getFillExternalGraphicFillPath());
        } catch (MalformedURLException e1) {
            graphicsPathText.setText("");
        }

        String wkMarkNameFill = polygonSymbolizerWrapper.getWkMarkNameFill();
        String mName = Utilities.markNamesToDef.inverse().get(wkMarkNameFill);
        int attributeIndex = getAttributeIndex(mName, Utilities.getAllMarksArray());
        if (attributeIndex != -1) {
            wkmarkNameCombo.select(attributeIndex);
        } else {
            wkmarkNameCombo.select(0);
        }

        Double tmpWkmWidth = isDouble(polygonSymbolizerWrapper.getWkMarkWidthFill());
        int tmpWkm = 3;
        if (tmpWkmWidth != null) {
            tmpWkm = tmpWkmWidth.intValue();
        }
        wkmWidthSpinner.setSelection(tmpWkm * 10);

        Double tmpWkmSize = isDouble(polygonSymbolizerWrapper.getWkMarkSizeFill());
        int tmpWkmSizeInt = 3;
        if (tmpWkmSize != null) {
            tmpWkmSizeInt = tmpWkmSize.intValue();
        }
        wkmSizeSpinner.setSelection(tmpWkmSizeInt * 10);

        // border color
        Color tmpWkmColor;
        try {
            tmpWkmColor = Color.decode(polygonSymbolizerWrapper.getWkMarkColorFill());
        } catch (Exception e) {
            tmpWkmColor = Color.gray;
        }
        wkmColorEditor.setColor(tmpWkmColor);

        // fill alpha
        String opacity = polygonSymbolizerWrapper.getFillOpacity();
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

        checkEnablements();
    }

    private void checkEnablements() {
        boolean comboIsNone = comboIsNone(fillOpacityAttributecombo);
        fillOpacitySpinner.setEnabled(comboIsNone);
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
        } else if (source.equals(fillOpacitySpinner) || source.equals(fillOpacityAttributecombo)) {
            boolean comboIsNone = comboIsNone(fillOpacityAttributecombo);
            if (comboIsNone) {
                int opacity = fillOpacitySpinner.getSelection();
                float opacityNorm = opacity / 100f;
                String fillOpacity = String.valueOf(opacityNorm);
                notifyListeners(fillOpacity, false, STYLEEVENTTYPE.FILLOPACITY);
            } else {
                int index = fillOpacityAttributecombo.getSelectionIndex();
                String field = fillOpacityAttributecombo.getItem(index);
                if (field.length() == 0) {
                    return;
                }
                notifyListeners(field, true, STYLEEVENTTYPE.FILLOPACITY);
            }
        } else if (source.equals(wkmarkNameCombo) || source.equals(wkmColorButton) || source.equals(wkmWidthSpinner)
                || source.equals(wkmSizeSpinner)) {
            doWkmGraphics();
        }

        checkEnablements();
    }
    public void modifyText( ModifyEvent e ) {
        Object source = e.getSource();
        if (source.equals(graphicsPathText)) {
            try {
                String text = graphicsPathText.getText();
                File graphicsFile = new File(text);
                if (graphicsFile.exists() || text.toLowerCase().startsWith("http")) {
                    text = graphicsFile.toURI().toURL().toExternalForm();
                    notifyListeners(text, false, STYLEEVENTTYPE.GRAPHICSPATHFILL);
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void doWkmGraphics() {
        int selectionIndex = wkmarkNameCombo.getSelectionIndex();
        String name = wkmarkNameCombo.getItem(selectionIndex);
        if (name.equals("")) {
            return;
        }
        name = Utilities.markNamesToDef.get(name);

        int selection = wkmWidthSpinner.getSelection();
        int digits = wkmWidthSpinner.getDigits();
        double value = selection / Math.pow(10, digits);
        String strokeWidth = String.valueOf(value);

        selection = wkmSizeSpinner.getSelection();
        digits = wkmSizeSpinner.getDigits();
        value = selection / Math.pow(10, digits);
        String strokeSize = String.valueOf(value);

        Color color = wkmColorEditor.getColor();
        Expression colorExpr = ff.literal(color);
        String strokeColor = colorExpr.evaluate(null, String.class);

        notifyListeners(new String[]{name, strokeWidth, strokeColor, strokeSize}, false, STYLEEVENTTYPE.WKMGRAPHICSFILL);
    }

}
