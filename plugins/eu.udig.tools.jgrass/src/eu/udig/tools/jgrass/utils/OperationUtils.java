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
package eu.udig.tools.jgrass.utils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.CompositeCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.tool.IToolContext;

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

import eu.udig.tools.jgrass.i18n.Messages;

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
        if (compare != 0) {
            showMessage(display,
                    Messages.getString("OperationUtils_warning"), Messages.getString("OperationUtils_sametypeproblem"), //$NON-NLS-1$ //$NON-NLS-2$
                    MSGTYPE.WARNING);
            return;
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
            UndoableMapCommand addFeatureCmd = cmdFactory.createAddFeatureCommand(feature, toLayer);
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
