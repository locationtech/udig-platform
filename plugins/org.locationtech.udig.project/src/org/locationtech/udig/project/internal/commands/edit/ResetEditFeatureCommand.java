/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands.edit;

import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
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
