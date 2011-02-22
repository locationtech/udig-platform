/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2007, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * Created on Jun 23, 2004
 */
package net.refractions.udig.internal.ui;
import java.awt.Rectangle;
import java.rmi.server.UID;
import java.util.Iterator;
import java.util.Set;

import javax.units.SI;

import net.refractions.udig.ui.graphics.SWTGraphics;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.geotools.referencing.FactoryFinder;

/**
 * IApplication used by the uDig product - subclass for your own application.
 * <p>
 * The UDIGApplication serves three goals:
 * <ul>
 * <li>It is a show case of what the SDK can do; usable by end-users as a functional GIS
 * Application
 * <li>It is an example for other RCP developers to copy (either cut and paste or subclass)
 * <li>It provides static final helper methods that can be used by other RCP developers
 * </ul>
 * If you wanted to configure how the menus and toolbars are ordered please look into
 * overriding the UDIGWorkbenchAdvisor in addition to this class.

 * <h2>Rolling your Own</h2>
 * Your application will need to be registered with the
 * <code>org.eclipse.equinox.applications</code> extension-point.
 *
 * @author Jesse Eichar, Refractions Research Inc.
 * @author Jody Garnett, Refractions Research Inc.
 * @since 0.3
 */
public class UDIGApplication implements IApplication {

    /**
     * Starts GIS application with the given context and returns a result. This method must not exit
     * until the application is finished and is ready to exit. The content of the context is
     * unchecked and should conform to the expectations of the application being invoked.
     * <p>
     * Applications can return any object they like. If an <code>Integer</code> is returned it is
     * treated as the program exit code if Eclipse is exiting.
     * <p>
     * Note: This method is called by the platform; it is not intended to be called directly by
     * clients.
     * </p>
     *
     * @return the return value of the application
     * @see #EXIT_OK
     * @see #EXIT_RESTART
     * @see #EXIT_RELAUNCH
     * @param context the application context to pass to the application
     * @exception Exception if there is a problem running this application.
     */
    @SuppressWarnings("unused")
    public Object start( IApplicationContext context ) throws Exception {
        WorkbenchAdvisor workbenchAdvisor = createWorkbenchAdvisor();
        Display display = PlatformUI.createDisplay();

        boolean isInitalized = init();
        if (!isInitalized) {
            return EXIT_OK;
        }
        int returnCode = EXIT_OK;
        try {
            returnCode = PlatformUI.createAndRunWorkbench(display, workbenchAdvisor);
        } catch (Throwable t) {
            UiPlugin.log(Messages.UDIGApplication_error, t);
        } finally {
            Platform.endSplash();
            display.dispose();
        }
        if (returnCode == PlatformUI.RETURN_RESTART) {
            return EXIT_RESTART;
        }
        return EXIT_OK;
    }

    /**
     * Returns the WorkbenchAdvisor that will control the setup of the application
     * <p>
     * It is recommended but not required that the advisor be a subclass of {@link UDIGWorkbenchAdvisor}
     * </p>
     *
     * @return the WorkbenchAdvisor that will control the setup of the application
     * @see UDIGWorkbenchAdvisor
     */
    protected WorkbenchAdvisor createWorkbenchAdvisor() {
        return new UDIGWorkbenchAdvisor();
    }

    /**
     * Forces this running application to exit. This method should wait until the running
     * application is ready to exit. The {@link #start(IApplicationContext)} should already have
     * exited or should exit very soon after this method exits
     * <p>
     * This method is only called to force an application to exit. This method will not be called if
     * an application exits normally from the {@link #start(IApplicationContext)} method.
     * <p>
     * Note: This method is called by the platform; it is not intended to be called directly by
     * clients.
     * </p>
     */
    public void stop() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null){
            return;
        }
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable(){
            public void run() {
                if (!display.isDisposed()){
                    workbench.close();
                }
            }
        });
    };

    /**
     * Called before the workbench is created.
     * <p>
     * The following checks are performed:
     * <ul>
     * <li>checkForJAI(): optional - Dig will work with reduced functionality if JAI is not
     * available
     * <li>checkForGDI(): required - uDig will not function on WIN_32 if GDI is not present
     * </ul>
     * This method also loads some commonly used objects; subclasses may override this method (say
     * to ask the user to login)
     *
     * @return <code>true </code> on successful startup; false to exit the application with an
     *         error.
     */
    protected boolean init() {
        checkForJAI();
        boolean required = checkForGDI();

        if (!required) {
            // we could not meet our requirements; please exit!
            return false;
        }

        loadCommonlyUsedObject();

        boolean login = checkLogin();
        if (!login) {
            return false;
        }
        return true;
    }

    /**
     * Forces the class loader to load several troublesome classes; mostly focused on FactorySPI
     * plugins used by the GeoTools library.
     */
    private void loadCommonlyUsedObject() {
        // potential fix for win32 (occasionally blocks in Drawing.feature(Point/etc) during first
        // render)
        new UID(); // seed the random number generator

        load(FactoryFinder.getCoordinateOperationAuthorityFactories());
        load(FactoryFinder.getCoordinateOperationAuthorityFactories());
        load(FactoryFinder.getCRSFactories());
        load(FactoryFinder.getCSFactories());
        load(FactoryFinder.getDatumAuthorityFactories());
        load(FactoryFinder.getDatumFactories());
        load(FactoryFinder.getMathTransformFactories());
        @SuppressWarnings("unused")
        Object o = SI.BIT;
        o = SI.GRAM;
        o = SI.KILOGRAM;
        o = SI.METER;
        o = SI.RADIAN;
        o = SI.SECOND;
        o = SI.STERADIAN;
    }

    @SuppressWarnings("unchecked")
    private void load( Set coordinateOperationAuthorityFactories ) {
        for( Iterator iter = coordinateOperationAuthorityFactories.iterator(); iter.hasNext(); ) {
            iter.next();
        }
    }

    /**
     * Ensures that GDI is available for the windows inclined. GDI is used by SWT to perform matrix
     * operations; uDIG cannot function without GDI on windows.
     *
     * @return false if GDI is needed and not found.
     */
    public static boolean checkForGDI() {
        if (Platform.getOS().equals(Platform.OS_WIN32)) {
            // test to make sure that GDI+ is installed
            Image image = null;
            Path path = null;
            try {
                image = new Image(Display.getCurrent(), 10, 10);
                path = SWTGraphics.convertToPath(new Rectangle(0, 0, 8, 8), Display.getCurrent());
            } catch (Exception e) {
                MessageDialog
                        .openError(
                                Display.getCurrent().getActiveShell(),
                                Messages.UDIGApplication_title,
                                Messages.UDIGApplication_error1
                                        + Messages.UDIGApplication_error2
                                        + "http://www.microsoft.com/downloads/details.aspx?FamilyID=6A63AB9C-DF12-4D41-933C-BE590FEAA05A&displaylang=en"); //$NON-NLS-1$
                return false;
            } finally {
                if (image != null)
                    image.dispose();
                if (path != null)
                    path.dispose();
            }
        }
        return true;
    }

    /**
     * Ensures Java Advanced Imaging is installed into the JRE being used.
     * <p>
     * If JAI is not available a dialog will be displayed and false is returned. JAI is not required
     * for everything; currently rasters will not function without JAI; but many simple vector
     * formats will.
     * </p>
     *
     * @return true if JAI is available.
     */
    public static boolean checkForJAI() {
        try {
            Class.forName("javax.media.jai.operator.OrDescriptor"); //$NON-NLS-1$
            return true;
        } catch (Throwable th) {
            JaiErrorDialog.display();
            return false;
        }
    }

    /**
     * Override to perform your own security check.
     *
     * @return true to indicate a successful login
     */
    public boolean checkLogin() {
        return true;
    }
}
