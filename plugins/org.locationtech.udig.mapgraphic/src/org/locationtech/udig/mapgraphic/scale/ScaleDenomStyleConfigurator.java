/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.scale;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.internal.Messages;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.style.IStyleConfigurator;
import org.locationtech.udig.ui.ColorEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A style configurator for the scale denom style.
 * 
 * @author Emily
 * @since 1.1.0
 */
public class ScaleDenomStyleConfigurator extends IStyleConfigurator implements SelectionListener {

	private Button btnCheck;
	private ColorEditor btnColor;
	private Text labelText;
	
	private ScaleDenomStyle style;
	
    @Override
    public boolean canStyle( Layer layer ) {
        return layer.hasResource(MapGraphic.class)
                && layer.getStyleBlackboard().contains(ScaleDenomStyleContent.ID);
    }

    @Override
    public void createControl( Composite parent ) {
        GridLayout gridLayout = new GridLayout(3, false);
        parent.setLayout(gridLayout);

        //label prefix part
        Label labelLabel = new Label(parent, SWT.NONE);
        labelLabel.setText(Messages.ScaleDenomStyleConfigurator_labelPrefix); 
        
        labelText = new Text(parent, SWT.BORDER);
        labelText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
        
        Label l = new Label(parent, SWT.NONE);
        l.setText(Messages.ScaleDenomStyleConfigurator_DrawBackground);
        
        btnCheck = new Button(parent, SWT.CHECK);
        btnCheck.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

        btnColor = new ColorEditor(parent);
        btnColor.getButton().setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
        
        btnCheck.addSelectionListener(this);
        btnColor.getButton().addSelectionListener(this);
    }
    
    
    @Override
    public void preApply() {
        style = (ScaleDenomStyle) getStyleBlackboard().get(ScaleDenomStyleContent.ID);
        style.setLabel(labelText.getText());
    }
	
	
    @Override
    protected void refresh() {
        style = (ScaleDenomStyle) getStyleBlackboard().get(ScaleDenomStyleContent.ID);
        if (style == null){
        	style = new ScaleDenomStyle();
        }
        
        labelText.setText(style.getLabel());
        btnCheck.setSelection(style.getColor() != null);
        if (style.getColor() != null){
        	btnColor.setColor(style.getColor());
        }
    }
    
    
    @Override
	public void widgetSelected(SelectionEvent e) {
		btnColor.setEnabled(btnCheck.getSelection());
		
		if (btnCheck.getSelection()){
			style.setColor(btnColor.getColor());
		}else{
			style.setColor(null);
		}
		
		getStyleBlackboard().put(ScaleDenomStyleContent.ID, style);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

}
