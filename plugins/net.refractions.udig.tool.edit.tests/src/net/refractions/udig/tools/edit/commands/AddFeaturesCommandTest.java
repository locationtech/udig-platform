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
package net.refractions.udig.tools.edit.commands;

import junit.framework.TestCase;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.filter.FidFilter;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;

/**
 * Test {@link net.refractions.udig.tools.edit.commands.SelectFeaturesInFilterCommand}
 * @author jones
 * @since 1.1.0
 */
public class AddFeaturesCommandTest extends TestCase {

    private TestHandler handler;
    static final String FEATURE_TYPE_NAME = "AddFeaturesCommandTestFeatures"; //$NON-NLS-1$

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        handler=new TestHandler(5, FEATURE_TYPE_NAME);
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.commands.AddFeaturesCommand.run(IProgressMonitor)'
     */
    public void testRun() throws Exception {
        FilterFactory fac=FilterFactoryFinder.createFilterFactory();
        String fid = FEATURE_TYPE_NAME+".1";//$NON-NLS-1$
        FidFilter filter = fac.createFidFilter(fid);
        SelectFeaturesInFilterCommand command=new SelectFeaturesInFilterCommand(handler.getEditBlackboard(), handler.getEditLayer(), filter);
        command.setMap(handler.getContext().getMap());
        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.run(nullProgressMonitor);

        assertEquals(2, handler.getEditBlackboard().getGeoms().size());
        assertTrue(fid.equals(handler.getEditBlackboard().getGeoms().get(1).getFeatureIDRef().get())
                || fid.equals(handler.getEditBlackboard().getGeoms().get(0).getFeatureIDRef().get()));

        command.rollback(nullProgressMonitor);
        assertEquals(1, handler.getEditBlackboard().getGeoms().size());
        assertFalse(handler.getEditBlackboard().getGeoms().get(0).isChanged());
    }

    public void testDiscriminatingFilter() throws Exception {
        FilterFactory fac=FilterFactoryFinder.createFilterFactory();
        String fid1 =  FEATURE_TYPE_NAME+".1"; //$NON-NLS-1$
        FidFilter filter = fac.createFidFilter(fid1);
        String fid2 =  FEATURE_TYPE_NAME+".3"; //$NON-NLS-1$
        filter.addFid(fid2);

        SelectFeaturesInFilterCommand command=new SelectFeaturesInFilterCommand(handler.getEditBlackboard(), handler.getEditLayer(), filter);
        command.setMap(handler.getContext().getMap());
        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.run(nullProgressMonitor);

        assertEquals(3, handler.getEditBlackboard().getGeoms().size());
        boolean found=false;
        for( EditGeom geom : handler.getEditBlackboard().getGeoms() ) {
            if( fid1.equals(geom.getFeatureIDRef().get()) ){
                found = true;
                break;
            }
        }
        assertTrue(found);

        found=false;
        for( EditGeom geom : handler.getEditBlackboard().getGeoms() ) {
            if( fid2.equals(geom.getFeatureIDRef().get()) ){
                found = true;
                break;
            }
        }
        assertTrue(found);

        command.rollback(nullProgressMonitor);
        assertEquals(1, handler.getEditBlackboard().getGeoms().size());
        assertFalse(handler.getEditBlackboard().getGeoms().get(0).isChanged());

    }

    public void testFilterNONE() throws Exception {
        SelectFeaturesInFilterCommand command = new SelectFeaturesInFilterCommand(handler.getEditBlackboard(), handler.getEditLayer(), Filter.NONE);
        command.setMap(handler.getContext().getMap());
        IProgressMonitor nullProgressMonitor=new NullProgressMonitor();
        command.run(nullProgressMonitor);

        assertEquals(6, handler.getEditBlackboard().getGeoms().size());
        boolean found = false;
        for( EditGeom geom : handler.getEditBlackboard().getGeoms() ) {
            String fid =  FEATURE_TYPE_NAME+".1"; //$NON-NLS-1$
            if( fid.equals(geom.getFeatureIDRef().get()) ){
                found = true;
                break;
            }
        }
        assertTrue(found);

        found=false;
        for( EditGeom geom : handler.getEditBlackboard().getGeoms() ) {
            String fid2 =  FEATURE_TYPE_NAME+".2"; //$NON-NLS-1$
            if( fid2.equals(geom.getFeatureIDRef().get()) ){
                found = true;
                break;
            }
        }
        found = false;
        for( EditGeom geom : handler.getEditBlackboard().getGeoms() ) {
            if(  (FEATURE_TYPE_NAME+".3").equals(geom.getFeatureIDRef().get()) ){ //$NON-NLS-1$
                found = true;
                break;
            }
        }
        assertTrue(found);

        found=false;
        for( EditGeom geom : handler.getEditBlackboard().getGeoms() ) {
            if( ( FEATURE_TYPE_NAME+".4").equals(geom.getFeatureIDRef().get()) ){ //$NON-NLS-1$
                found = true;
                break;
            }
        }
        found = false;
        for( EditGeom geom : handler.getEditBlackboard().getGeoms() ) {
            if( ( FEATURE_TYPE_NAME+".5" ).equals(geom.getFeatureIDRef().get()) ){ //$NON-NLS-1$
                found = true;
                break;
            }
        }
        assertTrue(found);

        command.rollback(nullProgressMonitor);
        assertEquals(1, handler.getEditBlackboard().getGeoms().size());
        assertFalse(handler.getEditBlackboard().getGeoms().get(0).isChanged());

    }

}
