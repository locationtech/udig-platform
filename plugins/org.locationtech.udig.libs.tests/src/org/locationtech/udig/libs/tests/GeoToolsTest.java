/*
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.libs.tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import javax.swing.Icon;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.eclipse.core.runtime.Platform;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.renderer.style.SVGGraphicFactory;
import org.geotools.util.Version;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.udig.libs.internal.Activator;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Literal;
import org.osgi.framework.Bundle;

/**
 * This JUnit TestCase is used to verify that GeoTools has been correctly
 * obtained from the libs plugin.
 *
 * @author Jody Garnett
 * @since 1.2.0
 */
public class GeoToolsTest {
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Bundle bundle = Platform.getBundle( Activator.ID );
        if( bundle == null ){
            throw new IllegalStateException("Please run as a JUnit Plug-in Test");
        }
    }

    @Test
    public void testGeoTools(){
        Version version = GeoTools.getVersion();
        assertEquals( 19, version.getMajor() );
        assertEquals( 4, version.getMinor() );
    }

    @Ignore("FIXME: due to migration to batik bundle from Orbit")
    @Test
    public void testSVGGraphicsFactory() throws Exception {
        URL url = GeoToolsTest.class.getResource("example.svg");
        
        SVGGraphicFactory svgFactory = new SVGGraphicFactory();
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        Literal expr = ff.literal( url.toExternalForm() );
        
        Icon icon = svgFactory.getIcon(null, expr, "image/svg",16 );
        assertNotNull( icon );
        
    }

    @Ignore("FIXME: due to migration to batik bundle from Orbit")
    @Test
    public void testParseSVG() throws Exception {
        URL url = GeoToolsTest.class.getResource("example.svg");
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        System.out.println("used parser : " + parser);
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        f.createDocument(url.toString());
    }
}
