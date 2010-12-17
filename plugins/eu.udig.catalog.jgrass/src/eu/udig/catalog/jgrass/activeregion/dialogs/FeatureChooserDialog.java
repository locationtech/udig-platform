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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.internal.impl.UDIGFeatureStore;
import net.refractions.udig.project.internal.impl.UDIGSimpleFeatureStore;

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
import org.geotools.data.DataStore;

public class FeatureChooserDialog extends AbstractChooserDialog {

    private IResourcesSelector active;
    private List<DataStore> selectedLayers;

    public void open( Shell parentShell, final int selectionType ) {

        Dialog dialog = new Dialog(parentShell){

            @Override
            protected void configureShell( Shell shell ) {
                super.configureShell(shell);
                shell.setText("Select vector map"); //$NON-NLS-1$
            }

            @Override
            protected Point getInitialSize() {
                return new Point(280, 380);
            }

            @Override
            protected Control createDialogArea( Composite parent ) {

                parentPanel = (Composite) super.createDialogArea(parent);
                GridLayout gLayout = (GridLayout) parentPanel.getLayout();

                gLayout.numColumns = 1;

                active = new FeatureLayerTreeViewer(parentPanel, SWT.BORDER, selectionType);

                makeCheckPanel();

                return parentPanel;
            }

            @Override
            protected void buttonPressed( int buttonId ) {
                if (buttonId == OK) {
                    Object tmp = null;
                    try {
                        tmp = ((List) active.getSelectedLayers()).get(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        selectedLayers = null;
                    }
                    if (tmp instanceof DataStore) {
                        selectedLayers = (List<DataStore>) active.getSelectedLayers();
                    } else if (tmp instanceof UDIGSimpleFeatureStore) {
                        List l = (List) active.getSelectedLayers();
                        List<DataStore> ll = new ArrayList<DataStore>();
                        for( Object object : l ) {
                            UDIGSimpleFeatureStore internal = (UDIGSimpleFeatureStore) object;
                            ll.add(internal.getDataStore());
                        }
                        selectedLayers = ll;
                    }
                } else {
                    selectedLayers = null;
                }
                super.buttonPressed(buttonId);
            }

        };
        dialog.setBlockOnOpen(true);
        dialog.open();
    }

    public void widgetSelected( SelectionEvent e ) {

        Button selectedButton = (Button) e.getSource();

        if (!selectedButton.getSelection()) {
            return;
        }

        isCatalogType = !selectedButton.getData("type").equals(VISIBLELAYERTYPE); //$NON-NLS-1$

        // remove the composite
        Widget[] childrens = parentPanel.getChildren();
        for( int i = 0; i < childrens.length; i++ ) {
            childrens[i].dispose();
        }

        if (isCatalogType) {
            active = new FeatureCatalogTreeViewer(parentPanel, SWT.BORDER, SWT.SINGLE);
        } else if (!isCatalogType) {
            active = new FeatureLayerTreeViewer(parentPanel, SWT.BORDER, SWT.SINGLE);
        }
        makeCheckPanel();

        parentPanel.layout();
    }

    public List<DataStore> getSelectedResources() {
        return selectedLayers;
    }

    public String getNameOfResourceAtIndex( int index ) {
        try {
            return selectedLayers.get(index).getTypeNames()[0];
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
