/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.points.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.locationtech.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import org.locationtech.udig.style.advanced.common.ParameterComposite;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.PointSymbolizerWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import org.locationtech.udig.style.advanced.internal.Messages;
import org.locationtech.udig.style.advanced.utils.Utilities;

/**
 * A composite that holds widgets for general parameter setting.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class PointGeneralParametersComposite extends ParameterComposite {

    private final Composite parent;
    private final String[] numericAttributesArrays;

    private Text nameText;
    private Spinner sizeSpinner;
    private Combo sizeAttributecombo;
    private Spinner rotationSpinner;
    private Combo rotationAttributecombo;
    private Spinner xOffsetSpinner;
    private Spinner yOffsetSpinner;
    private Text maxScaleText;
    private Text minScaleText;

    private Composite mainComposite;

    public PointGeneralParametersComposite( Composite parent, String[] numericAttributesArrays ) {
        this.parent = parent;
        this.numericAttributesArrays = numericAttributesArrays;
    }

    public Composite getComposite() {
        return mainComposite;
    }

    public void init( RuleWrapper ruleWrapper ) {
        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(3, true));

        // rule name
        Label nameLabel = new Label(mainComposite, SWT.NONE);
        nameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        nameLabel.setText(Messages.PointGeneralParametersComposite_0);
        nameText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData nameTextGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        nameTextGD.horizontalSpan = 2;
        nameText.setLayoutData(nameTextGD);
        nameText.setText(ruleWrapper.getName());
        nameText.addFocusListener(this);

        // header
        new Label(mainComposite, SWT.NONE);
        Label valueLabel = new Label(mainComposite, SWT.NONE);
        valueLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        valueLabel.setText(Messages.PointGeneralParametersComposite_1);
        Label fieldsLabel = new Label(mainComposite, SWT.NONE);
        fieldsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        fieldsLabel.setText(Messages.PointGeneralParametersComposite_2);

        // size
        Label sizeLabel = new Label(mainComposite, SWT.NONE);
        sizeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sizeLabel.setText(Messages.PointGeneralParametersComposite_3);
        sizeSpinner = new Spinner(mainComposite, SWT.BORDER);
        sizeSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sizeSpinner.setMaximum(200);
        sizeSpinner.setMinimum(0);
        sizeSpinner.setIncrement(1);
        String size = pointSymbolizerWrapper.getSize();
        Double tmpSize = isDouble(size);
        int tmp = 3;
        if (tmpSize != null) {
            tmp = tmpSize.intValue();
        }
        sizeSpinner.setSelection(tmp);
        sizeSpinner.addSelectionListener(this);
        sizeAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        sizeAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sizeAttributecombo.setItems(numericAttributesArrays);
        sizeAttributecombo.addSelectionListener(this);
        if (tmpSize == null) {
            int index = getAttributeIndex(size, numericAttributesArrays);
            if (index != -1) {
                sizeAttributecombo.select(index);
            }
        }

        Label rotationLabel = new Label(mainComposite, SWT.NONE);
        rotationLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        rotationLabel.setText(Messages.PointGeneralParametersComposite_4);
        rotationSpinner = new Spinner(mainComposite, SWT.BORDER);
        rotationSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        rotationSpinner.setMaximum(360);
        rotationSpinner.setMinimum(0);
        rotationSpinner.setIncrement(5);
        String rotation = pointSymbolizerWrapper.getRotation();
        Double tmpRotation = isDouble(rotation);
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
        if (tmpSize == null) {
            int index = getAttributeIndex(rotation, numericAttributesArrays);
            if (index != -1) {
                rotationAttributecombo.select(index);
            }
        }

        // offset
        Label offsetLabel = new Label(mainComposite, SWT.NONE);
        GridData offsetLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        offsetLabel.setLayoutData(offsetLabelGD);
        offsetLabel.setText(Messages.PointGeneralParametersComposite_5);

        String xOffset = pointSymbolizerWrapper.getxOffset();
        String yOffset = pointSymbolizerWrapper.getyOffset();
        Double tmpXOffset = Utilities.isNumber(xOffset, Double.class);
        Double tmpYOffset = Utilities.isNumber(yOffset, Double.class);
        if (tmpXOffset == null || tmpYOffset == null) {
            tmpXOffset = 0.0;
            tmpYOffset = 0.0;
        }
        xOffsetSpinner = new Spinner(mainComposite, SWT.BORDER);
        xOffsetSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        xOffsetSpinner.setMaximum(Utilities.OFFSET_MAX);
        xOffsetSpinner.setMinimum(Utilities.OFFSET_MIN);
        xOffsetSpinner.setIncrement(Utilities.OFFSET_STEP);
        xOffsetSpinner.setSelection((int) (10 * tmpXOffset));
        xOffsetSpinner.setDigits(1);
        xOffsetSpinner.addSelectionListener(this);

        yOffsetSpinner = new Spinner(mainComposite, SWT.BORDER);
        yOffsetSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        yOffsetSpinner.setMaximum(Utilities.OFFSET_MAX);
        yOffsetSpinner.setMinimum(Utilities.OFFSET_MIN);
        yOffsetSpinner.setIncrement(Utilities.OFFSET_STEP);
        yOffsetSpinner.setSelection((int) (10 * tmpYOffset));
        yOffsetSpinner.setDigits(1);
        yOffsetSpinner.addSelectionListener(this);

        // scale
        Label maxScaleLabel = new Label(mainComposite, SWT.NONE);
        GridData maxScaleLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        maxScaleLabel.setLayoutData(maxScaleLabelGD);
        maxScaleLabel.setText(Messages.PointGeneralParametersComposite_6);
        maxScaleText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData maxScaleTextSIMPLEGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        maxScaleTextSIMPLEGD.horizontalSpan = 2;
        maxScaleText.setLayoutData(maxScaleTextSIMPLEGD);
        maxScaleText.setText(ruleWrapper.getMaxScale());
        maxScaleText.addKeyListener(this);

        Label minScaleLabel = new Label(mainComposite, SWT.NONE);
        GridData minScaleLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        minScaleLabel.setLayoutData(minScaleLabelGD);
        minScaleLabel.setText(Messages.PointGeneralParametersComposite_7);
        minScaleText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData mainScaleTextSIMPLEGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        mainScaleTextSIMPLEGD.horizontalSpan = 2;
        minScaleText.setLayoutData(mainScaleTextSIMPLEGD);
        minScaleText.setText(ruleWrapper.getMinScale());
        minScaleText.addKeyListener(this);

        checkEnablements();
    }

    public void update( RuleWrapper ruleWrapper ) {
        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);

        nameText.setText(ruleWrapper.getName());
        // size
        String size = pointSymbolizerWrapper.getSize();
        Double tmpSize = isDouble(size);
        int tmp = 3;
        if (tmpSize != null) {
            tmp = tmpSize.intValue();
        }
        sizeSpinner.setSelection(tmp);
        if (tmpSize == null) {
            int index = getAttributeIndex(size, numericAttributesArrays);
            if (index != -1) {
                sizeAttributecombo.select(index);
            }
        }

        // rotation
        String rotation = pointSymbolizerWrapper.getRotation();
        Double tmpRotation = isDouble(rotation);
        tmp = 0;
        if (tmpRotation != null) {
            tmp = tmpRotation.intValue();
        }
        rotationSpinner.setSelection(tmp);
        if (tmpSize == null) {
            int index = getAttributeIndex(rotation, numericAttributesArrays);
            if (index != -1) {
                rotationAttributecombo.select(index);
            }
        }

        // offset
        String xOffset = pointSymbolizerWrapper.getxOffset();
        String yOffset = pointSymbolizerWrapper.getyOffset();
        Double tmpXOffset = Utilities.isNumber(xOffset, Double.class);
        Double tmpYOffset = Utilities.isNumber(yOffset, Double.class);
        if (tmpXOffset == null || tmpYOffset == null) {
            tmpXOffset = 0.0;
            tmpYOffset = 0.0;
        }
        xOffsetSpinner.setSelection((int) (10 * tmpXOffset));
        yOffsetSpinner.setSelection((int) (10 * tmpYOffset));

        // scale
        Double maxScaleDouble = isDouble(ruleWrapper.getMaxScale());
        if (maxScaleDouble == null || Double.isInfinite(maxScaleDouble)) {
            maxScaleText.setText(""); //$NON-NLS-1$
        } else {
            maxScaleText.setText(String.valueOf(maxScaleDouble));
        }
        Double minScaleDouble = isDouble(ruleWrapper.getMinScale());
        if (minScaleDouble == null || minScaleDouble == 0) {
            minScaleText.setText(""); //$NON-NLS-1$
        } else {
            minScaleText.setText(String.valueOf(minScaleDouble));
        }

        checkEnablements();
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(sizeAttributecombo) || source.equals(sizeSpinner)) {
            boolean comboIsNone = comboIsNone(sizeAttributecombo);
            if (comboIsNone) {
                int sizeInt = sizeSpinner.getSelection();
                String size = String.valueOf(sizeInt);
                notifyListeners(size, false, STYLEEVENTTYPE.SIZE);
            } else {
                int index = sizeAttributecombo.getSelectionIndex();
                String field = sizeAttributecombo.getItem(index);
                if (field.length() == 0) {
                    return;
                }
                notifyListeners(field, true, STYLEEVENTTYPE.SIZE);
            }
        } else if (source.equals(rotationSpinner) || source.equals(rotationAttributecombo)) {
            boolean comboIsNone = comboIsNone(rotationAttributecombo);
            if (comboIsNone) {
                int rotationInt = rotationSpinner.getSelection();
                String rotation = String.valueOf(rotationInt);
                notifyListeners(rotation, false, STYLEEVENTTYPE.ROTATION);
            } else {
                int index = rotationAttributecombo.getSelectionIndex();
                String field = rotationAttributecombo.getItem(index);
                if (field.length() == 0) {
                    return;
                }
                notifyListeners(field, true, STYLEEVENTTYPE.ROTATION);
            }
        } else if (source.equals(xOffsetSpinner) || source.equals(yOffsetSpinner)) {
            double x = Utilities.getDoubleSpinnerSelection(xOffsetSpinner);
            double y = Utilities.getDoubleSpinnerSelection(yOffsetSpinner);

            String offsetStr = x + "," + y; //$NON-NLS-1$
            notifyListeners(offsetStr, false, STYLEEVENTTYPE.OFFSET);
        }
        checkEnablements();
    }

    private void checkEnablements() {
        boolean comboIsNone = comboIsNone(rotationAttributecombo);
        rotationSpinner.setEnabled(comboIsNone);
        comboIsNone = comboIsNone(sizeAttributecombo);
        sizeSpinner.setEnabled(comboIsNone);
    }

    public void keyPressed( KeyEvent e ) {
    }

    public void keyReleased( KeyEvent e ) {
        Object source = e.getSource();
        if (source.equals(maxScaleText)) {
            String maxScale = maxScaleText.getText();
            notifyListeners(maxScale, false, STYLEEVENTTYPE.MAXSCALE);
        } else if (source.equals(minScaleText)) {
            String minScale = minScaleText.getText();
            notifyListeners(minScale, false, STYLEEVENTTYPE.MINSCALE);
        }
    }

    public void focusGained( FocusEvent e ) {
    }

    public void focusLost( FocusEvent e ) {
        Object source = e.getSource();
        if (source.equals(nameText)) {
            String text = nameText.getText();
            notifyListeners(text, false, STYLEEVENTTYPE.NAME);
        }
    }

}
