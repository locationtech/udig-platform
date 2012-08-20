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
package net.refractions.udig.project.ui.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.util.Iterator;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.provider.ContextModelItemProvider;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test cases for map and project item providers to ensure that when a map or layer is added then the item provider
 * fires an event for the viewers to update themselves.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class MapItemProviderTest extends AbstractProjectUITestCase {

    private UDIGAdapterFactoryContentProvider content;
    private Viewer viewer;
    private Map map;
    private boolean refresh=false;

    @Before
    public void setUp() throws Exception {
        content=new UDIGAdapterFactoryContentProvider(ProjectUIPlugin.getDefault()
                .getAdapterFactory());
        viewer=new Viewer(){

            @Override
            public Control getControl() {
                final Shell[] shells=new Shell[1];
                PlatformGIS.syncInDisplayThread(new Runnable(){
                    public void run() {
                        shells[0]=Display.getDefault().getShells()[0];
                    }
                });
                    
                return shells[0];
            }

            @Override
            public Object getInput() {
                return null;
            }

            @Override
            public ISelection getSelection() {
                return null;
            }

            @Override
            public void refresh() {
                refresh=true;
            }

            @Override
            public void setInput( Object input ) {
            }

            @Override
            public void setSelection( ISelection selection, boolean reveal ) {
            }
            
        };
        map = MapTests.createDefaultMap("name", 10, true, new Dimension(500,500)); //$NON-NLS-1$
        map.getLayersInternal().clear();
    }
    
    @After
    public void tearDown() throws Exception {
        content.dispose();
    }

    @Ignore
    @Test
    public void testMapItemProviderAddLayer() throws Exception {
        content.inputChanged(viewer, null, map);
        content.getChildren(map);
        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue()  {
                return getNumItemProviders()>0;
            }
            
        }, false );

        refresh=false;
        
        assertEquals( 1, getNumItemProviders() );
        
        map.getLayersInternal().add(ProjectFactory.eINSTANCE.createLayer());
        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue()  {
                return refresh;
            }
            
        }, false );
        assertTrue(refresh);
        assertEquals( 1, getNumItemProviders() );
    }


    private int getNumItemProviders() {
        Iterator iter = map.getContextModel().eAdapters().iterator();
        int i=0;
        while( iter.hasNext() ){
            if( iter.next() instanceof ContextModelItemProvider ){
                i++;
            }
        }
        return i;
    }

}
