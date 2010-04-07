package net.refractions.udig.catalog.ui.workflow;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.IConnectionErrorHandler;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

public class ConnectionErrorHandlerProcessor implements ExtensionPointProcessor {

    IService s;
    Throwable t;
    ArrayList<IConnectionErrorHandler> handlers;

    public List<IConnectionErrorHandler> process( IService s, Throwable t ) {
        this.s = s;
        this.t = t;
        handlers = new ArrayList<IConnectionErrorHandler>();

        ExtensionPointUtil
                .process(CatalogUIPlugin.getDefault(), IConnectionErrorHandler.XPID, this);

        return handlers;
    }

    public void process( IExtension extension, IConfigurationElement element ) throws Exception {

        try {
            IConnectionErrorHandler handler = (IConnectionErrorHandler) element
                    .createExecutableExtension("class"); //$NON-NLS-1$
            if (handler.canHandle(s, t)) {
                handlers.add(handler);
            }
        } catch (Throwable t) {
            CatalogUIPlugin.log(t.getLocalizedMessage(), t);
        }
    }
}