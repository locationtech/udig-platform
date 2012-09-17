package net.refractions.udig.tools.edit.impl;

import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.support.TestHandler;

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
     * Test method for 'net.refractions.udig.tools.edit.LineTool.initActivators(Set<Activator>)'
     */
    @Test
    public void testInitActivators() {
        tool.testinitActivators(handler.getActivators());
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.LineTool.initAcceptBehaviours(List<Behaviour>)'
     */
    @Test
    public void testInitAcceptBehaviours() {
        tool.testinitAcceptBehaviours(handler.getAcceptBehaviours());
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.LineTool.initCancelBehaviours(List<Behaviour>)'
     */
    @Test
    public void testInitCancelBehaviours() {
        tool.testinitCancelBehaviours(handler.getCancelBehaviours());
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.LineTool.initEventBehaviours(EditToolConfigurationHelper)'
     */
    @Test
    public void testInitEventBehaviours() {
        tool.testinitEventBehaviours(new EditToolConfigurationHelper(handler.getBehaviours()));
    }

}
