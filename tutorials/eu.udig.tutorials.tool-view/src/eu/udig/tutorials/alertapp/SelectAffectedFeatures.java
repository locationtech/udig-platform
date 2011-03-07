package eu.udig.tutorials.alertapp;

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.commands.selection.SelectCommand;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * An Operation that is only enabled when a view has:
 *  - The AlertAppContext as the selection object
 *  - There are envelopes on the ShowAlertMapGraphic Layer's blackboard. (Indicating the ShowAlertTool has been used)
 *  
 * The enablement properties are defined in the operation extension definition for this IOp
 * 
 * When the enablement properties are satisfied this IOp can be executed.  When it is it will
 * select all featues that intersect with the envelopes
 */
public class SelectAffectedFeatures implements IOp {

	@Override
	public void op(final Display display, Object target, IProgressMonitor monitor)
			throws Exception {
		AlertAppContext context = (AlertAppContext) target;
		
		@SuppressWarnings("unchecked")
		List<ReferencedEnvelope> envelopes = (List<ReferencedEnvelope>) context.getShowAlertsLayer().getBlackboard().get(ShowAlertsMapGraphic.ALERTS_KEY);
		
		GeometryFactory fac = new GeometryFactory();
		Geometry geom = null;
		for (ReferencedEnvelope env : envelopes) {
			Geometry envGeom = fac.toGeometry(env);
			if(geom == null) {
				geom = envGeom;
			} else {
				geom = geom.union(envGeom);
			}
		}

		ILayer layer =  context.getVectorLayer();

		FilterFactory2 filterFac = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
		Name geomName = layer.getResource(FeatureSource.class, monitor).getSchema().getGeometryDescriptor().getName();
		Filter selectionFilter = filterFac.intersects(filterFac.property(geomName), filterFac.literal(geom));
		
		SelectCommand updateCommand = new SelectCommand(layer, selectionFilter);
		
		layer.getMap().sendCommandASync(updateCommand);
		
	}

}
