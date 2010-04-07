package net.refractions.udig.catalog.internal.arcsde.ui;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.ServiceExtension2;
import net.refractions.udig.catalog.internal.arcsde.ArcServiceExtension;
import net.refractions.udig.catalog.ui.AbstractUDIGConnectionFactory;

public class ArcSDEConnectionFactory extends AbstractUDIGConnectionFactory {

    @Override
    protected Map<String, Serializable> doCreateConnectionParameters(Object context) {
        return null;
    }

    @Override
    protected URL doCreateConnectionURL(Object context) {
        return null;
    }

    @Override
    protected boolean doOtherChecks(Object context) {
        return false;
    }

    @Override
    protected ServiceExtension2 getServiceExtension() {
        return new ArcServiceExtension();
    }

}
