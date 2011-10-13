/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008-2011, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.junit.Test;

public class IDTest {

    @Test
    public void testHashCode() {
        ID idFile = new ID(new File("foo.txt"), "txt");
        String str = idFile.toString();

        assertTrue(idFile.hashCode() != 0);
        assertEquals(str.hashCode(), idFile.toString().hashCode());
    }

    @Test
    public void testIDFile() {
        File file = new File("foo.txt");
        ID idFile = new ID(file, "txt");

        assertEquals(file, idFile.toFile());
    }

    @Test
    public void testIDURL() throws Exception {
        File file = new File("foo.txt");
        URL url = file.toURL();
        ID idURL = new ID(url);

        assertEquals(url, idURL.toURL());
    }

    @Test
    public void testIDURI() throws Exception {
        File file = new File("foo.txt");
        URI uri = file.toURI();
        ID idURI = new ID(uri);

        assertEquals(uri, idURI.toURI());
    }

    @Test
    public void testIDIDString() {
        ID id1 = new ID(new File("foo.txt"), "txt");
        ID id2 = new ID(id1, "anchor");

        assertTrue(id2.toString().endsWith("#anchor"));
    }

    @Test
    public void testIDHandlingOfFragment() throws Exception {
        // TODO: ensure we are on windows here!
        String os = System.getProperty("os.name");
        File file;;
        if (os.toUpperCase().contains("WINDOWS")) {
            file = new File(
                    "C:\\Documents and Settings\\pfeiffp\\Desktop\\data_1_2\\data\\cities.shp");
        }
        else {
            file = new File(System.getProperty("user.home"),"test data/cities.shp");
        }
        ID id = new ID(file, null);
        ID idChild = new ID(id, "cities");

        URL query = idChild.toURL();
        ID idQuery = new ID(query);

        // Expect
        // file:/C:/Documents%20and%20Settings/pfeiffp/Desktop/data_1_2/data/cities.shp#cities
        // Ensure not
        // file:/C:/Documents%2520and%2520Settings/pfeiffp/Desktop/data_1_2/data/cities.shp#cities
        assertEquals("file service / georesource", id.toFile(), idChild.toFile());
        assertEquals("file 'query' / georesource", id.toFile(), idQuery.toFile());
        assertEquals("uri 'query' / georesource", idChild.toURI(), idQuery.toURI());
        assertEquals("str  'query' / georesource", idChild.toString(), idQuery.toString());
        assertEquals("ID  'query' / georesource", idChild, idQuery);
    }

}