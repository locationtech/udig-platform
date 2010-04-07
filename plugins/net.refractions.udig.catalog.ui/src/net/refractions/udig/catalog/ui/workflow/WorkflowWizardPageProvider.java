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
