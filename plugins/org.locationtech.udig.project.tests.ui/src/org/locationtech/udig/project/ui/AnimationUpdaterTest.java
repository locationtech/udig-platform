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
package org.locationtech.udig.project.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AnimationUpdaterTest extends AbstractProjectUITestCase {

    private Map map;

    @Before
    public void setUp() throws Exception {
        map=openMap();
        AnimationUpdater.setTesting(true);
    }

    @After
    public void tearDown() throws Exception {
        AnimationUpdater.setTesting(false);
    }

    /*
     * Test method for 'org.locationtech.udig.project.ui.AnimationUpdater.runTimer(IMapDisplay, List<IAnimation>)'
     */
    @Ignore
    @Test
    public void testViewportPaneSWTRunTimer() throws Exception {
        runAnimationTest();
    }
    
    @Ignore
    @Test
    public void testMultipleAnimations() throws Exception {
        final TestAnimation anim1=new TestAnimation();
        TestAnimation anim2=new TestAnimation();
        
        AnimationUpdater.runTimer(map.getRenderManagerInternal().getMapDisplay(), anim1);
        AnimationUpdater.runTimer(map.getRenderManagerInternal().getMapDisplay(), anim2);
        UDIGTestUtil.inDisplayThreadWait(10000, new WaitCondition(){

            public boolean isTrue() {
                return !anim1.isValid();
            }
        }, true);
        
        assertAnimationRan(anim1);
        assertAnimationRan(anim2);
    }
    /**
     *
     * @throws Exception
     */
    private void runAnimationTest() throws Exception {
        final TestAnimation testAnimation = new TestAnimation();
        AnimationUpdater.runTimer(map.getRenderManagerInternal().getMapDisplay(), testAnimation);
//      UDIGTestUtil.inDisplayThreadWait(10000, new Condition(){
      UDIGTestUtil.inDisplayThreadWait(100000000, new WaitCondition(){

            public boolean isTrue()  {
                return !testAnimation.isValid();
            }
        }, true);
        assertAnimationRan(testAnimation);
    }

    private void assertAnimationRan( final TestAnimation testAnimation ) throws Exception {

        List<Long> executionTimes = testAnimation.executionTimes;

        assertEquals(12, testAnimation.frame);
        assertTrue(testAnimation.frame+1<=executionTimes.size());

        // assert that the times between execution of animation occurred within 100 ms of the desired time
        for( int i=1; i< executionTimes.size(); i++ ) {
            if( i==executionTimes.size() )
                break;
//            System.out.println(executionTimes.get(i)-executionTimes.get(i-1));
            assertTrue(executionTimes.get(i-1)-executionTimes.get(i)<FRAME_INTERVAL+100);
        }
    }

    private Map openMap() {
        Map map=(Map) ApplicationGIS.getActiveMap();
        if( map==null ){
        map = ProjectFactory.eINSTANCE.createMap(ProjectPlugin.getPlugin().getProjectRegistry().getCurrentProject(),
                "AnimationTest", new ArrayList<Layer>()); //$NON-NLS-1$
        
        ApplicationGIS.openMap(map);
        try {
            UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

                public boolean isTrue()  {
                    return ApplicationGIS.getActiveMap()!=null;
                }
                
            }, true);
        } catch (Exception e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
        }
        return map;
    }
    
    
    
    final static short FRAME_INTERVAL=300;
    
    class TestAnimation extends AbstractDrawCommand implements IAnimation{

        volatile int frame=0;
        List<Long> executionTimes=Collections.synchronizedList(new ArrayList<Long>());

        public TestAnimation() {
            executionTimes.add(System.currentTimeMillis());
        }
        
        public short getFrameInterval() {
            return FRAME_INTERVAL;
        }

        public void nextFrame() {
            frame++;
        }
        public boolean hasNext() {
            return frame<12;
        }
        
        volatile int lastframeRendered=-1;
        public void run( IProgressMonitor monitor ) throws Exception {
            if( lastframeRendered!=frame && isValid() ){
                executionTimes.add(System.currentTimeMillis());
            }
            lastframeRendered=frame;
//            graphics.setStroke(ViewportGraphics.LINE_SOLID, 2);
//            graphics.draw(new Rectangle(0,0,frame*10,frame*10));
        }

        public Rectangle getValidArea() {
            return null;
        }

    }

}
