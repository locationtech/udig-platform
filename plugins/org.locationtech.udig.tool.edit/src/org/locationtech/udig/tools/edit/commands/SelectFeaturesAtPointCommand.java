/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Searches a layer for an intersection between a mouse click and a feature and selects the features
 * if found.
 * <p>
 * This command makes use of its own small framework of:
 * <ul>
 * <li>{@link SelectionParameter}
 * <li>{@link SelectionStratagy}
 * <li>{@link DeselectionStratagy}
 * </ul>
 *
 * @author Jesse
 * @since 1.1.0
 */
public class SelectFeaturesAtPointCommand extends AbstractCommand implements UndoableMapCommand {

    // The size of the box that is searched. It uses the preferences settings value for the size of
    // the area
    private int SEARCH_SIZE = Platform.getPreferencesService().getInt(ProjectUIPlugin.ID,
            PreferenceConstants.FEATURE_SELECTION_SCALEFACTOR,
            PreferenceConstants.DEFAULT_FEATURE_SELECTION_SCALEFACTOR, null);

    // The attribute name to be used when multiple features are retrieved.
    private String ATTRIBUTE_NAME = Platform.getPreferencesService().getString(
            ProjectUIPlugin.ID, PreferenceConstants.FEATURE_ATTRIBUTE_NAME, "id", null);

    private EditToolHandler handler;
    private final Set<Class< ? extends Geometry>> acceptableClasses = new HashSet<Class< ? extends Geometry>>();
    private MapMouseEvent event;
    private Class<? extends Filter> filterType;

    private UndoableMapCommand command;
    private final SelectionParameter parameters;
    // boolean indicating that transformation problems have been reported so don't want to fill logs with the same
    // report
    boolean warned = false;

    //used for locking
    public final Object lock = new Object();

    public SelectFeaturesAtPointCommand( SelectionParameter parameterObject ) {
        this.parameters = parameterObject;
        this.handler = parameterObject.handler;
        this.event = parameterObject.event;
        this.acceptableClasses.addAll(Arrays.asList(parameterObject.acceptableClasses));

        this.filterType = parameterObject.filterType;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        if (command != null) {
            // indicates a redo
            command.run(monitor);
        } else {
            IToolContext context = handler.getContext();
            ILayer editLayer = handler.getEditLayer();

            EditBlackboard editBlackboard = handler.getEditBlackboard(editLayer);
            editBlackboard.startBatchingEvents();
            BlockingSelectionAnim animation = new BlockingSelectionAnim(event.x, event.y);
            AnimationUpdater.runTimer(context.getMapDisplay(), animation);
            FeatureIterator<SimpleFeature> iter = getFeatureIterator();
            try {

                if (iter.hasNext()) {
                    runSelectionStrategies(monitor, iter);
                } else {
                    runDeselectionStrategies(monitor);
                }

                setAndRun(monitor, command);
			} finally {
				try {
					if (iter != null) {
						iter.close();
					}
				} finally {
					if (animation != null) {
						animation.setValid(false);
						animation = null;
					}
				}
				editBlackboard.fireBatchedEvents();
			}
        }
    }

    /**
     * Gets a feature iterator on the results of a BBox query.   BBox is used because intersects occasionally throws a 
     * Side-conflict error so it is not a good query.  
     * 
     * However maybe a better way is to try intersects then if that fails do a bbox?
     * For now we do bbox and test it with intersects
     * 
     * @return Pair of an option containing the first feature if it exists, and an iterator with the rest
     */
    private FeatureIterator<SimpleFeature> getFeatureIterator() throws IOException {
        ILayer editLayer = parameters.handler.getEditLayer();
        FeatureStore<SimpleFeatureType, SimpleFeature> store = getResource(editLayer);
        
        // transforms the bbox to the layer crs 
        ReferencedEnvelope bbox = handler.getContext().getBoundingBox(event.getPoint(), SEARCH_SIZE);
        try {
        	bbox = bbox.transform(parameters.handler.getEditLayer().getCRS(), true);
        } catch (TransformException e) {
        	logTransformationWarning(e);
        } catch (FactoryException e) {
        	logTransformationWarning(e);
        }
        // creates a bbox filter using the bbox in the layer crs and grabs the features present in this bbox
        // but not the edit geoms
        FilterFactory2 factory = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
        Filter createBBoxFilter = createBBoxFilter(bbox, editLayer, filterType);
        List<Filter> idFilterList = new LinkedList<Filter>();
        for (EditGeom geom : handler.getCurrentEditBlackboard().getGeoms()) {
                if (geom.getFeatureIDRef().get() != null) {
                        idFilterList.add(factory.id(factory.featureId(geom.getFeatureIDRef().get())));
                }
        }
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = store.getFeatures(
                factory.and(
                        createBBoxFilter,
                        factory.not(factory.or(idFilterList))));

        FeatureIterator<SimpleFeature> reader = new IntersectTestingIterator(bbox, collection.features());
        
        return reader;
    }

	@SuppressWarnings("unchecked")
	private FeatureStore<SimpleFeatureType, SimpleFeature> getResource(
			ILayer editLayer) throws IOException {
		FeatureStore<SimpleFeatureType, SimpleFeature> store = editLayer.getResource(FeatureStore.class, null);
		return store;
	}

    private void runDeselectionStrategies( IProgressMonitor monitor ) {

        List<DeselectionStrategy> strategies = parameters.deselectionStrategies;
        UndoableComposite compositeCommand = new UndoableComposite();
        for( DeselectionStrategy strategy : strategies ) {
            strategy.run(monitor, parameters, compositeCommand);
        }
        this.command = compositeCommand;

    }

    private void runSelectionStrategies(final IProgressMonitor monitor, FeatureIterator<SimpleFeature> reader ) {
        final List<SelectionStrategy> strategies = parameters.selectionStrategies;
        final UndoableComposite compositeCommand = new UndoableComposite();
        compositeCommand.setName(Messages.SelectGeometryCommand_name);
        
        /*
        SimpleFeature firstFeature = reader.next();
        for( SelectionStrategy selectionStrategy : strategies ) {
          selectionStrategy.run(monitor, compositeCommand, parameters, firstFeature,
              true);
        }
        
        
        while (reader.hasNext()){
          SimpleFeature nextFeature = reader.next();
            for( SelectionStrategy selectionStrategy : strategies ) {
              selectionStrategy.run(monitor, compositeCommand, parameters, nextFeature,
                  false);
            }
        }
        */
        //code that enables selection of appropriate feature when multiple exist
        //in the point where mouse click takes place
        //******************************************************************/
        List<SimpleFeature> featureList =new ArrayList<SimpleFeature>();
        while (reader.hasNext()){
            featureList.add(reader.next());
        }      
        final SimpleFeature[] features = featureList.toArray(new SimpleFeature[]{});

        if (features.length == 1) {
            for( SelectionStrategy selectionStrategy : strategies ) {
                selectionStrategy.run(monitor, compositeCommand, parameters, features[0],
                        true);
            }
        } else if (features.length > 1) {
            PlatformGIS.syncInDisplayThread(new Runnable() {
                @Override
                public void run() {
                    final String attribName = FeatureUtils.getActualPropertyName(
                            features[0].getFeatureType(), ATTRIBUTE_NAME);
                    final Menu menu = new Menu(((ViewportPane) event.source).getControl().getShell(), SWT.POP_UP);
                    for (final SimpleFeature feat : features) {
                        MenuItem item = new MenuItem(menu, SWT.PUSH);
                        //SimpleFeature feature=iter.next();
                        Object attribValue = attribName != null ? feat.getAttribute(attribName) : null;
                        item.setText(attribValue != null ? 
                                attribValue.toString() : feat.getID());

                        //add selection listener to execute selection logic upon menu item selection
                        item.addSelectionListener(new SelectionAdapter() {                                              
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                for( SelectionStrategy selectionStrategy : strategies ) {
                                    selectionStrategy.run(monitor, compositeCommand, parameters, feat,
                                            true);
                                }
                                //notify mother thread lock 
                                synchronized (lock) {
                                    lock.notify();
                                }
                            }
                        });
                    }

                    //add menu listener to dispose menu upon hide
                    //dispose is called on a new UI thread
                    menu.addMenuListener(new MenuAdapter() {                                                
                        @Override
                        public void menuHidden(MenuEvent e) {
                            e.display.asyncExec(new Runnable(){
                                @Override public void run(){
                                    menu.dispose();
                                }
                            });

                        }
                    });

                    //add dispose listener that calls notify on lock
                    //object. This is needed in case no selection has 
                    //occurred otherwise the lock object will never 
                    //be relinquished
                    menu.addDisposeListener(new DisposeListener() {
                        @Override
                        public void widgetDisposed(DisposeEvent e) {
                            //notify mother thread lock 
                            synchronized (lock) {
                                lock.notify();
                            }       
                        }
                    });

                    //menu.setLocation(event.getPoint().x+10, event.getPoint().y+10);
                    menu.setVisible(true);
                }

            });
            //lock here waiting for users selection or menu popup dispose to occur
            //THIS IS needed so that compositeCommand below can be properly populated 
            synchronized (lock) {
                try {
                    //add a 60 sec timeout just to ensure that if something weird
                    //happens, the lock eventually will be released
                    lock.wait(60000); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //******************************************************************/

        this.command = compositeCommand;
    }

    private void logTransformationWarning( Exception e ) {
        if(!warned){
            EditPlugin.log("Error transforming bbox from viewportmodel CRS to LayerCRS", e); //$NON-NLS-1$
        }
    }

    /**
     * Creates A geometry filter for the given layer.
     * 
     * @param boundingBox in the same crs as the viewport model.
     * @return a Geometry filter in the correct CRS or null if an exception occurs.
     */
    public Filter createBBoxFilter( ReferencedEnvelope boundingBox, ILayer layer, Class<? extends Filter> filterType ) {
        FilterFactory2 factory = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
        if (!layer.hasResource(FeatureSource.class))
            return Filter.EXCLUDE;
        try {

            SimpleFeatureType schema = layer.getSchema();
            Name geom = getGeometryAttDescriptor(schema).getName();
            
            Filter bboxFilter =factory.bbox(factory.property(geom), boundingBox);
            

            return bboxFilter;
        } catch (Exception e) {
            ProjectPlugin.getPlugin().log(e);
            return Filter.EXCLUDE;
        }
    }

    private void setAndRun( IProgressMonitor monitor, UndoableMapCommand undoableComposite )
            throws Exception {
        undoableComposite.setMap(getMap());
        undoableComposite.run(monitor);
    }

    public String getName() {
        return Messages.SelectGeometryCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        command.rollback(monitor);
    }

    private static GeometryDescriptor getGeometryAttDescriptor( SimpleFeatureType schema ) {
        return schema.getGeometryDescriptor();
    }
    
    private static class IntersectTestingIterator implements FeatureIterator<SimpleFeature>{
    	private final FeatureIterator<SimpleFeature> wrappedIter;
    	private final ReferencedEnvelope bbox;
    	private SimpleFeature next;
    	
		public IntersectTestingIterator(ReferencedEnvelope bbox, FeatureIterator<SimpleFeature> wrapped) {
			this.wrappedIter = wrapped;
			this.bbox=bbox;
		}

		public void close() {
			wrappedIter.close();
		}

		public boolean hasNext() {
			if( next!=null ){
				return true;
			}
			
			while (wrappedIter.hasNext() && next==null ){
				SimpleFeature feature=wrappedIter.next();
				if(intersects(feature)){
					next=feature;
				}
			}
			
			// TODO Auto-generated method stub
			return next!=null;
		}

	    private boolean intersects( SimpleFeature feature ) {
	        GeometryDescriptor geomDescriptor = getGeometryAttDescriptor(feature.getFeatureType());
	        
	        Geometry bboxGeom = new GeometryFactory().toGeometry(bbox);

	        Geometry geom = (Geometry) feature.getAttribute(geomDescriptor.getName());

	        try{
	            return geom.intersects(bboxGeom);
	        }catch (Exception e) {
	            // ok so exception happened during intersection.  This usually means geometry is a little crazy
	            // what to do?...
	            EditPlugin.log("Can't do intersection so I'm assuming they intersect", e); //$NON-NLS-1$
	            return false;
	        }
	    }
		public SimpleFeature next() throws NoSuchElementException {
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			SimpleFeature f = next;
			next=null;
			return f;
		}
    	
    }
}
