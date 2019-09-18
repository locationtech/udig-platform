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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.udig.core.IProvider;
import org.locationtech.udig.core.feature.AdaptableFeatureCollection;
import org.locationtech.udig.internal.ui.Trace;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.ui.internal.Messages;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * ContentProvider that agressively listens to FeatureCollection (using FeatureSource FeatureEvents).
 * 
 * @author Jody Garnett
 */
class FeatureTableContentProvider implements ILazyContentProvider, IProvider<Collection<SimpleFeature>> {

    private static final IProgressMonitor NULL = new NullProgressMonitor();
    /** FeatureContentProvider owningFeatureTableControl field */
    private final FeatureTableControl owningFeatureTableControl;

    private volatile IProgressMonitor monitor = NULL;
    private IProvider<IProgressMonitor> progressMonitorProvider;

    public FeatureTableContentProvider( FeatureTableControl control, IProvider<IProgressMonitor> progressMonitorProvider ) {
        owningFeatureTableControl = control;
        this.progressMonitorProvider=progressMonitorProvider;
    }
    
    /**
     * Listens for changes in FeatureSource, updates the viewer if content provider changes.
     * <p>
     * If we were extra cool we could filter the incoming FeatureEvents based on if they
     * are actually included in our ContentProvider. As it is we will refresh everything.
     */
    private FeatureListener listener = new FeatureListener() {

        @Override
        public void changed(FeatureEvent event) {
            if (listener == null) {
                event.getFeatureSource().removeFeatureListener(this);
                return;
            }

            TableViewer viewer = FeatureTableContentProvider.this.owningFeatureTableControl
                    .getViewer();

            switch (event.getType()) {
            case ADDED:
                try {
                    SimpleFeatureCollection changed = (SimpleFeatureCollection) event
                            .getFeatureSource().getFeatures(event.getFilter());
                    FeatureIterator<SimpleFeature> iterator = changed.features();
                    try {
                        while (iterator.hasNext()) {
                            SimpleFeature newFeature = iterator.next();
                            features.add(newFeature);
                        }
                        viewer.setItemCount(event.getFeatureSource().getCount(Query.ALL));
                        viewer.getTable().clearAll();
                    } finally {
                        iterator.close();
                    }
                } catch (IOException accessError) {
                }
                break;
            case REMOVED:
                for (Iterator<SimpleFeature> iter = features.iterator(); iter.hasNext();) {
                    SimpleFeature feature = iter.next();
                    if (event.getFilter().evaluate(feature)) {
                        iter.remove(); // event indicated this feature has been removed
                    }
                }
                viewer.setItemCount(features.size());
                viewer.getTable().clearAll();
                break;
            case CHANGED:
                try {
                    SimpleFeatureCollection changed = (SimpleFeatureCollection) event
                            .getFeatureSource().getFeatures(event.getFilter());

                    FeatureIterator<SimpleFeature> iterator = changed.features();
                    try {
                        while (iterator.hasNext()) {
                            SimpleFeature changedFeature = iterator.next();
                            SCAN: for (ListIterator<SimpleFeature> iter = features.listIterator(); iter
                                    .hasNext();) {
                                SimpleFeature item = iter.next();
                                if (item.getID().equals(changedFeature.getID())) {
                                    iter.set(changedFeature);
                                    break SCAN;
                                }
                            }
                        }
                        viewer.setItemCount(event.getFeatureSource().getCount(Query.ALL));
                        viewer.getTable().clearAll();
                    } finally {
                        iterator.close();
                    }
                } catch (IOException accessError) {
                }
                viewer.getTable().clearAll();
                break;
            case COMMIT:
                //TBD
                break;
            case ROLLBACK:
                //TBD
                break;

            default:
                break;
            }
        }
    };

    /** Memory bound cache of features for table.
     * May be sorted according to FID or any of the attributes so don't rely on any given order because
     * its liable to change.  User Lookup instead for quickly locating a features
     */
    List<SimpleFeature> features = Collections.synchronizedList( new ArrayList<SimpleFeature>());

    /**
     * Contains same features as Features but sorted by id
     */
    Map<String, SimpleFeature> lookup = new HashMap<String, SimpleFeature>();
    /**
     * If true then an edit has occurred and the table is being updated.
     */
    private volatile boolean updating=false;
    private boolean disposed=false;
    /**
     * Does nothing.
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     * @param viewer
     * @param oldInput
     * @param newInput
     */
    public void inputChanged( Viewer viewer, Object oldInput, final Object newInput ) {

        synchronized (this) {
            if (monitor != NULL) {
                monitor.setCanceled(true);

                UiPlugin.trace(Trace.FEATURE_TABLE, FeatureTableContentProvider.class, 
                        "#inputChanged(): cancelled monitor", null); //$NON-NLS-1$
                try {
                    PlatformGIS.wait(500, -1, new WaitCondition(){
                        
                        public boolean isTrue() {
                            return monitor == NULL;
                        }
                        
                    }, this);
                } catch (InterruptedException e) {
                    UiPlugin.log("Interrupted", e); //$NON-NLS-1$
                    return;
                }
            }
            features.clear();
            
            if (oldInput != null && oldInput instanceof AdaptableFeatureCollection) {
                AdaptableFeatureCollection old = (AdaptableFeatureCollection) oldInput;
                FeatureSource source = (FeatureSource) old.getAdapter(FeatureSource.class);
                if (source != null) {
                    source.removeFeatureListener(listener);
                }
            }
            if (newInput != null && newInput instanceof AdaptableFeatureCollection) {
                AdaptableFeatureCollection input = (AdaptableFeatureCollection) newInput;
                FeatureSource source = (FeatureSource) input.getAdapter(FeatureSource.class);
                if (source != null) {
                    source.addFeatureListener(listener);
                } else {
                    UiPlugin.trace(UiPlugin.ID, FeatureTableContentProvider.class,
                            "Unable to adapt to FeatureSource (to listen for changes):" + input,
                            null);
                }
            } else {
                UiPlugin.trace(UiPlugin.ID, FeatureTableContentProvider.class,
                        "Unable to access FeatureSource (to listen for changes):" + newInput, null);
            }

            if (newInput == null)
                return;

            monitor = progressMonitorProvider.get();
            monitor.setCanceled(false);
            owningFeatureTableControl.message(null);
            owningFeatureTableControl.notifyLoadingListeners(new LoadingEvent(false, monitor, true));

            final FeatureCollection<SimpleFeatureType, SimpleFeature> input = (FeatureCollection<SimpleFeatureType, SimpleFeature>) newInput;
            Display display = Display.getCurrent();
            owningFeatureTableControl.message(Messages.FeatureTableContentProvider_loading, display
                    .getSystemColor(SWT.COLOR_INFO_BACKGROUND), display
                    .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
            PlatformGIS.run(new ContentLoader(input));
        }
    }
    public void dispose() {
        synchronized( this ){
            if( disposed )
                return; 
            
            disposed=true;
        }
        features.clear();

        if (monitor != NULL) {
            monitor.setCanceled(true);
            try {
                PlatformGIS.wait(200, -1, new WaitCondition(){

                    public boolean isTrue() {
                        return monitor == NULL;
                    }

                }, this);
            } catch (InterruptedException e) {
                UiPlugin.log("Interrupted", e); //$NON-NLS-1$
                return;
            }
        }
    }
    public Collection<SimpleFeature> get(Object... params) {
        return features;
    }
    public void updateElement( int index ) {
        if (index >= features.size()) {
            owningFeatureTableControl.getViewer().replace("", index); //$NON-NLS-1$
        } else if (monitor != NULL && index == 0 && !updating) {
            owningFeatureTableControl.getViewer().replace(FeatureTableControl.LOADING, 0);
        } else {
            int resolvedIndex=index;
            //commented fragment below not needed since features list 
            //is already sorted using Collections.sort 
            //if( owningFeatureTableControl.getViewer().getTable().getSortDirection()==SWT.UP )
            //    resolvedIndex=features.size()-index-1;
            SimpleFeature feature = features.get(resolvedIndex);
            owningFeatureTableControl.getViewer().replace(feature, index);
        }
    }

    private class ContentLoader implements ISafeRunnable {

        private final FeatureCollection<SimpleFeatureType, SimpleFeature> input;

        public ContentLoader( FeatureCollection<SimpleFeatureType, SimpleFeature> input ) {
            this.input = input;
        }

        public void handleException( Throwable exception ) {
            UiPlugin.log("Error loading features", exception); //$NON-NLS-1$
        }

        public void run() throws Exception {
            if (cancel())
                return;
            UiPlugin.trace(Trace.FEATURE_TABLE, FeatureTableContentProvider.class, 
                    "Starting ContentLoader", null); //$NON-NLS-1$
            setEnabled(false);
            int i = 0;
            final int[] monitorUpdate = new int[1];
            monitorUpdate[0] = 0;
            boolean updated = false;
            long start = System.currentTimeMillis();
            FeatureIterator<SimpleFeature> iterator = null;
            try {
                iterator = input.features();
                while( iterator.hasNext() ) {
                    if (System.currentTimeMillis() - start > 500) {
                        if (!updated) {
                            updated = true;
                            updateTable(input, false);
                        }
                        start = System.currentTimeMillis();
                        updateMonitor(i, monitorUpdate);
                    }
                    if (cancel())
                        return;
                    SimpleFeature next = iterator.next();
                    features.add(next);
                    lookup.put(next.getID(), next);
                    i++;
                }
            } catch (OutOfMemoryError error) {
                error(input, i + " " + Messages.FeatureTableContentProvider_outOfMemory, true); //$NON-NLS-1$
                UiPlugin.log("Out of memory error in table view", error); //$NON-NLS-1$
                return;
            } catch (IndexOutOfBoundsException e) {
                error(input, Messages.FeatureTableContentProvider_unexpectedErro
                        + " " + Messages.FeatureTableContentProvider_probablecharseterror, false);
                UiPlugin.log("error loading features in table view", e); //$NON-NLS-1$
                return;
            } catch (Throwable t) {
                error(input, Messages.FeatureTableContentProvider_unexpectedErro
                        + t.getLocalizedMessage(), false);
                UiPlugin.log("error loading features in table view", t); //$NON-NLS-1$
                return;
            } finally {
                if (iterator != null)
                    iterator.close();
                UiPlugin.trace(Trace.FEATURE_TABLE, FeatureTableContentProvider.class, 
                        "Ending ContentLoader, Cancel state is:"+monitor.isCanceled(), null); //$NON-NLS-1$
            }
            if (!cancel()) {
                updateTable(input, true);
                setEnabled(true);
            }
        }

        /**
         * will setenable and set an error message on the feature table control.
         */
        private void error( final FeatureCollection<SimpleFeatureType, SimpleFeature> input, final String string,
                final boolean clearFeatures ) {
            UiPlugin.trace(Trace.FEATURE_TABLE, FeatureTableContentProvider.class, 
                    "ContentLoader#error: Error occurred in ContentLoader:\n"+string, null); //$NON-NLS-1$

            final Display display = owningFeatureTableControl.getViewer().getControl().getDisplay();
            display.asyncExec(new Runnable(){
                public void run() {
                    monitor.setCanceled(true);
                }
            });
            done();
            display.asyncExec(new Runnable(){
                public void run() {
                    owningFeatureTableControl.message(string, display
                            .getSystemColor(SWT.COLOR_INFO_BACKGROUND), display
                            .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                }
            });
        }

        private void updateMonitor( int i, final int[] monitorUpdate ) {
            final int j = i;
            owningFeatureTableControl.getViewer().getControl().getDisplay().asyncExec(
                    new Runnable(){
                        public void run() {
                            monitor
                                    .subTask(Messages.FeatureTableContentProvider_loadedFeatures
                                            + j);
                            monitor.worked(j - monitorUpdate[0]);
                            monitorUpdate[0] = j;
                        }
                    });
        }

        /**
         * If enabled it will finish the {@link IProgressMonitor}, null it out, notify listeners
         * and enable the table control. if not enabled it will begin the progress task and disable
         * the Table control
         * 
         * @param enabled
         */
        private void setEnabled( final boolean enabled ) {

            final Scrollable control = owningFeatureTableControl.getViewer().getTable();
            final int size;
            if (!enabled)
                size = input.size();
            else
                size = IProgressMonitor.UNKNOWN;

            control.getDisplay().asyncExec(new Runnable(){
                public void run() {

                    UiPlugin.trace(Trace.FEATURE_TABLE, FeatureTableContentProvider.class, 
                            "ContentLoader#setEnabled():"+enabled, null); //$NON-NLS-1$
                    if (enabled) {
                        done();
                    } else {
                        monitor.beginTask(Messages.FeatureTableControl_loading1
                                + input.getSchema().getName().getLocalPart()
                                + Messages.FeatureTableControl_loading2, size + 1);
                        monitor.worked(1);
                    }
                }
            });
        }
        private void done() {
            final Table control = owningFeatureTableControl.getViewer().getTable();
            Runnable runnable = new Runnable(){
                public void run() {

                    UiPlugin.trace(Trace.FEATURE_TABLE, FeatureTableContentProvider.class, 
                            "ContentLoader#done()|run():", null); //$NON-NLS-1$
                    
                    monitor.done();
                    synchronized (FeatureTableContentProvider.this) {
                        monitor = NULL;
                        FeatureTableContentProvider.this.notifyAll();
                        if (!control.isDisposed())
                            control.getVerticalBar().setEnabled(true);
                        if (control.getItemCount() > 0) {
                            owningFeatureTableControl.getViewer().replace(features.get(0), 0);
                        }
                        owningFeatureTableControl.notifyLoadingListeners(new LoadingEvent(monitor
                                .isCanceled(), null, false));
                    }
                }
            };
            if (Display.getCurrent() != control.getDisplay()) {
                control.getDisplay().asyncExec(runnable);
            } else {
                runnable.run();
            }
        }

        private void updateTable( final FeatureCollection<SimpleFeatureType, SimpleFeature> newInput, final boolean done ) {
            final Table table = owningFeatureTableControl.getViewer().getTable();
            table.getDisplay().asyncExec(new Runnable(){
                public void run() {

                    UiPlugin.trace(Trace.FEATURE_TABLE, FeatureTableContentProvider.class, 
                            "ContentLoader#updateTable(): done="+done, null); //$NON-NLS-1$
                    
                    owningFeatureTableControl.message(null);
                    int size = features.size();
                    owningFeatureTableControl.getViewer().setItemCount(size);
                    if (!done && !table.isDisposed())
                        table.getVerticalBar().setEnabled(false);
                }
            });
        }

        private boolean cancel() {
            synchronized (FeatureTableContentProvider.this) {
                if (monitor.isCanceled() || PlatformUI.getWorkbench().isClosing()) {
                    done();
                    return true;
                }
                return false;
            }
        }

    }

    /**
     * Updates the features that have the same feature ID to match the new feature or adds the features if they are not part of the
     * current collection.  
     *
     * @param features2 the feature collection that contains the modified or new features.  
     */
    public void update( FeatureCollection<SimpleFeatureType, SimpleFeature> features2 ) throws IllegalArgumentException{
        if( features==null )
            return;
        
        if( !owningFeatureTableControl.features.getSchema().equals(features2.getSchema()) )
            throw new IllegalArgumentException( "The feature type of the SimpleFeature Collection passed as a parameter does not have the same" + //$NON-NLS-1$
                    " feature type as the features in the table so it cannot be used to update the features." ); //$NON-NLS-1$
        
        ContentUpdater updater=new ContentUpdater(features2);
        PlatformGIS.run(updater);
    }
    
    SimpleFeature findFeature(String featureId ){
        return lookup.get(featureId);
    }
    
    private class ContentUpdater implements ISafeRunnable{

        private FeatureCollection<SimpleFeatureType, SimpleFeature> newFeatures;
        private int loaded=0;

        public ContentUpdater( FeatureCollection<SimpleFeatureType, SimpleFeature> features2 ) {
            this.newFeatures=features2;
        }

        public void handleException( Throwable exception ) {
            UiPlugin.log("Exception while updating the features in the FeatureTableControl", exception); //$NON-NLS-1$
        }

        public void run() throws Exception {
            synchronized( FeatureTableContentProvider.this){
                updating=true;
                if (monitor != NULL) {
                    // wait until finished loading
                    try {
                        PlatformGIS.wait(500, -1, new WaitCondition(){
    
                            public boolean isTrue() {
                                return monitor == NULL;
                            }
    
                        }, FeatureTableContentProvider.this);
                    } catch (InterruptedException e) {
                        UiPlugin.log("Interrupted", e); //$NON-NLS-1$
                        return;
                    }
                }
                
                startLoading();
                
                SimpleFeatureType schema = newFeatures.getSchema();
                FeatureIterator<SimpleFeature> iter=newFeatures.features();
                try{
                    boolean featuresWereAdded=false;
                    while( iter.hasNext() ){
                        if( monitor.isCanceled() )
                            break;
                        SimpleFeature newValue = iter.next();
                        SimpleFeature oldValue = findFeature(newValue.getID());
                        if( oldValue==null ){
                            featuresWereAdded=true;
                            features.add(newValue);
                            lookup.put(newValue.getID(), newValue);
                        }else{
                            for( int i = 0; i < schema.getAttributeCount(); i++ ) {
                                oldValue.setAttribute(i, newValue.getAttribute(i));
                            }
                        }
                        loaded++;
                        updateMonitor(loaded+Messages.FeatureTableContentProvider_updatingFeatures);
                    }
                    
                    updateTable(featuresWereAdded);
                }finally{
                    iter.close();
                }
                
            }
        }
        
        private void updateTable(final boolean featuresWereAdded) {
            final Table table = owningFeatureTableControl.getViewer().getTable();
            table.getDisplay().asyncExec(new Runnable(){
                public void run() {
                    if( featuresWereAdded ){
                        updateMonitor(Messages.FeatureTableContentProvider_sortTable);
                        owningFeatureTableControl.sort(false);
                        owningFeatureTableControl.getViewer().setItemCount(features.size());
                    }else{
                        table.clearAll();
                    }
                    monitor.done();
                    boolean cancelled = monitor.isCanceled();
                    monitor=NULL;
                    updating=false;
                    synchronized (FeatureTableContentProvider.this) {
                        FeatureTableContentProvider.this.notifyAll();
                    }
                    owningFeatureTableControl.notifyLoadingListeners(new LoadingEvent(cancelled, null, false));
                }
            });
        }


        private void updateMonitor(final String subTask) {
            Display display = owningFeatureTableControl.getControl().getDisplay();
            display.asyncExec(new Runnable(){
                public void run() {
                    monitor.subTask(subTask);
                    monitor.worked(1);
                }
            });
        }

        private void startLoading() {
            Display display = owningFeatureTableControl.getControl().getDisplay();
            display.asyncExec(new Runnable(){
                public void run() {
                    owningFeatureTableControl.notifyLoadingListeners(new LoadingEvent(false, monitor, true));
                    monitor=progressMonitorProvider.get();
                    monitor.setCanceled(false);
                    monitor.beginTask(Messages.FeatureTableContentProvider_updateTaskName, IProgressMonitor.UNKNOWN);
                }
            });
        }
    }
    
    /**
     * Checks the lookup table and the feature list to ensure that they have the same number of features and the same features.
     * An exception will be thrown otherwise.
     */
    public void assertInternallyConsistent(){
        if( features.size()!=lookup.size())
            throw new AssertionError("lookup table has "+lookup.size()+" features while feature list has "+features.size()+" features"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        for( SimpleFeature feature : features ) {
            SimpleFeature lookupFeature = lookup.get(feature.getID());
            if( lookup==null ){
                throw new AssertionError("Lookup table is missing "+feature); //$NON-NLS-1$
            }
            if( lookupFeature!=feature )
                throw new AssertionError("Lookup table contains: "+lookupFeature+" while feature list contains"+feature+".  They are" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        " not the same instance"); //$NON-NLS-1$
        }
    }
    /**
     * Removes the selected features (the features selected by the owning {@link FeatureTableControl}).
     * @return returns a collection of the deleted features
     */
    public FeatureCollection<SimpleFeatureType, SimpleFeature> deleteSelection() {
        final DefaultFeatureCollection deletedFeatures = new DefaultFeatureCollection();
        Runnable updateTable = new Runnable(){
            @SuppressWarnings("unchecked")
            public void run() {
                Collection<String> selectionFids = owningFeatureTableControl.getSelectionProvider().getSelectionFids();
                
                for( Iterator<SimpleFeature> iter = features.iterator(); iter.hasNext(); ) {
                    SimpleFeature feature =  iter.next();
                    if( selectionFids.contains(feature.getID()) ){
                        deletedFeatures.add(feature);
                        iter.remove();
                        lookup.remove(feature.getID());
                    }
                }
                
                selectionFids.clear();
                owningFeatureTableControl.getViewer().getTable().clearAll();
            }
        };
        
        if( Display.getCurrent()==null ){
            PlatformGIS.syncInDisplayThread(owningFeatureTableControl.getControl().getDisplay(), updateTable);
        }else{
            updateTable.run();
        }
        
        return deletedFeatures;
    }
}
