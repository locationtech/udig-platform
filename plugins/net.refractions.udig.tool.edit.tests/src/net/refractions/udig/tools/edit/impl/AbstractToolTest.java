package net.refractions.udig.tools.edit.impl;

import junit.framework.TestCase;
import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.support.TestHandler;

public abstract class AbstractToolTest extends TestCase {

    protected abstract AbstractEditTool createTool();
    
    AbstractEditTool tool;
    protected TestHandler handler;
    
    @Override
    protected void setUp() throws Exception {
        tool=createTool();
        handler = new TestHandler();
    }
    /*
     * Test method for 'net.refractions.udig.tools.edit.LineTool.initActivators(Set<Activator>)'
     */
    public void testInitActivators() {
        tool.testinitActivators(handler.getActivators());
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.LineTool.initAcceptBehaviours(List<Behaviour>)'
     */
    public void testInitAcceptBehaviours() {
        tool.testinitAcceptBehaviours(handler.getAcceptBehaviours());
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.LineTool.initCancelBehaviours(List<Behaviour>)'
     */
    public void testInitCancelBehaviours() {
        tool.testinitCancelBehaviours(handler.getCancelBehaviours());
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.LineTool.initEventBehaviours(EditToolConfigurationHelper)'
     */
    public void testInitEventBehaviours() {
        tool.testinitEventBehaviours(new EditToolConfigurationHelper(handler.getBehaviours()));
    }

}
