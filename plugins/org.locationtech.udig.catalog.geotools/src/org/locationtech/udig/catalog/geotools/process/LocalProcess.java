/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.geotools.process;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IProcess;
import net.refractions.udig.catalog.IProcessInfo;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.type.Name;
import org.opengis.util.InternationalString;

/**
 * LocalProcess wrapping up a GeoTools ProcessFactory and name.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.2.0
 */
public class LocalProcess extends IProcess {
    /** Internal info class */
    public class LocalProcessInfo extends IProcessInfo {
        LocalProcessInfo() {
            this.processName = LocalProcess.this.name;
            this.processFactory = folder.getFactory();

            this.processName = LocalProcess.this.name;
            try {
                this.schema = new URI(processName.getNamespaceURI());
            } catch (URISyntaxException e) {
            }
            InternationalString processDescription = processFactory.getDescription(processName);
            InternationalString processTitle = processFactory.getTitle(processName);

            if (processDescription != null) {
                this.description = processDescription.toString();
            }
            if (processTitle != null) {
                this.title = processTitle.toString();
            }
            String version = processFactory.getVersion(processName);
            if (version != null) {
                this.keywords = new String[]{version};
            }
            icon = CatalogUIPlugin.getDefault()
                    .getImageDescriptor(ISharedImages.GRID_OBJ);
        }
    }

    /**
     * Folder defining the factory being used.
     */
    LocalProcessFolder folder;

    /** Name of process being represented */
    Name name;

    private IProcessInfo info;

    private ID id;

    LocalProcess( LocalProcessFolder folder, Name name ) {
        this.folder = folder;
        this.name = name;
        this.id = new ID(folder.getID(), name.getLocalPart());
    }

    public Status getStatus() {
        return folder.getStatus();
    }

    public Throwable getMessage() {
        return folder.getMessage();
    }

    public String getTitle() {
        if (info != null) {
            return info.getTitle();
        }
        return name.getLocalPart();
    }

    @Override
    public synchronized IProcessInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null) {
            info = new LocalProcessInfo();
        }
        return info;
    }

    @Override
    public LocalProcessService service( IProgressMonitor monitor ) throws IOException {
        return folder.parent(monitor);
    }

    @Override
    public URL getIdentifier() {
        return id.toURL();
    }

    @Override
    public ID getID() {
        return id;
    }
}
