package net.refractions.udig.catalog;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

public class URLUtilsTest extends TestCase {

	public void testToRelativePath() throws Exception {
		URL url=new File("c:\\foo\\bar").toURL(); //$NON-NLS-1$
		File reference = new File( "c:/foo/bork/dooda" ); //$NON-NLS-1$

		URL result = URLUtils.toRelativePath(reference, url);
		assertEquals( "file:/../bar", result.toString()); //$NON-NLS-1$

		url=new File( "c:/foo/bork/dooda" ).toURL(); //$NON-NLS-1$
		result = URLUtils.toRelativePath(reference, url);
		assertEquals( "file:/./", result.toString()); //$NON-NLS-1$

		url=new File( "c:/foo/bork/BLEEP" ).toURL(); //$NON-NLS-1$
		result = URLUtils.toRelativePath(reference, url);
		assertEquals( "file:/BLEEP", result.toString()); //$NON-NLS-1$


		url=new URL("http://someurl.com"); //$NON-NLS-1$
		result = URLUtils.toRelativePath(reference, url);
		assertSame(url, result);


		url=new URL("file://c:/foo/bar#hi"); //$NON-NLS-1$
		result = URLUtils.toRelativePath(reference, url);
		assertSame(url, result);

	}

	public void testConstructURL() throws Exception {
		URL expected=new File("c:/foo/bar").toURL(); //$NON-NLS-1$
		File reference = new File( "c:/foo/bork/dooda" ); //$NON-NLS-1$

		URL result = URLUtils.constructURL(reference, "file:/../bar"); //$NON-NLS-1$
		assertEquals( expected.toString(), result.toString());

		expected=new File( "c:/foo/bork" ).toURL(); //$NON-NLS-1$
		result = URLUtils.constructURL(reference, "file:/./"); //$NON-NLS-1$
		assertEquals( expected.toString() , result.toString());

		expected=new File( "c:/foo/bork/BLEEP" ).toURL(); //$NON-NLS-1$
		result = URLUtils.constructURL(reference, "file:/BLEEP"); //$NON-NLS-1$
		assertEquals( expected.toString(), result.toString());


		expected=new URL("http://someurl.com"); //$NON-NLS-1$
		result = URLUtils.constructURL(reference, "http://someurl.com"); //$NON-NLS-1$
		assertEquals(expected.toString(), result.toString());

		expected=new URL("file:/c:/foo/bar#hi"); //$NON-NLS-1$
		result = URLUtils.constructURL(reference, "file:/c:/foo/bar#hi"); //$NON-NLS-1$
		assertEquals(expected.toString(), result.toString());

		expected=new URL("file:/d:/foo/bar#hi"); //$NON-NLS-1$
		result = URLUtils.constructURL(reference, "file:/d:/foo/bar#hi"); //$NON-NLS-1$
		assertEquals(expected.toString(), result.toString());

		File file = new File("."); //$NON-NLS-1$
		expected = new URL("file:/"+file.getCanonicalPath()); //$NON-NLS-1$
		result = URLUtils.constructURL(reference, "file:/"+file.getCanonicalPath()); //$NON-NLS-1$
		assertEquals(expected.toString(), result.toString());

	}

}
