package net.refractions.udig.catalog.internal.wmt.wmtsource.ww;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.internal.wmt.WMTPlugin;
import net.refractions.udig.catalog.wmt.internal.Messages;
import net.refractions.udig.core.internal.CorePlugin;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Represents a &lt;LayerSet&gt;, which may contain &lt;ChildLayerSet&gt;
 * and/or &lt;QuadTileSet&gt;
 * see: http://worldwindxml.worldwindcentral.com/zoomit.xml?version=1.4.0.0
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class LayerSet {
    private String name;
    private String id;
    
    private List<QuadTileSet> quadTileSets;
    private List<LayerSet> childLayerSets;
    
    public static final char ID_SEPERATOR = '-';
    
    public LayerSet(Element xmlElement, String id) throws Exception {
        this.name = xmlElement.getAttributeValue("Name"); //$NON-NLS-1$
        this.id = constructId(id, name); 
        
        getChildLayerSets(xmlElement.getChildren("ChildLayerSet")); //$NON-NLS-1$
        getQuadTileSets(xmlElement.getChildren("QuadTileSet")); //$NON-NLS-1$
    }

    private void getQuadTileSets(List<?> children) throws Exception {
        quadTileSets = new ArrayList<QuadTileSet>(children.size());
        
        for(Object child : children) {
            if (child instanceof Element) {
                quadTileSets.add(new QuadTileSet((Element) child, id)); 
            }
        }
    }

    private void getChildLayerSets(List<?> children) throws Exception {
        childLayerSets = new ArrayList<LayerSet>(children.size());
        
        for(Object child : children) {
            if (child instanceof Element) {
                childLayerSets.add(new LayerSet((Element) child, id)); 
            }
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getId() {
        return id;
    }
    
    public List<QuadTileSet> getQuadTileSets() {
        return quadTileSets;
    }
    
    public List<LayerSet> getChildLayerSets() {
        return childLayerSets;
    }
    
    @SuppressWarnings("nls")
    private static final String[] ILLEGAL_CHARACTERS = 
        { "/", "\n", "\r", "\t", "\0", "\f", "`", "?", "*", "\\", "<", ">", "|", "\"", ":" };
    public static String constructId(String prefix, String name) {
        String id = prefix + ID_SEPERATOR + name.replace("#", "%20"); //$NON-NLS-1$ //$NON-NLS-2$
        
        for (int i = 0; i < ILLEGAL_CHARACTERS.length; i++) {
            if (id.indexOf(ILLEGAL_CHARACTERS[i]) >= 0) {
                id = id.replace(ILLEGAL_CHARACTERS[i], "%20"); //$NON-NLS-1$ 
            }
        }
        
        return id.replace(" ", "%20"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * Constructs a LayerSet instance from a given file.
     * If the LayerSet of this file contains just a redirect,
     * we are following the redirect. 
     *
     * @param url
     * @return LayerSet instance representing a file
     * @throws Exception
     */
    public static LayerSet getFromUrl(URL url) throws Exception {
        try{
            Element layerSet = getRootElementFromUrl(url);
            
            if (layerSet == null) {
                throw new Exception(Messages.WWService_NoValidFile);
            } else {
                // check if this is just a redirect
                String redirect = layerSet.getAttributeValue("redirect"); //$NON-NLS-1$
                if (redirect == null) {
                    
                    return new LayerSet(layerSet, ""); //$NON-NLS-1$
                } else {
                    // Replace Version-Field
                    if (redirect.indexOf("${WORLDWINDVERSION}") >= 0) { //$NON-NLS-1$
                        redirect = redirect.replace("${WORLDWINDVERSION}", "1.4.0.0"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    
                    URL redirectUrl = new URL(null, redirect, CorePlugin.RELAXED_HANDLER);
                    layerSet = getRootElementFromUrl(redirectUrl);
                    
                    return new LayerSet(layerSet, ""); //$NON-NLS-1$
                }
            }
            
        } catch(Exception exc) {
            WMTPlugin.log("[LayerSet.getFromUrl] Loading WorldWind Config-File failed: " + url, exc); //$NON-NLS-1$
            throw exc;
        }       
    }
    
    private static Element getRootElementFromUrl(URL url) throws Exception {
        SAXBuilder builder = new SAXBuilder(false); 
        URLConnection connection = url.openConnection();            
        Document dom = builder.build(connection.getInputStream());
                           
        return dom.getRootElement(); 
    }
}
