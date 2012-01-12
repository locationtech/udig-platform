package net.refractions.udig.catalog;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.geotools.data.DataUtilities;

public class URLDocument extends AbstractDocument {

    private URL url;
    private ID id;

    public URLDocument( URL sourceURL ) {
        url = sourceURL;
        id = new ID(sourceURL);
    }

    public URLDocument( ID sourceID ) {
        url = sourceID.toURL();
        id = sourceID;
    }
    
    @Override
    public String getName() {
        return url.getHost() + url.getPath(); //$NON-NLS-1$
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URI getURI() {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return uri;
    }

    @Override
    public void open() {
        try {
            java.awt.Desktop.getDesktop().browse(url.toURI());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public String getReferences() {
        return url.toExternalForm();
    }

}
