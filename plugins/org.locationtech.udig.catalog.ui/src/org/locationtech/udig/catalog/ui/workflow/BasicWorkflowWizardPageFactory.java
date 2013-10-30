/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.ui.workflow;

import java.util.Map;


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
