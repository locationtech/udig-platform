/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tools.jgrass.orientationview;

import java.util.LinkedList;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.CompositeCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.command.factory.EditCommandFactory;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.jts.geom.Geometry;

import org.locationtech.udig.tools.jgrass.i18n.Messages;
import org.locationtech.udig.tools.jgrass.utils.OperationUtils;

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
