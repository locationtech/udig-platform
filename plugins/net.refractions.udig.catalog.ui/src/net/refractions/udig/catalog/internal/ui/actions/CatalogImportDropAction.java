package net.refractions.udig.catalog.internal.ui.actions;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.internal.ui.CatalogImport;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.UDIGConnectionFactory;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.core.internal.CorePlugin;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.ui.IDropAction;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Handles data being dropped on the catalog. Pretty generic
 *
 * @author jdeolive
 * @author Jesse
 */
public class CatalogImportDropAction extends IDropAction {

    PageProcessor p;

    public CatalogImportDropAction() {
        super();
    }

    @Override
    public boolean accept() {
        Object data = getData();
        if (data.getClass().isArray()) {
            Object[] objects = ((Object[]) data);
            for( Object object : objects ) {
                if (canAccept(object)) {
                    return false;
                }
            }
            return true;
        } else if (canAccept(data)) {
            return true;
        }

        return canAccept(data);
    }

    private boolean canAccept( Object data ) {
        if (data instanceof IResolve) {
            IResolve resolve = (IResolve) data;
            List<IResolve> find = CatalogPlugin.getDefault().getLocalCatalog().find(
                    resolve.getIdentifier(), new NullProgressMonitor());
            if (find != null && !find.isEmpty())
                return false;

            return canImport(data);
        }
        return canImport(data);
    }

    /**
     * returns true if the data can be imported into the catalog irregardless if it is already in
     * catalog
     *
     * @param data data to import
     * @return true if the data can be imported into the catalog irregardless if it is already in
     *         catalog
     */
    protected boolean canImport( Object data1 ) {
        Object data = data1;
        if (getData() instanceof String) {
            URL url = extractURL((String) getData());
            if (url != null) {
                data = url;
            }
        }
        // process the wizard page extension point to determine if anyone
        // can process the object
        p = new PageProcessor(data);
        ExtensionPointUtil.process(CatalogUIPlugin.getDefault(), UDIGConnectionFactory.XPID, p);

        return !p.ids.isEmpty();
    }

    @Override
    public void perform( IProgressMonitor monitor ) {
        Object data = getData();
        if (data instanceof String) {
            URL url = extractURL((String) data);
            if (url != null) {
                data = url;
            }
        }

        CatalogImport catalogImport = new CatalogImport();
        catalogImport.run(monitor, data);
    }
    /**
     * Searches a String looking for URLs and returns the first one it can find.
     *
     * @param data
     * @return
     */
    protected URL extractURL( String data ) {
        String decoded = data;
        try {
            decoded = URLDecoder.decode(decoded, "UTF-8"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e2) {
            // so ignore...
        }
        decoded = decoded.replaceAll("&amp;", "&"); //$NON-NLS-1$ //$NON-NLS-2$

        URL result = null;

        String line = decoded.replace("\\S+", ""); //$NON-NLS-1$

        result = matchAnchorTag(line);

        if (result != null) {
            return formatFileURL(result);
        }

        result = matchGotoTag(line);

        if (result != null) {
            return formatFileURL(result);
        }

        result = matchHttpTag(result, line);

        if (result != null) {
            return formatFileURL(result);
        }

        try {
            result = new URL(line);
        } catch (MalformedURLException e) {
        }
        if (result == null && !line.contains(":/")) { //$NON-NLS-1$
            // maybe its a file?
            try {
                String string = "file:" + line; //$NON-NLS-1$
                result = new URL(string);
            } catch (MalformedURLException e2) {
            }
        }
        if (result == null) {
            try {
                result = new URL(null, line, CorePlugin.RELAXED_HANDLER);
            } catch (MalformedURLException e1) {
                // worth a try

            }
        }
        return formatFileURL(result);
    }

    /**
     * Make sure that if the url is a file URL getFile will get the full path including the drive on
     * windows/
     *
     * @param result
     * @return
     */
    private URL formatFileURL( URL result ) {
        if (result.getProtocol().equalsIgnoreCase("file")) { //$NON-NLS-1$
            try {
                return new URL(result.toExternalForm());
            } catch (MalformedURLException e) {
                return result;
            }
        }
        return result;
    }

    private URL matchHttpTag( URL result, String line ) {
        // search for URL
        Pattern urlPattern = Pattern.compile("([hH][Tt][Tt][Pp]\\S*)"); //$NON-NLS-1$
        Matcher urlMatcher = urlPattern.matcher(line);

        if (urlMatcher.find()) {
            try {
                String group = urlMatcher.group(1);
                int index = group.indexOf('"');
                if (index != -1)
                    group = group.substring(0, index);
                index = group.indexOf('>');
                if (index != -1)
                    group = group.substring(0, index);
                result = new URL(group);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private URL matchGotoTag( String line ) {
        Pattern anchorPattern = Pattern.compile("goto=(\\S+)"); //$NON-NLS-1$
        Matcher anchorMatcher = anchorPattern.matcher(line);

        URL result = null;
        if (anchorMatcher.find()) {
            try {
                String group = anchorMatcher.group(1);
                int index = group.indexOf("\""); //$NON-NLS-1$
                if (index == -1)
                    index = group.length();
                result = new URL(group.substring(0, index));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private URL matchAnchorTag( String line ) {
        // search for anchor tags
        Pattern anchorPattern = Pattern.compile("<a.*href=\"(\\S+)\"\\S*"); //$NON-NLS-1$
        Matcher anchorMatcher = anchorPattern.matcher(line);

        URL result = null;
        if (anchorMatcher.find()) {
            try {
                String group = anchorMatcher.group(1);
                int index = group.indexOf("\""); //$NON-NLS-1$
                if (index == -1)
                    index = group.length();
                result = new URL(group.substring(0, index));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    static class PageProcessor implements ExtensionPointProcessor {

        Object data;
        List<String> ids = new ArrayList<String>();

        PageProcessor( Object data ) {
            this.data = data;
        }

        public void process( IExtension extension, IConfigurationElement element ) throws Exception {

            try {
                if (!"factory".equals(element.getName())) //$NON-NLS-1$
                    return;
                UDIGConnectionFactory factory = (UDIGConnectionFactory) element
                        .createExecutableExtension("class"); //$NON-NLS-1$

                if (factory.canProcess(data)) {
                    // get the id
                    IConfigurationElement[] elements = extension.getConfigurationElements();
                    for( int i = 0; i < elements.length; i++ ) {
                        // if ("wizardPage".equals(elements[i].getName())) { //$NON-NLS-1$
                        ids.add(elements[i].getAttribute("id")); //$NON-NLS-1$
                        // break;
                        // }
                    }
                }
            } catch (Throwable t) {
                String msg = "Error processing wizard page"; //$NON-NLS-1$
                CatalogPlugin.log(msg, t);
            }
        }

    }

    // class CatalogWizard extends CatalogImportWizard {
    //
    // List<String> ids;
    //
    // CatalogWizard(List<String> ids) {
    // this.ids = ids;
    //
    // }
    //
    // @Override
    // protected WizardPage[] getPrimaryPages() {
    //
    // DataSourceSelectionPage page = new DataSourceSelectionPage();
    // page.select(ids);
    //
    // return new WizardPage[] { page };
    //
    // }
    //
    // }
    //
    // class MapWizard extends AddLayersWizard {
    //
    // List<String> ids;
    //
    // MapWizard(List<String> ids) {
    // this.ids = ids;
    // }
    //
    // @Override
    // protected WizardPage[] getPrimaryPages() {
    //
    // DataSourceSelectionPage page = new DataSourceSelectionPage();
    // page.select(ids);
    //
    // ResourceSelectionPage rpage = new ResourceSelectionPage(
    // Messages.AddLayersWizard_layerSelection
    // );
    //
    // return new WizardPage[] { page, rpage };
    //
    // }
    //
    // }
}
