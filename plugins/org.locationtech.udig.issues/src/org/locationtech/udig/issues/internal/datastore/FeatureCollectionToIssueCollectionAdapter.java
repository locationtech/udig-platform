/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal.datastore;

import java.io.StringReader;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.core.enums.Priority;
import org.locationtech.udig.core.enums.Resolution;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.issues.IIssue;
import org.locationtech.udig.issues.IssuesListUtil;
import org.locationtech.udig.issues.internal.IssuesActivator;
import org.locationtech.udig.issues.internal.PlaceholderIssue;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Converts a features collection(issues that have been saved as features) to a collection of issues.
 *
 * Note a DefaultFeatureCollection is explicitly used in order to force the contents
 * into memory (allowing us to use an iterator through memory).
 *
 * @author Jesse
 * @since 1.1.0
 */
public class FeatureCollectionToIssueCollectionAdapter extends AbstractCollection<IIssue> implements Collection<IIssue> {

    private final FeatureCollection<SimpleFeatureType, SimpleFeature> features;
    private FeatureTypeAttributeMapper mapper;

    public FeatureCollectionToIssueCollectionAdapter( FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection, FeatureTypeAttributeMapper mapper ) {
        this.features=featureCollection;
        this.mapper=mapper;
    }

    @Override
    public Iterator<IIssue> iterator() {
        final FeatureIterator<SimpleFeature> iter = features.features();

        return new Iterator<IIssue>(){

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public IIssue next() {
                SimpleFeature feature = iter.next();
                String extensionPointID=(String) feature.getAttribute(mapper.getExtensionId());
                try{
                	IIssue issue = IssuesListUtil.createIssue(extensionPointID);

                	if( issue==null ){
                		return createPlaceHolder(feature, extensionPointID);
                	}

                    initIssue(feature,issue);

                    return issue;
                } catch (Throwable e) {
                    LoggingSupport.log(IssuesActivator.getDefault(), e);
                    return createPlaceHolder(feature, extensionPointID);
                }

            }

            @Override
            public void remove() {
                iter.close();
            }

            protected void initIssue( SimpleFeature feature, IIssue issue ) {
                String mementoData=(String) feature.getAttribute(mapper.getMemento());
                String viewData=(String) feature.getAttribute(mapper.getViewMemento());
                String groupId=(String) feature.getAttribute(mapper.getGroupId());
                String id=(String) feature.getAttribute(mapper.getId());

                String resolutionInt=(String) feature.getAttribute(mapper.getResolution());
                String priorityInt=(String) feature.getAttribute(mapper.getPriority());
                String description=(String) feature.getAttribute(mapper.getDescription());

                Resolution resolution=Resolution.valueOf(resolutionInt);
                if( resolution==null )
                    resolution=Resolution.UNRESOLVED;
                Priority priority=Priority.valueOf(priorityInt);
                if( priority==null )
                    priority=Priority.WARNING;


                issue.setDescription(description);
                issue.setResolution(resolution);
                issue.setPriority(priority);

                XMLMemento issueMemento=null;
                if (mementoData != null) {
                    try {
                        issueMemento = XMLMemento.createReadRoot(new StringReader(mementoData));
                    } catch (WorkbenchException e) {
                        issueMemento = null;
                    }
                }
                XMLMemento viewMemento=null;
                if (viewData != null){
                    try {
                        viewMemento = XMLMemento.createReadRoot(new StringReader(viewData));
                    } catch (WorkbenchException e) {
                        viewMemento = null;
                    }
                }

                ReferencedEnvelope env = new ReferencedEnvelope(feature.getBounds());

                issue.init(issueMemento, viewMemento, id, groupId, env);
            }

            protected IIssue createPlaceHolder( SimpleFeature feature, String extensionId ) {
                PlaceholderIssue issue=new PlaceholderIssue();
                issue.setExtensionID(extensionId);
                initIssue(feature, issue);
                return issue;
            }

        };
    }

	@Override
    public int size() {
        return features.size();
    }

}
