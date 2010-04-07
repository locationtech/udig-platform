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
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Creates the src directory.  This requires determining the version number of the libs plugin.  
 * The dir is written to the system property called {@value #LIBS_SRC_DIR}
 * 
 * @author jesse
 * @since 1.1.0
 */
public class Mkdir {

    
    private static final String LIBS_SRC_DIR = "libs_src_dir"; //$NON-NLS-1$

    public static void main( String[] args ) throws Exception {
        String antScriptDir = args[0];
        final File file = new File(antScriptDir+"/../net.refractions.udig.libs/META-INF/MANIFEST.MF"); //$NON-NLS-1$
        
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        try{
        String line = reader.readLine();
        
        while( line!=null ){
            if( foundVersionKey(line, antScriptDir) ){
                return;
            }
            line = reader.readLine();
        }
        }finally{
            reader.close();
        }

    }

    private static boolean foundVersionKey(String line, String antScriptDir) throws IOException {
        final String versionKey = "Bundle-Version:".toLowerCase(); //$NON-NLS-1$
        if( line.toLowerCase().startsWith(versionKey)){
            String version;
            if( line.contains(";") ){ //$NON-NLS-1$
                version = line.substring(versionKey.length(), versionKey.indexOf(';'));
            }else {
                version = line.substring(versionKey.length());
            }
            version = version.trim();
            String libSrcDir = "src/net.refractions.udig.libs_"+version+"/lib"; //$NON-NLS-1$ //$NON-NLS-2$
            new File(libSrcDir).mkdirs();
            FileWriter writer = new FileWriter(antScriptDir+"/dest.properties"); //$NON-NLS-1$
            try{
                writer.write(LIBS_SRC_DIR+"=${basedir}/"+libSrcDir); //$NON-NLS-1$
            }finally{
                writer.close();
            }
            return true;
        }
        return false;
    }

}
