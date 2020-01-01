/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.split;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.command.factory.EditCommandFactory;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.GeometryCreationUtil;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.IllegalFilterException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.Intersects;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;

import org.locationtech.udig.tools.feature.split.CannotSplitException;
import org.locationtech.udig.tools.feature.split.SplitFeatureBuilder;
import org.locationtech.udig.tools.feature.split.SplitFeatureBuilderFailException;
import org.locationtech.udig.tools.feature.util.GeoToolsUtils;
import org.locationtech.udig.tools.internal.i18n.Messages;
import org.locationtech.udig.tools.internal.ui.util.LayerUtil;
import org.locationtech.udig.tools.internal.ui.util.MapUtil;

/**
 * Undoable map command that splits a collection of Features with a given Line.
 * <p>
 * The splitting line is taken from the EditToolHandler's
 * {@link EditToolHandler#getCurrentShape()}
 * </p>
 * <p>
 * That line will then be used to cut the selected layer's geometries that
 * intersect it. If a selection is set on the layer, it will be respected, and
 * thus the command will apply to those features that are either selected and
 * intersect the splitting line.
 * </p>
 * <p>
 * For those Feature geometries that were split, the original Feature will be
 * deleted and as many new Features as geometries resulted of the split
 * operation will be created, maintaining the original Feature's attributes
 * other than the default geometry.
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 * @see SplitFeatureBuilder
 */
final class SplitFeaturesCommand extends AbstractCommand implements UndoableMapCommand {

	private static final String className = SplitFeaturesCommand.class.getName();
    
	/* Edit tool handler used by the tools. */
	private EditToolHandler		handler;
	/* The selected layer. */
	private ILayer				selectedLayer;

	/**
	 * Composite command used to aggregate the set of feature delete and create
	 * commands
	 */
	private UndoableComposite	composite;

	/**
	 * Creates a new split command to split the features of the
	 * <code>handler</code>'s selected layer with the line present in the
	 * handler's {@link EditToolHandler#getCurrentShape() current shape}.
	 * 
	 * @param handler
	 *            an EditToolHandler containing the context for the command to
	 *            run. For instance, the selected layer and the line shape drawn
	 *            by the user.
	 */
	public SplitFeaturesCommand(EditToolHandler handler) {

		final ILayer selectedLayer = handler.getContext().getSelectedLayer();

		assert selectedLayer.getSchema() != null;
		Class<?> geometryBinding = selectedLayer.getSchema().getGeometryDescriptor().getType().getBinding();
		assert geometryBinding != Point.class;
		assert geometryBinding != MultiPoint.class;

		assert selectedLayer.hasResource(FeatureStore.class);

		this.handler = handler;
		this.selectedLayer = selectedLayer;
	}

	public String getName() {

		return "Split Features Command"; //$NON-NLS-1$
	}

	/**
	 * Runs the split operation over the provided features and builds this
	 * command's state as an {@link UndoableComposite} with the list of commands
	 * needed to remove the split features and create the new ones, then
	 * delegates the execution to that {@link UndoableComposite} which is
	 * maintained to be reused by {@link #rollback(IProgressMonitor)}.
	 */
	public void run(IProgressMonitor monitor) throws Exception {

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		IProgressMonitor prepMonitor = SubMonitor.convert(monitor, 80);
		prepMonitor.beginTask("Splitting features", 1); //$NON-NLS-1$

		LineString splitter = getSplittingLineInMapCRS(handler);
		assert splitter.getUserData() instanceof CoordinateReferenceSystem;

		FeatureCollection<SimpleFeatureType, SimpleFeature> featuresToSplit = getFeaturesToSplit(splitter);
		prepMonitor.worked(2);

		final List<UndoableMapCommand> undoableCommands = new ArrayList<UndoableMapCommand>();

		List<UndoableMapCommand> commands = buildCommandList(featuresToSplit, splitter);
		undoableCommands.addAll(commands);
		prepMonitor.worked(3);

		prepMonitor.done();

		if (undoableCommands.size() == 0) {
            ProjectPlugin.log("The split did not apply to any feature"); //$NON-NLS-1$
		    
			throw new IllegalArgumentException(Messages.SplitFeaturesCommand_did_not_apply_to_any_feature);
		}
		IProgressMonitor splitMonitor = SubMonitor.convert(monitor, 20);
		composite = new UndoableComposite(undoableCommands);

		// cascade setMap on the aggregate commands
		composite.setMap(getMap());
		composite.run(splitMonitor);

		repaint();
	}

	private void repaint() {
		handler.setCurrentShape(null);
		handler.setCurrentState(EditState.NONE);

		final ILayer selectedLayer = handler.getContext().getSelectedLayer();
		EditBlackboard editBlackboard = handler.getEditBlackboard(selectedLayer);
		editBlackboard.clear();

		handler.repaint();
	}

	/**
	 * Returns the line drawn as the splitting line, transformed to JTS
	 * LineString in the current {@link IMap map}'s CRS.
	 * 
	 * @param handler
	 *            the {@link EditToolHandler} from where to grab the current
	 *            shape (the one drawn as the cutting line)
	 * @return The split line transformed to the map CRS.
	 * 
	 * @throws SplitFeaturesCommandException
	 */
	private  LineString getSplittingLineInMapCRS(final EditToolHandler handler)
		throws SplitFeaturesCommandException {

	    final PrimitiveShape currentShape = handler.getCurrentShape();
	    final LineString lineInLayerCrs = GeometryCreationUtil.createGeom(LineString.class, currentShape, false);
	    final CoordinateReferenceSystem mapCrs = MapUtil.getCRS(handler.getContext().getMap());

	    LineString splittingLine = reprojectSplitLine(lineInLayerCrs, mapCrs);

	    return splittingLine;
	}

	/**
	 * Reprojects the split line to the target CRS
	 * 
	 * @param lineInLayerCrs
	 * @param targetCrs
	 * @return the split line in the target crs
	 * @throws SplitFeaturesCommandException
	 */
	private  LineString reprojectSplitLine(final LineString lineInLayerCrs, final CoordinateReferenceSystem targetCrs) throws SplitFeaturesCommandException {
	    
	    try{
	        
            CoordinateReferenceSystem splitLineCrs = (CoordinateReferenceSystem) lineInLayerCrs.getUserData();
	        
	        if(splitLineCrs == null){
	            
	            final ILayer selectedLayer = this.handler.getContext().getSelectedLayer();
	            final CoordinateReferenceSystem layerCrs = LayerUtil.getCrs(selectedLayer);

	            assert this.handler.getCurrentShape() != null;
	            assert layerCrs != null;
	            
	            splitLineCrs = layerCrs;
	        }
	        
            LineString splitlineInMapCRS = (LineString) GeoToolsUtils.reproject(lineInLayerCrs, splitLineCrs, targetCrs);
            
            splitlineInMapCRS.setUserData(targetCrs);

	        
            return splitlineInMapCRS;
	    } catch (Exception e) {
        
            e.printStackTrace();
            ProjectPlugin.log("It does not possible transform the split line to the map crs", e); //$NON-NLS-1$
            throw new SplitFeaturesCommandException(Messages.SplitFeaturesCommand_cannot_transform_the_splitline_crs);  
	    }
	}

	/**
	 * Returns the features to be split by <code>splittingLine</code>.
	 * <p>
	 * To get the features, <code>splittingLine</code> is transformed to the
	 * layer's CRS and an Intersects filter is made with the result.
	 * </p>
	 * 
	 * @param splittingLine  The split line.
	 * 
	 * @return The features that are under the influence of the split line.
	 * 
	 * @throws SplitFeaturesCommandException
	 */
	private FeatureCollection<SimpleFeatureType, SimpleFeature> getFeaturesToSplit(final LineString splittingLine)
		throws SplitFeaturesCommandException {

	    try{
	        Filter extraFilter = createSplittingLineFilter(splittingLine);
	        FeatureCollection<SimpleFeatureType, SimpleFeature> selection = LayerUtil.getSelectedFeatures(selectedLayer,
	                    extraFilter);
	        return selection;
	        
        } catch (Exception e) {
            e.printStackTrace();
            ProjectPlugin.log("The split tool can not retrieve the features to be split", e); //$NON-NLS-1$
            throw new SplitFeaturesCommandException(Messages.SplitFeaturesCommand_cannot_retrieve_features_to_be_split);  
	    }
	}

	/**
	 * Creates the filter based on the layer filter and the split line. It would
	 * be an intersect filter between the split line and the layer, taking into
	 * account the own features that are selected in the layer.
	 * 
	 * @param splittingLine
	 *            The split line.
	 * @return The intersect filter between the split line and the layer.
	 * 
	 * @throws SplitFeaturesCommandException
	 */
	private Filter createSplittingLineFilter(LineString splittingLine)
		throws SplitFeaturesCommandException {

		try {

	        Filter filter = selectedLayer.getFilter();
	        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
	        Intersects intersectsFilter;

			final CoordinateReferenceSystem layerCrs = LayerUtil.getCrs(selectedLayer);
			final Geometry splitLineGeom = reprojectSplitLine(splittingLine, layerCrs);

			SimpleFeatureType schema = selectedLayer.getSchema();
			String geomName = schema.getGeometryDescriptor().getLocalName();

			intersectsFilter = ff.intersects(ff.property(geomName), ff.literal(splitLineGeom));

			if (Filter.EXCLUDE.equals(filter)) {
	            filter = intersectsFilter;
	        } else {
	            filter = ff.and(filter, intersectsFilter);
	        }
	        return filter;
	        
        } catch (IllegalFilterException e) {
            e.printStackTrace();
            ProjectPlugin.log("The split command found an illegal filter", e); //$NON-NLS-1$
            throw new SplitFeaturesCommandException(Messages.SplitFeaturesCommand_fail);
            
        } 
	}

	/**
	 * Creates the list of commands that are going to be executed on
	 * {@link #run(IProgressMonitor)}
	 * <p>
	 * NOTE: visible only to be accessed by unit tests, framework uses
	 * {@link #run(IProgressMonitor)} and you should never see this class from
	 * client code.
	 * </p>
	 * 
	 * @return A command list will all the operations that the split tool will
	 *         do.
	 * @throws SplitFeaturesCommandException
	 * @throws IOException
	 */
	private List<UndoableMapCommand> buildCommandList(	FeatureCollection<SimpleFeatureType, SimpleFeature> featuresToSplit,
														LineString splitterInMapCrs)
		throws SplitFeaturesCommandException {

		ProjectPlugin.log(className + " - Split original: " + splitterInMapCrs.toText()); //$NON-NLS-1$
	
		final EditCommandFactory cmdFac = EditCommandFactory.getInstance();
		final FeatureIterator<SimpleFeature> featureToSplitIterator = featuresToSplit.features();
		final CoordinateReferenceSystem mapCRS = handler.getContext().getCRS();

		try {
			
			List<SimpleFeature> originalFeatureList = asList(featureToSplitIterator);
			
            if(originalFeatureList.isEmpty()){
                throw new SplitFeaturesCommandException(Messages.SplitFeaturesCommand_did_not_apply_to_any_feature); 
            }
	        ProjectPlugin.log(className + " - Split using CRS: " + mapCRS.toString()); //$NON-NLS-1$

			SplitFeatureBuilder builder = SplitFeatureBuilder.newInstance(originalFeatureList, splitterInMapCrs, mapCRS);
			try {
				builder.buildSplit();
			} catch (CannotSplitException e) {
				throw new SplitFeaturesCommandException(e.getMessage());
			}

			// make the requires list of commands to update the affected features
			final List<UndoableMapCommand> undoableCommands = new LinkedList<UndoableMapCommand>();
			
			// delete the features that suffered split
			List<SimpleFeature> featuresThatSufferedSplit = builder.getFeaturesThatSufferedSplit();

			for (SimpleFeature feature : featuresThatSufferedSplit) {

			    UndoableMapCommand command = cmdFac.createDeleteFeature(feature, selectedLayer);
			    
				undoableCommands.add(command);
				ProjectPlugin.log(className + " - Delete original feature: " + ((Geometry) feature.getDefaultGeometry()).toText()); //$NON-NLS-1$
			}
			// add the new features
			List<SimpleFeature> splitResult = builder.getSplitResult();
			for (SimpleFeature feature : splitResult) {
			    UndoableMapCommand command = cmdFac.createAddFeatureCommand(feature, selectedLayer);
				undoableCommands.add(command);
				ProjectPlugin.log(className + " - Split result: " + ((Geometry) feature.getDefaultGeometry()).toText()); //$NON-NLS-1$
			}
			// modify the neighbor features.
			builder.buildNeighbours();
			List<SimpleFeature> modifiedNeighbour = builder.getNeighbourResult();
			for (SimpleFeature neighbor : modifiedNeighbour) {

			    for( SimpleFeature original : originalFeatureList ) {
                    if(original.getID().equals(neighbor.getID())){
                        UndoableMapCommand command = new ModifyGeometryFeatureCommand(original.getID(), (Geometry)neighbor.getDefaultGeometry(), (Geometry)original.getDefaultGeometry(), this.selectedLayer);
                        undoableCommands.add(command);

                        ProjectPlugin.log(className + " - Neighbour result: " + ((Geometry) neighbor.getDefaultGeometry()).toText()); //$NON-NLS-1$
                    }
                }
			}
	        return undoableCommands;

		} catch (SplitFeatureBuilderFailException sfbe) {
			sfbe.printStackTrace();
			ProjectPlugin.log("This split transaction is not possible.", sfbe); //$NON-NLS-1$
			throw new SplitFeaturesCommandException(Messages.SplitFeaturesCommand_split_transaction_failed); 

		} finally {
			if (featureToSplitIterator != null){
				featureToSplitIterator.close();
			}
		}
	}

        /**
         * Transforms the feature collection to a List
         * 
         * @param featureIterator
         * @return the feature list
         * @throws SplitFeaturesCommandException
         */
        private List<SimpleFeature> asList(
    	    final FeatureIterator<SimpleFeature> featureIterator)
    	    throws SplitFeaturesCommandException {
    
            List<SimpleFeature> featureList = new ArrayList<SimpleFeature>();
        
            while (featureIterator.hasNext()) {
        
        	    SimpleFeature feature = featureIterator.next();
        
        	    Geometry defaultGeometry = (Geometry) feature.getDefaultGeometry();
        	    ProjectPlugin.log(className
        		    + "- Original feature: " + defaultGeometry.toText()); //$NON-NLS-1$
        
        	    if (!defaultGeometry.isValid()) {
        		ProjectPlugin
        			.log(className
        				+ "- Original feature has invalid geometry: " + defaultGeometry.toText()); //$NON-NLS-1$
        		throw new SplitFeaturesCommandException(
        			Messages.SplitFeaturesCommand_the_feature_has_invalid_geometry);
        	    }
        	    featureList.add(feature);
        
            }
            return featureList;
        }

	/**
	 * Rolls back the split operation by delegating to the
	 * {@link UndoableComposite} created in {@link #run(IProgressMonitor)}, if
	 * any. Exits gracefully otherwise.
	 */
	public void rollback(IProgressMonitor monitor) throws Exception {

		if (composite != null) {

			composite.rollback(monitor);
		}
	}

}
