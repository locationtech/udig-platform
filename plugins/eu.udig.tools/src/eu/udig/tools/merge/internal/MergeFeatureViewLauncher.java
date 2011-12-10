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
package eu.udig.tools.merge.internal;

import java.text.MessageFormat;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.animation.MessageBubble;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;

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
import eu.udig.tools.merge.internal.view.MergeView;


/**
 * Responsible of showing the merge view.
 * <p>
 * This is class is responsible to get the inputs required by merge tool. It
 * decide if the input are valid, and then will launch the view.
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 */
public final class MergeFeatureViewLauncher {

	private String			message;
	private SimpleFeature[]	sourceFeatures	= null;
	private IToolContext	toolContext		= null;
	private MergeContext	mergeContext	= null;

	/**
	 * a new instance of MergeFeatureBehaviour
	 * 
	 * @param toolContext
	 *            the context of tool
	 * @param mergeContext
	 */
	public MergeFeatureViewLauncher(final IToolContext toolContext, MergeContext mergeContext) {

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
	public void setSourceFeatures(final SimpleFeature[] features) {

		assert features != null : "got null argument";

		this.sourceFeatures = new SimpleFeature[features.length];
		System.arraycopy(features, 0, sourceFeatures, 0, features.length);
	}

	/**
	 * Open the merge view and set its parameters. When all the parameters are
	 * set, the view will populate its widget with the correspondent data.
	 */
	public void launchView() throws IllegalStateException {

		assert this.sourceFeatures != null : "soueceFeatures is null";
		assert this.mergeContext != null : "merge context is null";

		MergeFeatureBuilder builder = createMergeBuilder();
		MergeView view;

		ApplicationGIS.getView(true, MergeView.id);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(MergeView.id);
		view = (MergeView) viewPart;

		assert view != null : "view is null";

		view.setMergeContext(mergeContext);
		view.setSourceFeatures(sourceFeatures);
		view.setBuilder(builder);

	}

	/**
	 * 
	 * @return
	 * @throws IllegalStateException
	 */
	private MergeFeatureBuilder createMergeBuilder() throws IllegalStateException {

		SimpleFeatureType type = sourceFeatures[0].getFeatureType();
		final Class<?> expectedGeometryType = type.getGeometryDescriptor().getType().getBinding();
		Geometry union;
		union = GeometryUtil.geometryUnion(DataUtilities.collection(sourceFeatures));
		try {
			checkGeomCollection(union, expectedGeometryType);

			union = GeometryUtil.adapt(union, (Class<? extends Geometry>) expectedGeometryType);
			final ILayer layer = this.toolContext.getSelectedLayer();
			MergeFeatureBuilder mergeBuilder = new MergeFeatureBuilder(sourceFeatures, union, layer);
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
	 * Valid the merge data:
	 * <ul>
	 * <li>Must select two or more feature</li>
	 * <li>The features selected are polygons they must intersect</li>
	 * </ul>
	 * 
	 * @return true if the input are OK
	 */
	public boolean isValid() {

		this.message = ""; //$NON-NLS-1$
		boolean valid = true;

		// Must select one or more feature
		int selectionCount = sourceFeatures.length;
		if (selectionCount < 1) {
			this.message = Messages.MergeFeatureBehaviour_select_one_or_more;
			valid = false;
		}
		return valid;
	}

	/**
	 * displays the error message
	 */
	public void handleError(IToolContext context, MapMouseEvent e) {

		AnimationUpdater.runTimer(context.getMapDisplay(), new MessageBubble(e.x, e.y, this.message, PreferenceUtil
					.instance().getMessageDisplayDelay())); //$NON-NLS-1$

	}

	/**
	 * Closes the view.
	 */
	public void closeView() {

		ApplicationGIS.getView(false, MergeView.id);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(MergeView.id);
		page.hideView(viewPart);

	}

}
