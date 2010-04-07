/*
 * Created on 29-Sep-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.refractions.udig.printing.ui.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.printing.model.PrintingModelPlugin;
import net.refractions.udig.printing.ui.Template;
import net.refractions.udig.printing.ui.TemplateFactory;
import net.refractions.udig.printing.ui.internal.editor.BoxAction;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Richard Gould TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class PrintingPlugin extends AbstractUIPlugin {
    private static PrintingPlugin plugin;

    private static ArrayList<BoxAction> extensionActions;

    private Map<String, TemplateFactory> templateFactories;

    public static final String TEMPLATE_FACTORIES_ID = "net.refractions.udig.printing.ui.templateFactories"; //$NON-NLS-1$
    public static final String PREF_DEFAULT_TEMPLATE = "udig.preferences.defaultTemplate"; //$NON-NLS-1$

    public static final String ID = "net.refractions.udig.printing.ui"; //$NON-NLS-1$

    private static final String boxEditActionGroupName = "editActionGroup"; //$NON-NLS-1$

    public PrintingPlugin() {
        super();
        plugin = this;
    }

    public static PrintingPlugin getDefault() {
        return plugin;
    }

    /**
     * Produces a of Name --> TemplateFactory
     * @return
     */
    public Map<String, TemplateFactory> getTemplateFactories() {
        if (templateFactories == null) {
            templateFactories = gatherTemplateFactories();
        }
        return templateFactories;
    }

    private Map<String, TemplateFactory> gatherTemplateFactories() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();

        IExtensionPoint extensionPoint = registry.getExtensionPoint(TEMPLATE_FACTORIES_ID);

        IExtension[] extensions = extensionPoint.getExtensions();

        final HashMap<String, TemplateFactory> results = new HashMap<String, TemplateFactory>();

        
        for( int i = 0; i < extensions.length; i++ ) {
            for( IConfigurationElement element : extensions[i].getConfigurationElements() ) {
                if( "templateFactory".equals( element.getName() )){
                    try {
                        Object templateFactory = element.createExecutableExtension("class"); //$NON-NLS-1$
                        if (templateFactory instanceof TemplateFactory) {
                            String id = element.getAttribute("id"); //$NON-NLS-1$
                            results.put(id, (TemplateFactory) templateFactory);
                        } else {
                            log("Bad extension! Declared class is not of type TemplateFactory!", null); //$NON-NLS-1$
                        }
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
                else if( "template".equals( element.getName() )){
                    final String id = element.getAttribute("id"); //$NON-NLS-1$
                    final IConfigurationElement remember = element;                            
                    TemplateFactory fakeFactory = new TemplateFactory(){
                        public Template createTemplate() {
                            try {
                                Object created = remember.createExecutableExtension("class"); //$NON-NLS-1$
                                if( created instanceof Template){
                                    return (Template) created;
                                }
                                else {
                                    log("Bad template for "+id+" Declared class is not of type Template", null); //$NON-NLS-1$
                                    results.remove(id);
                                }
                            }
                            catch (CoreException eek){
                                log( "Could not create template for "+id, eek );
                            }
                            return null;
                        }
                        public String getName() {
                            return remember.getAttribute("name"); //$NON-NLS-1$
                        }
                    };
                    results.put(id, fakeFactory );
                }
            }
        }

        return results;
    }

    /*
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
    }

    /*
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception {
        super.stop(context);
    }

    /**
     * Gets the Edit Actions that need to be added to the PageEditor
     * <p>
     * see net.refractions.printing.ui.boxprinter extension point for deprecated edit actions and
     * the net.refractions.printing.ui.editAction extension point for the "new" extension point
     * </p>
     */
    public synchronized static Collection<BoxAction> getBoxExtensionActions( IWorkbenchPart part ) {
        // In order for this to work the same actions must always be returned
        // TODO someday we need to check to see if there are new actions (IE new plugins were added)
        if (extensionActions == null) {
            extensionActions = new ArrayList<BoxAction>();

            String deprecatedActions = PrintingModelPlugin.BOX_PRINTER_EXTENSION_ID;
            extensionActions.addAll(getEditActions(part, deprecatedActions));

            String editActions = PrintingModelPlugin.EDIT_ACTION_EXTENSION_ID;
            extensionActions.addAll(getEditActions(part, editActions));
        }
        return extensionActions;
    }

    /**
     * @param part
     * @param deprecatedActions
     * @return
     */
    private static Collection<BoxAction> getEditActions( IWorkbenchPart part,
            String deprecatedActions ) {
        ArrayList<BoxAction> extensionActions = new ArrayList<BoxAction>();
        List<IConfigurationElement> list = ExtensionPointList
                .getExtensionPointList(deprecatedActions);
        for( IConfigurationElement element : list ) {
            if (element.getName().equals(PrintingPlugin.boxEditActionGroupName)) {
                String acceptable = element.getAttribute("acceptable"); //$NON-NLS-1$
                IConfigurationElement[] children = element.getChildren("editAction"); //$NON-NLS-1$
                for( IConfigurationElement element2 : children ) {
                    extensionActions.add(new BoxAction(part, element2, acceptable));
                }
            }
        }
        return extensionActions;
    }

    /**
     * Gets all the boxes that will appear in the tool bar.
     * 
     * @return all the boxes that will appear in the tool bar.
     */
    public synchronized List<BoxFactory> getVisibleBoxes() {
        List<BoxFactory> boxes = getBoxes();
        ArrayList<BoxFactory> visibleBoxes = new ArrayList<BoxFactory>();

        for( BoxFactory entry : boxes ) {
            if (entry.isVisible()) {
                visibleBoxes.add(entry);
            }
        }

        return visibleBoxes;
    }

    /**
     * Returns all the BoxFactories
     * 
     * @return all the BoxFactories 
     */
    public List<BoxFactory> getBoxes() {
        List<BoxFactory> boxes = gatherBoxes(ExtensionPointList
                .getExtensionPointList(PrintingModelPlugin.BOX_PRINTER_EXTENSION_ID));
        return boxes;
    }

    private List<BoxFactory> gatherBoxes( List<IConfigurationElement> list ) {

        List<BoxFactory> results = new ArrayList<BoxFactory>();

        for( IConfigurationElement element : list ) {
            if (!element.getName().equals("boxprinter")) //$NON-NLS-1$
                continue;
            results.add(new BoxFactory(element));
        }

        return results;
    }
    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log( String message, Throwable e ) {
        getDefault().getLog().log(new Status(IStatus.INFO, ID, IStatus.OK, message, e));
    }

    public final static String TRACE_PRINTING = "net.refractions.udig.printing.ui/debug/printing"; //$NON-NLS-1$

    /**
     * Performs the Platform.getDebugOption true check on the provided trace
     * 
     * @param trace constant, defined in the Trace class
     * @return true if -debug is on for this plugin
     */
    public static boolean isDebugging( final String trace ) {
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$
    }

    /**
     * Outputs a message or an Exception if the current plug-in is debugging.
     * 
     * @param message if not null, message will be sent to standard out
     * @param e if not null, e.printStackTrace() will be called.
     */
    public static void trace( String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
    }
}
