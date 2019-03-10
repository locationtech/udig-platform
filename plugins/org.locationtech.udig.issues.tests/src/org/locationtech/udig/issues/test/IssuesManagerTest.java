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
package org.locationtech.udig.issues.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.geotools.data.DataStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.core.enums.Priority;
import org.locationtech.udig.issues.AbstractIssue;
import org.locationtech.udig.issues.FeatureIssue;
import org.locationtech.udig.issues.IIssue;
import org.locationtech.udig.issues.IIssuesList;
import org.locationtech.udig.issues.IssuesList;
import org.locationtech.udig.issues.internal.IssuesManager;
import org.locationtech.udig.issues.listeners.IIssueListener;
import org.locationtech.udig.issues.listeners.IIssuesListListener;
import org.locationtech.udig.issues.listeners.IIssuesManagerListener;
import org.locationtech.udig.issues.listeners.IssuesListEvent;
import org.locationtech.udig.issues.listeners.IssuesListEventType;
import org.locationtech.udig.issues.listeners.IssuesManagerEvent;
import org.locationtech.udig.issues.listeners.IssuesManagerEventType;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;
import org.opengis.feature.simple.SimpleFeatureType;

public class IssuesManagerTest extends AbstractProjectUITestCase {

    private IssuesManager issueManager = new IssuesManager();
    private IIssuesList issueslist;

    @Before
    public void setUp() throws Exception {
        issueslist = issueManager.getIssuesList();
        issueslist.clear();
        FeatureIssue.setTesting(true);
    }
    
    @After
    public void tearDown() throws Exception {
        FeatureIssue.setTesting(false);
    }

    /*
     * Test method for 'org.locationtech.udig.project.ui.internal.IssuesManager.removeIssues(String)'
     */
    @Test
    public void testRemoveIssues() {
        assertEquals(0, issueslist.size());

        for( int i = 0; i < 10; i++ ) {
            issueslist.add(new DummyIssue(i, i < 6 ? "toRemove" : "others")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        assertEquals(10, issueslist.size());

        DummyListener l = new DummyListener();
        issueManager.addIssuesListListener(l);

        issueManager.removeIssues("toRemove"); //$NON-NLS-1$
        assertEquals("All the issues with groupId \"toRemove\"" + //$NON-NLS-1$
                " should be gone leaving 4 items", 4, issueslist.size()); //$NON-NLS-1$
        for( IIssue issue : issueslist ) {
            assertFalse("Item has groupId \"toRemove\"", issue.getGroupId().equals("toRemove")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        assertEquals(6, l.changes);
        assertEquals(1, l.timesCalled);
        l.changes = 0;
        l.timesCalled = 0;
        issueManager.removeIssues("hello"); //$NON-NLS-1$
        assertEquals(0, l.changes);
        assertEquals(0, l.timesCalled);
        assertEquals(4, issueslist.size());
    }

    @Test
    public void testSetIssuesList() throws Exception {
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
        issueslist.add(dummyIssue);
        assertTrue(addedListener.get());
        assertFalse(removedListener.get());

        addedListener.set(false);

        issueManager.setIssuesList(new IssuesList());

        assertFalse(addedListener.get());
        assertTrue(removedListener.get());

        removedListener.set(false);

        issueManager.setIssuesList(issueslist);

        assertTrue(addedListener.get());
        assertFalse(removedListener.get());
    }

    @Test
    public void testListeners() throws Exception {
        final IssuesListEvent[] listEvent = new IssuesListEvent[1];
        issueManager.addIssuesListListener(new IIssuesListListener(){

            public void notifyChange( IssuesListEvent event ) {
                listEvent[0] = event;
            }

        });

        final IssuesManagerEvent[] managerEvent = new IssuesManagerEvent[1];

        issueManager.addListener(new IIssuesManagerListener(){

            public void notifyChange( IssuesManagerEvent event ) {
                managerEvent[0] = event;
            }

        });

        issueManager.getIssuesList().add(new DummyIssue(0));

        assertNotNull(listEvent[0]);
        assertNull(managerEvent[0]);

        listEvent[0] = null;

        issueManager.setIssuesList(new IssuesList());

        assertEquals(IssuesManagerEventType.ISSUES_LIST_CHANGE, managerEvent[0].getType());
        assertNull(listEvent[0]);

        managerEvent[0] = null;

        issueManager.getIssuesList().add(new DummyIssue(0));

        assertNotNull(listEvent[0]);
        assertNull(managerEvent[0]);

    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testDirtyEvents() throws Exception {
            issueManager.setIssuesList(IssuesListTestHelper.createInMemoryDatastoreIssuesList(null, null));

            FeatureIssue createFeatureIssue = IssuesListTestHelper.createFeatureIssue("newFeature"); //$NON-NLS-1$
            issueManager.getIssuesList().add(createFeatureIssue);

            final IssuesManagerEvent[] managerEvent = new IssuesManagerEvent[1];

            issueManager.addListener(new IIssuesManagerListener(){

                public void notifyChange( IssuesManagerEvent event ) {
                    managerEvent[0] = event;
                }

            });

            createFeatureIssue.setPriority(Priority.CRITICAL);

            assertEquals(IssuesManagerEventType.DIRTY_ISSUE, managerEvent[0].getType());
            assertEquals(Boolean.TRUE, managerEvent[0].getNewValue());
            managerEvent[0] = null;

            issueManager.save(new NullProgressMonitor());

            assertEquals(IssuesManagerEventType.SAVE, managerEvent[0].getType());
            assertNull(managerEvent[0].getNewValue());
            assertEquals(createFeatureIssue, ((Collection) managerEvent[0].getOldValue())
                    .iterator().next());
    }

    @Test
    public void testSaveIssuesList() throws Exception {
        issueslist.add(IssuesListTestHelper.createFeatureIssue("1")); //$NON-NLS-1$

        // no exception happens, and nothing else.
        assertFalse(issueManager.save(new NullProgressMonitor()));

        DataStore[] store = new DataStore[1];
        SimpleFeatureType[] featureType = new SimpleFeatureType[1];
        IIssuesList issuesList = IssuesListTestHelper.createInMemoryDatastoreIssuesList(store, featureType);
        issueManager.setIssuesList(issuesList);

        FeatureIssue createIssue = IssuesListTestHelper.createFeatureIssue("2"); //$NON-NLS-1$
        issuesList.add(createIssue);
        issuesList.add(IssuesListTestHelper.createFeatureIssue("3")); //$NON-NLS-1$
        assertFalse(issueManager.save(new NullProgressMonitor()));

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

        assertTrue(issueManager.save(new NullProgressMonitor()));
        assertEquals(1, listener.saved.size());
        assertEquals(createIssue.getId(), listener.saved.iterator().next().getId());
    }

    @Test
    public void testLoadedIssueThrowsException() throws Exception {
        issueslist.add(new AbstractIssue(){

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
                return issueslist.isEmpty();
            }
            
        }, true);

        assertTrue(issueslist.isEmpty());
    }
}
