/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal;

import static org.locationtech.udig.issues.internal.IssuesPreferencePage.PREFERENCE_ID;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.issues.IIssue;
import org.locationtech.udig.issues.IIssuesList;
import org.locationtech.udig.issues.IIssuesManager;
import org.locationtech.udig.issues.IRemoteIssuesList;
import org.locationtech.udig.issues.IssueConstants;
import org.locationtech.udig.issues.IssuesList;
import org.locationtech.udig.issues.IssuesListConfigurator;
import org.locationtech.udig.issues.listeners.IIssueListener;
import org.locationtech.udig.issues.listeners.IIssuesListListener;
import org.locationtech.udig.issues.listeners.IIssuesManagerListener;
import org.locationtech.udig.issues.listeners.IssueEvent;
import org.locationtech.udig.issues.listeners.IssuePropertyChangeEvent;
import org.locationtech.udig.issues.listeners.IssuesListEvent;
import org.locationtech.udig.issues.listeners.IssuesManagerEvent;
import org.locationtech.udig.issues.listeners.IssuesManagerEventType;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.XMLMemento;

/**
 * @see org.locationtech.udig.issues.IIssuesManager
 * @author jones
 * @since 1.0.0
 */
public class IssuesManager extends Object implements IIssuesManager {

    private volatile IIssuesList issuesList;

    private volatile DirtyIssueList dirtyListener;

    private Collection<IIssuesManagerListener> listeners = new CopyOnWriteArraySet<>();

    private Collection<IIssuesListListener> listListeners = new CopyOnWriteArraySet<>();

    public IssuesManager() {
        setIssuesList(createListFromPreferences());
    }

    /**
     * Checks preferences and creates the issues list. If it is somehow messed up then
     * {@link IssuesList}.
     */
    static IIssuesList createListFromPreferences() {
        IIssuesList issuesList = null;
        IPreferenceStore preferenceStore = IssuesActivator.getDefault().getPreferenceStore();
        String listID = preferenceStore.getString(IssuesPreferencePage.PREFERENCE_ID);
        List<IConfigurationElement> extensions = ExtensionPointList
                .getExtensionPointList(IssueConstants.ISSUES_LIST_EXTENSION_ID);
        for (IConfigurationElement element : extensions) {
            String string = element.getNamespaceIdentifier() + "." + element.getAttribute("id");//$NON-NLS-1$//$NON-NLS-2$
            if ((string).equals(listID)) {
                try {
                    issuesList = (IIssuesList) element.createExecutableExtension("class"); //$NON-NLS-1$
                    String config = element.getAttribute("configurator"); //$NON-NLS-1$
                    if (config != null) {
                        IssuesListConfigurator configurator = (IssuesListConfigurator) element
                                .createExecutableExtension("configurator"); //$NON-NLS-1$
                        String data = preferenceStore
                                .getString(IssuesPreferencePage.PREFERENCE_ID + "/" + listID); //$NON-NLS-1$
                        XMLMemento memento = XMLMemento.createReadRoot(new StringReader(data));
                        configurator.initConfiguration(issuesList, memento);
                    }
                    break;
                } catch (CoreException e) {
                    issuesList = null;
                    LoggingSupport.log(IssuesActivator.getDefault(), e);
                }
            }
        }

        if (issuesList == null) {
            issuesList = new IssuesList();
        }

        if (issuesList instanceof IRemoteIssuesList) {
            try {
                ((IRemoteIssuesList) issuesList).refresh();
            } catch (IOException e) {
                LoggingSupport.log(IssuesActivator.getDefault(), "failed to refresh issues list", //$NON-NLS-1$
                        e);
                issuesList = new IssuesList();
            }
        } else if (issuesList instanceof IssuesList) {
            ((IssuesList) issuesList).load();
        }

        return issuesList;
    }

    @Override
    public IIssuesList getIssuesList() {
        return issuesList;
    }

    @Override
    public void addIssuesListListener(IIssuesListListener listener) {
        if (listener == null)
            throw new NullPointerException();
        issuesList.addListener(listener);
        listListeners.add(listener);
    }

    @Override
    public void removeIssuesListListener(IIssuesListListener listener) {
        if (listener == null)
            throw new NullPointerException();
        issuesList.removeListener(listener);
        listListeners.remove(listener);
    }

    public void removeIssues(String groupId) {
        if (groupId == null)
            throw new NullPointerException();
        issuesList.removeIssues(groupId);
    }

    public Set<String> getGroups() {
        return issuesList.getGroups();
    }

    public List<IIssue> getIssues(String groupId) {
        if (groupId == null)
            throw new NullPointerException();
        return issuesList.getIssues(groupId);
    }

    @Override
    public void setIssuesList(IIssuesList newList) {
        Object lock = issuesList == null ? this : issuesList;
        synchronized (lock) {
            if (newList == null)
                throw new NullPointerException();
            if (issuesList != null) {
                issuesList.removeListener(dirtyListener);
                for (IIssue issue : issuesList) {
                    issue.removeIssueListener(dirtyListener);
                }
            }

            IIssuesList oldList = issuesList;
            issuesList = newList;
            testIssues(newList, null);
            this.dirtyListener = new DirtyIssueList();
            issuesList.addListener(dirtyListener);

            IssuesActivator.getDefault().getPreferenceStore().setValue(PREFERENCE_ID,
                    newList.getExtensionID());

            for (IIssuesListListener l : listListeners) {
                issuesList.addListener(l);
            }

            for (IIssue issue : newList) {
                issue.addIssueListener(dirtyListener);
            }

            notifyListeners(newList, oldList, IssuesManagerEventType.ISSUES_LIST_CHANGE);
        }

    }

    private void notifyListeners(Object newValue, Object oldValue, IssuesManagerEventType type) {
        if (type == IssuesManagerEventType.DIRTY_ISSUE
                && !(issuesList instanceof IRemoteIssuesList))
            return;

        IssuesManagerEvent event = new IssuesManagerEvent(this, type, newValue, oldValue);
        for (IIssuesManagerListener listener : listeners) {
            listener.notifyChange(event);
        }
    }

    @Override
    public boolean save(IProgressMonitor monitor) throws IOException {
        if (!(issuesList instanceof IRemoteIssuesList)) {
            if (issuesList instanceof IssuesList) {
                ((IssuesList) issuesList).save();
            }
            return false;
        }

        Object lock = issuesList == null ? this : issuesList;
        final boolean[] result = new boolean[1];
        result[0] = false;
        final Collection<IIssue> dirtyIssues = new ArrayList<>();

        synchronized (lock) {
            monitor.beginTask(Messages.IssuesManager_task_title,
                    dirtyListener.dirtyIssues.size() + 1);
            monitor.worked(1);

            final IOException[] exception = new IOException[1];
            try {
                PlatformGIS.runBlockingOperation(new IRunnableWithProgress() {

                    @Override
                    public void run(IProgressMonitor monitor)
                            throws InvocationTargetException, InterruptedException {

                        dirtyIssues.addAll(dirtyListener.dirtyIssues);
                        IRemoteIssuesList list = (IRemoteIssuesList) issuesList;
                        for (IIssue issue : dirtyListener.dirtyIssues) {
                            result[0] = true;
                            try {
                                list.save(issue);
                                monitor.worked(1);
                            } catch (IOException e) {
                                exception[0] = e;
                            }
                        }
                    }

                }, monitor);
            } catch (Exception e) {
                LoggingSupport.log(IssuesActivator.getDefault(), "Error saving issues", e); //$NON-NLS-1$
            }

            if (exception[0] != null)
                throw exception[0];
        }
        monitor.done();
        notifyListeners(null, dirtyIssues, IssuesManagerEventType.SAVE);

        return result[0];

    }

    @Override
    public void addListener(IIssuesManagerListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(IIssuesManagerListener listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean isDirty() {
        return !dirtyListener.dirtyIssues.isEmpty() && (issuesList instanceof IRemoteIssuesList);
    }

    /**
     * Keeps track of which issues in the list are dirty.
     *
     * @author Jesse
     * @since 1.1.0
     */
    public class DirtyIssueList implements IIssuesListListener, IIssueListener {
        Collection<IIssue> dirtyIssues = new CopyOnWriteArraySet<>();

        @Override
        public void notifyChange(IssuesListEvent event) {
            switch (event.getType()) {
            case ADD:
                testIssues(event.getChanged(), this);
                break;
            case REMOVE:
                for (IIssue issue : event.getChanged()) {
                    issue.removeIssueListener(this);
                }
                break;
            case REFRESH:
                testIssues(event.getChanged(), this);
                break;
            case SAVE:
                dirtyIssues.removeAll(event.getChanged());

                break;
            default:
                break;
            }
        }

        @Override
        public void notifyChanged(IssueEvent event) {
            Boolean oldValue = dirtyIssues.isEmpty() ? Boolean.FALSE : Boolean.TRUE;
            dirtyIssues.add(event.getSource());
            notifyListeners(Boolean.TRUE, oldValue, IssuesManagerEventType.DIRTY_ISSUE);
        }

        @Override
        public void notifyPropertyChanged(IssuePropertyChangeEvent event) {
            Boolean oldValue = dirtyIssues.isEmpty() ? Boolean.FALSE : Boolean.TRUE;
            dirtyIssues.add(event.getSource());
            notifyListeners(Boolean.TRUE, oldValue, IssuesManagerEventType.DIRTY_ISSUE);
        }

    }

    void testIssues(Collection<? extends IIssue> issueList, IIssueListener listener) {
        final Collection<IIssue> toRemove = new ArrayList<>();
        for (IIssue issue : issueList) {
            try {

                // testing to ensure that the issue is a valid issue
                issue.getBounds();
                issue.getDescription();
                issue.getEditorID();
                issue.getEditorInput();
                issue.getExtensionID();
                issue.getGroupId();
                issue.getId();
                issue.getPerspectiveID();
                issue.getPriority();
                issue.getProblemObject();
                issue.getPropertyNames();
                issue.getResolution();
                issue.getViewPartId();

                if (listener != null)
                    issue.addIssueListener(listener);
            } catch (Throwable t) {
                toRemove.add(issue);
            }
        }
        if (!toRemove.isEmpty()) {
            PlatformGIS.run(new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    issuesList.removeAll(toRemove);
                }

            });
        }
    }

}
