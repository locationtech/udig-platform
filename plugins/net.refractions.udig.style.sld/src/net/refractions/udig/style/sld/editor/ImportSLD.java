package net.refractions.udig.style.sld.editor;

import java.io.File;
import java.io.IOException;

import net.refractions.udig.style.sld.SLDPlugin;
import net.refractions.udig.style.sld.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.styling.SLDParser;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryFinder;
import org.geotools.styling.StyledLayerDescriptor;

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
        StyleFactory factory = StyleFactoryFinder.createStyleFactory();
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
