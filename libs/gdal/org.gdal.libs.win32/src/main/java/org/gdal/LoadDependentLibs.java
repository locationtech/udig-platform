package org.gdal;

import it.geosolutions.imageio.gdalframework.GDALUtilities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.ContributorFactoryOSGi;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author fgdrf
 *
 */
public class LoadDependentLibs {


    private static Logger LOG = Logger.getLogger(LoadDependentLibs.class.getName());
    /**
     * load dependent libraries in correct order 
     */
    public LoadDependentLibs() {
        System.loadLibrary("lti_dsdk_dll");
        System.loadLibrary("msvcp71");
        System.loadLibrary("msvcR71");
        System.loadLibrary("gdal17");
        
        try {
            System.loadLibrary("gdal_ECW_JP2ECW");
        } catch (UnsatisfiedLinkError e) {
            // may occur on win64 bit systems with 32bit JRE
            LOG.warning("unable to load native gdal library 'gdal_ECW_JP2ECW'");
        }
    }
    
    public void initFragement(BundleContext context) {
        final IExtensionRegistry reg = Platform.getExtensionRegistry();

        if (GDALUtilities.isDriverAvailable("ECW")) {
            addExtension(context, "*.ecw", "ECW");
        }

        if (GDALUtilities.isDriverAvailable("MrSID")) {
            addExtension(context, "*.sid", "MrSID");
        }
    }
    
    /**
     * registers an additional extension for Extension-Point net.refractions.udig.catalog.ui.fileFormat
     * @param context
     * @param fileExtension
     * @param name
     */
    private void addExtension(BundleContext context, String fileExtension, String name) {
        StringBuffer sb = new StringBuffer();
        sb.append("<fragment>");
        sb.append(" <extension id=\"net.refractions.udig.catalog.imageio.formats\" name=\"ImageIO File Formats\" point=\"net.refractions.udig.catalog.ui.fileFormat\">");
        sb.append("  <fileService fileExtension=\"" + fileExtension + "\" name=\""+name+"\"/>");
        sb.append(" </extension>");
        sb.append("</fragment>");

        final IContributor contributor = ContributorFactoryOSGi.createContributor(context.getBundle());
        
        InputStream is = null;
        try {
                is = new ByteArrayInputStream(sb.toString().getBytes("UTF-8")); //$NON-NLS-1$
        } catch (final UnsupportedEncodingException e) {
            System.err.println(e);
        }
        
        final IExtensionRegistry reg = Platform.getExtensionRegistry();
        
        final Object token = ((ExtensionRegistry) reg).getTemporaryUserToken(); // internal

        if (is != null) {
            reg.addContribution(is, contributor, false, null, null, token);
        }
    }
}
