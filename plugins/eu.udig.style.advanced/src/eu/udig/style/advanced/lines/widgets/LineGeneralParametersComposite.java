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
package eu.udig.style.advanced.lines.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.udig.style.advanced.common.ParameterComposite;
import eu.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import eu.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.SymbolizerWrapper;

/**
 * A composite that holds widgets for polygon general parameter setting.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class LineGeneralParametersComposite extends ParameterComposite {

    private final Composite parent;
    private final String[] numericAttributesArrays;

    private Text nameText;
    private Text offsetText;
    private Text maxScaleText;
    private Text minScaleText;

    private Composite mainComposite;

    public LineGeneralParametersComposite( Composite parent, String[] numericAttributesArrays ) {
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
        SymbolizerWrapper symbolizersWrapper = ruleWrapper.getGeometrySymbolizersWrapper();

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

        Label offsetLabel = new Label(mainComposite, SWT.NONE);
        GridData offsetLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        offsetLabel.setLayoutData(offsetLabelGD);
        offsetLabel.setText("offset (x, y)");
        offsetText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData offsetSIMPLEGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        offsetSIMPLEGD.horizontalSpan = 2;
        offsetText.setLayoutData(offsetSIMPLEGD);

        String xOffset = symbolizersWrapper.getxOffset();
        String yOffset = symbolizersWrapper.getyOffset();
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

    /**
     * Update the panel.
     * 
     * @param ruleWrapper the {@link RuleWrapper}.
     */
    public void update( RuleWrapper ruleWrapper ) {
        SymbolizerWrapper symbolizersWrapper = ruleWrapper.getGeometrySymbolizersWrapper();

        nameText.setText(ruleWrapper.getName());

        // offset
        String xOffset = symbolizersWrapper.getxOffset();
        String yOffset = symbolizersWrapper.getyOffset();
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
        if (source.equals(offsetText)) {
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
