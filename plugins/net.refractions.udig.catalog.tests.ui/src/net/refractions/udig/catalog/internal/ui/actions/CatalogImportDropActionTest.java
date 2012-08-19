/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.ui.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jesse
 * @since 1.1.0
 */
public class CatalogImportDropActionTest {

    private CatalogImportDropAction action;

    @Before
    public void setUp() throws Exception {
        action = new CatalogImportDropAction();
    }

    /**
     * Test method for
     * {@link net.refractions.udig.catalog.internal.ui.actions.CatalogImportDropAction#canImport(java.lang.Object)}.
     */
    @Test
    public void testCanImport() {
        // TODO implement this test
    }

    /**
     * Test method for
     * {@link net.refractions.udig.catalog.internal.ui.actions.CatalogImportDropAction#extractURL(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testExtractURLWindows() throws Exception {
        String s = "c:\\ap_102\\yoohoo.shp";

        URL url = action.extractURL(s);

        assertEquals(s, url.getFile());
    }

    /**
     * Test method for
     * {@link net.refractions.udig.catalog.internal.ui.actions.CatalogImportDropAction#extractURL(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testExtractURLWindowsNetwork() {
        String s = "\\\\c\\ap_102\\yoohoo.shp";

        URL url = action.extractURL("file:"+s);

        assertEquals(s, url.getFile());
    }

    /**
     * Test method for
     * {@link net.refractions.udig.catalog.internal.ui.actions.CatalogImportDropAction#extractURL(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testExtractURLGOTO() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link net.refractions.udig.catalog.internal.ui.actions.CatalogImportDropAction#extractURL(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testExtractURLEmbedded() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link net.refractions.udig.catalog.internal.ui.actions.CatalogImportDropAction#extractURL(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testExtractURLHref() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link net.refractions.udig.catalog.internal.ui.actions.CatalogImportDropAction#extractURL(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testExtractURLStandard() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link net.refractions.udig.catalog.internal.ui.actions.CatalogImportDropAction#extractURL(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testExtractURLPostgis() {
        fail("Not yet implemented");
    }

}
