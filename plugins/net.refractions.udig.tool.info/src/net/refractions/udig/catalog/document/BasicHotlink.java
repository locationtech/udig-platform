package net.refractions.udig.catalog.document;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;

import org.eclipse.swt.program.Program;
import org.opengis.feature.simple.SimpleFeature;

public class BasicHotlink implements IHotlinkSource {
    class FileLink implements IDocument {
        private File file;
        private String attributeName;
        public FileLink(String attributeName, Object value) {
            String path = (String) value;
            File resourceFile = resource.getID().toFile();
            if (resourceFile != null) {
                // store a relative file path
                file = new File(resourceFile.getParent(), path);
            } else {
                file = new File(path);
            }
            this.attributeName = attributeName;
        }

        @Override
        public String getName() {
            return file != null ? file.getName() : "(empty)";
        }

        @Override
        public String getDescription() {
            return attributeName+" file:"+file;
        }

        @Override
        public String getLabel() {
            return file.getName();
        }

        @Override
        public String getAttributeName() {
            return attributeName;
        }

        @Override
        public Type getType() {
            return Type.FILE;
        }

        @Override
        public IAbstractDocumentSource getSource() {
            return null;
        }

        @Override
        public boolean open() {
            boolean success = Program.launch( file.toString() );
            return success;
        }

        @Override
        public boolean isEmpty() {
           return file == null;
        }
        @Override
        public String toString() {
            return "Basic Hotlink "+attributeName+" file:"+file;
        }
    }

    class WebLink implements IDocument {
        private URL url;
        private String attributeName;
        public WebLink(String attributeName, Object value) {
            String path = (String) value;
            try {
                url = new URL( path );
            } catch (MalformedURLException e) {
                url = null;
            }
            this.attributeName = attributeName;
        }
    
        @Override
        public String getName() {
            String external = url.toExternalForm();
            int split = external.lastIndexOf("/");
            if( split != -1 ){
                String name = external.substring(split+1);
                return name;
            }
            return external;
        }
    
        @Override
        public String getDescription() {
            return attributeName+" file:"+url;
        }

        @Override
        public String getLabel() {
            return url == null ? "(empty)" : url.getFile();
        }
    
        @Override
        public String getAttributeName() {
            return attributeName;
        }
    
        @Override
        public Type getType() {
            return Type.WEB;
        }
    
        @Override
        public IAbstractDocumentSource getSource() {
            return null;
        }
    
        @Override
        public boolean open() {
            boolean success = Program.launch( url.toExternalForm() );
            return success;
        }
    
        @Override
        public boolean isEmpty() {
           return url == null;
        }
        @Override
        public String toString() {
            return "Basic Hotlink "+attributeName+" link:"+url;
        }
    }

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
        Object value = feature.getAttribute(descriptor.getAttributeName());
        if (value == null) {
            return null; // document not available
        }
        switch (descriptor.getType()) {
        case FILE:
            return new FileLink(descriptor.getAttributeName(), value);
        case WEB:
            return new WebLink(descriptor.getAttributeName(), value);
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

}
