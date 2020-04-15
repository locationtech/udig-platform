/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.context.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.context.ContextPlugin;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.ProgressManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.referencing.CRS;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

/**
 * Import Context document as a new Map.
 * 
 * @author Jody Garnett
 * @since 1.0.0
 */
public class ContextImportWizard extends Wizard implements IImportWizard {
    URLWizardPage page;

    static Namespace oc = Namespace.getNamespace("http://www.opengis.net/oc"); //$NON-NLS-1$
    static Namespace sld = Namespace.getNamespace("http://www.opengis.net/sld"); //$NON-NLS-1$
    static Namespace xlink = Namespace.getNamespace("http://www.w3.org/1999/xlink"); //$NON-NLS-1$

    public ContextImportWizard() {
        super();
        setNeedsProgressMonitor(true); // monitor used for processing
    }

    @Override
    public boolean performFinish() {
        final URL url = page.url;
        if (url == null)
            return false;

        IRunnableWithProgress process = new IRunnableWithProgress(){
            public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                    InterruptedException {
                try {
                    process(url, monitor);
                } catch (Exception erp) {
                    throw new InvocationTargetException(erp);
                }
            }
        };

        try {
            getContainer().run(false, true, process);
        } catch (InvocationTargetException e) {
            page.setErrorMessage(e.getTargetException().getLocalizedMessage());
            return false;
        } catch (InterruptedException e) {
            page.canFlipToNextPage(); // reset message
            return false;
        }
        return true;

    }
    /**
     * Process a Context Document, a new IMap will be created if successful.
     * 
     * @param context
     * @param monitor
     */
    public static void process( final URL url, IProgressMonitor monitor ) throws Exception {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        try {
            monitor.beginTask(Messages.ContextImportWizard_task_title, 100); 
            monitor.subTask(MessageFormat.format(Messages.ContextImportWizard_task_connecting, url));
            monitor.worked(5);
            InputStream input = null;
            try {
                input = url.openStream();
                monitor.worked(15);
                SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 80);
                try {
                    process(input, subMonitor);
                } finally {
                    subMonitor.done();
                }
            } catch (IOException e) {
                throw new InvocationTargetException(e);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        throw new InvocationTargetException(e);
                    }
                }
            }

        } finally {
            monitor.done();
        }
    }

    /**
     * Process the inputstream - use page.url if you need more.
     * <p>
     * If an exception is thrown then the "processing" is considered bad, and the exception will be
     * presented as an error.
     * </p>
     */
    static protected void process( InputStream input, IProgressMonitor monitor ) throws Exception {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        monitor.beginTask(Messages.ContextImportWizard_parsingxml, 100); 
        monitor.worked(5);
        BufferedInputStream stream = new BufferedInputStream(input);
        Document document = new SAXBuilder().build(stream);
        monitor.worked(55);
        process(document, new SubProgressMonitor(monitor, 40));
    }

    /**
     * Parse XML document using JDOM.
     * 
     * @param document
     * @param monitor
     */
    static public void process( Document document, IProgressMonitor monitor ) {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        Element root = document.getRootElement();
        Namespace oc = Namespace.getNamespace("http://www.opengis.net/oc"); //$NON-NLS-1$
        Namespace ows = Namespace.getNamespace("http://www.opengis.net/ows"); //$NON-NLS-1$
        Element general = root.getChild("General", oc); //$NON-NLS-1$
        Element resourceList = root.getChild("ResourceList", oc); //$NON-NLS-1$

        String title = general.getChildText("Title", oc); //$NON-NLS-1$
        monitor
                .beginTask(MessageFormat.format(
                        Messages.ContextImportWizard_importing, title), resourceList.getContentSize() + 2);

        // TODO: This is internal, why so hard? Commands? Thought I could make
        // a Map and then use a command to hand it over to uDig and rendering.
        // (even system probly weighing in)

        Map map = createMap(title);
        map.setAbstract(general.getChildText("Abstract", oc)); //$NON-NLS-1$
        processBoundingBox(general.getChild("BoundingBox", ows), map); //$NON-NLS-1$
        monitor.worked(1);

        for( Iterator iter = resourceList.getContent().iterator(); iter.hasNext(); ) {
            Object child = iter.next();
            if (!(child instanceof Element))
                continue;

            Element resource = (Element) child;

            String name = resource.getAttributeValue("Name"); //$NON-NLS-1$
            monitor.subTask(name);
            if ("Layer".equals(resource.getName())) { //$NON-NLS-1$
                processLayer(resource, map);
            } else if ("SimpleFeatureType".equals(resource.getName())) { //$NON-NLS-1$
                processFeatureType(resource, map);
            } else {
                System.out.println("Skip " + name); //$NON-NLS-1$
            }
            monitor.worked(1);
        }
        monitor.subTask(MessageFormat.format(Messages.ContextImportWizard_adding, title));
        monitor.worked(1);
        monitor.done();
        ApplicationGIS.openMap(map);
    }

    /**
     * @param general
     * @param map
     */
    static private void processBoundingBox( Element boundingBox, Map map ) {
        Namespace ows = Namespace.getNamespace("http://www.opengis.net/ows"); //$NON-NLS-1$

        CoordinateReferenceSystem crs = null;
        Envelope env = null;
        if (boundingBox == null)
            return;

        try {
            crs = CRS.decode(boundingBox.getAttributeValue("crs")); //$NON-NLS-1$
        } catch (Exception noCRS) {
            System.out.println("No CRS " + boundingBox.getAttributeValue("crs")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        try {
            String lowerCorner[] = boundingBox.getChildTextTrim("LowerCorner", ows).split(" "); //$NON-NLS-1$ //$NON-NLS-2$
            String upperCorner[] = boundingBox.getChildTextTrim("UpperCorner", ows).split(" "); //$NON-NLS-1$ //$NON-NLS-2$

            env = new Envelope(Double.parseDouble(lowerCorner[0]), Double
                    .parseDouble(upperCorner[0]), Double.parseDouble(lowerCorner[1]), Double
                    .parseDouble(upperCorner[1]));
        } catch (Exception noBBox) {
            System.out.println("No BoundingBox" + boundingBox); //$NON-NLS-1$
        }
        if (env != null && crs != null) {
            ViewportModel viewportModel = map.getViewportModelInternal();
            viewportModel.setBounds(env);
            viewportModel.setCRS(crs);
        }
    }

    /**
     * Find the service, in catalog (lazy) or acquire (aggressive).
     * <p>
     * I would really rather the renderers dragged things into the catalog as the need them...
     * </p>
     * 
     * @param url URL used to start searching in the catalog
     * @param type The returned service must resolve to this type
     * @return URL of resulting service
     */
    static final private URL service( URL url, Class<?> type ) {
        ICatalog local = CatalogPlugin.getDefault().getLocalCatalog();
        List<IResolve> services = local.find(url, ProgressManager.instance().get());
        for( IResolve service : services ) {
            if (service.canResolve(type)) {
                return service.getIdentifier();
            }
        }
        IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
        List<IService> candidates = serviceFactory.createService(url);
        try {
            for( Iterator<IService> i=candidates.iterator(); i.hasNext();){
                IService service = i.next();
                if (service.canResolve(type)) {
                    IService registered = local.add(service);
                    i.remove(); // don't clean this one up
                    return registered.getIdentifier();
                }
            }
        }
        finally {
            serviceFactory.dispose( candidates, null );
        }
        // It is okay if we don't find anything, the user interface
        // will let the user know - tempting to force a broken WMS
        // handle onto the catalog though
        return url;
    }
    /**
     * @param resource
     * @param map
     */
    static private void processFeatureType( Element resource, Map map ) {
        Layer layer;
        String hidden = resource.getAttributeValue("hidden"); //$NON-NLS-1$
        String name = resource.getChildTextTrim("Name", oc); //$NON-NLS-1$
        String title = resource.getChildTextTrim("Title", oc); //$NON-NLS-1$
        String min = resource.getChildTextTrim("MinScaleDenominator", sld); //$NON-NLS-1$
        String max = resource.getChildTextTrim("MaxScaleDenominator", sld); //$NON-NLS-1$
        String srs = resource.getChildTextTrim("SRS", oc); //$NON-NLS-1$

        Element server = resource.getChild("Server", oc); //$NON-NLS-1$
        String service = server.getAttributeValue("service"); // should be OGC:WMS //$NON-NLS-1$
        String servertitle = server.getAttributeValue("title"); //$NON-NLS-1$
        String serverversion = server.getAttributeValue("version"); // eg 1.1.1 //$NON-NLS-1$

        Element onlineResouce = server.getChild("OnlineResource", oc); //$NON-NLS-1$
        String method = onlineResouce.getAttributeValue("method"); // url //$NON-NLS-1$
        String href = onlineResouce.getAttributeValue("href", xlink); // url //$NON-NLS-1$
        try {
            href = URLDecoder.decode(href, "US-ASCII"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }
        String type = onlineResouce.getAttributeValue("type", xlink); // eg simple //$NON-NLS-1$
        try {
            URL url = new URL(href
                    + "?SERVICE=WFS&VERSION=" + serverversion + "&REQUEST=GetCapabilities"); //$NON-NLS-1$ //$NON-NLS-2$
            IGeoResource georesource = findResource(url, name);
            if (georesource == null) {
                layer = ProjectFactory.eINSTANCE.createLayer();
                layer.setID(new URL(url + "#" + name)); //$NON-NLS-1$
            } else
                layer = map.getLayerFactory().createLayer(georesource);
        } catch (IOException e) {
            System.out.println("Skip " + name + " due to " + e); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }

        if (hidden != null) {
            layer.setVisible(!"0".equals(hidden)); //$NON-NLS-1$
        }
        layer.setName(title != null ? title : name);
        if (min != null)
            layer.setMinScaleDenominator(Double.parseDouble(min));
        if (max != null)
            layer.setMaxScaleDenominator(Double.parseDouble(max));

        try {
            layer.setCRS(CRS.decode(srs));
        } catch (Throwable ignore) {
            System.out.println(name + " srs unavailable:" + srs); //$NON-NLS-1$
        }
        map.getLayersInternal().add(layer);
    }

    private static IGeoResource findResource( URL url, String name ) throws IOException {
        URL appended = new URL(url.toString() + "#" + name); //$NON-NLS-1$
        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
        List<IResolve> results = localCatalog.find(appended, null);
        if (results.isEmpty()) {
            List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
            for( IService service : services ) {
                localCatalog.add(service);
                for( IGeoResource resource : service.resources(null) ) {
                    if( resource.getIdentifier().getRef().equals(name) ){
                        return resource;
                    }
                }
            }
        } else
            return (IGeoResource) results.get(0);
        return null;
    }

    /**
     * @param resource
     * @param map
     */
    static private void processLayer( Element resource, Map map ) {
        Layer layer = ProjectFactory.eINSTANCE.createLayer();
        String hidden = resource.getAttributeValue("hidden"); //$NON-NLS-1$
        if (hidden != null) {
            layer.setVisible(!"0".equals(hidden)); //$NON-NLS-1$
        }
        String queryable = resource.getAttributeValue("queryable"); //$NON-NLS-1$
        if (queryable != null) {
            layer.setInteraction(Interaction.SELECT, !"0".equals(queryable)); //$NON-NLS-1$ //$NON-NLS-2$
        }

        Namespace oc = Namespace.getNamespace("http://www.opengis.net/oc"); //$NON-NLS-1$
        Namespace sld = Namespace.getNamespace("http://www.opengis.net/sld"); //$NON-NLS-1$
        Namespace xlink = Namespace.getNamespace("http://www.w3.org/1999/xlink"); //$NON-NLS-1$

        String name = resource.getChildTextTrim("Name", oc); //$NON-NLS-1$
        {
            String title = resource.getChildTextTrim("Title", oc); //$NON-NLS-1$

            layer.setName(title != null ? title : name);
        }
        {
            Element server = resource.getChild("Server", oc); //$NON-NLS-1$

            String service = server.getAttributeValue("service"); // should be OGC:WMS //$NON-NLS-1$
                                                                    // //$NON-NLS-1$
            String title = server.getAttributeValue("title"); //$NON-NLS-1$
            String version = server.getAttributeValue("version"); // eg 1.1.1 //$NON-NLS-1$

            Element onlineResouce = server.getChild("OnlineResource", oc); //$NON-NLS-1$
            String type = onlineResouce.getAttributeValue("type", xlink); // eg simple //$NON-NLS-1$
                                                                            // //$NON-NLS-1$
            String href = onlineResouce.getAttributeValue("href", xlink); // url //$NON-NLS-1$
            try {
                href = URLDecoder.decode(href, "US-ASCII"); //$NON-NLS-1$
            } catch (UnsupportedEncodingException ex) {
                System.out.println(ex.toString());
                ex.printStackTrace();
            }

            try {
                URL url = new URL(href
                        + "?SERVICE=WMS&VERSION=" + version + "&REQUEST=GetCapabilities"); //$NON-NLS-1$ //$NON-NLS-2$
                url = service(url, WebMapServer.class);

                layer.setID(new URL(url + "#" + name)); //$NON-NLS-1$
            } catch (MalformedURLException e) {
                System.out.println("Skip " + name + " due to " + e); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }
        }
        String min = resource.getChildTextTrim("MinScaleDenominator", sld); //$NON-NLS-1$
        String max = resource.getChildTextTrim("MaxScaleDenominator", sld); //$NON-NLS-1$

        if (min != null)
            layer.setMinScaleDenominator(Double.parseDouble(min));
        if (max != null)
            layer.setMaxScaleDenominator(Double.parseDouble(max));

        String srs = resource.getChildTextTrim("SRS", oc); //$NON-NLS-1$
        try {
            layer.setCRS(CRS.decode(srs));
        } catch (Throwable ignore) {
            System.out.println(name + " srs unavailable:" + srs); //$NON-NLS-1$
        }

        map.getLayersInternal().add(layer);
    }

    /**
     * TODO: Move to a formal MapBuilder utility.
     * <p>
     * Intended workflow, MapBuilder looks a lot like a Map, calling create will issue the command
     * to create the map based on the collected parameters. Builders are paramertized by a factory -
     * in this case a ProjectFactory.
     * </p>
     * 
     * @param title Title of Map to be created
     * @return new IMap
     */
    static private Map createMap( String title ) {
        Project project = ProjectPlugin.getPlugin().getProjectRegistry().getCurrentProject();
        if (project == null) {
            project = ProjectPlugin.getPlugin().getProjectRegistry().getDefaultProject();
        }
        return ProjectFactory.eINSTANCE.createMap(project, title, new ArrayList<Layer>());
    }

    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        // grab dialog settings for context ...
        // selction can be used to figure out project ...
    }
    @Override
    public void addPages() {
        super.addPages();
        page = new URLWizardPage(
                Messages.ContextImportWizard_wizard_name, Messages.ContextImportWizard_wizard_title, ContextPlugin.getImageDescriptor("icons/wizban/import_owscontext_wiz_gif")){    //$NON-NLS-1$

            /** Override to check extention */
            protected boolean fileCheck( File file ) {
                if (!super.fileCheck(file))
                    return false;

                /*
                 * Commented out because I hate it when programs force a file to have an extension.
                 * What if it ends in .xml.bak? It is still an XML file :P -rgould
                 */
                // if( file.getName().toUpperCase().endsWith(".XML") ){ //$NON-NLS-1$
                // return true;
                // }
                // setErrorMessage("File required to have extention XML" );
                // return false;
                return true;
            }
        };
        addPage(page);
    }

}
