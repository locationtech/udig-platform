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
package eu.udig.omsbox.ui;

import java.util.List;

import net.refractions.udig.catalog.IGeoResource;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * Coverage chooser dialog.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CoverageChooserDialog extends AbstractChooserDialog {

    private IGeoResourcesSelector active;
    private Dialog dialog;

    private List<IGeoResource> resourcesList;

    public void open( Shell parentShell, final int selectionType ) {

        dialog = new Dialog(parentShell){

            @Override
            protected void configureShell( Shell shell ) {
                super.configureShell(shell);
                shell.setText("");
            }

            @Override
            protected Point getInitialSize() {
                return new Point(320, 450);
            }

            @Override
            protected Control createDialogArea( Composite parent ) {
                parentPanel = (Composite) super.createDialogArea(parent);
                GridLayout gLayout = (GridLayout) parentPanel.getLayout();
                gLayout.numColumns = 1;
                active = new CoverageLayersTreeViewer(parentPanel, SWT.BORDER, selectionType);
                makeCheckPanel();
                return parentPanel;
            }

            @Override
            protected void buttonPressed( int buttonId ) {
                if (buttonId == OK) {
                    resourcesList = active.getSelectedResources();
                } else {
                    resourcesList = null;
                }
                super.buttonPressed(buttonId);
            }
        };
        dialog.setBlockOnOpen(true);
        dialog.open();
    }

    public boolean isDisposed() {
        return dialog.getShell().isDisposed();
    }

    public void widgetSelected( SelectionEvent e ) {

        Button selectedButton = (Button) e.getSource();

        if (!selectedButton.getSelection()) {
            return;
        }

        isCatalogType = !selectedButton.getData("type").equals(VISIBLELAYERTYPE);

        // remove the composite
        Widget[] childrens = parentPanel.getChildren();
        for( int i = 0; i < childrens.length; i++ ) {
            childrens[i].dispose();
        }

        if (isCatalogType) {
            active = new CoverageCatalogTreeViewer(parentPanel, SWT.BORDER, SWT.SINGLE);
        } else if (!isCatalogType) {
            active = new CoverageLayersTreeViewer(parentPanel, SWT.BORDER, SWT.SINGLE);
        }
        makeCheckPanel();

        parentPanel.layout();
    }

    public List<IGeoResource> getSelectedResources() {
        return resourcesList;
    }

    public String getNameOfResourceAtIndex( int index ) {
        IGeoResource iGeoResource = resourcesList.get(index);
        return iGeoResource.getTitle();
    }

}
