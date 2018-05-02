package org.locationtech.udig.catalog.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
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
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = ((HttpURLConnection)connection);
                if (HttpURLConnection.HTTP_OK != httpConnection.getResponseCode()) {
                    throw new IOException("Responsecode != 200",
                            new Throwable(MessageFormat.format("HTTP {0} : {1}",
                                    httpConnection.getResponseCode(),
                                    httpConnection.getResponseMessage())));
                }
            }
            connection.setConnectTimeout(timeoutInMS);
            connection.connect();
        } catch (Throwable e) {
            Assume.assumeNoException("unable to connect to, skipping test", e);
            throw new RuntimeException(e);
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
