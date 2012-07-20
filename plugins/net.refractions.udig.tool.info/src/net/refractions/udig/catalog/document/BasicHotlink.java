package net.refractions.udig.catalog.document;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;

import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IHotlink;

public class BasicHotlink implements IHotlink {
    /**
     * {@link IGeoResource#getPersistentProperties()} key used to record 
     * hotlink descriptor list.
     */
    final static String HOTLINK = "hotlink";
    
    private IGeoResource resource;

    public BasicHotlink(IGeoResource resource) {
        this.resource = resource;
    }

    @Override
    public List<HotlinkDescriptor> getHotlinkAttributeList() {
        resource.getPersistentProperties().get(HOTLINK);
        return null;
    }

    @Override
    public List<IDocument> getHotlinks(SimpleFeature feature) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IDocument document(SimpleFeature feature, Name attributeName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IDocument file(SimpleFeature feature, Name attributeName, File file) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IDocument link(SimpleFeature feature, Name attributeName, URL link) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IDocument clear(SimpleFeature feature, Name attributeName) {
        // TODO Auto-generated method stub
        return null;
    }

}
