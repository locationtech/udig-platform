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
package org.locationtech.udig.catalog.ui.workflow;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.core.Pair;

/**
 * State capturing selected IGeoResources.
 * <p>
 * The selected resources are those in the {@link #getResources()} map. The keys are the selected
 * resources and the values are the parents. State normally uses a ConnectionState but it isn't
 * required is {@link #setServices(List)} is used to set the services.
 *
 * @author Justin Deolive
 * @since 1.1.0
 */
public class ResourceSelectionState extends State {

    /** list of resources * */
    Map<IGeoResource, IService> resources;

    private Collection<IService> services;

    public Collection<IService> getServices() {
        if (services != null) {
            return services;
        }
        List<IService> list = new ArrayList<>();

        ResourceSearchState search = getWorkflow().getState(ResourceSearchState.class);
        if (search != null) {
            for (IResolve resolve : search.getSelected()) {
                if (resolve instanceof IGeoResource) {
                    try {
                        IGeoResource geoResource = (IGeoResource) resolve;
                        IService service = geoResource.service(new NullProgressMonitor());
                        list.add(service);
                    } catch (IOException t) {
                        CatalogUIPlugin.log("Unable to connect to service " + t, t); //$NON-NLS-1$
                    }
                }
                if (resolve instanceof IService) {
                    IService service = (IService) resolve;
                    list.add(service);
                }
            }
        }
        EndConnectionState state = getWorkflow().getState(EndConnectionState.class);
        if (state != null) {
            Collection<IService> imported = state.getServices();
            if (imported != null) {
                list.addAll(imported);
            }
        }
        return list;
    }

    public void setServices(Collection<IService> services) {
        this.services = services;
    }

    public void setResources(Map<IGeoResource, IService> resources) {
        this.resources = resources;
    }

    public Map<IGeoResource, IService> getResources() {
        return resources;
    }

    @Override
    public void init(IProgressMonitor monitor) throws IOException {
        super.init(monitor);
        // try to generate some default resources based on context
        // LinkedHashMap to keep the order
        resources = new LinkedHashMap<>();

        // DnD and Workflow Context
        Object context = getWorkflow().getContext();

        // use the context object to try and match a resource
        if (context != null) {
            URL url = ID.cast(context).toURL();
            if (url != null) {
                addResource(monitor, url);
            } else if (context instanceof Iterable) {
                Iterable iterable = (Iterable) context;
                for (Object object : iterable) {
                    url = ID.cast(object).toURL();
                    if (url != null)
                        addResource(monitor, url);
                }
            }
        }
        ResourceSearchState search = getWorkflow().getState(ResourceSearchState.class);
        if (search != null) {
            for (IResolve resolve : search.getSelected()) {
                if (resolve instanceof IGeoResource) {
                    IGeoResource geoResource = (IGeoResource) resolve;
                    IService service = geoResource.service(new NullProgressMonitor());
                    resources.put(geoResource, service);
                }
            }
        }

        Collection<IService> services = getServices();

        List<IService> toRemove = new ArrayList<>();
        if (services != null) {
            for (IService service : services) {
                List<? extends IGeoResource> members = service.resources(monitor);
                if (members != null && members.isEmpty()) {
                    toRemove.add(service);
                    continue;
                }
                addPreferredResources(service, members);
                selectResourcesForDND(service, members);
            }
        }
        if (!toRemove.isEmpty())
            services.removeAll(toRemove);
    }

    private void selectResourcesForDND(IService service, List<? extends IGeoResource> members) {
        // if there is only a single resource then for drag and drop we can assume that
        // the user just wants to show the data on the map and not be queried just to
        // select the one element
        if (members.size() == 1) {
            resources.put(members.get(0), service);
        }
    }

    private void addPreferredResources(IService service, List<? extends IGeoResource> members) {
        Collection<URL> selectedResources = getPreferredResources();
        for (IGeoResource geoResource : members) {
            for (URL url : selectedResources) {
                if (resources.size() < selectedResources.size()) {
                    if (URLUtils.urlEquals(url, geoResource.getIdentifier(), false)) {
                        resources.put(geoResource, service);
                        break;
                    }
                }
            }
        }
    }

    private Collection<URL> getPreferredResources() {
        EndConnectionState state = getWorkflow().getState(EndConnectionState.class);
        if (state == null) {
            return Collections.emptyList();
        }
        Collection<URL> selectedResources = state.getSelectedResources();
        return selectedResources;
    }

    private void addResource(IProgressMonitor monitor, URL url) throws IOException {
        IGeoResource match = match(getServices(), url, monitor);
        if (match != null) {
            resources.put(match, match.service(monitor));
        }
    }

    @Override
    public Pair<Boolean, State> dryRun() {
        boolean resourcesReady = resources != null && !resources.isEmpty();
        // this is a guess. If the previous state has some selected resources and
        // dryRun has passed on it (this is known because we wouldn't be here)
        // the it is likely this state will pass.
        boolean connectionStateProvidesSelection = !getPreferredResources().isEmpty();

        boolean done = resourcesReady || connectionStateProvidesSelection;
        return new Pair<>(done, null);
    }

    @Override
    public boolean run(IProgressMonitor monitor) throws IOException {
        // complete if all the resources have been "selected"

        if (resources == null || resources.isEmpty())
            return false;

        int count = 0;

        Set<IService> parents = new HashSet<>();

        for (Map.Entry<IGeoResource, IService> entry : resources.entrySet()) {
            parents.add(entry.getValue());
        }

        Collection<IService> services = getServices();
        if (services != null) {
            try {
                monitor.beginTask("", services.size() * 10); //$NON-NLS-1$
                for (IService service : services) {
                    SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
                    try {
                        URL identifier = service.getIdentifier();
                        monitor.setTaskName(MessageFormat
                                .format(Messages.ResourceSelectionState_taskName, new Object[] {
                                        identifier.getProtocol() + "://" + identifier.getPath() })); //$NON-NLS-1$
                        count += service.resources(subMonitor).size();
                    } finally {
                        subMonitor.done();
                    }
                }
            } finally {
                monitor.done();
            }
        }

        return !resources.isEmpty();
    }

    /**
     * Scan through provided services to look up the requested URL as a GeoResource.
     *
     * @param services
     * @param url
     * @param monitor
     * @return
     * @throws IOException
     */
    private IGeoResource match(Collection<IService> services, URL url, IProgressMonitor monitor)
            throws IOException {

        // if there is no ref then the url is not matching a GeoResource
        if (url.getRef() == null || url.getRef().isEmpty()) {
            return null;
        }

        for (IService service : services) {
            for (IGeoResource resource : service.resources(monitor)) {
                if (resource.getIdentifier() == null) {
                    continue;
                }
                if (url.equals(resource.getIdentifier())) {
                    return resource;
                }
            }
        }
        return null; // not found
    }

    @Override
    public String getName() {
        return Messages.ResourceSelectionState_stateName;
    }
}
