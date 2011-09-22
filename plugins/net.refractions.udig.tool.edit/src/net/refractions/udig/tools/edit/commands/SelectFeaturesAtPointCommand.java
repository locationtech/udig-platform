/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditBlackboard;

import org.eclipse.core.runtime.IProgressMonitor;
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

    // The size of the box that is searched.  This is a 7x7 box of pixels.  
    private static final int SEARCH_SIZE = 7;
    private EditToolHandler handler;
    private final Set<Class< ? extends Geometry>> acceptableClasses = new HashSet<Class< ? extends Geometry>>();
    private MapMouseEvent event;
    private Class<? extends Filter> filterType;

    private UndoableMapCommand command;
    private final SelectionParameter parameters;
    // boolean indicating that transformation problems have been reported so don't want to fill logs with the same
    // report
    boolean warned = false;

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
        Filter createBBoxFilter = createBBoxFilter(bbox, editLayer, filterType);
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = store.getFeatures(createBBoxFilter);

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

    private void runSelectionStrategies( IProgressMonitor monitor, FeatureIterator<SimpleFeature> reader ) {
        List<SelectionStrategy> strategies = parameters.selectionStrategies;
        UndoableComposite compositeCommand = new UndoableComposite();
        compositeCommand.setName(Messages.SelectGeometryCommand_name);
        
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
