package net.refractions.udig.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import net.refractions.udig.internal.ui.UDIGDropHandler;
import net.refractions.udig.ui.AcceptLessTen.Data;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.IAdaptable;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class UDIGDropHandlerTest {

    private UDIGDropHandler handler;
    Object dropDestination = new TestDropDestination(); 
    private volatile int done=0;
    private boolean noAction=false;
    private IDropHandlerListener listener=new IDropHandlerListener(){


        public void done( IDropAction action, Throwable error ) {
            done++;
        }

        public void noAction( Object data ) {
            noAction=true;
        }

        public void starting( IDropAction action ) {
        }
        
    };
    private void reset() {
        AlwaysAcceptDropAction.reset();
        done=0;
        noAction=false;
    }


    @After
    public void tearDown() throws Exception {
        reset();
    }
    
    @Before
    public void setUp() throws Exception {
        handler=new UDIGDropHandler();
        handler.addListener(listener);
        handler.setTarget(dropDestination);
    }

    @Test
    public void testPerformDropDefaultOrNoAdaptTo() throws Exception {
        final String string = "Data"; //$NON-NLS-1$
        doDrop(string,1);
        assertEquals(string, AlwaysAcceptDropAction.droppedData);
        assertEquals(dropDestination, AlwaysAcceptDropAction.dropDestination);
        
        IAdaptable data = new IAdaptable(){

            public Object getAdapter( @SuppressWarnings("rawtypes") Class adapter ) {
                if( String.class.isAssignableFrom(adapter) )
                    return string;
                return null;
            }
            
        };
        
        doDrop(data, 0);
        assertTrue(noAction);
        assertEquals(0,done);
        assertNull(AlwaysAcceptDropAction.droppedData);
        assertNull(AlwaysAcceptDropAction.dropDestination);

        doDrop(Float.valueOf(1000000000),0);
        assertTrue(noAction);
        assertEquals(0,done);
        assertNull(AlwaysAcceptDropAction.droppedData);
        assertNull(AlwaysAcceptDropAction.dropDestination);

    }

    private void doDrop( final Object data, final int numExpectedActions ) throws Exception {
        reset();
        handler.performDrop(data, null);
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                if( numExpectedActions>0 )
                    return done>=numExpectedActions;
                else
                    return noAction==true;
            }
            
        }, true);
    }
    
    @Test
    public void testPerformDropAdaptTo() throws Exception {
        IAdaptable data = new IAdaptable(){

            public Object getAdapter( @SuppressWarnings("rawtypes") Class adapter ) {
                if( Integer.class.isAssignableFrom(adapter) )
                    return Integer.valueOf(10);
                return null;
            }
            
        };
        doDrop(data, 1);
        assertFalse(noAction);
        assertEquals(1, done);
        assertEquals(Integer.valueOf(10), AlwaysAcceptDropAction.droppedData);
        assertEquals(dropDestination, AlwaysAcceptDropAction.dropDestination);
    }

    @Ignore
    @Test
    public void testPerformDropListItemsManyActions() throws Exception {
        ArrayList<Object> data = new ArrayList<Object>();
        String data1 = "Data1"; //$NON-NLS-1$
        data.add(data1); 
        String data2 = "Data2"; //$NON-NLS-1$
        data.add(data2); 
        Double data3 = Double.valueOf(10);
        data.add(data3); 

        doDrop(data, 3);
        
        assertEquals(3,done);
        assertFalse(noAction);
        assertEquals(3, AlwaysAcceptDropAction.allDrops.size());
        assertEquals(data1, AlwaysAcceptDropAction.allDrops.get(0));
        assertEquals(data2, AlwaysAcceptDropAction.allDrops.get(1));
        assertEquals(data3, AlwaysAcceptDropAction.allDrops.get(2));
    }

    @Test
    public void testPerformDrop1PlusItemsOneAction() throws Exception {
        ArrayList<Object> data = new ArrayList<Object>();
        Double data1 = Double.valueOf(1.1); 
        data.add(data1); 
        Double data2 = Double.valueOf(2.2); 
        data.add(data2); 

        doDrop(data, 1);
        
        assertEquals(1,done);
        assertFalse(noAction);
        assertEquals(1, AlwaysAcceptDropAction.allDrops.size());
        assertEquals(data1, ((Object[])AlwaysAcceptDropAction.droppedData)[0]);
        assertEquals(data2, ((Object[])AlwaysAcceptDropAction.droppedData)[1]);
        assertEquals(dropDestination, AlwaysAcceptDropAction.dropDestination);


        Double data3 = Double.valueOf(10);
        data.add(data3); 
        
        doDrop(data, 1);
        
        assertEquals(1,done);
        assertFalse(noAction);
        assertEquals(1, AlwaysAcceptDropAction.allDrops.size());
        assertEquals(data1, ((Object[])AlwaysAcceptDropAction.droppedData)[0]);
        assertEquals(data2, ((Object[])AlwaysAcceptDropAction.droppedData)[1]);
        assertEquals(data3, ((Object[])AlwaysAcceptDropAction.droppedData)[2]);
        assertEquals(dropDestination, AlwaysAcceptDropAction.dropDestination);
    }

    @Test
    public void testPerformDrop2ItemsOneAction() throws Exception {
        ArrayList<Object> data = new ArrayList<Object>();
        Byte data1 = Byte.valueOf("1");  //$NON-NLS-1$
        data.add(data1); 
        Byte data2 = Byte.valueOf("2");  //$NON-NLS-1$
        data.add(data2); 
        Byte data3 = Byte.valueOf("10"); //$NON-NLS-1$
        data.add(data3); 

        doDrop(data, 1);
        
        assertEquals(1,done);
        assertFalse(noAction);
        assertEquals(1, AlwaysAcceptDropAction.allDrops.size());
        assertEquals(data1, ((Object[])AlwaysAcceptDropAction.droppedData)[0]);
        assertEquals(data2, ((Object[])AlwaysAcceptDropAction.droppedData)[1]);
        
        data.remove(0);
        
        doDrop(data, 1);
        
        assertEquals(1,done);
        assertFalse(noAction);
        assertEquals(1, AlwaysAcceptDropAction.allDrops.size());
        assertEquals(data2, ((Object[])AlwaysAcceptDropAction.droppedData)[0]);
        assertEquals(data3, ((Object[])AlwaysAcceptDropAction.droppedData)[1]);
        assertEquals(dropDestination, AlwaysAcceptDropAction.dropDestination);

        data.remove(0);
        
        doDrop(data, 0);
        
        assertEquals(0,done);
        assertTrue(noAction);
        
    }

    @Test
    public void testPerformDropPlusItemsOneAction() throws Exception {
        ArrayList<Object> data = new ArrayList<Object>();
        Long data1 = Long.valueOf(1); 
        data.add(data1); 
        Long data2 = Long.valueOf(2); 
        data.add(data2); 

        doDrop(data, 1);
        
        assertEquals(1,done);
        assertFalse(noAction);
        assertEquals(1, AlwaysAcceptDropAction.allDrops.size());
        assertEquals(data1, ((Object[])AlwaysAcceptDropAction.droppedData)[0]);
        assertEquals(data2, ((Object[])AlwaysAcceptDropAction.droppedData)[1]);
        assertEquals(dropDestination, AlwaysAcceptDropAction.dropDestination);


        Long data3 = Long.valueOf(10);
        data.add(data3); 
        
        doDrop(data, 1);
        
        assertEquals(1,done);
        assertFalse(noAction);
        assertEquals(1, AlwaysAcceptDropAction.allDrops.size());
        assertEquals(data1, ((Object[])AlwaysAcceptDropAction.droppedData)[0]);
        assertEquals(data2, ((Object[])AlwaysAcceptDropAction.droppedData)[1]);
        assertEquals(data3, ((Object[])AlwaysAcceptDropAction.droppedData)[2]);
        assertEquals(dropDestination, AlwaysAcceptDropAction.dropDestination);
    }

    @Test
    public void testPerformDrop10PlusItemsOneAction() throws Exception {
        ArrayList<Short> data = new ArrayList<Short>();
        Short data1 = Short.valueOf("1");  //$NON-NLS-1$
        data.add(data1); 
        Short data2 = Short.valueOf("2");  //$NON-NLS-1$
        data.add(data2); 
        Short data3 = Short.valueOf("10"); //$NON-NLS-1$
        data.add(data3); 

        doDrop(data, 0);
        
        assertEquals(0,done);
        assertTrue(noAction);
        
        while(data.size()<10)
            data.add(Short.valueOf("10")); //$NON-NLS-1$
        
        doDrop(data, 1);
        
        assertEquals(1,done);
        assertFalse(noAction);
        assertEquals(1, AlwaysAcceptDropAction.allDrops.size());
        assertEquals(10, ((Object[])AlwaysAcceptDropAction.droppedData).length);
        assertEquals(dropDestination, AlwaysAcceptDropAction.dropDestination);

        while(data.size()<20)
            data.add(Short.valueOf("10")); //$NON-NLS-1$
        
        doDrop(data, 1);
        
        assertEquals(1,done);
        assertFalse(noAction);
        assertEquals(1, AlwaysAcceptDropAction.allDrops.size());
        assertEquals(20, ((Object[])AlwaysAcceptDropAction.droppedData).length);
        assertEquals(dropDestination, AlwaysAcceptDropAction.dropDestination);

    }

    @Ignore
    @Test
    public void testPerformDropListButOnlySomeAreAcceptable() throws Exception {
        ArrayList<Data> data = new ArrayList<Data>();
        Data data1 = new Data(11);
        data.add(data1); 
        Data data2 = new Data(1); 
        data.add(data2); 
        Data data3 = new Data(5);
        data.add(data3); 

        doDrop(data, 2);
        
        assertEquals(2,done);
        assertFalse(noAction);
        assertEquals(2, AlwaysAcceptDropAction.allDrops.size());
        assertEquals(data2, AlwaysAcceptDropAction.allDrops.get(0));
        assertEquals(data3, AlwaysAcceptDropAction.allDrops.get(1));
    }

    @Ignore
    @Test
    public void testMixedDrop() throws Exception {
        ArrayList<Object> data = new ArrayList<Object>();
        Double data1 = Double.valueOf(11);
        data.add(data1); 
        Character data2 = 'c'; 
        data.add(data2); 
        Long data3 = Long.valueOf(5);
        data.add(data3); 

        doDrop(data, 2);
        
        assertEquals(2,done);
        assertFalse(noAction);
        assertEquals(2, AlwaysAcceptDropAction.allDrops.size());
        assertEquals(data1, ((Object[])AlwaysAcceptDropAction.allDrops.get(0))[0]);
        assertEquals(data2, ((Object[])AlwaysAcceptDropAction.allDrops.get(0))[1]);
        assertEquals(data3, AlwaysAcceptDropAction.allDrops.get(1));

    }
    
}
