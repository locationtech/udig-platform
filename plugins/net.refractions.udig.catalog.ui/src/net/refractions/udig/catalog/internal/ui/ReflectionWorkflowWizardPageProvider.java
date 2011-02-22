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
package net.refractions.udig.catalog.internal.ui;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.WeakHashMap;

import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPage;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.catalog.ui.workflow.Workflow.State;

/**
 * Returns a unique {@link WorkflowWizardPage} for each state.  Uses the class passed to the constructor
 * as a template.
 *
 * @author jesse
 * @since 1.1.0
 */
public class ReflectionWorkflowWizardPageProvider implements WorkflowWizardPageProvider {

    private final Map<State, WorkflowWizardPage> cache = new WeakHashMap<State, WorkflowWizardPage>();
    private final Constructor< ? extends WorkflowWizardPage> constructor;

    /**
     *
     * @param template
     */
    public ReflectionWorkflowWizardPageProvider( Class< ? extends WorkflowWizardPage> template ) {
        try {
            this.constructor = template.getConstructor();

            // make sure that the contructor works.
            constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("The class parameter must have a *public* 0 parameter constructor", e);
        }
    }

    public synchronized WorkflowWizardPage getWorkflowWizardPage( State state ) {
        WorkflowWizardPage page = cache.get(state);
        if( page ==  null ){
            page = newInstance();
            cache.put(state, page);
        }
        return page;
    }

    private WorkflowWizardPage newInstance() {
        try {
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
