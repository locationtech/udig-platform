/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.SystemUtils;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

public class URLUtilsTest {

    public static String SERVICE_COMPARISON_TEST_URL = "http://www.randomurl.com"; //$NON-NLS-1$

    @Test
    public void testEquals() throws Exception {

        URL url1 = new URL("file:/C:/test/my test/my test.shp"); //$NON-NLS-1$
        URL url2 = new URL("file:/C:/test/my test/mytest.shp"); //$NON-NLS-1$
        URL url3 = new URL("file:/C:/test/mytest/mytest.shp"); //$NON-NLS-1$
        URL url4 = new URL("file:/C:/test/mytest/mytest.shp#mytest"); //$NON-NLS-1$
        URL url5 = new URL("file:/C:/test/my test/my test.shp#my%20test"); //$NON-NLS-1$
        URL url6 = new URL("file:/C:/test/my test/my test.shp#my test"); //$NON-NLS-1$

        assertEquals(true, URLUtils.urlEquals(url1, url1, true));
        assertEquals(false, URLUtils.urlEquals(url1, url2, true));
        assertEquals(false, URLUtils.urlEquals(url2, url3, true));

        assertEquals(true, URLUtils.urlEquals(url1, url5, true));
        assertEquals(true, URLUtils.urlEquals(url5, url1, true));
        assertEquals(false, URLUtils.urlEquals(url1, url5, false));
        assertEquals(false, URLUtils.urlEquals(url5, url1, false));

        assertEquals(true, URLUtils.urlEquals(url1, url6, true));
        assertEquals(true, URLUtils.urlEquals(url6, url1, true));
        assertEquals(false, URLUtils.urlEquals(url1, url6, false));
        assertEquals(false, URLUtils.urlEquals(url6, url1, false));

        assertEquals(true, URLUtils.urlEquals(url3, url4, true));
        assertEquals(true, URLUtils.urlEquals(url4, url3, true));
        assertEquals(false, URLUtils.urlEquals(url3, url4, false));
        assertEquals(false, URLUtils.urlEquals(url4, url3, false));
    }

    @Test
    @Ignore("expected:<file:/[..]/bar> but was:<file:/[C:/foo]/bar>")
    public void testRelativeSameLevelWin() throws MalformedURLException {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        URL url = new File("C:\\foo\\bar").toURI().toURL(); //$NON-NLS-1$
        File reference = new File("C:/foo/bork/dooda"); //$NON-NLS-1$
        assertEquals("file:/../bar", URLUtils.toRelativePath(reference, url).toString()); //$NON-NLS-1$
    }

    @Test
    @Ignore("expected:<file:/.[./bar]> but was:<file:/.[/]>")
    public void testRelativeFromHereWin() throws MalformedURLException {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        URL url = new File("C:/foo/bork/dooda").toURI().toURL(); //$NON-NLS-1$
        File reference = new File("C:/foo/bork/dooda"); //$NON-NLS-1$
        assertEquals("file:/../bar", URLUtils.toRelativePath(reference, url).toString()); //$NON-NLS-1$
    }

    @Test
    @Ignore("expected:<file:/BLEEP> but was:<file:/C:/foo/bork/BLEEP>")
    public void testRelativeFromBeep() throws MalformedURLException {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        URL url = new File("C:/foo/bork/BLEEP").toURI().toURL(); //$NON-NLS-1$
        File reference = new File("C:/foo/bork/dooda"); //$NON-NLS-1$
        assertEquals("file:/BLEEP", URLUtils.toRelativePath(reference, url)); //$NON-NLS-1$
    }

    @Test
    @Ignore("not supported (yet)")
    public void testToRelativePath() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        try {
            URL url = new URL("file:/C:/Users/Jody/Desktop/raster/norway/trond50geor.jpg"); //$NON-NLS-1$
            File reference = new File("C:\\java\\udig\\runtime-udig.product\\.localCatalog"); //$NON-NLS-1$
            URLUtils.toRelativePath(reference, url);
            fail("we do not allow this right now"); //$NON-NLS-1$
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testPathWithSharp() throws MalformedURLException {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        File reference = new File(System.getProperty("user.home")); //$NON-NLS-1$
        URL url = new URL("file://C:/foo/bar#hi"); //$NON-NLS-1$
        Object result = URLUtils.toRelativePath(reference, url);
        assertSame(url, result);
    }

    @Test
    public void testSameUrlRelativePathHttpURL() throws MalformedURLException {
        File reference = new File(System.getProperty("user.home")); //$NON-NLS-1$
        URL url = new URL("http://someurl.com"); //$NON-NLS-1$
        URL result = URLUtils.toRelativePath(reference, url);
        assertSame(url, result);
    }

    @Test
    @Ignore("expected:<file:/[..]/bar> but was:<file:/[C:/Users/Razer/foo]/bar>")
    public void testConstructRaleativeBarToBork() throws MalformedURLException {
        File home = new File(System.getProperty("user.home")); //$NON-NLS-1$
        // URL expected = new File(new File(home, "foo"), "bar").toURI().toURL();
        File reference = new File(new File(new File(home, "foo"), "bork"), "dooda"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("file:/../bar", URLUtils.constructURL(reference, "file:/../bar").toString()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testConstructUrl() throws MalformedURLException {
        File home = new File(System.getProperty("user.home")); //$NON-NLS-1$
        File reference = new File(new File(new File(home, "foo"), "bork"), "dooda"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        URL expected = new URL("http://someurl.com"); //$NON-NLS-1$
        assertEquals(expected.toString(),
                URLUtils.constructURL(reference, "http://someurl.com").toString()); //$NON-NLS-1$
    }

    @Test
    public void testConstructURLrelativePath() throws IOException {
        File home = new File(System.getProperty("user.home")); //$NON-NLS-1$
        File reference = new File(new File(new File(home, "foo"), "bork"), "dooda"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        File file = new File("."); //$NON-NLS-1$
        String path = file.getCanonicalPath().replace('\\', '/');
        URL expected = new URL("file:/" + path); //$NON-NLS-1$
        assertEquals(expected.toString(),
                URLUtils.constructURL(reference, "file:/" + path).toString()); //$NON-NLS-1$
    }

    @Test
    public void testConstructURL() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);

        URL expected = new File("C:/foo/bar").toURI().toURL(); //$NON-NLS-1$
        File reference = new File("C:/foo/bork/dooda"); //$NON-NLS-1$

        expected = new File("C:/foo/bork").toURI().toURL(); //$NON-NLS-1$
        URL result = URLUtils.constructURL(reference, "file:/./"); //$NON-NLS-1$
        assertEquals(expected.toString(), result.toString());

        expected = new File("C:/foo/bork/BLEEP").toURI().toURL(); //$NON-NLS-1$
        result = URLUtils.constructURL(reference, "file:/BLEEP"); //$NON-NLS-1$
        assertEquals(expected.toString(), result.toString());

        expected = new URL("file:/C:/foo/bar#hi"); //$NON-NLS-1$
        result = URLUtils.constructURL(reference, "file:/C:/foo/bar#hi"); //$NON-NLS-1$
        assertEquals(expected.toString(), result.toString());

        expected = new URL("file:/D:/foo/bar#hi"); //$NON-NLS-1$
        result = URLUtils.constructURL(reference, "file:/D:/foo/bar#hi"); //$NON-NLS-1$
        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testUrl2FileAndBackWithSpacesInPath() {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        File file = new File("E:/Rahul/d a t a/uDigSampleData/bc_border.shp"); //$NON-NLS-1$
        URL result = URLUtils.fileToURL(file);
        File file2 = URLUtils.urlToFile(result);
        assertEquals(file, file2);
    }

    @Test
    public void testUrlEqualsWindows() throws MalformedURLException {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);

        assertTrue(URLUtils.urlEquals(new URL("file://c:\\java/udig/"), //$NON-NLS-1$
                new URL("file:/C:\\java\\udig"), false)); //$NON-NLS-1$
        assertFalse(URLUtils.urlEquals(new URL("file://d:\\java/udig/"), //$NON-NLS-1$
                new URL("file:/C:\\java\\udig"), false)); //$NON-NLS-1$
        assertTrue(URLUtils.urlEquals(new URL("file:///C:/java/udig"), //$NON-NLS-1$
                new URL("file:/C:\\java\\udig"), false)); //$NON-NLS-1$
    }

    @Test
    public void testUrlEquals() throws Exception {
        assertTrue(URLUtils.urlEquals(new URL("file:///java/udig"), //$NON-NLS-1$
                new URL("file:/java/udig"), false)); //$NON-NLS-1$
        assertFalse(URLUtils.urlEquals(new URL("file:///Java/udig"), //$NON-NLS-1$
                null, false));
        assertFalse(URLUtils.urlEquals(new URL("file:///java/udig"), //$NON-NLS-1$
                new URL("file:/java/udig#ResourceName"), false)); //$NON-NLS-1$
        assertTrue(URLUtils.urlEquals(new URL("file:///java/udig"), //$NON-NLS-1$
                new URL("file:/java/udig#ResourceName"), true)); //$NON-NLS-1$
    }
}
