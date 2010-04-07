package net.refractions.udig.project.internal.commands.edit;

import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;

/**
 * Replaces the edit feature with a new copy from the datastore. Any changes that were made to the
 * edit feature are lost.
 * 
 * @author jeichar
 */
public class ResetEditFeatureCommand extends AbstractCommand implements MapCommand, UndoableMapCommand {

    private SimpleFeature oldfeature;

    public void run( IProgressMonitor monitor ) throws Exception {
        this.oldfeature = getMap().getEditManager().getEditFeature();
        if (oldfeature == null)
            return;
        if (getMap().getEditManager().getEditLayer() == null) {
            getMap().getEditManagerInternal().setEditFeature(null, null);
            return;
        }

        FeatureStore<SimpleFeatureType, SimpleFeature> store = getMap().getEditManager().getEditLayer().getResource(
                FeatureStore.class, monitor);
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
		FeatureIterator<SimpleFeature> reader = store.getFeatures(
                filterFactory.id(FeatureUtils.stringToId(filterFactory, oldfeature.getID()))).features();
        if (reader.hasNext()) {
            SimpleFeature feature = reader.next();
            Layer layer = getMap().getEditManagerInternal().getEditLayerInternal();
            getMap().getEditManagerInternal().setEditFeature(feature, layer);
        } else {
            getMap().getEditManagerInternal().setEditFeature(null, null);
        }

    }

    public String getName() {
        return Messages.ResetEditFeatureCommand_0; 
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        getMap().getEditManagerInternal().setEditFeature(oldfeature,
                getMap().getEditManagerInternal().getEditLayerInternal());
    }

}
