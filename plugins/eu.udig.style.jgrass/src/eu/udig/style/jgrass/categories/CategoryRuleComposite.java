/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
 package eu.udig.style.jgrass.categories;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A composite that represents a facility to create JGrass category rules
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class CategoryRuleComposite implements SelectionListener, KeyListener {

    private Label valueLabel;
    private Label labelLabel;
    private Button enableRuleCheckButton;
    private Text valueText;
    private Text labelText;
    private boolean isActive = true;
    private CategoryRule rule = null;

    public CategoryRuleComposite( Composite parent, int style, CategoryRule rule ) {

        GridData gridData10 = new GridData();
        gridData10.horizontalAlignment = GridData.FILL;
        gridData10.grabExcessHorizontalSpace = true;
        gridData10.verticalAlignment = GridData.CENTER;
        GridData gridData7 = new GridData();
        gridData7.horizontalAlignment = GridData.FILL;
        gridData7.grabExcessHorizontalSpace = true;
        gridData7.verticalAlignment = GridData.CENTER;
        GridData gridData11 = new GridData();
        gridData11.horizontalAlignment = GridData.END;
        gridData11.verticalAlignment = GridData.CENTER;
        GridData gridData9 = new GridData();
        gridData9.horizontalAlignment = GridData.FILL;
        gridData9.grabExcessHorizontalSpace = true;
        gridData9.verticalAlignment = GridData.FILL;
        GridData gridData8 = new GridData();
        gridData8.horizontalAlignment = GridData.CENTER;
        gridData8.grabExcessHorizontalSpace = true;
        gridData8.verticalAlignment = GridData.CENTER;
        GridData gridData6 = new GridData();
        gridData6.horizontalAlignment = GridData.FILL;
        gridData6.grabExcessHorizontalSpace = true;
        gridData6.grabExcessVerticalSpace = false;
        gridData6.verticalAlignment = GridData.FILL;
        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 6;
        // gridLayout1.marginWidth = 5;
        // gridLayout1.marginHeight = 5;
        // gridLayout1.verticalSpacing = 5;
        gridLayout1.makeColumnsEqualWidth = false;
        GridData gridData5 = new GridData();
        gridData5.grabExcessHorizontalSpace = true;
        gridData5.verticalAlignment = GridData.BEGINNING;
        gridData5.horizontalAlignment = GridData.FILL;
        parent.setLayoutData(gridData5);
        parent.setLayout(gridLayout1);
        valueLabel = new Label(parent, SWT.NONE);
        valueLabel.setText("value");
        valueLabel.setLayoutData(gridData6);
        valueText = new Text(parent, SWT.BORDER);
        valueText.setLayoutData(gridData7);
        Label dummylabel = new Label(parent, SWT.NONE);
        dummylabel.setText("-");
        dummylabel.setLayoutData(gridData8);
        labelLabel = new Label(parent, SWT.NONE);
        labelLabel.setText("label");
        labelLabel.setLayoutData(gridData9);
        labelText = new Text(parent, SWT.BORDER);
        labelText.setLayoutData(gridData10);
        enableRuleCheckButton = new Button(parent, SWT.CHECK);
        enableRuleCheckButton.setLayoutData(gridData11);
        enableRuleCheckButton.addSelectionListener(this);

        valueText.addKeyListener(this);
        labelText.addKeyListener(this);

        /*
         * set values if needed
         */
        if (rule == null) {
            this.rule = new CategoryRule();
        } else {
            this.rule = rule;
        }
        enableRuleCheckButton.setSelection(this.rule.isActive());

        valueText.setText(this.rule.getValue());
        labelText.setText(this.rule.getLabel());

    }

    /**
     * @return the rule that created the widget and knows its last state
     */
    public CategoryRule getRule() {
        return rule;
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
        try {
            if (o instanceof Text) {
                rule.setLabel(labelText.getText());
                rule.setValue(valueText.getText());
            }
        } catch (NumberFormatException ne) {
            return;
        }

    }
}
