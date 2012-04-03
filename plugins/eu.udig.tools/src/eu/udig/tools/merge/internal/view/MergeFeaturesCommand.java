/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 * 		Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 * 		http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to license under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.merge.internal.view;

import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.internal.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Merge Command Factory
 * <p>
 * This is a wrapper over UndoableComposite which is responsible to construct
 * the command list required to delete the source features and create the new
 * merged feature.
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 */
final class MergeFeaturesCommand implements UndoableMapCommand {

	private UndoableComposite	compositeCmd		= null;

	public static MergeFeaturesCommand getInstance(	ILayer layer,
													FeatureCollection<SimpleFeatureType, SimpleFeature> sourceFeatures,
													SimpleFeature mergedFeature) {

		return new MergeFeaturesCommand(layer, sourceFeatures, mergedFeature);
	}

	/**
	 * Creates the set of commands required to delete the selected features 
	 * and add a new the new feature (merge feature).
	 *   
	 * @param layer
	 * @param sourceFeatures selected features
	 * @param mergedFeature	 the new feature
	 */
	private MergeFeaturesCommand(	final ILayer layer,
									final FeatureCollection<SimpleFeatureType, SimpleFeature> sourceFeatures,
									final SimpleFeature mergedFeature) {

		assert layer != null;
		assert sourceFeatures != null;
		assert mergedFeature != null;

		// creates the command to delete selected features

		final EditCommandFactory cmdFactory = EditCommandFactory.getInstance();
		final IMap map = layer.getMap();

		List<UndoableMapCommand> cmdList = new LinkedList<UndoableMapCommand>();

		final FeatureIterator<SimpleFeature> iter = sourceFeatures.features();
		while (iter.hasNext()) {
			SimpleFeature feature = iter.next();

			UndoableMapCommand deleteCmd = cmdFactory.createDeleteFeature(feature, layer);
			deleteCmd.setMap(map);
			
			cmdList.add(deleteCmd);
		}
		iter.close();

		// adds the merge feature to new layer
		UndoableMapCommand addCmd = cmdFactory.createAddFeatureCommand(mergedFeature, layer);
		addCmd.setMap(map);
		
		cmdList.add(addCmd);

		assert cmdList.size() >= 3; // Almost two delete and one add command

		this.compositeCmd = new UndoableComposite(cmdList);

	}

	public void rollback(IProgressMonitor monitor) throws Exception {

		this.compositeCmd.rollback(monitor);
	}

	public MapCommand copy() {
		return this.compositeCmd.copy();
	}

	public String getName() {
		return "Merge Features Command"; //$NON-NLS-1$
	}

	public void setMap(final IMap map) {
		this.compositeCmd.setMap(map);
	}

	public Map getMap() {
		return this.compositeCmd.getMap();
	}

	public void run(IProgressMonitor monitor) throws Exception {
		this.compositeCmd.run(monitor);
	}

}
