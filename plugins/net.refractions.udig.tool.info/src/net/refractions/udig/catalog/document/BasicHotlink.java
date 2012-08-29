package net.refractions.udig.catalog.document;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.internal.document.HotlinkFileDocument;
import net.refractions.udig.catalog.internal.document.HotlinkWebDocument;

import org.opengis.feature.simple.SimpleFeature;

public class BasicHotlink implements IHotlinkSource {
    
    private IGeoResource resource;

    public BasicHotlink(IGeoResource resource) {
        this.resource = resource;
    }

    @Override
    public List<HotlinkDescriptor> getHotlinkDescriptors() {
        List<HotlinkDescriptor> list = BasicHotlinkResolveFactory.getHotlinkDescriptors( resource );
        return Collections.unmodifiableList(list);
    }
    @Override
    public List<IDocument> getDocuments(SimpleFeature feature) {
        List<IDocument> list = new ArrayList<IDocument>();
        for (HotlinkDescriptor descriptor : getHotlinkDescriptors()) {
            IDocument document = getDocument(feature, descriptor.getAttributeName());
            list.add(document);
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public IDocument getDocument(SimpleFeature feature, String attributeName) {
        for (HotlinkDescriptor descriptor : getHotlinkDescriptors()) {
            if (descriptor.getAttributeName().equals(attributeName)) {
                return createDocument(feature, descriptor);
            }
        }
        return null; // not available
    }

    private IDocument createDocument(SimpleFeature feature, HotlinkDescriptor descriptor) {
        final String value = (String) feature.getAttribute(descriptor.getAttributeName());
        if (value == null) {
            return null; // document not available
        }
        switch (descriptor.getType()) {
        case FILE:
            return new HotlinkFileDocument(value, Collections.singletonList(descriptor));
        case WEB:
            return new HotlinkWebDocument(value, Collections.singletonList(descriptor));
        }
        return null;
    }

    @Override
    public IDocument setFile(SimpleFeature feature, String attributeName, File file) {
        
        return null;
    }

    @Override
    public IDocument setLink(SimpleFeature feature, String attributeName, URL link) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IDocument clear(SimpleFeature feature, String attributeName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IDocument setAction(SimpleFeature feature, String attributeName, String action) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canAttach() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canLink() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canUpdate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canRemove() {
        // TODO Auto-generated method stub
        return false;
    }

}
