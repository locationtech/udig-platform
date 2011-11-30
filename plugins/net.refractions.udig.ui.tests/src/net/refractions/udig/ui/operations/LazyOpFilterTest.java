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
package net.refractions.udig.ui.operations;

import junit.framework.TestCase;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Jesse
 * @since 1.1.0
 */
public class LazyOpFilterTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testTrueFalse() throws Exception {

        final boolean[] value=new boolean[1];
        value[0]=false;
        ILazyOpListener l = new ILazyOpListener(){

            public void notifyResultObtained( boolean result ) {
                value[0]=true;
            }
            
        };
        
        LazyOpFilter lazy=new LazyOpFilter(l, OpFilter.TRUE);
        
        assertTrue(lazy.accept("SOMETHING")); //$NON-NLS-1$
        assertTrue(lazy.accept("SOMETHING")); //$NON-NLS-1$
        assertTrue(lazy.accept("SOMETHING")); //$NON-NLS-1$
        assertFalse(value[0]);
        
        lazy=new LazyOpFilter(l, OpFilter.FALSE);
        
        assertFalse(lazy.accept("")); //$NON-NLS-1$
        assertFalse(lazy.accept("")); //$NON-NLS-1$
        assertFalse(lazy.accept("")); //$NON-NLS-1$
        assertFalse(value[0]);
    }
    
    /**
     * Test method for {@link net.refractions.udig.ui.operations.LazyOpFilter#accept(java.lang.Object)}.
     */
    public void testAcceptWithCachingNoBlocking() {
        CacheNoBlock filter=new CacheNoBlock(false);
        final boolean[] value=new boolean[1];
        value[0]=false;
        LazyOpFilter lazy=new LazyOpFilter(new ILazyOpListener(){

            public void notifyResultObtained( boolean result ) {
                value[0]=result;
            }
            
        }, filter);
        
        String string = "SomeObject"; //$NON-NLS-1$
        assertFalse(lazy.accept(string));
        
        filter.result=true;
        
        assertFalse(lazy.accept(string));
        
        filter.notifyListeners("otherObject"); //$NON-NLS-1$
        assertFalse(value[0]);
        
        assertFalse(lazy.accept(string));
        
        // test equality
        filter.notifyListeners("SomeObject"); //$NON-NLS-1$
        assertTrue(value[0]);
        assertTrue(lazy.accept(string));
    }

    public void XtestAcceptWithNoCachingNoBlocking() {
        NoCacheNoBlock filter=new NoCacheNoBlock(false);
        final boolean[] value=new boolean[1];
        value[0]=false;
        LazyOpFilter lazy=new LazyOpFilter(new ILazyOpListener(){

            public void notifyResultObtained( boolean result ) {
                value[0]=result;
            }
            
        }, filter);
        
        String string = "SomeObject"; //$NON-NLS-1$
        assertFalse(lazy.accept(string));
        
        filter.result=true;
        
        assertTrue(lazy.accept(string));
        
        filter.notifyListeners("otherObject"); //$NON-NLS-1$
        assertFalse(value[0]);
        
        
        filter.notifyListeners(string);
        assertFalse(value[0]);
        
        // load cache with false
        filter.result=false;
        lazy.accept(string);
        

        filter.result=true;

        filter.notifyListeners(string);
        assertTrue(value[0]);
        
    }

    public void testAcceptWithNoCachingBlocking() throws Exception {
        NoCacheBlock filter=new NoCacheBlock(false);
        final boolean[] value=new boolean[2];
        // the value of the result
        value[0]=false;
        // true if it was called.
        value[1]=false;
        LazyOpFilter lazy=new LazyOpFilter(new ILazyOpListener(){

            public void notifyResultObtained( boolean result ) {
                value[0]=result;
                value[1]=true;
            }
            
        }, filter);
        
        String string = "SomeObject"; //$NON-NLS-1$
        assertEquals(LazyOpFilter.DEFAULT_RETURN_VALUE, lazy.accept(string));
        
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                return value[1];
            }
            
        }, false);
        
        assertFalse(value[0]);
        
        filter.result=true;
        
        value[0]=true;
        value[1]=false;

        assertEquals(false, lazy.accept(string));
        
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                return value[1];
            }
            
        }, false);
        
        assertTrue(value[0]);

        value[0]=false;
        value[1]=false;
        
        filter.result=true;
        
        filter.notifyListeners(string);

        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                return value[1];
            }
            
        }, false);
        
        assertTrue(value[0]);
    }


    public void testAcceptWithCachingBlocking() throws Exception {
        CacheBlock filter=new CacheBlock(false);
        final boolean[] value=new boolean[2];
        // the value of the result
        value[0]=false;
        // true if it was called.
        value[1]=false;
        LazyOpFilter lazy=new LazyOpFilter(new ILazyOpListener(){

            public void notifyResultObtained( boolean result ) {
                value[0]=result;
                value[1]=true;
            }
            
        }, filter);
        
        String string = "SomeObject"; //$NON-NLS-1$        
        assertEquals(LazyOpFilter.DEFAULT_RETURN_VALUE, lazy.accept(string));

        
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                return value[1];
            }
            
        }, false);
        
        assertFalse(value[0]);
        assertFalse(lazy.accept(string));
        
        filter.result=true;
        
        assertFalse(lazy.accept(string));
        

        value[0]=false;
        value[1]=false;
        
        filter.result=true;
        
        filter.notifyListeners(string);

        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                return value[1];
            }
            
        }, false);
        
        assertTrue(value[0]);


        assertTrue(lazy.accept(string));
        
    }

    /**
     * Test method for {@link net.refractions.udig.ui.operations.LazyOpFilter#addListener(net.refractions.udig.ui.operations.IOpFilterListener)}.
     */
    public void testUnsupportedMethods() {
        LazyOpFilter f=new LazyOpFilter(null, new CacheBlock(false));
        try{
            f.addListener(new IOpFilterListener(){

                public void notifyChange( Object changed ) {
                }
                
            });
            fail();
        }catch (Exception e) {
            //good
        }
        try{
            f.canCacheResult();
            fail();
        }catch (Exception e) {
            //good
        }
        try{
            f.isBlocking();
            fail();
        }catch (Exception e) {
            //good
        }
        try{
            f.removeListener(new IOpFilterListener(){

                public void notifyChange( Object changed ) {
                }
                
            });
            fail();
        }catch (Exception e) {
            //good
        }
    }

    public void testDisable() throws Exception {
        CacheNoBlock filter=new CacheNoBlock(false);
        final boolean[] value=new boolean[1];
        value[0]=false;
        LazyOpFilter lazy=new LazyOpFilter(new ILazyOpListener(){

            public void notifyResultObtained( boolean result ) {
                value[0]=true;
            }
            
        }, filter);
        
        String string = "SomeObject"; //$NON-NLS-1$
        assertFalse(lazy.accept(string));
        
        filter.result=true;
        
        assertFalse(lazy.accept(string));
        
        assertEquals( 1, filter.getListeners().size());
        
        filter.notifyListeners(string);
        
        assertTrue( value[0]);
        
        lazy.disable();
        
        value[0]=false;
        
        filter.notifyListeners(string);
        assertFalse(value[0]);
        
    }
    
}
