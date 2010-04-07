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
package net.refractions.udig.project.ui.wizard.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.ui.export.CatalogExport;
import net.refractions.udig.catalog.ui.export.Data;
import net.refractions.udig.catalog.ui.export.ExportResourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardAdapter;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.core.AdapterUtil;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.ProgressManager;

import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.opengis.filter.Filter;

/**
 * Class for exporting the selected features to a shapefile or all of them if there is not selection
 * 
 * @author jesse
 * @since 1.1.0
 */
public class ExportFeatureSelection extends CatalogExport {

    public static class FeatureSelectionExportWizard extends WorkflowWizardAdapter {

        public FeatureSelectionExportWizard() {
            super(new ExportFeatureSelection().getWizard());
        }

    }
    
    private final class ExportLayerSelectionState extends ExportResourceSelectionState {
        @Override
        protected Object[] loadNonWorkbencSelection() {
            IMap activeMap = ApplicationGIS.getActiveMap();
            if (activeMap != ApplicationGIS.NO_MAP) {
                return new Object[]{activeMap};
            }
            return super.loadNonWorkbencSelection();
        }

        @Override
        protected Collection<Data> convertToGeoResource( Object object ) throws IOException {
            List<Data> data = new ArrayList<Data>();
            ILayer layer = AdapterUtil.instance.adaptTo(ILayer.class, object, ProgressManager
                    .instance().get());

            if (layer != null) {
                IGeoResource resource = layer.findGeoResource(FeatureSource.class);
                if (resource != null) {
                    Filter filter = layer.getFilter();
                    if (filter == Filter.EXCLUDE) {
                        // no selection provided; so lets do everything
                        filter = Filter.INCLUDE;
                    }
                    DefaultQuery query = new DefaultQuery(layer.getSchema().getTypeName(), filter);
                    Data data2 = new Data(resource, query);
                    data2.setChecked(layer.isVisible());
                    data.add(data2);
                }
            } else if (object instanceof IMap) {
                List<ILayer> mapLayers = ((IMap) object).getMapLayers();
                for( ILayer l : mapLayers ) {
                    // Adding to start each time so that top layer in map are at top of list.
                    data.addAll(0, convertToGeoResource(l));
                }
            } else {
                return super.convertToGeoResource(object);
            }
            return data;
        }
    }

    public WorkflowWizard getWizard() {
        return wizard;
    }
    
    /**
     * We want our subclass of {@link ExportResourceSelectionState} to be used instead
     */
    @Override
    protected Workflow createWorkflow() {
        ExportResourceSelectionState layerState = new ExportLayerSelectionState();
        Workflow workflow = new Workflow(new State[]{layerState});
        return workflow;
    }
    
    
    /**
     * Since we have a subclass of {@link ExportResourceSelectionState} we need to map
     * that state to the wizard page that is normally used for the {@link ExportResourceSelectionState} 
     */
    @Override
    protected Map<Class< ? extends State>, WorkflowWizardPageProvider> createPageMapping() {
        Map<Class< ? extends State>, WorkflowWizardPageProvider> pageMapping = super
                .createPageMapping();

        WorkflowWizardPageProvider page = pageMapping.get(ExportResourceSelectionState.class);
        pageMapping.put(ExportLayerSelectionState.class, page);
        
        return pageMapping;
    }

}
