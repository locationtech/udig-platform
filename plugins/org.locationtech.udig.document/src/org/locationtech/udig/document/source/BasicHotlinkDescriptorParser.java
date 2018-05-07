/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import org.locationtech.udig.document.DocumentPlugin;

/**
 * Maintains hotlink descriptors and their properties stored in
 * {@link IGeoResource#getPersistentProperties()}.
 * 
 * @author Naz Chan
 */
public class BasicHotlinkDescriptorParser {

    /**
     * {@link IGeoResource#getPersistentProperties()} key used to record hotlink descriptor list as
     * string. See {@link HotlinkDescriptor} for encoding.
     */
    private final static String HOTLINK = "hotlink"; //$NON-NLS-1$
    /**
     * {@link IGeoResource#getPersistentProperties()} key used to record hotlink enabled status.
     */
    private final static String HOTLINK_ENABLED = "hotlink_enabled"; //$NON-NLS-1$
    /**
     * Delimiter used to separate hotlink descriptors.
     */
    private final static String HOTLINK_DELIMITER = "|=|"; //$NON-NLS-1$
    /**
     * Regex equivalent of the hotlink delimiter.
     */
    private final static String HOTLINK_DELIMITER_REGEX = "\\|=\\|"; //$NON-NLS-1$
    
    private IGeoResource resource;

    public BasicHotlinkDescriptorParser(IGeoResource resource) {
        this.resource = resource;
    }
    
    /**
     * Checks if hotlinks are enabled or disabled.
     * 
     * @return true if enabled, otherwise false
     */
    public boolean isEnabled() {
        return getProperty(HOTLINK_ENABLED);
    }
    
    /**
     * Enables or disables hotlinks.
     * 
     * @param isEnabled
     */
    public void setEnabled(boolean isEnabled) {
        setProperty(HOTLINK_ENABLED, isEnabled);
    }
    
    private boolean getProperty(String key) {
        final Map<String, Serializable> props = resource.getPersistentProperties();
        if (props.containsKey(key)) {
            return Boolean.parseBoolean((String) props.get(key));
        }
        return false;
    }
    
    private void setProperty(String key, boolean value) {
        final Map<String, Serializable> props = resource.getPersistentProperties();
        props.put(key, Boolean.toString(value));
    }
    
    /**
     * Checks if the resource has hotlink descriptors.
     * 
     * @param resource
     * @return true if hotlinks descriptors are set, otherwise false
     */
    public boolean hasDescriptors() {
        return resource.getPersistentProperties().containsKey(HOTLINK);
    }

    /**
     * Gets the hotlink descriptors of the resource.
     * 
     * @return hotlink descriptors
     */
    public List<HotlinkDescriptor> getDescriptors() {
        final Map<String, Serializable> props = resource.getPersistentProperties();
        final String definitions = (String) props.get(HOTLINK);
        final List<HotlinkDescriptor> descriptors = new ArrayList<HotlinkDescriptor>();
        if (definitions != null && !definitions.isEmpty()) {
            final String definitionArray[] = definitions.split(HOTLINK_DELIMITER_REGEX);
            for (String definition : definitionArray) {
                try {
                    HotlinkDescriptor descriptor = new HotlinkDescriptor(definition);
                    descriptors.add(descriptor);
                } catch (Throwable t) {
                    DocumentPlugin.log("Unable describe hotlink:" + definition, t); //$NON-NLS-1$
                }
            }
        }
        return descriptors;
    }

    /**
     * Sets the hotlink descriptors of the resource.
     */
    public void setDescriptors(List<HotlinkDescriptor> descriptors) {
        final Map<String, Serializable> props = resource.getPersistentProperties();
        if (descriptors == null || descriptors.size() == 0) {
            props.remove(HOTLINK);
        } else {
            final StringBuilder sb = new StringBuilder();
            for (Iterator<HotlinkDescriptor> i = descriptors.iterator(); i.hasNext();) {
                sb.append(i.next().toString());
                if (i.hasNext()) {
                    sb.append(HOTLINK_DELIMITER);
                }
            }
            props.put(HOTLINK, sb.toString());
        }
    }

    /**
     * Clears the hotlink descriptors of the resource.
     */
    public void clearDescriptors() {
        setDescriptors(null);
    }

}
