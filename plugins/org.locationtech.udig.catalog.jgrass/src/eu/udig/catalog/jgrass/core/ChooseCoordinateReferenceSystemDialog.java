/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.catalog.jgrass.core;

import net.refractions.udig.ui.CRSChooser;
import net.refractions.udig.ui.Controller;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class ChooseCoordinateReferenceSystemDialog {

    private CRSChooser chooser;

    private CoordinateReferenceSystem crs = null;

    private boolean goGo = false;

    public void open( Shell parentShell ) {
        goGo = false;
        Dialog dialog = new Dialog(parentShell){

            @Override
            protected void configureShell( Shell shell ) {
                super.configureShell(shell);
                shell.setText("Choose CRS");
            }

            // @Override
            // protected Point getInitialSize() {
            // return new Point(250, 250);
            // }

            @Override
            protected Control createDialogArea( Composite parent ) {
                Composite comp = (Composite) super.createDialogArea(parent);
                GridLayout gLayout = (GridLayout) comp.getLayout();

                gLayout.numColumns = 1;

                chooser = new CRSChooser(new Controller(){

                    public void handleClose() {
                        buttonPressed(OK);
                    }

                    public void handleOk() {
                        buttonPressed(OK);
                    }

                });

                return chooser.createControl(parent);
            }

            @Override
            protected void buttonPressed( int buttonId ) {
                if (buttonId == OK) {
                    try {
                        crs = chooser.getCRS();
                    } catch (Exception e) {
                    }
                }
                close();
                goGo = true;
            }

        };

        dialog.setBlockOnOpen(true);
        dialog.open();
        while( !goGo ) {
            if (dialog.getShell().isDisposed()) {
                break;
            }

            if (Display.getCurrent().readAndDispatch()) {
                // dialog.getShell().getDisplay().readAndDispatch()) {
                continue;
            }

            if (goGo) {
                break;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }

    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

}
