package net.refractions.udig.document.source;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IHotlinkSource;
import net.refractions.udig.document.model.FileHotlinkDocument;
import net.refractions.udig.document.model.WebHotlinkDocument;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

public class BasicHotlink implements IHotlinkSource {
    
    private IGeoResource resource;

    public BasicHotlink(IGeoResource resource) {
        this.resource = resource;
    }

    @Override
    public List<HotlinkDescriptor> getHotlinkDescriptors(SimpleFeature feature, IProgressMonitor monitor) {
        List<HotlinkDescriptor> list = BasicHotlinkResolveFactory.getHotlinkDescriptors( resource );
        return Collections.unmodifiableList(list);
    }
    @Override
    public List<IDocument> getDocuments(SimpleFeature feature, IProgressMonitor monitor) {
        List<IDocument> list = new ArrayList<IDocument>();
        for (HotlinkDescriptor descriptor : getHotlinkDescriptors(feature, monitor)) {
            IDocument document = getDocument(feature, descriptor.getAttributeName(), monitor);
            list.add(document);
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public IDocument getDocument(SimpleFeature feature, String attributeName, IProgressMonitor monitor) {
        for (HotlinkDescriptor descriptor : getHotlinkDescriptors(feature, monitor)) {
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
            return new FileHotlinkDocument(value, Collections.singletonList(descriptor));
        case WEB:
            return new WebHotlinkDocument(value, Collections.singletonList(descriptor));
        }
        return null;
    }

    @Override
    public boolean canSetHotlink() {
        return false;
    }

    @Override
    public boolean setFile(SimpleFeature feature, String attributeName, File file,
            IProgressMonitor monitor) {
        return false;
    }

    @Override
    public boolean setLink(SimpleFeature feature, String attributeName, URL link,
            IProgressMonitor monitor) {
        return false;
    }

    @Override
    public boolean setAction(SimpleFeature feature, String attributeName, String action,
            IProgressMonitor monitor) {
        return false;
    }

    @Override
    public boolean canClearHotlink() {
        return false;
    }

    @Override
    public boolean clear(SimpleFeature feature, String attributeName, IProgressMonitor monitor) {
        return false;
    }

}
