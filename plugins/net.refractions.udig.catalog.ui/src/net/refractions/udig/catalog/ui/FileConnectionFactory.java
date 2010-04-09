package net.refractions.udig.catalog.ui;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.service.FormatProvider;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

public class FileConnectionFactory extends UDIGConnectionFactory {

    private ArrayList<String> extensionList;

    public boolean canProcess( Object context ) {
        return createConnectionURL(context) != null;
    }

    public Map<String, Serializable> createConnectionParameters( Object context ) {
        // do nothing, we are not connecting to a specific data store
        return null;
    }

    public URL createConnectionURL( Object context ) {
        URL url = CatalogPlugin.locateURL(context);

        if (url == null) {
            return null;
        }

        url = checkedURL(url);
        if (url == null || url.getFile() == null) {
            return null;
        }

        // Checks whether file is acceptable based on extension.
        String fileExt = url.getFile().substring(url.getFile().lastIndexOf('.') + 1);
        if (fileExt != null)
            fileExt = fileExt.toLowerCase();

        for( String goodExt : getExtensionList() ) {
            goodExt = goodExt.toLowerCase();
            if (fileExt.equals(goodExt.substring(goodExt.lastIndexOf('.') + 1))) {
                // actually do a test
                File f = URLUtils.urlToFile(url);
                if (f.exists())
                    return url;
            }
        }
        return null;
    }

    /** Check that any trailing #layer is removed from the url */
    static public URL checkedURL( URL url ) {
        String check = url.toExternalForm();
        int hash = check.indexOf('#');
        if (hash == -1) {
            return url;
        }
        try {
            return new URL(check.substring(0, hash));
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    synchronized List<String> getExtensionList() {
        if (extensionList == null) {
            final Set<String> extensionSet = new TreeSet<String>();
            ExtensionPointUtil.process(CatalogUIPlugin.getDefault(),
                    "net.refractions.udig.catalog.ui.fileFormat", new ExtensionPointProcessor(){ //$NON-NLS-1$
                        public void process( IExtension extension, IConfigurationElement element )
                                throws Exception {
                            if ("fileService".equals(element.getName())) {
                                String ext = element.getAttribute("fileExtension"); //$NON-NLS-1$
                                extensionSet.add(ext);
                            }
                            if ("provider".equals(element.getName())) {
                                FormatProvider provider = (FormatProvider) element
                                        .createExecutableExtension("class");
                                Set<String> extensions = provider.getExtensions();
                                extensionSet.addAll(extensions);
                            }
                        }
                    });
            extensionList = new ArrayList<String>(extensionSet);
        }
        return (List<String>) extensionList.clone();
    }
}
