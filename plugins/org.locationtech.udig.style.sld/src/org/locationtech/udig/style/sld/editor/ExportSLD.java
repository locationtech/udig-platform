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
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.xml.styling.SLDTransformer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.style.sld.SLDPlugin;
import org.locationtech.udig.style.sld.internal.Messages;

public class ExportSLD extends ExportTo {

    @Override
    public boolean canExport( Object target ) {
        return super.canExport(target);
    }

    @Override
    public String defaultName( Object target ) {
        String filename = null;
        if (target instanceof Layer) {
            Layer layer = (Layer) target;
            filename = layer.getName();
        } else if (target instanceof StyledLayerDescriptor) {
            StyledLayerDescriptor sld = (StyledLayerDescriptor) target;
            filename = sld.getTitle();
            if (filename == null) filename = sld.getName();
        }
        if (filename == null) filename = Messages.ImportExport_new; 
        filename = filename.replace(':', '_');
        return filename + " " + Messages.ImportExport_selected;  //$NON-NLS-1$
    }
    
    @Override
    public String prompt( Object target ) {       
        return Messages.ExportSLD_export; 
    }
    
    @Override
    public String[] getExtentions() {
        return addTo(new String[] {"sld"}, super.getExtentions()); //$NON-NLS-1$
    }
    
    @Override
    public void exportTo( Object target, File file, IProgressMonitor monitor ) throws Exception {
        SLDTransformer aTransformer = new SLDTransformer();
        aTransformer.setIndentation(StyleEditor.INDENT);
        String xml = ""; //$NON-NLS-1$
        if (target instanceof StyledLayerDescriptor) {
            StyledLayerDescriptor sld = (StyledLayerDescriptor) target;
            xml = aTransformer.transform(sld);
        } else {
            SLDPlugin.log("StyledLayerDescriptor not found", null); //$NON-NLS-1$
            throw (IOException) new IOException("SLD not found"); //$NON-NLS-1$
        }
        FileWriter w = new FileWriter(file);
        w.write(xml);
        w.close();
    }

    @Override
    public String[] getFilterNames() {
        return addTo(new String[]{ Messages.ImportExportSLD_document}, super.getFilterNames()); 
    }

}
