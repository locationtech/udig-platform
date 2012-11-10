/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.catalog.ui.wizard;

import java.io.Serializable;
import java.util.Map;

import net.refractions.udig.catalog.ui.AbstractUDIGImportPage;

import org.eclipse.jface.wizard.IWizardPage;
import org.geotools.data.DataStoreFactorySpi;

/**
 * Extend this page to connect to your own DataStore.
 * 
 * @author dzwiers
 * @since 0.3
 */
public abstract class DataStoreWizardPage extends AbstractUDIGImportPage /* extends WizardPage */{
    protected boolean canFlipToNextPage = false;
    
    public DataStoreWizardPage( String name ) {
        super(name);
    }
    
    /**
     * Return your connection parameters here.
     * <p>
     * Connection parameters are used with getDataStoreFactorySpi
     * to "connect" to a DataStore.
     */
    public abstract Map<String, Serializable> getParams();
    
    /**
     * Return your factory here.
     * <p>
     * Connection parameters are used with getDataStoreFactorySpi
     * to "connect" to a DataStore.
     */
    protected abstract DataStoreFactorySpi getDataStoreFactorySpi();
    
    @Override
    public abstract boolean isPageComplete();
    
    @Override
    final public boolean canFlipToNextPage() {
        IWizardPage[] pages = getWizard().getPages();
        return isPageComplete() && !pages[pages.length - 1].equals(this);
        // return canFlipToNextPage;
    }
    final public void setCanFlipToNextPage( boolean canFlipToNextPage ) {
        this.canFlipToNextPage = canFlipToNextPage;
    }
}