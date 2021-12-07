/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.examples;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.locationtech.udig.core.internal.CorePlugin;
import org.locationtech.udig.core.internal.ExtensionPointProcessor;
import org.locationtech.udig.core.internal.ExtensionPointUtil;
import org.locationtech.udig.project.ui.tool.ModalTool;

/**
 * The eclipse Platform is where all the magic happens.
 * <p>
 *
 * @author Jody Garnett
 * @since 1.2.0
 */
public class PlatformExample {
    void processingExtensionPointByHand() {
        String xpid = "org.locationtech.udig.project.ui.tool"; //$NON-NLS-1$
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry.getExtensionPoint(xpid);
        if (extensionPoint == null) {
            throw new NullPointerException("Could not find extensionPoint:" + xpid); //$NON-NLS-1$
        }
        for (IExtension extension : extensionPoint.getExtensions()) {
            for (IConfigurationElement element : extension.getConfigurationElements()) {
                String name = element.getName();
                System.out.println(name);
                if ("modalTool".equals(name)) { //$NON-NLS-1$
                    try {
                        ModalTool tool = (ModalTool) element.createExecutableExtension("class"); //$NON-NLS-1$
                        System.out.println(tool);
                    } catch (CoreException e) {
                        // Perhaps an error in the constructor?
                        String message = "Could not create Modal tool " //$NON-NLS-1$
                                + element.getAttribute("class"); //$NON-NLS-1$
                        Status status = new Status(IStatus.ERROR,
                                extension.getContributor().getName(), message, e);
                        Activator.getDefault().getLog().log(status);
                    }
                }
            }
        }
    }

    void processingExtensionPointUsingUtilityClassOld() {
        String xpid = "org.locationtech.udig.project.ui.tool"; //$NON-NLS-1$
        ExtensionPointUtil.process(CorePlugin.getDefault(), xpid, new ExtensionPointProcessor() {
            @Override
            public void process(IExtension extension, IConfigurationElement element)
                    throws Exception {
                String name = element.getName();
                System.out.println(name);
                if ("modalTool".equals(name)) { //$NON-NLS-1$
                    ModalTool tool = (ModalTool) element.createExecutableExtension("class"); //$NON-NLS-1$
                    System.out.println(tool);
                }
            }
        });
    }

    void processingExtensionPointUsingUtilityClass() {
        String xpid = "org.locationtech.udig.project.ui.tool"; //$NON-NLS-1$
        ExtensionPointUtil.process(Activator.getDefault(), xpid, new ExtensionPointProcessor() {
            @Override
            public void process(IExtension extension, IConfigurationElement element)
                    throws Exception {
                String name = element.getName();
                System.out.println(name);
                if ("modalTool".equals(name)) { //$NON-NLS-1$
                    ModalTool tool = (ModalTool) element.createExecutableExtension("class"); //$NON-NLS-1$
                    System.out.println(tool);
                }
            }
        });
    }
}
