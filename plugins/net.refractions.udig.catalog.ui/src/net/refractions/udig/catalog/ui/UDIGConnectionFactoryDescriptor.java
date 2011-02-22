package net.refractions.udig.catalog.ui;

import java.util.List;

import net.refractions.udig.catalog.ui.ConnectionFactoryManager.Descriptor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Descriptor for net.refractions.udig.catalog.ui.connectionFactory extensions.
 *
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

    public UDIGConnectionFactory getConnectionFactory() {
        return factory;
    }

    /**
     * Return the wizard page for the provided index
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

    public String getLabel( int pageIndex ) {
        return wizardPages.get(pageIndex).getConfigurationElement().getAttribute("name"); //$NON-NLS-1$
    }

    public String getDescription( int pageIndex ) {
        String desc = wizardPages.get(pageIndex).getConfigurationElement().getAttribute(
                "description"); //$NON-NLS-1$
        if (desc == null)
            return ""; //$NON-NLS-1$

        return desc.trim();
    }

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
    public ImageDescriptor getImage( int pageIndex ) {
        String ns = wizardPages.get(pageIndex).getConfigurationElement().getNamespaceIdentifier();
        String banner = wizardPages.get(pageIndex).getConfigurationElement().getAttribute("icon"); //$NON-NLS-1$

        if (banner == null)
            return null;

        return AbstractUIPlugin.imageDescriptorFromPlugin(ns, banner);
    }

    public ImageDescriptor getDescriptionImage( int pageIndex ) {
        String ns = wizardPages.get(pageIndex).getConfigurationElement().getNamespaceIdentifier();
        String banner = wizardPages.get(pageIndex).getConfigurationElement().getAttribute("banner"); //$NON-NLS-1$

        if (banner == null)
            return null;

        return AbstractUIPlugin.imageDescriptorFromPlugin(ns, banner);
    }

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
}
