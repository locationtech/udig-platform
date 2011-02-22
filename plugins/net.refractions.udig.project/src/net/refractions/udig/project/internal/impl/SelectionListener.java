/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.impl;

import java.io.IOException;

import net.refractions.udig.project.internal.ContextModel;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.Filter;

/**
 * Listens to the current selection
 *
 * @author Jesse
 * @since 1.0.0
 */
public class SelectionListener extends AdapterImpl {

    private static final String PROP_KEY = "EditFeature"; //$NON-NLS-1$

    private Filter lastFilter;

    private Layer lastLayer;

    Iterable<Notification> batchMsg;

    Notification layerMsg;

    EditManagerImpl editManager;

    /**
     * Construct <code>SelectionListener</code>.
     *
     * @param impl
     */
    public SelectionListener( EditManagerImpl editManager ) {
        this.editManager = editManager;
    }

    /**
     * @param layerMsg The layerMsg to set.
     * @uml.property name="layerMsg"
     */
    private synchronized void setLayerMsg( Notification msg ) {
        layerMsg = msg;
    }

    /**
     * @param batchMsg The batchMsg to set.
     * @uml.property name="batchMsg"
     */
    private synchronized void setBatchMsg( Iterable<Notification> msg ) {
        batchMsg = msg;
    }

    synchronized void resetMsgs() {
        batchMsg = null;
        layerMsg = null;
    }

    /**
     * TODO Purpose of net.refractions.udig.project.internal.impl
     * <p>
     * </p>
     *
     * @author Jesse
     * @since 1.0.0
     */
    class Changer extends Job {
        Layer layer;

        Changer() {
            super(Messages.SelectionListener_0);
            setSystem(true);
        }

        @Override
        protected IStatus run( IProgressMonitor monitor ) {
            if (!editManager.isEditLayerLocked()) {
                setEditFeature(layer);
            }
            return Status.OK_STATUS;
        }

    }

    Changer changer = new Changer();

    void selectionLayerChanged( final Layer layer ) {
        changer.layer = layer;
        changer.schedule();

    }

    boolean setEditFeature( Layer layer ) {
        Filter filter = layer.getFilter();

        if (!layer.isVisible() || !layer.isApplicable("net.refractions.udig.tool.default.editing")) //$NON-NLS-1$
            return false;
        if (filter.equals(lastFilter) && lastLayer == layer)
            return true;
        lastFilter = filter;
        lastLayer = layer;

        if (layer.getProperties().get(PROP_KEY) != null
                && layer.getProperties().get(PROP_KEY) instanceof Feature) {
            editManager.setEditFeature((Feature) layer.getProperties().get(PROP_KEY), layer);
            return true;
        }

        FeatureIterator reader = null;
        try {
            FeatureStore store = layer.getResource(FeatureStore.class, null);
            if (store == null) {
                if (editManager.getEditFeature() != null) {
                    editManager.setEditFeature(null, null);
                }
                return true;
            }
            FeatureCollection results = store.getFeatures(new DefaultQuery(layer.getSchema()
                    .getTypeName(), filter));
            reader = results.features();
            if (!reader.hasNext()) {
                if (editManager.getEditFeature() != null)
                    editManager.setEditFeature(null, null);
            }
            Feature feature = reader.next();
            layer.getProperties().put(PROP_KEY, feature);
            editManager.setEditFeature(feature, layer);
            return true;
        } catch (Exception e) {
            // TODO
            if (editManager.getEditFeature() != null)
                editManager.setEditFeature(null, null);
            return false;
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    Job selectionTimer = new Timer();

    /**
     * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( Notification msg ) {
        if (isSingleLayerFilterEvent(msg)) {
            setLayerMsg(msg);
            selectionTimer.schedule(20);
        }
        if (isEditManagerSetEvent(msg))
            mapChanged(msg);
        if (isContextModelSet(msg))
            contextModelChanged(msg);

        if (isBatchLayerFilterEvent(msg)) {
            setBatchMsg((Iterable<Notification>) msg);
            selectionTimer.schedule(20);
        }
    }

    /**
     * TODO summary sentence for isContextModelSet ...
     *
     * @param msg
     * @return
     */
    private boolean isContextModelSet( Notification msg ) {
        return ((EObject) msg.getNotifier()).eClass() == ProjectPackage.eINSTANCE.getMap()
                && msg.getFeatureID(msg.getNotifier().getClass()) == ProjectPackage.MAP__CONTEXT_MODEL;
    }

    private boolean isBatchLayerFilterEvent( Notification msg ) {
        return ((EObject) msg.getNotifier()).eClass() == ProjectPackage.eINSTANCE.getMap()
                && msg.getFeatureID(msg.getNotifier().getClass()) == ProjectPackage.MAP__BATCH_EVENT;
    }

    private boolean isEditManagerSetEvent( Notification msg ) {
        return ((EObject) msg.getNotifier()).eClass() == ProjectPackage.eINSTANCE.getEditManager()
                && msg.getFeatureID(msg.getNotifier().getClass()) == ProjectPackage.EDIT_MANAGER__MAP_INTERNAL;
    }

    private boolean isSingleLayerFilterEvent( Notification msg ) {
        return ((EObject) msg.getNotifier()).eClass() == ProjectPackage.eINSTANCE.getLayer()
                && msg.getFeatureID(msg.getNotifier().getClass()) == ProjectPackage.LAYER__FILTER;
    }

    private void contextModelChanged( Notification msg ) {
        if (msg.getOldValue() != null)
            ((ContextModel) msg.getOldValue()).removeDeepAdapter(this);
        if (msg.getNewValue() != null)
            ((ContextModel) msg.getNewValue()).addDeepAdapter(this);
    }


	@SuppressWarnings("unchecked")//$NON-NLS-1$
	private void mapChanged(Notification msg) {
		if (msg.getOldValue() != null) {
			Map map = (Map) msg.getOldValue();
			map.eAdapters().remove(this);
			if (map.getContextModel() != null) {
				map.getContextModel().removeDeepAdapter(this);
				for (Layer layer : map.getLayersInternal()) {
					if (layer.isType(FeatureStore.class)) {
						try {
							FeatureStore featureStore = layer.getResource(FeatureStore.class, null);
							if (featureStore != null) {
									featureStore.setTransaction(Transaction.AUTO_COMMIT);
							}
						} catch (IOException e) {
							ProjectPlugin.log(null, e);
						}
					}
				}
			}
		}
		if (msg.getNewValue() != null) {
			Map map = (Map) msg.getNewValue();
			map.eAdapters().add(this);
			if (map.getContextModel() != null) {
				map.getContextModel().addDeepAdapter(this);
				for (Layer layer : map.getLayersInternal()) {
					if (layer.isType(FeatureStore.class)) {
						try {
							FeatureStore store=layer.getResource(FeatureStore.class, null);
							if( store!=null && store.getTransaction()!=editManager.transaction )
								store.setTransaction(editManager.transaction);

						} catch (IOException e) {
							ProjectPlugin.log(null, e);
						}
					}
				}
			}
		}
	}

    private class Timer extends Job {

        /**
         * Construct <code>Timer</code>.
         *
         * @param name
         */
        public Timer() {
            super(Messages.SelectionListener_SelectionTimer);
            setSystem(true);
        }

        public synchronized IStatus run( IProgressMonitor monitor ) {
            if (batchMsg == null && layerMsg != null) {
                Notification msg = layerMsg;
                resetMsgs();
                selectionChanged((Layer) msg.getNotifier());
            } else if (batchMsg != null) {
                Iterable<Notification> msgs = batchMsg;
                resetMsgs();
                batchSelection(msgs);
            }
            return Status.OK_STATUS;
        }

        private void batchSelection( Iterable<Notification> msg ) {
            for( Notification notification : msg ) {
                Layer layer = (Layer) notification.getNotifier();
                layer.getProperties().put(PROP_KEY, null);
            }
            if (editManager.getMapInternal().getLayersInternal().isEmpty())
                return;

            if (editManager.isEditLayerLocked() && editManager.getEditLayer() != null) {
                setEditFeature(editManager.getEditLayerInternal());
                return;
            }

            Layer layer = null;
            if (editManager.selectedLayer != null)
                layer = editManager.selectedLayer;
            else if (editManager.getEditLayer() != null)
                layer = editManager.getEditLayerInternal();

            boolean found = false;
            if (layer != null) {
                // make sure that the located layer is one of the newly selected
                // feature.
                for( Notification notification : msg ) {
                    if (notification.getNotifier() == layer) {
                        found = true;
                    }
                }
            }
            if (!found)
                layer = null;

            if (layer == null || !setEditFeature(layer)) {
                for( Notification notification : msg ) {
                    layer = (Layer) notification.getNotifier();
                    if (layer.isVisible() && layer != editManager.selectedLayer) {
                        setEditFeature(layer);
                        break;
                    }
                }
            }

        }

        private void selectionChanged( Layer layer ) {
                layer.getProperties().put(PROP_KEY, null);
                setEditFeature(layer);
        }

    }
}
