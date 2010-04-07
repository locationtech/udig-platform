package net.refractions.udig.ui.operations;

import org.eclipse.core.runtime.IConfigurationElement;

public interface FilterParser {

    public String getElementName();
    public OpFilter parse(IConfigurationElement element);
}
