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

public class LessInterestingService implements ServiceExtension {

    public class LessInterestingServiceImpl extends IService
    {
        private URL id;

        public LessInterestingServiceImpl(URL id) {
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
        public List<? extends IGeoResource> resources(IProgressMonitor monitor)
                throws IOException {
            return null;
        }

        @Override
        protected IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
            return new IServiceInfo() {

                @Override
                public double getMetric() {
                    return 0.0;
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
            testURL = new URL(CatalogImplTest.SERVICE_COMPARISON_TEST_URL);
        }
        catch(Exception e) {}
        Serializable paramId = params.get("id"); //$NON-NLS-1$
        if( paramId == null || !paramId.equals(testURL))
        {
            return null;
        }
        
        return new LessInterestingServiceImpl((URL) params.get("id")); //$NON-NLS-1$
    }

    @Override
    public Map<String, Serializable> createParams(URL url) {
        Map<String, Serializable> params = new TreeMap<String, Serializable>();
        params.put("id",url); //$NON-NLS-1$
        return params;
    }

}
