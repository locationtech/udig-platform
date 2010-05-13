package net.refractions.udig.project.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.ui.CatalogImport;
import net.refractions.udig.catalog.internal.ui.ResourceSelectionPage;
import net.refractions.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import net.refractions.udig.catalog.ui.workflow.DataSourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.ResourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A CatalogImport that selects individual resources to form the layers
 * of a new map (or add to an existing map).
 * @since 1.1
 */
public class MapImport extends CatalogImport {

    private int layerPosition = -1;

    public void setLayerPosition( int layerPosition ) {
        this.layerPosition = layerPosition;
    }

    @Override
    protected Workflow createWorkflow() {
        DataSourceSelectionState dsState = new DataSourceSelectionState(true);
        ResourceSelectionState rsState = new ResourceSelectionState();

        Workflow workflow = new Workflow(new State[]{dsState, rsState});
        return workflow;
    }

    @Override
    protected java.util.Map<Class< ? extends State>, WorkflowWizardPageProvider> createPageMapping() {
        Map<Class< ? extends State>, WorkflowWizardPageProvider> pageMapping = super.createPageMapping();
        return addResourceSelectionPage(pageMapping);
    }

    /**
     * Appends the ResorceSelectionPage to a {@link WorkflowWizard}'s pageMapping
     *
     * @param pageMapping the starting page mapping.
     * 
     * @return the same mapping with the new entry added
     */
    public static java.util.Map<Class< ? extends State>, WorkflowWizardPageProvider> addResourceSelectionPage(
            Map<Class< ? extends State>, WorkflowWizardPageProvider> pageMapping ) {
        java.util.Map<Class< ? extends State>, WorkflowWizardPageProvider> map = pageMapping;
        ResourceSelectionPage page = new ResourceSelectionPage(Messages.MapImport_selectLayers);
        map.put(ResourceSelectionState.class, new BasicWorkflowWizardPageFactory(page)); 

        return map;
    }

    @Override
    protected WorkflowWizard createWorkflowWizard( Workflow workflow,
            java.util.Map<Class< ? extends State>, WorkflowWizardPageProvider> map ) {
        return new MapImportWizard(workflow, map);
    }

    /**
     * Provided workflow is used to add Data to the map.
     */
    protected class MapImportWizard extends CatalogImport.CatalogImportWizard {

        public MapImportWizard( Workflow workflow,
                java.util.Map<Class< ? extends State>, WorkflowWizardPageProvider> map ) {
            super(workflow, map);
            setWindowTitle("Add Data");
        } 
        
        @Override
        public boolean canFinish() {
            return super.canFinish();
        }
        
        @Override
        protected boolean performFinish( IProgressMonitor monitor ) {
            String name = Messages.MapImport_createMap; 
            monitor.beginTask(name, IProgressMonitor.UNKNOWN);
            monitor.setTaskName(name);
            boolean superFinished = super.performFinish(monitor);
            if (!superFinished)
                return superFinished;

            ResourceSelectionState state = getWorkflow().getState(ResourceSelectionState.class);
            java.util.Map<IGeoResource, IService> resourceMap = state.getResources();

            if (resourceMap == null || resourceMap.isEmpty())
                return false;

            // add the resources to the mapa
            List<IGeoResource> resourceList = new ArrayList<IGeoResource>(resourceMap.keySet());

            // reverse the list as ApplicationGIS.addLayersToMap add the first layer at the bottom
            // and so on
            Collections.reverse(resourceList);

            monitor.setTaskName(Messages.MapImport_addingLayersTask); 
            ApplicationGIS.addLayersToMap(ApplicationGIS.getActiveMap(), resourceList, layerPosition, null, true);

            return true;
        }

        @Override
        protected boolean isShowCatalogView() {
            return false;
        }
    }

}
