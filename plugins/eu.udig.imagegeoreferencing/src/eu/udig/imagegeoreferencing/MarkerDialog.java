/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package eu.udig.imagegeoreferencing;

import java.text.MessageFormat;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import eu.udig.imagegeoreferencing.i18n.Messages;

/**
 * Dialog for placing markers on the geoimage and map
 * 
 * @author GDavis, Refractions Research
 *
 */
public class MarkerDialog extends Dialog {

    final static int WIDTH_HINT = 450;
    private int markerCount;

    // degree amount for image warping (1-7), 1 seems to work best for basic warps, 2 is
    // good for more extreme warps (when the image needs to change a lot to fit)
    private static final int BASIC_WARP = 1;
    // private static final int EXTREME_WARP = 2;
    private int warpValue = BASIC_WARP;

    private Button removeMarkersBtn;
    private boolean removeMarkers = true;

    final static int STATE_ERROR = 1;
    final static int STATE_GOOD = 2;
    private int state = STATE_GOOD;

    /*
    private Button basicWarpBtn;
    private Button extremeWarpBtn;
    
    // radio button listener
    Listener listener = new Listener() {
        public void handleEvent(Event event) {
            try {
            	Button button = (Button) event.widget;
                Integer data = (Integer)button.getData();
                warpValue = data.intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };   
    */

    protected MarkerDialog( Shell parentShell, int markerCount ) {
        super(parentShell);
        this.markerCount = markerCount;
    }

    @Override
    protected void configureShell( Shell newShell ) {
        super.configureShell(newShell);
        newShell.setText(Messages.MarkerDialog_title);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        newShell.setLayout(layout);
    }

    protected Control createDialogArea( Composite parent ) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout(1, true);
        container.setLayout(layout);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.widthHint = WIDTH_HINT;
        container.setLayoutData(layoutData);

        // check the status of any selected geo image for the active map
        IMap map = ApplicationGIS.getActiveMap();
        GeoReferenceMapGraphic mapGraphic = GeoReferenceUtils.getMapGraphic(map);
        if (mapGraphic == null || mapGraphic.getImages() == null || mapGraphic.getImages().get(map) == null
                || mapGraphic.getImages().get(map).size() < 1) {
            createControlsSingleLabel(container, Messages.MarkerDialog_noimageloaded);
            state = STATE_ERROR;
            return container;
        } else if (GeoReferenceUtils.getSelectedGeoImage(mapGraphic.getImages(), map) == null) {
            createControlsSingleLabel(container, Messages.MarkerDialog_noimageselected);
            state = STATE_ERROR;
            return container;
        }

        // if we get here, there should be a valid selected geo image, so show proper
        // dialog controls
        state = STATE_GOOD;
        createMainDialogContents(container);

        return container;
    }

    public int getState() {
        return state;
    }

    /**
     * Create the main dialog components with radio buttons
     * 
     * @param container
     */
    private void createMainDialogContents( Composite container ) {
        createControlsSingleLabel(container,
                MessageFormat.format(Messages.MarkerDialog_desc, markerCount, markerCount, markerCount));

        /* default to "basic" warp for now
        // setup radio buttons
        Group groupRadio = new Group(container, SWT.NULL);
        groupRadio.setText(Messages.MarkerDialog_selecttype);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        groupRadio.setLayout(layout);
        groupRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        basicWarpBtn = new Button(groupRadio, SWT.RADIO);
        basicWarpBtn.setText(Messages.MarkerDialog_basicwarp);
        basicWarpBtn.setEnabled(true);
        basicWarpBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 
        basicWarpBtn.addListener(SWT.Selection, listener);
        basicWarpBtn.setData(new Integer(BASIC_WARP));
        basicWarpBtn.setSelection(true);
        
        extremeWarpBtn = new Button(groupRadio, SWT.RADIO);
        extremeWarpBtn.setText(Messages.MarkerDialog_extremewarp);
        extremeWarpBtn.setEnabled(true);
        extremeWarpBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 
        extremeWarpBtn.addListener(SWT.Selection, listener);        
        extremeWarpBtn.setData(new Integer(EXTREME_WARP));
        */

        // add note
        createControlsSingleLabel(container, Messages.MarkerDialog_note);

        // add checkbox
        removeMarkersBtn = new Button(container, SWT.CHECK);
        removeMarkersBtn.setText(Messages.MarkerDialog_removemarkerscheck);
        removeMarkersBtn.setEnabled(true);
        removeMarkersBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        removeMarkersBtn.setSelection(true);
    }

    private void createControlsSingleLabel( Composite container, String text ) {
        Label descLabel = new Label(container, SWT.WRAP);
        descLabel.setText(text);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.verticalSpan = 1;
        layoutData.horizontalSpan = 1;
        descLabel.setLayoutData(layoutData);
    }

    public int getWarpValue() {
        return warpValue;
    }

    public boolean isRemoveMarkers() {
        return removeMarkers;
    }

    /**
     * Save the checkbox value before closing
     */
    @Override
    public boolean close() {
        if (removeMarkersBtn != null) {
            removeMarkers = removeMarkersBtn.getSelection();
        }
        return super.close();
    }

}
