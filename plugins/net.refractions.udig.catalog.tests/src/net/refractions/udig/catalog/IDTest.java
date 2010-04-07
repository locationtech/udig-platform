package net.refractions.udig.catalog;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.geotools.referencing.operation.projection.NewZealandMapGrid;
import org.junit.Test;

public class IDTest {
    
    @Test
    public void testHashCode() {
        ID idFile = new ID( new File("foo.txt"), "txt" );
        String str = idFile.toString();
        
        assertTrue( idFile.hashCode() != 0 );
        assertEquals( str.hashCode(), idFile.toString().hashCode() );
    }

    @Test
    public void testIDFile() {
        File file = new File("foo.txt");
        ID idFile = new ID( file, "txt" );
        
        assertEquals( file, idFile.toFile() );
    }

    @Test
    public void testIDURL() throws Exception {
        File file = new File("foo.txt");
        URL url = file.toURL();
        ID idURL = new ID( url );
        
        assertEquals( url, idURL.toURL() );
    }

    @Test
    public void testIDURI() throws Exception {
        File file = new File("foo.txt");
        URI uri = file.toURI();
        ID idURI = new ID( uri );
        
        assertEquals( uri, idURI.toURI() );
    }

    @Test
    public void testIDIDString() {
        ID id1 = new ID( new File("foo.txt"), "txt" );
        ID id2 = new ID( id1, "anchor" );
        
        assertTrue( id2.toString().endsWith("#anchor") );
    }

}
