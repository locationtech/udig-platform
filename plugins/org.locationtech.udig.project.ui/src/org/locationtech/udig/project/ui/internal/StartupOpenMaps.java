/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.part.EditorPart;
import org.geotools.referencing.CRS;
import org.locationtech.udig.internal.ui.UDIGDropHandler;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.commands.OpenProjectElementCommand;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;
import org.locationtech.udig.ui.IDropAction;
import org.locationtech.udig.ui.IDropHandlerListener;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 * Loads the maps from the previous session and processes the command line arguments.
 * <p>
 * If the DND extension point will be called so that it will act as if the URL/string whatever was
 * dropped on the application. So if a URL to shapefile is on the commandline a new map will be
 * opened and will display the shapefile.
 *
 * @author jones
 */
public class StartupOpenMaps implements IStartup {

    private String[] args;

    public StartupOpenMaps() {
        args = Platform.getApplicationArgs();
    }

    /**
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    @Override
    public void earlyStartup() {

        List<URL> urls = new ArrayList<>(args.length);

        for (String string : args) {
            String urlString = string;
            try {
                if (urlString.startsWith("-")) { //$NON-NLS-1$
                    continue;
                }
                // try to make string a URL
                if (!urlString.contains(":/")) { //$NON-NLS-1$
                    File file = new File(urlString);
                    if (!file.exists())
                        continue;
                    urlString = file.toURI().toURL().toString();
                }
                if (urlString.startsWith("file:/")) {//$NON-NLS-1$
                    try {
                        new URL(urlString);
                    } catch (IOException ie) {
                        String tmp = urlString.substring(5);
                        if (tmp.contains(":")) { //$NON-NLS-1$
                            tmp = tmp.substring(tmp.indexOf(":")); //$NON-NLS-1$
                        }
                        File file = new File(tmp);
                        if (!file.exists())
                            continue;
                    }
                }

                urls.add(new URL(urlString));

            } catch (MalformedURLException e) {
                // ignore
            }
        }

        if (!urls.isEmpty()) {
            final List<URL> finalurls = urls;
            final String title = Messages.StartupOpenMaps_openURLDialogTitle;
            final IRunnableWithProgress openMapRunnable = new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    dropURLS(finalurls, monitor);
                }

            };
            PlatformGIS.runInProgressDialog(title, true, openMapRunnable, true);
        } else {
            IPreferenceStore p = ProjectUIPlugin.getDefault().getPreferenceStore();
            boolean proceed = p.getBoolean(PreferenceConstants.P_OPEN_MAPS_ON_STARTUP);
            if (!proceed) {
                return;
            }
            int numEditors = p.getInt(MapEditorWithPalette.ID);
            if (numEditors == 0)
                return;
            final String title = Messages.StartupOpenMaps_openMapDialogTitle;
            final IRunnableWithProgress openMapRunnable = new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    openLastOpenMaps(monitor);
                }

            };

            PlatformGIS.runInProgressDialog(title, true, openMapRunnable, true);
        }
    }

    /**
     * Opens maps from previous section. Only opens if there are no maps in the arguments, IE no
     * maps dropped onto uDig icon or uDig was not started by clicking on an associated file.
     */
    private void openLastOpenMaps(IProgressMonitor monitor) {
        IPreferenceStore p = ProjectUIPlugin.getDefault().getPreferenceStore();
        int numEditors = p.getInt(MapEditorWithPalette.ID);
        monitor.beginTask(Messages.StartupOpenMaps_openMapDialogTitle, numEditors * 2 + 2);
        p.setValue(MapEditorWithPalette.ID, 0);

        for (int i = 0; i < numEditors; i++) {
            monitor.worked(1);
            String id = MapEditorWithPalette.ID + ":" + i; //$NON-NLS-1$
            String name = p.getString(id);
            if (name == null || name.equals("")) //$NON-NLS-1$
                continue;
            p.setValue(id, ""); //$NON-NLS-1$
            if (!monitor.isCanceled()) {
                monitor.setTaskName(Messages.StartupOpenMaps_loadingTask + name);
                URI mapResourceURI = URI.createURI(name);
                Resource resource = ProjectPlugin.getPlugin().getProjectRegistry().eResource()
                        .getResourceSet().getResource(mapResourceURI, true);

                monitor.worked(1);
                // kick the classloader so that the CRS plug-ins are loaded correctly.
                try {
                    CRS.decode("EPSG:4326"); //$NON-NLS-1$
                } catch (NoSuchAuthorityCodeException e) {
                    throw (RuntimeException) new RuntimeException().initCause(e);
                } catch (FactoryException e) {
                    throw (RuntimeException) new RuntimeException().initCause(e);
                }
                final Object object = resource.getContents().get(0);
                if (object instanceof IProjectElement) {
                    OpenProjectElementCommand command = new OpenProjectElementCommand(
                            (IProjectElement) object);
                    monitor.setTaskName(Messages.StartupOpenMaps_OpenTask + ": " //$NON-NLS-1$
                            + ((IProjectElement) object).getName());
                    ApplicationGIS.getActiveProject().sendASync(command);
                }
            }
        }
    }

    private void dropURLS(List<URL> urls, IProgressMonitor monitor) {
        monitor.beginTask(Messages.StartupOpenMaps_openURLDialogTitle, urls.size() * 1 + 2);
        monitor.worked(1);
        Viewer viewer = LayersView.getViewer();
        if (viewer == null)
            ProjectUIPlugin.trace(getClass(), "Layers View is not available", (Exception) null); //$NON-NLS-1$

        UDIGDropHandler dropHandler = new UDIGDropHandler();
        dropHandler.setTarget(new EditorPart() {

            @Override
            public void doSave(IProgressMonitor monitor) {

            }

            @Override
            public void doSaveAs() {

            }

            @Override
            public void init(IEditorSite site, IEditorInput input) throws PartInitException {
            }

            @Override
            public boolean isDirty() {
                return false;
            }

            @Override
            public boolean isSaveAsAllowed() {
                return false;
            }

            @Override
            public void createPartControl(Composite parent) {

            }

            @Override
            public void setFocus() {

            }

        });

        closeIntro();

        for (URL url : urls) {
            monitor.worked(1);
            monitor.setTaskName(Messages.StartupOpenMaps_processingTask + ": " + url); //$NON-NLS-1$
            if (monitor.isCanceled())
                break;
            ProcessingURLSListener listener = new ProcessingURLSListener(this);
            try {
                dropHandler.addListener(listener);
                dropHandler.performDrop(url, null);
                while (!listener.processed && !monitor.isCanceled()) {
                    synchronized (this) {
                        try {
                            wait(500);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
            } finally {
                dropHandler.removeListener(listener);
            }
        }
    }

    private void closeIntro() {
        PlatformGIS.syncInDisplayThread(new Runnable() {

            @Override
            public void run() {
                IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
                IIntroPart intro = introManager.getIntro();
                if (intro != null)
                    introManager.closeIntro(intro);
            }

        });
    }

    public void testingSetArgs(String[] strings) {
        if (strings == null)
            this.args = new String[0];
        else {

            this.args = new String[strings.length];
            System.arraycopy(strings, 0, this.args, 0, args.length);
        }
    }

    private static class ProcessingURLSListener implements IDropHandlerListener {
        volatile boolean processed = false;

        StartupOpenMaps lock;

        public ProcessingURLSListener(StartupOpenMaps maps) {
            lock = maps;
        }

        @Override
        public void done(IDropAction action, Throwable t) {
            processed = true;

            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        public void starting(IDropAction action) {

        }

        @Override
        public void noAction(Object data) {
            processed = true;
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    /**
     * Stores open maps as references in preferences store. This allows the Startup implementation
     * to restore Maps if {@link PreferenceConstants#P_OPEN_MAPS_ON_STARTUP} is true.
     *
     * @param mapResourceURIToStore URI of map resource
     */
    public static void persistInfoForOpenMap(URI mapResourceURIToStore) {
        persistInfoForOpenMap(ProjectUIPlugin.getDefault().getPreferenceStore(),
                mapResourceURIToStore);
    }

    /**
     * Same as {@link #persistInfoForOpenMap(URI)} but with given PrefStore.
     *
     * @param preferenceStore PreferenceStore to save preferences.
     * @param mapResourceURIToStore URI of map resource
     */
    public static void persistInfoForOpenMap(IPreferenceStore preferenceStore,
            URI mapResourceURIToStore) {
        int numEditors = getNumberOfOpenMaps(preferenceStore, false);
        String id = getMapIdForPrefStore(numEditors++);
        preferenceStore.setValue(PreferenceConstants.P_OPEN_MAPS_PREFIX_ID, numEditors);
        preferenceStore.setDefault(id, ""); //$NON-NLS-1$
        preferenceStore.setValue(id, mapResourceURIToStore.toString());
    }

    public static int getNumberOfOpenMaps(IPreferenceStore preferenceStore, boolean reset) {
        int result = preferenceStore.getInt(PreferenceConstants.P_OPEN_MAPS_PREFIX_ID);
        if (reset) {
            preferenceStore.setValue(PreferenceConstants.P_OPEN_MAPS_PREFIX_ID, 0);
        }
        return result;
    }

    public static String getMapEditorForIndex(IPreferenceStore preferenceStore, int index,
            boolean clear) {
        String id = getMapIdForPrefStore(index);
        String openMapFromLastSession = preferenceStore.getString(id);
        if (clear) {
            preferenceStore.setValue(id, ""); //$NON-NLS-1$
        }
        return openMapFromLastSession;
    }

    private static String getMapIdForPrefStore(int index) {
        return PreferenceConstants.P_OPEN_MAPS_PREFIX_ID + ":" + index; //$NON-NLS-1$
    }
}
