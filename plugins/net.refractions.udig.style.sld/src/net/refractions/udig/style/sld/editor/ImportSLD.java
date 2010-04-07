/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.style.sld.editor;

import java.io.File;
import java.io.IOException;

import net.refractions.udig.style.sld.SLDPlugin;
import net.refractions.udig.style.sld.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.SLDParser;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyledLayerDescriptor;

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
