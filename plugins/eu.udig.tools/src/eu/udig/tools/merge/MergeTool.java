/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
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

import java.util.List;
import java.util.Set;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.activator.EditStateListenerActivator;
import net.refractions.udig.tools.edit.activator.ResetAllStateActivator;
import net.refractions.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import net.refractions.udig.tools.edit.enablement.ValidToolDetectionActivator;
import net.refractions.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.udig.tools.internal.i18n.Messages;
import eu.udig.tools.internal.util.StatusBar;
import eu.udig.tools.merge.internal.MergeContext;
import eu.udig.tools.merge.internal.MergeEventBehaviour;
import eu.udig.tools.merge.internal.view.MergeView;

/**
 * Merge the features in bounding box
 * <p>
 * This implementation is based in BBoxSelection. The extension add
 * behavior object which displays the merge dialog.
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class MergeTool extends AbstractEditTool {

	private MergeContext		mergeContext	= new MergeContext();

	private static final String	EXTENSION_ID	= "es.axios.udig.ui.editingtools.merge.MergeTool";	//$NON-NLS-1$

	public String getExtensionID() {

		return EXTENSION_ID;
	}

	@Override
	public void setActive(final boolean active) {

		super.setActive(active);
		IToolContext context = getContext();
		if (active && context.getMapLayers().size() > 0) {

			String message = Messages.MergeTool_select_features_to_merge;
			StatusBar.setStatusBarMessage(context, message);
		} else {
			StatusBar.setStatusBarMessage(context, "");//$NON-NLS-1$
		}
		if (!active) {

			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {

					// When the tool is deactivated, hide the view.
					ApplicationGIS.getView(false, MergeView.id);
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IViewPart viewPart = page.findView(MergeView.id);
					page.hideView(viewPart);
				}
			});
			mergeContext.initContext();

		}
	}

	@Override
	protected void initAcceptBehaviours(List<Behaviour> acceptBehaviours) {

		// nothing
	}

	@Override
	protected void initActivators(Set<Activator> activators) {

		activators.add(new EditStateListenerActivator());
		activators.add(new ResetAllStateActivator());
	}

	@Override
	protected void initCancelBehaviours(List<Behaviour> cancelBehaviours) {

		cancelBehaviours.add(new DefaultCancelBehaviour());
	}

	@Override
	protected void initEnablementBehaviours(List<EnablementBehaviour> enablementBehaviours) {

		enablementBehaviours.add(new ValidToolDetectionActivator(new Class[] {
				Geometry.class,
				LineString.class,
				MultiLineString.class,
				Polygon.class,
				MultiPolygon.class,
				Point.class,
				MultiPoint.class,
				GeometryCollection.class }));
		enablementBehaviours.add(new WithinLegalLayerBoundsBehaviour());
	}

	@Override
	protected void initEventBehaviours(EditToolConfigurationHelper helper) {

		helper.add(new MergeEventBehaviour(mergeContext));
		helper.done();
	}

}
