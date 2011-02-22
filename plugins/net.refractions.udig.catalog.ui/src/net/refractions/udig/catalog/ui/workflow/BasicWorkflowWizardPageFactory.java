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
package net.refractions.udig.catalog.ui.workflow;

import java.util.Map;

import net.refractions.udig.catalog.ui.workflow.Workflow.State;

/**
 * Simply returns the page that the factory is constructed with.  Or if the map constructor is used returns the
 * page associated with the state.
 *
 * @author jesse
 * @since 1.1.0
 */
public class BasicWorkflowWizardPageFactory implements WorkflowWizardPageProvider {

    private final WorkflowWizardPage page;
    private final Map<State, WorkflowWizardPage> mapping;

    public BasicWorkflowWizardPageFactory(WorkflowWizardPage page) {
        this.page = page;
        this.mapping = null;
    }

    public BasicWorkflowWizardPageFactory(Map<State, WorkflowWizardPage> mapping) {
        this.mapping = mapping;
        this.page = null;
    }

    public WorkflowWizardPage getWorkflowWizardPage( State state ) {
        if( page !=null ){
            return page;
        }
        return mapping.get(state);
    }

}
