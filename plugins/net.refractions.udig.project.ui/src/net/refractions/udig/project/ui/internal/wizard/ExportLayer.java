/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2007, Refractions Research Inc.
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
package net.refractions.udig.project.ui.internal.wizard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.ui.export.CatalogExport;
import net.refractions.udig.catalog.ui.export.CatalogExportDelegateWizard;
import net.refractions.udig.catalog.ui.export.Data;
import net.refractions.udig.catalog.ui.export.ExportResourceSelectionPage;
import net.refractions.udig.catalog.ui.export.ExportResourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.catalog.ui.workflow.Workflow.State;
import net.refractions.udig.core.AdapterUtil;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.ImageConstants;
import net.refractions.udig.project.ui.internal.Images;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IExportWizard;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.filter.Filter;

/**
 * Exports layers and maps as part of the export functionality.
 *
 * @author Jesse
 */
public class ExportLayer extends CatalogExportDelegateWizard implements
        IExportWizard {


    private static final MyCatalogExport CONFIGURATION = new MyCatalogExport();

    public ExportLayer() {
        super(CONFIGURATION.createWorkflow(), CONFIGURATION.createPageMapping());
        setWindowTitle( Messages.ExportLayer_WizardTitle );
    }


    final static class MyCatalogExport extends CatalogExport {
        private final class ExportResourceSelectionStateExtension extends
                ExportResourceSelectionState {
            @Override
            protected Object[] loadNonWorkbencSelection() {
                IMap activeMap = ApplicationGIS.getActiveMap();
                if( activeMap!=ApplicationGIS.NO_MAP){
                    return new Object[]{activeMap};
                }
                return super.loadNonWorkbencSelection();
            }

            @Override
            protected Collection<Data> convertToData(Object object)
                    throws IOException {
                List<Data> data = new ArrayList<Data>();
                ILayer layer = AdapterUtil.instance.adaptTo(ILayer.class, object, ProgressManager.instance().get());

                if( layer != null  ){
                    IGeoResource resource = layer.findGeoResource(FeatureSource.class);
                    if( resource !=null ){
                        Filter filter = layer.getFilter();
                        if( filter == Filter.ALL ){
                            // no selection provided; so lets do everything
                            filter = Filter.NONE;
                        }
                        DefaultQuery query = new DefaultQuery(layer.getSchema().getTypeName(), filter );
                        Data data2 = new Data(resource, query);
                        data2.setChecked(layer.isVisible());
                        data.add(data2);
                    }
                }else if( object instanceof IMap){
                        List<ILayer> mapLayers = ((IMap)object).getMapLayers();
                        for (ILayer l : mapLayers) {
                            // Adding to start each time so that top layer in map are at top of list.
                            data.addAll(0,convertToData(l));
                        }
                }else{
                    return super.convertToData(object);
                }
                return data;
            }
        }

        MyCatalogExport(){
            // I'm just updating this code.  Apparently this class is here to disable initializing
            //  not sure why... Oh I think it is because they don't want the
            // dialog created.  They just want to be able to obtain the default workflow and
            // page mapping.
            super(false);
        }

        @Override
        protected WorkflowWizard createWorkflowWizard(Workflow workflow,
                Map<Class<? extends State>, WorkflowWizardPageProvider> map) {
            return super.createWorkflowWizard(workflow, map);
        }

        protected Workflow createWorkflow() {
            ExportResourceSelectionState layerState = new ExportResourceSelectionStateExtension();
            Workflow workflow = new Workflow(new Workflow.State[]{layerState});
            return workflow;
        }

        @Override
        protected Map<Class<? extends State>, WorkflowWizardPageProvider> createPageMapping() {

            Map<Class<? extends State>, WorkflowWizardPageProvider> map = super.createPageMapping();

            String title = Messages.ExportLayer_PageTitle;
            ImageDescriptor banner = Images.getDescriptor("wizban/exportselection_wiz.gif"); //$NON-NLS-1$

            ExportResourceSelectionPage page = new ExportResourceSelectionPage( "Select Layers", title, banner ); //$NON-NLS-1$
            page.setMessage( Messages.ExportLayer_Message0);
            map.put(ExportResourceSelectionStateExtension.class, new BasicWorkflowWizardPageFactory(page));

            //TODO: add export support for formats other than shapefile

            return map;

        }
    }


}
