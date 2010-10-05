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
package eu.udig.catalog.jgrass.operations;

import java.net.URL;

import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import eu.udig.catalog.jgrass.core.JGrassService;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class JGrassMapsetAddOperation implements IOp {
    private String locationPath;
    private volatile String mapsetName;

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        final JGrassService mr = (JGrassService) target;

        Display.getDefault().syncExec(new Runnable(){

            public void run() {
                URL identifier = mr.getIdentifier();
                locationPath = URLUtils.urlToFile(identifier).getAbsolutePath();
                InputDialog iDialog = new InputDialog(Display.getDefault().getActiveShell(),
                        "New mapset name", "Please enter the name for the new mapset to create.",
                        "newmapset", null);
                iDialog.open();
                mapsetName = iDialog.getValue();
                if (mapsetName.indexOf(' ') != -1) {
                    MessageBox msgBox = new MessageBox(Display.getDefault().getActiveShell(),
                            SWT.ICON_ERROR);
                    msgBox
                            .setMessage("Mapset names can't contain spaces. Please choose a name without spaces.");
                    msgBox.open();
                    mapsetName = null;
                    return;
                }
            }
        });

        if (mapsetName != null && mapsetName.length() > 0) {
            JGrassCatalogUtilities.createMapset(locationPath, mapsetName, null, null);
            JGrassCatalogUtilities.addMapsetToCatalog(locationPath, mapsetName);
        }

    }
}
