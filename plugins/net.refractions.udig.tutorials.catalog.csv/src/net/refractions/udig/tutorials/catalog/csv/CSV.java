/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.tutorials.catalog.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.csvreader.CsvReader;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * A Comma Separated Value document.
 * 
 * @author Jody
 */
public class CSV {
    private File file;
    private int size = -1;    
    public CSV( File file ){
        this.file = file;
    }
    /**
     * Retrieves a CsvReader; this should be closed after use.
     * Please be sure to close the reader after use.
     * 
     * @return CsvReader
     * @throws FileNotFoundException 
     */
    public CsvReader reader() throws IOException {
        Reader reader = new FileReader( file );
        return new CsvReader(reader);
    }
    /** Calculate the size of the file */
    public synchronized int getSize(){
        if( size == -1 ){
            size = 0;
            try {
                CsvReader reader = reader();            
                reader.readHeaders();
                while( reader.readRecord() ){
                    size++;
                }
            }
            catch (IOException eek){                
            }            
        }
        return size;
    }
    public String toString() {
        return file.toString();
    }  
    
    /**
     * Utility method that will return a Point for the current reader.
     * @param reader
     * @return Point
     */
    public static Point getPoint( CsvReader reader ) throws IOException  {
        if( reader == null ) return null;
        GeometryFactory geometryFactory = new GeometryFactory();
        double x = Double.valueOf( reader.get("x"));
        double y = Double.valueOf( reader.get("y"));
        Coordinate coordinate = new Coordinate(x,y);
        return geometryFactory.createPoint( coordinate );
    }
    
}