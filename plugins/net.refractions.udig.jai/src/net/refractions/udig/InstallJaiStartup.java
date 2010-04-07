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

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;


/**
 * This startup checks to see if the correct version of JAI and Image IO is installed and installs
 * it in the JREs lib/ext if permitted by OS.  The jars are platform independent so for better performance
 * native implementations are recommended.
 * 
 * @author jesse
 * @since 1.2.0
 */
public class InstallJaiStartup implements IRunnableWithProgress, Runnable {

    private final class RunInProgressDialog implements Runnable {
		public void run() {
		    dialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());

		    try {
		        dialog.run(false, false, InstallJaiStartup.this);

	            String productName = productName();
	            
		        MessageDialog restartDialog = new MessageDialog(
		                dialog.getShell(),
		                Messages.InstallJaiStartup_1,
		                null,
		                Messages.InstallJaiStartup_2,
		                MessageDialog.INFORMATION, new String[]{MessageFormat.format(Messages.InstallJaiStartup_3, productName)}, 0);
		        restartDialog.open();
		        PlatformUI.getWorkbench().restart();
		    } catch (Exception e1) {
		        CorePlugin.log("Unable to copy JAI jars to user library", e1); //$NON-NLS-1$
		    }
		}

	}
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

    // runnable implementation method... installs JAI if it is not present in JRE
    public void run() {
    	if(!jaiInstalled()){

			RunInProgressDialog progressDialog = new RunInProgressDialog();
			if (Display.getCurrent() != null) {
				progressDialog.run();
			} else {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.asyncExec(progressDialog);
			}
    	}
    }

    private boolean jaiInstalled() {
        try {
            Class.forName("com.sun.media.jai.operator.ImageReadDescriptor"); //$NON-NLS-1$
            // JAI is installed
            return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

    /** runnableWithProgress implementation.  Installs JAI if possible
     * otherwise throws an exception
     */
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

        String[] extDirs=System.getProperty("java.ext.dirs").split(File.pathSeparator); //$NON-NLS-1$
        File writeable = null;
        for (String dirName : extDirs) {
			File dir = new File(dirName);
			if(dir.exists() && dir.isDirectory() && dir.canWrite()){
				writeable=dir;
				break;
			}
		}
        
        if( writeable==null ){
        	Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					String pattern = Messages.InstallJaiStartup_noWriteMessage;
					MessageDialog.openError(dialog.getShell(), Messages.InstallJaiStartup_errorTitle, 
							MessageFormat.format(pattern, productName()) );
				}
			});
        	throw new InvocationTargetException(new Exception(),"Unable to write install JAI, no writable directory"); //$NON-NLS-1$
        }

        try {
            for( String string : files ) {
                monitor.setTaskName(MessageFormat.format(Messages.InstallJaiStartup_0, string));
                InputStream source = InstallJaiStartup.class.getResourceAsStream(string);
                File dest = new File(writeable, string);

                copy(source, dest);
                monitor.worked(1);
            }
            monitor.done();

        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
    }
    private String productName() {
    	String productName;
    	IProduct product = Platform.getProduct();
    	if( product == null ){
    		CorePlugin.log("there is no product so default to uDig", null); //$NON-NLS-1$
    		productName = "uDig"; //$NON-NLS-1$
    	}else{
    		productName = product.getName();
    	}
    	return productName;
    }


}
