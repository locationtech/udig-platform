/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */

package org.locationtech.udig.render.gridcoverage.tool.info.internal;


import org.locationtech.udig.render.gridcoverage.basic.internal.Messages;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

/**
 * Allows editing/viewing of a Raster Channel. This includes selecting the band for the channel and
 * applying an gamma value. As well a histogram of the values is displayed.
 * <p>
 * Here is the pretty picture:
 * 
 * <pre><code>
 * +-- Channel ----------------+|
 * |        +----------+        |
 * | Band:  | 1       V|        |
 * |        +----------+        |       
 * |  <----------x------> xx    |
 * | -255       0       255     | 
 * +----------------------------+
 * </code></pre>
 * 
 * </p>

 * 
 * @author Emily Gouge (Refractions Research, Inc.)
 * @since 1.0.0
 */
public class ChannelViewer {

    // gui widgets
    private Group channelGrp;   // group with channel label
    private Combo cmbBands;     // band selection
    private Slider sliderConst; // slider widget
    private Label lblSlider;    //value of slider
    private String name;        //channel name
    
    private SelectionListener sync;
    private ControlDecoration warningDecorator;
   

    /**
     * Creates a new channel 
     * @param name      Name of channel
     * @param listener  Listener to call when modification to channel selection
     */
    public ChannelViewer( String name, SelectionListener listener ) {
        this.name = name;
        this.sync = listener;
    }
   
    /**
     * Sets the possible bands to be displayed in the draw down list.
     * 
     * @param bands
     */
    public void setBands( String[] bands ) {
        if (cmbBands != null){
            cmbBands.removeAll();
            if (bands != null) {
                for( String band : bands ) {
                    cmbBands.add(band);
                }
            }
        }
    }


    /**
     * Updates the channel band selection and color correction values
     * 
     * @param bandIndex - the index of the band 
     * @param colorCorrection - the value between -255 and 255 of color correction
     */
    public void set( int bandIndex, double colorCorrection) {
        listen(false);
        try{
            this.cmbBands.select(bandIndex);
            this.sliderConst.setSelection((int)colorCorrection + 255);
            this.lblSlider.setText( String.valueOf((int)colorCorrection));
        }finally{
            listen(true);
        }
    }

   
    void listen( boolean listen ) {
        if (listen) {
            this.cmbBands.addSelectionListener(this.sync);
            this.sliderConst.addSelectionListener(this.sync);
        } else {
            this.cmbBands.removeSelectionListener(this.sync);
            this.sliderConst.removeSelectionListener(this.sync);
        }
    }

    /**
     * Creates the control.
     * 
     * @param parent
     * @param listener1
     * @return
     */
    public void createControl( Composite parent ) {
        channelGrp = new Group(parent, SWT.SHADOW_ETCHED_IN);
        channelGrp.setText(name);
        channelGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridLayout layout = new GridLayout(3, false);
        layout.marginLeft=0;
        channelGrp.setLayout(layout);


        Label lblBand = new Label(channelGrp, SWT.NONE);
        lblBand.setText(Messages.ChannelViewer_BandLabel);

        cmbBands = new Combo(channelGrp, SWT.READ_ONLY | SWT.DROP_DOWN);
        cmbBands.setText("-1"); //$NON-NLS-1$

        // Create the control decoration warning image
        final Image fieldDecorationWarningImage;
        fieldDecorationWarningImage = FieldDecorationRegistry.getDefault().getFieldDecoration(
                FieldDecorationRegistry.DEC_ERROR).getImage();       
        // Create the control decoration
        warningDecorator = new ControlDecoration(cmbBands, SWT.RIGHT | SWT.CENTER);
        warningDecorator.setMarginWidth(4);
        warningDecorator.setImage(fieldDecorationWarningImage);
        warningDecorator.setDescriptionText("Warning!"); //$NON-NLS-1$
        warningDecorator.hide();
        
        Composite sliderGrp = new Composite(channelGrp, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        sliderGrp.setLayout(new GridLayout(1, false));
        sliderGrp.setLayoutData(gd);
        
        Composite grp = new Composite(sliderGrp, SWT.NONE);
        grp.setLayout(new GridLayout(4, true));
        gd = new GridData(GridData.FILL_HORIZONTAL);
        grp.setLayoutData(gd);
        
        sliderConst = new Slider(grp,SWT.NONE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        sliderConst.setLayoutData(gd);
        sliderConst.setMinimum(0);
        sliderConst.setMaximum(255*2);

        lblSlider = new Label(grp, SWT.NONE);
        gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        gd.widthHint = 25;
        lblSlider.setLayoutData(gd);
        lblSlider.setText("0000"); //$NON-NLS-1$
        
        Label txtLeft = new Label(grp, SWT.LEFT);
        txtLeft.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        txtLeft.setText("-255"); //$NON-NLS-1$
        Label txtMiddle = new Label(grp, SWT.CENTER);
        txtMiddle.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        txtMiddle.setText("0"); //$NON-NLS-1$
        Label txtRight = new Label(grp, SWT.RIGHT);
        txtRight.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
        txtRight.setText("255"); //$NON-NLS-1$
        
        sliderConst.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected( SelectionEvent e ) {
            }

            public void widgetSelected( SelectionEvent e ) {
                lblSlider.setText( String.valueOf(sliderConst.getSelection() - 255));
            }});
        sliderConst.setSelection(255);
        
        listen(true);
    }

    /**
     * Adds an error decoration with given error message.  If null
     * will remove decoration
     * 
     *
     * @param error
     */
    public void setError(String error){
        if (error != null){
            warningDecorator.setDescriptionText(error);
            warningDecorator.show();
        }else{
            warningDecorator.hide();
        }
    }
    /**
     * @return the index of the selected band 
     */
    public int getBandIndex(){
        return this.cmbBands.getSelectionIndex();
    }
    
    /**
     *
     * @return the color correction as a value between -255 and 255
     */
    public int getColorCorrectionValue(){
        return sliderConst.getSelection()-255;
    }

}
