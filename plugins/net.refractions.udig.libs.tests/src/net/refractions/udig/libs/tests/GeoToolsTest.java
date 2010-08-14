/**
 * 
 */
package net.refractions.udig.libs.tests;


import org.geotools.factory.GeoTools;
import org.geotools.util.Version;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This JUnit TestCase is used to verify that GeoTools has been correctly
 * obtained from the libs plugin.
 *
 * @author Jody Garnett
 * @since 1.2.0
 */
public class GeoToolsTest {
    
    @Test
    public void testGeoTools(){
         Version version = GeoTools.getVersion();
         assertEquals( 2, version.getMajor() );
         assertTrue( version.getMinor().toString().startsWith("6") );
    }

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

}
