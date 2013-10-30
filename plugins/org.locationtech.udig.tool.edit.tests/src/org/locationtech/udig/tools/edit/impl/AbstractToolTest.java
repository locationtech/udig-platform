/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.impl;

import org.locationtech.udig.tools.edit.AbstractEditTool;
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.support.TestHandler;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractToolTest {

    protected abstract AbstractEditTool createTool();
    
    AbstractEditTool tool;
    protected TestHandler handler;
    
    @Before
    public void abstractToolTestSetUp() throws Exception {
        tool=createTool();
        handler = new TestHandler();
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.LineTool.initActivators(Set<Activator>)'
     */
    @Test
    public void testInitActivators() {
        tool.testinitActivators(handler.getActivators());
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.LineTool.initAcceptBehaviours(List<Behaviour>)'
     */
    @Test
    public void testInitAcceptBehaviours() {
        tool.testinitAcceptBehaviours(handler.getAcceptBehaviours());
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.LineTool.initCancelBehaviours(List<Behaviour>)'
     */
    @Test
    public void testInitCancelBehaviours() {
        tool.testinitCancelBehaviours(handler.getCancelBehaviours());
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.LineTool.initEventBehaviours(EditToolConfigurationHelper)'
     */
    @Test
    public void testInitEventBehaviours() {
        tool.testinitEventBehaviours(new EditToolConfigurationHelper(handler.getBehaviours()));
    }

}
