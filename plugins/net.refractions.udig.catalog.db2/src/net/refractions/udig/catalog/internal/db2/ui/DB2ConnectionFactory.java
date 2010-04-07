package net.refractions.udig.catalog.internal.db2.ui;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.ServiceExtension2;
import net.refractions.udig.catalog.internal.db2.DB2ServiceExtension;
import net.refractions.udig.catalog.ui.AbstractUDIGConnectionFactory;

public class DB2ConnectionFactory extends AbstractUDIGConnectionFactory {

    @Override
    protected Map<String, Serializable> doCreateConnectionParameters( Object context ) {
        return null;
    }

    @Override
    protected URL doCreateConnectionURL( Object context ) {
        return null;
    }

    @Override
    protected boolean doOtherChecks( Object context ) {
        return false;
    }

    @Override
    protected ServiceExtension2 getServiceExtension() {
        return new DB2ServiceExtension();
    }
}
