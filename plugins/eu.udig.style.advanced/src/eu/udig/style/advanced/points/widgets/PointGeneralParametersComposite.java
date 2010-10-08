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

import eu.udig.style.advanced.common.ParameterComposite;
import eu.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import eu.udig.style.advanced.common.styleattributeclasses.PointSymbolizerWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.RuleWrapper;

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
    private Text offsetText;
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
        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(PointSymbolizerWrapper.class);
        
        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(3, true));

        // rule name
        Label nameLabel = new Label(mainComposite, SWT.NONE);
        nameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        nameLabel.setText("Rule name");
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
        valueLabel.setText("Manual");
        Label fieldsLabel = new Label(mainComposite, SWT.NONE);
        fieldsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        fieldsLabel.setText("Field based");

        // size
        Label sizeLabel = new Label(mainComposite, SWT.NONE);
        sizeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sizeLabel.setText("size");
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
        rotationLabel.setText("rotation");
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

        Label offsetLabel = new Label(mainComposite, SWT.NONE);
        GridData offsetLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        offsetLabel.setLayoutData(offsetLabelGD);
        offsetLabel.setText("offset (x, y)");
        offsetText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData offsetSIMPLEGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        offsetSIMPLEGD.horizontalSpan = 2;
        offsetText.setLayoutData(offsetSIMPLEGD);
        
        String xOffset = pointSymbolizerWrapper.getxOffset();
        String yOffset = pointSymbolizerWrapper.getyOffset();
        Double tmpXOffsetD = isDouble(xOffset);
        Double tmpYOffsetD = isDouble(yOffset);
        if (tmpXOffsetD == null || tmpYOffsetD == null) {
            tmpXOffsetD = 0.0;
            tmpYOffsetD = 0.0;
        }
        offsetText.setText(tmpXOffsetD + ", " + tmpYOffsetD);
        offsetText.addKeyListener(this);

        Label maxScaleLabel = new Label(mainComposite, SWT.NONE);
        GridData maxScaleLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        maxScaleLabel.setLayoutData(maxScaleLabelGD);
        maxScaleLabel.setText("maximum scale");
        maxScaleText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData maxScaleTextSIMPLEGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        maxScaleTextSIMPLEGD.horizontalSpan = 2;
        maxScaleText.setLayoutData(maxScaleTextSIMPLEGD);
        maxScaleText.setText(ruleWrapper.getMaxScale());

        Label minScaleLabel = new Label(mainComposite, SWT.NONE);
        GridData minScaleLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        minScaleLabel.setLayoutData(minScaleLabelGD);
        minScaleLabel.setText("minimum scale");
        minScaleText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData mainScaleTextSIMPLEGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        mainScaleTextSIMPLEGD.horizontalSpan = 2;
        minScaleText.setLayoutData(mainScaleTextSIMPLEGD);
        minScaleText.setText(ruleWrapper.getMinScale());
    }

    public void update( RuleWrapper ruleWrapper ) {
        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(PointSymbolizerWrapper.class);
        
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
        Double tmpXOffsetD = isDouble(xOffset);
        Double tmpYOffsetD = isDouble(yOffset);
        if (tmpXOffsetD == null || tmpYOffsetD == null) {
            tmpXOffsetD = 0.0;
            tmpYOffsetD = 0.0;
        }
        offsetText.setText(tmpXOffsetD + ", " + tmpYOffsetD);

        // scale
        Double maxScaleDouble = isDouble(ruleWrapper.getMaxScale());
        if (maxScaleDouble == null) {
            maxScaleDouble = 0.0;
        }
        maxScaleText.setText(String.valueOf(maxScaleDouble));
        Double minScaleDouble = isDouble(ruleWrapper.getMinScale());
        if (minScaleDouble == null) {
            minScaleDouble = 0.0;
        }
        minScaleText.setText(String.valueOf(minScaleDouble));
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(sizeSpinner)) {
            int sizeInt = sizeSpinner.getSelection();
            String size = String.valueOf(sizeInt);
            notifyListeners(size, false, STYLEEVENTTYPE.SIZE);
        } else if (source.equals(sizeAttributecombo)) {
            int index = sizeAttributecombo.getSelectionIndex();
            String field = sizeAttributecombo.getItem(index);
            if (field.length() == 0) {
                return;
            }
            notifyListeners(field, true, STYLEEVENTTYPE.SIZE);
        } else if (source.equals(rotationSpinner)) {
            int rotationInt = rotationSpinner.getSelection();
            String rotation = String.valueOf(rotationInt);
            notifyListeners(rotation, false, STYLEEVENTTYPE.ROTATION);
        } else if (source.equals(rotationAttributecombo)) {
            int index = rotationAttributecombo.getSelectionIndex();
            String field = rotationAttributecombo.getItem(index);
            if (field.length() == 0) {
                return;
            }
            notifyListeners(field, true, STYLEEVENTTYPE.ROTATION);
        } else if (source.equals(offsetText)) {
            String offsetStr = offsetText.getText();
            notifyListeners(offsetStr, false, STYLEEVENTTYPE.OFFSET);
        }
    }

    public void keyPressed( KeyEvent e ) {
    }

    public void keyReleased( KeyEvent e ) {
        Object source = e.getSource();
        if (source.equals(offsetText)) {
            String offsetStr = offsetText.getText();
            notifyListeners(offsetStr, false, STYLEEVENTTYPE.OFFSET);
        } else if (source.equals(maxScaleText)) {
            String maxScale = maxScaleText.getText();
            notifyListeners(maxScale, false, STYLEEVENTTYPE.MAXSCALE);
        } else if (source.equals(minScaleText)) {
            String maxScale = minScaleText.getText();
            notifyListeners(maxScale, false, STYLEEVENTTYPE.MINSCALE);
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
