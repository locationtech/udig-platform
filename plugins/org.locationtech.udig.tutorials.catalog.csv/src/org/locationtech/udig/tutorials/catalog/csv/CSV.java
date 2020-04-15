/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.catalog.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

/**
 * A Comma Separated Value document.
 * <p>
 * Although the OpenCSV library provides a parser for CSV files
 * it is handy to have a wrapper class to hold on to a specific {@link File}
 * and provide access to the CSV contents.
 * <p>
 * We are making use of the <b>OpenCSV</b> library so that this tutorial does not
 * get bogged down in how to correct handle quote characters, and to give
 * you a chance to wrap up a provided jar as an OSGi bundle for use with uDig.
 * 
 * @See http://opencsv.sourceforge.net/
 * @author Jody
 */
public class CSV {
    private static Set<String> LONGITUDE = new HashSet<String>(
            Arrays.asList(new String[]{"X","LON","LONGITUDE"}));
    private static Set<String> LATITUDE = new HashSet<String>(
            Arrays.asList(new String[]{"Y","LAT","LATITUDE"}));
    
    private File file;
    private int lat = -1;
    private int lon = -1;
    private int size = -1;
    private String header[] = null;
    
    public CSV( File file ){
        this.file = file;
    }
    /**
     * Retrieves a CsvReader; this should be closed after use.
     * <p>
     * The initial header will be read, and the returned CSVReader
     * will be in position to read the first row of content.
     * 
     * Please be sure to close the reader after use.
     * 
     * @return CSVReader, must call {@link CSVReader#close()} to free FileReader
     * @throws FileNotFoundException 
     */
    public CSVReader reader() throws IOException {
        Reader fileReader = new FileReader( file );
        CSVReader reader = new CSVReader(fileReader);
        String row[] = reader.readNext();
        if( header == null ){
            setHeader(row);
        }
        return reader;
    }
    public void setHeader(String[] row) {
        header = row;
        for( int col=0; col<header.length;col++){
            String name = header[col];
            if( name == null ) continue;
            if( LATITUDE.contains( name.toUpperCase())){
                lat = col;
            }
            if( LONGITUDE.contains( name.toUpperCase())){
                lon = col;
            }
        }
    }
    
    public String[] getHeader() {
        return header;
    }
    public int getHeader( String name ){
        if( header == null ) return -1;
        for(int col=0; col<header.length; col++){
            if( header[col] == null ) continue;
            if( header[col].equalsIgnoreCase(name)){
                return col;
            }
        }
        return -1;
    }
    
    /** Calculate the size of the file */
    public synchronized int getSize(){
        if( size == -1 ){
            size = 0;
            try {
                CSVReader reader = reader();
                String row[] = reader.readNext(); // skip headers!
                while ((row = reader.readNext()) != null) {
                    size++;
                }
            }
            catch (IOException eek){
                eek.printStackTrace();
            }
        }
        return size;
    }
    public String toString() {
        return file.toString();
    }  
    
    /**
     * Utility method that will return a Point for the current reader.
     * <p>
     * Depends on the {@link #LON} and {@link #LAT} being configured
     * from header prior to use.
     * 
     * @param row As provided by {@link CSVReader#readNext()}
     * @return Point
     */
    public Point getPoint( String row[] ) throws IOException  {
        if( row == null ) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        if( lat != -1 && lon != -1 ){
            double x = Double.valueOf( row[lon]);
            double y = Double.valueOf( row[lat]);
            Coordinate coordinate = new Coordinate(x,y);
            return geometryFactory.createPoint( coordinate );
        }
        else {
            return null; // point not available
        }
    }
    
}
