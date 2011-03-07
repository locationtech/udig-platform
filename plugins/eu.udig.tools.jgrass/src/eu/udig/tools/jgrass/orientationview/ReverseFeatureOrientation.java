/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.udig.tools.jgrass.orientationview;

import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.CompositeCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

import eu.udig.tools.jgrass.i18n.Messages;
import eu.udig.tools.jgrass.utils.OperationUtils;

/**
 * Operation that inverts the orientation of selected features.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ReverseFeatureOrientation extends OperationUtils implements IOp {

    private int count = 0;
    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ILayer selectedLayer = (ILayer) target;
        SimpleFeatureSource featureSource = (SimpleFeatureSource) selectedLayer.getResource(FeatureSource.class,
                new SubProgressMonitor(monitor, 1));
        if (featureSource == null) {
            return;
        }
        SimpleFeatureCollection featureCollection = featureSource.getFeatures(selectedLayer.getQuery(true));
        SimpleFeatureIterator featureIterator = featureCollection.features();
        EditCommandFactory cmdFactory = EditCommandFactory.getInstance();
        List<UndoableMapCommand> cmdList = new LinkedList<UndoableMapCommand>();
        count = 0;
        while( featureIterator.hasNext() ) {
            SimpleFeature feature = featureIterator.next();
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            Geometry newGeometry = geometry.reverse();
            UndoableMapCommand setGeometryCmd = cmdFactory.createSetGeomteryCommand(feature, selectedLayer, newGeometry);
            cmdList.add(setGeometryCmd);
            count++;
        }
        CompositeCommand compositeCommand = new CompositeCommand(cmdList);
        IToolContext toolContext = ApplicationGIS.createContext(ApplicationGIS.getActiveMap());
        toolContext.sendSyncCommand(compositeCommand);

        showMessage(display, Messages.getString("ReverseFeatureOrientation_info"), //$NON-NLS-1$
                Messages.getString("ReverseFeatureOrientation_infomsg") + count, MSGTYPE.INFO); //$NON-NLS-1$

    }

}
