/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.xml.styling.SLDParser;
import org.locationtech.udig.style.sld.SLDPlugin;
import org.locationtech.udig.style.sld.internal.Messages;

/**
 * "ImportFrom" and SLD file.
 * 
 * @since 1.1.0
 */
public class ImportSLD extends ImportFrom {

    @Override
    public boolean canImport( Object target ) {
        return super.canImport(target);
    }

    @Override
    public String defaultName( Object target ) {
        //unused??
        return null;
    }
    
    @Override
    public String prompt( Object target ) {       
        return Messages.ImportSLD_title; 
    }
    
    @Override
    public String[] getExtentions() {
        return addTo(new String[] {"sld"}, super.getExtentions()); //$NON-NLS-1$
    }
    
    @Override
    public Object importFrom( File file, IProgressMonitor monitor ) throws Exception {
        StyleFactory factory = CommonFactoryFinder.getStyleFactory( null );
        SLDParser stylereader = new SLDParser(factory, file);
        StyledLayerDescriptor newSLD = stylereader.parseSLD();
        //TODO: handle exceptions
        if (newSLD == null) {
            //exceptional!
            SLDPlugin.log("SLD Import returned null", null); //$NON-NLS-1$
            throw (IOException) new IOException("SLD import returned null"); //$NON-NLS-1$
        }
        return newSLD;
    }

    @Override
    public String[] getFilterNames() {
        return addTo(new String[]{Messages.ImportExportSLD_document}, super.getFilterNames()); 
    }

}
