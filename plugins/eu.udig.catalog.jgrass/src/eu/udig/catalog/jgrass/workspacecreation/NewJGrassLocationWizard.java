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
package eu.udig.catalog.jgrass.workspacecreation;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.hydrologis.jgrass.libs.region.JGrassRegion;
import eu.udig.catalog.jgrass.JGrassPlugin;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class NewJGrassLocationWizard extends Wizard implements INewWizard {

    public boolean canFinish = false;
    private WorkspaceProperties props;

    public NewJGrassLocationWizard() {
        super();
        // setNeedsProgressMonitor(true);
        setWindowTitle("Creation of a new JGrass location");
        setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                JGrassPlugin.PLUGIN_ID, "icons/simulwizard.png"));
    }

    @Override
    public void addPages() {
        props = new WorkspaceProperties();
        W01CreateLocationWizardPage page1 = new W01CreateLocationWizardPage(props);
        addPage(page1);
        W02CreateLocationWizardPage page2 = new W02CreateLocationWizardPage(props);
        addPage(page2);
        W03CreateLocationWizardPage page3 = new W03CreateLocationWizardPage(props);
        addPage(page3);
    }

    @Override
    public boolean performFinish() {
        // TODO create a nice workspace with the properties
        for( String m : props.mapsets ) {
            System.out.println(m);
        }
        System.out.println(props.locationPath);
        System.out.println(props.north);
        System.out.println(props.xres);
        System.out.println(props.crs.getName().toString());

        try {
            JGrassRegion window = new JGrassRegion(props.west, props.east, props.south,
                    props.north, props.xres, props.yres);
            JGrassCatalogUtilities.createLocation(props.locationPath, props.crs, window);
            for( String mapset : props.mapsets ) {
                JGrassCatalogUtilities.createMapset(props.locationPath, mapset, null, null);
                // set the WIND file
                String mapsetPath = props.locationPath + File.separator + mapset;
                JGrassRegion.writeWINDToMapset(mapsetPath, window);
            }

            JGrassCatalogUtilities.addServiceToCatalog(props.locationPath + File.separator
                    + JGrassCatalogUtilities.JGRASS_WORKSPACE_FILENAME, new NullProgressMonitor());
        } catch (IOException e) {
            JGrassPlugin
                    .log(
                            "JGrassPlugin problem: eu.hydrologis.udig.catalog.workspacecreation.wizard#NewJGrassLocationWizard#performFinish", e); //$NON-NLS-1$
            e.printStackTrace();
        }

        return true;
    }
    @Override
    public boolean canFinish() {
        return canFinish;
    }

    public void init( IWorkbench workbench, IStructuredSelection selection ) {
    }

}
