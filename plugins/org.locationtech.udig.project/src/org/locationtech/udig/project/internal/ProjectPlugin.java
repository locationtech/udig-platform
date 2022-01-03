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
package org.locationtech.udig.project.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.locationtech.udig.project.internal.impl.ProjectRegistryImpl;
import org.locationtech.udig.ui.PostShutdownTask;
import org.locationtech.udig.ui.ShutdownTaskList;
import org.osgi.framework.BundleContext;

/**
 * This is the central singleton for the Project model plugin. <!-- begin-user-doc --> <!--
 * end-user-doc -->
 *
 * @generated
 */
public final class ProjectPlugin extends EMFPlugin {
    /** Plugin ID */
    public static final String ID = "org.locationtech.udig.project"; //$NON-NLS-1$

    /**
     * Keep track of the singleton. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public static final ProjectPlugin INSTANCE = new ProjectPlugin();

    /**
     * Keep track of the singleton. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    static Implementation plugin;

    /**
     * Create the instance. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public ProjectPlugin() {
        super(new ResourceLocator[] {});
    }

    /**
     * Returns the singleton instance of the Eclipse plugin. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @return the singleton instance.
     * @generated
     */
    @Override
    public ResourceLocator getPluginResourceLocator() {
        return plugin;
    }

    /**
     * Save the collection of projects
     *
     * @param projects projects to save
     * @return Collection of error messages or empty collection
     */
    public static Collection<String> saveProjects(Collection<Project> projects) {
        ArrayList<String> errors = new ArrayList<>();
        for (Project project : projects) {
            try {
                Resource eResource = project.eResource();
                Map<String, String> saveOptions = getPlugin().saveOptions;
                eResource.save(saveOptions);
                List<ProjectElement> elementsInternal = project.getElementsInternal();
                for (ProjectElement projectElement : elementsInternal) {
                    projectElement.eResource().save(saveOptions);
                }
            } catch (Exception e) {
                log("Error while saving resource", e); //$NON-NLS-1$
                String msg = "Error occurred while saving project: " + project.getID().toString(); //$NON-NLS-1$
                errors.add(msg);
            }
        }

        return errors;
    }

    /**
     * Returns the singleton instance of the Eclipse plugin. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @return the singleton instance.
     * @generated
     */
    public static Implementation getPlugin() {
        return plugin;
    }

    /**
     * TODO Purpose of org.locationtech.udig.project.internal
     *
     * @author Jesse
     * @since 1.0.0
     * @generated
     */
    public static class Implementation extends EclipsePlugin {

        /**
         * Creates an instance. <!-- begin-user-doc --> <!-- end-user-doc -->
         *
         * @generated
         */
        public Implementation() {
            super();
            // Remember the static instance.
            plugin = this;
        }

        /**
         * Controls whether the warning message of non-undoable commands is shown.
         */
        private boolean undoableCommandWarning = true;

        /**
         * Sets the control flag if warnings of non-undoable commands are shown
         */
        public void setUndoableCommandWarning(boolean value) {
            undoableCommandWarning = value;
        }

        /**
         * Returns a boolean value if warnings of non-undoable commands are shown
         *
         * @return Boolean value if warnings of non-undoable commands are shown
         */
        public boolean getUndoableCommandWarning() {
            return undoableCommandWarning;
        }

        @Override
        public void start(BundleContext context) throws Exception {
            super.start(context);
            ShutdownTaskList.instance().addPostShutdownTask(new PostShutdownTask() {

                @Override
                public int getProgressMonitorSteps() {
                    List<Resource> resources = getProjectRegistry().eResource().getResourceSet()
                            .getResources();
                    return resources.size();
                }

                @Override
                public void handlePostShutdownException(Throwable t) {
                    ProjectPlugin.log("", t); //$NON-NLS-1$
                }

                @Override
                public void postShutdown(IProgressMonitor monitor, IWorkbench workbench)
                        throws Exception {
                    monitor.beginTask(Messages.ProjectPlugin_saving_task_name, 0);
                    turnOffEvents();
                    List<Resource> resources = getProjectRegistry().eResource().getResourceSet()
                            .getResources();
                    for (Iterator<Resource> iter = resources.iterator(); iter.hasNext();) {
                        Resource resource = iter.next();
                        if (resource == null || resource.getContents() == null
                                || resource.getContents().isEmpty()) {
                            ProjectPlugin.log("Not saving empty contents" //$NON-NLS-1$
                                    + (resource != null ? " " + resource.getURI() : "")); //$NON-NLS-1$ ////$NON-NLS-2$
                            continue;
                        }
                        Object next = resource.getAllContents().next();
                        if (resource.isModified() && next != null && !((EObject) next).eIsProxy()) {
                            try {
                                resource.save(saveOptions);
                            } catch (Exception e) {
                                ProjectPlugin.log("Error saving " + resource.getURI(), e); //$NON-NLS-1$
                            }
                        }
                        monitor.worked(1);
                    }
                }

            });
            undoableCommandWarning = "true" //$NON-NLS-1$
                    .equals(getString("org.locationtech.udig.project.undoableCommandWarning")); //$NON-NLS-1$
        }

        protected static final String ENCODING = "UTF-8"; //$NON-NLS-1$

        /**
         * EMF save parameters.
         *
         * @uml.property name="saveOptions"
         * @uml.associationEnd qualifier="key:java.lang.Object java.lang.String"
         */
        public Map<String, String> saveOptions = new HashMap<>();

        private ScopedPreferenceStore preferenceStore;

        {
            saveOptions.put(XMLResource.OPTION_ENCODING, ENCODING);
        }

        @Override
        public void stop(BundleContext context) throws Exception {
            super.stop(context);
        }

        /**
         * @see org.locationtech.udig.project.internal.ProjectFactory#getProjectRegistry()
         * @uml.property name="projectRegistry"
         */
        public ProjectRegistry getProjectRegistry() {
            return ProjectRegistryImpl.getProjectRegistry();
        }

        public void turnOffEvents() {
            Iterator allIter = getProjectRegistry().eResource().getResourceSet().getAllContents();
            while (allIter.hasNext()) {
                Object tmp = allIter.next();
                Notifier obj = (Notifier) tmp;
                if (obj != null) {
                    obj.eSetDeliver(false);
                }
            }
        }

        /**
         * Returns the preference store for this UI plug-in. This preference store is used to hold
         * persistent settings for this plug-in in the context of a workbench. Some of these
         * settings will be user controlled, whereas others may be internal setting that are never
         * exposed to the user.
         * <p>
         * If an error occurs reading the preference store, an empty preference store is quietly
         * created, initialized with defaults, and returned.
         * </p>
         * <p>
         * <strong>NOTE:</strong> As of Eclipse 3.1 this method is no longer referring to the core
         * runtime compatibility layer and so plug-ins relying on
         * Plugin#initializeDefaultPreferences will have to access the compatibility layer
         * themselves.
         * </p>
         *
         * @return the preference store
         */
        public synchronized ScopedPreferenceStore getPreferenceStore() {
            // Create the preference store lazily.
            if (preferenceStore == null) {
                preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
                        getBundle().getSymbolicName());

            }
            return preferenceStore;
        }
    }

    /**
     * Writes an info log in the plugin's log.
     *
     * @param message
     */
    public static void log(String message) {
        log(message, null);
    }

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log(String message, Throwable e) {
        getPlugin().getLog()
                .log(new Status(IStatus.INFO, ID, 0, message == null ? "" : message, e)); //$NON-NLS-1$
    }

    /**
     * Writes an error log in the plugin's log.
     */
    public static void error(String message) {
        getPlugin().getLog().log(new Status(IStatus.ERROR, ID, message));
    }

    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much preferred to do this:
     *
     * <pre>
     * <code>
     * private static final String RENDERING = "org.locationtech.udig.project/render/trace";
     * if (ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase(RENDERING)) {
     *     System.out.println("your message here");
     * }
     * </code>
     * </pre>
     * </p>
     */
    private static void trace(String message, Throwable e) {
        if (getPlugin().isDebugging()) {
            if (message != null) {
                System.out.println(message);
            }
            if (e != null) {
                e.printStackTrace(System.out);
            }
        }
    }

    /**
     * Messages that only engage if getDefault().isDebugging() and the trace option traceID is true.
     * Available trace options can be found in the Trace class. (They must also be part of the
     * .options file)
     */
    public static void trace(String traceID, Class caller, String message, Throwable e) {
        if (isDebugging(traceID)) {
            trace(caller, message, e);
        }
    }

    /**
     * Adds the name of the caller class to the message.
     *
     * @param caller class of the object doing the trace.
     * @param message tracing message, may be null.
     * @param e exception, may be null.
     */
    public static void trace(Class caller, String message, Throwable e) {
        trace(caller.getSimpleName() + ": " + message, e); //$NON-NLS-1$
    }

    /**
     * Performs the Platform.getDebugOption true check on the provided trace
     * <p>
     * Note: ProjectUIPlugin.getDefault().isDebugging() must also be on.
     * <ul>
     * <li>Trace.RENDER - trace rendering progress</li>
     * </ul>
     * </p>
     *
     * @param trace currently only RENDER is defined
     */
    public static boolean isDebugging(final String trace) {
        return getPlugin().isDebugging() && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$
    }

}
