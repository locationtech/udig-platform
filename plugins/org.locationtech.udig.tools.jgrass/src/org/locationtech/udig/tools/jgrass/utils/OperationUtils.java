/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tools.jgrass.utils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.CompositeCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.command.factory.EditCommandFactory;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.tool.IToolContext;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import org.locationtech.udig.tools.jgrass.i18n.Messages;

/**
 * Common methods for less code in operations.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class OperationUtils {
    public static enum MSGTYPE {
        INFO, ERROR, WARNING;
    };

    protected void showMessage( final Display display, final String title, final String msg, final MSGTYPE type ) {
        display.asyncExec(new Runnable(){
            public void run() {
                switch( type ) {
                case INFO:
                    MessageDialog.openInformation(display.getActiveShell(), title, msg);
                    break;
                case ERROR:
                    MessageDialog.openError(display.getActiveShell(), title, msg);
                    break;
                case WARNING:
                    MessageDialog.openWarning(display.getActiveShell(), title, msg);
                    break;
                default:
                    break;
                }
            }
        });
    }

    protected void moveFeatures( final Display display, IProgressMonitor monitor, ILayer selectedLayer, boolean moveUp )
            throws IOException {
        SimpleFeatureSource featureSource = (SimpleFeatureSource) selectedLayer.getResource(FeatureSource.class,
                new SubProgressMonitor(monitor, 1));
        if (featureSource == null) {
            return;
        }

        int delta = 1;
        if (!moveUp) {
            delta = -1;
        }

        IMap activeMap = ApplicationGIS.getActiveMap();
        List<ILayer> mapLayers = activeMap.getMapLayers();
        int currentPosition = mapLayers.indexOf(selectedLayer);

        int toPosition = currentPosition + delta;
        if (toPosition < 0 || toPosition > mapLayers.size() - 1) {
            showMessage(display,
                    Messages.getString("OperationUtils_warning"), Messages.getString("OperationUtils_nolayer"), MSGTYPE.WARNING); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        ILayer toLayer = mapLayers.get(toPosition);

        SimpleFeatureType toSchema = toLayer.getSchema();
        SimpleFeatureType selectedSchema = selectedLayer.getSchema();
        int compare = DataUtilities.compare(toSchema, selectedSchema);
        boolean needsReOrder = false;
        if (compare == -1) {
            showMessage(display,
                    Messages.getString("OperationUtils_warning"), Messages.getString("OperationUtils_sametypeproblem"), //$NON-NLS-1$ //$NON-NLS-2$
                    MSGTYPE.WARNING);
            return;
        } else if (compare == 1){
            showMessage(display,
                    Messages.getString("OperationUtils_warning"), "featureType match but needs reordering", MSGTYPE.INFO); //$NON-NLS-1$ //$NON-NLS-2$
                needsReOrder = true;
        }

        SimpleFeatureCollection featureCollection = featureSource.getFeatures(selectedLayer.getQuery(true));
        if (featureCollection.size() < 1) {
            showMessage(
                    display,
                    Messages.getString("OperationUtils_warning"), Messages.getString("OperationUtils_nofeaturesproblem"), MSGTYPE.WARNING); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }

        SimpleFeatureIterator featureIterator = featureCollection.features();
        EditCommandFactory cmdFactory = EditCommandFactory.getInstance();
        List<UndoableMapCommand> copyOverList = new LinkedList<UndoableMapCommand>();
        List<UndoableMapCommand> deleteOldList = new LinkedList<UndoableMapCommand>();
        int count = 0;
        while( featureIterator.hasNext() ) {
            SimpleFeature feature = featureIterator.next();
            UndoableMapCommand addFeatureCmd = cmdFactory.createAddFeatureCommand(
                    needsReOrder ? DataUtilities.reType(toSchema, feature, true) : feature, toLayer);
            copyOverList.add(addFeatureCmd);
            UndoableMapCommand deleteFeatureCmd = cmdFactory.createDeleteFeature(feature, selectedLayer);
            deleteOldList.add(deleteFeatureCmd);
            count++;
        }

        /*
         * first copy things over and if that works, delete the old ones
         */
        IToolContext toolContext = ApplicationGIS.createContext(ApplicationGIS.getActiveMap());
        try {
            CompositeCommand compositeCommand = new CompositeCommand(copyOverList);
            toolContext.sendSyncCommand(compositeCommand);
        } catch (Exception e) {
            showMessage(display,
                    Messages.getString("OperationUtils_error"), Messages.getString("OperationUtils_copyproblem"), MSGTYPE.ERROR); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        try {
            CompositeCommand compositeCommand = new CompositeCommand(deleteOldList);
            toolContext.sendSyncCommand(compositeCommand);
            showMessage(
                    display,
                    Messages.getString("OperationUtils_info"), MessageFormat.format(Messages.getString("OperationUtils_movedinfo"), count), MSGTYPE.WARNING); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (Exception e) {
            showMessage(display,
                    Messages.getString("OperationUtils_error"), Messages.getString("OperationUtils_deleteproblem"), MSGTYPE.ERROR); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
