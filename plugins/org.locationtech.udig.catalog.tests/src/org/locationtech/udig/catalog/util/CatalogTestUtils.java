package org.locationtech.udig.catalog.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.junit.Assume;

/**
 * Utility Catalog Test functions
 * 
 * @author FGasdorf
 *
 */
public final class CatalogTestUtils {

    /**
     * uses Assume.assumeNoException to verify if its possible to connect to an URL
     * @param urls List of URL's to connect to
     */
    public static void assumeNoConnectionException(URL url, int timeoutInMS) {
        try {
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(timeoutInMS);
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
            Assume.assumeNoException(e);
        }
    }
    
    /**
     * uses Assume.assumeNoException to verify if its possible to connect to an URL
     * @param urls List of URL's to connect to
     */
    public static void assumeNoConnectionException(List<URL> urls, int timeoutInMS) {
        if (urls != null) {
            for (URL url : urls) {
                assumeNoConnectionException(url, timeoutInMS);
            }
        }
    }

}
