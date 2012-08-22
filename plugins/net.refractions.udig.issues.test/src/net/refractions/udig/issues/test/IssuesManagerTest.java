package net.refractions.udig.issues.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.core.enums.Priority;
import net.refractions.udig.issues.AbstractIssue;
import net.refractions.udig.issues.FeatureIssue;
import net.refractions.udig.issues.IIssue;
import net.refractions.udig.issues.IIssuesList;
import net.refractions.udig.issues.IIssuesManager;
import net.refractions.udig.issues.IssuesList;
import net.refractions.udig.issues.internal.IssuesManager;
import net.refractions.udig.issues.listeners.IIssueListener;
import net.refractions.udig.issues.listeners.IIssuesListListener;
import net.refractions.udig.issues.listeners.IIssuesManagerListener;
import net.refractions.udig.issues.listeners.IssuesListEvent;
import net.refractions.udig.issues.listeners.IssuesListEventType;
import net.refractions.udig.issues.listeners.IssuesManagerEvent;
import net.refractions.udig.issues.listeners.IssuesManagerEventType;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.geotools.data.DataStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

public class IssuesManagerTest extends AbstractProjectUITestCase {

    @Before
    public void setUp() throws Exception {
        FeatureIssue.setTesting(true);
    }
    
    @After
    public void tearDown() throws Exception {
        FeatureIssue.setTesting(false);
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.IssuesManager.removeIssues(String)'
     */
    @Ignore
    @Test
    public void testRemoveIssues() {
        IssuesManager m = new IssuesManager();
        IIssuesList issueslist = m.getIssuesList();
        for( int i = 0; i < 10; i++ ) {
            issueslist.add(new DummyIssue(i, i < 6 ? "toRemove" : "g" + i)); //$NON-NLS-1$ //$NON-NLS-2$
        }
        DummyListener l = new DummyListener();
        m.addIssuesListListener(l);

        assertEquals(10, issueslist.size());
        m.removeIssues("toRemove"); //$NON-NLS-1$
        assertEquals("All the issues with groupId \"toRemove\"" + //$NON-NLS-1$
                " should be gone leaving 4 items", 4, issueslist.size()); //$NON-NLS-1$
        for( IIssue issue : issueslist ) {
            assertFalse("Item has groupId \"toRemove\"", issue.getGroupId().equals("toRemove")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        assertEquals(6, l.changes);
        assertEquals(1, l.timesCalled);
        l.changes = 0;
        l.timesCalled = 0;
        m.removeIssues("hello"); //$NON-NLS-1$
        assertEquals(0, l.changes);
        assertEquals(0, l.timesCalled);
        assertEquals(4, issueslist.size());
    }

    @Ignore
    @Test
    public void testSetIssuesList() throws Exception {
        IssuesManager m = new IssuesManager();
        IIssuesList issuesList = new IssuesList();
        m.setIssuesList(issuesList);

        final AtomicBoolean addedListener = new AtomicBoolean(false);
        final AtomicBoolean removedListener = new AtomicBoolean(false);
        DummyIssue dummyIssue = new DummyIssue(3){
            @Override
            public void addIssueListener( IIssueListener listener ) {
                addedListener.set(true);
            }

            @Override
            public void removeIssueListener( IIssueListener listener ) {
                removedListener.set(true);
            }
        };
        issuesList.add(dummyIssue);
        assertTrue(addedListener.get());
        assertFalse(removedListener.get());

        addedListener.set(false);

        m.setIssuesList(new IssuesList());

        assertFalse(addedListener.get());
        assertTrue(removedListener.get());

        removedListener.set(false);

        m.setIssuesList(issuesList);

        assertTrue(addedListener.get());
        assertFalse(removedListener.get());
    }

    @Ignore
    @Test
    public void testListeners() throws Exception {

        IssuesManager m = new IssuesManager();
        final IssuesListEvent[] listEvent = new IssuesListEvent[1];
        m.addIssuesListListener(new IIssuesListListener(){

            public void notifyChange( IssuesListEvent event ) {
                listEvent[0] = event;
            }

        });

        final IssuesManagerEvent[] managerEvent = new IssuesManagerEvent[1];

        m.addListener(new IIssuesManagerListener(){

            public void notifyChange( IssuesManagerEvent event ) {
                managerEvent[0] = event;
            }

        });

        m.getIssuesList().add(new DummyIssue(0));

        assertNotNull(listEvent[0]);
        assertNull(managerEvent[0]);

        listEvent[0] = null;

        m.setIssuesList(new IssuesList());

        assertEquals(IssuesManagerEventType.ISSUES_LIST_CHANGE, managerEvent[0].getType());
        assertNull(listEvent[0]);

        managerEvent[0] = null;

        m.getIssuesList().add(new DummyIssue(0));

        assertNotNull(listEvent[0]);
        assertNull(managerEvent[0]);

    }

    @Ignore
    @Test
    public void testDirtyEvents() throws Exception {
            IssuesManager m = new IssuesManager();
            m.setIssuesList(IssuesListTestHelper.createInMemoryDatastoreIssuesList(null, null));

            FeatureIssue createFeatureIssue = IssuesListTestHelper.createFeatureIssue("newFeature"); //$NON-NLS-1$
            m.getIssuesList().add(createFeatureIssue);

            final IssuesManagerEvent[] managerEvent = new IssuesManagerEvent[1];

            m.addListener(new IIssuesManagerListener(){

                public void notifyChange( IssuesManagerEvent event ) {
                    managerEvent[0] = event;
                }

            });

            createFeatureIssue.setPriority(Priority.CRITICAL);

            assertEquals(IssuesManagerEventType.DIRTY_ISSUE, managerEvent[0].getType());
            assertEquals(Boolean.TRUE, managerEvent[0].getNewValue());
            managerEvent[0] = null;

            m.save(new NullProgressMonitor());

            assertEquals(IssuesManagerEventType.SAVE, managerEvent[0].getType());
            assertNull(managerEvent[0].getNewValue());
            assertEquals(createFeatureIssue, ((Collection) managerEvent[0].getOldValue())
                    .iterator().next());
    }

    @Ignore
    @Test
    public void testSaveIssuesList() throws Exception {
        IssuesManager m = new IssuesManager();
        IIssuesList issuesList = new IssuesList();
        m.setIssuesList(issuesList);
        issuesList.add(IssuesListTestHelper.createFeatureIssue("1")); //$NON-NLS-1$

        // no exception happens, and nothing else.
        assertFalse(m.save(new NullProgressMonitor()));

        DataStore[] store = new DataStore[1];
        SimpleFeatureType[] featureType = new SimpleFeatureType[1];
        issuesList = IssuesListTestHelper.createInMemoryDatastoreIssuesList(store, featureType);
        m.setIssuesList(issuesList);

        FeatureIssue createIssue = IssuesListTestHelper.createFeatureIssue("2"); //$NON-NLS-1$
        issuesList.add(createIssue);
        issuesList.add(IssuesListTestHelper.createFeatureIssue("3")); //$NON-NLS-1$
        assertFalse(m.save(new NullProgressMonitor()));

        createIssue.setPriority(Priority.TRIVIAL);

        class Listener implements IIssuesListListener {

            private Collection< ? extends IIssue> saved;

            public void notifyChange( IssuesListEvent event ) {

                if (event.getType() == IssuesListEventType.SAVE)
                    saved = event.getChanged();
            }

        };

        Listener listener = new Listener();
        issuesList.addListener(listener);

        assertTrue(m.save(new NullProgressMonitor()));
        assertEquals(1, listener.saved.size());
        assertEquals(createIssue.getId(), listener.saved.iterator().next().getId());
    }

    @Ignore
    @Test
    public void testLoadedIssueThrowsException() throws Exception {
        final IIssuesList list = IIssuesManager.defaultInstance.getIssuesList();
        list.clear();
        list.add(new AbstractIssue(){

            public void fixIssue( IViewPart part, IEditorPart editor ) {
            }

            public String getExtensionID() {
                return null;
            }

            public String getProblemObject() {
                throw new RuntimeException();
            }

            public void init( IMemento memento, IMemento viewMemento, String issueId, String groupId, ReferencedEnvelope bounds ) {
            }

            public void save( IMemento memento ) {
            }
            
        });
        
        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue()  {
                return list.isEmpty();
            }
            
        }, true);

        assertTrue(list.isEmpty());
        
    }
    
}
