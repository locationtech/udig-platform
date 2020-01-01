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

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.geotools.styling.SelectedChannelType;
import org.locationtech.udig.style.sld.AbstractSimpleConfigurator;
import org.locationtech.udig.style.sld.internal.Messages;
import org.opengis.filter.expression.Literal;

/**
 * Allows editing/viewing of a Raster Channel. This includes selecting the band for the channel and
 * applying an gamma value. As well a histogram of the values is displayed.
 * <p>
 * Here is the pretty picture:
 * 
 * <pre><code>
 * +-- Channel ---------------------------------------------+
 * |        +----------+       +------------------------+   |
 * | Band:  | 1       V|       |                        |   |
 * |        +----------+       |                        |   |        
 * |                           |    Histogram           |   |
 * |        +----------+       |                        |   |
 * | Gamma: | 1.0     V|       |                        |   |
 * |        +----------+       +------------------------+   |       
 * +--------------------------------------------------------+
 * </code></pre>
 * 
 * </p>
 * <p>
 * Workflow:
 * <ol>
 * <li>createControl( parent ) - set up controls
 * <li>set(SelectedChannelType, int, int[][]) - sets the selected values for the controls and
 * redraws the histogram
 * <li>Listener.widgetSelected/modifyText - User performs an "edit"
 * <li>Listener.sync( SelectionEvent ) - update fields with values of controls
 * <li>fire( SelectionSevent ) - notify SimpleStyleConfigurator of change
 * <li>getName() - gets the name of the band the user selected to represent the channel
 * <li>getGamma() - gets the gamma the user selected
 * </ul>
 * </p>
 * 
 * @author Emily Gouge (Refractions Research, Inc.)
 * @since 1.0.0
 */
public class ChannelViewer {

    // gui widgets
    private Group channelGrp; // group with channel label
    private Combo cmbBands; // band selection
    private Label lblLevel; // label for gamma selection
    private Spinner spnLevel; // spinner for gamma selection
    private Label lblBand; // label for band selection
    private Label lblHistogram; // label for drawing histogram

    private String name; // name of band
    private int bandIndex; // zero based index of the band (red = 0; green = 1; blue = 2);

    private Color channelColor; // the color associated with the channel - used for histogram

    private SelectionListener listener;
    private int[][] histogramData = null;

    // listener applied when changes made to widget
    private class Listener implements SelectionListener, ModifyListener {
        public void widgetDefaultSelected( SelectionEvent e ) {
            sync(e);
        }

        public void widgetSelected( SelectionEvent e ) {
            sync(e);
        }
        public void modifyText( ModifyEvent e ) {
            sync(AbstractSimpleConfigurator.selectionEvent(e));
        }
        private void sync( SelectionEvent e ) {
            fire(e);
        }
    }
    private Listener sync = new Listener();

    /**
     * Creates a new channel viewer
     * 
     * @param name The name of the channel
     * @param channelColor The color associated with the channel
     * @param bandIndex The index of the channel
     */
    public ChannelViewer( String name, Color channelColor, int bandIndex ) {
        this.name = name;
        this.channelColor = channelColor;
        this.bandIndex = bandIndex;
    }

    /**
     * Sets the possible bands to be displayed in the draw down list.
     * 
     * @param bands
     */
    public void setBands( String[] bands ) {
        if (cmbBands != null) {
            cmbBands.removeAll();
            for( String band : bands ) {
                cmbBands.add(band);
            }
        }
    }
    /**
     * Accepts a listener that will be notified when content changes.
     * 
     * @param listener1
     */
    public void addListener( SelectionListener listener1 ) {
        this.listener = listener1;
    }

    /**
     * Remove listener
     * 
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
     * Called to set up this "viewer" based on the provided symbolizer
     * 
     * @param sym
     * @param defaultValue - the value to select from the bands array if channel is null (0 based)
     */
    public void set( SelectedChannelType channel, int defaultValue ) {
        listen(false); // don't sync when setting up
        try {
            int channelindex = -1;
            if (channel != null) {
                String channelname = channel.getChannelName().evaluate(channel, String.class);
                try {
                    channelindex = Integer.parseInt(channelname);
                    channelindex--; // get the 0 based index
                    this.cmbBands.setText((channelindex < this.cmbBands.getItemCount())
                            ? this.cmbBands.getItem(channelindex)
                            : this.cmbBands.getItem(0));
                } catch (Exception ex) {
                    // band isn't valid number
                    this.cmbBands.setText(this.cmbBands.getItem(0));
                }

                Double v = (Double) (((Literal) channel.getContrastEnhancement().getGammaValue())
                        .getValue());
                if (v != null) {
                    this.spnLevel.setSelection((int) (v.doubleValue() * 10));
                }
            } else {
                this.cmbBands.setText((defaultValue < this.cmbBands.getItemCount()) ? this.cmbBands
                        .getItem(defaultValue) : this.cmbBands.getItem(0));
                this.spnLevel.setSelection(10);
            }
            drawHistogram();
        } finally {
            listen(true); // listen to user now
        }
    }

    /**
     * Updates the histogram with the new histogram values.
     * 
     * @param histValues
     */
    public void updateHistogram( int[][] histValues ) {
        this.histogramData = histValues;
        drawHistogram();
    }

    /**
     * Draws the histogram on a label
     */
    private void drawHistogram() {
        if (histogramData == null){
            lblHistogram.setText("Loading ...");
            return;
        }
        Image oldImage = lblHistogram.getImage();
        Rectangle bounds = lblHistogram.getBounds();
        Display display = lblHistogram.getDisplay();
        if (bounds.width <= 0 || bounds.height <= 0) {
            // cannot draw image
            return;
        }

        Image newImage = new Image(display, bounds.width, bounds.height);
        lblHistogram.setText(Messages.ChannelViewer_HistogramLabel);
        GC gc = new GC(newImage);

        if (bandIndex < 0 || bandIndex > histogramData.length) {
            // draw border and return nothing
            gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
            gc.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);
            return;
        }

        int[] histValues = histogramData[bandIndex];
        int max = -1;
        for( int i : histValues ) {
            if (i > max) {
                max = i;
            }
        }
        org.eclipse.swt.graphics.Color color2 = null;
        try {
            int size = (int) ((bounds.width - 1) / 256);
            int inc = 1;
            if (size == 0) {
                inc = (int) Math.ceil(256.0 / (bounds.width - 1));
                int numIntervals = (int) 256 / inc;
                size = (int) ((bounds.width - 1) / numIntervals);
            }

            // draw bar
            color2 = new org.eclipse.swt.graphics.Color(display, channelColor.getRed(),
                    channelColor.getGreen(), channelColor.getBlue());
            gc.setForeground(color2);
            double values = 0;
            int cnt = 0;
            for( int i = 0; i < histValues.length; i++ ) {
                values += histValues[i];
                cnt++;

                if (i % inc == 0) {
                    // show the average value so no data is skipped
                    values = values / cnt;
                    int height = (int) (((bounds.height - 1) * values) / max);
                    if (height > 0) {
                        gc.drawRectangle(i * size, (bounds.height - 1), size, -height);
                    }
                    values = 0;
                    cnt = 0;
                }
            }
            if (cnt > 0) {
                values = values / cnt;

                int height = (int) (((bounds.height - 1) * values) / max);
                if (height > 0) {
                    gc.drawRectangle(bounds.width - 2, (bounds.height - 1), size, -height);
                }
            }
            gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
            gc.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);

        } finally {
            gc.dispose();
            if (color2 != null) {
                color2.dispose();
            }
        }

        lblHistogram.setImage(newImage);
        if (oldImage != null) {
            oldImage.dispose();
        }
        lblHistogram.redraw();
    }

    void listen( boolean listen ) {
        if (listen) {
            this.cmbBands.addSelectionListener(this.sync);
            this.spnLevel.addModifyListener(this.sync);
        } else {
            this.cmbBands.removeSelectionListener(this.sync);
            this.spnLevel.removeModifyListener(this.sync);
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

        channelGrp.setLayout(new GridLayout(2, false));

        Composite left = new Composite(channelGrp, SWT.EMBEDDED);
        left.setLayout(new GridLayout(2, false));

        lblBand = new Label(left, SWT.NONE);
        lblBand.setText(Messages.ChannelViewer_BandLabel);

        cmbBands = new Combo(left, SWT.READ_ONLY | SWT.DROP_DOWN);
        cmbBands.setText("-1"); //$NON-NLS-1$

        lblLevel = new Label(left, SWT.NONE);
        lblLevel.setText(Messages.ChannelViewer_GammaLabel);

        spnLevel = new Spinner(left, SWT.BORDER);
        spnLevel.setValues(255, 0, 255, 1, 1, 5);

        Composite right = new Composite(channelGrp, SWT.EMBEDDED);

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.verticalSpan = 2;
        right.setLayoutData(gd);

        right.setLayout(new FillLayout(SWT.VIRTUAL));

        lblHistogram = new Label(right, SWT.NONE);
        lblHistogram.setText(Messages.ChannelViewer_HistogramLabel);
    }

    /**
     * Enables/Disables the control and its subcomponents.
     * 
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        this.channelGrp.setEnabled(enabled);

        this.cmbBands.setEnabled(enabled);
        this.spnLevel.setEnabled(enabled);

        this.lblLevel.setEnabled(enabled);
        this.lblBand.setEnabled(enabled);

        this.lblHistogram.setEnabled(enabled);

        if (enabled) {
            drawHistogram();
        }
    }

    /**
     * @return the name of the band selected to represent the channel
     */
    public String getName() {
        return String.valueOf(this.cmbBands.getSelectionIndex() + 1);
    }
    /**
     * @return The gamma selected by the user
     */
    public double getGamma() {
        return this.spnLevel.getSelection() / 10.0;
    }
}
