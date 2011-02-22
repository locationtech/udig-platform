package net.refractions.udig.ui;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * This class is used to configure a menu bar and a cool bar. This is called
 * when the workbench starts up. The uDig ActionBarAdvisor delegates to this
 * class if supplied.
 * <p>
 * The implementation of this interface that is called by the framework
 * is specified by the extension in the preferences under the key
 * 'net.refractions.udig.ui/menuBuilder'. You can place this in your
 * product's plugin_customization.ini as a quick way to hack a uDig install
 * to look like a custom app.
 *
 * Example entry in plugin_customization.ini:
 * <pre>
 * net.refractions.udig.ui/menuBuilder=net.refractions.udig.ui.UDIGMenuBuilder
 * </pre>
 *
 * See net.refractions.udig.ui.UDIGMenuBuilder for an example implementation.
 *
 * @deprecated Please use org.eclipse.ui.menus extention point
 * @author Richard Gould, Refractions Research Inc.
 * @since 1.1.0
 */
public interface MenuBuilder {

    public final String XPID = "net.refractions.udig.ui.menuBuilders"; //$NON-NLS-1$
    /**
     * Points to id field of extension point attribute
     */
    public final String ATTR_ID = "id"; //$NON-NLS-1$

    /**
     * Points to class field of extension point attribute
     */
    public final String ATTR_CLASS = "class"; //$NON-NLS-1$

    /**
     * Instructs this class that it should configure and fill the provided
     * IMenuManager with menus and actions.
     *
     * @param menuBar
     * @param window The window that contains this menu
     */
    public abstract void fillMenuBar( IMenuManager menuBar, IWorkbenchWindow window );

    /**
     * Instructs this class to configure and fill the provided ICoolBarManager
     * with actions and buttons.
     *
     * @param coolBar
     * @param window The window that contains the CoolBar
     */
    public abstract void fillCoolBar( ICoolBarManager coolBar, IWorkbenchWindow window );

}
