/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.polygons.widgets;

import static org.locationtech.udig.style.advanced.utils.Utilities.ff;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.locationtech.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import org.locationtech.udig.style.advanced.common.ParameterComposite;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.PolygonSymbolizerWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import org.locationtech.udig.style.advanced.internal.Messages;
import org.locationtech.udig.style.advanced.utils.Utilities;
import org.locationtech.udig.ui.ColorEditor;
import org.opengis.filter.expression.Expression;

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
    private ColorEditor fillColorEditor;
    private Button fillColorButton;
    private Spinner fillOpacitySpinner;
    private Combo fillOpacityAttributecombo;
    private Combo wkmarkNameCombo;
    private Spinner wkmWidthSpinner;
    private ColorEditor wkmColorEditor;
    private Text graphicsPathText;
    private Button graphicsPathButton;
    private Button wkmColorButton;
    private Spinner wkmSizeSpinner;
    private Spinner graphicssizeSpinner;
    private final String[] stringAttributesArrays;
    private Combo fillColorAttributecombo;

    private Button colorLabel;
    private Button graphicsFillLabel;
    private Button wkmLabel; 
    
    public PolygonFillParametersComposite( Composite parent, String[] numericAttributesArrays, String[] stringAttributesArrays ) {
        this.parent = parent;
        this.numericAttributesArrays = numericAttributesArrays;
        this.stringAttributesArrays = stringAttributesArrays;
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
        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(3, true));

        fillEnableButton = new Button(mainComposite, SWT.CHECK);
        GridData fillEnableButtonGD = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        fillEnableButtonGD.horizontalSpan = 3;
        fillEnableButton.setLayoutData(fillEnableButtonGD);
        fillEnableButton.setText(Messages.PolygonFillParametersComposite_0);
        fillEnableButton.addSelectionListener(this);

        /*
         * radio panel for fill choice
         */
        // fill color
        colorLabel = new Button(mainComposite, SWT.RADIO);
        colorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        colorLabel.setText(Messages.PolygonFillParametersComposite_1);
        colorLabel.addSelectionListener(this);
        
        fillColorEditor = new ColorEditor(mainComposite);
        
        fillColorButton = fillColorEditor.getButton();
        GridData fillColorButtonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        fillColorButton.setLayoutData(fillColorButtonGD);
        fillColorButton.addSelectionListener(this);
        
        fillColorAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        fillColorAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fillColorAttributecombo.setItems(stringAttributesArrays);
        fillColorAttributecombo.addSelectionListener(this);
        fillColorAttributecombo.select(0);

       

        // graphics fill
        graphicsFillLabel = new Button(mainComposite, SWT.RADIO);
        graphicsFillLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        graphicsFillLabel.setText(Messages.PolygonFillParametersComposite_2);
        graphicsFillLabel.addSelectionListener(this);
        
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
        graphicsPathText.setText(""); //$NON-NLS-1$
        
        graphicsPathText.addModifyListener(this);
        graphicsPathButton = new Button(pathComposite, SWT.PUSH);
        graphicsPathButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        graphicsPathButton.setText("..."); //$NON-NLS-1$
        graphicsPathButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
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
        
        new Label(mainComposite, SWT.NONE);
        // mark size
        graphicssizeSpinner = new Spinner(mainComposite, SWT.BORDER);
        graphicssizeSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        graphicssizeSpinner.setMaximum(1000);
        graphicssizeSpinner.setMinimum(0);
        graphicssizeSpinner.setIncrement(1);
        graphicssizeSpinner.setToolTipText(Messages.PolygonFillParametersComposite_5);
        graphicssizeSpinner.setDigits(1);
        graphicssizeSpinner.addSelectionListener(this);
        new Label(mainComposite, SWT.NONE);
        

        // well known marks
        wkmLabel = new Button(mainComposite, SWT.RADIO);
        wkmLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        wkmLabel.setText(Messages.PolygonFillParametersComposite_3);
        wkmLabel.addSelectionListener(this);
        
        wkmarkNameCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData wkmarkNameComboGD = new GridData(SWT.FILL, SWT.FILL, false, false);
        wkmarkNameComboGD.horizontalSpan = 2;
        wkmarkNameCombo.setLayoutData(wkmarkNameComboGD);
        wkmarkNameCombo.setItems(Utilities.getAllMarksArray());
        wkmarkNameCombo.addSelectionListener(this);

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
        wkmWidthSpinner.setToolTipText(Messages.PolygonFillParametersComposite_4);
        wkmWidthSpinner.setDigits(1);
        wkmWidthSpinner.addSelectionListener(this);

        // mark size
        wkmSizeSpinner = new Spinner(wkmarkComposite, SWT.BORDER);
        wkmSizeSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        wkmSizeSpinner.setMaximum(1000);
        wkmSizeSpinner.setMinimum(0);
        wkmSizeSpinner.setIncrement(1);
        wkmSizeSpinner.setToolTipText(Messages.PolygonFillParametersComposite_5);
        wkmSizeSpinner.setDigits(1);
        wkmSizeSpinner.addSelectionListener(this);

        wkmColorEditor = new ColorEditor(wkmarkComposite);
        wkmColorButton = wkmColorEditor.getButton();
        wkmColorButton.addSelectionListener(this);
        GridData wkmColorButtonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        wkmColorButton.setLayoutData(wkmColorButtonGD);

        // header
        new Label(mainComposite, SWT.NONE);
        Label valueLabel = new Label(mainComposite, SWT.NONE);
        valueLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        valueLabel.setText(Messages.PolygonFillParametersComposite_6);
        Label fieldsLabel = new Label(mainComposite, SWT.NONE);
        fieldsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        fieldsLabel.setText(Messages.PolygonFillParametersComposite_7);

        // border alpha
        Label fillOpactityLabel = new Label(mainComposite, SWT.NONE);
        fillOpactityLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fillOpactityLabel.setText(Messages.PolygonFillParametersComposite_8);
        fillOpacitySpinner = new Spinner(mainComposite, SWT.BORDER);
        fillOpacitySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fillOpacitySpinner.setMaximum(100);
        fillOpacitySpinner.setMinimum(0);
        fillOpacitySpinner.setIncrement(10);

        fillOpacitySpinner.addSelectionListener(this);
        fillOpacityAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        fillOpacityAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fillOpacityAttributecombo.setItems(numericAttributesArrays);
        fillOpacityAttributecombo.addSelectionListener(this);
        fillOpacityAttributecombo.select(0);

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

        colorLabel.setSelection(true);
        wkmLabel.setSelection(false);
        graphicsFillLabel.setSelection(false);
        
        String color = polygonSymbolizerWrapper.getFillColor();
        Color tmpColor = null;
        try {
            tmpColor = Color.decode(color);
        } catch (Exception e) {
            // ignore and try for field
        }
        if (tmpColor != null) {
        	colorLabel.setSelection(true);
            fillColorEditor.setColor(tmpColor);
        } else {
            int index = getAttributeIndex(color, stringAttributesArrays);
            if (index != -1) {
            	colorLabel.setSelection(true);
                fillColorAttributecombo.select(index);
            }
        }

        // graphics path
        try {
        	String text = polygonSymbolizerWrapper.getFillExternalGraphicFillPath();
        	
        	Double tmpsize = null;
        	if (polygonSymbolizerWrapper.getFillGraphicFill() != null &&
        			polygonSymbolizerWrapper.getFillGraphicFill().getSize() != null){
        		try{
        			tmpsize = (Double) polygonSymbolizerWrapper.getFillGraphicFill().getSize().evaluate(null, Double.class);
        		}catch (Exception ex){
        			ex.printStackTrace();
        		}
        		
        	}
        	
        	if (text != null && text.length() > 0){
        		graphicsPathText.setText(text);
        		graphicsFillLabel.setSelection(true);
        		colorLabel.setSelection(false);
        		wkmLabel.setSelection(false);
        	}
        	int size = 16;
            if (tmpsize != null) {
            	 size = tmpsize.intValue();
            }
            graphicssizeSpinner.setSelection(size * 10);
             
             
        } catch (MalformedURLException e1) {
            graphicsPathText.setText(""); //$NON-NLS-1$
        }

        String wkMarkNameFill = polygonSymbolizerWrapper.getWkMarkNameFill();
        String mName = Utilities.markNamesToDef.inverse().get(wkMarkNameFill);
        int attributeIndex = getAttributeIndex(mName, Utilities.getAllMarksArray());
        if (attributeIndex != -1) {
            wkmarkNameCombo.select(attributeIndex);
            wkmLabel.setSelection(true);
            colorLabel.setSelection(false);
            graphicsFillLabel.setSelection(false);
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
        int tmpWkmSizeInt = 10;
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
    	boolean enabled = fillEnableButton.getSelection();
    	for (Control c : mainComposite.getChildren()){
         	if (c != fillEnableButton){
         		c.setEnabled(enabled);
         	}
         }
    	if (!fillEnableButton.getSelection()){
    		 return;    		
    	}
    	
        if (colorLabel.getSelection()){
        	fillColorEditor.setEnabled(comboIsNone(fillColorAttributecombo));
        	fillColorAttributecombo.setEnabled(true);
        	graphicsPathText.setEnabled(false);
        	graphicsPathButton.setEnabled(false);
        	graphicssizeSpinner.setEnabled(false);
         	wkmarkNameCombo.setEnabled(false);
        	wkmColorButton.setEnabled(false);
        	wkmSizeSpinner.setEnabled(false);
        	wkmWidthSpinner.setEnabled(false);
        	
        	boolean comboIsNone = comboIsNone(fillColorAttributecombo);
        	fillColorEditor.setEnabled(comboIsNone);
        	
        }else if (graphicsFillLabel.getSelection()){
        	fillColorEditor.setEnabled(false);
        	fillColorAttributecombo.setEnabled(false);
        	graphicsPathText.setEnabled(true);
        	graphicsPathButton.setEnabled(true);
        	graphicssizeSpinner.setEnabled(true);
        	wkmarkNameCombo.setEnabled(false);
        	wkmColorButton.setEnabled(false);
        	wkmSizeSpinner.setEnabled(false);
        	wkmWidthSpinner.setEnabled(false);
        }else if (wkmLabel.getSelection()){
        	fillColorEditor.setEnabled(false);
        	fillColorAttributecombo.setEnabled(false);
        	graphicsPathText.setEnabled(false);
        	graphicsPathButton.setEnabled(false);
        	graphicssizeSpinner.setEnabled(false);
        	wkmarkNameCombo.setEnabled(true);
        	wkmColorButton.setEnabled(true);
        	wkmSizeSpinner.setEnabled(true);
        	wkmWidthSpinner.setEnabled(true);
        }
                	
        boolean comboIsNone = comboIsNone(fillOpacityAttributecombo);
        fillOpacitySpinner.setEnabled(comboIsNone);
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(fillEnableButton)) {
            boolean selected = fillEnableButton.getSelection();
            notifyListeners(String.valueOf(selected), false, STYLEEVENTTYPE.FILLENABLE);
            
            //set enabled state of children
            for (Control c : mainComposite.getChildren()){
            	if (c != fillEnableButton){
            		c.setEnabled(selected);
            	}
            }
            if (colorLabel.getSelection()){

            	colorLabel.setSelection(true);
            }else if (wkmLabel.getSelection()){
            	wkmLabel.setSelection(true);
            }else if (graphicsFillLabel.getSelection()){
            	graphicsFillLabel.setSelection(true);
            }
            checkEnablements();
            
        } else if (source.equals(fillColorButton) || source.equals(fillColorAttributecombo) || ( source.equals(colorLabel) && colorLabel.getSelection() ) ) {
            boolean comboIsNone = comboIsNone(fillColorAttributecombo);
            if (comboIsNone) {
                Color color = fillColorEditor.getColor();
                Expression colorExpr = ff.literal(color);
                String fillColor = colorExpr.evaluate(null, String.class);
                notifyListeners(fillColor, false, STYLEEVENTTYPE.FILLCOLOR);
            } else {
                int index = fillColorAttributecombo.getSelectionIndex();
                String field = fillColorAttributecombo.getItem(index);
                if (field.length() == 0) {
                    return;
                }
                notifyListeners(field, true, STYLEEVENTTYPE.FILLCOLOR);
            }
        } else if (source.equals(fillOpacitySpinner) || source.equals(fillOpacityAttributecombo) ) {
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
                || source.equals(wkmSizeSpinner) || (source.equals(wkmLabel) && wkmLabel.getSelection())) {
            doWkmGraphics();
        }else if ((source.equals(graphicsFillLabel) && graphicsFillLabel.getSelection()) || source.equals(graphicssizeSpinner)){
        	String text = graphicsPathText.getText();
            File graphicsFile = new File(text);
            try {
            	if (graphicsFile.exists() || text.toLowerCase().startsWith("http")) { //$NON-NLS-1$
            		text = graphicsFile.toURI().toURL().toExternalForm();
            	}
            }catch (Exception ex){
            	ex.printStackTrace();
            }
            notifyListeners(new String[]{text, getGraphicsSize()}, false, STYLEEVENTTYPE.GRAPHICSPATHFILL);
        }
        	

        checkEnablements();
    }
    
    private String getGraphicsSize(){
        int selection = graphicssizeSpinner.getSelection();
        int digits = graphicssizeSpinner.getDigits();
        double value = selection / Math.pow(10, digits);
        String size = String.valueOf(value);
        return size;
    }
    
    public void modifyText( ModifyEvent e ) {
        Object source = e.getSource();
        if (source.equals(graphicsPathText)) {
            try {
                String text = graphicsPathText.getText();
                File graphicsFile = new File(text);
                if (graphicsFile.exists() || text.toLowerCase().startsWith("http")) { //$NON-NLS-1$
                    text = graphicsFile.toURI().toURL().toExternalForm();
                    notifyListeners(new String[]{text, getGraphicsSize()}  , false, STYLEEVENTTYPE.GRAPHICSPATHFILL);
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void doWkmGraphics() {
        int selectionIndex = wkmarkNameCombo.getSelectionIndex();
        String name = wkmarkNameCombo.getItem(selectionIndex);
        if (name.equals("")) { //$NON-NLS-1$
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
