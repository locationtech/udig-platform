/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.locationtech.udig.core.IProvider;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.internal.ui.Trace;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.ui.internal.Messages;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.geotools.data.Query;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.FilterAttributeExtractor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;

/**
 * Manages selection for the {@link FeatureTableControl}
 * 
 * @author Jesse
 * @since 1.1.0
 */
class FeatureTableSelectionProvider implements ISelectionProvider {

    private FeatureTableControl owner;
    private Set<String> selectionFids = new HashSet<String>();
    private Set<ISelectionChangedListener> selectionChangedListeners = new CopyOnWriteArraySet<ISelectionChangedListener>();

    /**
     * if null then no set selection is running if not null then it must be cancelled.
     */
    volatile IProgressMonitor progressMonitor;
    private IProvider<IProgressMonitor> progressMonitorProvider;

    public FeatureTableSelectionProvider( FeatureTableControl control, IProvider<IProgressMonitor> progressMonitorProvider  ) {
        this.owner = control;
        this.progressMonitorProvider=progressMonitorProvider;
    }

    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        selectionChangedListeners.add(listener);
    }

    public ISelection getSelection() {
        checkWidget();
        if (selectionFids.isEmpty())
            return new StructuredSelection();
        return new StructuredSelection(getId());

    }

    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        selectionChangedListeners.remove(listener);
    }

    public void setSelection( ISelection selection ) {
        setSelection(selection, true);
    }

    void setSelection( final ISelection newSelection, final boolean reveal ) {
        checkWidget();
        if (progressMonitor != null) {
            progressMonitor.setCanceled(true);
            UiPlugin.trace(Trace.FEATURE_TABLE, FeatureTableSelectionProvider.class, 
                    "#setSelection(): cancelled monitor", null); //$NON-NLS-1$

        }
        try {
            PlatformGIS.wait(500, -1, new WaitCondition(){

                public boolean isTrue() {
                    return progressMonitor==null;
                }
                
            }, this);
        } catch (InterruptedException e) {
            UiPlugin.log("Interrupted", e); //$NON-NLS-1$
            return;
        }

        synchronized (this) {
            progressMonitor = progressMonitorProvider.get();
            progressMonitor.setCanceled(false);

            PlatformGIS.run(new SelectionLoader(newSelection, reveal));
        }
    }

    protected void notifyListeners() {
        //      let listeners know the selection has changed.
        SelectionChangedEvent event = new SelectionChangedEvent(owner,
                getSelection());
        for( ISelectionChangedListener listener : selectionChangedListeners ) {
            try{
                listener.selectionChanged(event);
            }catch (Throwable e) {
                UiPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }
    /**
     * Returns the fids.  It can be used to add new fids.  No notification of the change is raised.
     *
     * @return
     */
    public Collection<String> getSelectionFids() {
        checkWidget();
        return selectionFids;
    }

    public Id getId() {
        checkWidget();
        FilterFactory2 fac = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
        Set<Identifier> ids = FeatureUtils.stringToId(fac, selectionFids);
        return fac.id(ids);
    }

    private void checkWidget() {
        if (Display.getCurrent() == null)
            SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
    }

    private class SelectionLoader implements ISafeRunnable {

        private final ISelection newSelection;
        private final boolean reveal;

        public SelectionLoader( ISelection newSelection, boolean reveal ) {
            this.newSelection = newSelection;
            this.reveal = reveal;
        }

        public void handleException( Throwable exception ) {
            UiPlugin.log("Error setting selection on table view", exception); //$NON-NLS-1$
        }

        public void run() throws Exception {
            startProgress();
            try {
                if (newSelection.isEmpty()) {
                    if (owner.getViewer().getControl().isDisposed()){
                        done();
                        return;
                    }
                    owner.getViewer().getControl().getDisplay().asyncExec(new Runnable(){
                        public void run() {
                            updateMonitor(3);
                            owner.getViewer().getTable().setSelection(new TableItem[0]);

                            selectionFids.clear();

                            updateMonitor(3);
                            
                            owner.getViewer().getTable().clearAll();
                            notifyListeners();
                        }

                    });

                } else if (newSelection instanceof IStructuredSelection) {
                    IStructuredSelection structured = (IStructuredSelection) newSelection;
                    final Set<String> fids = new HashSet<String>();
                    obtainFidsFromSelection(structured, fids);

                    // selection is equivalent to last selection so return
                    if (selectionFids.equals(fids))
                        return;

                    FeatureTableContentProvider provider = (FeatureTableContentProvider) owner
                            .getViewer().getContentProvider();

                    final List<SimpleFeature> features = provider.features;
                    int i = 0;
                    synchronized( provider.features ){
                        for( SimpleFeature feature : features ) {
                            if (fids.contains(feature.getID())) {
                                break;
                            }
                            i++;
                        }
                    }
                    
                    updateMonitor(1);

                    final int index = i;

                    owner.getViewer().getControl().getDisplay().asyncExec(new Runnable(){
                        public void run() {
                            updateMonitor(1);

                            owner.getViewer().getTable().setSelection(new TableItem[0]);

                            selectionFids = fids;

                            final Table table = owner.getViewer().getTable();
                            // clear non-virtual data so that it will be re-labelled. This is not
                            // too
                            // bad of an operation (I think)
                            table.clearAll();
                            if (reveal && index < features.size()) {
                                // show selection if there is one.
                                table.setTopIndex(index);
                            }

                            notifyListeners();
                        }
                    });
                }
            } catch (Abort e) {
                // its ok just aborting the finally will clean up
            }finally {
                done();
            }
        }

        private void updateMonitor( final int ticks ) {
            if( Display.getCurrent()!=null ){
                progressMonitor.worked(ticks);
            }else{
                owner.getControl().getDisplay().asyncExec(new Runnable(){
                    public void run() {
                        progressMonitor.worked(ticks);
                    }
                });
            }
        }

        private void done() {
            Runnable runnable = new Runnable(){
                public void run() {
                    synchronized (FeatureTableSelectionProvider.this) {
                        progressMonitor.done();
                        progressMonitor = null;
                        FeatureTableSelectionProvider.this.notifyAll();
                    }
                }
            };
            if( Display.getCurrent()!=null ){
                runnable.run();
            }else{
                owner.getControl().getDisplay().asyncExec(runnable);
            }
        }

        private void startProgress() {
            Runnable runnable = new Runnable(){
                public void run() {
                    progressMonitor.beginTask(Messages.FeatureTableSelectionProvider_loading_new_selection, 10);
                    progressMonitor.worked(1);
                }
            };
            if( Display.getCurrent()!=null ){
                runnable.run();
            }else{
                owner.getControl().getDisplay().asyncExec(runnable);
            }
        }

        private void obtainFidsFromSelection( IStructuredSelection structured,
                final Set<String> fids ) throws IOException, Abort {
            int usedTicks = 0;
            for( Iterator iter = structured.iterator(); iter.hasNext(); ) {
                
                if (progressMonitor.isCanceled())
                    throw new Abort();

                Object element = (Object) iter.next();

                if (element instanceof String) {
                    fids.add((String) element);
                } else if (element instanceof SimpleFeature) {
                    fids.add(((SimpleFeature) element).getID());
                } else if (element instanceof Id) {
                    String[] fids2 = ((Id) element).getIDs().toArray(new String[0]);
                    fids.addAll(Arrays.asList(fids2));
                } else if (element instanceof IAdaptable) {

                    obtainFidsFromAdaptable(fids, (IAdaptable) element);
                } else if (element instanceof Filter) {
                    UiPlugin
                            .log(
                                    "Tried to set selection on table but the selection contained a non-Fid Filter that could not " + //$NON-NLS-1$
                                            "adapt to a FeatureSource so the selection could not be applied.  Ignoring filter", //$NON-NLS-1$
                                    null);
                } else {
                    UiPlugin
                            .log(
                                    "Tried to set selection on table but the selection contained a " + element.getClass().getSimpleName() + //$NON-NLS-1$
                                            " but this type cannot be converted to Fids", null); //$NON-NLS-1$
                }
                if( usedTicks < 7 )
                    updateMonitor(1);
            }
            if( usedTicks < 7 )
                updateMonitor(7-usedTicks);
        }

        /**
         * Obtain fids from the features source if possible.
         * 
         * @param fids the set to add fids to
         * @param adaptable the object that adapted to the filter. hopefully can adapt to a feature
         *        source as well
         * @param filter filter to use for obtaining fids.
         * @throws IOException
         * @throws Abort
         */
        private void obtainFidsFromAdaptable( final Set<String> fids, IAdaptable adaptable )
                throws IOException, Abort {
            Filter filter = null;
            if (adaptable.getAdapter(Filter.class) != null)
                filter = (Filter) adaptable.getAdapter(Filter.class);
            else if (adaptable.getAdapter(Query.class) != null)
                filter = ((Query) adaptable.getAdapter(Query.class)).getFilter();

            if (filter == null)
                return;

            FeatureSource<SimpleFeatureType, SimpleFeature> source = null;
            if (adaptable.getAdapter(FeatureSource.class) != null) {
                source = (FeatureSource<SimpleFeatureType, SimpleFeature>) adaptable.getAdapter(FeatureSource.class);
            }

            if (source == null) {
                UiPlugin
                        .log(
                                "last resource run filter on features in table view...  Might now work since table view" + //$NON-NLS-1$
                                        " does not have any Geometries", new Exception()); //$NON-NLS-1$

                if (owner.getViewer() != null && owner.getViewer().getInput() != null)
                    for( SimpleFeature feature : ((FeatureTableContentProvider) owner.getViewer()
                            .getContentProvider()).features ) {
                        if (progressMonitor.isCanceled())
                            throw new Abort();
                        if (filter.evaluate(feature))
                            fids.add(feature.getID());
                    }
            } else {
                Query defaultQuery = new Query(source.getSchema().getName().getLocalPart(),
                        filter, new String[0]);
                // TODO: Remove this workaround in 2.6.1 (note this has no performance impact)
                Set<String> required = (Set) filter.accept( new FilterAttributeExtractor(), null );                
                defaultQuery.setPropertyNames( required.toArray(new String[0]) );
                
                // get features that are just fids no attributes
                FeatureCollection<SimpleFeatureType, SimpleFeature>  features = source.getFeatures(defaultQuery);
                long start=System.currentTimeMillis();
                
                FeatureIterator<SimpleFeature> featureIterator = features.features();
                try {
                    while( featureIterator.hasNext() ) {
                        if (progressMonitor.isCanceled())
                            throw new Abort();
                        if( System.currentTimeMillis()-start>500){
                            start=System.currentTimeMillis();
                            owner.getViewer().getControl().getDisplay().asyncExec(new Runnable(){
                                public void run() {
                                    progressMonitor.subTask(fids.size()+" selected"); //$NON-NLS-1$
                                }
                            });
                        }
                        fids.add(featureIterator.next().getID());
                    }
                } finally {
                    featureIterator.close();
                }
            }
        }
    }

    private static class Abort extends Exception {

        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;
    }

}
