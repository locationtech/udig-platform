package net.refractions.udig.catalog;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * This is the url document implementation. This acts as a container to the url itself and provides
 * getters for url related metadata.
 * 
 * @author Naz Chan
 */
public class URLDocument extends AbstractDocument {

    private URL url;
    
    public URLDocument() {
        // Nothing
    }

    public URLDocument(URL url) {
        this.url = url;
    }
    
    public URL getUrl() {
        return url;
    }
    
    public void setUrl(URL url) {
        this.url = url;
    }
    
    @Override
    public String getName() {
        if (url != null) {
            final String detail = url.getHost() + url.getPath(); 
            if (label != null) {
                return String.format(LABEL_FORMAT, label, detail); 
            }
            return detail;    
        } else {
            if (label != null) {
                return String.format(LABEL_FORMAT, label, UNASSIGNED); 
            }
            return UNASSIGNED_NO_LABEL;
        }
    }

    @Override
    public String getDescription() {
        if (url != null) {
            return url.toString();    
        }
        return UNASSIGNED_NO_LABEL;
    }

    @Override
    public URI getURI() {
        if (url != null) {
            try {
                return url.toURI();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }            
        }
        return null;
    }

    @Override
    public boolean open() {
        if (url != null) {
            try {
                java.awt.Desktop.getDesktop().browse(url.toURI());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }            
        }
        return false;
    }

    @Override
    public Type getType() {
        return Type.WEB;
    }

    @Override
    public boolean isEmpty() {
        return (url == null);
    }

}
