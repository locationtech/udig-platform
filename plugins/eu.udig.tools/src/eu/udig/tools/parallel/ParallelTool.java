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
package eu.udig.tools.parallel;

import java.util.List;
import java.util.Set;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.activator.DrawCurrentGeomVerticesActivator;
import net.refractions.udig.tools.edit.activator.DrawGeomsActivator;
import net.refractions.udig.tools.edit.activator.EditStateListenerActivator;
import net.refractions.udig.tools.edit.activator.GridActivator;
import net.refractions.udig.tools.edit.activator.ResetAllStateActivator;
import net.refractions.udig.tools.edit.activator.SetRenderingFilter;
import net.refractions.udig.tools.edit.activator.SetSnapBehaviourCommandHandlerActivator;
import net.refractions.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import net.refractions.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import net.refractions.udig.tools.edit.behaviour.DrawCreateVertexSnapAreaBehaviour;
import net.refractions.udig.tools.edit.behaviour.SetSnapSizeBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import net.refractions.udig.tools.edit.enablement.ValidToolDetectionActivator;
import net.refractions.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

//TODO remove older imports
//import es.axios.udig.ui.editingtools.precisionparallels.internal.ParallelContext;
import eu.udig.tools.parallel.internal.ParallelContext;
import eu.udig.tools.parallel.internal.ParallelPreview;
import eu.udig.tools.parallel.internal.behaviour.PrecisionToolAcceptBehaviour;
import eu.udig.tools.parallel.internal.behaviour.SetInitialPointEventBehaviour;
import eu.udig.tools.parallel.internal.behaviour.SetReferenceFeatureBehaviour;
import eu.udig.tools.parallel.view.ParallelParametersView;
//import es.axios.udig.ui.editingtools.precisionparallels.internal.ParallelPreview;
//import es.axios.udig.ui.editingtools.precisionparallels.internal.behaviour.SetInitialPointEventBehaviour;
//import es.axios.udig.ui.editingtools.precisionparallels.internal.behaviour.SetReferenceFeatureBehaviour;
//import es.axios.udig.ui.editingtools.precisionparallels.view.ParallelParametersView;

/**
 * Creates a precision parallel line.
 * 
 * With a reference line and an initial point, create a parallel line which
 * could change the distance between the reference line.
 * 
 * FIXME If it doesn't use the snap, the map doesn't repaint correctly.
 * 
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @author Marco Foi (www.mcfoi.it) [porting to uDig core]
 * 
 */
public class ParallelTool extends AbstractEditTool {

	private ParallelContext			parallelContext	= new ParallelContext();
	private ParallelPreview			parallelPreview	= null;
	private ParallelParametersView	view			= null;

	@Override
	public void setActive(boolean active) {
		super.setActive(active);

		if (active) {

			parallelContext.setHandler(getHandler());
			parallelContext.initContext();
			parallelContext.setEditBlackBoard(getHandler().getEditBlackboard(getHandler().getEditLayer()));
			parallelContext.deleteObservers();

			parallelPreview = ParallelPreview.getInstance();
			parallelPreview.setParameters(getContext(), getHandler(), parallelContext);
			parallelContext.addObserver(parallelPreview);

			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					// run the view, set the parallelcontext and add the
					// parameters view as observer.
					ApplicationGIS.getView(true, ParallelParametersView.id);
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IViewPart viewPart = page.findView(ParallelParametersView.id);
					view = (ParallelParametersView) viewPart;
					assert view != null : "view is null"; //$NON-NLS-1$
					view.setParallelContext(parallelContext);

				}

			});
		} else {
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					// When the tool is deactivated, hide the view.
					ApplicationGIS.getView(false, ParallelParametersView.id);
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IViewPart viewPart = page.findView(ParallelParametersView.id);
					page.hideView(viewPart);
				}
			});
		}
	}

	@Override
	protected void initActivators(Set<Activator> activators) {
		activators.add(new EditStateListenerActivator());
		activators.add(new DrawGeomsActivator(DrawGeomsActivator.DrawType.LINE));
		activators.add(new SetSnapBehaviourCommandHandlerActivator());
		activators.add(new DrawCurrentGeomVerticesActivator());
		// activators.add(new DrawOrthoAxesActivator());
		activators.add(new ResetAllStateActivator());
		activators.add(new SetRenderingFilter());
		activators.add(new GridActivator());
	}

	@Override
	protected void initAcceptBehaviours(List<Behaviour> acceptBehaviours) {
		acceptBehaviours.add(new AcceptChangesBehaviour(LineString.class, false));
		acceptBehaviours.add(new PrecisionToolAcceptBehaviour(parallelContext));
		// acceptBehaviours.add(new DeselectEditShapeAcceptBehaviour());
	}

	@Override
	protected void initCancelBehaviours(List<Behaviour> cancelBehaviours) {
		cancelBehaviours.add(new DefaultCancelBehaviour());
	}

	@Override
	protected void initEventBehaviours(EditToolConfigurationHelper helper) {

		helper.add(new DrawCreateVertexSnapAreaBehaviour());
		// helper.add(new DrawSnapAreaBehaviour());

		helper.add(new SetReferenceFeatureBehaviour(parallelContext));

		helper.add(new SetInitialPointEventBehaviour(parallelContext));

		helper.add(new SetSnapSizeBehaviour());
		helper.add(new AcceptOnDoubleClickBehaviour());
		helper.done();

	}

	@Override
	protected void initEnablementBehaviours(List<EnablementBehaviour> enablementBehaviours) {
		enablementBehaviours.add(new WithinLegalLayerBoundsBehaviour());
		enablementBehaviours.add(new ValidToolDetectionActivator(new Class[] {
				Geometry.class,
				Polygon.class,
				MultiPolygon.class,
				LineString.class,
				MultiLineString.class }));

	}
}
