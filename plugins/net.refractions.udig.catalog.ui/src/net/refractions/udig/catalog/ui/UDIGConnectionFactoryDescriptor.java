/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.catalog.ui;

import java.util.List;

import net.refractions.udig.catalog.ui.ConnectionFactoryManager.Descriptor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Descriptor for net.refractions.udig.catalog.ui.connectionFactory extensions.
 * <p>
 * The initial data source selection state/page allows the user to choose a wizard
 * to go forward with. Each available wizard is represented as one of these
 * UDIGConnectionFactoryDescriptor; with the list of wizardPages defined by the
 * extension point.
 * </p>
 * This is also used to support lazy loading of the UDIGConnectionFactory so we can
 * put it off until needed.
 * </p>
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 */
public class UDIGConnectionFactoryDescriptor {
    UDIGConnectionFactory factory;
    private List<Descriptor<UDIGConnectionPage>> wizardPages;
    private Descriptor<UDIGConnectionFactory> factoryDescriptor;

    public UDIGConnectionFactoryDescriptor( Descriptor<UDIGConnectionFactory> factoryDescriptor )
            throws CoreException {
        this.factoryDescriptor = factoryDescriptor;
        factory = factoryDescriptor.getConcreteInstance();
        factory.setDescriptor(this);
        wizardPages = ConnectionFactoryManager.instance().getPageDescriptor(factoryDescriptor);
    }

    /**
     * The connection factory being used to create an IService on the successful
     * completion of this wizard.
     *
     * @return connection factory used to create an IService
     */
    public UDIGConnectionFactory getConnectionFactory() {
        return factory;
    }

    /**
     * Return the wizard page for the provided index
     * <p>
     * The page will be initialized with the label, description and banner defined
     * for it in the extension point.
     * 
     * @param pageIndex of the page to return
     * @return the wizard page for the provided index
     */
    public UDIGConnectionPage createConnectionPage( int pageIndex ) throws CoreException {
        UDIGConnectionPage page = wizardPages.get(pageIndex).getConcreteInstance();

        page.setTitle(getLabel(pageIndex));
        page.setDescription(getDescription(pageIndex));
        page.setImageDescriptor(getDescriptionImage(pageIndex));

        return page;
    }

    /**
     * Name for the indicated wizard page
     *
     * @param pageIndex
     * @return name for the indicated wizard page
     */
    public String getLabel( int pageIndex ) {
        return wizardPages.get(pageIndex).getConfigurationElement().getAttribute("name"); //$NON-NLS-1$
    }

    /**
     * Description for the indicated wizard page
     *
     * @param pageIndex
     * @return description for the indicated wizard page
     */
    public String getDescription( int pageIndex ) {
        String desc = wizardPages.get(pageIndex).getConfigurationElement().getAttribute(
                "description"); //$NON-NLS-1$
        if (desc == null)
            return ""; //$NON-NLS-1$

        return desc.trim();
    }

    /**
     * Id for this connection factory; mostly used in reporting problems.
     *
     * @return id for this connection factory
     */
    public String getId() {
	    // check for backwards compatibility
		IConfigurationElement configurationElement = wizardPages.get(0).getConfigurationElement();
        String deprecatedId = configurationElement.getAttribute("id");
        String id = this.factoryDescriptor.getConfigurationElement().getAttribute("id");
         if( id!=null && id.trim().length()>0 ){
             return id;
         }
        return deprecatedId; //$NON-NLS-1$
	}
    
    /**
     * Icon for the indicated wizard page
     *
     * @param pageIndex
     * @return icon for the indicated wizard page
     */
    public ImageDescriptor getImage( int pageIndex ) {
        String ns = wizardPages.get(pageIndex).getConfigurationElement().getNamespaceIdentifier();
        String banner = wizardPages.get(pageIndex).getConfigurationElement().getAttribute("icon"); //$NON-NLS-1$

        if (banner == null)
            return null;

        return AbstractUIPlugin.imageDescriptorFromPlugin(ns, banner);
    }

    /**
     * The Banner for the indicated wizard page.
     *
     * @param pageIndex
     * @return Banner for the indicated wizard page
     */
    public ImageDescriptor getDescriptionImage( int pageIndex ) {
        String ns = wizardPages.get(pageIndex).getConfigurationElement().getNamespaceIdentifier();
        String banner = wizardPages.get(pageIndex).getConfigurationElement().getAttribute("banner"); //$NON-NLS-1$

        if (banner == null)
            return null;

        return AbstractUIPlugin.imageDescriptorFromPlugin(ns, banner);
    }
    
    /**
     * Retrieves the "type" attribute for the indicated wizard page.
     * <p>
     * Note: "type" does not seem to be defined in the extension point schema so
     * I am unsure if this method is in use.
     * @param pageIndex
     * @return Returns the type if availale, appears to always be null
     */
    public String getServiceType( int pageIndex ) {
        return wizardPages.get(pageIndex).getConfigurationElement().getAttribute("type"); //$NON-NLS-1$
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;

        if (obj instanceof UDIGConnectionFactoryDescriptor) {
            UDIGConnectionFactoryDescriptor descriptor = (UDIGConnectionFactoryDescriptor) obj;

            return getId() != null && getId().equals(descriptor.getId());
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (getId() == null)
            return "".hashCode(); //$NON-NLS-1$
        return getId().hashCode();
    }

    /**
     * Returns the number of wizard pages
     */
    public int getWizardPageCount() {
        return wizardPages.size();
    }
    
    @Override
    public String toString() {
        StringBuilder build = new StringBuilder( factory.getClass().getSimpleName() );
        build.append(" Info");
        if( !wizardPages.isEmpty() ){
            build.append(" '");
            build.append( getLabel(0) );
            build.append("'");
        }
        return build.toString();
    }
}