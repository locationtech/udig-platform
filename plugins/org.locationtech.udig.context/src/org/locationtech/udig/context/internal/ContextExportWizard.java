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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.context.ContextPlugin;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.geotools.data.ResourceInfo;
import org.geotools.data.ServiceInfo;
import org.geotools.ows.wms.Layer;
import org.geotools.ows.wms.StyleImpl;
import org.geotools.ows.wms.WMSCapabilities;
import org.geotools.data.wfs.WFSDataStore;

import org.geotools.ows.wms.WebMapServer;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

public class ContextExportWizard extends Wizard implements IExportWizard {

    IMap selection;
    MapExportPage page;
    
    public ContextExportWizard() {
        super();
        setNeedsProgressMonitor( true ); // monitor used for processing
    }

    @Override
    public boolean performFinish() {
        final File file = page.file;
        if( file == null ) return false;
        
        if( file.exists() ){
            if( page.overwriteExistingFileCheckbox.getSelection() ){
                file.delete();
            }
            else {
                page.setErrorMessage(Messages.ContextExportWizard_prompt_error_fileExists); 
                return false;
            }
        }
        try {
            export( page.selectedMap, file );
        }
        catch( IOException io ){
            page.setErrorMessage( io.getLocalizedMessage() );
            return false;
        }  
        page.setMessage(Messages.ContextExportWizard_prompt_done); 
        return true;
    }

    public static void export( IMap map, File file ) throws IOException{
        file.createNewFile();
        BufferedWriter out = new BufferedWriter( new FileWriter( file ) );
        writeContext( map, out );
        out.close();   
    }
    static void writeContext( IMap map, BufferedWriter out ) throws IOException {
        writeHeader( map, out );
        writeGeneral( map, out );
        append( 2, out,   "<ResourceList>");         //$NON-NLS-1$
        for( ILayer layer : map.getMapLayers() ){
            try {
                if( layer.isType( Layer.class ) ){
                    writeLayer( layer, out );
                }
                else if( layer.isType( WFSDataStore.class ) ){                    
                    writeFeatureType( layer, out );
                }
                else {
                    // n/a 
                }
                    
            }
            catch( IOException io){
                // skip - unable to figure out details ...
            }
        }
        append( 2, out,   "</ResourceList>"); //$NON-NLS-1$
        append( 0, out, "</OWSContext>"); //$NON-NLS-1$
    }
 
    private static void append( int indent, Writer out, String txt ) throws IOException{
        out.append( "                                       ".substring(0,indent)); //$NON-NLS-1$
        out.append( txt );
        out.append("\n");//$NON-NLS-1$
    }   

    private static void writeFeatureType( ILayer layer, BufferedWriter out ) throws IOException {
        WFSDataStore wfs = (WFSDataStore) layer.getResource( WFSDataStore.class, null );
        SimpleFeatureType type = layer.getSchema();
        String typeName = type.getName().getLocalPart();
        
        int hidden = layer.isVisible() ? 1 : 0;
        ServiceInfo serviceInfo = wfs.getInfo();
		String title = serviceInfo.getTitle();
        String version = "1.0.0"; // only known entry at this time //$NON-NLS-1$
        URI source = serviceInfo.getSource();
        String get = source == null? "" : source.toString();
        
        ResourceInfo resourceInfo = wfs.getFeatureSource(typeName).getInfo();
        append( 4, out, "<SimpleFeatureType hidden=\""+ hidden +"\">" ); //$NON-NLS-1$ //$NON-NLS-2$
        append( 6, out,   "<Server service=\"OGC:WFS\" title=\""+title+"\" version=\""+version+"\">" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        append( 8, out,     "<OnlineResource method=\"GET\" xlink:href=\""+get+"\" xlink:type=\"simple\"/>" ); //$NON-NLS-1$ //$NON-NLS-2$
        append( 6, out,   "</Server>" ); //$NON-NLS-1$
        append( 6, out,   "<Name>"+typeName+"</Name>"); //$NON-NLS-1$ //$NON-NLS-2$
        append( 6, out,   "<Title>"+resourceInfo.getTitle()+"</Title>");     //$NON-NLS-1$ //$NON-NLS-2$
		//if( !Double.isNaN( layer.getMinScaleDenominator() ))
//        append( 6, out,   "<sld:MinScaleDenominator>"+layer.getMinScaleDenominator()+"</sld:MinScaleDenominator>");
//if( !Double.isNaN( layer.getMaxScaleDenominator() ))
//        append( 6, out,   "<sld:MinScaleDenominator>"+layer.getMaxScaleDenominator()+"</sld:MinScaleDenominator>");
        String SRS="EPSG:4326";
        CoordinateReferenceSystem crs = resourceInfo.getCRS();
        //TODO: anyone knows how to get the urn for the crs object?
		append( 6, out,   "<SRS>"+ SRS +"</SRS>"); //$NON-NLS-1$ //$NON-NLS-2$
        append( 4, out, "</SimpleFeatureType>" ); //$NON-NLS-1$
    }

    private static void writeLayer( ILayer layer, BufferedWriter out ) throws IOException {
        Layer wmsLayer = layer.getResource( Layer.class, null );
        WebMapServer wms = layer.getResource( WebMapServer.class, null );
        WMSCapabilities caps = wms.getCapabilities();
        String version = caps.getVersion();
        
        String title = wms.getCapabilities().getService().getTitle();
        int hidden = layer.isVisible() ? 1 : 0;
        int info = layer.getInteraction(Interaction.INFO) ? 1 : 0;
        String get = caps.getRequest().getGetCapabilities().getGet().toExternalForm();
System.out.println(get); if (get.endsWith("&")) get = get.substring(0,get.length()-1); //$NON-NLS-1$
        append( 4, out, "<Layer hidden=\""+ hidden +"\" queryable=\""+info+"\">" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        append( 6, out,   "<Server service=\"OGC:WMS\" title=\""+title+"\" version=\""+version+"\">" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        append( 8, out,     "<OnlineResource method=\"GET\" xlink:href=\""+get+"\" xlink:type=\"simple\"/>" ); //$NON-NLS-1$ //$NON-NLS-2$
        append( 6, out,   "</Server>" ); //$NON-NLS-1$
        append( 6, out,   "<Name>"+wmsLayer.getName()+"</Name>"); //$NON-NLS-1$ //$NON-NLS-2$
        append( 6, out,   "<Title>"+wmsLayer.getTitle()+"</Title>");                //$NON-NLS-1$ //$NON-NLS-2$
        if( !Double.isNaN( wmsLayer.getScaleDenominatorMin() ))
            append( 6, out,   "<sld:MinScaleDenominator>"+wmsLayer.getScaleDenominatorMin()+"</sld:MinScaleDenominator>");         //$NON-NLS-1$ //$NON-NLS-2$
        if( !Double.isNaN( wmsLayer.getScaleDenominatorMax() ))
            append( 6, out,   "<sld:MaxScaleDenominator>"+wmsLayer.getScaleDenominatorMax()+"</sld:MaxScaleDenominator>");         //$NON-NLS-1$ //$NON-NLS-2$
        for( String srs : (Set<String>) wmsLayer.getSrs() ){
            append( 6, out,   "<SRS>"+srs+"</SRS>"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        append( 6, out,   "<FormatList>"); //$NON-NLS-1$
        
boolean first = true; // TODO: look up preferences?
for( String format : caps.getRequest().getGetMap().getFormats() ){
    if( first ){
        append( 8, out,     "<Format current=\"1\">"+format+"</Format>");                 //$NON-NLS-1$ //$NON-NLS-2$
        first = false;
    }
        append( 8, out,   "<Format>"+format+"</Format>");     //$NON-NLS-1$ //$NON-NLS-2$
}
        append( 6, out,   "</FormatList>"); //$NON-NLS-1$
        
        first = true; // TODO: look up on styleblackboard?
        append( 6, out,   "<StyleList>"); //$NON-NLS-1$
        Object styles=wmsLayer.getStyles();
        List list;
        if( styles instanceof String )
            list=Collections.singletonList(styles);
        else if( styles instanceof List )
            list=(List)styles;
        else
            list = Collections.emptyList();
        for( Iterator<Object> iter = list.iterator(); iter.hasNext(); ) {
            Object next=iter.next();
            if( next instanceof String ){
                String style=(String)next;
                first=writeStyle(style, style, first, out);
            }else if( next instanceof StyleImpl ){
                StyleImpl style=(StyleImpl)next;
                writeStyle(style.getName(), style.getTitle().toString(), first, out);
            }
        }
        append(6, out, "</StyleList>"); //$NON-NLS-1$
        append( 4, out, "</Layer>" );                 //$NON-NLS-1$
    }
        
    private static boolean writeStyle(String name, String title, boolean first, Writer out) throws IOException{
        if (first) {
            append(8, out, "<Style current=\"1\">"); //$NON-NLS-1$
        } else {
            append(8, out, "<Style>"); //$NON-NLS-1$
        }
        append(10, out, "<Name>" + name + "</Name>"); //$NON-NLS-1$ //$NON-NLS-2$
        append(10, out, "<Title>" + title + "</Title>"); //$NON-NLS-1$ //$NON-NLS-2$
        append(8, out, "</Style>"); //$NON-NLS-1$
        return false;
    }

    /** TODO: Test me ! And move me to CRS utility class */
    private static String srs( CoordinateReferenceSystem crs ){
        if( crs != null && crs.getIdentifiers() != null )
            for( Identifier id : crs.getIdentifiers() ){
                String srs = id.toString();
                if( srs.startsWith( "EPSG" ) ) return srs;             //$NON-NLS-1$
            }
        return "EPSG:4326"; //$NON-NLS-1$
    }
    private static void writeGeneral( IMap map, BufferedWriter out ) throws IOException  {
        IViewportModel view = map.getViewportModel();
        int w = 640;
        int h = (int) ( ((double)w) * view.getAspectRatio() );
        Envelope bbox = view.getBounds();
        CoordinateReferenceSystem crs = view.getCRS();
        String user = System.getenv("user.name"); //$NON-NLS-1$
        
        append( 2, out, "<General>"); //$NON-NLS-1$
        append( 4, out,   "<Window height=\""+h+"\" width=\""+w+"\"/>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        append( 4, out,   "<ows:BoundingBox crs=\""+srs( view.getCRS() )+ "\">" ); //$NON-NLS-1$ //$NON-NLS-2$
        append( 6, out,      "<ows:LowerCorner>"+bbox.getMinX()+" "+bbox.getMinY()+"</ows:LowerCorner>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        append( 6, out,      "<ows:UpperCorner>"+bbox.getMaxX()+" "+bbox.getMaxY()+"</ows:UpperCorner>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        append( 4, out,   "</ows:BoundingBox>"); //$NON-NLS-1$
        append( 4, out,   "<Title>"+map.getName()+"</Title>"); //$NON-NLS-1$ //$NON-NLS-2$
        append( 4, out,   "<ows:ServiceProvider>");         //$NON-NLS-1$
        append( 6, out,      "<ows:ProviderName>OWS-3 GeoDSS Thread</ows:ProviderName>"); //$NON-NLS-1$
        append( 6, out,      "<ows:ServiceContact>"); //$NON-NLS-1$
        append( 8, out,      "<ows:IndividualName>"+user+"</ows:IndividualName>"); //$NON-NLS-1$ //$NON-NLS-2$
        append( 6, out,      "</ows:ServiceContact>"); //$NON-NLS-1$
        append( 4, out,   "</ows:ServiceProvider>"); //$NON-NLS-1$
        append( 2, out, "</General>"); //$NON-NLS-1$
    }

    private static void writeHeader( IMap map, BufferedWriter out ) throws IOException {
        Date now = new Date();
        String id = "geodss."+now.getYear()+now.getMonth()+now.getDay()+now.getHours()+now.getMinutes(); //$NON-NLS-1$
        
        append( 0, out, "<OWSContext id=\""+id+"\" version=\"0.0.13\" "); //$NON-NLS-1$ //$NON-NLS-2$
        append( 0, out, "    xmlns=\"http://www.opengis.net/oc\""); //$NON-NLS-1$
        append( 0, out, "    xmlns:ogc=\"http://www.opengis.net/ogc\""); //$NON-NLS-1$
        append( 0, out, "    xmlns:ows=\"http://www.opengis.net/ows\""); //$NON-NLS-1$
        append( 0, out, "    xmlns:param=\"http;//www.opengis.net/param\""); //$NON-NLS-1$
        append( 0, out, "    xmlns:sld=\"http://www.opengis.net/sld\""); //$NON-NLS-1$
        append( 0, out, "    xmlns:xlink=\"http://www.w3.org/1999/xlink\""); //$NON-NLS-1$
        append( 0, out, "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""); //$NON-NLS-1$
        append( 0, out, "    xsi:schemaLocation=\"http://www.opengis.net/oc oc_0_0_13.xsd\"" ); //$NON-NLS-1$
        append( 0, out, "    >"); //$NON-NLS-1$
    }

    /**
     * Note given the restriction we placed on the selection
     * we can expect the structured selection to provide us with
     * an IMap.
     */
    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        if( selection != null ){
            Object obj = selection.getFirstElement();
            if( obj != null && obj instanceof IMap){
                this.selection = (IMap) obj;
            }
        }
        // note we can latch onto the workbench here, for opening views
        // etc ...
        //
    }
    
    public void addPages() {
        super.addPages();
        
        if( ApplicationGIS.getActiveMap() == ApplicationGIS.NO_MAP){            
            return; // no pages no go!            
        }
        page = new MapExportPage(Messages.ContextExportWizard_page_name, Messages.ContextExportWizard_page_title, ContextPlugin.getImageDescriptor("icons/wizban/import_owscontext_wiz_gif" ));    //$NON-NLS-1$
        addPage( page );
    }
}
