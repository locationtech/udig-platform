/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.wizard;

import java.util.Map;

import org.locationtech.udig.catalog.internal.ui.ResourceSelectionPage;
import org.locationtech.udig.catalog.ui.wizard.CatalogImport;
import org.locationtech.udig.catalog.ui.wizard.ResourceSearchPage;
import org.locationtech.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import org.locationtech.udig.catalog.ui.workflow.DataSourceSelectionState;
import org.locationtech.udig.catalog.ui.workflow.ResourceSearchState;
import org.locationtech.udig.catalog.ui.workflow.ResourceSelectionState;
import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.Workflow;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizard;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import org.locationtech.udig.project.ui.internal.Messages;

/**
 * A CatalogImport that selects individual resources to form the layers of a new map (or add to an
 * existing map).
 * 
 * @since 1.1
 * @version 1.3.3
 */
public class MapImport extends CatalogImport {

    private int layerPosition = -1;

    /**
     * Capture where new layers should be inserted.
     * 
     * @param layerPosition
     */
    public void setLayerPosition(int layerPosition) {
        this.layerPosition = layerPosition;
    }

    @Override
    protected Workflow createWorkflow() {
        // FIXME Andrea: Jody, this is the part that breaks DnD. Uncomment it to have it working
        // ResourceSearchState searchState = new ResourceSearchState();
        DataSourceSelectionState dsState = new DataSourceSelectionState(true);
        ResourceSelectionState rsState = new ResourceSelectionState();

        Workflow workflow = new Workflow(new State[]{dsState, rsState});
        // Workflow workflow = new Workflow(new State[] { searchState, rsState });
        return workflow;
    }

    @Override
    protected Map<Class<? extends State>, WorkflowWizardPageProvider> createPageMapping() {
        Map<Class<? extends State>, WorkflowWizardPageProvider> map = super.createPageMapping();

        ResourceSearchPage searchPage = new ResourceSearchPage(Messages.ResourceSelectionPage_title);
        
        map.put( ResourceSearchState.class, pageProvider( searchPage ) );
        
        addResourceSelectionPage( map );
        return map;
    }


    /**
     * Appends the ResorceSelectionPage to a {@link WorkflowWizard}'s pageMapping
     * 
     * @param pageMapping the starting page mapping.
     * 
     * @return the same mapping with the new entry added
     */
    public static java.util.Map<Class<? extends State>, WorkflowWizardPageProvider> addResourceSelectionPage(
            Map<Class<? extends State>, WorkflowWizardPageProvider> pageMapping) {
        Map<Class<? extends State>, WorkflowWizardPageProvider> map = pageMapping;
        
        ResourceSelectionPage page = new ResourceSelectionPage(Messages.MapImport_selectLayers);
        map.put(ResourceSelectionState.class, new BasicWorkflowWizardPageFactory(page));

        return map;
    }

    @Override
    protected WorkflowWizard createWorkflowWizard(Workflow workflow,
            java.util.Map<Class<? extends State>, WorkflowWizardPageProvider> map) {
        return new MapImportWizard(workflow, map, layerPosition);
    }

}
