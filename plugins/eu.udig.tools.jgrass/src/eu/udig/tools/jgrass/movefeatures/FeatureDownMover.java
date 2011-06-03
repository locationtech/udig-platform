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
package eu.udig.tools.jgrass.movefeatures;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import eu.udig.tools.jgrass.utils.OperationUtils;

/**
 * Operation to move features one layer down. 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class FeatureDownMover extends OperationUtils implements IOp {

    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ILayer selectedLayer = (ILayer) target;
        moveFeatures(display, monitor, selectedLayer, false);
    }

}
