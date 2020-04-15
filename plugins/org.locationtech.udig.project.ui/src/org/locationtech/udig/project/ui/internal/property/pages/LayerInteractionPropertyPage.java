/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.property.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.geometry.jts.Geometries;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.internal.Messages;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;

/**
 * Preference page for controlling Layer interaction with user.
 * 
 * @author pfeiffp
 */
public class LayerInteractionPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

    private Button visibleButton;
    private Button layerButton;
    private Button informationButton;
    private Button selectButton;
    private Button editButton;
    private Button backgroundButton;
    private Button aoiButton;
    private Layer layer;
    private boolean isPolygon = false;
    private boolean isRaster = false;
    
    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents( Composite parent ) {
        layer = (Layer) getElement();

        SimpleFeatureType schema = layer.getSchema();

        // check if layer is polygon
        if (schema != null) {
            GeometryDescriptor geomDescriptor = schema.getGeometryDescriptor();
            if (geomDescriptor != null) {
                Class< ? extends Geometry> binding = (Class< ? extends Geometry>) geomDescriptor
                        .getType().getBinding();
                switch( Geometries.getForBinding(binding) ) {
                case MULTIPOLYGON:
                case POLYGON:
                    isPolygon = true;
                    break;
                default:
                }
            }
        }
        // check if raster layer
        else {
            if (layer.canAdaptTo(AbstractGridCoverage2DReader.class)) {
                isRaster = true;
            }
        }

        Composite interactionPage = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        interactionPage.setLayout(layout);

        Group generalGroup = new Group(interactionPage, SWT.SHADOW_ETCHED_IN);
        generalGroup.setText(Messages.LayerInteraction_General);

        visibleButton = new Button(generalGroup, SWT.CHECK);
        visibleButton.setText(Messages.LayerInteraction_Visible);
        visibleButton.setLocation(40, 20);
        visibleButton.pack();
        visibleButton.addSelectionListener(defaultSelectionListener());

        Group toolsGroup = new Group(interactionPage, SWT.SHADOW_ETCHED_IN);
        toolsGroup.setText(Messages.LayerInteraction_Tools);

        layerButton = new Button(toolsGroup, SWT.RADIO);
        layerButton.setText(Messages.LayerInteraction_Layer);
        layerButton.setLocation(20, 20);
        layerButton.pack();
        layerButton.addSelectionListener(new SelectionListener(){
            public void widgetSelected( SelectionEvent event ) {
                setBackgroundLayer(!layerButton.getSelection());
                setApplyButton();
            }
            public void widgetDefaultSelected( SelectionEvent event ) {
            }
        });

        informationButton = new Button(toolsGroup, SWT.CHECK);
        informationButton.setText(Messages.LayerInteraction_Information);
        informationButton.setLocation(40, 40);
        informationButton.pack();
        informationButton.addSelectionListener(defaultSelectionListener());

        selectButton = new Button(toolsGroup, SWT.CHECK);
        selectButton.setText(Messages.LayerInteraction_Select);
        selectButton.setLocation(40, 60);
        selectButton.pack();
        selectButton.addSelectionListener(defaultSelectionListener());

        editButton = new Button(toolsGroup, SWT.CHECK);
        editButton.setText(Messages.LayerInteraction_Edit);
        editButton.setLocation(40, 80);
        editButton.pack();
        editButton.addSelectionListener(defaultSelectionListener());

        backgroundButton = new Button(toolsGroup, SWT.RADIO);
        backgroundButton.setText(Messages.LayerInteraction_Background);
        backgroundButton.setLocation(20, 100);
        backgroundButton.pack();
        backgroundButton.addSelectionListener(new SelectionListener(){
            public void widgetSelected( SelectionEvent event ) {
                setBackgroundLayer(backgroundButton.getSelection());
                setApplyButton();
            }
            public void widgetDefaultSelected( SelectionEvent event ) {
            }
        });

        aoiButton = new Button(toolsGroup, SWT.CHECK);
        aoiButton.setText(Messages.LayerInteraction_AOI);
        aoiButton.setLocation(40, 120);
        aoiButton.pack();
        aoiButton.addSelectionListener(defaultSelectionListener());

        loadLayer();
        return interactionPage;
    }
    
    /*
     * Returns a new default selection listener to add to buttons
     */
    private SelectionListener defaultSelectionListener() {
        return new SelectionListener(){
            public void widgetSelected( SelectionEvent event ) {
                setApplyButton();
            }
            public void widgetDefaultSelected( SelectionEvent event ) {
            }
        };
    }
    
    @Override
    public boolean performOk() {
        saveLayer();
        return super.performOk();
    }
    
    @Override
    protected void performApply() {
        saveLayer();
        super.performApply();
    }

    @Override
    protected void performDefaults() {
        loadLayer();
        super.performDefaults();
    }
    
    /** Update the apply and revert buttons if anything has been modified ... */
    protected void setApplyButton(){
        boolean changed = (
                visibleButton.getSelection() != layer.isVisible()
                || backgroundButton.getSelection() != layer.getInteraction( Interaction.BACKGROUND )
                || informationButton.getSelection() != layer.getInteraction( Interaction.INFO )
                || selectButton.getSelection() != layer.isSelectable()
                || editButton.getSelection() != layer.getInteraction( Interaction.EDIT )
                || aoiButton.getSelection() != layer.getInteraction( Interaction.AOI ) 
        );
        
        this.getApplyButton().setEnabled(changed);
        this.getDefaultsButton().setEnabled(changed);
    }
    
    /*
     * Saves any changes in interaction values for this layer
     */
    private void saveLayer() {
        if( visibleButton.getSelection() != layer.isVisible() ){
            layer.setVisible(visibleButton.getSelection());
        }
        if( backgroundButton.getSelection() != layer.getInteraction( Interaction.BACKGROUND )){
            layer.setInteraction(Interaction.BACKGROUND, backgroundButton.getSelection());
        }
        if( informationButton.getSelection() != layer.getInteraction( Interaction.INFO )){
            layer.setInteraction(Interaction.INFO, informationButton.getSelection());
        }
        if( selectButton.getSelection() != layer.isSelectable() ){
            layer.setSelectable(selectButton.getSelection());
        }
        if( editButton.getSelection() != layer.getInteraction( Interaction.EDIT )){
            layer.setInteraction(Interaction.EDIT, editButton.getSelection());
        }
        if( aoiButton.getSelection() != layer.getInteraction( Interaction.AOI )){
            layer.setInteraction(Interaction.AOI, aoiButton.getSelection());
        }
    }
    
    /* Grabs the layer and fills in the current page. */
    private void loadLayer() {
        
        // set values and enable / disable buttons
        visibleButton.setSelection(layer.isVisible());
        
        // set background layer
        boolean isBackgroundLayer = layer.getInteraction(Interaction.BACKGROUND);
		backgroundButton.setSelection(isBackgroundLayer);
        layerButton.setSelection(!isBackgroundLayer);
        setBackgroundLayer(isBackgroundLayer);
    }
    
    /*
     * enables button and sets the selection to the value supplied
     */
    private void enableButton(Button button, boolean selection) {
        button.setEnabled(true);
        button.setSelection(selection);
    }
    
    /*
     * disables the button and sets selection to false
     */
    private void disableButton(Button button) {
        button.setEnabled(false);
        button.setSelection(false);
    }
    
    /*
     * Sets background layer options based on layer properties
     */
    private void setBackgroundLayer(boolean selection) {
        if (selection) {
            // enable background layer options if applicable
            setPolygonLayer();
            
            // disable non background layer options
            disableButton(informationButton);
            disableButton(selectButton);
            disableButton(editButton);
        }
        else {
            // check if raster layer
            setRasterLayer();
            
            // disable background layer options
            disableButton(aoiButton);
        }
    }

    /*
     * Sets polygon layer options based on layer properties
     */
    private void setPolygonLayer() {
        aoiButton.setEnabled(isPolygon);
        if (isPolygon) {
            aoiButton.setSelection(layer.getInteraction(Interaction.AOI));
        }
        else {
            aoiButton.setSelection(false);
        }
    }
    
    /*
     * Sets raster layer options based on layer properties
     */
    private void setRasterLayer() {
        if (isRaster) {
            // enable raster options
            enableButton(informationButton, layer.getInteraction(Interaction.INFO));
            // disable non raster options
            disableButton(selectButton);
            disableButton(editButton);
        }
        else {
            enableButton(informationButton, layer.getInteraction(Interaction.INFO));
            enableButton(selectButton, layer.isSelectable());
            enableButton(editButton, layer.getInteraction(Interaction.EDIT));
        }
        
    }

}
