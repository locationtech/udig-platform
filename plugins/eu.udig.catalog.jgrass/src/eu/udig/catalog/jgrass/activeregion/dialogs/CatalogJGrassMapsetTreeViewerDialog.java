/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
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
package eu.udig.catalog.jgrass.activeregion.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;

/**
 * <p>
 * This class supplies a tree viewer containing the JGrass mapsets that are at that time present in
 * the catalog.
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public class CatalogJGrassMapsetTreeViewerDialog {

    private CatalogJGrassMapsetsTreeViewer active;
    private List<JGrassMapsetGeoResource> newLayers;
    private boolean doContinue = false;

    private synchronized boolean isContinueRequired() {
        return doContinue;
    }

    private synchronized void setRequireContinue( boolean cont ) {
        doContinue = cont;
    }

    public void open( Shell parentShell ) {
        try {
            setRequireContinue(false);

            Dialog dialog = new Dialog(parentShell){

                @Override
                protected void configureShell( Shell shell ) {
                    super.configureShell(shell);
                    shell.setText("Select mapset"); //$NON-NLS-1$
                }

                @Override
                protected Point getInitialSize() {
                    return new Point(250, 250);
                }

                @Override
                protected Control createDialogArea( Composite parent ) {
                    Composite parentPanel = (Composite) super.createDialogArea(parent);
                    GridLayout gLayout = (GridLayout) parentPanel.getLayout();

                    gLayout.numColumns = 1;

                    active = new CatalogJGrassMapsetsTreeViewer(parentPanel, SWT.BORDER, SWT.SINGLE);

                    return parentPanel;
                }

                @Override
                protected void buttonPressed( int buttonId ) {
                    super.buttonPressed(buttonId);
                    if (buttonId == OK) {
                        newLayers = active.getSelectedLayers();
                    } else {
                        newLayers = null;
                    }
                    setRequireContinue(true);
                }

            };

            dialog.setBlockOnOpen(true);
            dialog.open();
            
            while( !isContinueRequired() ) {
                if (dialog.getShell().isDisposed()) {
                    break;
                }

                if (Display.getCurrent().readAndDispatch()) {
                    continue;
                }

                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<JGrassMapsetGeoResource> getSelectedLayers() {
        return newLayers;
    }
}
