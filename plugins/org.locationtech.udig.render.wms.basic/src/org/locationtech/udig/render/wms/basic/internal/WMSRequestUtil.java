/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2021, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.render.wms.basic.internal;

import java.util.List;
import java.util.stream.Collectors;

import org.geotools.ows.wms.WebMapServer;

/**
 * @author fgdrf
 *
 */
public class WMSRequestUtil {

    public static String getRequestImageFormatBestMatch(WebMapServer webMapServer,
            String[] preferredImageFormats) {
        List<String> formats = webMapServer.getCapabilities().getRequest().getGetMap().getFormats();
        List<String> lowerCaseFormats = formats.stream().map(String::toLowerCase).collect(Collectors.toList());

        if (preferredImageFormats == null || preferredImageFormats.length == 0) {
            return formats.get(0);
        }

        for (String format : preferredImageFormats) {
            if (lowerCaseFormats.contains(format.toLowerCase())) {
                return format.toLowerCase();
            }
        }

        return formats.get(0);
    }
}
