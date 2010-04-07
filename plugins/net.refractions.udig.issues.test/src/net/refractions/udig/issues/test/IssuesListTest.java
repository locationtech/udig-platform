package net.refractions.udig.issues.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.issues.IIssue;


public class IssuesListTest extends AbstractProjectUITestCase {

    DummyIssueList list=new DummyIssueList();
    DummyListener listener=new DummyListener();
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        for( int i=0; i<10; i++){
            list.add(new DummyIssue(i));
        }
        int i=0;
        for( IIssue issue : list ) {
            assertEquals(String.valueOf(i),issue.getProblemObject());
            i++;
        }
        
        list.addListener(listener);
    }
    
    @Override
    protected void tearDown() throws Exception {
        list.clear();
        list.clearlisteners();
        listener.changes=0;
        listener.timesCalled=0;
        super.tearDown();
    }
 

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesList.add(IIssue)'
     */
    public void testAddIIssue() {
        list.add( new DummyIssue(10) );
        assertEquals(1,listener.changes);
        assertEquals(1,listener.timesCalled);
        assertEquals( String.valueOf(10), list.get(list.size()-1).getProblemObject() );
        assertEquals(11, list.size());
        assertNotNull(list.get(0).getId());
    }
    
    public void testClear() throws Exception {
        list.clear();
        assertEquals(10,listener.changes);
        assertEquals(1,listener.timesCalled);
    }

    public void testRetainAll() throws Exception {
        List<IIssue> toRemove=new ArrayList<IIssue>(3);
        toRemove.add(this.list.get(3));
        toRemove.add(this.list.get(4));
        toRemove.add(this.list.get(5));
        
        list.retainAll(toRemove);

        assertEquals(7,listener.changes);
        assertEquals(1,listener.timesCalled);
        assertEquals(3, list.size());
        int i=3;
        for( IIssue issue : list ) {
            assertEquals(String.valueOf(i), (issue.getProblemObject()));
            i++;
        }     
    }
    
    public void testSet() throws Exception {
        IIssue issue=list.set(3, list.get(0));
        

        assertEquals(String.valueOf(3), (issue.getProblemObject()));
        
        assertEquals(list.get(0), list.get(3));
        assertEquals(2,listener.changes);
        assertEquals(2,listener.timesCalled);
        assertEquals(10, list.size());
    }
    
    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesList.add(int, IIssue)'
     */
    public void testAddIntIIssue() {

        list.add( 2, new DummyIssue(10) );
        assertEquals(1,listener.changes);
        assertEquals(1,listener.timesCalled);
        assertEquals(11, list.size());
        assertEquals( String.valueOf(10), list.get(2).getProblemObject() );
    }

    Collection<? extends IIssue> createCollection(int startid){
        List<IIssue> list=new ArrayList<IIssue>(10);
        for( int i=0; i<10; i++){
            list.add(new DummyIssue(i+startid));
        }
        int i=20;
        for( IIssue issue : list ) {
            assertEquals(String.valueOf(i),issue.getProblemObject());
            i++;
        }
        return list;
    }
    
    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesList.addAll(Collection<? extends IIssue>)'
     */
    public void testAddAllCollectionOfQextendsIIssue() {
        list.addAll(createCollection(20));

        assertEquals(10,listener.changes);
        assertEquals(1,listener.timesCalled);
        assertEquals(20, list.size());
        int i=0;
        for( IIssue issue : list ) {
            if( i<10){
                assertEquals(i,Integer.parseInt(issue.getProblemObject()));
            }else{
                int j=Integer.parseInt(issue.getProblemObject());
                assertEquals(i+10,j);                
            }
            i++;
        }        
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesList.addAll(int, Collection<? extends IIssue>)'
     */
    public void testAddAllIntCollectionOfQextendsIIssue() {
        list.addAll(3, createCollection(20));

        assertEquals(10,listener.changes);
        assertEquals(1,listener.timesCalled);
        int i=0;
        for( IIssue issue : list ) {
            if( i<3 || i>12){
                assertTrue(Integer.parseInt(issue.getProblemObject())<10);
            }else{
                int j=Integer.parseInt(issue.getProblemObject());
                assertTrue(j>10);                
            }
            i++;
        }        
        assertEquals(20, list.size());
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesList.addFirst(IIssue)'
     */
    public void testAddFirstIIssue() {

        list.addFirst( new DummyIssue(10) );
        assertEquals(1,listener.changes);
        assertEquals(1,listener.timesCalled);
        assertEquals( String.valueOf(10), list.get(0).getProblemObject() );
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesList.addLast(IIssue)'
     */
    public void testAddLastIIssue() {
        list.addLast( new DummyIssue(10) );
        assertEquals(1,listener.changes);
        assertEquals(1,listener.timesCalled);
        assertEquals( String.valueOf(10), list.get(10).getProblemObject() );
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesList.remove()'
     */
    public void testRemove() {
        list.remove();

        assertEquals(1,listener.changes);
        assertEquals(1,listener.timesCalled);
        assertEquals( String.valueOf(1), list.get(0).getProblemObject() );
        assertEquals(9, list.size() );
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesList.remove(int)'
     */
    public void testRemoveInt() {
        list.remove(2);

        assertEquals(1,listener.changes);
        assertEquals(1,listener.timesCalled);
        assertEquals( String.valueOf(3), list.get(2).getProblemObject() );
        assertEquals(9, list.size() );
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesList.remove(Object)'
     */
    public void testRemoveObject() {
        list.remove(list.get(2));

        assertEquals(1,listener.changes);
        assertEquals(1,listener.timesCalled);
        assertEquals( String.valueOf(3), list.get(2).getProblemObject() );
        assertEquals(9, list.size() );
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesList.removeAll(Collection<?>)'
     */
    public void testRemoveAllCollectionOfQ() {
        List<IIssue> toRemove=new ArrayList<IIssue>(3);
        toRemove.add(this.list.get(3));
        toRemove.add(this.list.get(4));
        toRemove.add(this.list.get(5));
        
        this.list.removeAll(toRemove);
        
        assertEquals(3,listener.changes);
        assertEquals(1,listener.timesCalled);
        for( IIssue issue : this.list ) {
            assertFalse(String.valueOf(3).equals(issue.getProblemObject()));
            assertFalse(String.valueOf(4).equals(issue.getProblemObject()));
            assertFalse(String.valueOf(5).equals(issue.getProblemObject()));
        }    
        
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesList.removeFirst()'
     */
    public void testRemoveFirst() {
        list.removeFirst();

        assertEquals(1,listener.changes);
        assertEquals(1,listener.timesCalled);
        assertEquals( String.valueOf(1), list.get(0).getProblemObject() );
        assertEquals(9, list.size() );
    }
    

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesList.removeLast()'
     */
    public void testRemoveLast() {
        list.removeLast();

        assertEquals(1,listener.changes);
        assertEquals(1,listener.timesCalled);
        DummyIssueList list2 = this.list;
        for( IIssue issue : list2 ) {
            assertFalse(issue.getProblemObject().equals(String.valueOf(9)));
        }
        assertEquals(9, list.size() );
    }
    
}
