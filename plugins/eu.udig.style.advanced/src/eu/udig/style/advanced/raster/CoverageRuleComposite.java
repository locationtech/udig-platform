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
package eu.udig.style.advanced.raster;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A composite that represents a facility to create color rules
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class CoverageRuleComposite implements MouseListener, SelectionListener, KeyListener {

    private Label fromLabel;
    private Label toLabel;
    private Button enableRuleCheckButton;
    private Text fromValueText;
    private Text toValueText;
    private boolean isActive = true;
    private CoverageRule rule = null;
    private Composite parent = null;
    private Text alphaText;

    public CoverageRuleComposite( Composite parent, int style, CoverageRule rule ) {
        this.parent = parent;

        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 7;
        gridLayout1.makeColumnsEqualWidth = false;

        GridData gridData5 = new GridData();
        gridData5.grabExcessHorizontalSpace = true;
        gridData5.verticalAlignment = GridData.BEGINNING;
        gridData5.horizontalAlignment = GridData.FILL;
        parent.setLayoutData(gridData5);
        parent.setLayout(gridLayout1);

        int colorLabelWidth = 40;
        int textWidth = 130;
        int alphaTextWidth = 40;

        GridData fromLabelGD = new GridData();
        fromLabelGD.horizontalAlignment = GridData.FILL;
        fromLabelGD.verticalAlignment = GridData.FILL;
        fromLabelGD.widthHint = colorLabelWidth;
        fromLabel = new Label(parent, SWT.BORDER);
        fromLabel.setText("");
        fromLabel.setLayoutData(fromLabelGD);
        fromLabel.addMouseListener(this);

        GridData fromValueTextGD = new GridData();
        fromValueTextGD.horizontalAlignment = GridData.FILL;
        fromValueTextGD.verticalAlignment = GridData.CENTER;
        fromValueTextGD.widthHint = textWidth;
        fromValueText = new Text(parent, SWT.BORDER);
        fromValueText.setLayoutData(fromValueTextGD);

        GridData dummyLabelGD = new GridData();
        dummyLabelGD.horizontalAlignment = GridData.CENTER;
        dummyLabelGD.verticalAlignment = GridData.CENTER;
        dummyLabelGD.widthHint = 5;
        Label dummylabel = new Label(parent, SWT.NONE);
        dummylabel.setText("-");
        dummylabel.setLayoutData(dummyLabelGD);

        GridData toLabelGD = new GridData();
        toLabelGD.horizontalAlignment = GridData.FILL;
        toLabelGD.verticalAlignment = GridData.FILL;
        toLabelGD.widthHint = colorLabelWidth;
        toLabel = new Label(parent, SWT.BORDER);
        toLabel.setText("");
        toLabel.setLayoutData(toLabelGD);
        toLabel.addMouseListener(this);

        GridData toValueTextGD = new GridData();
        toValueTextGD.horizontalAlignment = GridData.FILL;
        toValueTextGD.verticalAlignment = GridData.CENTER;
        toValueTextGD.widthHint = textWidth;
        toValueText = new Text(parent, SWT.BORDER);
        toValueText.setLayoutData(toValueTextGD);

        GridData enableRuleCheckButtonGD = new GridData();
        enableRuleCheckButtonGD.horizontalAlignment = GridData.END;
        enableRuleCheckButtonGD.verticalAlignment = GridData.CENTER;
        enableRuleCheckButton = new Button(parent, SWT.CHECK);
        enableRuleCheckButton.setLayoutData(enableRuleCheckButtonGD);
        enableRuleCheckButton.addSelectionListener(this);

        GridData alphaGD = new GridData();
        alphaGD.horizontalAlignment = GridData.FILL;
        alphaGD.verticalAlignment = GridData.CENTER;
        alphaGD.widthHint = alphaTextWidth;
        alphaText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        alphaText.setLayoutData(alphaGD);
        alphaText.setText("1.0");

        alphaText.addKeyListener(this);
        fromValueText.addKeyListener(this);
        toValueText.addKeyListener(this);

        /*
         * set values if needed
         */
        if (rule == null) {
            this.rule = new CoverageRule();
        } else {
            this.rule = rule;
        }
        enableRuleCheckButton.setSelection(this.rule.isActive());
        String value = null;
        if (this.rule.getFromToValues()[0] != this.rule.getFromToValues()[0]) {
            value = "";
        } else {
            value = String.valueOf(this.rule.getFromToValues()[0]);
        }
        fromValueText.setText(value);
        if (this.rule.getFromToValues()[1] != this.rule.getFromToValues()[1]) {
            value = "";
        } else {
            value = String.valueOf(this.rule.getFromToValues()[1]);
        }
        toValueText.setText(value);
        fromLabel.setBackground(this.rule.getFromColor());
        toLabel.setBackground(this.rule.getToColor());

        alphaText.setText(String.valueOf(this.rule.getOpacity()));
    }

    /**
     * @return the rule that created the widget and knows its last state
     */
    public CoverageRule getRule() {
        return rule;
    }

    public void mouseDoubleClick( MouseEvent e ) {
    }

    public void mouseDown( MouseEvent e ) {
        Object o = e.getSource();
        if (o instanceof Label) {
            Label l = (Label) o;
            ColorDialog c = new ColorDialog(parent.getShell());
            c.setRGB(new RGB(l.getBackground().getRed(), l.getBackground().getGreen(), l.getBackground().getBlue()));
            RGB color = c.open();
            if (color == null) {
                return;
            }
            Color colorObject = new Color(parent.getDisplay(), color);

            ((Label) o).setBackground(colorObject);
            if (o.equals(fromLabel)) {
                rule.setFromColor(colorObject);
            } else if (o.equals(toLabel)) {
                rule.setToColor(colorObject);
            }

        }
    }

    public void mouseUp( MouseEvent e ) {
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
    }

    public void widgetSelected( SelectionEvent e ) {
        Button b = (Button) e.getSource();
        if (b.equals(enableRuleCheckButton)) {
            isActive = b.getSelection();
            rule.setActive(isActive);
        }
    }

    public void keyPressed( KeyEvent e ) {

    }

    public void keyReleased( KeyEvent e ) {
        Object o = e.widget;
        if (o instanceof Text) {
            double[] tmp = rule.getFromToValues();
            try {
                tmp[0] = Double.parseDouble(fromValueText.getText());
            } catch (NumberFormatException ne) {
                fromValueText.setText("0");
                return;
            }
            try {
                tmp[1] = Double.parseDouble(toValueText.getText());
            } catch (NumberFormatException ne) {
                toValueText.setText("0");
                return;
            }
            rule.setFromToValues(tmp);
            try {
                double alpha = Double.parseDouble(alphaText.getText());
                rule.setOpacity(alpha);
            } catch (NumberFormatException ne) {
                alphaText.setText("1.0");
                return;
            }
        }

    }
}
