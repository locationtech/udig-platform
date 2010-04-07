package net.refractions.udig.catalog.wmsc.server;

public class Capability {

    private VendorSpecificCapabilities vs = null;

    public void setVendorCapabilities( VendorSpecificCapabilities vs ) {
        this.vs = vs;
    }

    public VendorSpecificCapabilities getVSCapabilities() {
        return this.vs;
    }
    
}
