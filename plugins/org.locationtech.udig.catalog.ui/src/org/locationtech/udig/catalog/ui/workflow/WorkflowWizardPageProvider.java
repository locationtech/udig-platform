/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.workflow;


/**
 * Creates a Wizard page for the provided state.
 * 
 * @author jesse
 * @since 1.1.0
 * 
 * @see 
 */
public interface WorkflowWizardPageProvider {

    /**
     * Returns the wizard page for the state.  
     * <p>
     * For each state instance the same page instance must be returned.
     * </p>
     *
     * @param state the state to create the wizard page for.
     * @return the wizard page for the state.  
     * <p>
     * For each state instance the same page instance must be returned.
     * </p>
     */
    WorkflowWizardPage getWorkflowWizardPage(State state);
}
