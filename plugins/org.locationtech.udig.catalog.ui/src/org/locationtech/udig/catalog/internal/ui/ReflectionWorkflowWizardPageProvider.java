/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.ui;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.WeakHashMap;

import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPage;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;

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
