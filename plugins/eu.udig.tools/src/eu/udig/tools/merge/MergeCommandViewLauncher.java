/**
 * 
 */
package eu.udig.tools.merge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Or;

import com.vividsolutions.jts.geom.Envelope;

import eu.udig.tools.internal.i18n.Messages;
import eu.udig.tools.internal.ui.util.DialogUtil;

/**
 * 
 * @author Mauricio Pazos
 *
 */
final class MergeCommandViewLauncher  extends AbstractCommand implements UndoableMapCommand{

	
	private static final Logger LOGGER = Logger.getLogger(MergeCommandViewLauncher.class.getName());
	private MergeContext mergeContext;
	private IToolContext toolContext;


	public MergeCommandViewLauncher(MergeContext mergeContext,  IToolContext toolContext) {

		this.mergeContext = mergeContext;
		this.toolContext = toolContext;
	
	}
	@Override
	public void run(IProgressMonitor monitor) throws Exception {

		viewLauncher(this.toolContext);
		
	}


	@Override
	public String getName() {
		return MergeCommandViewLauncher.class.getName();
	}


	@Override
	public void rollback(IProgressMonitor monitor) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	

	/**
	 * <p>
	 * 
	 * <pre>
	 * Get the features under the bbox area.
	 * Validates the features in bounding box and launch the view.
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param toolContext
	 * @throws MergeToolException 
	 * 
	 */
	private void viewLauncher(IToolContext toolContext) throws MergeToolException {

		final String viewTitle = Messages.MergeTool_title_tool;
		List<SimpleFeature> selectedFeatures;

		try {
			List<Envelope> bboxList = new ArrayList<Envelope>();
			bboxList.addAll(mergeContext.getBoundList());
			// gets the features in bounding box(from this bbox and previous bbox) FIXME why!!!
			selectedFeatures = Util.retrieveFeaturesInBBox(bboxList, toolContext);
			if( selectedFeatures.isEmpty() ){
				LOGGER.warning("nothing was selected to merge"); //$NON-NLS-1$
				return;
			}
			bboxList.clear();
			
			// get the features only for this bbox
//			bboxList.add(this.lastBounds); //  FIXME I think this is enough
//			lastSelectedFeatures = retrieveFeaturesInBBox(bboxList, toolContext);
//
//			if (e.isControlDown()) {
//				// control is down, so those feature are supposed to be deleted.
//				mergeContext.addDeletedFeature(lastSelectedFeatures);
//				mergeContext.removeEnvelope(this.lastBounds);
//			} else {
//				// if a feature is removed from the view of the merge, and here
//				// is added again, update the list.
//				mergeContext.updateDeletedFeatureList(lastSelectedFeatures);
//			}
//			 exclude the deleted features from the allFeatures list.
//			if (this.mergeContext.getDeletedFeatures().size() > 0) {
//				selectedFeatures.removeAll( this.mergeContext.getDeletedFeatures() );
//			}
		} catch (IOException e1) {
			final String msg = Messages.MergeTool_failed_getting_selection;
			DialogUtil.openError(viewTitle, msg);
			throw (RuntimeException) new RuntimeException(msg).initCause(e1);
		}
		assert selectedFeatures != null;

		final MergeViewManager mergeViewManager = new MergeViewManager(toolContext, this.mergeContext);

		mergeViewManager.setSourceFeatures(selectedFeatures);

		if (!mergeViewManager.isValid()) {

			mergeViewManager.closeMergeView();
			
			mergeContext.initContext();
			throw new MergeToolException("The merge is not possible");
		}
		try {
			
			mergeViewManager.openMergeView();
			
		} catch (IllegalStateException ise) {
			
			DialogUtil.openError(viewTitle, ise.getMessage());
			mergeViewManager.closeMergeView();
			
			mergeContext.initContext();
			
			throw ise;
		}
	}




}
