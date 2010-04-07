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
package net.refractions.udig.catalog.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.catalog.CatalogPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

/**
 * Only required for 1.1.x. Provides a way to take an arbitrary CRS and find its EPSG code
 * 
 * @author jesse
 * @since 1.1.0
 */
public class CRSUtil {

    /**
     * Looks up the AUTHORITY code for a crs or return -1 if it can't find it
     * 
     * @throws NoSuchAuthorityCodeException
     */
    public static CoordinateReferenceSystem findEPSGCode( CoordinateReferenceSystem crs,
            IProgressMonitor monitor ) {

        if( !extractAuthorityCodes(crs).isEmpty() ){
            return crs;
        }
        
        Set<String> codes = CRS.getSupportedCodes("EPSG"); //$NON-NLS-1$
        monitor.beginTask("Searching for EPSG Code", codes.size()); //$NON-NLS-1$
        for( String string : codes ) {
            if (monitor.isCanceled()) {
                return null;
            }
            CoordinateReferenceSystem loaded;
            try {
                loaded = CRS.decode(string);
                if (CRS.equalsIgnoreMetadata(loaded, crs)) {
                    return loaded;
                }
            } catch (NoSuchAuthorityCodeException e) {
                // continue to search
            } catch (FactoryException e) {
                // continue to search
            }
            monitor.worked(1);
        }
        return null;
    }
    
    public static Collection<String> extractAuthorityCodes( CoordinateReferenceSystem crs ) {
         Set<ReferenceIdentifier> identifiers = crs.getIdentifiers();

        Set<String> codes = new HashSet<String>();
        for( ReferenceIdentifier identifier : identifiers ) {
            InternationalString title = identifier.getAuthority().getTitle();
            if (title.toString().equalsIgnoreCase("EPSG")) { //$NON-NLS-1$
                codes.add(identifier.toString());
                break;
            }
            Collection< ? extends InternationalString> alternateTitles = identifier.getAuthority()
                    .getAlternateTitles();

            for( InternationalString alternateTitle : alternateTitles ) {
                if (alternateTitle.toString().equalsIgnoreCase("EPSG")) { //$NON-NLS-1$
                    codes.add(identifier.toString());
                    break;
                }
            }

        }
        return codes;
    }

    /**
     * This general purpose function simply makes a recommendation for using imperial units based on the
     * coordinate reference system. It is quite simple and defaults to false (ie. metric) for most cases.
     * At the time of writing it was used by the DistanceTool and the MapGraphic.Scalebar to decide on
     * metric/imperial if the user had chosen 'auto' in the uDIG preferences (or the scalebar style).
     * 
     * Currently the code simply gets the first axis, and looks at the string representation of it's unit.
     * If this is "ft" or starts with "foot" (as in "foot_survey_us"), it returns true, otherwise false.
     * We decided against a complete analysis of the units, because the structure is highly variable (multiply
     * nested set), and this code was much simpler, and therefore easy to read and debug. It worked well against
     * a number of test cases, particularly in the NAD83* range.
     *
     * @author craig
     * @since 1.2.0 (M3)
     * @param crs
     * @return true if there is a sign of 'ft' or 'foot_survey_us' in the CRS units.
     */
    public static boolean isCoordinateReferenceSystemImperial(org.opengis.referencing.crs.CoordinateReferenceSystem crs){
        try {
            String crs_axis_units = crs.getCoordinateSystem().getAxis(0).getUnit().toString().toLowerCase();
            if(crs_axis_units.equals("ft") || crs_axis_units.startsWith("foot")){ //$NON-NLS-1$ //$NON-NLS-2$
                return true;
            }else if(!crs_axis_units.equals("m") && !crs_axis_units.equals("°")){ //$NON-NLS-1$ //$NON-NLS-2$
                // TODO: We have this here temporarily to see if we have missed any other common cases
                CatalogPlugin.trace("Unknown CRS units: "+crs_axis_units, null); //$NON-NLS-1$
            }
        }catch(Exception e){
            // This is to catch unexpected errors, fall-back to 'metric' without bothering the user too much
            // TODO: We could consider externalizing this string, but it is one that will not be seen by normal users, so ...
            CatalogPlugin.trace("Failed to auto-detect scalebar units from CRS: "+e.toString(),null); //$NON-NLS-1$
        }
        return false;
    }

}
