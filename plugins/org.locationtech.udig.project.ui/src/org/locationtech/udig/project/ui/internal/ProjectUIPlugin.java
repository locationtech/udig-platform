/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.locationtech.udig.core.AbstractUdigUIPlugin;
import org.locationtech.udig.project.ui.feature.FeaturePanelProcessor;
import org.osgi.framework.BundleContext;

/**
 * The Plugin class for the org.locationtech.udig.project plugin. Provides access to plugin
 * resources.
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class ProjectUIPlugin extends AbstractUdigUIPlugin {

    /**
     * The default speed for mouse double click in milliseconds.
     */
    public static final int DEFAULT_DOUBLECLICK_SPEED_MILLIS = 1000;

    private static final String ICONS_PATH = "icons/"; //$NON-NLS-1$

    /** The plugin ID */
    public static final String ID = "org.locationtech.udig.project.ui"; //$NON-NLS-1$

    /** Preference store for the last directory open by the file selection dialog */
    public static final String PREF_OPEN_DIALOG_DIRECTORY = "udig.preferences.openDialog.lastDirectory"; //$NON-NLS-1$

    /**
     * The maximum number of resources per service that can be added to a map without asking the
     * user for permission.
     */
    public static final int MAX_RESOURCES_IN_SERVICE = 1;

	private static ProjectUIPlugin INSTANCE;

    List<AdapterFactory> adapterFactories;

    private static final String ADAPTER_FACTORIES_ID = "org.locationtech.udig.project.ui.itemProviderAdapterFactories"; //$NON-NLS-1$

    public static final String MOUSE_SPEED_KEY = "MOUSE_SPEED_KEY";

    private PropertySheetPage propertySheetPage;

    /**
     * creates a plugin instance
     */
    public ProjectUIPlugin() {
        super();
        INSTANCE = this;
    }

    FeatureEditorExtensionProcessor featureEditProcessor = null;
    
    FeaturePanelProcessor featurePanelProcessor = null;
    
    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        
        new ActiveMapTracker().startup();
    }

    /**
     * Returns the system created plugin object
     * 
     * @return the plugin object
     */
    public static ProjectUIPlugin getDefault() {
        return INSTANCE;
    }

    /**
     * Returns the ToolManager singleton.
     * 
     * @return the ToolManager singleton.
     * @deprecated
     */
    // public ToolManager getToolManager() {
    // return UDIGApplicationUI.getToolManager()
    // }
    /**
     * @return Returns the adapterFactories.
     */
    public List<AdapterFactory> getAdapterFactories() {
        if (adapterFactories == null) {
            adapterFactories = new ArrayList<AdapterFactory>();

            IExtensionRegistry registry = Platform.getExtensionRegistry();
            IExtensionPoint extensionPoint = registry.getExtensionPoint(ADAPTER_FACTORIES_ID);
            IExtension[] extensions = extensionPoint.getExtensions();

            for( int i = 0; i < extensions.length; i++ ) {
                IConfigurationElement[] elements = extensions[i].getConfigurationElements();

                for( int j = 0; j < elements.length; j++ ) {
                    try {
                        Object adapterFactory = elements[j].createExecutableExtension("class"); //$NON-NLS-1$
                        if (adapterFactory instanceof AdapterFactory) {
                            adapterFactories.add((AdapterFactory) adapterFactory);
                        }
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return Collections.unmodifiableList(adapterFactories);
    }

    Object mutex = new Object();

    /**
     * This accesses a cached version of the property sheet. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @return An IProperty page for the selected object
     */
    public IPropertySheetPage getPropertySheetPage() {
        if (propertySheetPage == null) {
            synchronized (mutex) {

                propertySheetPage = new PropertySheetPage(){

                    public void makeContributions( IMenuManager menuManager,
                            IToolBarManager toolBarManager, IStatusLineManager statusLineManager ) {
                        super.makeContributions(menuManager, toolBarManager, statusLineManager);
                    }

                    public void setActionBars( IActionBars actionBars ) {
                        super.setActionBars(actionBars);
                    }
                };
                propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(
                        getAdapterFactory()));
            }
        }

        return propertySheetPage;
    }

    private AdapterFactory adapterFactory;

    /**
     * Returns the adapterfactory instance.
     * 
     * @return the adapterfactory instance.
     */
    public AdapterFactory getAdapterFactory() {
        if (adapterFactory == null) {
            synchronized (mutex) {
                List<AdapterFactory> factories = new ArrayList<AdapterFactory>();

                factories.addAll(ProjectUIPlugin.getDefault().getAdapterFactories());

                // This one should be added last
                factories.add(new ReflectiveItemProviderAdapterFactory());

                adapterFactory = new ComposedAdapterFactory(factories);
            }
        }
        return adapterFactory;
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */

    public void stop( BundleContext context ) throws Exception {
        if (featureEditProcessor != null){
            featureEditProcessor.stopPartListener();
        }
        if (LayerGeneratedGlyphDecorator.getInstance() != null)
            LayerGeneratedGlyphDecorator.getInstance().dispose();
        super.stop(context);
    }

    /**
     * Gets the FeatureEditorProcessor instance.
     * 
     * @return
     */
    public FeatureEditorExtensionProcessor getFeatureEditProcessor() {
        if (featureEditProcessor == null) {
            featureEditProcessor = new FeatureEditorExtensionProcessor();
            featureEditProcessor.startPartListener();
        }
        return featureEditProcessor;
    }
    
    /**
     * Gets the FeatureEditorProcessor instance.
     * 
     * @return
     */
    public FeaturePanelProcessor getFeaturePanelProcessor() {
        if (featurePanelProcessor == null){
            featurePanelProcessor = new FeaturePanelProcessor();
        }
        return featurePanelProcessor;
    }

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log( String message2, Throwable e ) {
        String message=message2;
        if (message == null)
            message = Messages.ProjectUIPlugin_error + e; 
        getDefault().getLog().log(new Status(IStatus.INFO, ID, IStatus.OK, message, e));
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:<pre><code>
     * private static final String RENDERING = "org.locationtech.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * 
     */
    private static void trace( String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (message != null){
                System.out.println(message);
            }
            if (e != null){
                e.printStackTrace(System.out);
            }
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
        trace(caller.getSimpleName()+": "+message, e); //$NON-NLS-1$ //$NON-NLS-2$
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
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$    
    }
    /**
     * The delay used to determine double click speed.
     * 
     * <p>
     * The delay defaults to 100 milliseconds.
     * </p>
     * 
     * @return the milliseconds used for the double-click speed.
     */
    public int getDoubleClickSpeed() {
        IPreferenceStore store = ProjectUIPlugin.getDefault().getPreferenceStore();
        int mouseSpeed = store.getInt(MOUSE_SPEED_KEY);
        if (mouseSpeed == 0) {
            mouseSpeed = DEFAULT_DOUBLECLICK_SPEED_MILLIS;
        }
        return mouseSpeed; 
    }

	/* (non-Javadoc)
	 * @see org.locationtech.udig.core.AbstractUdigUIPlugin#getIconPath()
	 */
	public IPath getIconPath() {
		return new Path(ICONS_PATH);
	}

}
