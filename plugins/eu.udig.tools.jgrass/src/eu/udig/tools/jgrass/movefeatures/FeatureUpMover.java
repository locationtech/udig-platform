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
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import eu.udig.tools.jgrass.utils.OperationUtils;

/**
 * Operation to move features one layer up. 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class FeatureUpMover extends OperationUtils implements IOp {

    private int count = 0;
    @Override
    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ILayer selectedLayer = (ILayer) target;
        SimpleFeatureSource featureSource = (SimpleFeatureSource) selectedLayer.getResource(FeatureSource.class,
                new SubProgressMonitor(monitor, 1));
        if (featureSource == null) {
            return;
        }

        IMap activeMap = ApplicationGIS.getActiveMap();
        List<ILayer> mapLayers = activeMap.getMapLayers();
        int currentPosition = mapLayers.indexOf(selectedLayer);
        int oneUp = currentPosition + 1;
        if (oneUp < 0 || oneUp > mapLayers.size() - 1) {
            showMessage(display, "WARNING", "There is no layer to move the feature into above.", MSGTYPE.WARNING);
            return;
        }
        ILayer oneUpLayer = mapLayers.get(oneUp);

        SimpleFeatureType oneUpSchema = oneUpLayer.getSchema();
        SimpleFeatureType selectedSchema = selectedLayer.getSchema();
        int compare = DataUtilities.compare(oneUpSchema, selectedSchema);
        if (compare != 0) {
            showMessage(display, "WARNING", "Feature moving is allowed only between layer of same type and attributes.",
                    MSGTYPE.WARNING);
            return;
        }

        SimpleFeatureCollection featureCollection = featureSource.getFeatures(selectedLayer.getQuery(true));
        if (featureCollection.size() < 1) {
            showMessage(display, "WARNING", "No selected features found to be moved.", MSGTYPE.WARNING);
            return;
        }

        SimpleFeatureIterator featureIterator = featureCollection.features();
        EditCommandFactory cmdFactory = EditCommandFactory.getInstance();
        List<UndoableMapCommand> copyOverList = new LinkedList<UndoableMapCommand>();
        List<UndoableMapCommand> deleteOldList = new LinkedList<UndoableMapCommand>();
        count = 0;
        while( featureIterator.hasNext() ) {
            SimpleFeature feature = featureIterator.next();
            UndoableMapCommand addFeatureCmd = cmdFactory.createAddFeatureCommand(feature, oneUpLayer);
            copyOverList.add(addFeatureCmd);
            UndoableMapCommand deleteFeatureCmd = cmdFactory.createDeleteFeature(feature, selectedLayer);
            deleteOldList.add(deleteFeatureCmd);
            count++;
        }

        /*
         * first copy things over and if that works, delete the old ones
         */
        CompositeCommand compositeCommand = new CompositeCommand(copyOverList);
        IToolContext toolContext = ApplicationGIS.createContext(ApplicationGIS.getActiveMap());
        toolContext.sendSyncCommand(compositeCommand);

        compositeCommand = new CompositeCommand(deleteOldList);
        toolContext.sendSyncCommand(compositeCommand);

        showMessage(display, "INFO", MessageFormat.format("Moved {0} features to the upper layer.", count), MSGTYPE.WARNING);

    }

}
