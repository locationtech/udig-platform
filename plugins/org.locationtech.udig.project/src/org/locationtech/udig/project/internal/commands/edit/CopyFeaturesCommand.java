/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands.edit;

import java.awt.Rectangle;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.ViewportModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureEvent.Type;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.collection.AdaptorFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Envelope;

/**
 * Copies features selected by the filter from the source layer to the destination layer. Then sets
 * the filter of the destination layer to be the newly added features.
 * 
 * @author jones
 * @since 1.1.0
 */
public class CopyFeaturesCommand extends AbstractCommand implements UndoableMapCommand {

    private Layer destinationLayer;
    private Filter filter;
    private ILayer sourceLayer;

    // for undo
    private Id addedFeaturesFilter;
    private Filter previousDesinationLayerFilter;
    private ReferencedEnvelope previousEnvelope;

    public CopyFeaturesCommand( ILayer sourceLayer, Filter filter, ILayer destinationLayer ) {
        this.sourceLayer = sourceLayer;
        this.filter = filter;
        this.destinationLayer = (Layer) destinationLayer;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        
        if (sourceLayer == null || destinationLayer == null)
            return;
        previousEnvelope = getMap().getViewportModel().getBounds();
        previousDesinationLayerFilter = destinationLayer.getFilter();
        copyFeatures(sourceLayer, filter, destinationLayer, monitor);
    }

    @SuppressWarnings("unchecked")
    private void copyFeatures( ILayer sourceLayer, Filter filter, final Layer targetLayer,
            final IProgressMonitor monitor ) {
        final String copyFeaturesCommand_name = Messages.CopyFeaturesCommand_name;
		String name = MessageFormat.format(copyFeaturesCommand_name,0);
		monitor.setTaskName(name);
		monitor.beginTask(name, 104);
        final int[] worked = new int[]{-3};
        monitor.worked(1);
        try {
            SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 2);
            FeatureStore<SimpleFeatureType, SimpleFeature> destination = targetLayer.getResource(
                    FeatureStore.class, subProgressMonitor);
            subProgressMonitor.done();
            worked[0] += 2;
            subProgressMonitor = new SubProgressMonitor(monitor, 2);
            FeatureSource<SimpleFeatureType, SimpleFeature> source = sourceLayer.getResource(
                    FeatureSource.class, subProgressMonitor);
            subProgressMonitor.done();
            worked[0] += 2;
            // If no FeatureStore then features can't be copied
            // If no FeatureSource then features can't be copied
            if (destination == null || source == null) {
                targetLayer.setFilter(filter);
                return;
            }

            // Create a query that will get the attributes that are compatible
            // what is compatible? Do the attribute names and types have to be the same or
            // just the types.
            // Executive decision:
            // Match both name and type, the rest must be customized.
            final HashMap<String, String> attributeMap = new HashMap<String, String>();
            Query query = createQuery(sourceLayer, filter, targetLayer, attributeMap);
            if (attributeMap.isEmpty()) {
                targetLayer.setFilter(filter);
                return;
            }
            MathTransform mt = createMathTransform(sourceLayer, targetLayer);
            FeatureCollection<SimpleFeatureType, SimpleFeature> features = source
                    .getFeatures(query);
            SimpleFeatureType schema = targetLayer.getSchema();

            
            CopyFeatureCollection c = new CopyFeatureCollection(schema, features, monitor, worked,
                    mt, attributeMap, targetLayer.layerToMapTransform());
            Envelope env = c.env;
            targetLayer.eSetDeliver(false);
            FeatureListener listener = new FeatureListener() {
        		long lastUpdate = System.currentTimeMillis();
        		long count = 0;
				public void changed(FeatureEvent event) {
					if(event.getType() == Type.ADDED) {
						count ++;
		            	if(System.currentTimeMillis() - lastUpdate > 1000) {
		            		Display.getDefault().asyncExec(new Runnable() {
								
								public void run() {
									String name2 = MessageFormat.format(copyFeaturesCommand_name,0);
				                	monitor.setTaskName(name2);
								}
							});
		                	lastUpdate = System.currentTimeMillis();
		            	}
					}
				}
			};
			boolean performedZoom = false;
            try {

            	destination.addFeatureListener(listener);
                List<FeatureId> fids = destination.addFeatures(c);

                performedZoom = displayCopiedFeatures(env);

                FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools
                        .getDefaultHints());
                addedFeaturesFilter = filterFactory.id(new HashSet(fids));
            } finally {
                targetLayer.eSetDeliver(true);
                destination.removeFeatureListener(listener);
                //close all iterators in CopyFeatureCollection. If
                //not done then a deadlock may occur while trying to 
                //copy features within the same layer due to read locks
                //not being properly released.
                for (FeatureIterator it : c.iterators.values()) {
                	it.close();
                }
            }
            if(performedZoom) {
            	getMap().getRenderManager().refresh(null);
            } else {
            	targetLayer.refresh(env);
            }
            getMap().getRenderManager().refresh(targetLayer, env);
            
        } catch (IOException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        } finally {
            monitor.done();
        }
    }

    private boolean displayCopiedFeatures( Envelope env ) {
        if (!getMap().getViewportModel().getBounds().intersects(env) && !env.isNull()
                || tooSmallOnScreen(env)) {

            double d = env.getHeight() / 2;
            double e = env.getWidth() / 2;
            
            ViewportModel viewportModel = getMap().getViewportModelInternal();
			viewportModel.eSetDeliver(false);
            try {
				viewportModel.setBounds(
						env.getMinX() - e, env.getMaxX() + e,
						env.getMinY() - d, env.getMaxY() + d);
				return true;
			}finally{
            	viewportModel.eSetDeliver(true);
            }
        } else {
        	return false;
        }
       
    }

    private MathTransform createMathTransform( ILayer sourceLayer, ILayer targetLayer ) {
        MathTransform temp;
        try {
            CoordinateReferenceSystem targetCRS = targetLayer.getCRS();
            CoordinateReferenceSystem sourceCRS = sourceLayer.getCRS();
            if (targetCRS.equals(sourceCRS))
                temp = null;
            else
                temp = CRS.findMathTransform(sourceCRS, targetCRS, true);
            if (temp == null || temp.isIdentity())
                temp = null;
        } catch (FactoryException e1) {
            ProjectPlugin.log("", e1); //$NON-NLS-1$
            temp = null;
        }

        if (temp == null) {
            try {
                return CRS
                        .findMathTransform(DefaultGeographicCRS.WGS84, DefaultGeographicCRS.WGS84);
            } catch (Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
                return null;
            }
        }
        return temp;
    }

    boolean tooSmallOnScreen( Envelope env ) {
        double[] d = new double[]{env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY()};
        getMap().getViewportModel().worldToScreenTransform().transform(d, 0, d, 0, 2);
        Rectangle r = new Rectangle((int) d[0], (int) d[1], (int) Math.abs(d[2] - d[0]), (int) Math
                .abs(d[3] - d[1]));
        return (double) r.getWidth() < getMap().getRenderManager().getMapDisplay().getWidth()
                / (double) 16
                && (double) r.getHeight() < getMap().getRenderManager().getMapDisplay().getHeight()
                        / (double) 16;
    }

    public static int updateProgress( final IProgressMonitor monitor, int numberToCopyForIncrement,
            final int[] worked ) {
        int result = 0;
        if (numberToCopyForIncrement < 1) {
            monitor.worked(1);
            worked[0]++;
            int i = 100 / (100 - worked[0]);
            result = 1000 * i;
        } else {
            result = numberToCopyForIncrement - 1;
        }

        return result;
    }

    /**
     * Creates a query that requests the features in sourceLayer as dictated by filter. Query only
     * requests the attributes that can be mapped from sourceLayer to targetLayer.
     * 
     * @param queryAttributes populates with a mapping of attributeTypeNames from targetLayer to
     *        sourcelayer
     * @return
     */
    @SuppressWarnings("unchecked")
    private Query createQuery( ILayer sourceLayer, Filter filter, Layer targetLayer,
            Map<String, String> queryAttributes ) {
        SimpleFeatureType sourceSchema = sourceLayer.getSchema();
        SimpleFeatureType targetSchema = targetLayer.getSchema();
        // Maps type names to type names since we are ignoring case

        queryAttributes.putAll(FeatureUtils.createAttributeMapping(sourceSchema, targetSchema));
        Set<String> properties = new HashSet(queryAttributes.values());
        return new Query(sourceSchema.getName().getLocalPart(), filter, properties
                .toArray(new String[properties.size()]));
    }

    public String getName() {
        return Messages.CopyFeaturesCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        if (sourceLayer == null || destinationLayer == null)
            return;
        monitor.beginTask(Messages.CopyFeaturesCommand_undo + getName(), 4);
        monitor.worked(1);
        this.destinationLayer.eSetDeliver(false);
        try {
            FeatureStore<SimpleFeatureType, SimpleFeature> store = this.destinationLayer
                    .getResource(FeatureStore.class, new SubProgressMonitor(monitor, 1));
            store.removeFeatures(addedFeaturesFilter);
            this.destinationLayer.setFilter(previousDesinationLayerFilter);
        } finally {
            this.destinationLayer.eSetDeliver(true);
        }
        getMap().getViewportModelInternal().setBounds(this.previousEnvelope);
    }

    private static class CopyFeatureCollection extends AdaptorFeatureCollection {

        final SimpleFeatureType schema;
        final FeatureCollection<SimpleFeatureType, SimpleFeature> features;
        final IProgressMonitor monitor;
        final int[] worked;
        final MathTransform mt, toWorld;
        final Map<String, String> attributeMap;
        final ReferencedEnvelope env;

        CopyFeatureCollection( SimpleFeatureType schema,
                FeatureCollection<SimpleFeatureType, SimpleFeature> features,
                IProgressMonitor monitor, int[] worked, MathTransform mt,
                HashMap<String, String> attributeMap, MathTransform toWorld ) {
            super("copyCollection", schema);
            this.schema = schema;
            this.features = features;
            this.monitor = monitor;
            this.worked = worked;
            this.mt = mt;
            this.attributeMap = attributeMap;
            this.env = new ReferencedEnvelope(schema.getCoordinateReferenceSystem());
            this.toWorld = toWorld;
        }

        @Override
        public int size() {
            return features.size();
        }
        
        public ReferencedEnvelope getBounds() {
            return features.getBounds();
        }

        Map<Iterator, FeatureIterator<SimpleFeature>> iterators = new HashMap<Iterator, FeatureIterator<SimpleFeature>>();

        @Override
        protected Iterator openIterator() {
            final FeatureIterator<SimpleFeature> iter = features.features();
            Iterator i = new Iterator(){

                // for progress monitor.
                int numberToCopyForIncrement = 1000;
                private SimpleFeature next;
                Iterator<SimpleFeature> copiedFeatures;

                public SimpleFeature next() {
                    SimpleFeature result = next;
                    next = null;
                    return result;
                }

                public boolean hasNext() {
                    while( next == null ) {
                        if (copiedFeatures != null && copiedFeatures.hasNext()) {
                            next = copiedFeatures.next();
                        } else {
                            if (!iter.hasNext())
                                return false;
                            SimpleFeature source = iter.next();
                            numberToCopyForIncrement = updateProgress(monitor,
                                    numberToCopyForIncrement, worked);
                            copiedFeatures = FeatureUtils.copyFeature(source, schema, attributeMap,
                                    mt).iterator();
                            if (!copiedFeatures.hasNext())
                                return false;
                            next = copiedFeatures.next();
                            ReferencedEnvelope newbounds = new ReferencedEnvelope(next.getBounds());
                            try {
                                newbounds = newbounds.transform(env.getCoordinateReferenceSystem(), true);
                                env.expandToInclude(newbounds);
                            } catch (TransformException e) {
                                ProjectPlugin.log("", e); //$NON-NLS-1$
                            } catch (FactoryException e) {
                                ProjectPlugin.log("", e); //$NON-NLS-1$
                            }

                        }
                    }
                    return next != null;
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

            };
            iterators.put(i, iter);
            return i;
        }

        @Override
        protected void closeIterator( Iterator close ) {
            iterators.get(close).close();
        }
    }

}
