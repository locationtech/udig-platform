/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.shp.tests;

import java.io.File;
import java.net.URL;

import net.refractions.udig.catalog.IDocument.Type;

import junit.framework.TestCase;

/**
 * Abstract test class for shape document tests.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public abstract class AbstractShpDocTest extends TestCase {

    protected File file;
    protected URL url;
    
    protected File file1;
    protected File file2;
    
    protected URL url1;
    protected URL url2;
    
    protected static final String DIRECTORY = "internal";
    protected static final String SHAPEFILE = "australia.shp";
    protected static final String FILE1 = "readme.txt";
    protected static final String FILE2 = "australia.png";
    protected static final String WEB1 = "http://en.wikipedia.org/wiki/Australia";
    protected static final String WEB2 = "http://en.wikipedia.org/wiki/History_of_Australia";
    
    protected static final String FILE_ATTR = "FILE";
    protected static final String FILE_ATTR_LBL = "File";
    protected static final Type FILE_ATTR_TYPE = Type.FILE;
    
    protected static final String LINK_ATTR_LBL = "Link";
    protected static final String LINK_ATTR = "LINK";
    protected static final Type LINK_ATTR_TYPE = Type.WEB;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        file = new File( new File(DIRECTORY), SHAPEFILE);
        url = file.toURI().toURL();
        
        file1 = new File( new File(DIRECTORY ), FILE1);
        file2 = new File( new File(DIRECTORY ), FILE2);
        
        url1 = new URL(WEB1);
        url2 = new URL(WEB2);
        
        setUpInternal();
        
    }
    
    protected void setUpInternal() {
        // Override in child class 
    }
    
}

