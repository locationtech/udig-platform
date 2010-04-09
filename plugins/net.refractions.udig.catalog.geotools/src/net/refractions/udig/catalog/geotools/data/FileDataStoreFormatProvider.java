package net.refractions.udig.catalog.geotools.data;

import java.util.Set;

import net.refractions.udig.catalog.service.FormatProvider;

import org.geotools.data.FileDataStoreFinder;

public class FileDataStoreFormatProvider implements FormatProvider {

    public Set<String> getExtensions() {
        Set<String> extensions = FileDataStoreFinder.getAvailableFileExtentions();        
        return extensions;
    }

}
