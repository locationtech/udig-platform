/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.internal.wizard;

import java.util.Map;

import net.refractions.udig.catalog.internal.ui.CatalogImport;
import net.refractions.udig.catalog.internal.ui.ResourceSelectionPage;
import net.refractions.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import net.refractions.udig.catalog.ui.workflow.DataSourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.ResourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.project.ui.internal.Messages;


/**
 * A CatalogImport that selects individual resources to form the layers
 * of a new map (or add to an existing map).
 * @since 1.1
 * @version 1.3.3
 */
public class MapImport extends CatalogImport {

    private int layerPosition = -1;
    
    /**
     * Capture where new layers should be inserted.
     * @param layerPosition
     */
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
        return new MapImportWizard(workflow, map, layerPosition);
    }

}
