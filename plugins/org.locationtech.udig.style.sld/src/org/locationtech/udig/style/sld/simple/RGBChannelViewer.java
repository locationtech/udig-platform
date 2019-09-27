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
package org.locationtech.udig.style.sld.simple;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SelectedChannelType;
import org.locationtech.udig.style.sld.internal.Messages;

/**
 * Allows editing/viewing of a Red/Green/Blue Raster Channels. 
 * This uses three ChannelViewer to represent Red,Green, and Blue Channels
 *  
 * <p>
 * Workflow:
 * <ol>
 * <li>createControl( parent ) - set up controls
 * <li>set(RasterSymbolizer, int[][]) - sets the selected values for bands and draws the histogram
 * <li>Listener.widgetSelected - User performs an "edit"
 * <li>Listener.sync( SelectionEvent ) - update fields with values of controls
 * <li>fire( SelectionSevent ) - notify SimpleStyleConfigurator of change
 * </ul>
 * </p>
 * </p>
 * 
 * @author Emily Gouge (Refractions Reserach, Inc.)
 * @since 1.1.0
 */
public class RGBChannelViewer  {

    //gui widgets
    private ChannelViewer redChannel;
    private ChannelViewer blueChannel;
    private ChannelViewer greenChannel;
    private Button chEnabled;
        
    private SelectionListener listener; 
    
    // listener for selecting / deselecting the widget
    private class Listener implements SelectionListener{

        public void widgetDefaultSelected( SelectionEvent e ) {
            sync(e);
        }

        public void widgetSelected( SelectionEvent e ) {
            sync(e);
        }
        private void sync(SelectionEvent e){
            fire(e);
        }
    }
    private Listener sync = new Listener();
    
    /**
     * Creates a new RBG Channel Viewer
     * @param red     red channel
     * @param green   green channel
     * @param blue    blue channel
     */
    public RGBChannelViewer(ChannelViewer red, ChannelViewer green, ChannelViewer blue){
        this.redChannel = red;
        this.blueChannel = blue;
        this.greenChannel = green;
    }
            
    /**
     * Accepts a listener that will be notified when content changes.
     * @param listener1 
     */
    public void addListener( SelectionListener listener1 ) {
        this.listener = listener1;
        this.redChannel.addListener(listener1);
        this.blueChannel.addListener(listener1);
        this.greenChannel.addListener(listener1);
    }

    /**
     * Remove listener.
     * @param listener1 
     */
    public void removeListener( SelectionListener listener1 ) {
        if (this.listener == listener1)
            this.listener = null;
    }
    
    
    /**
     * TODO summary sentence for fire ...
     * 
     * @param event
     */
    protected void fire( SelectionEvent event ) {
        if (this.listener == null)
            return;
        this.listener.widgetSelected(event);
    }
    
    /**
     * 
     *
     * @return The red channel
     */
    public ChannelViewer getRedChannel(){
        return this.redChannel;
    }
    /**
     * 
     *
     * @return the green channel
     */
    public ChannelViewer getGreenChannel(){
        return this.greenChannel;
    }
    /**
     * 
     *
     * @return the blue channel
     */
    public ChannelViewer getBlueChannel(){
        return this.blueChannel;
    }
    
    void listen( boolean listen ){
        if( listen ){
            chEnabled.addSelectionListener(this.sync);
        } else {
            chEnabled.removeSelectionListener(this.sync);
        }
    }
    
    /**
     * Creates the control
     *
     * @param parent
     * @return the created control
     */
    public Composite createControl(Composite parent){
       Group g = new Group(parent, SWT.SHADOW_ETCHED_IN);
       g.setLayout(new GridLayout(1, false));
       
       chEnabled = new Button(g, SWT.CHECK);
       chEnabled.setText(Messages.RGBChannelViewer_RBGChannelSelectionLabel);
       chEnabled.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1));
       chEnabled.setSelection(true);
       chEnabled.addSelectionListener(new SelectionAdapter(){
           public void widgetSelected(SelectionEvent e) {
               setChannelsEnabled(chEnabled.getSelection());
           }
           
       });
       
       redChannel.createControl(g);
       greenChannel.createControl(g);
       blueChannel.createControl(g);
       
       return g;
    }
    
    /**
     * 
     *
     * @return true if RBG Channel selection enabled
     */
    public boolean isEnabled(){
        return chEnabled.getSelection();
    }
    
    /**
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled){
        chEnabled.setSelection(enabled);
        setChannelsEnabled(enabled);
    }
    
    public void setEditable(boolean editable){
        setEnabled(editable);
        chEnabled.setEnabled(editable);
    }
    
    private void setChannelsEnabled(boolean enabled){
        redChannel.setEnabled(enabled);
        greenChannel.setEnabled(enabled);
        blueChannel.setEnabled(enabled);
    }
    
    /**
     * Updates the values used to draw the histogram
     *
     * @param rgbHistograms
     */
    public void updateHistograms(int[][] rgbHistograms){
        redChannel.updateHistogram(rgbHistograms);
        blueChannel.updateHistogram(rgbHistograms);
        greenChannel.updateHistogram(rgbHistograms);
    }
    
    /**
     * Sets the default values for the raster symbolizer
     *
     * @param rs
     * @param rgbHistograms
     */
    public void set( RasterSymbolizer rs ) {
        listen(false); // don't sync when setting up
        try {
            ChannelSelection cs = rs.getChannelSelection();
            SelectedChannelType red = null;
            SelectedChannelType green = null;
            SelectedChannelType blue = null;
            
            if (cs == null) {
                setEnabled(false);
            }else{
                SelectedChannelType[] sct = cs.getRGBChannels();
                if (sct[0] == null && sct[1] == null && sct[2] == null) {
                    setEnabled(false);
                }else{
                    setEnabled(true);
                    red = sct[0];
                    green = sct[1];
                    blue = sct[2];
                }
            }
            redChannel.set(red, 0);
            greenChannel.set(green, 1);
            blueChannel.set(blue, 2);
        }catch (Exception ex){
            ex.printStackTrace();
        } finally {
            listen(true); // listen to user now
        }
    }
    
    public void setBands(String[] bands){
        redChannel.setBands(bands);
        greenChannel.setBands(bands);
        blueChannel.setBands(bands);
    }
}
