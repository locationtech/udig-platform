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
package org.locationtech.udig.internal.ui;

import java.awt.Rectangle;
import java.rmi.server.UID;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.ShutdownTaskList;
import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;
import org.locationtech.udig.ui.internal.Messages;

import si.uom.SI;

/**
 * This class checks that the UDIG plug-ins are able to function.
 * <p>
 * Explicitly this class checks the following:
 * <ul>
 * <li>checkForJAI() - the uDig plug-ins make use of Java Advanced Imaging, this check makes sure
 * that it is in fact available.
 * <li>checkForGDI() - in the windows platform GDI libraries must be available for advanced widgets
 * to be used (so we can have some matrix math).
 * </ul>
 * Over time it would be nice to check the HTTP proxy settings, and a few other things that make it
 * easier to work with uDig (but for now this will do).
 *
 * @author hbullen
 */
public class UDIGStartup implements IStartup {

    /**
     * The following checks are performed when the workbench is first opened.
     * <ul>
     * <li>checkForJAI(): optional - Dig will work with reduced functionality if JAI is not
     * available
     * <li>checkForGDI(): required - uDig will not function on WIN_32 if GDI is not present
     * </ul>
     * This method also loads some commonly used objects; subclasses may override this method (say
     * to ask the user to login)
     *
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    @Override
    public void earlyStartup() {
        checkForGDI();
        loadCommonlyUsedObject();
        PlatformUI.getWorkbench().addWorkbenchListener(ShutdownTaskList.instance());
    }

    /**
     * Forces the class loader to load several troublesome classes; mostly focused on FactorySPI
     * plugins used by the GeoTools library.
     */
    static protected void loadCommonlyUsedObject() {
        // potential fix for win32 (occasionally blocks in Drawing.feature(Point/etc) during first
        // render)
        new UID(); // seed the random number generator

        @SuppressWarnings("unused")
        Object o = SI.GRAM; // SI.BIT
        o = SI.KILOGRAM;
        o = SI.METRE;
        o = SI.RADIAN;
        o = SI.SECOND;
        o = SI.STERADIAN;
    }

    /**
     * Ensures that GDI is available for the windows inclined. GDI is used by SWT to perform matrix
     * operations; uDIG cannot function without GDI on windows.
     *
     * @return false if GDI is needed and not found.
     */
    public static boolean checkForGDI() {
        final boolean[] status = new boolean[1];
        if (Platform.getOS().equals(Platform.OS_WIN32)) {
            final Display display = Display.getDefault();
            display.syncExec(new Runnable() {
                @Override
                public void run() {
                    // test to make sure that GDI+ is installed
                    Image image = null;
                    Path path = null;
                    try {
                        image = new Image(display, 10, 10);
                        path = AWTSWTImageUtils.convertToPath(new Rectangle(0, 0, 8, 8), display);
                        status[0] = true;
                    } catch (Exception e) {
                        final String message =
                            Messages.UDIGApplication_error1
                            + Messages.UDIGApplication_error2
                            + "http://www.microsoft.com/downloads/details.aspx?FamilyID=6A63AB9C-DF12-4D41-933C-BE590FEAA05A&displaylang=en"; //$NON-NLS-1$
                        LoggingSupport.log(UiPlugin.getDefault(), message);

                        MessageDialog dialog = new MessageDialog(display.getActiveShell(),
                                Messages.UDIGApplication_title, null, message, MessageDialog.ERROR,
                                new String[] { "Exit", "Continue" }, 0);
                        int answer = dialog.open();
                        if (answer == 0) {
                            PlatformUI.getWorkbench().close();
                            // System.exit(1); // need to ask the workbench to exit so this is
                            // clean...
                        }
                    } finally {
                        if (image != null)
                            image.dispose();
                        if (path != null)
                            path.dispose();
                    }
                }
            });
        }
        return status[0];
    }

}
