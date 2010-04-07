package net.refractions.udig.ui.action;

import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Description of NewObjectAction.
 * <p>
 * Information about NewObjectAction is processed from the extension point IConfigurationElement data.
 * <p>
 * You can consider this a really early version of "Action" back before Eclipse 3.3 made
 * this sort of thing easy.
 * 
 * @author jones
 * @since 0.6.0
 */
public class NewObjectDelegate {
    /** NewItem id field */
    public final String id;
    /** NewItem text field */
    public final String text;
    /** NewItem icon field */
    public final ImageDescriptor icon;
    /** NewItem element field */
    public final IConfigurationElement element;
    private IWorkbenchWindowActionDelegate delegate;
    private IWorkbenchWindow window;

    /**
     * Construct <code>UDIGActionBarAdvisor.NewContribution.NewItem</code>.
     * 
     * @param element The configuration element that holds the properties (from plugin.xml)
     * @param window The window this action will operate in.
     */
    public NewObjectDelegate( IConfigurationElement element, IWorkbenchWindow window ) {
        this.element = element;
        this.text = element.getAttribute("label"); //$NON-NLS-1$
        this.id = element.getAttribute("id"); //$NON-NLS-1$
        String iconPath = element.getAttribute("icon"); //$NON-NLS-1$
        if (iconPath != null) {
            this.icon = AbstractUIPlugin.imageDescriptorFromPlugin(element.getNamespaceIdentifier(),
                    iconPath);
        } else
            this.icon = null;
        this.window = window;
    }
    
    /**
     * Create the IWorkbenchWindowActionDelegate (if required) and call run.
     */
    public void runAction() {
        if (delegate == null) {
            try {
                delegate = (IWorkbenchWindowActionDelegate) element
                        .createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                UiPlugin.log(null, e);
            }
        }
        if (delegate != null) {
            delegate.init(window);
            delegate.selectionChanged(null, new StructuredSelection());
            delegate.run(null);
        }
    }
}
