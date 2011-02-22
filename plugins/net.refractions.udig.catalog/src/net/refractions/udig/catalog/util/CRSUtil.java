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

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.referencing.CRS;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.NoSuchAuthorityCodeException;
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
    @SuppressWarnings("unchecked")
    public static CoordinateReferenceSystem findEPSGCode( CoordinateReferenceSystem crs,
            IProgressMonitor monitor ) {

        Set<String> codes = CRS.getSupportedCodes("EPSG");
        monitor.beginTask("Searching for EPSG Code", codes.size());
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
            }
            monitor.worked(1);
        }
        return null;
    }

    public static Collection<String> extractAuthorityCodes( CoordinateReferenceSystem crs ) {
        Set<Identifier> identifiers = crs.getIdentifiers();

        Set<String> codes = new HashSet<String>();
        for( Identifier identifier : identifiers ) {
            InternationalString title = identifier.getAuthority().getTitle();
            if (title.toString().equalsIgnoreCase("EPSG")) { //$NON-NLS-1$
                codes.add(identifier.toString());
                break;
            }
            Collection<InternationalString> alternateTitles = identifier.getAuthority()
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

}
