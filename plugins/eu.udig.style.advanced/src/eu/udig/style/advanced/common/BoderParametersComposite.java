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
package eu.udig.style.advanced.common;

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

import eu.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import eu.udig.style.advanced.common.styleattributeclasses.LineSymbolizerWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import eu.udig.style.advanced.utils.StolenColorEditor;
import eu.udig.style.advanced.utils.Utilities;

/**
 * A composite that holds widgets for polygon border parameter setting.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class BoderParametersComposite extends ParameterComposite implements ModifyListener {

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
    private Text graphicsPathText;

    private Text dashText;
    private Text dashOffsetText;
    private Combo lineJoinCombo;
    private Combo lineCapCombo;
    private final String[] stringAttributesArrays;
    private Combo borderColorAttributecombo;

    public BoderParametersComposite( Composite parent, String[] numericAttributesArrays, String[] stringattributesArrays ) {
        this.parent = parent;
        this.numericAttributesArrays = numericAttributesArrays;
        this.stringAttributesArrays = stringattributesArrays;
    }

    @Override
    public Composite getComposite() {
        return mainComposite;
    }

    /**
     * Initialize the panel with pre-existing values.
     * 
     * @param ruleWrapper the {@link RuleWrapper}.
     */
    public void init( RuleWrapper ruleWrapper ) {
        LineSymbolizerWrapper lineSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                LineSymbolizerWrapper.class);

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(3, true));

        boolean widgetEnabled = lineSymbolizerWrapper.hasStroke();

        borderEnableButton = new Button(mainComposite, SWT.CHECK);
        GridData borderEnableButtonGD = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        borderEnableButtonGD.horizontalSpan = 3;
        borderEnableButton.setLayoutData(borderEnableButtonGD);
        borderEnableButton.setText("enable/disable border");
        borderEnableButton.setSelection(widgetEnabled);
        borderEnableButton.addSelectionListener(this);

        // header
        new Label(mainComposite, SWT.NONE);
        Label valueLabel = new Label(mainComposite, SWT.NONE);
        valueLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        valueLabel.setText("Manual");
        Label fieldsLabel = new Label(mainComposite, SWT.NONE);
        fieldsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        fieldsLabel.setText("Field based");

        // border width
        Label borderWidthLabel = new Label(mainComposite, SWT.NONE);
        borderWidthLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderWidthLabel.setText("width");
        borderWidthSpinner = new Spinner(mainComposite, SWT.BORDER);
        borderWidthSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderWidthSpinner.setMaximum(500);
        borderWidthSpinner.setMinimum(0);
        borderWidthSpinner.setIncrement(10);

        String width = lineSymbolizerWrapper.getStrokeWidth();
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
        borderOpactityLabel.setText("opacity");
        borderOpacitySpinner = new Spinner(mainComposite, SWT.BORDER);
        borderOpacitySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderOpacitySpinner.setMaximum(100);
        borderOpacitySpinner.setMinimum(0);
        borderOpacitySpinner.setIncrement(10);

        String opacity = lineSymbolizerWrapper.getStrokeOpacity();
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
        borderColorLabel.setText("color");
        String color = lineSymbolizerWrapper.getStrokeColor();
        Color tmpColor;
        try {
            tmpColor = Color.decode(color);
        } catch (Exception e) {
            tmpColor = Color.gray;
        }
        borderColorEditor = new StolenColorEditor(mainComposite, this);
        borderColorEditor.setColor(tmpColor);
        borderColorButton = borderColorEditor.getButton();
        GridData borderColorButtonSIMPLEGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        borderColorButton.setLayoutData(borderColorButtonSIMPLEGD);

        borderColorAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        borderColorAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderColorAttributecombo.setItems(stringAttributesArrays);
        borderColorAttributecombo.addSelectionListener(this);
        borderColorAttributecombo.select(0);
        if (tmpColor == null) {
            int index = getAttributeIndex(color, stringAttributesArrays);
            if (index != -1) {
                borderColorAttributecombo.select(index);
            }
        }

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
            graphicsPathText.setText(lineSymbolizerWrapper.getStrokeExternalGraphicStrokePath());
        } catch (MalformedURLException e1) {
            graphicsPathText.setText("");
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

        // line properties
        // dash
        Label dashLabel = new Label(mainComposite, SWT.NONE);
        GridData dashLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dashLabel.setLayoutData(dashLabelGD);
        dashLabel.setText("dash");
        dashText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData dashGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dashGD.horizontalSpan = 2;
        dashText.setLayoutData(dashGD);

        String dash = lineSymbolizerWrapper.getDash();
        float[] dashArray = Utilities.getDash(dash);
        if (dashArray != null) {
            dashText.setText(dash);
        } else {
            dashText.setText(""); //$NON-NLS-1$
        }
        dashText.addModifyListener(this);
        // dashoffset
        Label dashOffsetLabel = new Label(mainComposite, SWT.NONE);
        GridData dashOffsetLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dashOffsetLabel.setLayoutData(dashOffsetLabelGD);
        dashOffsetLabel.setText("dash offset");
        dashOffsetText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData dashOffsetGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dashOffsetGD.horizontalSpan = 2;
        dashOffsetText.setLayoutData(dashOffsetGD);

        String dashOffset = lineSymbolizerWrapper.getDashOffset();
        Double dashOffsetFloat = isDouble(dashOffset);
        if (dashOffsetFloat != null) {
            dashOffsetText.setText(dashOffset);
        } else {
            dashOffsetText.setText(""); //$NON-NLS-1$
        }
        dashOffsetText.addModifyListener(this);

        // line cap
        Label linCapLabel = new Label(mainComposite, SWT.NONE);
        linCapLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        linCapLabel.setText("line cap");
        lineCapCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData lineCapGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        lineCapGD.horizontalSpan = 2;
        lineCapCombo.setLayoutData(lineCapGD);
        lineCapCombo.setItems(Utilities.lineCapNames);
        lineCapCombo.addSelectionListener(this);

        String lineCap = lineSymbolizerWrapper.getLineCap();
        if (lineCap != null) {
            int index = getAttributeIndex(lineCap, Utilities.lineCapNames);
            if (index != -1) {
                lineCapCombo.select(index);
            }
        }

        // line join
        Label linJoinLabel = new Label(mainComposite, SWT.NONE);
        linJoinLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        linJoinLabel.setText("line join");
        lineJoinCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData lineJoinGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        lineJoinGD.horizontalSpan = 2;
        lineJoinCombo.setLayoutData(lineJoinGD);
        lineJoinCombo.setItems(Utilities.lineJoinNames);
        lineJoinCombo.addSelectionListener(this);

        String lineJoin = lineSymbolizerWrapper.getLineJoin();
        if (lineJoin != null) {
            int index = getAttributeIndex(lineJoin, Utilities.lineJoinNames);
            if (index != -1) {
                lineJoinCombo.select(index);
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
        LineSymbolizerWrapper lineSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                LineSymbolizerWrapper.class);

        boolean widgetEnabled = lineSymbolizerWrapper.hasStroke();
        // border
        borderEnableButton.setSelection(widgetEnabled);

        String width = lineSymbolizerWrapper.getStrokeWidth();
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
        String color = lineSymbolizerWrapper.getStrokeColor();
        Color tmpColor = null;
        try {
            tmpColor = Color.decode(color);
        } catch (Exception e) {
            // ignore and try for field
        }
        if (tmpColor != null) {
            borderColorEditor.setColor(tmpColor);
        } else {
            int index = getAttributeIndex(color, stringAttributesArrays);
            if (index != -1) {
                borderColorAttributecombo.select(index);
            }
        }

        // graphics path
        try {
            graphicsPathText.setText(lineSymbolizerWrapper.getStrokeExternalGraphicStrokePath());
        } catch (MalformedURLException e) {
            graphicsPathText.setText("");
        }

        // border alpha
        String opacity = lineSymbolizerWrapper.getStrokeOpacity();
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

        // dash
        String dash = lineSymbolizerWrapper.getDash();
        float[] dashArray = Utilities.getDash(dash);
        if (dashArray != null) {
            dashText.setText(dash);
        } else {
            dashText.setText("");
        }
        // dash offset
        String dashOffset = lineSymbolizerWrapper.getDashOffset();
        Double dashOffsetDouble = isDouble(dashOffset);
        if (dashOffsetDouble != null) {
            dashOffsetText.setText(dashOffset);
        } else {
            dashOffsetText.setText("");
        }

        // line cap
        String lineCap = lineSymbolizerWrapper.getLineCap();
        if (lineCap != null) {
            int index = getAttributeIndex(lineCap, Utilities.lineCapNames);
            if (index != -1) {
                lineCapCombo.select(index);
            }
        }

        // line join
        String lineJoin = lineSymbolizerWrapper.getLineJoin();
        if (lineJoin != null) {
            int index = getAttributeIndex(lineJoin, Utilities.lineJoinNames);
            if (index != -1) {
                lineJoinCombo.select(index);
            }
        }

        checkEnablements();
    }

    private void checkEnablements() {
        boolean comboIsNone = comboIsNone(borderOpacityAttributecombo);
        borderOpacitySpinner.setEnabled(comboIsNone);
        comboIsNone = comboIsNone(borderWidthAttributecombo);
        borderWidthSpinner.setEnabled(comboIsNone);
        comboIsNone = comboIsNone(borderColorAttributecombo);
        borderColorEditor.setEnabled(comboIsNone);
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
        } else if (source.equals(borderColorButton) || source.equals(borderColorAttributecombo)) {
            boolean comboIsNone = comboIsNone(borderColorAttributecombo);
            if (comboIsNone) {
                Color color = borderColorEditor.getColor();
                Expression colorExpr = ff.literal(color);
                String strokeColor = colorExpr.evaluate(null, String.class);
                notifyListeners(strokeColor, false, STYLEEVENTTYPE.BORDERCOLOR);
            } else {
                int index = borderColorAttributecombo.getSelectionIndex();
                String field = borderColorAttributecombo.getItem(index);
                if (field.length() == 0) {
                    return;
                }
                notifyListeners(field, true, STYLEEVENTTYPE.BORDERCOLOR);
            }
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
        } else if (source.equals(lineCapCombo)) {
            int index = lineCapCombo.getSelectionIndex();
            String item = lineCapCombo.getItem(index);
            if (item.length() == 0) {
                return;
            }
            notifyListeners(item, true, STYLEEVENTTYPE.LINECAP);
        } else if (source.equals(lineJoinCombo)) {
            int index = lineJoinCombo.getSelectionIndex();
            String item = lineJoinCombo.getItem(index);
            if (item.length() == 0) {
                return;
            }
            notifyListeners(item, true, STYLEEVENTTYPE.LINEJOIN);
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

                    // FIXME bring those to gui
                    String strokeWidth = String.valueOf(1);
                    String strokeSize = String.valueOf(15);

                    notifyListeners(new String[]{text, strokeWidth, strokeSize}, false, STYLEEVENTTYPE.GRAPHICSPATHBORDER);
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        } else if (source.equals(dashText)) {
            String text = dashText.getText();
            float[] dash = Utilities.getDash(text);
            if (dash == null) {
                return;
            }
            notifyListeners(new String[]{text}, false, STYLEEVENTTYPE.DASH);
        } else if (source.equals(dashOffsetText)) {
            String text = dashOffsetText.getText();
            notifyListeners(new String[]{text}, false, STYLEEVENTTYPE.DASHOFFSET);
        }
    }

}
