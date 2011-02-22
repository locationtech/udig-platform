/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
 */
package net.refractions.udig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import net.refractions.udig.core.internal.CorePlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

/**
 * This startup checks to see if the correct version of JAI and Image IO is installed and installs
 * it in ~/Library/Java/Extensions/.
 *
 * @author jesse
 * @since 1.1.0
 */
public class InstallJaiStartup implements IStartup, IRunnableWithProgress, Runnable {

    private ProgressMonitorDialog dialog;

    // copy one file to another. Both must be files and not directories
    private void copy( InputStream source, File dest ) throws IOException {
        if (!dest.exists()) {
            dest.getParentFile().mkdirs();
            dest.createNewFile();
        }

        BufferedInputStream in = new BufferedInputStream(source);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));

        byte[] buf = new byte[1024];
        int len = in.read(buf);
        while( len > 0 ) {
            out.write(buf, 0, len);
            len = in.read(buf);
        }
        in.close();
        out.close();
    }

    public void earlyStartup() {
        try {
            Class.forName("com.sun.media.jai.operator.ImageReadDescriptor"); //$NON-NLS-1$
            // JAI is installed
        } catch (ClassNotFoundException e) {
            final Display display = PlatformUI.getWorkbench().getDisplay();
            display.asyncExec(this);
        }
    }

    public void run( IProgressMonitor monitor ) throws InvocationTargetException,
            InterruptedException {

        String[] files = new String[]{"clibwrapper_jiio.jar", //$NON-NLS-1$
                "jai_codec.jar", //$NON-NLS-1$
                "jai_core.jar", //$NON-NLS-1$
                "jai_imageio.jar", //$NON-NLS-1$
                "mlibwrapper_jai.jar" //$NON-NLS-1$
        };

        monitor.beginTask("", files.length + 1); //$NON-NLS-1$
        monitor.worked(1);

        File destDir = new File(System.getProperty("user.home") + "/Library/Java/Extensions/"); //$NON-NLS-1$//$NON-NLS-2$
        destDir.mkdirs();

        try {
            for( String string : files ) {
                monitor.setTaskName(MessageFormat.format(Messages.InstallJaiStartup_0, string));
                InputStream source = InstallJaiStartup.class.getResourceAsStream(string);
                File dest = new File(destDir, string);

                copy(source, dest);
                monitor.worked(1);
            }
            monitor.done();

        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
    }

    public void run() {
        dialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());

        try {
            dialog.run(false, false, this);
            MessageDialog d = new MessageDialog(
                    dialog.getShell(),
                    Messages.InstallJaiStartup_1,
                    null,
                    Messages.InstallJaiStartup_2,
                    MessageDialog.INFORMATION, new String[]{Messages.InstallJaiStartup_3}, 0);
            d.open();
            PlatformUI.getWorkbench().restart();
        } catch (Exception e1) {
            CorePlugin.log("Unable to copy JAI jars to user library", e1); //$NON-NLS-1$
        }
    }
}
