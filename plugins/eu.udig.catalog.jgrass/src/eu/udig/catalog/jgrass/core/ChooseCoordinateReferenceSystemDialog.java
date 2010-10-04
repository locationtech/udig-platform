/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
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
