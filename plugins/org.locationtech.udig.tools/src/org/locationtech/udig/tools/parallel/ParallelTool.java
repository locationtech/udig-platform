/*******************************************************************************
 * Copyright (c) 2006,2012,2013 County Council of Gipuzkoa, Department of Environment
 *                              and Planning and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Aritz Davila (Axios) - initial API, implementation, and documentation
 *    Mauricio Pazos (Axios) - initial API, implementation, and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.parallel;

import java.util.List;
import java.util.Set;

import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.tools.edit.AbstractEditTool;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.EnablementBehaviour;
import org.locationtech.udig.tools.edit.activator.DrawCurrentGeomVerticesActivator;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator;
import org.locationtech.udig.tools.edit.activator.EditStateListenerActivator;
import org.locationtech.udig.tools.edit.activator.GridActivator;
import org.locationtech.udig.tools.edit.activator.ResetAllStateActivator;
import org.locationtech.udig.tools.edit.activator.SetRenderingFilter;
import org.locationtech.udig.tools.edit.activator.SetSnapBehaviourCommandHandlerActivator;
import org.locationtech.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import org.locationtech.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import org.locationtech.udig.tools.edit.behaviour.DrawCreateVertexSnapAreaBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SetSnapSizeBehaviour;
import org.locationtech.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import org.locationtech.udig.tools.edit.enablement.ValidToolDetectionActivator;
import org.locationtech.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import org.locationtech.udig.tools.parallel.internal.ParallelContext;
import org.locationtech.udig.tools.parallel.internal.ParallelPreview;
import org.locationtech.udig.tools.parallel.internal.behaviour.PrecisionToolAcceptBehaviour;
import org.locationtech.udig.tools.parallel.internal.behaviour.SetInitialPointEventBehaviour;
import org.locationtech.udig.tools.parallel.internal.behaviour.SetReferenceFeatureBehaviour;
import org.locationtech.udig.tools.parallel.view.ParallelParametersView;

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
