/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.junit.Test;

public class IDTest {

    @Test
    public void testHashCode() {
        ID idFile = new ID(new File("foo.txt"), "txt"); //$NON-NLS-1$ //$NON-NLS-2$
        String str = idFile.toString();

        assertTrue(idFile.hashCode() != 0);
        assertEquals(str.hashCode(), idFile.toString().hashCode());
    }

    @Test
    public void testIDFile() {
        File file = new File("foo.txt"); //$NON-NLS-1$
        ID idFile = new ID(file, "txt"); //$NON-NLS-1$

        assertEquals(file, idFile.toFile());
    }

    @Test
    public void testIDURL() throws Exception {
        File file = new File("foo.txt"); //$NON-NLS-1$
        URL url = file.toURI().toURL();
        ID idURL = new ID(url);

        assertEquals(url, idURL.toURL());
    }

    @Test
    public void testIDURI() throws Exception {
        File file = new File("foo.txt"); //$NON-NLS-1$
        URI uri = file.toURI();
        ID idURI = new ID(uri);

        assertEquals(uri, idURI.toURI());
    }

    @Test
    public void testIDIDString() {
        ID id1 = new ID(new File("foo.txt"), "txt"); //$NON-NLS-1$ //$NON-NLS-2$
        ID id2 = new ID(id1, "anchor"); //$NON-NLS-1$

        assertTrue(id2.toString().endsWith("#anchor")); //$NON-NLS-1$
    }

    @Test
    public void testIDHandlingOfFragment() throws Exception {
        String os = System.getProperty("os.name"); //$NON-NLS-1$
        File file;
        if (os.toUpperCase().contains("WINDOWS")) { //$NON-NLS-1$
            file = new File(
                    "C:\\Documents and Settings\\pfeiffp\\Desktop\\data_1_2\\data\\cities.shp"); //$NON-NLS-1$
        } else {
            file = new File(System.getProperty("user.home"), "test data/cities.shp"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        ID id = new ID(file, null);
        ID idChild = new ID(id, "cities"); //$NON-NLS-1$

        URL query = idChild.toURL();
        ID idQuery = new ID(query);

        // Expect
        // file:/C:/Documents%20and%20Settings/pfeiffp/Desktop/data_1_2/data/cities.shp#cities
        // Ensure not
        // file:/C:/Documents%2520and%2520Settings/pfeiffp/Desktop/data_1_2/data/cities.shp#cities
        assertEquals("file service / georesource", id.toFile(), idChild.toFile()); //$NON-NLS-1$
        assertEquals("file 'query' / georesource", id.toFile(), idQuery.toFile()); //$NON-NLS-1$
        assertEquals("uri 'query' / georesource", idChild.toURI(), idQuery.toURI()); //$NON-NLS-1$
        assertEquals("str  'query' / georesource", idChild.toString(), idQuery.toString()); //$NON-NLS-1$
        assertEquals("ID  'query' / georesource", idChild, idQuery); //$NON-NLS-1$
    }

}
