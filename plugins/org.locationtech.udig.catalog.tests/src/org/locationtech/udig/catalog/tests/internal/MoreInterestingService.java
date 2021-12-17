/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2015, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.catalog.tests.internal;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.ServiceExtension;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.URLUtilsTest;

public class MoreInterestingService implements ServiceExtension {

    public class MoreInterestingServiceImpl extends IService {
        private URL id;

        public MoreInterestingServiceImpl(URL id) {
            this.id = id;
        }

        @Override
        public Throwable getMessage() {
            return null;
        }

        @Override
        public URL getIdentifier() {
            return id;
        }

        @Override
        public List<? extends IGeoResource> resources(IProgressMonitor monitor) throws IOException {
            return null;
        }

        @Override
        protected IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
            return new IServiceInfo() {

                @Override
                public double getMetric() {
                    return 1.0;
                }

            };
        }

        @Override
        public IServiceInfo getInfo(IProgressMonitor monitor) throws IOException {
            return createInfo(monitor);
        }

        @Override
        public Map<String, Serializable> getConnectionParams() {
            return null;
        }
    }

    @Override
    public IService createService(URL id, Map<String, Serializable> params) {
        URL testURL = null;
        try {
            testURL = new URL(URLUtilsTest.SERVICE_COMPARISON_TEST_URL);
        } catch (Exception e) {
        }
        Serializable paramId = params.get("id"); //$NON-NLS-1$
        if (paramId instanceof URL && URLUtils.urlEquals(testURL, (URL) paramId, false)) {
            return new MoreInterestingServiceImpl((URL) params.get("id")); //$NON-NLS-1$
        }
        return null;

    }

    @Override
    public Map<String, Serializable> createParams(URL url) {
        Map<String, Serializable> params = new TreeMap<>();
        params.put("id", url); //$NON-NLS-1$
        return params;
    }

}
