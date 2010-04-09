/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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