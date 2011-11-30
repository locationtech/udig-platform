package net.refractions.udig.catalog;

import java.io.File;
import java.net.URL;

import org.geotools.data.DataUtilities;

import junit.framework.TestCase;

public class URLUtilsTest extends TestCase {

    public void testStub() throws Exception {
        assertTrue(true);
    }
       
    public void xtestPrefix() throws Exception {
        File file;
        String prefix;
        String os = System.getProperty("os.name");
        if (os.toUpperCase().contains("WINDOWS")) {
            file = new File("C:\\foo\\bar");
            assertEquals("C", "C:", URLUtils.getPrefix(file));

            file = new File("D:\\foo\\bar");
            assertEquals("D", "D:", URLUtils.getPrefix(file));

            file = new File("\\\\machine\\share\\foo.txt");
            prefix = URLUtils.getPrefix(file);
            assertEquals("\\\\machine\\share", prefix);
        }
        file = new File(System.getProperty("user.home"));
        prefix = URLUtils.getPrefix(file);
        assertNull("none", prefix);

        file = new File(System.getProperty("user.dir"));
        prefix = URLUtils.getPrefix(file);
        assertNull("none", prefix);

        file = new File("foo.bar");
        prefix = URLUtils.getPrefix(file);
        assertNull("none", prefix);
    }

    public void xtestToRelativePath() throws Exception {
        URL url;
        File reference = new File(System.getProperty("user.home"));

        URL result;

        String os = System.getProperty("os.name");
        if (os.toUpperCase().contains("WINDOWS")) {

            url = new File("C:\\foo\\bar").toURI().toURL(); //$NON-NLS-1$
            reference = new File("C:/foo/bork/dooda"); //$NON-NLS-1$

            result = URLUtils.toRelativePath(reference, url);
            assertEquals("file:/../bar", result.toString()); //$NON-NLS-1$

            url = new File("C:/foo/bork/dooda").toURI().toURL(); //$NON-NLS-1$
            result = URLUtils.toRelativePath(reference, url);
            assertEquals("file:/./", result.toString()); //$NON-NLS-1$

            url = new File("C:/foo/bork/BLEEP").toURI().toURL(); //$NON-NLS-1$
            result = URLUtils.toRelativePath(reference, url);
            assertEquals("file:/BLEEP", result.toString()); //$NON-NLS-1$

            try {
                url = new URL("file:/C:/Users/Jody/Desktop/raster/norway/trond50geor.jpg");
                reference = new File("C:\\java\\udig\\runtime-udig.product\\.localCatalog");
                result = URLUtils.toRelativePath(reference, url);
                fail("we do not allow this right now");
            } catch (Exception e) {
                // expected
            }
        }
        url = new URL("http://someurl.com"); //$NON-NLS-1$
        result = URLUtils.toRelativePath(reference, url);
        assertSame(url, result);

        url = new URL("file://C:/foo/bar#hi"); //$NON-NLS-1$
        result = URLUtils.toRelativePath(reference, url);
        assertSame(url, result);

    }

    public void xtestConstructURL() throws Exception {
        File home = new File( System.getProperty("user.home"));
        
        URL expected = new File( new File( home, "foo" ), "bar").toURI().toURL();
        File reference = new File( new File( new File( home, "foo" ), "bork"), "dooda" );
        
        URL result;

        result = URLUtils.constructURL(reference, "file:/../bar");
        assertEquals("file:/../bar", result.toString());

        File file = new File(".");
        expected = new URL("http://someurl.com");
        result = URLUtils.constructURL(reference, "http://someurl.com");
        assertEquals(expected.toString(), result.toString());
        
        String path = file.getCanonicalPath().replace('\\', '/');
        expected = new URL("file:/" + path );
        result = URLUtils.constructURL(reference, "file:/" + path );

        assertEquals(expected.toString(), result.toString());

        String os = System.getProperty("os.name");
        if (os.toUpperCase().contains("WINDOWS")) {
            expected = new File("C:/foo/bar").toURL(); //$NON-NLS-1$
            reference = new File("C:/foo/bork/dooda"); //$NON-NLS-1$

            expected = new File("C:/foo/bork").toURL();
            result = URLUtils.constructURL(reference, "file:/./");
            assertEquals(expected.toString(), result.toString());

            expected = new File("C:/foo/bork/BLEEP").toURL();
            result = URLUtils.constructURL(reference, "file:/BLEEP");
            assertEquals(expected.toString(), result.toString());

            expected = new URL("file:/C:/foo/bar#hi");
            result = URLUtils.constructURL(reference, "file:/C:/foo/bar#hi");
            assertEquals(expected.toString(), result.toString());

            expected = new URL("file:/D:/foo/bar#hi");
            result = URLUtils.constructURL(reference, "file:/D:/foo/bar#hi");
            assertEquals(expected.toString(), result.toString());

            file = new File("E:/Rahul/d a t a/uDigSampleData/bc_border.shp");
            result = URLUtils.fileToURL(file);
            File file2 = URLUtils.urlToFile(result);
            assertEquals(file, file2);
        }

    }

}
