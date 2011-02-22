/**
 * <copyright></copyright> $Id: ProjectPlugin.java 30923 2008-10-25 04:34:39Z jeichar $
 */
package net.refractions.udig.project.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.refractions.udig.project.internal.impl.ProjectRegistryImpl;
import net.refractions.udig.ui.PostShutdownTask;
import net.refractions.udig.ui.ShutdownTaskList;

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
import org.osgi.framework.BundleContext;

/**
 * This is the central singleton for the Project model plugin.
 * <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * @generated
 */
public final class ProjectPlugin extends EMFPlugin {
    /** Plugin ID */
    public final static String ID = "net.refractions.udig.project"; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * Keep track of the singleton.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
     * Create the instance.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ProjectPlugin() {
        super(new ResourceLocator[]{});
    }

    /**
     * Returns the singleton instance of the Eclipse plugin.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return the singleton instance.
     * @generated
     */
    public ResourceLocator getPluginResourceLocator() {
        return plugin;
    }

    /**
     * Returns the singleton instance of the Eclipse plugin.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return the singleton instance.
     * @generated
     */
    public static Implementation getPlugin() {
        return plugin;
    }

    /**
     * TODO Purpose of net.refractions.udig.project.internal <p> </p>
     * @author   Jesse
     * @since   1.0.0
     * @generated
     */
    public static class Implementation extends EclipsePlugin {

        /**
         * Creates an instance.
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * @generated
         */
        public Implementation() {
            super();

            // Remember the static instance.
            //
            plugin = this;
        }
        /**
         * Controls whether the warning message of non-undoable commands is shown.
         * @deprecated Use the getter and setter methods.
         */
        public boolean undoableCommandWarning = true;

        public void setUndoableCommandWarning( boolean value ) {
            undoableCommandWarning = value;
        }

        public boolean getUndoableCommandWarning() {
            return undoableCommandWarning;
        }

        /**
         * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
         */
        public void start( BundleContext context ) throws Exception {
            super.start(context);
            ShutdownTaskList.instance().addPostShutdownTask(new PostShutdownTask(){

                public int getProgressMonitorSteps() {
                    List resources = getProjectRegistry().eResource().getResourceSet()
                            .getResources();
                    return resources.size();
                }

                public void handlePostShutdownException( Throwable t ) {
                    ProjectPlugin.log("", t); //$NON-NLS-1$
                }

                public void postShutdown( IProgressMonitor monitor, IWorkbench workbench )
                        throws Exception {
                    monitor.beginTask(Messages.ProjectPlugin_saving_task_name, 0);
                    turnOffEvents();
                    List resources = getProjectRegistry().eResource().getResourceSet()
                            .getResources();
                    for( Iterator iter = resources.iterator(); iter.hasNext(); ) {
                        Resource resource = (Resource) iter.next();
                        if (resource.getContents().isEmpty())
                            continue;
                        Object next = resource.getAllContents().next();
                        if (resource.isModified() && next != null && !((EObject) next).eIsProxy()) {
                            try {
                                resource.save(saveOptions);
                            } catch (Exception e) {
                                ProjectPlugin.log("Error saving", e); //$NON-NLS-1$
                            }
                        }
                        monitor.worked(1);
                    }
                }

            });
            undoableCommandWarning = "true".equals(getString("net.refractions.udig.project.undoableCommandWarning")); //$NON-NLS-1$//$NON-NLS-2$
        }

        protected static final String ENCODING = "UTF-8"; //$NON-NLS-1$

        /**
         * EMF save parameters.
         * @uml.property   name="saveOptions"
         * @uml.associationEnd   qualifier="key:java.lang.Object java.lang.String"
         */
        public Map<String, String> saveOptions = new HashMap<String, String>();

        private ScopedPreferenceStore preferenceStore;

        {
            saveOptions.put(XMLResource.OPTION_ENCODING, ENCODING);
        }

        /**
         * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
         */
        public void stop( BundleContext context ) throws Exception {
            super.stop(context);
        }

        /**
         * @see net.refractions.udig.project.internal.ProjectFactory#getProjectRegistry()
         * @uml.property   name="projectRegistry"
         */
        public ProjectRegistry getProjectRegistry() {
            return ProjectRegistryImpl.getProjectRegistry();
        }

        public void turnOffEvents() {
            Iterator allIter = getProjectRegistry().eResource().getResourceSet().getAllContents();
            while( allIter.hasNext() ) {
                Object tmp = allIter.next();
                Notifier obj = (Notifier) tmp;
                if (obj != null) {
                    obj.eSetDeliver(false);
                }
            }
        }

        /**
         * Returns the preference store for this UI plug-in.
         * This preference store is used to hold persistent settings for this plug-in in
         * the context of a workbench. Some of these settings will be user controlled,
         * whereas others may be internal setting that are never exposed to the user.
         * <p>
         * If an error occurs reading the preference store, an empty preference store is
         * quietly created, initialized with defaults, and returned.
         * </p>
         * <p>
         * <strong>NOTE:</strong> As of Eclipse 3.1 this method is
         * no longer referring to the core runtime compatibility layer and so
         * plug-ins relying on Plugin#initializeDefaultPreferences
         * will have to access the compatibility layer themselves.
         * </p>
         *
         * @return the preference store
         */
        public synchronized ScopedPreferenceStore getPreferenceStore() {
            // Create the preference store lazily.
            if (preferenceStore == null) {
                preferenceStore = new ScopedPreferenceStore(new InstanceScope(), getBundle()
                        .getSymbolicName());

            }
            return preferenceStore;
        }
    }

    /**
     * Writes an info log in the plugin's log.
     * @param message
     */
    public static void log( String message ) {
        log(message, null);
    }

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log( String message, Throwable e ) {
        getPlugin().getLog()
                .log(new Status(IStatus.INFO, ID, 0, message == null ? "" : message, e)); //$NON-NLS-1$
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:<pre><code>
     * private static final String RENDERING = "net.refractions.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     *
     */
    private static void trace( String message, Throwable e ) {
        if (getPlugin().isDebugging()) {
            if (message != null)
                System.out.println(message + "\n"); //$NON-NLS-1$
            if (e != null)
                e.printStackTrace(System.out);
        }
    }
    /**
     * Messages that only engage if getDefault().isDebugging() and the trace option traceID is true.
     * Available trace options can be found in the Trace class.  (They must also be part of the .options file)
     */
    public static void trace( String traceID, Class caller, String message, Throwable e ) {
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
    public static void trace( Class caller, String message, Throwable e ) {
        trace("Tracing - " + caller.getSimpleName() + ": " + message, e); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Performs the Platform.getDebugOption true check on the provided trace
     * <p>
     * Note: ProjectUIPlugin.getDefault().isDebugging() must also be on.
     * <ul>
     * <li>Trace.RENDER - trace rendering progress
     * </ul>
     * </p>
     *
     * @param trace currently only RENDER is defined
     */
    public static boolean isDebugging( final String trace ) {
        return getPlugin().isDebugging() && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$

    }

}
