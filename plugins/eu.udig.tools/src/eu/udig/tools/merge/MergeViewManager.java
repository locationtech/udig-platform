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
package eu.udig.tools.merge;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.animation.MessageBubble;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.DataUtilities;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.udig.tools.geometry.internal.util.GeometryUtil;
import eu.udig.tools.internal.i18n.Messages;
import eu.udig.tools.merge.internal.view.MergeFeatureBuilder;
import eu.udig.tools.merge.internal.view.MergeView;


/**
 * This class is responsible of showing the merge view.
 * <p>
 * This is class is responsible to get the inputs required by merge tool. It
 * decide if the input are valid, and then will launch the view.
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 * 
 */
final class MergeViewManager {

	private String			message;
	private List<SimpleFeature>	sourceFeatures	= new LinkedList<SimpleFeature>();
	private IToolContext	toolContext		= null;
	private MergeContext	mergeContext	= null;

	/**
	 * a new instance of MergeFeatureBehaviour
	 * 
	 * @param toolContext
	 *            the context of tool
	 * @param mergeContext
	 */
	public MergeViewManager(final IToolContext toolContext, MergeContext mergeContext) {

		assert toolContext != null;
		assert mergeContext != null;

		this.toolContext = toolContext;
		this.mergeContext = mergeContext;
	}

	/**
	 * Set the features to merge
	 * 
	 * @param features
	 */
	public void setSourceFeatures(final List<SimpleFeature> features) {

		assert features != null : "got null argument"; //$NON-NLS-1$

		this.sourceFeatures = features;
	}

	/**
	 * Opens the merge view and set its parameters. When all the parameters are
	 * set, the view will populate its widget with the correspondent data.
	 */
	public void openMergeView() throws IllegalStateException {

		assert this.sourceFeatures != null : "sourceFeatures is null"; //$NON-NLS-1$
		assert this.mergeContext != null : "merge context is null"; //$NON-NLS-1$

		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {

				MergeView view = (MergeView)ApplicationGIS.getView(true, MergeView.ID);
				if(view == null){
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					view = (MergeView) page.findView(MergeView.ID);
				}
				assert view != null : "view is null"; //$NON-NLS-1$

				view.setMergeContext(mergeContext);
				view.setSourceFeatures(sourceFeatures);

				MergeFeatureBuilder builder = createMergeBuilder(sourceFeatures);
				view.setBuilder(builder);
			}
		});
		

	}

	/**
	 * Creates the merge builder using the set of features selected 
	 * @return {@link MergeFeatureBuilder}
	 * @throws IllegalStateException
	 */
	private MergeFeatureBuilder createMergeBuilder(final List<SimpleFeature> sourceFeatures) throws IllegalStateException {

		try {
			SimpleFeatureType type = sourceFeatures.get(0).getFeatureType();
			final Class<?> expectedGeometryType = type.getGeometryDescriptor()
					.getType().getBinding();
			
			Geometry union;
			
			union = GeometryUtil.geometryUnion(DataUtilities.collection(sourceFeatures));
			checkGeomCollection(union, expectedGeometryType);

			union = GeometryUtil.adapt(union,(Class<? extends Geometry>) expectedGeometryType);
			final ILayer layer = this.toolContext.getSelectedLayer();
			
			MergeFeatureBuilder mergeBuilder = new MergeFeatureBuilder(
					sourceFeatures, union, layer);
			return mergeBuilder;
			
		} catch (IllegalArgumentException iae) {
			throw new IllegalStateException(iae.getMessage());
		}
	}

	private void checkGeomCollection(Geometry union, Class<?> expectedGeometryType) throws IllegalArgumentException {

		if (Polygon.class.equals(expectedGeometryType) && (MultiPolygon.class.equals(union.getClass()))
					&& union.getNumGeometries() > 1) {

			final String msg = MessageFormat.format(Messages.GeometryUtil_DonotKnowHowAdapt, union.getClass()
						.getSimpleName(), expectedGeometryType.getSimpleName());

			throw new IllegalArgumentException(msg);
		}
	}

	/**
	 * It should be two or more feature selected</li>
	 * 
	 * @return true if the merge operation could be executed
	 */
	public boolean isValid() {

		this.message = ""; //$NON-NLS-1$

		// Must select one or more feature
		if (this.sourceFeatures.size() < 1) {
			this.message = Messages.MergeFeatureBehaviour_select_one_or_more;
			return false;
		}
		return true;
	}

	/**
	 * Closes the view.
	 */
	public void closeMergeView() {

		ApplicationGIS.getView(false, MergeView.ID);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(MergeView.ID);
		page.hideView(viewPart);

	}

}
