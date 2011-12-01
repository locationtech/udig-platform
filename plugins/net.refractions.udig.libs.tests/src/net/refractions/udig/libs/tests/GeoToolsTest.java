/**
 * 
 */
package net.refractions.udig.libs.tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import javax.swing.Icon;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.renderer.style.SVGGraphicFactory;
import org.geotools.util.Version;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Literal;
import org.w3c.dom.Document;

/**
 * This JUnit TestCase is used to verify that GeoTools has been correctly
 * obtained from the libs plugin.
 *
 * @author Jody Garnett
 * @since 1.2.0
 */
public class GeoToolsTest {
    
    @Ignore
    public void testGeoTools(){
         Version version = GeoTools.getVersion();
         assertEquals( 2, version.getMajor() );
         assertTrue( version.getMinor().toString().startsWith("6") );
    }
    @Test
    public void testSVGGraphicsFactory() throws Exception {
        URL url = GeoToolsTest.class.getResource("example.svg");
        
        SVGGraphicFactory svgFactory = new SVGGraphicFactory();
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        Literal expr = ff.literal( url.toExternalForm() );
        
        Icon icon = svgFactory.getIcon(null, expr, "image/svg",16 );
        assertNotNull( icon );
        
    }
    
    @Test
    public void testParseSVG() throws Exception {
        URL url = GeoToolsTest.class.getResource("example.svg");
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        Document doc = f.createDocument(url.toString());
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
